package com.sankuai.waimai.router.demo.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.common.DefaultUriRequest;
import com.sankuai.waimai.router.common.FragmentUriRequest;
import com.sankuai.waimai.router.core.OnCompleteListener;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.demo.R;
import com.sankuai.waimai.router.demo.basic.TestUriRequestActivity;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.demo.lib2.ToastUtils;

/**
 * Created by hailiangliao on 2017/12/25.
 */

public class DemoFragment extends Fragment {

    public static DemoFragment newInstance() {
        return new DemoFragment();
    }

    public DemoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_demo, container, false);
        v.findViewById(R.id.btn_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FragmentUriRequest(DemoFragment.this, DemoConstant.JUMP_ACTIVITY_1)
                        .activityRequestCode(100)
                        .putExtra(TestUriRequestActivity.INTENT_TEST_INT, 1)
                        .putExtra(TestUriRequestActivity.INTENT_TEST_STR, "str")
                        .overridePendingTransition(R.anim.enter_activity, R.anim.exit_activity)
                        .onComplete(new OnCompleteListener() {
                            @Override
                            public void onSuccess(@NonNull UriRequest request) {
                                ToastUtils.showToast(request.getContext(), "跳转成功");
                            }

                            @Override
                            public void onError(@NonNull UriRequest request, int resultCode) {

                            }
                        })
                        .start();
            }
        });
        return v;
    }
}
