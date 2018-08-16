package com.sankuai.waimai.router.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.components.RouterComponents;
import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriInterceptor;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.utils.LazyInitHelper;
import com.sankuai.waimai.router.utils.RouterUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Scheme跳转，通过注解 {@link RouterUri} 配置，可处理多个Scheme+Host
 *
 * Created by jzj on 2018/3/23.
 */
public class UriAnnotationHandler extends UriHandler {

    private static boolean sAddNotFoundHandler = true;

    public static void setAddNotFoundHandler(boolean addNotFoundHandler) {
        sAddNotFoundHandler = addNotFoundHandler;
    }

    private final Map<String, PathHandler> mMap = new HashMap<>();
    private final String mDefaultScheme;
    private final String mDefaultHost;

    private final LazyInitHelper mInitHelper = new LazyInitHelper("UriAnnotationHandler") {
        @Override
        protected void doInit() {
            initAnnotationConfig();
        }
    };

    public UriAnnotationHandler(@Nullable String defaultScheme, @Nullable String defaultHost) {
        mDefaultScheme = RouterUtils.toNonNullString(defaultScheme);
        mDefaultHost = RouterUtils.toNonNullString(defaultHost);
    }

    /**
     * @see LazyInitHelper#lazyInit()
     */
    public void lazyInit() {
        mInitHelper.lazyInit();
    }

    protected void initAnnotationConfig() {
        RouterComponents.loadAnnotation(this, IUriAnnotationInit.class);
    }

    public PathHandler getPathHandler(String scheme, String host) {
        return mMap.get(RouterUtils.schemeHost(scheme, host));
    }

    /**
     * 给指定scheme和host的节点设置path前缀
     */
    public void setPathPrefix(String scheme, String host, String prefix) {
        PathHandler pathHandler = getPathHandler(scheme, host);
        if (pathHandler != null) {
            pathHandler.setPathPrefix(prefix);
        }
    }

    /**
     * 给所有节点设置path前缀
     */
    public void setPathPrefix(String prefix) {
        for (PathHandler pathHandler : mMap.values()) {
            pathHandler.setPathPrefix(prefix);
        }
    }

    public void register(String scheme, String host, String path,
            Object handler, boolean exported, UriInterceptor... interceptors) {
        // 没配的使用默认值
        if (TextUtils.isEmpty(scheme)) {
            scheme = mDefaultScheme;
        }
        if (TextUtils.isEmpty(host)) {
            host = mDefaultHost;
        }
        String schemeHost = RouterUtils.schemeHost(scheme, host);
        PathHandler pathHandler = mMap.get(schemeHost);
        if (pathHandler == null) {
            pathHandler = createPathHandler();
            mMap.put(schemeHost, pathHandler);
        }
        pathHandler.register(path, handler, exported, interceptors);
    }

    @NonNull
    protected PathHandler createPathHandler() {
        PathHandler pathHandler = new PathHandler();
        if (sAddNotFoundHandler) {
            pathHandler.setDefaultChildHandler(NotFoundHandler.INSTANCE);
        }
        return pathHandler;
    }

    private PathHandler getChild(@NonNull UriRequest request) {
        return mMap.get(request.schemeHost());
    }

    @Override
    public void handle(@NonNull UriRequest request, @NonNull UriCallback callback) {
        mInitHelper.ensureInit();
        super.handle(request, callback);
    }

    @Override
    protected boolean shouldHandle(@NonNull UriRequest request) {
        return getChild(request) != null;
    }

    @Override
    protected void handleInternal(@NonNull UriRequest request, @NonNull UriCallback callback) {
        PathHandler pathHandler = getChild(request);
        if (pathHandler != null) {
            pathHandler.handle(request, callback);
        } else {
            // 没找到的继续分发
            callback.onNext();
        }
    }

    @Override
    public String toString() {
        return "UriAnnotationHandler";
    }
}
