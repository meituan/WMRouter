package com.sankuai.waimai.router.demo.advanced.services;

import android.content.Context;
import androidx.annotation.NonNull;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.demo.lib2.advanced.services.IAccountService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzj on 2018/3/26.
 */
@RouterService(interfaces = IAccountService.class, key = DemoConstant.SINGLETON, singleton = true)
public class FakeAccountService implements IAccountService {

    @RouterProvider
    public static FakeAccountService getInstance() {
        return new FakeAccountService(Router.getService(Context.class, "/application"));
    }

    private boolean mIsLogin = false;
    private final List<Observer> mObservers = new ArrayList<>();

    private FakeAccountService(Context context) {
        // ...
    }

    @Override
    public void startLogin(Context context) {
        Router.startUri(context, DemoConstant.LOGIN);
    }

    @Override
    public boolean isLogin() {
        return mIsLogin;
    }

    @Override
    public void registerObserver(Observer observer) {
        if (observer != null && !mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(Observer observer) {
        if (observer != null) {
            mObservers.remove(observer);
        }
    }

    @Override
    public void notifyLoginSuccess() {
        mIsLogin = true;
        Observer[] observers = getObservers();
        for (int i = observers.length - 1; i >= 0; --i) {
            observers[i].onLoginSuccess();
        }
    }

    @Override
    public void notifyLoginCancel() {
        Observer[] observers = getObservers();
        for (int i = observers.length - 1; i >= 0; --i) {
            observers[i].onLoginCancel();
        }
    }

    @Override
    public void notifyLoginFailure() {
        Observer[] observers = getObservers();
        for (int i = observers.length - 1; i >= 0; --i) {
            observers[i].onLoginFailure();
        }
    }

    @Override
    public void notifyLogout() {
        mIsLogin = false;
        Observer[] observers = getObservers();
        for (int i = observers.length - 1; i >= 0; --i) {
            observers[i].onLogout();
        }
    }

    @NonNull
    private Observer[] getObservers() {
        return mObservers.toArray(new Observer[mObservers.size()]);
    }
}
