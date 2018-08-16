package com.sankuai.waimai.router.plugin;

import static com.sankuai.waimai.router.interfaces.Const.INIT_METHOD;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import com.android.SdkConstants;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.builder.model.AndroidProject;
import com.android.utils.FileUtils;

import com.sankuai.waimai.router.interfaces.Const;
import com.sankuai.waimai.router.service.ServiceImpl;

import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskOutputs;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by jzj on 2018/4/25.
 */

public class WMRouterServiceProcessor {

    private static final String WRITE_ASSETS = "WriteAssets: ";
    private static final String FIND_SERVICE = "FindService: ";
    private static final String SERVICE_META = "FindService:     ";
    private static final String GENERATE_INIT = "GenerateInit: ";

    /**
     * interfaceName --> ( key --> impl )
     */
    private Map<String, Map<String, ServiceImpl>> mMap = new HashMap<>();

    /**
     * interfaceName
     */
    private Set<String> mInterfaceNames = new HashSet<>();

    /**
     * implementationName
     */
    private Set<String> mImplementationNames = new HashSet<>();

    public Set<String> getInterfaceNames() {
        return mInterfaceNames;
    }

    public Set<String> getImplementationNames() {
        return mImplementationNames;
    }

    public void generateServiceInitClass(String directory) {

        if (mMap.isEmpty()) {
            WMRouterLogger.info(GENERATE_INIT + "skipped, no service found");
            return;
        }

        try {
            WMRouterLogger.info(GENERATE_INIT + "start...");
            long ms = System.currentTimeMillis();

            ClassWriter writer = new ClassWriter(
                    ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new ClassVisitor(ASM5, writer) {
            };
            String className = Const.SERVICE_LOADER_INIT.replace('.', '/');
            cv.visit(50, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null);

            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    INIT_METHOD, "()V", null, null);

            mv.visitCode();
            for (Map.Entry<String, Map<String, ServiceImpl>> entry : mMap.entrySet()) {
                String interfaceName = entry.getKey();
                Map<String, ServiceImpl> map = entry.getValue();
                for (Map.Entry<String, ServiceImpl> implEntry : map.entrySet()) {
                    String key = implEntry.getKey();
                    ServiceImpl impl = implEntry.getValue();
                    mv.visitLdcInsn(interfaceName);
                    mv.visitLdcInsn(key);
                    mv.visitLdcInsn(impl.getImplementation());
                    mv.visitInsn(impl.isSingleton() ? ICONST_1 : ICONST_0);
                    mv.visitMethodInsn(INVOKESTATIC, Const.SERVICE_LOADER_CLASS.replace('.', '/'),
                            "put",
                            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
                            false);
                }
            }
            mv.visitMaxs(4, 0);
            mv.visitInsn(RETURN);
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

    public void findServices(Project project, TaskOutputs outputs, ApplicationVariant variant) {
        if (!outputs.getHasOutput()) {
            WMRouterLogger.warn(FIND_SERVICE + "no output");
            return;
        }

        WMRouterLogger.info(FIND_SERVICE + "start...");
        long ms = System.currentTimeMillis();
        FileCollection files = outputs.getFiles();
        for (File file : files) {
            String path = file.getPath();
            FileTree tree = project.fileTree(new HashMap<String, Object>() {{
                put("dir", file);
            }});

            WMRouterLogger.debug(SERVICE_META + "process file tree = %s", file);

            // 处理services目录
            processDirectories(path, tree);

            // 处理jar包
            processJarFiles(tree);
        }

        // 处理Service文件
        String servicesFolderName = getClassesDir(project, variant, Const.SERVICE_PATH);
        FileTree servicesTree = project.fileTree(new HashMap<String, Object>() {{
            put("dir", servicesFolderName);
        }});

        WMRouterLogger.debug(SERVICE_META + "process classes dir = %s", servicesTree);

        servicesTree.visit(new FileVisitor() {
            @Override
            public void visitDir(FileVisitDetails fileVisitDetails) {

            }

            @Override
            public void visitFile(FileVisitDetails fileVisitDetails) {
                processFile(fileVisitDetails.getFile());
            }
        });

        WMRouterLogger.info(FIND_SERVICE + "cost %s ms", System.currentTimeMillis() - ms);
    }

    public Map<String, Map<String, ServiceImpl>> getServiceMap() {
        return mMap;
    }

    public void writeToAssets(Project project, ApplicationVariant variant) {
        if (mMap.isEmpty()) {
            WMRouterLogger.info(WRITE_ASSETS + "skipped, no service found");
            return;
        }

        WMRouterLogger.info(WRITE_ASSETS + "start...");
        long ms = System.currentTimeMillis();
        File dir = new File(getAssetsDir(project, variant, Const.ASSETS_PATH));
        if (dir.isFile()) {
            dir.delete();
        }
        dir.mkdirs();
        for (Map.Entry<String, Map<String, ServiceImpl>> entry : mMap.entrySet()) {
            String interfaceName = entry.getKey();
            Map<String, ServiceImpl> map = entry.getValue();
            if (interfaceName != null && map != null && !map.isEmpty()) {
                try (PrintWriter writer = new PrintWriter(new File(dir, interfaceName))) {
                    for (ServiceImpl impl : map.values()) {
                        writer.println(impl.toConfig());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        WMRouterLogger.info(WRITE_ASSETS + "cost %s ms", System.currentTimeMillis() - ms);
    }

    private static String getClassesDir(Project project, ApplicationVariant variant, String path) {
        return FileUtils.join(project.getBuildDir().getPath(),
                AndroidProject.FD_INTERMEDIATES,
                "classes",
                variant.getDirName(),
                path);
    }

    private static String getAssetsDir(Project project, ApplicationVariant variant, String path) {
        return FileUtils.join(project.getBuildDir().getPath(),
                AndroidProject.FD_INTERMEDIATES,
                "assets",
                variant.getDirName(),
                path);
    }

    private void processDirectories(String path, FileTree tree) {
        FileCollection filter = tree.filter(new Spec<File>() {
            @Override
            public boolean isSatisfiedBy(File file) {
                return file.getPath().substring(path.length()).contains(Const.SERVICE_PATH);
            }
        });
        for (File f : filter) {
            processFile(f);
        }
    }

    private void processJarFiles(FileTree tree) {
        FileCollection filter = tree.filter(new Spec<File>() {
            @Override
            public boolean isSatisfiedBy(File file) {
                return file.getName().endsWith(SdkConstants.DOT_JAR);
            }
        });
        for (File jarFile : filter) {
            processJarFile(jarFile);
        }
    }

    private void processJarFile(File jarFile) {
        WMRouterLogger.debug(SERVICE_META + "process jar file = %s", jarFile);
        try (ZipFile zipFile = new ZipFile(jarFile)) {
            if (hasServiceEntry(zipFile)) {
                WMRouterLogger.debug(SERVICE_META + "    found service entry, modify jar file");
                File tempFile = new File(jarFile.getPath() + ".tmp");
                try (ZipInputStream is = new ZipInputStream(new FileInputStream(jarFile));
                     ZipOutputStream os = new ZipOutputStream(new FileOutputStream(tempFile))) {
                    ZipEntry entry;
                    while ((entry = is.getNextEntry()) != null) {
                        if (isServiceEntry(entry)) {
                            try {
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(is));
                                String interfaceName = getSuffix(entry.getName(), "/");
                                processFileContent(reader, interfaceName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            os.putNextEntry(new ZipEntry(entry));
                            IOUtils.copy(is, os);
                        }
                    }
                    jarFile.delete();
                    tempFile.renameTo(jarFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSuffix(String s, String splitter) {
        int i = s.lastIndexOf(splitter);
        if (i < 0) {
            return s;
        } else {
            return s.substring(i + splitter.length());
        }
    }

    private boolean hasServiceEntry(ZipFile zipFile) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (isServiceEntry(entry)) {
                return true;
            }
        }
        return false;
    }

    private boolean isServiceEntry(ZipEntry entry) {
        return !entry.isDirectory() && entry.getName().startsWith(Const.SERVICE_PATH);
    }

    private void processFile(File f) {
        WMRouterLogger.debug(SERVICE_META + "file = %s", f);
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            processFileContent(reader, f.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        f.delete();
    }

    private void processFileContent(BufferedReader reader, String interfaceName)
            throws IOException {
        WMRouterLogger.debug(SERVICE_META + "process file content, interface = %s", interfaceName);
        String line;
        mInterfaceNames.add(interfaceName);
        while ((line = reader.readLine()) != null) {
            ServiceImpl impl = ServiceImpl.fromConfig(line);
            if (impl != null) {

                WMRouterLogger.debug(SERVICE_META + "process line success '%s'", line);

                Map<String, ServiceImpl> map = mMap.computeIfAbsent(interfaceName,
                        k -> new HashMap<>());

                ServiceImpl prev = map.put(impl.getKey(), impl);
                String errorMsg = ServiceImpl.checkConflict(interfaceName, prev, impl);
                if (errorMsg != null) {
                    WMRouterLogger.fatal(errorMsg);
                }

                mImplementationNames.add(impl.getImplementation());
            } else {
                WMRouterLogger.debug(SERVICE_META + "process line error '%s'", line);
            }
        }
    }
}
