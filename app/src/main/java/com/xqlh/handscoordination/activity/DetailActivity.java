package com.xqlh.handscoordination.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.base.BaseActivity;
import com.xqlh.handscoordination.excele.Excele;
import com.xqlh.handscoordination.utils.Constants;
import com.xqlh.handscoordination.utils.MySqliteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class DetailActivity extends BaseActivity {
    @Bind(R.id.tv_id)
    TextView tv_id;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.tv_searchTime)
    TextView tv_searchTime;
    @Bind(R.id.tv_consumingTime)
    TextView tv_consumingTime;
    @Bind(R.id.bt_export)
    Button bt_export;
    @Bind(R.id.bt_return)
    Button bt_return;
    @Bind(R.id.tv_number)
    TextView tv_number;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alert;
    private String sql;
    private Cursor cursor;
    private int userCount;
    private String excelPath;
    SQLiteDatabase db;
    MySqliteOpenHelper mySqliteOpenHelper;
    private String id;
    private String name;
    private String searchTime;
    private String consumingTime;
    private String detailTime;
    private String number;
    Excele excele;

    @Override
    public int setContent() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean setFullScreen() {
        return false;
    }

    @Override
    public void init() {
        mySqliteOpenHelper = new MySqliteOpenHelper(getApplicationContext());
        db = mySqliteOpenHelper.getReadableDatabase();

        Intent intent = getIntent();
        detailTime = intent.getStringExtra("detailTime");

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("导出到Excele:");
        alertDialogBuilder.setMessage("文件路径:文件管理下智能双手调节仪文件夹中");
        setlectData();
    }

    @OnClick({R.id.bt_export, R.id.bt_return})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.bt_export:
                //
                alertDialogBuilder.setPositiveButton("确定导出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        excelPath = Constants.getExcelDir() + File.separator + "学号" + id + "姓名" + name + ".xls";
                        //创建excele表
                        excele = new Excele(DetailActivity.this, excelPath);

                        writeExcele(excele);
                    }
                });

                alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialogBuilder.setCancelable(true);
                alert = alertDialogBuilder.create();
                alert.show();
                break;
            case R.id.bt_return:
                Intent intent = new Intent(DetailActivity.this, RecordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    public void writeExcele(Excele excele) {
        List<String> listString = new ArrayList<>();
        listString.add(id);
        listString.add(name);
        listString.add(searchTime);
        listString.add(consumingTime);
        listString.add(number);
        excele.writeToExcel(listString);
    }

    public void setlectData() {
        Log.i("lz", detailTime + "查询的时间");
        sql = "select id,name,searchTime,number,consumingTime from data where detailTime = " + "'" + detailTime + "'";
        cursor = db.rawQuery(sql, null);
        userCount = cursor.getCount();
        Log.i("lz", userCount + "游标的大小");
        if (userCount > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor.getCount() != 0) {
                    id = cursor.getString(cursor.getColumnIndex("id"));
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    searchTime = cursor.getString(cursor.getColumnIndex("searchTime"));
                    number = cursor.getString(cursor.getColumnIndex("number"));
                    consumingTime = cursor.getString(cursor.getColumnIndex("consumingTime"));
                }
            }
            cursor.close();
        }
        tv_id.setText(id);
        tv_name.setText(name);
        tv_searchTime.setText(searchTime);
        tv_consumingTime.setText(consumingTime);
        tv_number.setText(number);
    }
}
