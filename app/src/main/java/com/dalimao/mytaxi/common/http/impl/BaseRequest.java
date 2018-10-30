package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class BaseRequest implements IRequest {
    private String method = "POST";
    private String url;
    private Map<String, String> header;
    private Map<String, Object> body;

    public BaseRequest(String url) {
        this.url = url;
        header = new HashMap<>();
        body = new HashMap<>();
        header.put("Application-Id", "myTaxiID");
        header.put("API-key", "myTaxiKey");
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setHeader(String key, String value) {
        header.put(key, value);
    }

    @Override
    public void setBody(String key, String value) {
        body.put(key, value);
    }

    @Override
    public String getUrl() {
        if (GET.equals(url)) {
            for (String key :
                    body.keySet()) {
                url = url.replace("${" + key + "}", body.get(key).toString());
            }
        }
        return url;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }

    @Override
    public Object getBody() {
        if (body != null) {
            return new Gson().toJson(this.body, HashMap.class);
        } else {
            return "{}";
        }
    }
}
