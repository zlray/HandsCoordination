package com.xqlh.handscoordination;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.Company.test2D.UnityPlayerActivity;
import com.unity3d.player.UnityPlayer;
import com.xqlh.handscoordination.activity.ResultActivity;

public class Unity3DActivity extends UnityPlayerActivity {

    String num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("lz", "双手调节仪");

        Intent intent = getIntent();
        num = intent.getStringExtra("num");
        Log.i("lz", "numnumnum：" + num );
        //num为1  加载第一个地图  2为第二个地图
        UnityPlayer.UnitySendMessage("Main Camera", "startScene", num);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * 获得的结果数据
     *
     * @param score （2_12）格式  _前为出去次数后为时间
     */
    public void getScore(String score) {
        Intent intent = new Intent(Unity3DActivity.this, ResultActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
    }
}
