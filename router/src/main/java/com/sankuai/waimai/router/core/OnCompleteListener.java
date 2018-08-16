package com.sankuai.waimai.router.core;

import android.support.annotation.NonNull;

/**
 * URI分发完成的监听器
 *
 * Created by jzj on 2017/4/18.
 */
public interface OnCompleteListener extends UriResult {

    /**
     * 分发成功
     */
    void onSuccess(@NonNull UriRequest request);

    /**
     * 分发失败
     *
     * @param resultCode 错误代码，可参考 {@link UriResult}
     */
    void onError(@NonNull UriRequest request, int resultCode);
}
