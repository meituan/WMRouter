package com.sankuai.waimai.router.demo.fragment2fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sankuai.waimai.router.annotation.RouterPage;
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
@RouterPage(path = DemoConstant.TEST_DEMO_FRAGMENT_2, interceptors = DemoFragmentInterceptor.class)
public class Demo2Fragment extends Fragment {

    public static Demo2Fragment newInstance() {
        return new Demo2Fragment();
    }

    public Demo2Fragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_demo_2, container, false);
        String message = getArguments().getString("message","");
        TextView textView = v.findViewById(R.id.text_message);
        textView.setText("get msg:" + message);

        v.findViewById(R.id.btn_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FragmentUriRequest(Demo2Fragment.this, DemoConstant.JUMP_ACTIVITY_1)
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
