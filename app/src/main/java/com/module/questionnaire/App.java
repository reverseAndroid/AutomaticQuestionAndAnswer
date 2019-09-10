package com.module.questionnaire;

import android.app.Application;
import android.content.Context;

import com.module.questionnaire.utils.LogUtils;
import com.module.questionnaire.utils.SPUtils;
import com.module.questionnaire.utils.http.ApiRetrofit;
import com.module.questionnaire.utils.Constant;
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
        ApiRetrofit.getInstance().getAppConfig(Constant.BASE_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appConfigResponse -> {
                    if (appConfigResponse.isSuccess()) {
                        Constant.URL = appConfigResponse.getData().getApi_url();
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }
}
