package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IResponse;

public class BaseResponse implements IResponse {

    public static int STATE_TOKEN_INVALID = 100006;
    private int code;
    private String data;
    private Exception exception;

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public void setData(String data) {
        this.data = data;
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
