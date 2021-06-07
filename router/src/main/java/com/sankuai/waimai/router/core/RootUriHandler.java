package com.sankuai.waimai.router.core;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.sankuai.waimai.router.utils.LazyInitHelper;


/**
 * 最顶层的 {@link UriHandler}
 *
 * Created by jzj on 2017/4/17.
 */
public class RootUriHandler extends ChainedHandler {

    private final Context mContext;
    private OnCompleteListener mGlobalOnCompleteListener;

    public RootUriHandler(Context context) {
        mContext = context.getApplicationContext();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * @see LazyInitHelper#lazyInit()
     */
    public void lazyInit() {

    }

    /**
     * 全局 {@link OnCompleteListener}
     */
    public void setGlobalOnCompleteListener(OnCompleteListener listener) {
        mGlobalOnCompleteListener = listener;
    }

    /**
     * 全局 {@link OnCompleteListener}
     */
    public OnCompleteListener getGlobalOnCompleteListener() {
        return mGlobalOnCompleteListener;
    }

    @Override
    public RootUriHandler addChildHandler(@NonNull UriHandler handler, int priority) {
        return (RootUriHandler) super.addChildHandler(handler, priority);
    }

    @Override
    public RootUriHandler addChildHandler(@NonNull UriHandler handler) {
        return addChildHandler(handler, 0);
    }

    @SuppressWarnings("unchecked")
    public <T extends UriHandler> T findChildHandlerByClass(Class<T> clazz) {
        for (UriHandler handler : getHandlers()) {
            if (clazz.isInstance(handler)) {
                return (T) handler;
            }
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    public void startUri(@NonNull UriRequest request) {
        if (request == null) {

            String msg = "UriRequest为空";
            Debugger.fatal(msg);
            UriRequest req = new UriRequest(mContext, Uri.EMPTY).setErrorMessage(msg);
            onError(req, UriResult.CODE_BAD_REQUEST);

        } else if (request.getContext() == null) {

            String msg = "UriRequest.Context为空";
            Debugger.fatal(msg);
            UriRequest req = new UriRequest(mContext, request.getUri(), request.getFields())
                    .setErrorMessage(msg);
            onError(req, UriResult.CODE_BAD_REQUEST);

        } else if (request.isUriEmpty()) {

            String msg = "跳转链接为空";
            Debugger.e(msg);
            request.setErrorMessage(msg);
            onError(request, UriResult.CODE_BAD_REQUEST);

        } else {

            if (Debugger.isEnableLog()) {
                Debugger.i("");
                Debugger.i("---> receive request: %s", request.toFullString());
            }
            handle(request, new RootUriCallback(request));
        }
    }

    private void onSuccess(@NonNull UriRequest request) {
        OnCompleteListener globalListener = mGlobalOnCompleteListener;
        if (globalListener != null) {
            globalListener.onSuccess(request);
        }
        final OnCompleteListener listener = request.getOnCompleteListener();
        if (listener != null) {
            listener.onSuccess(request);
        }
    }

    private void onError(@NonNull UriRequest request, int resultCode) {
        OnCompleteListener globalListener = mGlobalOnCompleteListener;
        if (globalListener != null) {
            globalListener.onError(request, resultCode);
        }
        final OnCompleteListener listener = request.getOnCompleteListener();
        if (listener != null) {
            listener.onError(request, resultCode);
        }
    }

    protected class RootUriCallback implements UriCallback {

        private final UriRequest mRequest;

        public RootUriCallback(UriRequest request) {
            mRequest = request;
        }

        @Override
        public void onNext() {
            onComplete(CODE_NOT_FOUND);
        }

        @Override
        public void onComplete(int resultCode) {
            switch (resultCode) {

                case CODE_REDIRECT:
                    // 重定向，重新跳转
                    Debugger.i("<--- redirect, result code = %s", resultCode);
                    startUri(mRequest);
                    break;

                case CODE_SUCCESS:
                    // 跳转成功
                    mRequest.putField(UriRequest.FIELD_RESULT_CODE, resultCode);
                    onSuccess(mRequest);
                    Debugger.i("<--- success, result code = %s", resultCode);
                    break;

                default:
                    // 跳转失败
                    mRequest.putField(UriRequest.FIELD_RESULT_CODE, resultCode);
                    onError(mRequest, resultCode);
                    Debugger.i("<--- error, result code = %s", resultCode);
                    break;
            }
        }
    }
}
