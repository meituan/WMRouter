package com.sankuai.waimai.router.service;

/**
 * Service的一个实现
 *
 * Created by jzj on 2018/4/19.
 */
public class ServiceImpl {

    public static final String SPLITTER = ":";
    public static final String SINGLETON = "singleton";

    public static ServiceImpl fromConfig(String ln) {
        if (isEmpty(ln)) {
            return null;
        }

        // 格式 [ content ] # comment

        // 去掉注释
        int ci = ln.indexOf('#');
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }

        // 去掉空格
        ln = ln.trim();

        if (ln.length() == 0) {
            return null;
        }

        // 格式
        // key : implementation : singleton
        // 或
        // implementation

        String[] split = ln.split(SPLITTER);
        if (split.length == 0) {
            return null;
        }

        String key, implementation;
        boolean singleton;
        if (split.length == 1) {
            key = implementation = trim(split[0]);
            singleton = false;
        } else {
            key = trim(split[0]);
            implementation = trim(split[1]);
            singleton = split.length >= 3 && SINGLETON.equalsIgnoreCase(trim(split[2]));
        }
        if (isEmpty(key) || !isClassName(implementation)) {
            return null;
        }
        return new ServiceImpl(key, implementation, singleton);
    }

    public static String checkConflict(String interfaceName, ServiceImpl impl,
            ServiceImpl previous) {
        if (impl != null && previous != null && !stringEquals(previous.implementation,
                impl.implementation)) {
            return String.format("接口%s对应key='%s'存在多个实现: %s, %s",
                    interfaceName, impl.getKey(), previous, impl);
        }
        return null;
    }

    @SuppressWarnings("StringEquality")
    private static boolean stringEquals(String s1, String s2) {
        return s1 == s2 || s1 != null && s1.equals(s2);
    }

    private static boolean isEmpty(String key) {
        return key == null || key.length() == 0;
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    /**
     * 是否为合法的className。考虑到配置文件一般都是注解生成器输出，这里只做简单判断。
     */
    private static boolean isClassName(String s) {
        return !isEmpty(s);
    }

    private final String key;
    private final String implementation;
    private final Class implementationClazz;
    private final boolean singleton;

    public ServiceImpl(String key, Class implementation, boolean singleton) {
        if (key == null || implementation == null) {
            throw new RuntimeException("key和implementation不应该为空");
        }
        this.key = key;
        this.implementation = "";
        this.implementationClazz = implementation;
        this.singleton = singleton;
    }

    public ServiceImpl(String key, String implementation, boolean singleton) {
        if (isEmpty(implementation)) {
            throw new RuntimeException("implementation不应该为空");
        }
        this.key = isEmpty(key) ? implementation : key; // 没有指定key，则为implementation
        this.implementation = implementation;
        this.implementationClazz = null;
        this.singleton = singleton;
    }

    public String toConfig() {
        String s = key + SPLITTER + implementation;
        if (singleton) {
            s += SPLITTER + SINGLETON;
        }
        return s;
    }

    /**
     * not null
     */
    public String getKey() {
        return key;
    }

    /**
     * nullable
     */
    public String getImplementation() {
        return implementation;
    }

    /**
     * nullable
     */
    public Class getImplementationClazz() {
        return implementationClazz;
    }

    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public String toString() {
        return implementation;
    }
}
