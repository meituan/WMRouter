package com.sankuai.waimai.router.service;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * 使用Context构造
 *
 * Created by jzj on 2018/3/30.
 */

public class ContextFactory implements IFactory {

    private final Context mContext;

    public ContextFactory(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public <T> T create(@NonNull Class<T> clazz) throws Exception {
        return clazz.getConstructor(Context.class).newInstance(mContext);
    }
}
