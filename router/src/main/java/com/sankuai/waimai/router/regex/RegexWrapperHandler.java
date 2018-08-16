package com.sankuai.waimai.router.regex;

import android.support.annotation.NonNull;

import com.sankuai.waimai.router.common.WrapperHandler;
import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;

import java.util.regex.Pattern;

/**
 * Created by jzj on 2018/3/26.
 */

public class RegexWrapperHandler extends WrapperHandler {

    private final Pattern mPattern;
    private final int mPriority;

    public RegexWrapperHandler(@NonNull Pattern pattern, int priority,
            @NonNull UriHandler delegate) {
        super(delegate);
        mPattern = pattern;
        mPriority = priority;
    }

    public int getPriority() {
        return mPriority;
    }

    @Override
    protected boolean shouldHandle(@NonNull UriRequest request) {
        return mPattern.matcher(request.getUri().toString()).matches();
    }

    @Override
    public String toString() {
        return "RegexWrapperHandler(" + mPattern + ")";
    }
}
