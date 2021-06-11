package com.wmrouter.adapter.rxjava3

import com.sankuai.waimai.router.core.OnCompleteListener
import com.sankuai.waimai.router.core.UriRequest
import com.sankuai.waimai.router.exception.RouteException
import io.reactivex.rxjava3.core.*

/**
 *
 *  @Author LiABao
 *  RXJAVA 支持  建议配合AutoDispose使用
 *  @Since 2021/6/11
 *
 */

// 用的最多的Observable
fun UriRequest.requestObservable(): Observable<UriRequest> {
    return Observable.create<UriRequest> {
        onComplete(object : OnCompleteListener {

            override fun onSuccess(request: UriRequest) {
                it.onNext(request)
                it.onComplete()
            }

            override fun onError(request: UriRequest, resultCode: Int) {
                it.onError(RouteException("url:${request.errorMessage} code:$resultCode"))
            }

        }).start()
    }
}


// 用的比较少的Maybe
fun UriRequest.requestMaybe(): Maybe<UriRequest> {
    return Maybe.create<UriRequest> {
        onComplete(object : OnCompleteListener {

            override fun onSuccess(request: UriRequest) {
                it.onSuccess(request)
                it.onComplete()
            }

            override fun onError(request: UriRequest, resultCode: Int) {
                it.onError(RouteException("url:${request.errorMessage} code:$resultCode"))
            }

        }).start()
    }
}


// 面试爱问的背压 Flowable
fun UriRequest.requestFlowable(backStrategy: BackpressureStrategy = BackpressureStrategy.DROP):
        Flowable<UriRequest> {
    return Flowable.create<UriRequest>({
        onComplete(object : OnCompleteListener {

            override fun onSuccess(request: UriRequest) {
                it.onNext(request)
                it.onComplete()
            }

            override fun onError(request: UriRequest, resultCode: Int) {
                it.onError(RouteException("url:${request.errorMessage} code:$resultCode"))
            }

        }).start()
    }, backStrategy)
}


// 比较合理的Single
fun UriRequest.requestSingle(): Single<UriRequest> {
    return Single.create<UriRequest> {
        onComplete(object : OnCompleteListener {

            override fun onSuccess(request: UriRequest) {
                it.onSuccess(request)
            }

            override fun onError(request: UriRequest, resultCode: Int) {
                it.onError(RouteException("url:${request.errorMessage} code:$resultCode"))
            }
        }).start()
    }
}


// 不关心结果的Complete
fun UriRequest.requestComplete(): Completable {
    return Completable.create {
        onComplete(object : OnCompleteListener {

            override fun onSuccess(request: UriRequest) {
                it.onComplete()
            }

            override fun onError(request: UriRequest, resultCode: Int) {
                it.onError(RouteException("url:${request.errorMessage} code:$resultCode"))
            }
        }).start()
    }
}