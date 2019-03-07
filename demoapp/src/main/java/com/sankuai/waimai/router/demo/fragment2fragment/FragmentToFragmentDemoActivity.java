package com.sankuai.waimai.router.demo.fragment2fragment;

import android.os.Bundle;

import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.demo.R;
import com.sankuai.waimai.router.demo.lib2.BaseActivity;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.fragment.v4.FragmentUriTransactionRequest;

/**
 * Created by hailiangliao on 2017/12/13.
 */
@RouterUri(path = DemoConstant.TEST_FRAGMENT_TO_FRAGMENT_ACTIVITY)
public class FragmentToFragmentDemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        launchFragment();
    }

    private void launchFragment() {
        new FragmentUriTransactionRequest(this, DemoConstant.TEST_DEMO_FRAGMENT_1)
                .add(R.id.fragment_container)
                .allowingStateLoss()
                .start();
    }

}
