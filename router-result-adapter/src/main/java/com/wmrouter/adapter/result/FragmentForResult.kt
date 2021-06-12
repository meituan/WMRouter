package com.wmrouter.adapter.result

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.properties.Delegates

/**
 * @Author LiABao
 * 通过给当前页面注册一个空fragment接受回调
 * @Since 2021/6/11
 */
class FragmentForResult : Fragment() {

    var onSuccess: () -> Unit = {}
    var onFail: () -> Unit = {}
    var clazz: Class<out Any>? = null
    var mIntent: Intent? = null

    var code: Int = REQUEST_CODE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = if (mIntent == null) {
            Intent()
        } else {
            mIntent
        }
        clazz?.let { intent?.setClass(requireContext(), it) }
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code) {
            if (resultCode == Activity.RESULT_OK) {
                onSuccess.invoke()
            } else {
                onFail.invoke()
            }
            removeFragment()
        }
    }

    private fun removeFragment() {
        try {
            childFragmentManager.beginTransaction().apply {
                remove(this@FragmentForResult)
            }.commitNowAllowingStateLoss()
        } catch (e: Exception) {

        }
    }
}


fun AppCompatActivity.startForResult(
    code: Int = REQUEST_CODE, intent: Intent,
    onSuccess: () -> Unit = {},
    onFail: () -> Unit = {}
) {
    val fragment = FragmentForResult()
    fragment.onSuccess = onSuccess
    fragment.onFail = onFail
    fragment.mIntent = intent
    fragment.code = code
    supportFragmentManager.beginTransaction().apply {
        if (fragment.isAdded) {
            remove(fragment)
        }
        add(fragment, fragment.toString())
    }.commitNowAllowingStateLoss()
}

const val REQUEST_CODE = 1024