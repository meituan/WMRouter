package com.sankuai.waimai.router.utils;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * key大小写不敏感、忽略null值的Map。Map内部采用小写保存key。
 */
public class CaseInsensitiveNonNullMap<T> {

    private final HashMap<String, T> mMap = new HashMap<>();

    public T put(String key, T value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return null;
        }
        return mMap.put(RouterUtils.toLowerCase(key), value);
    }

    public T get(String key) {
        return mMap.get(RouterUtils.toLowerCase(key));
    }

    public T remove(String key) {
        return mMap.remove(RouterUtils.toLowerCase(key));
    }

    public boolean containsKey(String key) {
        return mMap.containsKey(RouterUtils.toLowerCase(key));
    }
}
