package com.sankuai.waimai.router.utils;

import android.support.annotation.NonNull;

import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.core.Debugger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Provider缓存
 */
public class ProviderPool {

    private static final HashMap<Class, Method> CACHE = new HashMap<>();

    private static final Method NOT_FOUND = Object.class.getDeclaredMethods()[0];

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        Method provider = getProvider(clazz);
        if (provider == NOT_FOUND) {
            Debugger.i("[ProviderPool] provider not found: %s", clazz);
            return null;
        } else {
            Debugger.i("[ProviderPool] provider found: %s", provider);
            try {
                return (T) provider.invoke(null);
            } catch (Exception e) {
                Debugger.fatal(e);
            }
        }
        return null;
    }

    @NonNull
    private static <T> Method getProvider(@NonNull Class<T> clazz) {
        Method provider = CACHE.get(clazz);
        if (provider == null) {
            synchronized (CACHE) {
                provider = CACHE.get(clazz);
                if (provider == null) {
                    provider = findProvider(clazz);
                    CACHE.put(clazz, provider);
                }
            }
        }
        return provider;
    }

    @NonNull
    private static Method findProvider(@NonNull Class clazz) {
        Debugger.i("[ProviderPool] >>> find provider with reflection: %s", clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(RouterProvider.class) != null) {
                if (Modifier.isStatic(method.getModifiers()) &&
                        method.getReturnType() == clazz &&
                        RouterUtils.isEmpty(method.getParameterTypes())) {
                    return method;
                } else {
                    Debugger.fatal("[ProviderPool] RouterProvider注解的应该是静态无参数方法，且返回值类型为当前Class");
                    return NOT_FOUND;
                }
            }
        }
        return NOT_FOUND;
    }
}
