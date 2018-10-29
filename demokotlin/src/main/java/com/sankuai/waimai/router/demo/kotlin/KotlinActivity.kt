package com.sankuai.waimai.router.demo.kotlin

import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import com.sankuai.waimai.router.demo.lib2.BaseActivity
import com.sankuai.waimai.router.demo.lib2.DemoConstant

@RouterUri(path = [DemoConstant.KOTLIN])
class KotlinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}