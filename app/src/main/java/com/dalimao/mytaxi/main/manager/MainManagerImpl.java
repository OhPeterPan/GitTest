package com.dalimao.mytaxi.main.manager;

import android.util.Log;

import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.Api;
import com.dalimao.mytaxi.common.http.bean.Account;
import com.dalimao.mytaxi.common.http.bean.CommonBean;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.main.bean.DivResponse;
import com.dalimao.mytaxi.main.bean.Order;
import com.dalimao.mytaxi.main.bean.OrderStateResponse;
import com.dalimao.mytaxi.map.bean.LocationInfo;
import com.dalimao.mytaxi.rx.RxBus;
import com.dalimao.mytaxi.util.SharedPreferenceManager;
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
                            //  Log.d("wak", "司机信息：" + response.getData());
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

    @Override
    public void sendNetUploadMyLocation(final LocationInfo locationInfo) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) {

                IRequest request = new BaseRequest(Api.Config.getDomain() + Api.UPLOAD_LOCATION_URL);
                request.setBody("latitude", new Double(locationInfo.latitude).toString());
                request.setBody("longitude", new Double(locationInfo.longitude).toString());
                request.setBody("rotation", new Float(locationInfo.rotation).toString());
                Log.i("wak", "key:" + locationInfo.key);
                request.setBody("key", locationInfo.key);

                IResponse response = client.post(request, false);
                Log.i("wak", "位置上报：" + response.getData());
                if (response.getCode() == BaseResponse.STATE_SUC_CODE)
                    return new Gson().fromJson(response.getData(), CommonBean.class);
                else
                    return null;
            }
        });
    }

    @Override
    public void sendNetCallDriver(final String key, final float mCost, final LocationInfo startLocationInfo, final LocationInfo endLocationInfo) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) {
                Account account = (Account) SharedPreferenceManager.get(SharedPreferenceManager.ACCOUNT_KEY, Account.class);
                IRequest request = new BaseRequest(Api.Config.getDomain() + Api.CALL_DRIVER_URL);
                request.setBody("key", key);
                request.setBody("uid", account.uid);
                request.setBody("phone", account.account);
                request.setBody("startLatitude",
                        new Double(startLocationInfo.latitude).toString());
                request.setBody("startLongitude",
                        new Double(startLocationInfo.longitude).toString());
                request.setBody("endLatitude",
                        new Double(endLocationInfo.latitude).toString());
                request.setBody("endLongitude",
                        new Double(endLocationInfo.longitude).toString());
                request.setBody("cost", new Float(mCost).toString());
                IResponse response = client.post(request, false);
                Log.i("wak", "呼叫司机：" + response.getData());
                if (response.getCode() == BaseResponse.STATE_SUC_CODE) {
                    OrderStateResponse orderStateResponse = new Gson().fromJson(response.getData(), OrderStateResponse.class);
                    orderStateResponse.state = OrderStateResponse.CALL_DIVER_STATE;
                    return orderStateResponse;
                } else {
                    return null;
                }
            }
        });

    }

    @Override
    public void cancelOrder(final Order mCurrentOrder) {
        RxBus.getInstance().chainProcess(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                IRequest request = new BaseRequest(Api.Config.getDomain() + Api.CANCEL_ORDER_URL);
                request.setBody("id", mCurrentOrder.orderId);
                IResponse response = client.post(request, false);
                if (response.getCode() == BaseResponse.STATE_SUC_CODE) {//连接成功
                    Log.i("wak", response.getData());
                    // return null;
                    // OrderStateResponse orderStateResponse = new Gson().fromJson(response.getData(), OrderStateResponse.class);
                    OrderStateResponse orderStateResponse = new OrderStateResponse();
                    orderStateResponse.state = OrderStateResponse.CANCEL_ORDER_STATE;
                    return orderStateResponse;
                } else {//连接失败

                    return response;
                }

            }
        });
    }
}
