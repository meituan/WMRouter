package com.sankuai.waimai.router.core;

import android.support.annotation.NonNull;

import java.util.ArrayList;


/**
 * 支持添加多个子 {@link UriInterceptor} ，按先后顺序依次异步执行
 * Created by jzj on 2017/4/11.
 */
public class ChainedInterceptor implements UriInterceptor {

    private final ArrayList<UriInterceptor> mInterceptors = new ArrayList<>();
    private final ChainedInterceptorRunner mRunner = new ChainedInterceptorRunner();

    @SuppressWarnings("ConstantConditions")
    public void addInterceptor(@NonNull UriInterceptor interceptor) {
        if (interceptor != null) {
            mInterceptors.add(interceptor);
        }
    }

    @Override
    public void intercept(@NonNull UriRequest request, @NonNull UriCallback callback) {
        mRunner.run(mInterceptors.iterator(), request, callback);
    }

    public static class ChainedInterceptorRunner extends ChainedAsyncHelper<UriInterceptor> {

        @Override
        protected void runAsync(@NonNull UriInterceptor interceptor,
                @NonNull UriRequest request,
                @NonNull UriCallback callback) {
            if (Debugger.isEnableLog()) {
                Debugger.i("    %s: intercept, request = %s",
                        interceptor.getClass().getSimpleName(), request);
            }
            interceptor.intercept(request, callback);
        }
    }
}
