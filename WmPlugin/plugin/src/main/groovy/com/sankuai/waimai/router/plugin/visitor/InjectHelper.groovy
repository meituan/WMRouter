package com.sankuai.waimai.router.plugin.visitor


import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class InjectHelper {
    def jarFile
    def classItems

    InjectHelper(File jarFile, HashSet<String> classItems) {
        this.jarFile = jarFile
        this.classItems = classItems
    }

    void setClassItems(classItems) {
        this.classItems = classItems
    }

    byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        Log.info("item:" + classItems)
        ClassVisitor methodFilterCV = new ClassFilterVisitor(classWriter, classItems)
        ClassReader cr = new ClassReader(srcClass)
        cr.accept(methodFilterCV, 0)
        return classWriter.toByteArray()
    }


    File modifyJarFile(File tempDir) {
        /** 设置输出到的jar */
        def hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        def optJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
        /**
         * 读取原jar
         */
        def file = new JarFile(jarFile)
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()
            String className

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (entryName.endsWith(".class")) {
                className = AutoRegisterTransform.path2Classname(entryName)
                if (checkRouterInitClassName(className)) {
                    try {
                        Log.info("modifyClass $entryName")
                        modifiedClassBytes = modifyClass(sourceClassBytes)
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes)
            } else {
                jarOutputStream.write(modifiedClassBytes)
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
        return optJar
    }

    static boolean checkRouterInitClassName(String className) {
        String dexClassName = "com.kronos.router.RouterLoader"
        return dexClassName == className
    }
}
