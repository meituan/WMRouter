package com.sankuai.waimai.router.demo.basic;

import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.demo.lib2.BaseActivity;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

/**
 * Created by jzj on 2018/3/26.
 */
@RouterUri(
        scheme = DemoConstant.DEMO_SCHEME,
        host = DemoConstant.DEMO_HOST,
        path = DemoConstant.EXPORTED_PATH,
        exported = true
)
public class ExportedActivity extends BaseActivity {

}
