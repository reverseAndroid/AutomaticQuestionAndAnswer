package com.module.questionnaire.utils.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.module.questionnaire.bean.response.AnswerResponse;
import com.module.questionnaire.bean.response.BaseResponse;
import com.module.questionnaire.bean.response.BootPlanResponse;
import com.module.questionnaire.bean.response.DecisionMakingResponse;
import com.module.questionnaire.bean.response.LoginResponse;
import com.module.questionnaire.bean.response.MeListResponse;
import com.module.questionnaire.bean.response.QuestionResponse;
import com.module.questionnaire.bean.response.RegionalChoiceResponse;
import com.module.questionnaire.utils.Constant;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import rx.Completable;
import rx.Observable;

public class NewApiRetrofit extends BaseApiRetrofit {

    public Api mApi;
    public static NewApiRetrofit mInstance;

    private NewApiRetrofit() {
        super();
        Gson gson = new GsonBuilder().setLenient().create();

        //在构造方法中完成对Retrofit接口的初始化
        mApi = new Retrofit.Builder()
                .baseUrl(Constant.URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(Api.class);
    }

    public static NewApiRetrofit getInstance() {
        if (mInstance == null) {
            synchronized (NewApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new NewApiRetrofit();
                }
            }
        }
        return mInstance;
    }

    @Multipart
    private RequestBody getJsonRequestBody(Object obj) {
        String route = new Gson().toJson(obj);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), route);
        return body;
    }

    @Multipart
    private Map<String, RequestBody> getFormRequestBody(Map<String, String> requestDataMap) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), requestDataMap.get(key) == null ? "" : requestDataMap.get(key));
            requestBodyMap.put(key, requestBody);
        }
        return requestBodyMap;
    }

    public Observable<LoginResponse> login(Map<String, String> params) {
        return mApi.login(getJsonRequestBody(params));
    }


    public Observable<QuestionResponse> getQuestionList(Map<String, String> params) {
        return mApi.getQuestionList(getJsonRequestBody(params));
    }

    public Observable<AnswerResponse> getAnswerList() {
        return mApi.getAnswerList();
    }

    public Observable<RegionalChoiceResponse> getRegionalChoice(Map<String, String> params) {
        return mApi.getRegionalChoice(getJsonRequestBody(params));
    }

    public Observable<BaseResponse> getLoanContract(String url) {
        return mApi.getLoanContract(url);
    }

    public Observable<BootPlanResponse> getBootPlan(Map<String, String> params) {
        return mApi.getBootPlan(getFormRequestBody(params));
    }

    public Observable<DecisionMakingResponse> getDecisionMaking() {
        return mApi.getDecisionMaking();
    }

    public Observable<MeListResponse> getMeList(Map<String, String> params) {
        return mApi.getMeList(getFormRequestBody(params));
    }

    public Observable<Object> register(Map<String, String> params) {
        return mApi.register(getJsonRequestBody(params));
    }
}
