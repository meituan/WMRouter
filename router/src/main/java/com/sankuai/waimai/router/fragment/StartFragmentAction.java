package com.sankuai.waimai.router.fragment;
/*
 * Copyright (C) 2005-2018 Meituan Inc.All Rights Reserved.
 * Description：
 * History：
 *
 * @desc
 * @author chenmeng06
 * @date 2019/3/5
 */

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.sankuai.waimai.router.core.UriRequest;

public interface StartFragmentAction {
    String START_FRAGMENT_ACTION = "StartFragmentAction";


    /**
     *  <p>启动Fragment操作。</p>
     * @param intent 跳转要用的intent
     * @return 是否执行了startFragment操作
     */
    boolean startFragment(@NonNull UriRequest request, @NonNull Bundle intent)
            throws ActivityNotFoundException, SecurityException;
}