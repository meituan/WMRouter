package com.sankuai.waimai.router.fragment;
/*
 * Copyright (C) 2005-2018 Meituan Inc.All Rights Reserved.
 * Description：
 * History：
 *
 * @desc
 * @author chenmeng06
 * @date 2019/3/7
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.sankuai.waimai.router.components.ActivityLauncher;
import com.sankuai.waimai.router.core.UriRequest;

import java.io.Serializable;

/**
 * 带参数的基类 支持了extra参数
 */
public abstract class AbsFragmentUriRequest extends UriRequest {

    public AbsFragmentUriRequest(@NonNull Context context, String uri) {
        super(context, uri);
    }

    @Override
    public void start() {
        putField(StartFragmentAction.START_FRAGMENT_ACTION, getStartFragmentAction());
        super.start();
    }

    protected abstract StartFragmentAction getStartFragmentAction();

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, Serializable value) {
        extra().putSerializable(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, boolean[] value) {
        extra().putBooleanArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, byte[] value) {
        extra().putByteArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, short[] value) {
        extra().putShortArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, char[] value) {
        extra().putCharArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, int[] value) {
        extra().putIntArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, long[] value) {
        extra().putLongArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, float[] value) {
        extra().putFloatArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, double[] value) {
        extra().putDoubleArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, String[] value) {
        extra().putStringArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, CharSequence[] value) {
        extra().putCharSequenceArray(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtra(String name, Bundle value) {
        extra().putBundle(name, value);
        return this;
    }

    /**
     * 附加到Intent的Extra
     */
    public AbsFragmentUriRequest putExtras(Bundle extras) {
        if (extras != null) {
            extra().putAll(extras);
        }
        return this;
    }

    @NonNull
    private synchronized Bundle extra() {
        Bundle extra = getField(Bundle.class, ActivityLauncher.FIELD_INTENT_EXTRA, null);
        if (extra == null) {
            extra = new Bundle();
            putField(ActivityLauncher.FIELD_INTENT_EXTRA, extra);
        }
        return extra;
    }
}
