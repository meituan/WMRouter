package com.sankuai.waimai.router.components;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.core.UriRequest;

/**
 * 跳转来源相关。跳转来源可用于权限控制(exported)、埋点统计等功能。
 * 在 {@link UriRequest} 中设置的 {@link #FIELD_FROM} ，会被带到Intent的参数
 * {@link #INTENT_KEY_URI_FROM} 中，Activity也可以根据此参数做特殊逻辑处理。
 *
 * Created by jzj on 2018/1/29.
 */
public class UriSourceTools {

    /**
     * 无效来源
     */
    public static final int FROM_INVALID = 0;
    /**
     * 外部跳转
     */
    public static final int FROM_EXTERNAL = FROM_INVALID + 1;
    /**
     * 内部跳转
     */
    public static final int FROM_INTERNAL = FROM_EXTERNAL + 1;
    /**
     * 从WebView跳转
     */
    public static final int FROM_WEBVIEW = FROM_INTERNAL + 1;
    /**
     * 从Push跳转
     */
    public static final int FROM_PUSH = FROM_WEBVIEW + 1;

    public static final String FIELD_FROM = "com.sankuai.waimai.router.from";

    /**
     * Intent中Scheme跳转来源参数的Key
     */
    public static final String INTENT_KEY_URI_FROM = "com.sankuai.waimai.router.from";

    public static boolean sDisableExportedControl = false;

    /**
     * 是否禁用外部跳转控制
     */
    public static void setDisableExportedControl(boolean disableExportedControl) {
        sDisableExportedControl = disableExportedControl;
    }

    /**
     * 跳转来源控制
     */
    public static boolean shouldHandle(UriRequest request, boolean exported) {
        return sDisableExportedControl || exported || getSource(request) != FROM_EXTERNAL;
    }

    public static void setSource(UriRequest request, int from) {
        if (request != null) {
            request.putField(FIELD_FROM, from);
        }
    }

    public static int getSource(@NonNull UriRequest request) {
        return getSource(request, FROM_INTERNAL);
    }

    public static int getSource(@NonNull UriRequest request, int defaultValue) {
        return request.getIntField(FIELD_FROM, defaultValue);
    }

    /**
     * 从request将source参数设置到intent中
     */
    public static void setIntentSource(Intent intent, UriRequest request) {
        if (intent != null && request != null) {
            Integer result = request.getField(Integer.class, FIELD_FROM);
            if (result != null) {
                setSource(intent, result);
            }
        }
    }

    /**
     * 将source参数设置到intent中
     */
    public static void setSource(Intent intent, int source) {
        if (intent != null) {
            intent.putExtra(UriSourceTools.INTENT_KEY_URI_FROM, source);
        }
    }

    public static int getSource(Intent intent, int defaultValue) {
        return getInt(intent, INTENT_KEY_URI_FROM, defaultValue);
    }

    public static int getSource(Bundle bundle, int defaultValue) {
        return bundle == null ? defaultValue : bundle.getInt(INTENT_KEY_URI_FROM, defaultValue);
    }

    private static int getInt(Intent intent, String key, int defaultValue) {
        if (intent == null) {
            return defaultValue;
        }
        try {
            return intent.getIntExtra(key, defaultValue);
        } catch (Exception e) {
            Debugger.fatal(e);
        }
        return defaultValue;
    }
}
