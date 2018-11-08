package com.dalimao.mytaxi.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dalimao.mytaxi.TaxiApplication;
import com.google.gson.Gson;

public class SharedPreferenceManager {

    public static final String ACCOUNT_KEY = "account";
    private static SharedPreferences sharedPreferences;
    private static final String TAG = "SharedPreferencesDao";

    private SharedPreferenceManager() {

    }

    private static SharedPreferences getInstance() {

        if (sharedPreferences == null) {
            synchronized (SharedPreferenceManager.class) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TaxiApplication.getInstance().getApplicationContext());
            }
        }
        return sharedPreferences;
    }


    public static void save(String key, Object o) {
        String value = new Gson().toJson(o);
        save(key, value);
    }

    public static void save(String key, String value) {
        getInstance().edit().putString(key, value).commit();
    }

    /**
     * 读取 k-v
     */
    public static String get(String key) {

        return getInstance().getString(key, null);
    }

    /**
     * 读取对象
     */
    public static Object get(String key, Class cls) {

        String value = get(key);
        try {
            if (value != null) {
                Object o = new Gson().fromJson(value, cls);
                return o;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }
}
