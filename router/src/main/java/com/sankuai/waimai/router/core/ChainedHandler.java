package com.sankuai.waimai.router.core;

import androidx.annotation.NonNull;

import com.sankuai.waimai.router.utils.PriorityList;

import java.util.Iterator;
import java.util.List;

/**
 * 支持添加多个子 {@link UriHandler} ，按先后顺序依次异步执行
 * Created by jzj on 2017/4/13.
 */
public class ChainedHandler extends UriHandler {

    private final PriorityList<UriHandler> mHandlers = new PriorityList<>();

    /**
     * 添加一个Handler
     *
     * @param priority 优先级。优先级越大越先执行；相同优先级，先加入的先执行。
     */
    public ChainedHandler addChildHandler(@NonNull UriHandler handler, int priority) {
        mHandlers.addItem(handler, priority);
        return this;
    }

    /**
     * 添加一个Handler，优先级为0
     */
    public ChainedHandler addChildHandler(@NonNull UriHandler handler) {
        return addChildHandler(handler, 0);
    }

    @NonNull
    protected List<UriHandler> getHandlers() {
        return mHandlers;
    }

    @Override
    protected boolean shouldHandle(@NonNull UriRequest request) {
        return !mHandlers.isEmpty();
    }

    @Override
    protected void handleInternal(@NonNull final UriRequest request, @NonNull final UriCallback callback) {
        next(mHandlers.iterator(), request, callback);
    }

    private void next(@NonNull final Iterator<UriHandler> iterator, @NonNull final UriRequest request,
                      @NonNull final UriCallback callback) {
        if (iterator.hasNext()) {
            UriHandler t = iterator.next();
            t.handle(request, new UriCallback() {
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
