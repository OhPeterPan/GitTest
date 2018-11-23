package com.dalimao.mytaxi.main.manager;

import com.dalimao.mytaxi.map.bean.LocationInfo;

public interface IMainManager {
    void sendNetNearDivLocation(double latitude, double longitude);

    void sendNetUploadMyLocation(LocationInfo locationInfo);

    void sendNetCallDriver(String key, float mCost, LocationInfo startLocationInfo, LocationInfo endLocationInfo);
}
