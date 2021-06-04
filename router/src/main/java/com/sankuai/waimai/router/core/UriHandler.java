package com.sankuai.waimai.router.core;

import androidx.annotation.NonNull;

/**
 * 处理某一类或某个URI。支持添加若干个 {@link UriInterceptor} 。
 * 子类主要覆写 {@link #shouldHandle(UriRequest)} 和 {@link #handleInternal(UriRequest, UriCallback)} 方法。
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

    /**
     * 处理URI。通常不需要覆写本方法。
     *
     * @param request  URI跳转请求
     * @param callback 处理完成后的回调
     */
    public void handle(@NonNull final UriRequest request, @NonNull final UriCallback callback) {
        if (shouldHandle(request)) {
            Debugger.i("%s: handle request %s", this, request);
            if (mInterceptor != null && !request.isSkipInterceptors()) {
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
     * 是否要处理给定的URI。在 {@link UriInterceptor} 之前调用。
     */
    protected abstract boolean shouldHandle(@NonNull UriRequest request);

    /**
     * 处理URI。在 {@link UriInterceptor} 之后调用。
     */
    protected abstract void handleInternal(@NonNull UriRequest request, @NonNull UriCallback callback);
}
