package com.sankuai.waimai.router.demo.lib2;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by jzj on 2018/3/26.
 */

public class DialogUtils {

    public static ProgressDialog showProgress(Context context, String title) {
        return ProgressDialog.show(context, title, null, true, false);
    }

    public static void dismiss(ProgressDialog dialog) {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception ignored) {
            }
        }
    }
}
