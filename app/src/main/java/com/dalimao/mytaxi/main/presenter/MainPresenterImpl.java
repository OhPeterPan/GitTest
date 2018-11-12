package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.main.bean.DivResponse;
import com.dalimao.mytaxi.main.manager.IMainManager;
import com.dalimao.mytaxi.main.view.IMainView;
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

    @RegisterBus
    public void getNearDivResponse(DivResponse response) {
        view.getNearDivResponse(response);
    }
}
