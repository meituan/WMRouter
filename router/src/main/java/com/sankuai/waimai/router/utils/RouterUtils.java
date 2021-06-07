package com.sankuai.waimai.router.utils;

import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.sankuai.waimai.router.core.Debugger;

import java.util.Map;

/**
 * Created by jzj on 2018/3/27.
 */

public class RouterUtils {

    /**
     * 转成小写
     */
    public static String toLowerCase(String s) {
        return TextUtils.isEmpty(s) ? s : s.toLowerCase();
    }

    /**
     * 转成非null的字符串，如果为null返回空串
     */
    public static String toNonNullString(String s) {
        return s == null ? "" : s;
    }

    /**
     * 是否为null或长度为0
     */
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    /**
     * 根据scheme和host生成字符串
     */
    @NonNull
    public static String schemeHost(String scheme, String host) {
        return toNonNullString(toLowerCase(scheme)) + "://" + toNonNullString(toLowerCase(host));
    }

    /**
     * 根据scheme和host生成字符串
     */
    public static String schemeHost(Uri uri) {
        return uri == null ? null : schemeHost(uri.getScheme(), uri.getHost());
    }

    /**
     * 在Uri中添加参数
     *
     * @param uri    原始uri
     * @param params 要添加的参数
     * @return uri    新的uri
     */
    public static Uri appendParams(Uri uri, Map<String, String> params) {
        if (uri != null && params != null && !params.isEmpty()) {
            Uri.Builder builder = uri.buildUpon();
            try {
                for (String key : params.keySet()) {
                    if (TextUtils.isEmpty(key)) continue;
                    final String val = uri.getQueryParameter(key);
                    if (val == null) { // 当前没有此参数时，才会添加
                        final String value = params.get(key);
                        builder.appendQueryParameter(key, value);
                    }
                }
                return builder.build();
            } catch (Exception e) {
                Debugger.fatal(e);
            }
        }
        return uri;
    }

    /**
     * 添加斜线前缀
     */
    public static String appendSlash(String path) {
        if (path != null && !path.startsWith("/")) {
            path = '/' + path;
        }
        return path;
    }
}
