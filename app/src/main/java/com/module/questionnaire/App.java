package com.module.questionnaire;

import android.app.Application;
import android.content.Context;

import com.module.questionnaire.utils.LogUtils;
import com.module.questionnaire.utils.SPUtils;
import com.module.questionnaire.utils.http.ApiRetrofit;
import com.module.questionnaire.utils.http.Constant;
import com.module.questionnaire.utils.http.NewApiRetrofit;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class App extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initAppConfig();
    }

    private void initAppConfig() {
        String url = "http://console.anxinabc.com/api_v2/config/appConfig";
        ApiRetrofit.getInstance().getAppConfig(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appConfigResponse -> {
                    if (appConfigResponse.isSuccess()) {
                        Constant.URL = appConfigResponse.getData().getUrl();
                        initToken();
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }

    private void initToken() {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", "18993195341");
        params.put("password", "123456");
        NewApiRetrofit.getInstance().login(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    if (loginResponse.isSuccess()) {
                        SPUtils.getInstance().put(Constant.TOKEN, loginResponse.getData().getAccess_token());

                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }
}
