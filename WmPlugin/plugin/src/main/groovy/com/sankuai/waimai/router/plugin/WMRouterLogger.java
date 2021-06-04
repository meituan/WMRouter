package com.sankuai.waimai.router.plugin;

import com.sankuai.waimai.router.interfaces.Const;

/**
 * Created by jzj on 2018/4/25.
 */

public class WMRouterLogger {

    private static final String PREFIX = "[" + Const.NAME + "] ";

    private static boolean sEnableLog = false;
    private static boolean sEnableDebug = false;
    private static boolean sAbortOnError = false;

    public static void setConfig(WMRouterExtension extension) {
        sEnableDebug = extension.getEnableDebug();
        sEnableLog = extension.getEnableLog() || extension.getEnableDebug();
        sAbortOnError = extension.getAbortOnError();
    }

    public static void debug(String s, Object... args) {
        if (sEnableDebug) {
            System.out.println(format(s, args));
        }
    }

    public static void info(String s, Object... args) {
        if (sEnableLog) {
            System.out.println(format(s, args));
        }
    }

    public static void warn(String s, Object... args) {
        if (sEnableLog) {
            System.err.println(format(s, args));
        }
    }

    public static void error(String s, Object... args) {
        if (sEnableLog) {
            System.err.println(format(s, args));
        }
    }

    public static void error(Throwable t) {
        if (sEnableLog) {
            t.printStackTrace();
        }
    }

    public static void fatal(Throwable t) {
        if (sAbortOnError) {
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new RuntimeException(t);
            }
        } else {
            t.printStackTrace();
        }
    }

    public static void fatal(String s, Object... args) {
        fatal(new RuntimeException(format(s, args)));
    }

    private static String format(String s, Object... args) {
        return PREFIX + (args.length == 0 ? s : String.format(s, args));
    }
}
