package com.xqlh.handscoordination.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */

public class Constants {

    /**
     * SharedPreferences文件名,存放应用的状态
     * The name of SharedPreferences file,stores the application'status
     */
    public static final String USER_INFOR = "user_infor";
    //学生的id
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";

    //app的状态
    public static final String APP_STATE = "app_state";
    public static final String APP_REGISTER = "register_state";

    //注册的状态
    public static final String REGISTER_INFO = "register_info";
    public static final String REGISTER_TIME = "register_time";
    public static final String REGISTER_CODE = "register_code";
    public static final String REGISTER_RESULT = "register_result";

    public static final String DB = "yks.db";
    public static final int DB_VERSION = 1;
    //    AUTOINCREMENT NOT NULL
    public static final String TOKEN = "YmNkMzJhYWIxNDhiNDYyZGJmMTVjMzNkNjk3NDk2YTEtNDU2ZmY5OTA0OWI2MmVlZGJhMTVjYjUzZGY5MDdkMWItdHc0NG05cnkybzEtMTQ5NDU2MjkxNA==";

    public static final String Base_LOGIN_URL = "http://open.bnuxq.com/api/Account/token";


    public static final String CREATE_USER =

            "create table user ("
                    + "_id integer PRIMARY KEY AUTOINCREMENT NOT NULL," //
                    + "id text," //学号
                    + "name text)";  //姓名


    public static final String CREATE_DATA =
            "create table data ("
                    + "_id integer PRIMARY KEY AUTOINCREMENT NOT NULL," //
                    + "id text,"
                    + "name text,"
                    + "searchTime text,"
                    + "detailTime text,"
                    + "number text,"
                    + "consumingTime text)";  //姓名


    //把日期转为字符串
    public static String ConverToString(String s) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return df.format(new Date(s));
    }

    //把字符串转为日期
    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    public static String getExcelDir() {
        // SD卡指定文件夹
        String sdcardPath = Environment.getExternalStorageDirectory().toString();

        Log.i("lz", sdcardPath);

        File dir = new File(sdcardPath + File.separator + "双手调节仪");

        Log.i("lz", dir + "");

        if (dir.exists()) {
            return dir.toString();
        } else {
            dir.mkdirs();
            Log.e("BAG", "保存路径不存在,");
            return dir.toString();
        }
    }
}


