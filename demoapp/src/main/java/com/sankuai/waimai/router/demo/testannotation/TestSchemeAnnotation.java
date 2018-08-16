package com.sankuai.waimai.router.demo.testannotation;

import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.common.UriParamInterceptor;
import com.sankuai.waimai.router.core.ChainedInterceptor;
import com.sankuai.waimai.router.core.UriHandler;

/**
 * 测试Page注解生成器
 *
 * Created by jzj on 2018/3/23.
 */
public class TestSchemeAnnotation {

    @RouterUri(path = "/test/handler")
    public static class TestHandler extends EmptyHandler {

    }

    @RouterUri(path = "/test/schemehost", scheme = "test")
    public static class TestSchemeHostHandler2 extends EmptyHandler {

    }

    @RouterUri(path = "/test/schemehost", scheme = "test", host = "test.demo.com")
    public static class TestSchemeHostHandler extends EmptyHandler {

    }

    @RouterUri(path = "/test/interceptor", interceptors = UriParamInterceptor.class)
    public static class TestInterceptorHandler extends EmptyHandler {

    }

    @RouterUri(path = "/test/interceptors", interceptors = {UriParamInterceptor.class,
            ChainedInterceptor.class})
    public static class TestInterceptorsHandler extends EmptyHandler {

    }

    @RouterUri(path = "/test/abstract")
    public static abstract class AbstractHandler extends UriHandler {

    }

    @RouterUri(path = "/test/class")
    public static class NormalClass {

    }
}
