package com.sankuai.waimai.router.components;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.sankuai.waimai.router.activity.StartActivityAction;
import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.core.UriResult;

import java.util.List;

/**
 * 启动Activity的默认实现
 *
 * Created by jzj on 2018/4/28.
 */
public class DefaultActivityLauncher implements ActivityLauncher {

    public static final DefaultActivityLauncher INSTANCE = new DefaultActivityLauncher();

    private boolean mCheckIntentFirst = false;

    /**
     * 跳转前是否先检查Intent
     */
    public void setCheckIntentFirst(boolean checkIntentFirst) {
        mCheckIntentFirst = checkIntentFirst;
    }

    @SuppressWarnings("ConstantConditions")
    public int startActivity(@NonNull UriRequest request, @NonNull Intent intent) {

        if (request == null || intent == null) {
            return UriResult.CODE_ERROR;
        }

        Context context = request.getContext();

        // Extra
        Bundle extra = request.getField(Bundle.class, FIELD_INTENT_EXTRA);
        if (extra != null) {
            intent.putExtras(extra);
        }

        // Flags
        Integer flags = request.getField(Integer.class, FIELD_START_ACTIVITY_FLAGS);
        if (flags != null) {
            intent.setFlags(flags);
        }

        // request code
        Integer requestCode = request.getField(Integer.class, FIELD_REQUEST_CODE);

        // 是否限制Intent的packageName，限制后只会启动当前App内的页面，不启动其他App的页面，bool型
        boolean limitPackage = request.getBooleanField(FIELD_LIMIT_PACKAGE, false);

        // 设置package，先尝试启动App内的页面
        intent.setPackage(context.getPackageName());

        int r = startIntent(request, intent, context, requestCode, true);

        if (limitPackage || r == UriResult.CODE_SUCCESS) {
            return r;
        }

        // App内启动失败，再尝试启动App外页面
        intent.setPackage(null);

        return startIntent(request, intent, context, requestCode, false);
    }

    /**
     * 启动Intent
     *
     * @param internal 是否启动App内页面
     */
    protected int startIntent(@NonNull UriRequest request, @NonNull Intent intent,
                              Context context, Integer requestCode, boolean internal) {
        if (!checkIntent(context, intent)) {
            return UriResult.CODE_NOT_FOUND;
        }

        if (startActivityByAction(request, intent, internal) == UriResult.CODE_SUCCESS) {
            return UriResult.CODE_SUCCESS;
        }

        return startActivityByDefault(request, context, intent, requestCode, internal);
    }

    /**
     * 检查Intent是否可跳转
     */
    protected boolean checkIntent(Context context, Intent intent) {
        if (mCheckIntentFirst) {
            try {
                PackageManager pm = context.getPackageManager();
                List list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                return list != null && list.size() > 0;
            } catch (Exception e) {
                // package manager has died
                Debugger.fatal(e);
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 使用指定的 {@link StartActivityAction} 启动Intent
     *
     * @param internal 是否启动App内页面
     */
    protected int startActivityByAction(@NonNull UriRequest request,
                                        @NonNull Intent intent, boolean internal) {
        try {
            final StartActivityAction action = request.getField(
                    StartActivityAction.class, FIELD_START_ACTIVITY_ACTION);
            boolean result = action != null && action.startActivity(request, intent);

            if (result) {
                doAnimation(request);

                if (internal) {
                    request.putField(FIELD_STARTED_ACTIVITY, INTERNAL_ACTIVITY);
                    Debugger.i("    internal activity started"
                            + " by StartActivityAction, request = %s", request);
                } else {
                    request.putField(FIELD_STARTED_ACTIVITY, EXTERNAL_ACTIVITY);
                    Debugger.i("    external activity started"
                            + " by StartActivityAction, request = %s", request);
                }

                return UriResult.CODE_SUCCESS;
            } else {
                return UriResult.CODE_ERROR;
            }
        } catch (ActivityNotFoundException e) {
            Debugger.w(e);
            return UriResult.CODE_NOT_FOUND;
        } catch (SecurityException e) {
            Debugger.w(e);
            return UriResult.CODE_FORBIDDEN;
        }
    }

    /**
     * 使用默认方式启动Intent
     *
     * @param internal 是否启动App内页面
     */
    protected int startActivityByDefault(UriRequest request, @NonNull Context context,
                                         @NonNull Intent intent, Integer requestCode, boolean internal) {
        try {
            Bundle options = request.getField(Bundle.class, FIELD_START_ACTIVITY_OPTIONS);

            if (requestCode != null && context instanceof Activity) {
                ActivityCompat.startActivityForResult((Activity) context, intent, requestCode,
                        options);
            } else {
                ActivityCompat.startActivity(context, intent, options);
            }
            doAnimation(request);

            if (internal) {
                request.putField(FIELD_STARTED_ACTIVITY, INTERNAL_ACTIVITY);
                Debugger.i("    internal activity started"
                        + ", request = %s", request);
            } else {
                request.putField(FIELD_STARTED_ACTIVITY, EXTERNAL_ACTIVITY);
                Debugger.i("    external activity started"
                        + ", request = %s", request);
            }

            return UriResult.CODE_SUCCESS;
        } catch (ActivityNotFoundException e) {
            Debugger.w(e);
            return UriResult.CODE_NOT_FOUND;
        } catch (SecurityException e) {
            Debugger.w(e);
            return UriResult.CODE_FORBIDDEN;
        }
    }

    /**
     * 执行动画
     */
    protected void doAnimation(UriRequest request) {
        Context context = request.getContext();
        int[] anim = request.getField(int[].class, FIELD_START_ACTIVITY_ANIMATION);
        if (context instanceof Activity && anim != null && anim.length == 2) {
            ((Activity) context).overridePendingTransition(anim[0], anim[1]);
        }
    }
}
