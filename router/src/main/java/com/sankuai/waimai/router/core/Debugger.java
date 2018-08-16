package com.sankuai.waimai.router.core;

import android.support.annotation.Nullable;

import com.sankuai.waimai.router.interfaces.Const;

/**
 * Created by jzj on 2018/1/29.
 */
public class Debugger {

    public interface Logger {

        void d(String msg, Object... args);

        void i(String msg, Object... args);

        void w(String msg, Object... args);

        void w(Throwable t);

        void e(String msg, Object... args);

        void e(Throwable t);

        void fatal(String msg, Object... args);

        void fatal(Throwable t);
    }

    /**
     * 输出调试信息的Tag
     */
    public static final String LOG_TAG = Const.NAME;

    @Nullable
    private static Logger sLogger = null;

    public static void setLogger(Logger logger) {
        sLogger = logger;
    }

    private static boolean sEnableDebug = false;

    private static boolean sEnableLog = false;

    public static void setEnableDebug(boolean enableDebug) {
        sEnableDebug = enableDebug;
    }

    public static boolean isEnableDebug() {
        return sEnableDebug;
    }

    public static void setEnableLog(boolean enableLog) {
        sEnableLog = enableLog;
    }

    public static boolean isEnableLog() {
        return sEnableLog;
    }

    public static void d(String msg, Object... args) {
        if (sLogger != null) {
            sLogger.d(msg, args);
        }
    }

    public static void i(String msg, Object... args) {
        if (sLogger != null) {
            sLogger.i(msg, args);
        }
    }

    public static void w(String msg, Object... args) {
        if (sLogger != null) {
            sLogger.w(msg, args);
        }
    }

    public static void w(Throwable t) {
        if (sLogger != null) {
            sLogger.w(t);
        }
    }

    public static void e(String msg, Object... args) {
        if (sLogger != null) {
            sLogger.e(msg, args);
        }
    }

    public static void e(Throwable t) {
        if (sLogger != null) {
            sLogger.e(t);
        }
    }

    public static void fatal(String msg, Object... args) {
        if (sLogger != null) {
            sLogger.fatal(msg, args);
        }
    }

    public static void fatal(Throwable t) {
        if (sLogger != null) {
            sLogger.fatal(t);
        }
    }
}
