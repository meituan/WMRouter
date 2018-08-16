package com.sankuai.waimai.router.demo.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sankuai.waimai.router.common.DefaultUriRequest;
import com.sankuai.waimai.router.core.OnCompleteListener;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.demo.lib2.BaseActivity;

/**
 * 接收所有外部跳转的ProxyActivity
 * <p>
 * Created by jzj on 2018/4/9.
 */

public class UriProxyActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultUriRequest.startFromProxyActivity(this, new OnCompleteListener() {
            @Override
            public void onSuccess(@NonNull UriRequest request) {
                finish();
            }

            @Override
            public void onError(@NonNull UriRequest request, int resultCode) {
                finish();
            }
        });
    }
}
