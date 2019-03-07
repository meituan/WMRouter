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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.core.UriRequest;

public class FragmentUriTransactionRequest extends AbsFragmentUriTransactionRequest {
    private final FragmentManager mFragmentManager;

    /**
     * @param activity 父activity
     * @param uri      地址
     */
    public FragmentUriTransactionRequest(@NonNull Activity activity, String uri) {
        super(activity, uri);
        mFragmentManager = activity.getFragmentManager();
    }

    /**
     * @param fragment 父fragment
     * @param uri      地址
     */
    @RequiresApi(17)
    public FragmentUriTransactionRequest(@NonNull Fragment fragment, String uri) {
        super(fragment.getActivity(), uri);
        mFragmentManager = fragment.getChildFragmentManager();
    }

    /**
     * @param context context
     * @param fragmentManager fragmentManager
     * @param uri uri
     */
    public FragmentUriTransactionRequest(@NonNull Context context,@NonNull FragmentManager fragmentManager, String uri) {
        super(context, uri);
        mFragmentManager = fragmentManager;
    }


    @Override
    protected StartFragmentAction getStartFragmentAction() {
        return new BuildStartFragmentAction(mFragmentManager, mContainerViewId, mType, mAllowingStateLoss, mTag);
    }

    static class BuildStartFragmentAction implements StartFragmentAction {

        private final FragmentManager mFragmentManager;
        private final int mContainerViewId;
        private final int mStartType;
        private final boolean mAllowingStateLoss;
        private final String mTag;

        BuildStartFragmentAction(@NonNull FragmentManager fragmentManager,
                                 @IdRes int containerViewId, int startType,
                                 boolean allowingStateLoss, String tag) {
            mFragmentManager = fragmentManager;
            mContainerViewId = containerViewId;
            mStartType = startType;
            mAllowingStateLoss = allowingStateLoss;
            mTag = tag;
        }

        @Override
        public boolean startFragment(@NonNull UriRequest request, @NonNull Bundle bundle) throws ActivityNotFoundException, SecurityException {
            String fragmentClassName = request.getStringField(FragmentTransactionHandler.FRAGMENT_CLASS_NAME);
            if (TextUtils.isEmpty(fragmentClassName)) {
                Debugger.fatal("FragmentTransactionHandler.handleInternal()应返回的带有ClassName");
                return false;
            }
            try {
                Fragment fragment = Fragment.instantiate(request.getContext(), fragmentClassName, bundle);
                if (fragment == null) {
                    return false;
                }
                if (mStartType == TYPE_CUSTOM) {
                    //自定义处理不做transaction，直接放在request里面回调
                    request.putField(CUSTOM_FRAGMENT_NAME, fragment);
                    return true;
                }
                if (mContainerViewId == 0) {
                    Debugger.fatal("FragmentTransactionHandler.handleInternal()mContainerViewId");
                    return false;
                }

                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                switch (mStartType) {
                    case TYPE_ADD:
                        transaction.add(mContainerViewId, fragment, mTag);
                        break;
                    case TYPE_REPLACE:
                        transaction.replace(mContainerViewId, fragment, mTag);
                        break;
                }
                if (mAllowingStateLoss) {
                    transaction.commitAllowingStateLoss();
                } else {
                    transaction.commit();
                }
                return true;
            } catch (Exception e) {
                Debugger.e(e);
                return false;
            }
        }
    }
}
