package com.wmrouter.adapter.result.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sankuai.waimai.router.components.ActivityLauncher
import com.sankuai.waimai.router.components.DefaultActivityLauncher
import com.sankuai.waimai.router.core.Debugger
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.core.UriResult
import com.wmrouter.adapter.result.startForResult

/**
 * @Author LiABao
 * 通过给当前页面注册一个空fragment接受回调
 * @Since 2021/6/11
 */
open class ForResultActivityLauncher : DefaultActivityLauncher() {

    override fun startActivityByAction(
        request: UriRequest,
        intent: Intent,
        internal: Boolean
    ): Int {
        return UriResult.CODE_NOT_FOUND
    }

    override fun startActivityByDefault(
        request: UriRequest,
        context: Context,
        intent: Intent,
        requestCode: Int?,
        internal: Boolean
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

    companion object {
        @JvmStatic
        val INSTSNCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ForResultActivityLauncher()
        }
    }

}