package com.dalimao.mytaxi.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.dalimao.mytaxi.map.bean.LocationInfo;
import com.dalimao.mytaxi.util.SensorEventHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GaoDeMapLayerImpl implements IMapLayer {
    private Context mContext;
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mMapLocationChangeListener;
    private String KEY_MY_MARKERE = "100";
    private CommonLocationChangeListener mLocationChangeListener;
    private MyLocationStyle myLocationStyle;
    private SensorEventHelper mSensorHelper;
    // 管理地图标记集合
    private Map<String, Marker> markerMap = new HashMap<>();
    private LocationSource.OnLocationChangedListener mListener;
    private boolean mFirstFix = true;
    private String mCity;

    public GaoDeMapLayerImpl(Context context) {
        this.mContext = context;
        mapView = new MapView(mContext);
        aMap = mapView.getMap();
    }

    @Override
    public View getMapView() {
        return mapView;
    }

    @Override
    public void setLocationChangeListener(CommonLocationChangeListener listener) {
        this.mLocationChangeListener = listener;
    }

    @Override
    public void setLocationRes(int res) {
        // 自定义系统定位小蓝点
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(res));// 设置小蓝点的图标//R.drawable.location_marker

        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        //myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
    }

    @Override
    public void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap) {
        Marker storedMarker = markerMap.get(locationInfo.key);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bitmap);
        if (storedMarker != null) {
            storedMarker.setPosition(new LatLng(locationInfo.latitude, locationInfo.longitude));
            storedMarker.setRotateAngle(locationInfo.rotation);
        } else {
            MarkerOptions options = new MarkerOptions();
            options.icon(des);
            options.anchor(0.5f, 0.5f);
            options.position(new LatLng(locationInfo.latitude, locationInfo.longitude));
            Marker marker = aMap.addMarker(options);
            marker.setRotateAngle(locationInfo.rotation);
            markerMap.put(locationInfo.key, marker);
            if (mSensorHelper != null && locationInfo.key.equals(KEY_MY_MARKERE))
                mSensorHelper.setCurrentMarker(marker);//定位图标旋转
        }

    }

    @Override
    public void onCreate(Bundle state) {
        mapView.onCreate(state);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setUpMap();
        mSensorHelper = new SensorEventHelper(mContext);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener listener) {
                mListener = listener;
                if (mlocationClient == null) {
                    mlocationClient = new AMapLocationClient(mContext);
                    mLocationOption = new AMapLocationClientOption();
                    //设置定位监听
                    mlocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation amapLocation) {
                            if (mListener != null && amapLocation != null) {
                                if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点  没有这一句点击定位按钮无效
                                    // Log.i("wak", "来吧？");
                                    //定位成功后把当前城市获取出来
                                    mCity = amapLocation.getCity();
                                    LocationInfo location = new LocationInfo(amapLocation.getLatitude(), amapLocation.getLongitude());
                                    location.name = amapLocation.getPoiName();
                                    //  location.name = amapLocation.getAddress();

                                    location.key = KEY_MY_MARKERE;
                                    if (mFirstFix) {
                                        mFirstFix = false;
                                        Log.i("wak", amapLocation.getLocationDetail() + ":::" + amapLocation.getDistrict() + ":::" + amapLocation.getStreet());
                                        CameraUpdate up = CameraUpdateFactory
                                                .newCameraPosition(new CameraPosition(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()),
                                                        18, 30, 0));
                                        aMap.moveCamera(up);
                                        if (mLocationChangeListener != null)
                                            mLocationChangeListener.onLocation(location);

                                    } else {

                                        if (mLocationChangeListener != null)
                                            mLocationChangeListener.onLocationChanged(location);
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

            @Override
            public void deactivate() {
                mListener = null;
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;

            }
        });// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    @Override
    public void onResume() {
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
            mlocationClient = null;
        }

        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
    }

    @Override
    public String getCity() {
        return mCity;
    }

    @Override
    public void queryPoiAddress(String input, final SearchAddressListener listener) {
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        InputtipsQuery inputquery = new InputtipsQuery(input, "");
        //inputquery.setCityLimit(true);//限制在当前城市
        Inputtips inputTips = new Inputtips(mContext, inputquery);
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> list, int rCode) {
                if (rCode == 1000) {
                    List<LocationInfo> locationInfos = new ArrayList<LocationInfo>();
                    LocationInfo locationInfo;
                    for (Tip tip : list) {
                        locationInfo = new LocationInfo(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
                        locationInfo.name = tip.getName();
                        locationInfos.add(locationInfo);
                    }
                    listener.searchComplete(locationInfos);
                } else {
                    listener.searchError(rCode);
                }

            }
        });
        inputTips.requestInputtipsAsyn();
    }
}
