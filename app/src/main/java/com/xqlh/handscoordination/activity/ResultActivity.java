package com.xqlh.handscoordination.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xqlh.handscoordination.MainActivity;
import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.base.BaseActivity;
import com.xqlh.handscoordination.utils.Constants;
import com.xqlh.handscoordination.utils.MySqliteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

public class ResultActivity extends BaseActivity {
    private String score;

    @Bind(R.id.tv_time)
    TextView tv_time;

    @Bind(R.id.tv_number)
    TextView tv_number;

    SQLiteDatabase db;
    MySqliteOpenHelper mySqliteOpenHelper;
    private String detailTime;
    private String searchTime;
    SharedPreferences sp;
    String id;
    String name;
    String time; //消耗的时间
    String number;  //脱轨次数


    @Override
    public int setContent() {
        return R.layout.activity_result;
    }

    @Override
    public boolean setFullScreen() {
        return true;
    }

    @Override
    public void init() {
        score = getIntent().getStringExtra("score");
        Log.i(TAG, "结果页面" + score);
        number = score.substring(0, score.indexOf("_"));
        time = score.substring(score.indexOf("_") + 1);
        Log.i(TAG, "次数：" + number + "  时间：" + time);

        mySqliteOpenHelper = new MySqliteOpenHelper(this);
        db = mySqliteOpenHelper.getWritableDatabase();
        sp = getSharedPreferences(Constants.USER_INFOR, MODE_PRIVATE);

        id = sp.getString(Constants.USER_ID, "");
        name = sp.getString(Constants.USER_NAME, "");
        tv_time.setText(time);
        tv_number.setText(number);
    }

    @OnClick({R.id.bt_save, R.id.bt_return})
    public void OnClick(View view) {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (view.getId()) {
            case R.id.bt_save:
                //
                saveData();
                startActivity(intent);
                break;
            case R.id.bt_return:
                startActivity(intent);
                break;
        }
    }
    public void saveData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        detailTime = formatter.format(curDate);

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate1 = new Date(System.currentTimeMillis());//获取当前时间
        searchTime = formatter1.format(curDate1);

        db.beginTransaction();
        db.execSQL("insert into data(id,name,searchTime,detailTime,number,consumingTime) " +
                        "values(?,?,?,?,?,?)",
                new Object[]{id, name, searchTime, detailTime, number, time});

        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
