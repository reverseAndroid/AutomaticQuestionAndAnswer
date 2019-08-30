package com.module.questionnaire.utils.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;

/**
 * Created by Administrator on 2019/5/22.
 */

public class StringRetrofit extends BaseApiRetrofit {

    public Api mApi;
    public static StringRetrofit mInstance;

    private StringRetrofit() {
        super();
        //在构造方法中完成对Retrofit接口的初始化
        mApi = new Retrofit.Builder()
                       .baseUrl(Constant.BASE_URL)
                       .client(getClient())
                       .addConverterFactory(ScalarsConverterFactory.create())
                       .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                       .build()
                       .create(Api.class);
    }

    public static StringRetrofit getInstance() {
        if (mInstance == null) {
            synchronized (StringRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new StringRetrofit();
                }
            }
        }
        return mInstance;
    }
}
