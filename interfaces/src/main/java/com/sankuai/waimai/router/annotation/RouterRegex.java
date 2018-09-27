package com.sankuai.waimai.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定一个正则匹配的跳转，此注解可以用在Activity和UriHandler上
 *
 * Created by jzj on 2018/3/19.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RouterRegex {

    /**
     * 完整uri的正则表达式匹配
     */
    String regex();

    /**
     * 优先级，数字越大越先执行，默认为0
     */
    int priority() default 0;

    /**
     * 是否允许外部跳转
     */
    boolean exported() default false;

    /**
     * 要添加的interceptors
     */
    Class[] interceptors() default {};
}
