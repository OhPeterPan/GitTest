package com.dalimao.mytaxi.map.bean;

public class LocationInfo {
    public String id;
    public String name;
    public double latitude;
    public double longitude;
    public float rotation;
    public LocationInfo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
