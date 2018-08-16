package com.sankuai.waimai.router.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.core.UriRequest;

/**
 * 通过Activity类名跳转
 *
 * Created by jzj on 2018/3/23.
 */

public class ActivityClassNameHandler extends AbsActivityHandler {

    @NonNull
    private final String mClassName;

    public ActivityClassNameHandler(@NonNull String className) {
        if (TextUtils.isEmpty(className)) {
            Debugger.fatal(new NullPointerException("className不应该为空"));
        }
        mClassName = className;
    }

    @NonNull
    @Override
    protected Intent createIntent(@NonNull UriRequest request) {
        return new Intent().setClassName(request.getContext(), mClassName);
    }

    @Override
    public String toString() {
        return "ActivityHandler (" + mClassName + ")";
    }
}
