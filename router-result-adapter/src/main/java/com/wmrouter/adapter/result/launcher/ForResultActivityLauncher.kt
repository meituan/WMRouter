package com.wmrouter.adapter.result.launcher

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sankuai.waimai.router.components.ActivityLauncher
import com.sankuai.waimai.router.core.Debugger
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import com.wmrouter.adapter.result.startForResult

/**
 * 启动Activity的默认实现
 *
 *
 * Created by jzj on 2018/4/28.
 */
open class ForResultActivityLauncher : ActivityLauncher {
    private var mCheckIntentFirst = false

    override fun startActivity(request: UriRequest, intent: Intent): Int {
        val context = request.context

        // Extra
        val extra = request.getField(Bundle::class.java, ActivityLauncher.FIELD_INTENT_EXTRA)
        if (extra != null) {
            intent.putExtras(extra)
        }

        // Flags
        val flags = request.getField(Int::class.java, ActivityLauncher.FIELD_START_ACTIVITY_FLAGS)
        if (flags != null) {
            intent.flags = flags
        }

        // request code
        val requestCode: Integer? =
            request.getField(Integer::class.java, ActivityLauncher.FIELD_REQUEST_CODE)

        // 是否限制Intent的packageName，限制后只会启动当前App内的页面，不启动其他App的页面，bool型
        val limitPackage = request.getBooleanField(ActivityLauncher.FIELD_LIMIT_PACKAGE, false)

        // 设置package，先尝试启动App内的页面
        intent.setPackage(context.packageName)
        return startIntent(request, intent, context, requestCode, true)
    }

    /**
     * 启动Intent
     *
     * @param internal 是否启动App内页面
     */
    private fun startIntent(
        request: UriRequest, intent: Intent,
        context: Context, requestCode: Integer?, internal: Boolean
    ): Int {
        if (!checkIntent(context, intent)) {
            return UriResult.CODE_NOT_FOUND
        }
        return startActivityByDefault(request, context, intent, requestCode, internal)
    }

    /**
     * 检查Intent是否可跳转
     */
    private fun checkIntent(context: Context, intent: Intent?): Boolean {
        return if (mCheckIntentFirst) {
            try {
                if (intent == null) {
                    return false
                }
                val pm = context.packageManager
                val list: List<ResolveInfo> =
                    pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                list.isNotEmpty()
            } catch (e: Exception) {
                // package manager has died
                Debugger.fatal(e)
                false
            }
        } else {
            true
        }
    }


    /**
     * 使用默认方式启动Intent
     *
     * @param internal 是否启动App内页面
     */
    private fun startActivityByDefault(
        request: UriRequest, context: Context,
        intent: Intent, requestCode: Integer?, internal: Boolean
    ): Int {
        return try {
            val options =
                request.getField(Bundle::class.java, ActivityLauncher.FIELD_START_ACTIVITY_OPTIONS)
            if (requestCode != null && context is AppCompatActivity) {
                context.startForResult(requestCode.toInt(), intent, {
                    request.onCompleteListener.onSuccess(request)
                }, {
                    request.onCompleteListener.onError(request, UriResult.CODE_NOT_FOUND)
                })
            } else {
                ActivityCompat.startActivity(context, intent, options)
            }
            doAnimation(request)
            if (internal) {
                request.putField(
                    ActivityLauncher.FIELD_STARTED_ACTIVITY,
                    ActivityLauncher.INTERNAL_ACTIVITY
                )
                Debugger.i("    internal activity started" + ", request = %s", request)
            } else {
                request.putField(
                    ActivityLauncher.FIELD_STARTED_ACTIVITY,
                    ActivityLauncher.EXTERNAL_ACTIVITY
                )
                Debugger.i("    external activity started" + ", request = %s", request)
            }
            return if (requestCode != null && context is AppCompatActivity) {
                UriResult.CODE_FOR_RESULT
            } else {
                UriResult.CODE_SUCCESS
            }
        } catch (e: ActivityNotFoundException) {
            Debugger.w(e)
            UriResult.CODE_NOT_FOUND
        } catch (e: SecurityException) {
            Debugger.w(e)
            UriResult.CODE_FORBIDDEN
        }
    }

    /**
     * 执行动画
     */
    private fun doAnimation(request: UriRequest) {
        val context = request.context
        val anim =
            request.getField(IntArray::class.java, ActivityLauncher.FIELD_START_ACTIVITY_ANIMATION)
        if (context is Activity && anim != null && anim.size == 2) {
            context.overridePendingTransition(anim[0], anim[1])
        }
    }

    companion object {
        @JvmField
        val INSTANCE = ForResultActivityLauncher()
    }
}