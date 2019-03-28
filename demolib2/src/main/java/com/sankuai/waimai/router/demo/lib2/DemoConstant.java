package com.sankuai.waimai.router.demo.lib2;

/**
 * Created by jzj on 2018/4/19.
 */

public class DemoConstant {

    // 页面
    public static final String JUMP_ACTIVITY_1 = "/jump_activity_1";
    public static final String JUMP_ACTIVITY_2 = "/jump_activity_2";
    public static final String JUMP_FRAGMENT_ACTIVITY = "/jump_fragment_activity";


    public static final String KOTLIN = "/kotlin";
    public static final String JUMP_WITH_REQUEST = "/jump_with_request";

    public static final String DEMO_SCHEME = "demo_scheme";
    public static final String DEMO_HOST = "demo_host";
    public static final String EXPORTED_PATH = "/exported";
    public static final String NOT_EXPORTED_PATH = "/not_exported";

    public static final String TEST_LIB1 = "/lib1";
    public static final String TEST_LIB2 = "/lib2";
    public static final String TEST_NOT_FOUND = "/not_found";
    public static final String ADVANCED_DEMO = "/advanced_demo";

    public static final String HOME_AB_TEST = "/home_ab_test";

    public static final String SHOW_TOAST_HANDLER = "/show_toast_handler";

    public static final String TEL = "tel:123456789";

    // regex
    public static final String INNER_URL_REGEX =
            "http(s)?://(.*\\.)?(meituan|sankuai|dianping)\\.(com|info|cn).*";
    public static final String HTTP_URL_REGEX =
            "http(s)?://.*";

    public static final String LOGIN = "/login";
    public static final String BROWSER = "/browser";
    public static final String NEARBY_SHOP_WITH_LOCATION_INTERCEPTOR = "/nearby_shop_with_location";
    public static final String ACCOUNT_WITH_LOGIN_INTERCEPTOR = "/account_with_login";
    public static final String SERVICE_LOADER = "/service_loader";

    // Service
    public static final String TEST_FRAGMENT = "/fragment/test";
    public static final String SINGLETON = "/singleton";
    public static final String KOTLIN_SERVICE = "/kotlin/service";

    // method
    public static final String ADD_METHOD = "/method/add";
    public static final String GET_VERSION_CODE = "/method/get_version_code";

    public static final String TEST_FRAGMENT_TO_FRAGMENT_ACTIVITY = "/jump_fragment_to_fragment_activity";
    public static final String TEST_DEMO_FRAGMENT_1 = "/fragment/demo_fragment_1";
    public static final String TEST_DEMO_FRAGMENT_2 = "/fragment/demo_fragment_2";

}
