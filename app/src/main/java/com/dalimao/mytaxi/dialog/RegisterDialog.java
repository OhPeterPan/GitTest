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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.Api;
import com.dalimao.mytaxi.common.http.bean.Account;
import com.dalimao.mytaxi.common.http.bean.CommonBean;
import com.dalimao.mytaxi.common.http.bean.LoginResponse;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.util.SharedPreferenceManager;
import com.google.gson.Gson;

import java.lang.ref.SoftReference;

public class RegisterDialog extends Dialog {
    private static final int SERVICE_ERROR = 100;
    private static final int LOGIN_SUC = 1;
    private String phoneNumber;
    private ImageView close;
    private TextView phone;
    private EditText pw;
    private EditText pw1;
    private TextView tips;
    private Button btn_confirm;
    private MyHandler myHandler;
    private IHttpClient client;
    private ProgressBar loading;

    private static class MyHandler extends Handler {

        private SoftReference<RegisterDialog> softReference;

        public MyHandler(RegisterDialog registerDialog) {
            softReference = new SoftReference<>(registerDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RegisterDialog registerDialog = softReference.get();
            if (registerDialog == null) return;
            switch (msg.what) {
                case BaseResponse.STATE_SUC_CODE://注册成功，自动登录
                    registerDialog.showRegisterSuc();
                    break;
                case SERVICE_ERROR:
                    ToastUtils.showShort("登陆失败");
                    registerDialog.serviceErr();
                    break;
                case LOGIN_SUC:
                    registerDialog.showLoginSuc();
                    break;
            }
        }
    }

    private void serviceErr() {
        loading.setVisibility(View.GONE);
        tips.setVisibility(View.VISIBLE);
        tips.setTextColor(getContext()
                .getResources()
                .getColor(R.color.error_red));
        tips.setText(getContext()
                .getString(R.string.error_server));
    }

    public void showLoginSuc() {
        dismiss();
        ToastUtils.showShort(getContext().getString(R.string.login_suc));
    }

    //
    private void showRegisterSuc() {
        loading.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.GONE);
        tips.setVisibility(View.VISIBLE);
        tips.setTextColor(getContext()
                .getResources()
                .getColor(R.color.color_text_normal));
        tips.setText(getContext()
                .getString(R.string.register_suc_and_loging));
        new Thread() {
            @Override
            public void run() {
                super.run();
                String psd = pw.getText().toString();
                Log.i("wak", phoneNumber + ":::" + psd);
                IRequest request = new BaseRequest(Api.Config.getDomain() + Api.AUTH_URL);
                request.setBody("phone", phoneNumber);
                request.setBody("passwrod", psd);
                IResponse response = client.post(request, false);
                Log.i("wak", response.getData());
                if (response.getCode() == BaseResponse.STATE_SUC_CODE) {
                    LoginResponse loginResponse = new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (loginResponse.getCode() == BaseResponse.STATE_SUC_CODE) {//保存账户信息到本地
                        Account account = loginResponse.data;
                        SharedPreferenceManager.save(SharedPreferenceManager.ACCOUNT_KEY, account);
                        myHandler.sendEmptyMessage(LOGIN_SUC);
                    } else {
                        myHandler.sendEmptyMessage(SERVICE_ERROR);
                    }
                } else {
                    myHandler.sendEmptyMessage(SERVICE_ERROR);
                }
            }
        }.start();
    }

    public RegisterDialog(@NonNull Context context, String phoneNumber) {
        this(context, R.style.Dialog);
        this.phoneNumber = phoneNumber;
        myHandler = new MyHandler(this);
        client = new OkHttpClientImpl();
    }

    public RegisterDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_pw);
        initView();
    }

    private void initView() {
        close = findViewById(R.id.close);
        phone = findViewById(R.id.phone);
        phone.setText("你的手机号为：" + phoneNumber);
        pw = findViewById(R.id.pw);
        loading = findViewById(R.id.loading);
        pw1 = findViewById(R.id.pw1);
        tips = findViewById(R.id.tips);
        btn_confirm = findViewById(R.id.btn_confirm);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRegister();
            }
        });
    }

    private void confirmRegister() {
        String psd = this.pw.getText().toString().trim();
        if (StringUtils.isEmpty(psd)) {
            tips.setVisibility(View.VISIBLE);
            tips.setText(getContext().getResources().getString(R.string.password_is_null));
        }
        String psd1 = this.pw.getText().toString().trim();
        if (!StringUtils.equals(psd, psd1)) {
            tips.setVisibility(View.VISIBLE);
            tips.setText(getContext().getResources().getString(R.string.password_is_not_equal));
            return;
        }
        final String password = pw.getText().toString();
        new Thread() {
            @Override
            public void run() {
                super.run();
                IRequest request = new BaseRequest(Api.Config.getDomain() + Api.REGISTER_URL);
                request.setBody("phone", phoneNumber);
                request.setBody("password", password);
                request.setBody("uid", System.currentTimeMillis() + "");
                IResponse response = client.post(request, false);
                Log.i("wak", "result:" + response.getData());
                if (response.getCode() == BaseResponse.STATE_SUC_CODE) {
                    CommonBean commonBean = new Gson().fromJson(response.getData(), CommonBean.class);
                    if (commonBean.getCode() == BaseResponse.STATE_SUC_CODE) {
                        myHandler.sendEmptyMessage(BaseResponse.STATE_SUC_CODE);
                    } else {
                        myHandler.sendEmptyMessage(SERVICE_ERROR);
                    }
                } else {
                    myHandler.sendEmptyMessage(SERVICE_ERROR);
                }
            }
        }.start();
    }
}
