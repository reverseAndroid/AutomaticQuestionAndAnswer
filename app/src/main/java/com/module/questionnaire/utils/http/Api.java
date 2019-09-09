package com.module.questionnaire.utils.http;

import com.module.questionnaire.bean.response.AnswerResponse;
import com.module.questionnaire.bean.response.AppConfigResponse;
import com.module.questionnaire.bean.response.BaseResponse;
import com.module.questionnaire.bean.response.BootPlanResponse;
import com.module.questionnaire.bean.response.DecisionMakingResponse;
import com.module.questionnaire.bean.response.LoginResponse;
import com.module.questionnaire.bean.response.MeListResponse;
import com.module.questionnaire.bean.response.QuestionResponse;
import com.module.questionnaire.bean.response.RegionalChoiceResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2019/5/9.
 */

public interface Api {

    //请求AppConfig
    @POST
    Observable<AppConfigResponse> getAppConfig(@Url String url);

    //登录
    @POST("api/v1/login")
    Observable<LoginResponse> login(@Body RequestBody requestBody);

    //获取问题列表
    @POST("api_v4/lead/lead_question")
    Observable<QuestionResponse> getQuestionList(@Body RequestBody requestBody);

    //获取答案列表
    @POST("api_v4/lead/lead_answer")
    Observable<AnswerResponse> getAnswerList();

    //获取地区
    @POST("api_v4/lead/getAddr")
    Observable<RegionalChoiceResponse> getRegionalChoice(@Body RequestBody requestBody);

    //获取当前借款合约详情
    @POST
    Observable<BaseResponse> getLoanContract(@Url String url);

    //获取引导方案
    @Multipart
    @POST("api_v4/lead/lead_plan")
    Observable<BootPlanResponse> getBootPlan(@PartMap Map<String, RequestBody> params);

    //获取多维决策
    @POST("api_v4/lead/lead_branch")
    Observable<DecisionMakingResponse> getDecisionMaking();

    //获取我的页面列表数据
    @Multipart
    @POST("api/v1/tab")
    Observable<MeListResponse> getMeList(@PartMap Map<String, RequestBody> params);
}
