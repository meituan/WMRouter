package com.sankuai.waimai.router.service;

import android.support.annotation.NonNull;

import com.sankuai.waimai.router.utils.ProviderPool;

/**
 * 默认的Factory，先尝试Provider，再尝试无参数构造
 *
 * Created by jzj on 2018/3/30.
 */

public class DefaultFactory implements IFactory {

    public static final DefaultFactory INSTANCE = new DefaultFactory();

    private DefaultFactory() {

    }

    @NonNull
    @Override
    public <T> T create(@NonNull Class<T> clazz) throws Exception {
        T t = ProviderPool.create(clazz);
        if (t != null) {
            return t;
        } else {
            return clazz.newInstance();
        }
    }
}
