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
import com.dalimao.mytaxi.map.bean.LocationInfo;
import com.dalimao.mytaxi.util.SensorEventHelper;

import java.util.HashMap;
import java.util.Map;

public class GaoDeMapLayerImpl implements IMapLayer {
    private Context mContext;
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mMapLocationChangeListener;
    private String KEY_MY_MARKERE = "100";
    private boolean firstLocation = true;
    private CommonLocationChangeListener mLocationChangeListener;
    private MyLocationStyle myLocationStyle;
    private SensorEventHelper sensorEventHelper;
    private Marker mLocMarker;
    // 管理地图标记集合
    private Map<String, Marker> markerMap = new HashMap<>();

    public GaoDeMapLayerImpl(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mapView = new MapView(mContext);
        aMap = mapView.getMap();

        mlocationClient = new AMapLocationClient(mContext);
        mLocationOption = new AMapLocationClientOption();
        //setUpMap();

        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mlocationClient.startLocation();

        sensorEventHelper = new SensorEventHelper(mContext);
        sensorEventHelper.registerSensorListener();
    }

    @Override
    public View getMapView() {
        return mapView;
    }

    @Override
    public void setLocationChangeListener(CommonLocationChangeListener listener) {
        this.mLocationChangeListener = listener;
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        if (myLocationStyle != null) {
            aMap.setMyLocationStyle(myLocationStyle);
        }

        // 设置地图激活（加载监听）
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mMapLocationChangeListener = onLocationChangedListener;
                Log.d("wak", "activate");
            }

            @Override
            public void deactivate() {

                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
            }
        });
        // 设置默认定位按钮是否显示，这里先不想业务使用方开放
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false，这里先不想业务使用方开放
        aMap.setMyLocationEnabled(true);
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

        Marker storedMarker = markerMap.get(locationInfo.id);
        LatLng latLng = new LatLng(locationInfo.latitude,
                locationInfo.longitude);
        if (storedMarker != null) {
            // 如果已经存在则更新角度、位置
            storedMarker.setPosition(latLng);
            storedMarker.setRotateAngle(locationInfo.rotation);
        } else {
            // 如果不存在则创建
            MarkerOptions options = new MarkerOptions();
            BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bitmap);
            options.icon(des);
            options.anchor(0.5f, 0.5f);
            options.position(latLng);
            Marker marker = aMap.addMarker(options);
            marker.setRotateAngle(0.0f);
            markerMap.put(locationInfo.id, marker);
            if (locationInfo.id.equals(KEY_MY_MARKERE)) {
                // 传感器控制我的位置标记的旋转角度
                sensorEventHelper.setCurrentMarker(marker);
            }
        }

    }

    @Override
    public void onCreate(Bundle state) {
        mapView.onCreate(state);
        setUpMap();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        setUpLocation();
    }


    public void setUpLocation() {

        //设置监听器
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                // 定位变化位置
                if (mMapLocationChangeListener != null) {
                    // 地图已经激活，通知蓝点实时更新
                    mMapLocationChangeListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                    Log.d("wak", "onLocationChanged");
                    LocationInfo locationInfo = new LocationInfo(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    locationInfo.name = aMapLocation.getPoiName();
                    locationInfo.id = KEY_MY_MARKERE;
                    if (firstLocation) {
                        firstLocation = false;
                        LatLng latLng = new LatLng(aMapLocation.getLatitude(),
                                aMapLocation.getLongitude());

                        CameraUpdate up = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                latLng, 18, 30, 30));
                        aMap.moveCamera(up);
                        if (mLocationChangeListener != null) {

                            mLocationChangeListener.onLocation(locationInfo);
                        }

                    }
                    if (mLocationChangeListener != null) {

                        mLocationChangeListener.onLocationChanged(locationInfo);
                    }
                }
            }
        });
        mlocationClient.startLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        mlocationClient.stopLocation();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        mlocationClient.onDestroy();
        sensorEventHelper.unRegisterSensorListener();
    }
}
