package com.sankuai.waimai.router.exception;

/**
 * Created by zhuoxiuwu
 * on 2019/4/29
 * email nimdanoob@gmail.com
 */
public class DefaultServiceException extends RuntimeException{

    public DefaultServiceException(String msg){
        super(msg);
    }

    public static DefaultServiceException foundMoreThanOneImpl(Class service) {
        return new DefaultServiceException("因为" + service.getCanonicalName() + "有多个实现类,Router无法决定默认使用哪个来构造实例;"
                + "你可以通过@RouterService的defaultImpl参数设置一个默认的实现类");
    }
}
