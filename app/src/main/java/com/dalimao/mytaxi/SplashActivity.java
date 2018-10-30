package com.dalimao.mytaxi;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= 21) {
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_logo);
            ImageView iv_logo = findViewById(R.id.iv_logo);
            iv_logo.setImageDrawable(animatedVectorDrawable);
            animatedVectorDrawable.start();
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                final IResponse response = new OkHttpClientImpl().get(new BaseRequest("http://httpbin.org/get?id=id"), false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("wak", response.getCode() + "我是:" + response.getData());
                    }
                });

            }
        }.start();

    }
}
