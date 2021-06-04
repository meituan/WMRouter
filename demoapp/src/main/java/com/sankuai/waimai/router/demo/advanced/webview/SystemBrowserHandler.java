package com.sankuai.waimai.router.demo.advanced.webview;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.sankuai.waimai.router.annotation.RouterRegex;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.core.UriResult;

/**
 * 跳转到系统自带浏览器
 *
 * Created by jzj on 2018/3/26.
 */
@RouterRegex(regex = DemoConstant.HTTP_URL_REGEX)
public class SystemBrowserHandler extends UriHandler {

    @Override
    protected boolean shouldHandle(@NonNull UriRequest request) {
        return true;
    }

    @Override
    protected void handleInternal(@NonNull UriRequest request, @NonNull UriCallback callback) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(request.getUri());
            request.getContext().startActivity(intent);
            callback.onComplete(UriResult.CODE_SUCCESS);
        } catch (Exception e) {
            callback.onComplete(UriResult.CODE_ERROR);
        }
    }
}
