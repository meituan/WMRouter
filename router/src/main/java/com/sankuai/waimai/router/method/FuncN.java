package com.sankuai.waimai.router.method;

public interface FuncN<R> extends Function {
    R call(Object... args);
}
