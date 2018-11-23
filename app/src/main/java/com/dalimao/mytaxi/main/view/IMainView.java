package com.dalimao.mytaxi.main.view;

import com.dalimao.mytaxi.main.bean.DivResponse;
import com.dalimao.mytaxi.main.bean.OrderStateResponse;
import com.dalimao.mytaxi.map.bean.LocationInfo;

public interface IMainView extends IView {
    void getNearDivResponse(DivResponse response);

    void updateDivLocation(LocationInfo locationInfo);

    void callDriverCallback(OrderStateResponse response);
}
