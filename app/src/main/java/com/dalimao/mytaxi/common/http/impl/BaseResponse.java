package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IResponse;

public class BaseResponse implements IResponse {

    private int code;
    private String data;

    public void setCode(int code) {
        code = code;
    }

    public void setData(String data) {
        data = data;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getData() {
        return data;
    }
}
