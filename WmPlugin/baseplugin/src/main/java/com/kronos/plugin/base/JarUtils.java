package com.kronos.plugin.base;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

class JarUtils {

    public static File modifyJarFile(File jarFile, File tempDir,
                                     BaseTransform transform) throws IOException {
        /** 设置输出到的jar */
        String hexName = DigestUtils.md5Hex(jarFile.getAbsolutePath()).substring(0, 8);
        File optJar = new File(tempDir, hexName + jarFile.getName());
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar));
        /**
         * 读取原jar
         */
        JarFile file = new JarFile(jarFile);
        Enumeration<JarEntry> enumeration = file.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            InputStream inputStream = file.getInputStream(jarEntry);

            String entryName = jarEntry.getName();

            ZipEntry zipEntry = new ZipEntry(entryName);

            jarOutputStream.putNextEntry(zipEntry);
            byte[] modifiedClassBytes = null;
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
            if (entryName.endsWith(".class")) {
                try {
                    modifiedClassBytes = transform.process(entryName, sourceClassBytes);
                } catch (Exception ignored) {

                }
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes);
            } else {
                jarOutputStream.write(modifiedClassBytes);
            }
            jarOutputStream.closeEntry();
        }
        jarOutputStream.close();
        file.close();
        return optJar;
    }


    static HashSet<String> scanJarFile(File jarFile) throws IOException {
        HashSet<String> hashSet = new HashSet<>();
        JarFile file = new JarFile(jarFile);
        Enumeration<JarEntry> enumeration = file.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.endsWith(".class")) {
                hashSet.add(entryName);
            }
        }
        file.close();
        return hashSet;
    }

    static void deleteJarScan(File jarFile, List<String> removeClasses, DeleteCallBack callBack) throws IOException {
        /**
         * 读取原jar
         */
        JarFile file = new JarFile(jarFile);
        Enumeration<JarEntry> enumeration = file.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.endsWith(".class") && removeClasses.contains(entryName)) {
                InputStream inputStream = file.getInputStream(jarEntry);
                byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
                try {
                    if (callBack != null) {
                        callBack.delete(entryName, sourceClassBytes);
                    }
                } catch (Exception ignored) {

                }
            }

        }
        file.close();
    }


    static void deleteJarScan(File jarFile, DeleteCallBack callBack) throws IOException {
        /**
         * 读取原jar
         */
        JarFile file = new JarFile(jarFile);
        Enumeration<JarEntry> enumeration = file.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            InputStream inputStream = file.getInputStream(jarEntry);
            String entryName = jarEntry.getName();
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
            if (entryName.endsWith(".class")) {
                try {
                    if (callBack != null) {
                        callBack.delete(entryName, sourceClassBytes);
                    }
                } catch (Exception ignored) {

                }
            }

        }
        file.close();
    }
}
