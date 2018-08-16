package com.sankuai.waimai.router.demo.lib2.advanced.services;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

/**
 * Created by jzj on 2018/4/19.
 */

public class DemoServiceManager {

    public static IAccountService getAccountService() {
        return Router.getService(IAccountService.class, DemoConstant.SINGLETON);
    }

    public static ILocationService getLocationService() {
        return Router.getService(ILocationService.class, DemoConstant.SINGLETON);
    }
}
