package com.xqlh.handscoordination.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MySqliteOpenHelper extends SQLiteOpenHelper {


    public MySqliteOpenHelper(Context context) {
        super(context, Constants.DB, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constants.CREATE_DATA);
        db.execSQL(Constants.CREATE_USER);
        Log.i("lz", "数据库创建成功");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
