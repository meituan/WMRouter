package com.sankuai.waimai.router.regex;

import com.sankuai.waimai.router.components.AnnotationInit;

/**
 * Created by jzj on 2018/3/29.
 */

public interface IRegexAnnotationInit extends AnnotationInit<RegexAnnotationHandler> {

    @Override
    void init(RegexAnnotationHandler handler);
}
