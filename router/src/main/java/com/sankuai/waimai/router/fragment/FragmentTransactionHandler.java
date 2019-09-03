package com.sankuai.waimai.router.fragment;
/*
 * Copyright (C) 2005-2018 Meituan Inc.All Rights Reserved.
 * Description：
 * History：
 *
 * @desc
 * @author chenmeng06
 * @date 2019/3/5
 */


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.core.UriResult;

import static com.sankuai.waimai.router.components.ActivityLauncher.FIELD_INTENT_EXTRA;

/**
 * Fragment处理的的Handler
 */
public final class FragmentTransactionHandler extends UriHandler {
    public final static String FRAGMENT_CLASS_NAME = "FRAGMENT_CLASS_NAME";

    @NonNull
    private final String mClassName;

    @NonNull
    public String getClassName() {
        return mClassName;
    }

    public FragmentTransactionHandler(@NonNull String className) {
        mClassName = className;
    }

    @Override
    protected boolean shouldHandle(@NonNull UriRequest request) {
        return true;
    }

    @Override
    protected void handleInternal(@NonNull UriRequest request, @NonNull UriCallback callback) {
        if (TextUtils.isEmpty(mClassName)) {
            Debugger.fatal("FragmentTransactionHandler.handleInternal()应返回的带有ClassName");
            callback.onComplete(UriResult.CODE_BAD_REQUEST);
            return;
        }

        StartFragmentAction action = request.getField(StartFragmentAction.class, StartFragmentAction.START_FRAGMENT_ACTION);
        if (action == null) {
            Debugger.fatal("FragmentTransactionHandler.handleInternal()应返回的带有StartFragmentAction");
            callback.onComplete(UriResult.CODE_BAD_REQUEST);
            return;
        }

        if (!request.hasField(FRAGMENT_CLASS_NAME)) {
            //判断一下，便于被替换
            request.putField(FRAGMENT_CLASS_NAME, mClassName);
        }

        // Extra
        Bundle extra = request.getField(Bundle.class, FIELD_INTENT_EXTRA);
        boolean success = action.startFragment(request, extra);
        // 完成
        callback.onComplete(success ? UriResult.CODE_SUCCESS : UriResult.CODE_BAD_REQUEST);
    }
}
