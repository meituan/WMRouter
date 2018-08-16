package com.sankuai.waimai.router.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.sankuai.waimai.router.core.UriRequest;

/**
 * 启动Activity操作
 *
 * Created by jzj on 2017/4/18.
 */
public interface StartActivityAction {

    /**
     * <p>启动Activity操作（可在此修改Intent，设置动画等）。</p>
     *
     * <p>在执行 {@link Context#startActivity(Intent)} 前调用此方法。</p>
     *
     * <p>
     * 返回true：已经处理了startActivity操作。<br/>
     * 返回false：未处理，之后会继续执行默认的startActivity逻辑。
     * </p>
     *
     * @param intent 跳转要用的intent
     * @return 是否执行了startActivity操作
     */
    boolean startActivity(@NonNull UriRequest request, @NonNull Intent intent)
            throws ActivityNotFoundException, SecurityException;
}
