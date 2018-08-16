package com.sankuai.waimai.router.common;

import com.sankuai.waimai.router.components.AnnotationInit;

/**
 * Created by jzj on 2018/3/29.
 */

public interface IUriAnnotationInit extends AnnotationInit<UriAnnotationHandler> {

    @Override
    void init(UriAnnotationHandler handler);
}
