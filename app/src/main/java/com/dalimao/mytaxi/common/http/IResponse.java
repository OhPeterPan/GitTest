package com.dalimao.mytaxi.common.http;

public interface IResponse {
    public static final int STATE_ERROR_CODE = 100001;
    public static final int STATE_SUC_CODE = 200;

    void setCode(int code);

    void setData(String data);

    int getCode();

    String getData();
}
