package com.sankuai.waimai.router.components;

import android.util.Log;

import com.sankuai.waimai.router.core.Debugger;

/**
 * 默认的Logger实现
 */
public class DefaultLogger implements Debugger.Logger {

    public static final DefaultLogger INSTANCE = new DefaultLogger();

    @Override
    public void d(String msg, Object... args) {
        if (Debugger.isEnableLog()) {
            Log.d(Debugger.LOG_TAG, format(msg, args));
        }
    }

    @Override
    public void i(String msg, Object... args) {
        if (Debugger.isEnableLog()) {
            Log.i(Debugger.LOG_TAG, format(msg, args));
        }
    }

    @Override
    public void w(String msg, Object... args) {
        if (Debugger.isEnableLog()) {
            Log.w(Debugger.LOG_TAG, format(msg, args));
        }
    }

    @Override
    public void w(Throwable t) {
        if (Debugger.isEnableLog()) {
            Log.w(Debugger.LOG_TAG, t);
        }
    }

    @Override
    public void e(String msg, Object... args) {
        if (Debugger.isEnableLog()) {
            Log.e(Debugger.LOG_TAG, format(msg, args));
        }
    }

    @Override
    public void e(Throwable t) {
        if (Debugger.isEnableLog()) {
            Log.e(Debugger.LOG_TAG, "", t);
        }
    }

    @Override
    public void fatal(String msg, Object... args) {
        if (Debugger.isEnableLog()) {
            Log.e(Debugger.LOG_TAG, format(msg, args));
        }
        handleError(new RuntimeException(format(msg, args)));
    }

    @Override
    public void fatal(Throwable t) {
        if (Debugger.isEnableLog()) {
            Log.e(Debugger.LOG_TAG, "", t);
        }
        handleError(t);
    }

    /**
     * 处理fatal级别的错误。默认行为是在调试环境下抛出异常，非调试环境不做处理。
     */
    protected void handleError(Throwable t) {
        if (Debugger.isEnableDebug()) {
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new RuntimeException(t);
            }
        }
    }

    protected String format(String msg, Object... args) {
        if (args != null && args.length > 0) {
            try {
                return String.format(msg, args);
            } catch (Throwable t) {
                e(t);
            }
        }
        return msg;
    }
}
