package com.sankuai.waimai.router.demo.advanced.webview;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sankuai.waimai.router.annotation.RouterRegex;
import com.sankuai.waimai.router.common.UriParamInterceptor;
import com.sankuai.waimai.router.demo.R;
import com.sankuai.waimai.router.demo.lib2.BaseActivity;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

/**
 * WebView页面，显示网页
 *
 * Created by jzj on 2018/3/19.
 */
@RouterRegex(regex = DemoConstant.INNER_URL_REGEX, exported = true, priority = 1,
        interceptors = {CommonParamInterceptor.class, UriParamInterceptor.class})
public class WebViewActivity extends BaseActivity {

    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.progressBar);

        mWebView.setWebViewClient(new DemoWebViewClient());
        mWebView.setWebChromeClient(new DemoChromeClient());

        Intent intent = getIntent();
        String url = intent.getDataString();
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }

        mWebView.loadUrl(url);
    }

    class DemoWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(WebViewActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    class DemoChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }
}
