package com.xqlh.handscoordination.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.adapter.AdapterRecord;
import com.xqlh.handscoordination.base.BaseActivity;
import com.xqlh.handscoordination.entity.Entity;
import com.xqlh.handscoordination.event.EventCheck;
import com.xqlh.handscoordination.event.EventSql;
import com.xqlh.handscoordination.excele.Excele;
import com.xqlh.handscoordination.utils.Constants;
import com.xqlh.handscoordination.utils.Loading_view;
import com.xqlh.handscoordination.utils.MySqliteOpenHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class RecordActivity extends BaseActivity {

    @Bind(R.id.et_id)
    EditText et_id;

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.tv_start)
    TextView tv_start;

    @Bind(R.id.tv_end)
    TextView tv_end;

    @Bind(R.id.bt_search)
    Button bt_search;

    @Bind(R.id.rv_record)
    RecyclerView rv_record;
    @Bind(R.id.bt_all)
    Button bt_all;
    @Bind(R.id.bt_export)
    Button bt_export;


    private int mYear;
    private int mMonth;
    private int mDay;
    private static final int SHOW_START = 0;
    private static final int START = 1;
    private static final int SHOW_END = 2;
    private static final int END = 3;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alert;
    private Loading_view loading;
    String sql;
    Cursor cursor;
    SQLiteDatabase db1;
    MySqliteOpenHelper mySqliteOpenHelper;

    Map<Integer, Boolean> map;
    AdapterRecord adapterRecord;

    private List<Entity> mlist;
    Entity entity;
    private String id;
    private String name;
    private String searchTime;
    private String consumingTime;
    private String number;
    private String detailTime;
    private int userCount;
    List<Entity> entityList;
    private String excelPath;
    private Excele excele;

    boolean b = true;

    @Override
    public int setContent() {
        return R.layout.activity_record;
    }

    @Override
    public boolean setFullScreen() {
        return false;
    }

    @Override
    public void init() {
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("导出到Excele:");
        alertDialogBuilder.setMessage("文件路径:文件管理下的3D迷宫中");

        EventBus.getDefault().register(this);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        loding();
        mySqliteOpenHelper = new MySqliteOpenHelper(getApplicationContext());
        db1 = mySqliteOpenHelper.getReadableDatabase();

    }

    public void loding() {//点击加载并按钮模仿网络请求
        loading = new Loading_view(this, R.style.CustomDialog);
        loading.show();
        new Handler().postDelayed(new Runnable() {//定义延时任务模仿网络请求
            @Override
            public void run() {
                selectAll(); //
                loading.dismiss();//3秒后调用关闭加载的方法
            }
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatet(EventSql messageEvent) {
        switch (messageEvent.getStr()) {
            case "updateAll":
                selectAll();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatetCheck(EventCheck messageEvent) {
        switch (messageEvent.getStr()) {
            case "updateCheck":
                map = adapterRecord.getMap();
                Log.i("lz", map.values() + "map.values()map.values()map.values()");
                if (map.containsValue(false)) {
                    bt_all.setText("全选");
                    b = true;
                }
                break;
        }
    }

    @OnClick({R.id.tv_start, R.id.tv_end, R.id.bt_search, R.id.bt_all, R.id.bt_export, R.id.bt_return})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start:
                setDateTime1();
                Message msg = new Message();
                if (tv_start.equals((TextView) view)) {
                    msg.what = RecordActivity.SHOW_START;
                    RecordActivity.this.dateandtimeHandler.sendMessage(msg);
                }
                break;
            case R.id.tv_end:
                setDateTime2();
                Message msg1 = new Message();
                if (tv_end.equals((TextView) view)) {
                    msg1.what = RecordActivity.SHOW_END;
                    RecordActivity.this.dateandtimeHandler.sendMessage(msg1);
                }
                break;
            case R.id.bt_search:
                selectAll();
                break;
            case R.id.bt_all:
                map = adapterRecord.getMap();
                Log.i("lz", b + "b          b            b");
                if (b) {
                    bt_all.setText("取消全选");
                    for (int i = 0; i < map.size(); i++) {
                        map.put(i, true);

                        adapterRecord.notifyDataSetChanged();
                    }
                    Log.i("lz", map.values() + "map.values()map.values()map.values()");
                    b = false;
                } else {
                    bt_all.setText("全选");
                    for (int i = 0; i < map.size(); i++) {
                        map.put(i, false);
                        adapterRecord.notifyDataSetChanged();
                    }
                    Log.i("lz", map.values() + "map.values()map.values()map.values()");
                    b = true;
                }
                break;
//            case R.id.bt_clearAll:
//                map = adapterRecord.getMap();
//                for (int i = 0; i < map.size(); i++) {
//                    map.put(i, false);
//                    adapterRecord.notifyDataSetChanged();
//                }
//                adapterRecord.clearlist();
//                break;
            case R.id.bt_export:
                if (adapterRecord.getMap().containsValue(true)) {

                    alertDialogBuilder.setPositiveButton("确定导出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //点击加载并按钮模仿网络请求
                            loading = new Loading_view(RecordActivity.this, R.style.CustomDialog);
                            loading.show();
                            new Handler().postDelayed(new Runnable() {//定义延时任务模仿网络请求
                                @Override
                                public void run() {
                                    mlist = adapterRecord.a();
                                    for (int i = 0; i < mlist.size(); i++) {
                                        writeToExcele(mlist.get(i).getDetailTime());
                                    }
                                    loading.dismiss();//3秒后调用关闭加载的方法
                                }
                            }, 500);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert = alertDialogBuilder.create();
                    alert.show();
                } else {

                    Toast.makeText(RecordActivity.this, "请选择要导出的数据", Toast.LENGTH_SHORT).show();
                }
                //
                break;
            case R.id.bt_return:
                finish();
                break;
            default:
                break;
        }
    }

    public void writeToExcele(String detailTime) {
        Log.i("lz", detailTime + "查询的时间");
        sql = "select id,name,searchTime,number,consumingTime from data where detailTime = " + "'" + detailTime + "'";
        cursor = db1.rawQuery(sql, null);
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

        excelPath = Constants.getExcelDir() + File.separator + "学号" + id + "姓名" + name + ".xls";
        //创建excele表
        excele = new Excele(RecordActivity.this, excelPath);

        List<String> listString = new ArrayList<>();
        listString.add(id);
        listString.add(name);
        listString.add(searchTime);
        listString.add(consumingTime);
        listString.add(number);
        excele.writeToExcel(listString);
    }

    public void selectAll() {
        //只通过学号查询
        String ida = et_id.getText().toString().trim();
        String namea = et_name.getText().toString().trim();
        String starta = tv_start.getText().toString().trim();
        String enda = tv_end.getText().toString().trim();
        Date dateStart = null;
        Date dateEnd = null;
        long longStart = 0;
        long longEnd = 0;
        try {
            dateStart = Constants.ConverToDate(starta);
            longStart = dateStart.getTime();
            dateEnd = Constants.ConverToDate(enda);
            longEnd = dateEnd.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 通过id开始
        if (!"".equals(ida)) { //id不为空
            if ("".equals(namea)) { //name为空
                if ("".equals(starta) && "".equals(enda)) { //时间为空
                    //只通过id查询
                    selectOnlyId(ida);
                } else if (!"".equals(starta) && !"".equals(enda)) {
                    if (longStart > longEnd) {
                        Toast.makeText(RecordActivity.this, "开始时间不能小于结束时间", Toast.LENGTH_SHORT).show();
                    } else {
                        //通过id 和 时间查询
                        selectOnlyIdTime(ida, starta, enda);
                    }
                }
            } else {// name 不为空
                if ("".equals(starta) && "".equals(enda)) {
                    //只通过id name 查询
                    selectOnlyIdName(ida, namea);
                } else if (!"".equals(starta) && !"".equals(enda)) {
                    if (longStart > longEnd) {
                        Toast.makeText(RecordActivity.this, "开始时间不能小于结束时间", Toast.LENGTH_SHORT).show();
                    } else {
                        //通过id name 时间查询
                        selectIdNameTime(ida, namea, starta, enda);
                    }
                }
            }
        } else {//id为空
            if ("".equals(namea)) { //name为空
                if ("".equals(starta) && "".equals(enda)) {
                    //查询全部
                    select();
                } else if (!"".equals(starta) && !"".equals(enda)) {
                    if (longStart > longEnd) {
                        Toast.makeText(RecordActivity.this, "开始时间不能小于结束时间", Toast.LENGTH_SHORT).show();
                    } else {
                        //只查询时间
                        selectOnlyTime(starta, enda);
                    }

                }
            } else { // id为空  name不为空
                if ("".equals(starta) && "".equals(enda)) {
                    //只查询 name
                    selectOnlyName(namea);
                } else if (!"".equals(starta) && !"".equals(enda)) {
                    if (longStart > longEnd) {
                        Toast.makeText(RecordActivity.this, "开始时间不能小于结束时间", Toast.LENGTH_SHORT).show();
                    } else {
                        //只查询 name 一段时间内
                        selectOnlyNameTime(namea, starta, enda);
                    }
                }
            }
        }
    }

    /**
     * 查询全部的信息
     */
    public void select() {
        entityList = new ArrayList<>();
        sql = "select id,name,searchTime,detailTime from data ORDER BY _id  DESC";
        cursor = db1.rawQuery(sql, null);
        userCount = cursor.getCount();
        if (userCount > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor.getCount() != 0) {
                    id = cursor.getString(cursor.getColumnIndex("id"));
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    searchTime = cursor.getString(cursor.getColumnIndex("searchTime"));
                    detailTime = cursor.getString(cursor.getColumnIndex("detailTime"));
                    //实体类
                    entity = new Entity(id, name, searchTime, detailTime);
                    entityList.add(entity);
                    rv_record.setLayoutManager(new LinearLayoutManager(this));
                    adapterRecord = new AdapterRecord(this, entityList);
                    rv_record.setAdapter(adapterRecord);
                }
            }
            cursor.close();
        } else {
            rv_record.setLayoutManager(new LinearLayoutManager(this));
            adapterRecord = new AdapterRecord(this, new ArrayList<Entity>());
            rv_record.setAdapter(adapterRecord);
        }
    }

    //单个查询
    public void selectOnlyId(String id) {
        sql = "select * from data where id = " + "'" + id + "'" + "ORDER BY _id  DESC";
        cursor = db1.rawQuery(sql, null);
        selectData(cursor);
    }

    public void selectOnlyTime(String startTime, String endTime) {
        sql = "select * from data where searchTime between " + "'" + startTime + "'" + "and" + "'" + endTime + "'"
                + "ORDER BY _id  DESC";
        cursor = db1.rawQuery(sql, null);
        selectData(cursor);
    }

    public void selectOnlyName(String name) {
        sql = "select * from data where name = " + "'" + name + "'"
                + "ORDER BY _id  DESC";
        cursor = db1.rawQuery(sql, null);
        selectData(cursor);
    }

    public void selectOnlyIdName(String id, String name) {
        sql = "select * from data where id = " + "'" + id + "'"
                + "and name = " + "'" + name + "'" + "ORDER BY _id  DESC";
        cursor = db1.rawQuery(sql, null);
        selectData(cursor);
    }

    public void selectOnlyIdTime(String id, String startTime, String endTime) {
        sql = "select * from data where id = " + "'" + id + "'" + " and searchTime between "
                + "'" + startTime + "'" + "and" + "'" + endTime + "'" + "ORDER BY _id  DESC";
        cursor = db1.rawQuery(sql, null);
        selectData(cursor);
    }

    public void selectOnlyNameTime(String name, String startTime, String endTime) {
        sql = "select * from data where name = " + "'" + name + "'"
                + "and searchTime between " + "'" + startTime + "'" + "and" + "'" + endTime + "'"
                + "ORDER BY _id  DESC";
        cursor = db1.rawQuery(sql, null);
        selectData(cursor);

    }

    public void selectIdNameTime(String id, String name, String startTime, String endTime) {
        sql = "select * from data where id = " + "'" + id + "'"
                + "and name = " + "'" + name + "'"
                + " and searchTime between "
                + "'" + startTime + "'" + "and" + "'" + endTime + "'" + "ORDER BY _id  DESC";
        cursor = db1.rawQuery(sql, null);
        selectData(cursor);
    }

    public void selectData(Cursor cursor) {
        entityList = new ArrayList<>();
        userCount = cursor.getCount();
        if (userCount > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor.getCount() != 0) {
                    id = cursor.getString(cursor.getColumnIndex("id"));
                    name = cursor.getString(cursor.getColumnIndex("name"));
                    searchTime = cursor.getString(cursor.getColumnIndex("searchTime"));
                    detailTime = cursor.getString(cursor.getColumnIndex("detailTime"));
                    //实体类
                    entity = new Entity(id, name, searchTime, detailTime);
                    entityList.add(entity);
                    rv_record.setLayoutManager(new LinearLayoutManager(this));
                    adapterRecord = new AdapterRecord(this, entityList);
                    rv_record.setAdapter(adapterRecord);
                }
            }
            cursor.close();
            Toast.makeText(RecordActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RecordActivity.this, "该数据不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置日期
     */
    private void setDateTime1() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplay1();
    }

    private void setDateTime2() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplay2();
    }

    /**
     * 更新日期显示
     */
    private void updateDateDisplay1() {
        tv_start.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay));
    }

    private void updateDateDisplay2() {
        tv_end.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay));
    }

    /**
     * 日期控件的事件
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDateDisplay1();
        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDateDisplay2();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START:
                return new DatePickerDialog(this, mDateSetListener1, mYear, mMonth, mDay);
            case END:
                return new DatePickerDialog(this, mDateSetListener2, mYear, mMonth, mDay);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case START:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
            case END:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;

        }
    }

    /**
     * 处理日期和时间控件的Handler
     */
    Handler dateandtimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RecordActivity.SHOW_START:
                    showDialog(START);
                    break;
                case RecordActivity.SHOW_END:
                    showDialog(END);
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
