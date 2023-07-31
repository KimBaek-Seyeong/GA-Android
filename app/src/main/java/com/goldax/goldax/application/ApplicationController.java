package com.goldax.goldax.application;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.goldax.goldax.network.NetworkService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationController extends MultiDexApplication {
    // 어플리케이션 인스턴스 하나 선언
    private static ApplicationController INSTANCE;
    private static Context mContext;

    private NetworkService mNetworkService;
    private static final String BASE_URL = "http://13.124.64.142:3000/";

    public static ApplicationController getInstance() {
        return INSTANCE;
    }

    public NetworkService getNetworkService() {
        return mNetworkService;
    }

    public Context getApplicationContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        INSTANCE = this;

        buildService();
    }

    public void buildService() {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mNetworkService = retrofit.create(NetworkService.class);
    }
}
