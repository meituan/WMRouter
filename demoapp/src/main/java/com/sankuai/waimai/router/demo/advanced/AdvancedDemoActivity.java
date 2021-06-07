package com.sankuai.waimai.router.demo.advanced;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.demo.R;
import com.sankuai.waimai.router.demo.lib2.BaseActivity;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

/**
 * Created by jzj on 2018/3/19.
 */
@RouterUri(path = DemoConstant.ADVANCED_DEMO)
public class AdvancedDemoActivity extends BaseActivity {

    public static final String[] URIS = {

            // Interceptor测试
            DemoConstant.ACCOUNT_WITH_LOGIN_INTERCEPTOR,
            DemoConstant.NEARBY_SHOP_WITH_LOCATION_INTERCEPTOR,

            // 正则测试
            "http://www.meituan.com",
            "http://www.google.com",

            // 跳转UriHandler；重定向测试
            "/browser?url=http%3a%2f%2fwww.meituan.com",

            // UriHandler显示Toast
            DemoConstant.SHOW_TOAST_HANDLER,

            // 根据AB策略跳转不同页面
            DemoConstant.HOME_AB_TEST,

            // ServiceLoader测试
            DemoConstant.SERVICE_LOADER,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout container = findViewById(R.id.layout_container);
        for (final String uri : URIS) {
            Button button = new Button(this);
            button.setAllCaps(false);
            button.setText(uri);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jump(uri);
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            container.addView(button, params);
        }
    }

    private void jump(String uri) {
        Router.startUri(this, uri);
    }
}
