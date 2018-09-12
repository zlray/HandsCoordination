package com.xqlh.handscoordination;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xqlh.handscoordination.activity.IntroduceActivity;
import com.xqlh.handscoordination.activity.LoginActivity;
import com.xqlh.handscoordination.activity.RecordActivity;
import com.xqlh.handscoordination.activity.RegisterActivity;
import com.xqlh.handscoordination.base.BaseActivity;
import com.xqlh.handscoordination.entity.EntityRegisterCheck;
import com.xqlh.handscoordination.utils.Constants;
import com.xqlh.handscoordination.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends BaseActivity {

    @Bind(R.id.bt_start)
    Button bt_start;
    @Bind(R.id.bt_record)
    Button bt_record;
    @Bind(R.id.bt_login)
    Button bt_login;
    @Bind(R.id.bt_exit)
    Button bt_exit;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.bt_exit_back)
    ImageView bt_exit_back;

    SharedPreferences spRegister;
    SharedPreferences spUser;
    SharedPreferences spApp;
    String name;
    String id;
    String managerName;
    String managerPassword;
    boolean isRegister;
    String TermOfValidity;
    String PollCode;
    String uuid;
    String ResultMsg;
    OkHttpClient mOkHttpClient;
    EntityRegisterCheck entityRegisterCheck;
    private int code;
    private String msg;
    private boolean Result;
    long cuTime;
    long stopTime;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public int setContent() {
        return R.layout.activity_main;
    }

    @Override
    public boolean setFullScreen() {
        return false;
    }

    @Override
    public void init() {


        spRegister = getSharedPreferences(Constants.REGISTER_INFO, MODE_PRIVATE);
        // 学生信息的sp
        spUser = getSharedPreferences(Constants.USER_INFOR, MODE_PRIVATE);
        //app的状态
        spApp = getSharedPreferences(Constants.APP_STATE, MODE_PRIVATE);

        checkStudent();

        //读取是否已经注册的状态值
        isRegister = spApp.getBoolean(Constants.APP_REGISTER, false);
        Log.i("lz", isRegister + ".................");
        if (!isRegister) {
            finish();
            ToastUtils.showNOrmalToast(MainActivity.this, "请先注册");
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            mOkHttpClient = new OkHttpClient();
            //获得UUID
            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(this.TELEPHONY_SERVICE);
            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            uuid = deviceUuid.toString().trim();
            //获取当前的时间
            cuTime = System.currentTimeMillis();

            // 联网检测
            //如果有网进行
            if (isNetworkAvailable(this)) {
                //获得注册码
                PollCode = spRegister.getString(Constants.REGISTER_CODE, null);
                postAsynHttp(uuid, PollCode);
            }
            //获得截至的时间
            TermOfValidity = spRegister.getString(Constants.REGISTER_TIME, null);
            ResultMsg = spRegister.getString(Constants.REGISTER_RESULT, null);
            //如果截至时间大于当前时间
            try {
                if (TermOfValidity != null) {
                    stopTime = Constants.ConverToDate(TermOfValidity).getTime();
                    Log.i("lz", stopTime + "stopTime  stopTime  stopTime");
                    Log.i("lz", cuTime + "cuTime  cuTime  cuTime");
                    if (stopTime >= cuTime) {
                        //如果读取的信息为注册成功
                        if ("注册成功".equals(ResultMsg)) {
                            //判断学生是否登录
                            return;
                        } else {
                            //如果注册失败
                            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } else {
                        //修改值
                        SharedPreferences.Editor editor = spApp.edit();
                        editor.putBoolean(Constants.APP_REGISTER, false);
                        editor.commit();

                        Toast.makeText(MainActivity.this, "注册码过期", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 检测学生是否已经登录,登录后显示学生的信息
     */
    public void checkStudent() {
        //获得用户的id name
        //不点击登录，直接点击测试页面
        id = spUser.getString(Constants.USER_ID, "");
        name = spUser.getString(Constants.USER_NAME, "");
        Log.i("lz", id + "检测当前页面" + name);
        if (!"".equals(id) && !"".equals(name)) {
            tv_name.setVisibility(View.VISIBLE);
            bt_exit_back.setVisibility(View.VISIBLE);
            bt_exit.setVisibility(View.VISIBLE);
            tv_name.setText(name);
        } else {
            tv_name.setVisibility(View.INVISIBLE);
            bt_exit_back.setVisibility(View.INVISIBLE);
            bt_exit.setVisibility(View.INVISIBLE);
        }
    }

    //未登录 id name 设置按钮不显示
    public void setShared() {
        //登录的状态
        id = spUser.getString(Constants.USER_ID, "");
        name = spUser.getString(Constants.USER_NAME, "");
        if (!"".equals(id) && !"".equals(name)) {
            tv_name.setVisibility(View.VISIBLE);
            bt_exit_back.setVisibility(View.VISIBLE);
            bt_exit.setVisibility(View.VISIBLE);
        } else {
            tv_name.setVisibility(View.INVISIBLE);
            bt_exit_back.setVisibility(View.INVISIBLE);
            bt_exit.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.bt_start, R.id.bt_record, R.id.bt_login, R.id.bt_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start:
                setShared();
                if (!"".equals(id) && !"".equals(name)) {
                    //跳转到测试页面
                    startActivity(new Intent(MainActivity.this, IntroduceActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "学生请先登录", Toast.LENGTH_SHORT).show();
                    //跳转到登录页面
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
            case R.id.bt_record:
                managerName = spUser.getString(Constants.USER_ID, null);
                managerPassword = spUser.getString(Constants.USER_NAME, null);
                if ("admin".equals(managerName) && "admin".equals(managerPassword)) {
                    startActivity(new Intent(MainActivity.this, RecordActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "管理员请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                    intent1.putExtra("admin", "admin");
                    startActivity(intent1);
                }
                break;
            case R.id.bt_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.bt_exit:
                SharedPreferences.Editor editor = spUser.edit();
                editor.clear();
                editor.commit();
                tv_name.setVisibility(View.INVISIBLE);
                bt_exit_back.setVisibility(View.INVISIBLE);
                bt_exit.setVisibility(View.INVISIBLE);
                ToastUtils.showNOrmalToast(MainActivity.this, "退出成功");
                break;
            default:
                break;
        }
    }

    private void postAsynHttp(String uuid, String PollCode) {
        JSONObject object = new JSONObject();
        try {
            object.put("UUID", uuid);
            Log.i("lz", uuid + "uuid  uuid  uuid");
            object.put("PollCode", PollCode);
            Log.i("lz", PollCode + "PollCode  PollCode  PollCode");
            object.put("AppType", 111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody jsonBody = RequestBody.create(JSON, object.toString());
        Log.i("lz", object.toString() + "object.toString()  object.toString()  object.toString()");

        Request request = new Request.Builder()
                .url("http://open.bnuxq.com/api/clientapp/checkpollcode")
                .post(jsonBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string().trim();
                entityRegisterCheck = new Gson().fromJson(str, EntityRegisterCheck.class);
                code = entityRegisterCheck.getCode();
                msg = entityRegisterCheck.getMsg();
                Result = entityRegisterCheck.isResult();
                Log.i("lz", code + "code   code   code-------");
                Log.i("lz", msg + "msg   msg   msg------");
                Log.i("lz", Result + "Result   Result   Result-------");
                if (code == 1) {
                    if (Result == true) {
                        return;
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "注册码已经过期，请从新注册", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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



