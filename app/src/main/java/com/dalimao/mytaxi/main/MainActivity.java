package com.dalimao.mytaxi.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.adapter.PoiAdapter;
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
import com.dalimao.mytaxi.main.bean.DivInfoBean;
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

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

public class MainActivity extends AppCompatActivity implements IMainView {

    private IHttpClient client;

    private FrameLayout relative_main_activity;
    private IMapLayer apLayer;
    private IMainPresenter presenter;
    private Bitmap divBitmap;
    private String mPushKey = "";
    private LocationInfo startLocationInfo;
    private TextView tvLocationCity;
    private AutoCompleteTextView startLocation;
    private AutoCompleteTextView endLocation;
    private LocationInfo startLcationInfo;
    private PoiAdapter poiAdapter;

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
                // Log.i("wak", "多次回调？");
                //  apLayer.addOrUpdateMarker(locationInfo, BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked));
            }

            @Override
            public void onLocation(LocationInfo locationInfo) {
                // Log.i("wak", "第一次回调？");
                startLcationInfo = locationInfo;
                tvLocationCity.setText(apLayer.getCity());
                startLocation.setText(startLcationInfo.name);

                apLayer.addOrUpdateMarker(locationInfo, BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked));
                startLocationInfo = locationInfo;
                nearDivLocation(locationInfo.latitude, locationInfo.longitude);
                //上报位置
                uploadMyLocation(locationInfo);
            }
        });
        apLayer.onCreate(savedInstanceState);
        //apLayer.setLocationRes(R.drawable.navi_map_gps_locked);
        initView();

        IMainManager mainManager = new MainManagerImpl();
        presenter = new MainPresenterImpl(this, mainManager);
        RxBus.getInstance().register(presenter);

        startPushService();
    }

    private void initView() {
        relative_main_activity = findViewById(R.id.mapContain);
        tvLocationCity = findViewById(R.id.tvLocationCity);
        startLocation = findViewById(R.id.startLocation);
        endLocation = findViewById(R.id.endLocation);
        relative_main_activity.addView(apLayer.getMapView());
        endLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!StringUtils.isEmpty(input)) {//去查询poi
                    apLayer.queryPoiAddress(input, new IMapLayer.SearchAddressListener() {
                        @Override
                        public void searchComplete(List<LocationInfo> results) {//得到所有的地址信息
                            // 更新列表
                            updatePoiList(results);

                        }

                        @Override
                        public void searchError(int code) {

                        }
                    });

                }
            }
        });
    }

    private void updatePoiList(final List<LocationInfo> results) {
        List<String> mData = new ArrayList<>();

        for (LocationInfo locationInfo : results) {
            mData.add(locationInfo.name);
        }
        if (poiAdapter == null) {
            poiAdapter = new PoiAdapter(this, mData);
            endLocation.setAdapter(poiAdapter);
        } else {
            poiAdapter.notifyData(mData);
        }
        endLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String end = (String) poiAdapter.getItem(position);
                endLocation.setText(end);
                final LocationInfo endLocationInfo = results.get(position);
                apLayer.clearAllMark();
                apLayer.polyline(startLocationInfo, endLocationInfo, Color.GREEN, new IMapLayer.PolylineCompleteListener() {

                    @Override
                    public void polylineComplete(DivInfoBean bean) {
                        apLayer.moveCamera(startLocationInfo, endLocationInfo);
                    }

                    @Override
                    public void polylineError(int code) {

                    }
                });
            }
        });

    }

    private void startPushService() {
        // 推送服务
        // 初始化BmobSDK
        Bmob.initialize(this, Api.Config.getApplicationKey());
        // 使用推送服务时的初始化操作
        BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);
        installation.save();
        mPushKey = installation.getInstallationId();

        // 启动推送服务
        BmobPush.startWork(this);
    }

    /**
     * 给后台上报我的位置 然后后台发通知给我
     *
     * @param locationInfo
     */
    private void uploadMyLocation(LocationInfo locationInfo) {
        locationInfo.key = mPushKey;
        presenter.sendNetUploadMyLocation(locationInfo);
    }

    private void nearDivLocation(double latitude, double longitude) {
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
            apLayer.addOrUpdateMarker(locationInfo, divBitmap);
        }
    }

    @Override
    public void updateDivLocation(LocationInfo locationInfo) {
        if (divBitmap == null || divBitmap.isRecycled())
            divBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        apLayer.addOrUpdateMarker(locationInfo, divBitmap);
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
