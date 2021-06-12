package com.sankuai.waimai.router.demo.kotlin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sankuai.waimai.router.common.DefaultUriRequest
import com.sankuai.waimai.router.demo.lib2.ToastUtils
import com.wmrouter.adapter.coroutines.await
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 *
 *  @Author LiABao
 *  @Since 2021/6/12
 *
 */
class TestViewModel : ViewModel() {
    private val handler = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
        ToastUtils.showToast("跳转失败")
    }

    fun route(context: Context) {
        viewModelScope.launch(handler) {
            val response = DefaultUriRequest(context, JUMP_WITH_REQUEST)
                .activityRequestCode(100)
                .putExtra(INTENT_TEST_INT, 1)
                .putExtra(INTENT_TEST_STR, "str").await()
            ToastUtils.showToast(response.context, "跳转成功")
        }
    }

    companion object {

        const val JUMP_WITH_REQUEST = "/jump_with_request"

        const val INTENT_TEST_INT = "test_int"
        const val INTENT_TEST_STR = "test_str"
    }

}