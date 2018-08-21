package com.sankuai.waimai.router.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.components.RouterComponents;
import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.generated.ServiceLoaderInit;
import com.sankuai.waimai.router.interfaces.Const;
import com.sankuai.waimai.router.utils.ClassPool;
import com.sankuai.waimai.router.utils.LazyInitHelper;
import com.sankuai.waimai.router.utils.SingletonPool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过接口Class获取实现类
 *
 * Created by jzj on 2018/3/29.
 *
 * @param <I> 接口类型
 */
public class ServiceLoader<I> {

    private static final Map<String, ServiceLoader> SERVICES = new HashMap<>();

    private static final LazyInitHelper sInitHelper = new LazyInitHelper("ServiceLoader") {
        @Override
        protected void doInit() {
            try {
                ServiceLoaderInit.init();
//                Class.forName(Const.SERVICE_LOADER_INIT)
//                        .getMethod(Const.INIT_METHOD)
//                        .invoke(null);
                Debugger.i("[ServiceLoader] init class invoked");
            } catch (NoClassDefFoundError e) {
                Debugger.w("[ServiceLoader] init class not found");
            } catch (Exception e) {
                Debugger.fatal(e);
            }
        }
    };

    /**
     * @see LazyInitHelper#lazyInit()
     */
    public static void lazyInit() {
        sInitHelper.lazyInit();
    }

    public static void put(String interfaceName, String key, String implementName, boolean singleton) {
        ServiceLoader loader = SERVICES.get(interfaceName);
        if (loader == null) {
            loader = new ServiceLoader(interfaceName);
            SERVICES.put(interfaceName, loader);
        }
        loader.putImpl(key, implementName, singleton);
    }

    /**
     * 根据接口获取 {@link ServiceLoader}
     */
    @SuppressWarnings("unchecked")
    public static <T> ServiceLoader<T> load(Class<T> interfaceClass) {
        sInitHelper.ensureInit();
        if (interfaceClass == null) {
            Debugger.fatal(new NullPointerException("ServiceLoader.load的class参数不应为空"));
            return EmptyServiceLoader.INSTANCE;
        }
        String interfaceName = interfaceClass.getName();
        ServiceLoader service = SERVICES.get(interfaceName);
        if (service == null) {
            synchronized (SERVICES) {
                service = SERVICES.get(interfaceName);
                if (service == null) {
                    service = new ServiceLoader(interfaceName);
                    service.loadData();
                    SERVICES.put(interfaceName, service);
                }
            }
        }
        return service;
    }

    /**
     * key --> class name
     */
    private HashMap<String, ServiceImpl> mMap = new HashMap<>();

    private final String mInterfaceName;

    private ServiceLoader(String interfaceName) {
        if (interfaceName == null) {
            mInterfaceName = "";
        } else {
            mInterfaceName = interfaceName;
        }
    }

    private void putImpl(String key, String implementName, boolean singleton) {
        if (key != null && implementName != null) {
            mMap.put(key, new ServiceImpl(key, implementName, singleton));
        }
    }

    /**
     * 创建指定key的实现类实例，使用 {@link RouterProvider} 方法或无参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key) {
        return createInstance(mMap.get(key), null);
    }

    /**
     * 创建指定key的实现类实例，使用Context参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key, Context context) {
        return createInstance(mMap.get(key), new ContextFactory(context));
    }

    /**
     * 创建指定key的实现类实例，使用指定的Factory构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回null
     */
    public <T extends I> T get(String key, IFactory factory) {
        return createInstance(mMap.get(key), factory);
    }

    /**
     * 创建所有实现类的实例，使用 {@link RouterProvider} 方法或无参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @NonNull
    public <T extends I> List<T> getAll() {
        return getAll((IFactory) null);
    }

    /**
     * 创建所有实现类的实例，使用Context参数构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @NonNull
    public <T extends I> List<T> getAll(Context context) {
        return getAll(new ContextFactory(context));
    }

    /**
     * 创建所有实现类的实例，使用指定Factory构造。对于声明了singleton的实现类，不会重复创建实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @NonNull
    public <T extends I> List<T> getAll(IFactory factory) {
        Collection<ServiceImpl> services = mMap.values();
        if (services.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(services.size());
        for (ServiceImpl impl : services) {
            T instance = createInstance(impl, factory);
            if (instance != null) {
                list.add(instance);
            }
        }
        return list;
    }

    /**
     * 获取指定key的实现类。注意，对于声明了singleton的实现类，获取Class后还是可以创建新的实例。
     *
     * @return 可能返回null
     */
    public <T extends I> Class<T> getClass(String key) {
        return ClassPool.get(mMap.get(key));
    }

    /**
     * 获取所有实现类的Class。注意，对于声明了singleton的实现类，获取Class后还是可以创建新的实例。
     *
     * @return 可能返回EmptyList，List中的元素不为空
     */
    @NonNull
    public <T extends I> List<Class<T>> getAllClasses() {
        List<Class<T>> list = new ArrayList<>(mMap.size());
        for (ServiceImpl impl : mMap.values()) {
            Class<T> clazz = ClassPool.get(impl);
            if (clazz != null) {
                list.add(clazz);
            }
        }
        return list;
    }

    private void loadData() {
        InputStream is = null;
        BufferedReader reader = null;
        try {
            try {
                is = Router.getRootHandler().getContext().getAssets()
                        .open(Const.ASSETS_PATH + mInterfaceName);
            } catch (FileNotFoundException e) {
                Debugger.w("assets file for interface '%s' not found", mInterfaceName);
            }
            if (is == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(is));
            String ln;
            while ((ln = reader.readLine()) != null) {
                ServiceImpl impl = ServiceImpl.fromConfig(ln);
                if (impl != null) {
                    ServiceImpl prev = mMap.put(impl.getKey(), impl);
                    String errorMsg = ServiceImpl.checkConflict(mInterfaceName, prev, impl);
                    if (errorMsg != null) {
                        Debugger.fatal(errorMsg);
                    }
                }
            }
        } catch (IOException e) {
            Debugger.fatal(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Debugger.w(e);
            }
        }
    }

    @Nullable
    private <T extends I> T createInstance(@Nullable ServiceImpl impl, @Nullable IFactory factory) {
        if (impl == null) {
            return null;
        }
        Class<T> clazz = ClassPool.get(impl);
        if (impl.isSingleton()) {
            try {
                return SingletonPool.get(clazz, factory);
            } catch (Exception e) {
                Debugger.fatal(e);
            }
        } else {
            try {
                if (factory == null) {
                    factory = RouterComponents.getDefaultFactory();
                }
                T t = factory.create(clazz);
                Debugger.i("[ServiceLoader] create instance: %s, result = %s", clazz, t);
                return t;
            } catch (Exception e) {
                Debugger.fatal(e);
            }
        }
        return null;
    }

    public static class EmptyServiceLoader extends ServiceLoader {

        public static final ServiceLoader INSTANCE = new EmptyServiceLoader();

        public EmptyServiceLoader() {
            super(null);
        }

        @NonNull
        @Override
        public List<Class> getAllClasses() {
            return Collections.emptyList();
        }

        @NonNull
        @Override
        public List getAll() {
            return Collections.emptyList();
        }

        @NonNull
        @Override
        public List getAll(IFactory factory) {
            return Collections.emptyList();
        }
    }
}
