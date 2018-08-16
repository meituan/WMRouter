package com.sankuai.waimai.router.method;

public interface Func2<T1, T2, R> extends Function {
    R call(T1 t1, T2 t2);
}
