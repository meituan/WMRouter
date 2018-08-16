package com.sankuai.waimai.router.demo.advanced.services;

import android.support.v4.app.Fragment;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

/**
 * Created by jzj on 2018/4/19.
 */
@RouterService(interfaces = Fragment.class, key = DemoConstant.TEST_FRAGMENT)
public class TestFragment extends Fragment {

}
