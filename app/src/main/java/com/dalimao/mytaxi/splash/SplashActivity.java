package com.dalimao.mytaxi.splash;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.dalimao.mytaxi.main.MainActivity;
import com.dalimao.mytaxi.R;

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
}
