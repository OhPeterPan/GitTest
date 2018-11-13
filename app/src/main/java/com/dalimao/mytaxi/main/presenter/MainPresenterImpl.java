package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.main.bean.DivResponse;
import com.dalimao.mytaxi.main.manager.IMainManager;
import com.dalimao.mytaxi.main.view.IMainView;
import com.dalimao.mytaxi.map.bean.LocationInfo;
import com.dalimao.mytaxi.rx.RegisterBus;

public class MainPresenterImpl implements IMainPresenter {

    private IMainView view;
    private IMainManager mainManager;

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

    @RegisterBus
    public void getNearDivResponse(DivResponse response) {
        view.getNearDivResponse(response);
    }

    @RegisterBus
    public void updateDivLocation(LocationInfo locationInfo){
        view.updateDivLocation( locationInfo);
    }
}
