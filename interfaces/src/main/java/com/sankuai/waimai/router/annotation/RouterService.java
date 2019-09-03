package com.sankuai.waimai.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个Service，通过interface和key加载实现类。此注解可以用在任意静态类上。
 *
 * Created by jzj on 2018/3/29.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RouterService {

    /**
     * 实现的接口（或继承的父类）
     */
    Class[] interfaces();

    /**
     * 同一个接口的多个实现类之间，可以通过唯一的key区分。
     */
    String[] key() default {};

    /**
     * 是否为单例。如果是单例，则使用ServiceLoader.getService不会重复创建实例。
     */
    boolean singleton() default false;

    /**
     * 是否设置为默认实现类。如果是默认实现类，则在获取该实现类实例时可以不指定key
     * @return
     */
    boolean defaultImpl() default false;
}
