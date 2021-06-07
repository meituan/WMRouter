package com.sankuai.waimai.router.common;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.HashMap;

/**
 * 对应@RouterPage的默认封装子类，自动拼装PageAnnotationHandler.SCHEME_HOST和path，避免每次都要手动拼装
 * Created by liaohailiang on 2018/9/27.
 */
public class DefaultPageUriRequest extends DefaultUriRequest {

    public DefaultPageUriRequest(@NonNull Context context, @NonNull String path) {
        super(context, PageAnnotationHandler.SCHEME_HOST + path);
    }

    public DefaultPageUriRequest(@NonNull Context context, @NonNull String path,
                                 HashMap<String, Object> extra) {
        super(context, PageAnnotationHandler.SCHEME_HOST + path, extra);
    }
}
