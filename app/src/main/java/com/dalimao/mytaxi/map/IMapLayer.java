package com.dalimao.mytaxi.map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.dalimao.mytaxi.map.bean.LocationInfo;

import java.util.List;

public interface IMapLayer {
    /**
     * 获取地图
     */
    View getMapView();

    /**
     * 设置位置变化监听
     */
    void setLocationChangeListener(CommonLocationChangeListener listener);

    /**
     * 设置定位图标
     */
    void setLocationRes(int res);

    /**
     * 添加更新标记点 包括位置，角度（可通过id识别）
     */
    void addOrUpdateMarker(LocationInfo info, Bitmap bitmap);

    /**
     * 生命周期函数
     */

    void onCreate(Bundle state);

    void onResume();

    void onSaveInstanceState(Bundle outState);

    void onPause();

    void onDestroy();

    String getCity();

    void queryPoiAddress(String input, SearchAddressListener listener);

    interface SearchAddressListener {
        void searchComplete(List<LocationInfo> locationInfo);

        void searchError(int code);
    }

    /**
     * Created by liuguangli on 17/5/30.
     */
    interface CommonLocationChangeListener {
        void onLocationChanged(LocationInfo locationInfo);//除了首次定位后的位置更新回调

        void onLocation(LocationInfo locationInfo);//首次定位回调
    }
}
