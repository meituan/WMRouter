package com.sankuai.waimai.router.demo.advanced.services;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.method.Func2;

/**
 * Created by jzj on 2018/4/16.
 */
@RouterService(interfaces = Func2.class, key = DemoConstant.ADD_METHOD, singleton = true)
public class AddMethod implements Func2<Integer, Integer, Integer> {

    @Override
    public Integer call(Integer a, Integer b) {
        return a + b;
    }
}
