package com.dalimao.mytaxi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dalimao.corelibrary.VerificationCodeInput;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.Api;
import com.dalimao.mytaxi.common.http.bean.CommonBean;
import com.dalimao.mytaxi.common.http.bean.CommonDetailBean;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.google.gson.Gson;

import java.lang.ref.SoftReference;

public class VerifyInputDialog extends Dialog {
    private static final int GET_VERIFY_SUC = 1;
    private static final int GET_VERIFY_FAIL = -1;
    private static final int CHECK_CODE_SUC = 2;
    private static final int CHECK_CODE_FAIL = -2;
    private String phoneNumber;
    IHttpClient client;
    private Button mBtn_resend;

    private CountDownTimer mCountDownTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            String time = getContext().getResources().getString(R.string.after_time_resend);
            String result = String.format(time, millisUntilFinished / 1000);
            mBtn_resend.setEnabled(false);
            mBtn_resend.setText(result);
        }

        @Override
        public void onFinish() {
            mBtn_resend.setEnabled(true);
            mBtn_resend.setText("重新发送");
        }
    };
    private MyHandler mHandler;
    private ProgressBar mLoading;
    private TextView mError;
    private ImageView mClose;
    private VerificationCodeInput mCodeInput;

    private static class MyHandler extends Handler {

        private SoftReference<VerifyInputDialog> mSoftReference;

        public MyHandler(VerifyInputDialog dialog) {
            mSoftReference = new SoftReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            VerifyInputDialog verifyInputDialog = mSoftReference.get();
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_VERIFY_SUC:
                    verifyInputDialog.mCountDownTimer.start();
                    break;
                case GET_VERIFY_FAIL:

                    break;
                case CHECK_CODE_SUC:
                    verifyInputDialog.setCheckState(true);
                    break;
                case CHECK_CODE_FAIL:
                    verifyInputDialog.setCheckState(false);
                    break;
            }
        }
    }

    private void setCheckState(boolean state) {
        if (!state) {//验证错误
            mCodeInput.setEnabled(true);
            mLoading.setVisibility(View.GONE);
            mError.setVisibility(View.VISIBLE);
        } else {//验证成功后去判断他是否是已经注册的
            mLoading.setVisibility(View.GONE);
            mError.setVisibility(View.GONE);
        }
    }

    public VerifyInputDialog(@NonNull Context context, String phone) {
        this(context, R.style.Dialog);
        this.phoneNumber = phone;
        client = new OkHttpClientImpl();
        mHandler = new MyHandler(this);
    }

    public VerifyInputDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.dialog_smscode_input, null);
        setContentView(view);
        initView();
    }

    private void initView() {
        mClose = findViewById(R.id.close);
        TextView phone = findViewById(R.id.phone);
        String testStr = getContext().getResources().getString(R.string.sms_code_send_phone);
        String result = String.format(testStr, phoneNumber);
        phone.setText(result);
        mBtn_resend = findViewById(R.id.btn_resend);
        mCodeInput = findViewById(R.id.verificationCodeInput);
        mLoading = findViewById(R.id.loading);
        mError = findViewById(R.id.error);
        sendCode();
        mCodeInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String code) {
                mLoading.setVisibility(View.VISIBLE);
                Log.i("wak", code + "::" + phoneNumber);
                checkVerify(code);
            }
        });
    }

    private void checkVerify(final String code) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String url = Api.Config.getDomain() + Api.CHECK_VERIFY_URL;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phoneNumber);
                request.setBody("code", code);
                IResponse response = client.get(request, false);
                if (response.getCode() == IResponse.STATE_SUC_CODE) {
                    CommonDetailBean commonDetailBean = new Gson().fromJson(response.getData(), CommonDetailBean.class);
                    if (commonDetailBean.getCode() == IResponse.STATE_SUC_CODE) {
                        mHandler.sendEmptyMessage(CHECK_CODE_SUC);
                    } else {
                        mHandler.sendEmptyMessage(CHECK_CODE_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(CHECK_CODE_FAIL);
                }
            }
        }.start();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCountDownTimer.cancel();
    }

    private void sendCode() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                IRequest request = new BaseRequest(Api.Config.getDomain() + Api.GET_VERIFY_URL);
                request.setBody("phone", phoneNumber);
                IResponse response = client.get(request, false);
                String result = response.getData();
                Log.i("wak", result);
                if (response.getCode() == IResponse.STATE_SUC_CODE) {
                    CommonBean commonBean = new Gson().fromJson(result, CommonBean.class);
                    if (commonBean.getCode() == IResponse.STATE_SUC_CODE) {
                        mHandler.sendEmptyMessage(GET_VERIFY_SUC);
                    } else {
                        mHandler.sendEmptyMessage(GET_VERIFY_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(GET_VERIFY_FAIL);
                }

            }
        }.start();
    }
}
