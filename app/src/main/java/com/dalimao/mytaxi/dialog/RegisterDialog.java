package com.dalimao.mytaxi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dalimao.mytaxi.R;

import java.lang.ref.SoftReference;

public class RegisterDialog extends Dialog {
    private String phoneNumber;
    private ImageView close;
    private TextView phone;
    private EditText pw;
    private EditText pw1;
    private TextView tips;
    private Button btn_confirm;
    private MyHandler myHandler;

    private static class MyHandler extends Handler {

        private SoftReference<RegisterDialog> softReference;

        public MyHandler(RegisterDialog registerDialog) {
            softReference = new SoftReference<>(registerDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RegisterDialog registerDialog = softReference.get();


        }
    }

    public RegisterDialog(@NonNull Context context, String phoneNumber) {
        this(context, R.style.Dialog);
        this.phoneNumber = phoneNumber;
        myHandler = new MyHandler(this);
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

    }
}
