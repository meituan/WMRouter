package com.sankuai.waimai.router.demo.testannotation;

import com.sankuai.waimai.router.annotation.RouterPage;
import com.sankuai.waimai.router.common.UriParamInterceptor;
import com.sankuai.waimai.router.core.ChainedInterceptor;
import com.sankuai.waimai.router.core.UriHandler;

/**
 * 测试Page注解生成器
 *
 * Created by jzj on 2018/3/23.
 */
public class TestPageAnnotation {

    @RouterPage(path = "/test/handler")
    public static class TestHandler extends EmptyHandler {

    }

    @RouterPage(path = "/test/interceptor", interceptors = UriParamInterceptor.class)
    public static class TestInterceptorHandler extends EmptyHandler {

    }

    @RouterPage(path = "/test/interceptors", interceptors = {UriParamInterceptor.class, ChainedInterceptor.class})
    public static class TestInterceptorsHandler extends EmptyHandler {

    }

    @RouterPage(path = "/test/abstract")
    public static abstract class AbstractHandler extends UriHandler {

    }

    @RouterPage(path = "/test/class")
    public static class NormalClass {

    }
}
