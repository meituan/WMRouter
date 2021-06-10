package com.sankuai.waimai.router.demo.basic;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.widget.TextView;

import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.demo.lib2.BaseActivity;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.demo.R;

/**
 * Created by jzj on 2018/3/27.
 */
@RouterUri(path = DemoConstant.JUMP_WITH_REQUEST)
public class TestUriRequestActivity extends BaseActivity {

    public static final String INTENT_TEST_INT = "test_int";
    public static final String INTENT_TEST_STR = "test_str";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        TextView text = findViewById(R.id.text);

        Intent intent = getIntent();
        StringBuilder s = new StringBuilder();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                s.append(key).append(" = ").append(extras.get(key)).append('\n');
            }
        }
        text.setText(s.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK);
    }
}
