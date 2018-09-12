package com.xqlh.handscoordination.activity;

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
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xqlh.handscoordination.MainActivity;
import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.base.BaseActivity;
import com.xqlh.handscoordination.entity.EntityRegisterResult;
import com.xqlh.handscoordination.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionGen;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.bt_register)
    Button bt_register;
    @Bind(R.id.et_register)
    EditText et_register;
    OkHttpClient mOkHttpClient;
    String uuid;
    String registerCode;
    EntityRegisterResult entityRegisterResult;
    boolean isRegister;
    //偏好设置文件存储
    private SharedPreferences preferences;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    String ResultMsg;
    EntityRegisterResult.ResultBean resultBean;
    String TermOfValidity;
    String PollCode;
    SharedPreferences spApp;

    @Override
    public int setContent() {
        return R.layout.activity_register;
    }

    @Override
    public boolean setFullScreen() {
        return false;
    }

    @Override
    public void init() {
        //申请权限
//        requestAllPower();

        PermissionGen.with(RegisterActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .request();


        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(this.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        uuid = deviceUuid.toString().trim();

        preferences = getSharedPreferences(Constants.REGISTER_INFO, MODE_PRIVATE);

        spApp = getSharedPreferences(Constants.APP_STATE, MODE_PRIVATE);

        mOkHttpClient = new OkHttpClient();
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @OnClick({R.id.bt_register})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_register:
                registerCode = et_register.getText().toString().toString();
                if (isNetworkAvailable(this)) {
                    postAsynHttp(registerCode);

                } else {
                    Toast.makeText(RegisterActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void postAsynHttp(final String registerCode) {
        JSONObject object = new JSONObject();
        try {
            object.put("UUID", uuid);
            object.put("PollCode", registerCode);
            object.put("AppType", 111);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody jsonBody = RequestBody.create(JSON, object.toString());
        Request request = new Request.Builder()
                .url("http://open.bnuxq.com/api/clientapp/register")
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
                Log.i("lz", str + "aaaaaaaaaaaaa");
                entityRegisterResult = new Gson().fromJson(str, EntityRegisterResult.class);
                resultBean = entityRegisterResult.getResult();
                if (entityRegisterResult != null && resultBean != null) {
                    //过期时间
                    TermOfValidity = resultBean.getTermOfValidity();
                    //注册码
                    PollCode = resultBean.getPollCode();
                    //注册结果
                    ResultMsg = entityRegisterResult.getResultMsg();
                    Log.i("lz", ResultMsg + " ResultMsg     ResultMsg        ResultMsg");
                    if ("注册成功".equals(ResultMsg)) {
                        Log.i("lz", PollCode + "PollCode   PollCode   PollCode");
                        Log.i("lz", TermOfValidity + "TermOfValidity   TermOfValidity   TermOfValidity");
                        Log.i("lz", ResultMsg + "ResultMsg   ResultMsg   ResultMsg");
                        SharedPreferences.Editor editor = preferences.edit();
                        //存储注册码，有效时间 ,注册的结果
                        editor.putString(Constants.REGISTER_CODE, PollCode);
                        editor.putString(Constants.REGISTER_TIME, TermOfValidity);
                        editor.putString(Constants.REGISTER_RESULT, ResultMsg);
                        editor.commit();
                        //存储app已经注册
                        SharedPreferences.Editor editor1 = spApp.edit();
                        editor1.putBoolean(Constants.APP_REGISTER, true);
                        editor1.commit();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //跳转到主页面
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "注册码错误或者已过期，请重新输入", Toast.LENGTH_SHORT).show();
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
