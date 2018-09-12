package com.xqlh.handscoordination.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xqlh.handscoordination.MainActivity;
import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.entity.EntityLoginCheck;
import com.xqlh.handscoordination.utils.Constants;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


public class TeacherFragment extends Fragment {
    @Bind(R.id.et_username)
    EditText et_username;

    @Bind(R.id.et_password)
    EditText et_password;

    @Bind(R.id.bt_manage_login)
    Button bt_manage_login;
    @Bind(R.id.bt_manage_return)
    Button bt_manage_return;

    private String username;
    private String password;

    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    private EntityLoginCheck entityLoginCheck;

    private int code;
    private String msg;
    private String Result;
    private String ResultMsg;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_teacher, container, false);
        sp = getActivity().getSharedPreferences(Constants.USER_INFOR, MODE_PRIVATE);
        editor = sp.edit();
        ButterKnife.bind(this, view);
        return view;
    }
    @OnClick({R.id.bt_manage_login, R.id.bt_manage_return})
    public void Onclikc(View view) {
        switch (view.getId()) {

            case R.id.bt_manage_login:
                username = et_username.getText().toString();
                password = et_password.getText().toString();

                if (isNetworkAvailable(getActivity())) {
                    if (!"".equals(username)) {
                        if (!"".equals(password)) {
                            par(username, password);
                        } else {
                            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "用户名，密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if ("admin".equals(username) && "admin".equals(password)) {
                        editor.putString(Constants.USER_ID, "admin");
                        editor.putString(Constants.USER_NAME, "admin");
                        editor.commit();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bt_manage_return:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    //yangguo 123456
    //
    public void par(final String username, final String password) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url(Constants.Base_LOGIN_URL + "?username=" + username + "&password=" + password)
                .addHeader("Authorization", Constants.TOKEN)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.i("lz", str + "str    str    str");
                entityLoginCheck = new Gson().fromJson(str, EntityLoginCheck.class);
                code = entityLoginCheck.getCode();
                msg = entityLoginCheck.getMsg();
                Result = entityLoginCheck.getResultMsg();
                ResultMsg = entityLoginCheck.getResultMsg();
                if (code == 1) {
                    if (!"".equals(Result)) {
                        editor.putString(Constants.USER_ID, username);
                        editor.putString(Constants.USER_NAME, password);
                        editor.commit();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
