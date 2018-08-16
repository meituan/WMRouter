package com.sankuai.waimai.router.demo.advanced.abtest;

public class FakeABTestService {

    public static String getHomeABStrategy() {
        return Math.random() > 0.5 ? "A" : "B";
    }
}
