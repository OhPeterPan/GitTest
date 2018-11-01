package com.dalimao.mytaxi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;

public class VerifyInputDialog extends Dialog {
    private String phoneNumber;
    IHttpClient client;


    public VerifyInputDialog(@NonNull Context context, String phone) {
        this(context, R.style.Dialog);
        this.phoneNumber = phone;
        client = new OkHttpClientImpl();
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
        ImageView close = findViewById(R.id.close);
        TextView phone = findViewById(R.id.phone);
        String testStr = getContext().getResources().getString(R.string.sms_code_send_phone);
        String result = String.format(testStr, phoneNumber);
        phone.setText(result);

        Button btn_resend = findViewById(R.id.btn_resend);
        VerificationCodeInput verificationCodeInput = findViewById(R.id.verificationCodeInput);
        ProgressBar loading = findViewById(R.id.loading);
        TextView error = findViewById(R.id.error);
        sendCode();
        verificationCodeInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String s) {

            }
        });
    }

    private void sendCode() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                IRequest request = new BaseRequest(Api.Config.getDomain() + Api.GET_VERIFY_URL);
                request.setBody("phone", phoneNumber);
                IResponse response = client.get(request, false);
                System.out.println("result:" + response.getData());
            }
        }.start();


    }
}
