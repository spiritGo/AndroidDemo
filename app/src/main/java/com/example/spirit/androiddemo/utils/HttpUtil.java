package com.example.spirit.androiddemo.utils;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    private static HttpUtil httpUtil;
    private final OkHttpClient client;
    private final Gson gson;

    private HttpUtil() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public static HttpUtil getHttpUtil() {
        if (httpUtil == null) {
            synchronized (HttpUtil.class) {
                if (httpUtil == null) {
                    httpUtil = new HttpUtil();
                }
            }
        }
        return httpUtil;
    }

    public <T> ArrayList<T> getNetData(Class<T> tClass, String city) throws IOException {
        ArrayList<T> ts = new ArrayList<>();
        Request request = new Request.Builder()
                .url(UrlUtil.WEATHER + city)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String string = response.body().string();
            try {
                JSONObject jsonObject = new JSONObject(string);
                int status = (int) jsonObject.get("status");
                System.out.println(string);
                if (status == 200) {
                    SPUtil.putString(SPUtil.WEATHER_INFO, string);
                } else {
                    SPUtil.putBoolean(SPUtil.FAILED, true);
                }

                String info = SPUtil.getString(SPUtil.WEATHER_INFO, "");
                if (!info.equals("")) {
                    ts.add(gson.fromJson(info, tClass));
                } else {
                    ts.add(gson.fromJson(string, tClass));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ts;
        }
        return null;
    }
}
