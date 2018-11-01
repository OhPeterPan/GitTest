package com.dalimao.mytaxi.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.dialog.PhoneInoutDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLoginState();
    }

    private void checkLoginState() {
        boolean tookenActive = false;
        if (!tookenActive) {
            showInputPhoneDialog();
        } else {

        }
    }

    private void showInputPhoneDialog() {
        PhoneInoutDialog phoneInoutDialog = new PhoneInoutDialog(this);
        phoneInoutDialog.show();
    }
}
