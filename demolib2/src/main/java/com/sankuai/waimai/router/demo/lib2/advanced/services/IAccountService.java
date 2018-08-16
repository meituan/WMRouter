package com.sankuai.waimai.router.demo.lib2.advanced.services;

import android.content.Context;

/**
 * Created by jzj on 2018/4/19.
 */

public interface IAccountService {

    boolean isLogin();

    void startLogin(Context context);

    void registerObserver(Observer observer);

    void unregisterObserver(Observer observer);

    void notifyLoginSuccess();

    void notifyLoginCancel();

    void notifyLoginFailure();

    void notifyLogout();

    interface Observer {

        void onLoginSuccess();

        void onLoginCancel();

        void onLoginFailure();

        void onLogout();
    }
}
