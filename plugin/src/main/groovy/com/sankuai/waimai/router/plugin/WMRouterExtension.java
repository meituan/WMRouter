package com.sankuai.waimai.router.plugin;

/**
 * Created by jzj on 2018/4/23.
 */

public class WMRouterExtension {

    /**
     * 可通过ext设置是否使用Transform。
     * <pre>
     * project.ext.wm_router_transform = true
     * apply plugin: 'WMRouter'
     * </pre>
     */
    public static final String USE_TRANSFORM = "wm_router_transform";

    /**
     * 启用插件
     */
    private boolean enable = true;
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

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getEnable() {
        return enable;
    }

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
