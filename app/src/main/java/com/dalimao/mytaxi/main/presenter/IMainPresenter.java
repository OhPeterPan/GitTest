package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.map.bean.LocationInfo;

public interface IMainPresenter {

    void sendNetNearLocation(double latitude, double longitude);

    void sendNetUploadMyLocation(LocationInfo locationInfo);

    void callDriver(String key, float mCost, LocationInfo startLocationInfo, LocationInfo endLocationInfo);
}
