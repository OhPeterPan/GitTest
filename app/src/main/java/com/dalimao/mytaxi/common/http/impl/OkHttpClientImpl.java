package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpClientImpl implements IHttpClient {
    OkHttpClient client = new OkHttpClient.Builder().build();

    @Override
    public IResponse get(IRequest request, boolean forceCache) {
        Map<String, String> header = request.getHeader();
        Request.Builder builder = new Request.Builder();
        if (header != null) {
            for (String key : header.keySet()) {
                builder.addHeader(key, header.get(key).toString());
            }
        }
        System.out.println(request.getUrl());
        builder.url(request.getUrl()).get();
        Request okRequest = builder.build();
        //client.newCall(okRequest).execute();
        return execute(okRequest);
    }

    private IResponse execute(Request request) {
        BaseResponse commonResponse = new BaseResponse();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            System.out.println(result);
            commonResponse.setCode(response.code());
            commonResponse.setData(result);
        } catch (IOException e) {
            e.printStackTrace();
            commonResponse.setCode(IResponse.STATE_ERROR_CODE);
            commonResponse.setData(e.getMessage());
        }

        return commonResponse;
    }

    @Override
    public IResponse post(IRequest request, boolean forceCache) {
        String mediaType = "application/json;charset=utf-8";
        MediaType JSON = MediaType.parse(mediaType);
        RequestBody requestBody = RequestBody.create(JSON, request.getBody().toString());

        Map<String, String> header = request.getHeader();
        Request.Builder builder = new Request.Builder();
        for (String key :
                header.keySet()) {
            builder.addHeader(key, header.get(key));
        }
        Request okRequest = builder.url(request.getUrl()).post(requestBody).build();
        return execute(okRequest);
    }
}
