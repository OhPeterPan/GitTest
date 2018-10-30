package com.dalimao.mytaxi;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpTest {
    @Test
    public void getRequest() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        Request request = new Request.Builder().url("http://httpbin.org/get?id=id").build();
        OkHttpClient httpClient = client.build();
        try {
            Response response = httpClient.newCall(request).execute();
            //  Log.i("wak", response.body().string());
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void postRequest() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        String contentType = "application/json; charset=utf-8";
        MediaType JSON = MediaType.parse(contentType);
        RequestBody body = RequestBody.create(JSON, "\"name\":\"wuaoke\"}");
        Request request = new Request.Builder().url("http://httpbin.org/post").post(body).build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println("post:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void onInterceptor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                long startTime = System.currentTimeMillis();
                Request request = chain.request();
                Response response = chain.proceed(request);
                long endTime = System.currentTimeMillis();
                System.out.println("时间：" + (endTime - startTime));
                return response;
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();


        String mediaType = "application/json; charset=utf-8";
        MediaType JSON = MediaType.parse(mediaType);
        RequestBody body = RequestBody.create(JSON, "{\"name\":\"wuaoke\"}");
        Request request = new Request.Builder().url("http://httpbin.org/post")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void cache() {
        Cache cache = new Cache(new File("cache.cache"), 1024 * 1024);
/*        OkHttpClient client = new OkHttpClient.Builder().cache(cache).build();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, "{\"name\":\"wuaoke\"}");*/
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        try {
            Response response = client.newCall(request).execute();
            Response cacheResponse = response.cacheResponse();
            Response networkResponse = response.networkResponse();
            if (cacheResponse != null) {
                System.out.println("缓存");
            }
            if (networkResponse != null) {
                System.out.println("网络");
            }
            System.out.println("result：" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
