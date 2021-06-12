package com.sankuai.waimai.router.demo.basic

import com.sankuai.waimai.router.demo.lib2.DemoConstant

/**
 *
 *  @Author LiABao
 *  @Since 2021/6/11
 *
 */
object Constant {
    val URIS = arrayOf( // 基本页面跳转，支持不配置Scheme、Host，支持多个path
        DemoConstant.JUMP_ACTIVITY_1,
        DemoConstant.JUMP_ACTIVITY_2,  // Kotlin
        DemoConstant.KOTLIN,  // request跳转测试
        DemoConstant.JUMP_WITH_REQUEST,  // 自定义Scheme、Host测试；外部跳转测试
        DemoConstant.DEMO_SCHEME + "://" + DemoConstant.DEMO_HOST
                + DemoConstant.EXPORTED_PATH,
        (DemoConstant.DEMO_SCHEME + "://" + DemoConstant.DEMO_HOST
                + DemoConstant.NOT_EXPORTED_PATH),  // Library工程测试
        DemoConstant.TEST_LIB1,
        DemoConstant.TEST_LIB2,  // 拨打电话
        DemoConstant.TEL,  // 降级策略
        DemoConstant.TEST_NOT_FOUND,  // Fragment test
        DemoConstant.JUMP_FRAGMENT_ACTIVITY,  // Fragment to Fragment test
        DemoConstant.TEST_FRAGMENT_TO_FRAGMENT_ACTIVITY,  // 高级Demo页面
        DemoConstant.ADVANCED_DEMO
    )
}