package com.sankuai.waimai.router.core;

import android.support.annotation.NonNull;

import java.util.Iterator;

/**
 * 依次执行多个异步操作的辅助工具类
 * Created by jzj on 2017/4/13.
 */
public abstract class ChainedAsyncHelper<T> {

    /**
     * 按List中的顺序，依次执行多个异步操作
     */
    public final void run(Iterator<T> iterator, @NonNull UriRequest request,
            @NonNull UriCallback callback) {
        next(iterator, request, callback);
    }

    /**
     * t执行异步操作，执行完后调用callback，回传执行结果
     */
    protected abstract void runAsync(@NonNull T t, @NonNull final UriRequest request,
            @NonNull final UriCallback callback);

    private void next(@NonNull final Iterator<T> iterator, @NonNull final UriRequest request,
            @NonNull final UriCallback callback) {
        if (iterator.hasNext()) {
            T t = iterator.next();
            runAsync(t, request, new UriCallback() {
                @Override
                public void onNext() {
                    next(iterator, request, callback);
                }

                @Override
                public void onComplete(int resultCode) {
                    callback.onComplete(resultCode);
                }
            });
        } else {
            callback.onNext();
        }
    }
}
