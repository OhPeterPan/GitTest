package com.dalimao.mytaxi.main.manager;

import android.util.Log;

import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.Api;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.main.bean.DivResponse;
import com.dalimao.mytaxi.rx.RxBus;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.reactivex.functions.Function;

public class MainManagerImpl implements IMainManager {

    private IHttpClient client;

    public MainManagerImpl() {
        client = new OkHttpClientImpl();
    }

    @Override
    public void sendNetNearDivLocation(final double latitude, final double longitude) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                String url = Api.Config.getDomain() + Api.NEAR_DIV_URL;
                IRequest request = new BaseRequest(url);
                request.setBody("latitude", new Double(latitude).toString());
                request.setBody("longitude", new Double(longitude).toString());
                IResponse response = client.get(request, false);

                if (response.getCode() == BaseResponse.STATE_SUC_CODE) {
                    try {
                        DivResponse divResponse = new Gson().fromJson(response.getData(), DivResponse.class);
                        if (divResponse.getCode() == BaseResponse.STATE_SUC_CODE) {
                            Log.d("wak", "result：" + response.getData());
                            return divResponse;
                        }
                    } catch (JsonSyntaxException e) {
                        return null;
                    }
                }
                return null;
            }
        });
    }
}
