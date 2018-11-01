package com.dalimao.mytaxi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.RegexUtils;
import com.dalimao.mytaxi.R;

public class PhoneInoutDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private EditText phone;
    private Button btn_next;
    private ImageView close;

    public PhoneInoutDialog(@NonNull Context context) {
        this(context, R.style.Dialog);
        this.context = context;
    }

    public PhoneInoutDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.dialog_phone_input, null);
        setContentView(view);
        initView();
    }

    private void initView() {
        phone = findViewById(R.id.phone);
        close = findViewById(R.id.close);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        close.setOnClickListener(this);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changePhoneState();
            }
        });
    }

    private void changePhoneState() {
        String phoneNumber = phone.getText().toString().trim();
        btn_next.setEnabled(RegexUtils.isMobileSimple(phoneNumber));
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.btn_next:
                String phoneNumber = phone.getText().toString().trim();
                VerifyInputDialog verifyInputDialog = new VerifyInputDialog(context, phoneNumber);
                verifyInputDialog.show();
                break;
            case R.id.close:
                break;

        }
    }
}
