package com.dalimao.mytaxi.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.Api;
import com.dalimao.mytaxi.common.http.bean.Account;
import com.dalimao.mytaxi.common.http.bean.LoginResponse;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.dialog.PhoneInoutDialog;
import com.dalimao.mytaxi.main.bean.DivResponse;
import com.dalimao.mytaxi.main.manager.IMainManager;
import com.dalimao.mytaxi.main.manager.MainManagerImpl;
import com.dalimao.mytaxi.main.presenter.IMainPresenter;
import com.dalimao.mytaxi.main.presenter.MainPresenterImpl;
import com.dalimao.mytaxi.main.view.IMainView;
import com.dalimao.mytaxi.map.GaoDeMapLayerImpl;
import com.dalimao.mytaxi.map.IMapLayer;
import com.dalimao.mytaxi.map.bean.LocationInfo;
import com.dalimao.mytaxi.rx.RxBus;
import com.dalimao.mytaxi.util.SharedPreferenceManager;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements IMainView {

    private IHttpClient client;

    private RelativeLayout relative_main_activity;
    private IMapLayer apLayer;
    private IMainPresenter presenter;
    private Bitmap divBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClientImpl();
        checkLoginState();

        apLayer = new GaoDeMapLayerImpl(this);
        apLayer.setLocationChangeListener(new IMapLayer.CommonLocationChangeListener() {
            @Override
            public void onLocationChanged(LocationInfo locationInfo) {
                Log.i("wak", "多次回调？");
                //  apLayer.addOrUpdateMarker(locationInfo, BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked));
            }

            @Override
            public void onLocation(LocationInfo locationInfo) {
                Log.i("wak", "啥意思？");
                apLayer.addOrUpdateMarker(locationInfo, BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked));
                nearDivLocation(locationInfo.latitude, locationInfo.longitude);

            }
        });
        apLayer.onCreate(savedInstanceState);
        //apLayer.setLocationRes(R.drawable.navi_map_gps_locked);

        relative_main_activity = findViewById(R.id.relative_main_activity);
        relative_main_activity.addView(apLayer.getMapView());
        IMainManager mainManager = new MainManagerImpl();
        presenter = new MainPresenterImpl(this, mainManager);
        RxBus.getInstance().register(presenter);
    }

    private void nearDivLocation(double latitude, double longitude) {
        Log.d("wak", "去请求司机:" + latitude + ":::" + longitude);
        presenter.sendNetNearLocation(latitude, longitude);
    }

    @Override
    public void getNearDivResponse(DivResponse response) {

        if (response == null) return;
        //Log.d("wak", "司机位置:" + response.data.get(0).latitude + ":::" + response.data.get(0).longitude);
        if (divBitmap == null || divBitmap.isRecycled())
            divBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);

        LocationInfo locationInfo;
        for (int i = 0; i < response.data.size(); i++) {
            locationInfo = response.data.get(i);
            Log.d("wak", "司机位置:" + locationInfo.latitude + ":::" + locationInfo.longitude);
            apLayer.addOrUpdateMarker(locationInfo, divBitmap);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        apLayer.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        apLayer.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        apLayer.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        apLayer.onDestroy();
        RxBus.getInstance().unRegister(presenter);
    }

    private void checkLoginState() {
        boolean tookenActive = false;
        final Account account = (Account) SharedPreferenceManager.get(SharedPreferenceManager.ACCOUNT_KEY, Account.class);
        if (account != null) {
            if (account.expired > System.currentTimeMillis()) {
                tookenActive = true;
            }
        }

        if (!tookenActive) {
            showInputPhoneDialog();
        } else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String url = Api.Config.getDomain() + Api.LOGIN_URL;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token", account.token);
                    IResponse response = client.post(request, false);
                    if (response.getCode() == BaseResponse.STATE_SUC_CODE) {
                        LoginResponse loginResponse = new Gson().fromJson(response.getData(), LoginResponse.class);

                        if (loginResponse.getCode() == BaseResponse.STATE_SUC_CODE) {

                            Account account = loginResponse.data;
                            SharedPreferenceManager.save(SharedPreferenceManager.ACCOUNT_KEY, account);

                            // 通知 UI

                            ToastUtils.showShort("登录成功");
                        } else if (loginResponse.getCode() == BaseResponse.STATE_TOKEN_INVALID) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showInputPhoneDialog();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort("服务器错误");
                            }
                        });
                    }
                }
            }.start();
        }
    }

    private void showInputPhoneDialog() {
        PhoneInoutDialog phoneInoutDialog = new PhoneInoutDialog(this);
        phoneInoutDialog.show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError(Exception e) {

    }
}
