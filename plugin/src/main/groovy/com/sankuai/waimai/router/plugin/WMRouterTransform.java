package com.sankuai.waimai.router.plugin;

import com.android.SdkConstants;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.collect.ImmutableSet;
import com.sankuai.waimai.router.interfaces.Const;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
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
        return false;
    }

    @Override
    public void transform(TransformInvocation invocation) {
        WMRouterLogger.info(TRANSFORM + "start...");
        long ms = System.currentTimeMillis();

        Set<String> initClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

        for (TransformInput input : invocation.getInputs()) {
            input.getJarInputs().parallelStream().forEach(jarInput -> {
                File src = jarInput.getFile();
                File dst = invocation.getOutputProvider().getContentLocation(
                        jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(),
                        Format.JAR);
                try {
                    scanJarFile(src, initClasses);
                    FileUtils.copyFile(src, dst);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            input.getDirectoryInputs().parallelStream().forEach(directoryInput -> {
                File src = directoryInput.getFile();
                File dst = invocation.getOutputProvider().getContentLocation(
                        directoryInput.getName(), directoryInput.getContentTypes(),
                        directoryInput.getScopes(), Format.DIRECTORY);
                try {
                    scanDir(src, initClasses);
                    FileUtils.copyDirectory(src, dst);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        File dest = invocation.getOutputProvider().getContentLocation(
                "WMRouter", TransformManager.CONTENT_CLASS,
                ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY);
        generateServiceInitClass(dest.getAbsolutePath(), initClasses);

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
    private void generateServiceInitClass(String directory, Set<String> classes) {

        if (classes.isEmpty()) {
            WMRouterLogger.info(GENERATE_INIT + "skipped, no service found");
            return;
        }

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
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, clazz.replace('.', '/'),
                        "init",
                        "()V",
                        false);
            }
            mv.visitMaxs(0, 0);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitEnd();
            cv.visitEnd();

            File dest = new File(directory, className + SdkConstants.DOT_CLASS);
            dest.getParentFile().mkdirs();
            new FileOutputStream(dest).write(writer.toByteArray());

            WMRouterLogger.info(GENERATE_INIT + "cost %s ms", System.currentTimeMillis() - ms);

        } catch (IOException e) {
            WMRouterLogger.fatal(e);
        }
    }
}
