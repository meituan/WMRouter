package com.sankuai.waimai.router.demo.testannotation;

import com.sankuai.waimai.router.annotation.RouterService;

/**
 * Created by jzj on 2018/3/29.
 */

public class TestServiceAnnotation {

    public interface IService {

    }

    @RouterService(interfaces = IService.class)
    public static class ServiceImpl1 implements IService {

    }

    @RouterService(interfaces = IService.class)
    public static class ServiceImpl2 implements IService {

    }

    @RouterService(interfaces = Object.class, key = "/service/test_annotation_1")
    public static class TestPathService1 {

    }

    @RouterService(interfaces = Object.class, key = "/service/test_annotation_2")
    public static class TestPathService2 {

    }
}
