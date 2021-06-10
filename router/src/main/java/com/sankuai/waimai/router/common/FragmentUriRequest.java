package com.sankuai.waimai.router.common;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sankuai.waimai.router.activity.StartActivityAction;
import com.sankuai.waimai.router.components.ActivityLauncher;
import com.sankuai.waimai.router.core.Debugger;
import com.sankuai.waimai.router.core.UriRequest;

import java.util.HashMap;

/**
 * 继承DefaultUriRequest，用于从fragment跳转
 * Created by liaohailiang on 2018/11/16.
 */
public class FragmentUriRequest extends DefaultUriRequest {

    public FragmentUriRequest(@NonNull Fragment fragment, @NonNull Uri uri) {
        super(fragment.getContext(), uri);
        configStartAction(fragment);
    }

    public FragmentUriRequest(@NonNull Fragment fragment, @NonNull String uri) {
        super(fragment.getContext(), uri);
        configStartAction(fragment);
    }

    public FragmentUriRequest(@NonNull Fragment fragment, @NonNull String uri, HashMap<String, Object> extra) {
        super(fragment.getContext(), uri, extra);
        configStartAction(fragment);
    }

    private void configStartAction(@NonNull Fragment fragment) {
        putField(ActivityLauncher.FIELD_START_ACTIVITY_ACTION, new FragmentStartActivityAction(fragment));
    }

    private static class FragmentStartActivityAction implements StartActivityAction {
        private Fragment fragment;

        public FragmentStartActivityAction(@NonNull Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public boolean startActivity(@NonNull UriRequest request, @NonNull Intent intent)
                throws ActivityNotFoundException, SecurityException {
            try {
                Bundle options = request.getField(Bundle.class, ActivityLauncher.FIELD_START_ACTIVITY_OPTIONS);

                Integer requestCode = request.getField(Integer.class, ActivityLauncher.FIELD_REQUEST_CODE);

                if (requestCode != null) {
                    fragment.startActivityForResult(intent, requestCode, options);
                } else {
                    fragment.startActivity(intent, options);
                }
                return true;
            } catch (ActivityNotFoundException e) {
                Debugger.w(e);
                return false;
            } catch (SecurityException e) {
                Debugger.w(e);
                return false;
            }
        }
    }
}
