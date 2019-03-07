package com.sankuai.waimai.router.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sankuai.waimai.router.demo.R;
import com.sankuai.waimai.router.demo.lib2.BaseActivity;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.annotation.RouterUri;

/**
 * Created by hailiangliao on 2017/12/13.
 */
@RouterUri(path = DemoConstant.JUMP_FRAGMENT_ACTIVITY)
public class FragmentDemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        launchFragment();
    }

    private void launchFragment() {
        Fragment fragment = DemoFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                fragment).commit();
    }
}
