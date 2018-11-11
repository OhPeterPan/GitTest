package com.dalimao.mytaxi.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
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
import com.dalimao.mytaxi.util.SensorEventHelper;
import com.dalimao.mytaxi.util.SharedPreferenceManager;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements LocationSource {

    private IHttpClient client;
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private SensorEventHelper sensorEventHelper;
    private Marker mLocMarker;
    private boolean mFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

        sensorEventHelper = new SensorEventHelper(this);
        sensorEventHelper.registerSensorListener();
        client = new OkHttpClientImpl();
        checkLoginState();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标

        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        //myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        sensorEventHelper.unRegisterSensorListener();
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
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation amapLocation) {
                    if (mListener != null && amapLocation != null) {
                        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                            mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                            LatLng latLng = new LatLng(amapLocation.getLatitude(),
                                    amapLocation.getLongitude());
                            CameraUpdate update = CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition(latLng, 18, 30, 30));
                            aMap.moveCamera(update);

                            if (mFirst) {
                                addMarker(latLng);
                                sensorEventHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                            } else {

                            }

                        } else {
                            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                            Log.e("AmapErr", errText);
                        }
                    }
                }
            });
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }


    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        //mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
