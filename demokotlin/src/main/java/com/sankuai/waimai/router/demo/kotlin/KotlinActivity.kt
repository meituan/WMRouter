package com.sankuai.waimai.router.demo.kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import com.sankuai.waimai.router.Router
import com.sankuai.waimai.router.annotation.RouterUri
import com.sankuai.waimai.router.demo.kotlin.databinding.KotlinActivityDemoBinding
import com.sankuai.waimai.router.demo.lib2.BaseActivity
import com.sankuai.waimai.router.demo.lib2.DemoConstant

@RouterUri(path = [DemoConstant.KOTLIN])
class KotlinActivity : BaseActivity() {

    private val testModel: TestViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewbinding = KotlinActivityDemoBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)
        val a = Router.callMethod(DemoConstant.ADD_METHOD, 2, 3) as Int
        viewbinding.text1.text = "Kotlin模块调用Java模块的Service：\n2 + 3 = $a"
        viewbinding.btnJump.setOnClickListener {
            testModel.route(it.context)
        }
    }
}