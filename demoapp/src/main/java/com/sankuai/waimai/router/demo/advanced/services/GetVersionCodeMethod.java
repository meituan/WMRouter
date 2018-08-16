package com.sankuai.waimai.router.demo.advanced.services;

import android.content.Context;
import android.content.pm.PackageManager;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sankuai.waimai.router.demo.app.DemoApplication;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.method.Func0;

/**
 * Created by jzj on 2018/4/19.
 */
@RouterService(interfaces = Func0.class, key = DemoConstant.GET_VERSION_CODE, singleton = true)
public class GetVersionCodeMethod implements Func0<Integer> {

    @Override
    public Integer call() {
        try {
            Context context = DemoApplication.getContext();
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }
}
