package com.dalimao.mytaxi.main.bean;

import com.dalimao.mytaxi.common.http.bean.CommonBean;
import com.dalimao.mytaxi.map.bean.LocationInfo;

import java.util.List;

public class DivResponse extends CommonBean {
  public   List<LocationInfo> data;
}
