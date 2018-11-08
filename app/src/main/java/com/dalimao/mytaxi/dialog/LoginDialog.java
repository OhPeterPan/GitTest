package com.dalimao.mytaxi.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.Api;
import com.dalimao.mytaxi.common.http.bean.Account;
import com.dalimao.mytaxi.common.http.bean.LoginResponse;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.util.SharedPreferenceManager;
import com.google.gson.Gson;

import java.lang.ref.SoftReference;

public class LoginDialog extends Dialog {
    private static final int LOGIN_SUC = 200;
    private static final int PW_ERR = 100005;
    private static final int SERVER_FAIL = -1;
    private String phoneNumber;
    private EditText phone;
    private EditText password;
    private IHttpClient client;
    private MyHandler mHandler;
    private ProgressBar loading;
    private TextView tips;


    private static class MyHandler extends Handler {

        private SoftReference<LoginDialog> softReference;

        public MyHandler(LoginDialog loginDialog) {
            softReference = new SoftReference<>(loginDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoginDialog loginDialog = softReference.get();
            switch (msg.what) {
                case LOGIN_SUC:
                    loginDialog.showSuc();
                    break;
                case PW_ERR:
                    loginDialog.showPwErr();
                    break;
                case SERVER_FAIL:
                    loginDialog.showServiceErr();
                    break;
            }
        }
    }

    private void showServiceErr() {
        loading.setVisibility(View.GONE);
        tips.setVisibility(View.VISIBLE);
        tips.setTextColor(getContext()
                .getResources()
                .getColor(R.color.error_red));
        tips.setText(getContext()
                .getString(R.string.error_server));
    }

    private void showSuc() {
        dismiss();
        loading.setVisibility(View.GONE);
        tips.setVisibility(View.GONE);
        ToastUtils.showShort("登陆成功");
    }

    private void showPwErr() {
        loading.setVisibility(View.GONE);
        tips.setVisibility(View.VISIBLE);
        tips.setTextColor(getContext()
                .getResources()
                .getColor(R.color.error_red));
        tips.setText(getContext()
                .getString(R.string.password_error));
    }

    public LoginDialog(@NonNull Context context, String phone) {
        this(context, R.style.Dialog);
        this.phoneNumber = phone;
        client = new OkHttpClientImpl();
        mHandler = new MyHandler(this);
    }

    public LoginDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_input);
        initView();
    }

    private void initView() {
        phone = findViewById(R.id.phone);
        phone.setText(phoneNumber);
        password = findViewById(R.id.password);
        loading = findViewById(R.id.loading);
        tips = findViewById(R.id.tips);
        Button btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                String url = Api.Config.getDomain() + Api.AUTH_URL;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phoneNumber);
                String psd = password.getText().toString();
                request.setBody("password", psd);

                IResponse response = client.post(request, false);
                Log.i("wak", response.getData());
                if (response.getCode() == BaseResponse.STATE_SUC_CODE) {
                    LoginResponse bizRes =
                            new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (bizRes.getCode() == BaseResponse.STATE_SUC_CODE) {
                        // 保存登录信息
                        // todo: 加密存储
                        Account account = bizRes.data;
                        SharedPreferenceManager.save(SharedPreferenceManager.ACCOUNT_KEY, account);

                        // 通知 UI
                        mHandler.sendEmptyMessage(LOGIN_SUC);
                    }
                    if (bizRes.getCode() == 100005) {
                        mHandler.sendEmptyMessage(PW_ERR);
                    } else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }
            }
        }.start();
    }

}
