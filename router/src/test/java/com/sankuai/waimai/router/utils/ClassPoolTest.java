package com.sankuai.waimai.router.utils;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.core.Debugger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ClassPoolTest {

    @Before
    public void setUp() throws Exception {
        Debugger.setLogger(new JavaLogger());
    }

    @Test
    public void get() {
        Class clazz = ClassPool.get(Router.class.getName());
        Assert.assertSame(Router.class, clazz);
    }

    @Test
    public void notFound() {
        Class clazz = ClassPool.get("xxx");
        Assert.assertSame(null, clazz);
    }

    @Test
    public void cache() {
        ClassPool.get(ClassPool.class.getName());
        ClassPool.get(ClassPool.class.getName());
        ClassPool.get(ClassPool.class.getName());
        ClassPool.get("yyy");
        ClassPool.get("yyy");
        ClassPool.get("yyy");
    }
}