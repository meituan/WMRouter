package com.sankuai.waimai.router.core;

import android.support.annotation.NonNull;

/**
 * 处理某一类或某个Uri的Handler
 *
 * Created by jzj on 17/2/27.
 */
public abstract class UriHandler {

    protected ChainedInterceptor mInterceptor;

    @SuppressWarnings("ConstantConditions")
    public UriHandler addInterceptor(@NonNull UriInterceptor interceptor) {
        if (interceptor != null) {
            if (mInterceptor == null) {
                mInterceptor = new ChainedInterceptor();
            }
            mInterceptor.addInterceptor(interceptor);
        }
        return this;
    }

    public UriHandler addInterceptors(UriInterceptor... interceptors) {
        if (interceptors != null && interceptors.length > 0) {
            if (mInterceptor == null) {
                mInterceptor = new ChainedInterceptor();
            }
            for (UriInterceptor interceptor : interceptors) {
                mInterceptor.addInterceptor(interceptor);
            }
        }
        return this;
    }

    public void handle(@NonNull final UriRequest request, @NonNull final UriCallback callback) {
        if (shouldHandle(request)) {
            Debugger.i("%s: handle request %s", this, request);
            if (mInterceptor != null) {
                mInterceptor.intercept(request, new UriCallback() {
                    @Override
                    public void onNext() {
                        handleInternal(request, callback);
                    }

                    @Override
                    public void onComplete(int result) {
                        callback.onComplete(result);
                    }
                });
            } else {
                handleInternal(request, callback);
            }
        } else {
            Debugger.i("%s: ignore request %s", this, request);
            callback.onNext();
        }
    }

    /**
     * toString方法用于打Log
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * 是否要处理给定的uri。在Interceptor之前调用。
     */
    protected abstract boolean shouldHandle(@NonNull UriRequest request);

    /**
     * 处理uri。在Interceptor之后调用。
     */
    protected abstract void handleInternal(@NonNull UriRequest request, @NonNull UriCallback callback);
}
