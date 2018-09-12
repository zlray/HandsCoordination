package com.xqlh.handscoordination.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/1/23.
 */

public class ToastUtils {
    private static Toast mToastNormal;

    /**
     * 普通的toast提示
     */
    public static void showNOrmalToast(Context mContext, String message) {

        ToastUtils.cancel();
        if (mToastNormal == null) {
            mToastNormal = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        }
        mToastNormal.show();
    }

    /**
     * toast取消
     */
    public static void cancel() {
        if (mToastNormal != null) {
            mToastNormal.cancel();
            mToastNormal = null;
        }

    }
}
