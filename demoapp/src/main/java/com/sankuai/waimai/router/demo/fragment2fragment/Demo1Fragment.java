package com.sankuai.waimai.router.demo.fragment2fragment;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sankuai.waimai.router.annotation.RouterPage;
import com.sankuai.waimai.router.common.PageAnnotationHandler;
import com.sankuai.waimai.router.core.OnCompleteListener;
import com.sankuai.waimai.router.core.UriRequest;
import com.sankuai.waimai.router.demo.R;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;
import com.sankuai.waimai.router.demo.lib2.ToastUtils;
import com.sankuai.waimai.router.fragment.v4.FragmentBuildUriRequest;
import com.sankuai.waimai.router.fragment.v4.FragmentTransactionUriRequest;

/**
 * Created by hailiangliao on 2017/12/25.
 * Update by chenmeng06 on 2019/3/6
 */
@RouterPage(path = DemoConstant.TEST_DEMO_FRAGMENT_1, interceptors = DemoFragmentInterceptor.class)
public class Demo1Fragment extends Fragment {

    public static Demo1Fragment newInstance() {
        return new Demo1Fragment();
    }

    public Demo1Fragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_demo_1, container, false);
        //测试Fragment
        v.findViewById(R.id.btn_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FragmentTransactionUriRequest(Demo1Fragment.this.getActivity(), PageAnnotationHandler.SCHEME_HOST + DemoConstant.TEST_DEMO_FRAGMENT_2)
                        .replace(R.id.fragment_container)
                        .putExtra("message","HelloWorld") //测试参数
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

        //测试自定义jump
        // FragmentUriTransactionRequest在特殊情况下无法处理的时候，
        // 直接在onComplete返回Fragment自行处理

        v.findViewById(R.id.btn_cus_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FragmentBuildUriRequest(Demo1Fragment.this.getContext(), PageAnnotationHandler.SCHEME_HOST + DemoConstant.TEST_DEMO_FRAGMENT_2)
                        .putExtra("message","HelloWorld") //测试参数
                        .onComplete(new OnCompleteListener() {
                            @Override
                            public void onSuccess(@NonNull UriRequest request) {
                                Fragment fragment = request.getField(Fragment.class, FragmentBuildUriRequest.FRAGMENT);
                                Demo1Fragment.this.getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, fragment)
                                        .commitAllowingStateLoss();
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
