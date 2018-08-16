package com.sankuai.waimai.router.utils;

import android.support.annotation.NonNull;
import android.util.LruCache;

import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.service.ServiceImpl;

/**
 * Class缓存
 *
 * Created by jzj on 2018/3/29.
 */
public class ClassPool {

    private static final LruCache<String, Class> CACHE = new LruCache<>(100);
    private static final Class NOT_FOUND = NotFound.class;

    private static class NotFound {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Class> T get(ServiceImpl impl) {
        if (impl == null) {
            return null;
        }
        Class clazz = impl.getImplementationClazz();
        if (clazz != null) {
            return (T) clazz;
        }
        return get(impl.getImplementation());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Class> T get(String className) {
        if (className == null) {
            return null;
        }
        Class t = getClass(className);
        if (t == NOT_FOUND) {
            Debugger.e("[ClassPool] get class failed: %s", className);
            return null;
        } else {
            Debugger.i("[ClassPool] get class success: %s", className);
            return (T) t;
        }
    }

    @NonNull
    private static Class getClass(@NonNull String className) {
        Class t = CACHE.get(className);
        if (t != null) {
            return t;
        } else {
            synchronized (CACHE) {
                t = CACHE.get(className);
                if (t == null) {
                    t = findClass(className);
                    CACHE.put(className, t);
                }
            }
            return t;
        }
    }

    @NonNull
    private static Class findClass(@NonNull String className) {
        try {
            Debugger.i("[ClassPool] >>> get class with reflection: %s", className);
            Class<?> clazz = Class.forName(className);
            if (clazz != null) {
                return clazz;
            } else {
                Debugger.fatal("[ClassPool] 没找到Class '%s'", className);
                return NOT_FOUND;
            }
        } catch (ClassNotFoundException e) {
            Debugger.fatal(e);
            return NOT_FOUND;
        }
    }
}
