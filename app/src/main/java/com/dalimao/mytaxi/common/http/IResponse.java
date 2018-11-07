package com.dalimao.mytaxi.common.http;

public interface IResponse {
    public static final int STATE_ERROR_CODE = 100001;
    public static final int STATE_SUC_CODE = 200;

    public static final int REGISTER_SUC_CODE = 100002;
    public static final int REGISTER_FAIL_CODE = 100003;

    void setCode(int code);

    void setData(String data);

    int getCode();

    String getData();
}
