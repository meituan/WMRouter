package com.kronos.plugin.base;

import java.io.File;
import java.io.FileOutputStream;

public class ClassUtils {

    public static String path2Classname(String entryName) {
        return entryName.replace(File.separator, ".").replace(".class", "");
    }

    public static boolean checkClassName(String className) {
        return (!className.contains("R\\$") && !className.endsWith("R")
                && !className.endsWith("BuildConfig"));
    }

    public static File saveFile(File mTempDir, byte[] modifiedClassBytes) {
        File modified = null;
        try {
            if (modifiedClassBytes != null) {
                modified = mTempDir;
                if (modified.exists()) {
                    modified.delete();
                }
                modified.createNewFile();
                FileOutputStream stream = new FileOutputStream(modified);
                stream.write(modifiedClassBytes);
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modified;
    }


}