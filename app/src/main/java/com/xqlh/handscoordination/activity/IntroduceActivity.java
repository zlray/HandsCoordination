package com.xqlh.handscoordination.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;

import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.Unity3DActivity;
import com.xqlh.handscoordination.base.BaseActivity;
import com.xqlh.handscoordination.utils.MySqliteOpenHelper;

import butterknife.Bind;
import butterknife.OnClick;

public class IntroduceActivity extends BaseActivity {
    @Bind(R.id.bt_return)
    Button bt_return;
    MediaPlayer mediaPlayer;
    SQLiteDatabase db;
    MySqliteOpenHelper mySqliteOpenHelper;

    @Override
    public int setContent() {
        return R.layout.activity_introduce;
    }

    @Override
    public boolean setFullScreen() {
        return false;
    }

    @Override
    public void init() {
        mediaPlayer = MediaPlayer.create(IntroduceActivity.this, R.raw.introduce);
        mediaPlayer.start();
        mySqliteOpenHelper = new MySqliteOpenHelper(this);
        db = mySqliteOpenHelper.getWritableDatabase();
    }

    @OnClick({R.id.bt_A, R.id.bt_B, R.id.bt_return})
    public void OnClikc(View view) {
        switch (view.getId()) {
            case R.id.bt_A:
                Intent intent1 = new Intent(IntroduceActivity.this,Unity3DActivity.class);
                intent1.putExtra("num","1");
                startActivity(intent1);
                break;
            case R.id.bt_B:
                Intent intent2 = new Intent(IntroduceActivity.this,Unity3DActivity.class);
                intent2.putExtra("num","2");
                startActivity(intent2);
                break;
            case R.id.bt_return:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }
}
