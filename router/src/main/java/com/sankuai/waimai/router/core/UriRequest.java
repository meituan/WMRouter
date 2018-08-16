package com.sankuai.waimai.router.core;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.utils.RouterUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 一次Uri跳转请求。可以包含扩展参数，例如事件监听；在 {@link UriHandler} {@link UriInterceptor} 间通信；返回值等。
 *
 * Created by jzj on 2017/4/11.
 */
public class UriRequest {

    /**
     * 跳转请求完成的回调
     */
    public static final String FIELD_COMPLETE_LISTENER =
            "com.sankuai.waimai.router.core.CompleteListener";
    /**
     * 跳转请求的结果
     */
    public static final String FIELD_RESULT_CODE = "com.sankuai.waimai.router.core.result";
    /**
     * 跳转请求失败信息
     */
    public static final String FIELD_ERROR_MSG = "com.sankuai.waimai.router.core.error.msg";

    @NonNull
    private final Context mContext;
    @NonNull
    private Uri mUri;
    @NonNull
    private final HashMap<String, Object> mFields;

    private String mSchemeHost = null;

    public UriRequest(@NonNull Context context, String uri) {
        this(context, parseUriSafely(uri), new HashMap<String, Object>());
    }

    public UriRequest(@NonNull Context context, Uri uri) {
        this(context, uri, new HashMap<String, Object>());
    }

    public UriRequest(@NonNull Context context, String uri, HashMap<String, Object> fields) {
        this(context, parseUriSafely(uri), fields);
    }

    public UriRequest(@NonNull Context context, Uri uri, HashMap<String, Object> fields) {
        mContext = context;
        mUri = uri == null ? Uri.EMPTY : uri;
        mFields = fields == null ? new HashMap<String, Object>() : fields;
    }

    @NonNull
    public HashMap<String, Object> getFields() {
        return mFields;
    }

    private static Uri parseUriSafely(@Nullable String uri) {
        return TextUtils.isEmpty(uri) ? Uri.EMPTY : Uri.parse(uri);
    }

    /**
     * 监听URI分发完成事件
     *
     * @see OnCompleteListener
     */
    public UriRequest onComplete(OnCompleteListener listener) {
        putField(FIELD_COMPLETE_LISTENER, listener);
        return this;
    }

    public UriRequest setResultCode(int resultCode) {
        putField(FIELD_RESULT_CODE, resultCode);
        return this;
    }

    public UriRequest setErrorMessage(String message) {
        putField(FIELD_ERROR_MSG, message);
        return this;
    }

    public OnCompleteListener getOnCompleteListener() {
        return getField(OnCompleteListener.class, UriRequest.FIELD_COMPLETE_LISTENER);
    }

    public int getResultCode() {
        return getIntField(FIELD_RESULT_CODE, UriResult.CODE_ERROR);
    }

    public String getErrorMessage() {
        return getStringField(FIELD_ERROR_MSG, "");
    }

    /**
     * 根据scheme和host生成的字符串
     */
    public String schemeHost() {
        if (mSchemeHost == null) {
            mSchemeHost = RouterUtils.schemeHost(getUri());
        }
        return mSchemeHost;
    }

    /**
     * 判断Uri是否为空。空的Uri会在RootHandler中被处理，其他Handler不需要关心。
     */
    public boolean isUriEmpty() {
        return Uri.EMPTY.equals(mUri);
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }

    @NonNull
    public Uri getUri() {
        return mUri;
    }

    @SuppressWarnings("ConstantConditions")
    public void setUri(@NonNull Uri uri) {
        if (uri != null && !Uri.EMPTY.equals(uri)) {
            mUri = uri;
            mSchemeHost = null;
        } else {
            Debugger.fatal("UriRequest.setUri不应该传入空值");
        }
    }

    /**
     * 设置Extra参数
     */
    public <T> UriRequest putField(@NonNull String key, T val) {
        if (val != null) {
            mFields.put(key, val);
        }
        return this;
    }

    public synchronized <T> UriRequest putFieldIfAbsent(@NonNull String key, T val) {
        if (val != null) {
            if (!mFields.containsKey(key)) {
                mFields.put(key, val);
            }
        }
        return this;
    }

    public UriRequest putFields(HashMap<String, Object> fields) {
        if (fields != null) {
            mFields.putAll(fields);
        }
        return this;
    }

    public boolean hasField(@NonNull String key) {
        return mFields.containsKey(key);
    }

    public int getIntField(@NonNull String key, int defaultValue) {
        return getField(Integer.class, key, defaultValue);
    }

    public long getLongField(@NonNull String key, long defaultValue) {
        return getField(Long.class, key, defaultValue);
    }

    public boolean getBooleanField(@NonNull String key, boolean defaultValue) {
        return getField(Boolean.class, key, defaultValue);
    }

    public String getStringField(@NonNull String key) {
        return getField(String.class, key, null);
    }

    public String getStringField(@NonNull String key, String defaultValue) {
        return getField(String.class, key, defaultValue);
    }

    public <T> T getField(@NonNull Class<T> clazz, @NonNull String key) {
        return getField(clazz, key, null);
    }

    public <T> T getField(@NonNull Class<T> clazz, @NonNull String key, T defaultValue) {
        Object field = mFields.get(key);
        if (field != null) {
            try {
                return clazz.cast(field);
            } catch (ClassCastException e) {
                Debugger.fatal(e);
            }
        }
        return defaultValue;
    }

    public void start() {
        Router.startUri(this);
    }

    @Override
    public String toString() {
        return mUri.toString();
    }

    public String toFullString() {
        StringBuilder s = new StringBuilder(mUri.toString());
        s.append(", fields = {");
        boolean first = true;
        for (Map.Entry<String, Object> entry : mFields.entrySet()) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            s.append(entry.getKey()).append(" = ").append(entry.getValue());
        }
        s.append("}");
        return s.toString();
    }
}
