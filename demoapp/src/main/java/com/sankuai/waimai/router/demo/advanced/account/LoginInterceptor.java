package com.sankuai.waimai.router.demo.advanced.account;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.sankuai.waimai.router.core.UriCallback;
import com.sankuai.waimai.router.core.UriInterceptor;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.core.UriResult;
import com.sankuai.waimai.router.demo.lib2.CustomUriResult;
import com.sankuai.waimai.router.demo.lib2.advanced.services.DemoServiceManager;
import com.sankuai.waimai.router.demo.lib2.advanced.services.IAccountService;

/**
 * Created by jzj on 2018/3/20.
 */

public class LoginInterceptor implements UriInterceptor {

    @Override
    public void intercept(@NonNull UriRequest request, @NonNull final UriCallback callback) {
        final IAccountService accountService = DemoServiceManager.getAccountService();
        if (accountService.isLogin()) {
            callback.onNext();
        } else {
            Toast.makeText(request.getContext(), "请先登录~", Toast.LENGTH_SHORT).show();
            accountService.registerObserver(new IAccountService.Observer() {
                @Override
                public void onLoginSuccess() {
                    accountService.unregisterObserver(this);
                    callback.onNext();
                }

                @Override
                public void onLoginCancel() {
                    accountService.unregisterObserver(this);
                    callback.onComplete(CustomUriResult.CODE_LOGIN_CANCEL);
                }

                @Override
                public void onLoginFailure() {
                    accountService.unregisterObserver(this);
                    callback.onComplete(CustomUriResult.CODE_LOGIN_FAILURE);
                }

                @Override
                public void onLogout() {
                    accountService.unregisterObserver(this);
                    callback.onComplete(UriResult.CODE_ERROR);
                }
            });
            DemoServiceManager.getAccountService().startLogin(request.getContext());
        }
    }
}
