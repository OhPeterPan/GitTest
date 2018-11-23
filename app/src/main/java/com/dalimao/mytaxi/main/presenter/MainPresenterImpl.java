package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.main.bean.DivResponse;
import com.dalimao.mytaxi.main.bean.Order;
import com.dalimao.mytaxi.main.bean.OrderStateResponse;
import com.dalimao.mytaxi.main.manager.IMainManager;
import com.dalimao.mytaxi.main.view.IMainView;
import com.dalimao.mytaxi.map.bean.LocationInfo;
import com.dalimao.mytaxi.rx.RegisterBus;

public class MainPresenterImpl implements IMainPresenter {

    private IMainView view;
    private IMainManager mainManager;
    private Order mCurrentOrder;

    public MainPresenterImpl(IMainView view, IMainManager mainManager) {
        this.view = view;
        this.mainManager = mainManager;
    }

    @Override
    public void sendNetNearLocation(double latitude, double longitude) {
        mainManager.sendNetNearDivLocation(latitude, longitude);
    }

    @Override
    public void sendNetUploadMyLocation(LocationInfo locationInfo) {
        mainManager.sendNetUploadMyLocation(locationInfo);
    }

    @Override
    public void callDriver(String key, float mCost, LocationInfo startLocationInfo, LocationInfo endLocationInfo) {
        mainManager.sendNetCallDriver(key, mCost, startLocationInfo, endLocationInfo);
    }

    @Override
    public void cancelOrder() {
        if (mCurrentOrder != null) {
            mainManager.cancelOrder(mCurrentOrder);
        } else {
            view.cancelOrderFail();
        }
    }

    @RegisterBus
    public void getNearDivResponse(DivResponse response) {
        view.getNearDivResponse(response);
    }

    @RegisterBus
    public void updateDivLocation(LocationInfo locationInfo) {
        view.updateDivLocation(locationInfo);
    }

    @RegisterBus
    public void callDriverCallback(OrderStateResponse response) {
        if (response.state == OrderStateResponse.CALL_DIVER_STATE) {//呼叫司机
            mCurrentOrder = response.data;
            view.callDriverCallback(response);
        } else if (response.state == OrderStateResponse.CANCEL_ORDER_STATE) {//取消订单
            view.cancelOrderSuc(response);
        }
    }

    //联网出现问题使用这个
    @RegisterBus
    public void commResponse(IResponse response) {

    }


}
