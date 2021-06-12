package com.wmrouter.adapter.coroutines

import com.sankuai.waimai.router.core.OnCompleteListener
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.exception.RouteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * @Author LiABao
 * 增加kotlin 协程拓展函数  挂起恢复的形式继续往下开发
 * @Since 2021/6/12
 */


suspend fun UriRequest.await(): UriRequest {
    return suspendCancellableCoroutine {
        onComplete(object : OnCompleteListener {
            override fun onSuccess(request: UriRequest) {
                it.resume(request)
            }

            override fun onError(request: UriRequest, resultCode: Int) {
                it.resumeWithException(RouteException("url:${request.errorMessage} code:$resultCode"))
            }

        })
        start()
    }
}

suspend fun UriRequest.awaitDispatcher(context: CoroutineContext = Dispatchers.Main): UriRequest {
    return withContext(context) {
        await()
    }
}