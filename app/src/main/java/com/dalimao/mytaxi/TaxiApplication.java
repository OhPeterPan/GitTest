package com.dalimao.mytaxi;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

public class TaxiApplication extends Application {
    private static TaxiApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Utils.init(this);
    }

    public static TaxiApplication getInstance() {
        return application;
    }
}
