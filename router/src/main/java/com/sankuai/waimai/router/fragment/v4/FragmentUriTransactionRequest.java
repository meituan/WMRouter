package com.sankuai.waimai.router.fragment.v4;
/*
 * Copyright (C) 2005-2018 Meituan Inc.All Rights Reserved.
 * Description：
 * History：
 *
 * @desc
 * @author chenmeng06
 * @date 2019/3/5
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.fragment.AbsFragmentUriTransactionRequest;
import com.sankuai.waimai.router.fragment.FragmentTransactionHandler;
import com.sankuai.waimai.router.fragment.StartFragmentAction;

/**
 * v4 Fragment跳转的Handler
 */
public class FragmentUriTransactionRequest extends AbsFragmentUriTransactionRequest {

    private final FragmentManager mFragmentManager;

    /**
     * @param activity 父activity
     * @param uri 地址
     */
    public FragmentUriTransactionRequest(@NonNull FragmentActivity activity, String uri) {
        super(activity, uri);
        mFragmentManager = activity.getSupportFragmentManager();
    }

    /**
     * @param fragment 父fragment
     * @param uri 地址
     */
    public FragmentUriTransactionRequest(@NonNull Fragment fragment, String uri) {
        super(fragment.getContext(), uri);
        mFragmentManager = fragment.getChildFragmentManager();
    }

    @Override
    protected StartFragmentAction getStartFragmentAction(int containerViewId, int type) {
        return new BuildStartFragmentAction(mFragmentManager, containerViewId, type);
    }

    static class BuildStartFragmentAction implements StartFragmentAction {

        private final FragmentManager mFragmentManager;
        private final int mContainerViewId;
        private final int mStartType;

        BuildStartFragmentAction(@NonNull FragmentManager fragmentManager, @IdRes int containerViewId, int startType) {
            mFragmentManager = fragmentManager;
            mContainerViewId = containerViewId;
            mStartType = startType;
        }

        @Override
        public boolean startFragment(@NonNull UriRequest request, @NonNull Bundle bundle) throws ActivityNotFoundException, SecurityException {
            if(mContainerViewId == 0) {
                Debugger.fatal("FragmentTransactionHandler.handleInternal()应返回的带有ClassName");
                return false;
            }
            String fragmentClassName = request.getStringField(FragmentTransactionHandler.FRAGMENT_CLASS_NAME);
            if(TextUtils.isEmpty(fragmentClassName)) {
                Debugger.fatal("FragmentTransactionHandler.handleInternal()应返回的带有ClassName");
                return false;
            }
            try {
                Fragment fragment = Fragment.instantiate(request.getContext(),fragmentClassName, bundle);
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                switch (mStartType) {
                    case TYPE_ADD:
                        transaction.add(mContainerViewId, fragment).commit();
                        break;
                    case TYPE_REPLACE:
                        transaction.replace(mContainerViewId, fragment).commit();
                        break;
                }
                return true;
            }catch (Exception e){
                Debugger.e(e);
                return false;
            }
        }
    }
}
