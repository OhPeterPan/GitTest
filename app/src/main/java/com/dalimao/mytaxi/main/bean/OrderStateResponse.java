package com.dalimao.mytaxi.main.bean;

import com.dalimao.mytaxi.common.http.bean.CommonBean;

public class OrderStateResponse extends CommonBean {
    public static int CALL_DIVER_STATE = 1;
    public static int CANCEL_ORDER_STATE = 100;//取消订单
    public int state;
    public Order data;
}
