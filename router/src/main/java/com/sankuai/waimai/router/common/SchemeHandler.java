package com.sankuai.waimai.router.common;

import android.support.annotation.NonNull;

import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.utils.RouterUtils;

/**
 * 处理一个固定的Scheme+Host，并通过Path分发
 *
 * Created by jzj on 17/2/27.
 */
public class SchemeHandler extends PathHandler {

    @NonNull
    private String mSchemeHost;

    public SchemeHandler(String scheme, String host) {
        mSchemeHost = RouterUtils.schemeHost(scheme, host);
    }

    @Override
    public boolean shouldHandle(@NonNull UriRequest request) {
        return matchSchemeHost(request);
    }

    protected boolean matchSchemeHost(@NonNull UriRequest request) {
        return mSchemeHost.equals(request.schemeHost());
    }

    @Override
    public String toString() {
        return "SchemeHandler(" + mSchemeHost + ")";
    }
}
