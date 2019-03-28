package com.sankuai.waimai.router.fragment;
/*
 * Copyright (C) 2005-2018 Meituan Inc.All Rights Reserved.
 * Description：
 * History：
 *
 * @desc
 * @author chenmeng06
 * @date 2019/3/6
 */


import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.sankuai.waimai.router.common.PageAnnotationHandler;


/**
 * Fragment路由跳转基类
 */
public abstract class AbsFragmentTransactionUriRequest extends AbsFragmentUriRequest {

    protected final static int TYPE_ADD = 1;
    protected final static int TYPE_REPLACE = 2;

    protected int mType = TYPE_ADD;
    protected int mContainerViewId;
    protected boolean mAllowingStateLoss;
    protected String mTag;

    public AbsFragmentTransactionUriRequest(@NonNull Context context, String uri) {
        super(context, uri);
    }

    /**
     * 在containerViewId上添加指定的Fragment
     *
     * @param containerViewId 容器ID
     * @return this
     */
    public AbsFragmentTransactionUriRequest add(@IdRes int containerViewId) {
        mContainerViewId = containerViewId;
        mType = TYPE_ADD;
        return this;
    }

    /**
     * 在containerViewId上替换指定的Fragment
     *
     * @param containerViewId 容器ID
     * @return this
     */
    public AbsFragmentTransactionUriRequest replace(@IdRes int containerViewId) {
        mContainerViewId = containerViewId;
        mType = TYPE_REPLACE;
        return this;
    }
    /**
     * 指定tag
     *
     * @param tag 指定tag
     * @return this
     */
    public AbsFragmentTransactionUriRequest tag(String tag) {
        mTag = tag;
        return this;
    }

    /**
     * 允许状态丢失的提交
     *
     * @return this
     */
    public AbsFragmentTransactionUriRequest allowingStateLoss() {
        mAllowingStateLoss = true;
        return this;
    }


}
