package com.sankuai.waimai.router.demo.fragment;
/*
 * Copyright (C) 2005-2018 Meituan Inc.All Rights Reserved.
 * Description：
 * History：
 *
 * @desc
 * @author chenmeng06
 * @date 2019/3/5
 */

import android.support.annotation.NonNull;

import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriInterceptor;
import com.sankuai.waimai.router.core.UriRequest;

public class DemoFragmentInterceptor implements UriInterceptor {
    @Override
    public void intercept(@NonNull UriRequest request, @NonNull UriCallback callback) {
        Debugger.d("it worked");
        callback.onNext();
    }
}
