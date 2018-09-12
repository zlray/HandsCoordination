package com.xqlh.handscoordination.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xqlh.handscoordination.MainActivity;
import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.utils.ChineseLimitInputTextWatcher;
import com.xqlh.handscoordination.utils.Constants;
import com.xqlh.handscoordination.utils.MySqliteOpenHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFragment extends Fragment {


    @Bind(R.id.et_id)
    EditText et_id;

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.bt_login)
    Button bt_login;
    @Bind(R.id.bt_return)
    Button bt_return;

    private String id;
    private String name;
    private String ida;
    private String namea;
    private SharedPreferences spUser;
    SharedPreferences.Editor editor;
    View view;
    SQLiteDatabase db;
    MySqliteOpenHelper mySqliteOpenHelper;
    String nameDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mySqliteOpenHelper = new MySqliteOpenHelper(getActivity());
        db = mySqliteOpenHelper.getReadableDatabase();
        spUser = getActivity().getSharedPreferences(Constants.USER_INFOR, MODE_PRIVATE);
        view = inflater.inflate(R.layout.fragment_student, container, false);
        ButterKnife.bind(this, view);
        et_name.addTextChangedListener(new ChineseLimitInputTextWatcher(et_name));
        editor = spUser.edit();
        return view;
    }

    @OnClick({R.id.bt_login, R.id.bt_return})
    public void Onclikc(View view) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (view.getId()) {
            case R.id.bt_login:
                //获得输入框的内容
                String sql = null;
                id = et_id.getText().toString().trim();
                name = et_name.getText().toString().trim();
                //从数据库中
                sql = "select name from user where id = " + "'" + id + "'";
                Cursor cursor = db.rawQuery(sql, null);
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        if (cursor.getCount() != 0) {
                            nameDB = cursor.getString(cursor.getColumnIndex("name"));
                            if (nameDB.equals(name)) {
                                ida = spUser.getString(Constants.USER_ID, "");
                                namea = spUser.getString(Constants.USER_NAME, "");
                                if (!TextUtils.isEmpty(et_id.getText().toString())) {
                                    if (!TextUtils.isEmpty(et_name.getText().toString())) {
                                        if ("".equals(ida) && "".equals(namea)) {
                                            //存入id，name
                                            editor.putString(Constants.USER_ID, id);
                                            editor.putString(Constants.USER_NAME, name);
                                            editor.commit();
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getActivity(), "请先注销已登录用户", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "姓名不能为空", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "学号,姓名不能为空", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getActivity(), "学号与姓名不符合", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    cursor.close();
                } else {
                    db.beginTransaction();
                    db.execSQL("insert into user(id,name) " +
                                    "values(?,?)",
                            new Object[]{id, name});
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    ida = spUser.getString(Constants.USER_ID, "");
                    namea = spUser.getString(Constants.USER_NAME, "");
                    if (!TextUtils.isEmpty(et_id.getText().toString())) {
                        if (!TextUtils.isEmpty(et_name.getText().toString())) {
                            if ("".equals(ida) && "".equals(namea)) {
                                //存入id，name
                                editor.putString(Constants.USER_ID, id);
                                editor.putString(Constants.USER_NAME, name);
                                editor.commit();
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "请先注销已登录用户", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "姓名不能为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "学号,姓名不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bt_return:
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
