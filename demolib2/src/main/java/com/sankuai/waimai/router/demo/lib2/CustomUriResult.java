package com.sankuai.waimai.router.demo.lib2;

/**
 * 自定义的UriResultCode，为了避免冲突，自定义的建议用负数值
 *
 * Created by jzj on 2018/3/27.
 */
public interface CustomUriResult {

    int CODE_LOGIN_CANCEL = -100;
    int CODE_LOGIN_FAILURE = -101;

    int CODE_LOCATION_FAILURE = -200;
}
