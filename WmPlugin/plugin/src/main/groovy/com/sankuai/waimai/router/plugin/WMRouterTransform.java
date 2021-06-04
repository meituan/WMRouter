package com.sankuai.waimai.router.plugin;

import com.android.SdkConstants;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.collect.ImmutableSet;
import com.kronos.plugin.base.BaseTransform;
import com.kronos.plugin.base.ClassUtils;
import com.kronos.plugin.base.DeleteCallBack;
import com.kronos.plugin.base.TransformCallBack;
import com.sankuai.waimai.router.interfaces.Const;
import com.sankuai.waimai.router.plugin.visitor.ClassFilterVisitor;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class WMRouterTransform extends Transform {

    private static final String TRANSFORM = "Transform: ";
    private static final String GENERATE_INIT = "GenerateInit: ";

    /**
     * Linux/Unix： com/sankuai/waimai/router/generated/service
     * Windows：    com\sankuai\waimai\router\generated\service
     */
    public static final String INIT_SERVICE_DIR = Const.GEN_PKG_SERVICE.replace('.', File.separatorChar);
    /**
     * com/sankuai/waimai/router/generated/service
     */
    public static final String INIT_SERVICE_PATH = Const.GEN_PKG_SERVICE.replace('.', '/');

    @Override
    public String getName() {
        return Const.NAME;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation invocation) {
        WMRouterLogger.info(TRANSFORM + "start...");
        long ms = System.currentTimeMillis();
        //非增量编译，先清空输出目录
        if(!invocation.isIncremental()){
            try {
                invocation.getOutputProvider().deleteAll();
            } catch (IOException e) {
                WMRouterLogger.fatal(e);
            }
        }
        Set<String> initClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Set<String> deleteClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());
        BaseTransform baseTransform = new BaseTransform(invocation, new TransformCallBack() {
            @Override
            public byte[] process(String className, byte[] bytes, BaseTransform baseTransform) {
                String checkClassName = ClassUtils.path2Classname(className);
                if (checkClassName.startsWith(Const.GEN_PKG_SERVICE)) {
                    initClasses.add(className);
                }
                return null;
            }
        });
        baseTransform.setDeleteCallBack(new DeleteCallBack() {
            @Override
            public void delete(String className, byte[] bytes) {
                String checkClassName = ClassUtils.path2Classname(className);
                if (checkClassName.startsWith(Const.GEN_PKG_SERVICE)) {
                    deleteClasses.add(className);
                }
            }
        });
        baseTransform.openSimpleScan();
        baseTransform.startTransform();
        File dest = invocation.getOutputProvider().getContentLocation(
                "WMRouter", TransformManager.CONTENT_CLASS,
                ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY);
        generateServiceInitClass(dest.getAbsolutePath(), initClasses, deleteClasses);
        WMRouterLogger.info(TRANSFORM + "cost %s ms", System.currentTimeMillis() - ms);
    }

    /**
     * 扫描由注解生成器生成到包 {@link Const#GEN_PKG_SERVICE} 里的初始化类
     */
    private void scanJarFile(File file, Set<String> initClasses) throws IOException {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(SdkConstants.DOT_CLASS) && name.startsWith(INIT_SERVICE_PATH)) {
                String className = trimName(name, 0).replace('/', '.');
                initClasses.add(className);
                WMRouterLogger.info("    find ServiceInitClass: %s", className);
            }
        }
    }

    /**
     * 扫描由注解生成器生成到包 {@link Const#GEN_PKG_SERVICE} 里的初始化类
     */
    private void scanDir(File dir, Set<String> initClasses) throws IOException {
        File packageDir = new File(dir, INIT_SERVICE_DIR);
        if (packageDir.exists() && packageDir.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(packageDir,
                    new SuffixFileFilter(SdkConstants.DOT_CLASS, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE);
            for (File f : files) {
                String className = trimName(f.getAbsolutePath(), dir.getAbsolutePath().length() + 1)
                        .replace(File.separatorChar, '.');
                initClasses.add(className);
                WMRouterLogger.info("    find ServiceInitClass: %s", className);
            }
        }
    }

    /**
     * [prefix]com/xxx/aaa.class --> com/xxx/aaa
     * [prefix]com\xxx\aaa.class --> com\xxx\aaa
     */
    private String trimName(String s, int start) {
        return s.substring(start, s.length() - SdkConstants.DOT_CLASS.length());
    }

    /**
     * 生成格式如下的代码，其中ServiceInit_xxx由注解生成器生成。
     * <pre>
     * package com.sankuai.waimai.router.generated;
     *
     * public class ServiceLoaderInit {
     *
     *     public static void init() {
     *         ServiceInit_xxx1.init();
     *         ServiceInit_xxx2.init();
     *     }
     * }
     * </pre>
     */
    private void generateServiceInitClass(String directory, Set<String> classes, Set<String> deleteClass) {

        if (classes.isEmpty()) {
            WMRouterLogger.info(GENERATE_INIT + "skipped, no service found");
            return;
        }
        File dest = new File(directory, INIT_SERVICE_PATH + SdkConstants.DOT_CLASS);
        if (!dest.exists()) {
            try {
                WMRouterLogger.info(GENERATE_INIT + "start...");
                long ms = System.currentTimeMillis();

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, writer) {
                };
                String className = Const.SERVICE_LOADER_INIT.replace('.', '/');
                cv.visit(50, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null);

                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                        Const.INIT_METHOD, "()V", null, null);

                mv.visitCode();

                for (String clazz : classes) {
                    String input = clazz.replace(".class", "");
                    input = input.replace(".", "/");
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, input,
                            "init",
                            "()V",
                            false);
                }
                mv.visitMaxs(0, 0);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitEnd();
                cv.visitEnd();

                dest.getParentFile().mkdirs();
                new FileOutputStream(dest).write(writer.toByteArray());

                WMRouterLogger.info(GENERATE_INIT + "cost %s ms", System.currentTimeMillis() - ms);

            } catch (IOException e) {
                WMRouterLogger.fatal(e);
            }
        } else {
            try {
                modifyClass(dest, classes, deleteClass);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void modifyClass(File file, Set<String> items, Set<String> deleteItems) throws IOException {
        try {
            InputStream inputStream = new FileInputStream(file);
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
            byte[] modifiedClassBytes = modifyClass(sourceClassBytes, items, deleteItems);
            if (modifiedClassBytes != null) {
                ClassUtils.saveFile(file, modifiedClassBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    byte[] modifyClass(byte[] srcClass, Set<String> items, Set<String> deleteItems) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor methodFilterCV = new ClassFilterVisitor(classWriter, items, deleteItems);
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG);
        return classWriter.toByteArray();
    }

}
