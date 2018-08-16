package com.sankuai.waimai.router.utils;

import android.util.Log;

import com.sankuai.waimai.router.core.Debugger;

public class JavaLogger implements Debugger.Logger {

    private void log(char level, String msg) {
        msg = String.format("{ %s } %s", level, msg);
        switch (level) {
            case 'd':
            case 'i':
            case 'w':
                System.out.println(msg);
                break;
            case 'e':
            case 'f':
                System.err.println(msg);
                break;
        }
    }

    @Override
    public void d(String msg, Object... args) {
        log('d', String.format(msg, args));
    }

    @Override
    public void i(String msg, Object... args) {
        log('i', String.format(msg, args));
    }

    @Override
    public void w(String msg, Object... args) {
        log('w', String.format(msg, args));
    }

    @Override
    public void w(Throwable t) {
        log('w', Log.getStackTraceString(t));
    }

    @Override
    public void e(String msg, Object... args) {
        log('e', String.format(msg, args));
    }

    @Override
    public void e(Throwable t) {
        log('e', Log.getStackTraceString(t));
    }

    @Override
    public void fatal(String msg, Object... args) {
        log('f', String.format(msg, args));
    }

    @Override
    public void fatal(Throwable t) {
        log('f', Log.getStackTraceString(t));
    }
}