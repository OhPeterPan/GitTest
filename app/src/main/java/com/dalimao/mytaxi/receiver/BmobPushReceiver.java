package com.dalimao.mytaxi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dalimao.mytaxi.map.bean.LocationInfo;
import com.dalimao.mytaxi.rx.RxBus;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class BmobPushReceiver extends BroadcastReceiver {
    private static final int MSG_TYPE_LOCATION = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {

            String msg = intent.getStringExtra("msg");
            try {
                JSONObject jsonObject = new JSONObject(msg);
                int type = jsonObject.optInt("type");
                if (type == MSG_TYPE_LOCATION) {
                    LocationInfo locationInfo = new Gson().fromJson(jsonObject.optString("data"), LocationInfo.class);
                    RxBus.getInstance().sendData(locationInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
