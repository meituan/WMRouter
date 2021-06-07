package com.sankuai.waimai.router.plugin;

/**
 * Created by jzj on 2018/4/23.
 */

public class WMRouterExtension {

    /**
     * 出错时中断编译
     */
    private boolean abortOnError = true;
    /**
     * 是否允许Log
     */
    private boolean enableLog = true;
    /**
     * 是否开启Debug。Debug模式会输出更详细的Log。
     */
    private boolean enableDebug = false;

    public void setAbortOnError(boolean abortOnError) {
        this.abortOnError = abortOnError;
    }

    public boolean getAbortOnError() {
        return abortOnError;
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

    public boolean getEnableLog() {
        return enableLog;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public boolean getEnableDebug() {
        return enableDebug;
    }
}
