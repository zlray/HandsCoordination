package com.xqlh.handscoordination.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.xqlh.handscoordination.utils.ToastUtils;

import butterknife.ButterKnife;


public abstract class BaseActivity extends FragmentActivity {

    /**
     * 定义一个成员变量
     * Define a  member arivable that is isFullScreen.
     * it's default value is false,which means not full-screen.
     */
    private boolean isFullScreen = false;
    protected Context mContext;
    protected static final String TAG = "lz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 必须设置在加载布局前
         * Must be set before loading the Layout
         */
        isFullScreen = setFullScreen();

        if (isFullScreen) {
            /**
             * 设置为无标题，全屏
             *
             * setting to Untitled,Full Screen
             */
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );

        } else {
            /**
             * 设置为无标题
             * set to Untitled
             */
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
        mContext = BaseActivity.this;

        setContentView(setContent());
        //绑定注解
        ButterKnife.bind(this);

        init();
    }

    /**
     * @return
     * @description 加载该Acitivit的布局
     * loading the layout of current Activity
     */
    public abstract int setContent();

    /**
     * @description 是否设置当前Activity为全屏
     * Weather to set the current Activity to full screen
     */
    public abstract boolean setFullScreen();

    /**
     * @description 加载当前Activity的控件
     * Loading Controls of current Activity
     */
    public abstract void init();

    /**
     * 处理onStop方法,
     */
    @Override
    protected void onStop() {
        ToastUtils.cancel();
        super.onStop();

    }


    /**
     * 处理onPause方法
     */
    @Override
    protected void onPause() {
        ToastUtils.cancel();
        super.onPause();
    }

    /**
     * 处理onDestory方法
     */
    @Override
    protected void onDestroy() {
        ToastUtils.cancel();
        super.onDestroy();

    }

}
