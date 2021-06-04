package com.sankuai.waimai.router.demo.testannotation;

import androidx.annotation.NonNull;

import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;

/**
 * Created by jzj on 2018/3/26.
 */

public class EmptyHandler extends UriHandler {

    @Override
    protected boolean shouldHandle(@NonNull UriRequest request) {
        return false;
    }

    @Override
    protected void handleInternal(@NonNull UriRequest request, @NonNull UriCallback callback) {

    }
}
