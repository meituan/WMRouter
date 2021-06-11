package com.sankuai.waimai.router.demo.basic

import android.content.Context
import com.sankuai.waimai.router.common.DefaultUriRequest
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.demo.R
import com.wmrouter.adapter.rxjava3.requestObservable
import io.reactivex.rxjava3.core.Observable

/**
 *
 *  @Author LiABao
 *  @Since 2021/6/11
 *
 */
object RxJavaHelper {
    
    //提供多个可拓展方法
    @JvmStatic
    fun start(context: Context, uri: String): Observable<UriRequest> {
        return DefaultUriRequest(context, uri)
            .activityRequestCode(100)
            .putExtra(TestUriRequestActivity.INTENT_TEST_INT, 1)
            .putExtra(TestUriRequestActivity.INTENT_TEST_STR, "str")
            .overridePendingTransition(R.anim.enter_activity, R.anim.exit_activity)
            .requestObservable()
    }

}