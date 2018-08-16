package com.sankuai.waimai.router.demo.testannotation;

import com.sankuai.waimai.router.annotation.RouterRegex;
import com.sankuai.waimai.router.common.UriParamInterceptor;
import com.sankuai.waimai.router.core.ChainedInterceptor;
import com.sankuai.waimai.router.core.UriHandler;

/**
 * 测试Page注解生成器
 *
 * Created by jzj on 2018/3/23.
 */
public class TestRegexAnnotation {

    @RouterRegex(regex = "http(s)://test.demo.com/.*")
    public static class TestHandler extends EmptyHandler {

    }

    @RouterRegex(regex = "http(s)://test.demo.com/test/interceptor.*", interceptors = UriParamInterceptor.class)
    public static class TestInterceptorHandler extends EmptyHandler {

    }

    @RouterRegex(regex = "http(s)://test.demo.com/test/interceptors.*", interceptors = {UriParamInterceptor.class,
            ChainedInterceptor.class})
    public static class TestInterceptorsHandler extends EmptyHandler {

    }

    @RouterRegex(regex = "http(s)://test.demo.com/test/abstract.*")
    public static abstract class AbstractHandler extends UriHandler {

    }

    @RouterRegex(regex = "http(s)://test.demo.com/test/class.*")
    public static class NormalClass {

    }
}
