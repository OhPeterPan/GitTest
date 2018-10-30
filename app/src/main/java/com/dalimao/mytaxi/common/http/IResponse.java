package com.dalimao.mytaxi.common.http;

public interface IResponse {
    public static final int STATE_ERROR_CODE = 100001;

    void setCode(int code);

    void setData(String data);

    int getCode();

    String getData();
}
