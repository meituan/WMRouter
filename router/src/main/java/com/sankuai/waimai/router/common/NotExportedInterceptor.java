package com.sankuai.waimai.router.common;

import android.support.annotation.NonNull;

import com.sankuai.waimai.router.components.UriSourceTools;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriInterceptor;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.core.UriResult;

/**
 * 不支持exported拦截并返回 {@link UriResult#CODE_FORBIDDEN}
 *
 * Created by jzj on 2018/3/26.
 */

public class NotExportedInterceptor implements UriInterceptor {

    public static final NotExportedInterceptor INSTANCE = new NotExportedInterceptor();

    private NotExportedInterceptor() {
    }

    @Override
    public void intercept(@NonNull UriRequest request, @NonNull UriCallback callback) {
        if (UriSourceTools.shouldHandle(request, false)) {
            callback.onNext();
        } else {
            callback.onComplete(UriResult.CODE_FORBIDDEN);
        }
    }
}
