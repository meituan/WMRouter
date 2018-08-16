package com.sankuai.waimai.router.demo.advanced.webview;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.core.UriResult;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

/**
 * Created by jzj on 2018/3/27.
 */
@RouterUri(path = DemoConstant.BROWSER)
public class BrowserSchemeHandler extends UriHandler {

    @Override
    protected boolean shouldHandle(@NonNull UriRequest request) {
        return isHttpOrHttps(getUrl(request));
    }

    @Override
    protected void handleInternal(@NonNull UriRequest request, @NonNull UriCallback callback) {
        Uri url = getUrl(request);
        if (url == null) {
            callback.onComplete(UriResult.CODE_ERROR);
        } else {
            request.setUri(url);
            callback.onComplete(UriResult.CODE_REDIRECT);
        }
    }

    private static Uri getUrl(@NonNull UriRequest request) {
        String url = request.getUri().getQueryParameter("url");
        return url == null ? null : Uri.parse(url);
    }

    /**
     * 是不是Http(s)链接
     */
    private static boolean isHttpOrHttps(Uri uri) {
        if (uri == null) return false;
        final String scheme = uri.getScheme();
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }
}
