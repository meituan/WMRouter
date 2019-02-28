package com.sankuai.waimai.router.demo.kotlin

import com.sankuai.waimai.router.annotation.RouterService
import com.sankuai.waimai.router.demo.lib2.DemoConstant

@RouterService(interfaces = [Object::class], key = [DemoConstant.KOTLIN_SERVICE])
class KotlinService {

}