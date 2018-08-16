package com.sankuai.waimai.router.utils;

import android.support.annotation.NonNull;

import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.service.IFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SingletonPoolTest {

    public static abstract class Base {

        private final String mName;

        public Base(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    @Before
    public void setUp() throws Exception {
        Debugger.setLogger(new JavaLogger());
    }

    public static class TestSingleton extends Base {

        public TestSingleton() {
            super("");
        }
    }

    @Test
    public void get() throws Exception {
        TestSingleton o1 = SingletonPool.get(TestSingleton.class, null);
        Assert.assertNotNull(o1);

        TestSingleton o2 = SingletonPool.get(TestSingleton.class, null);
        Assert.assertSame(o1, o2);
    }

    public static class TestProvider extends Base {

        @RouterProvider
        public static TestProvider provideInstance() {
            return new TestProvider("provider");
        }

        public TestProvider() {
            super("default");
        }

        public TestProvider(String name) {
            super(name);
        }
    }

    @Test
    public void provider() throws Exception {
        TestProvider p = SingletonPool.get(TestProvider.class, null);
        Assert.assertNotNull(p);
        Assert.assertEquals("provider", p.getName());
    }

    public static class TestFactory extends Base {

        public TestFactory() {
            super("default");
        }

        public TestFactory(String name) {
            super(name);
        }
    }

    @Test
    public void factory() throws Exception {
        TestFactory o = SingletonPool.get(TestFactory.class, new IFactory() {
            @NonNull
            @Override
            public <T> T create(@NonNull Class<T> clazz) throws Exception {
                return clazz.getConstructor(String.class).newInstance("factory");
            }
        });
        Assert.assertNotNull(o);
        Assert.assertEquals("factory", o.getName());
    }
}