package com.sankuai.waimai.router.utils;

import android.support.annotation.NonNull;

import com.sankuai.waimai.router.components.RouterComponents;
import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.service.IFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例缓存
 *
 * Created by jzj on 2018/3/29.
 */
public class SingletonPool {

    private static final Map<Class, Object> CACHE = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <I, T extends I> T get(Class<I> clazz, IFactory factory) throws Exception {
        if (clazz == null) {
            return null;
        }
        if (factory == null) {
            factory = RouterComponents.getDefaultFactory();
        }
        Object instance = getInstance(clazz, factory);
        Debugger.i("[SingletonPool]   get instance of class = %s, result = %s", clazz, instance);
        return (T) instance;
    }

    @NonNull
    private static Object getInstance(@NonNull Class clazz, @NonNull IFactory factory) throws Exception {
        Object t = CACHE.get(clazz);
        if (t != null) {
            return t;
        } else {
            synchronized (CACHE) {
                t = CACHE.get(clazz);
                if (t == null) {
                    Debugger.i("[SingletonPool] >>> create instance: %s", clazz);
                    t = factory.create(clazz);
                    //noinspection ConstantConditions
                    if (t != null) {
                        CACHE.put(clazz, t);
                    }
                }
            }
            return t;
        }
    }
}
