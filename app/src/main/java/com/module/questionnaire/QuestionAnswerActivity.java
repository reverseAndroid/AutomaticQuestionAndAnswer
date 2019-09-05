package com.module.questionnaire;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;
import com.module.questionnaire.adapter.AddFormViewAdapter;
import com.module.questionnaire.adapter.AddRecyclerViewAdapter;
import com.module.questionnaire.adapter.AddRegionalChoiceViewAdapter;
import com.module.questionnaire.adapter.MainAdapter;
import com.module.questionnaire.adapter.MultipleSelectionViewAdapter;
import com.module.questionnaire.bean.ContactBean;
import com.module.questionnaire.bean.MultipleSelectionBean;
import com.module.questionnaire.bean.QuestionAnswerBean;
import com.module.questionnaire.bean.RadioRecyclerViewBean;
import com.module.questionnaire.bean.RegionalChoiceBean;
import com.module.questionnaire.bean.response.AnswerResponse;
import com.module.questionnaire.bean.response.BootPlanResponse;
import com.module.questionnaire.bean.response.DecisionMakingResponse;
import com.module.questionnaire.bean.response.QuestionResponse;
import com.module.questionnaire.utils.GetAddressUtil;
import com.module.questionnaire.utils.GlideEngine;
import com.module.questionnaire.utils.LogUtils;
import com.module.questionnaire.utils.SPUtils;
import com.module.questionnaire.utils.http.ApiRetrofit;
import com.module.questionnaire.utils.http.Constant;
import com.module.questionnaire.utils.http.NewApiRetrofit;
import com.module.questionnaire.widget.VoiceView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class QuestionAnswerActivity extends AppCompatActivity implements View.OnClickListener, MainAdapter.ItemUpdateListener {

    private Toolbar mToolbar;
    private TextView mTextTitle;
    private FrameLayout mFrameRefresh;
    private NestedScrollView mNestedScrollView;
    private ImageView mImageCustomerServiceAvatar;
    private TextView mTextCustomerServiceName;
    private RatingBar mRatingBar;
    private TextView mTextCustomerServiceJob;
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearLayout;
    private MainAdapter mMainAdapter;

    //引导方案
    private BootPlanResponse mBootPlanResponse;
    private List<List<Integer>> mBootPlanList;
    //多维决策
    private DecisionMakingResponse mDecisionMakingResponse;
    //问题List
    private QuestionResponse mQuestionResponse;
    //答案List
    private AnswerResponse mAnswerResponse;
    //整合
    private List<QuestionAnswerBean> mList = new ArrayList<>();
    //当前是第几个问题item了
    private int mIndex;
    //答案id，目前只有获取通讯录和获取位置需要
    private String mAnswerId;
    //地区所有数据
    private List<RegionalChoiceBean> mAllRegionalChoiceList = new ArrayList<>();
    //地区第一级数据
    private List<RegionalChoiceBean> mFirstList = new ArrayList<>();
    //当前地区View展示的数据
    private List<RegionalChoiceBean> mCurrentShowList = new ArrayList<>();
    //获取到的用户通讯录
    private List<ContactBean> mContactList = new ArrayList<>();
    //用户跳转通讯录时的输入框对象
    private EditText mEditPhone;
    private Uri mUri;
    private MediaRecorder mMediaRecorder;
    //判断是否开始录音
    private boolean mIsRecording = false;
    //录音计时
    private float mTimer;
    //如果有好几种定位提供方式，当前只取第一次定位到的地址，后面的不管
    private boolean mIsLocation = false;
    //判断是否是最后一个问题
    private boolean mIsOver;
    private long mExitTime;

    private static final int WRITE_EXTERNAL_STORAGE = 1000;
    private static final int OPEN_ALBUM = 1001;
    private static final int CAMERA = 1002;
    private static final int OPEN_CAMERA = 1003;
    private static final int RECORD_AUDIO = 1004;
    private static final int OPEN_FILE = 1005;
    private static final int GPS = 1006;
    private static final int READ_CONTACTS = 1007;
    private static final int SELECTION_CONTACT = 1008;

    private static final int MSG_AUDIO_HAS_PREPARED = 101;
    private static final int MSG_VOLUME_UPDATED = 102;
    private static final int MSG_DIALOG_DISMISS = 103;
    // 最大录音时长1000*60*10;
    private static final int MAX_LENGTH = 1000 * 60 * 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);
        StatusBarUtil.setColor(this, Color.WHITE, 0);
        //修改状态栏字体颜色变成黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //暂时放在这里
        initAppConfig();
        initTitle();
        initView();
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
                        initData();
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }

    private void initTitle() {
        mToolbar = findViewById(R.id.layout_title_tb);
        mToolbar.setNavigationOnClickListener(v -> finish());
        mTextTitle = findViewById(R.id.layout_title_title_tv);
        mFrameRefresh = findViewById(R.id.layout_title_right_fl);
        mFrameRefresh.setOnClickListener(this);
    }

    private void initView() {
        mNestedScrollView = findViewById(R.id.main_nsv);
        mImageCustomerServiceAvatar = findViewById(R.id.main_customer_service_avatar_iv);
        mTextCustomerServiceName = findViewById(R.id.main_customer_service_name_tv);
        mRatingBar = findViewById(R.id.main_customer_service_evaluation_rb);
        mTextCustomerServiceJob = findViewById(R.id.main_customer_service_job_tv);
        mRecyclerView = findViewById(R.id.main_conversation_rv);
        mLinearLayout = findViewById(R.id.main_answer_option_ll);

        Glide.with(this).load(R.drawable.icon_main_customer_service_avatar).apply(new RequestOptions().circleCrop().placeholder(R.mipmap.ic_launcher)).into(mImageCustomerServiceAvatar);
        mRatingBar.setRating(4);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void initData() {
        //获取问题列表
        Map<String, String> params = new HashMap<>();
        params.put("groupid", "1");
        NewApiRetrofit.getInstance().getQuestionList(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(questionResponse -> {
                    if (questionResponse.isSuccess()) {
                        mQuestionResponse = questionResponse;
                        startAnswerListTask();
                        startRegionalChoiceTask();
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));

        //获取引导方案
        Map<String, String> params1 = new HashMap<>();
        params1.put("access_token", SPUtils.getInstance().getString(Constant.TOKEN));
        params1.put("type", "1");
        NewApiRetrofit.getInstance().getBootPlan(params1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bootPlanResponse -> {
                    if (bootPlanResponse.isSuccess()) {
                        mBootPlanResponse = bootPlanResponse;
                        mBootPlanList = new Gson().fromJson(bootPlanResponse.getData().getContent(), new TypeToken<List<List<Integer>>>() {
                        }.getType());
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));

        //获取多维决策
        NewApiRetrofit.getInstance().getDecisionMaking()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(decisionMakingResponse -> {
                    if (decisionMakingResponse.isSuccess()) {
                        mDecisionMakingResponse = decisionMakingResponse;
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }

    //获取答案列表
    private void startAnswerListTask() {
        NewApiRetrofit.getInstance().getAnswerList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(answerResponse -> {
                    if (answerResponse.isSuccess()) {
                        mAnswerResponse = answerResponse;
                        setLoadAdapter();

                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }

    //加载主页adapter
    private void setLoadAdapter() {
        QuestionAnswerBean bean = new QuestionAnswerBean();
        bean.setId(mQuestionResponse.getData().get(mIndex).getId());
        bean.setType(getQuestionType(mQuestionResponse.getData().get(mIndex).getType()));
        bean.setLabel(mQuestionResponse.getData().get(mIndex).getLabel());
        mList.add(bean);
        mMainAdapter = new MainAdapter(this, mList);
        mRecyclerView.setAdapter(mMainAdapter);
        mMainAdapter.setItemUpdateListener(this);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    //获取地区数据
    private void startRegionalChoiceTask() {
        Map<String, String> params = new HashMap<>();
        params.put("pid", "0");
        NewApiRetrofit.getInstance().getRegionalChoice(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(regionalChoiceResponse -> {
                    if (regionalChoiceResponse.isSuccess()) {
                        mAllRegionalChoiceList = regionalChoiceResponse.getData();
                        for (RegionalChoiceBean bean : regionalChoiceResponse.getData()) {
                            if (bean.getPid().equals("0")) {
                                mFirstList.add(bean);
                            }
                        }
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.layout_title_right_fl) {
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
            mList.clear();
            mMainAdapter.refreshAdapter();
            mMainAdapter = null;
            setLoadAdapter();
        }
    }

    /**
     * onQuestionInteraction是作为问答方数据加载完毕后，通知回答方做出反应的接口
     * (就是用于处理得到问题后，做出答案类型判断并展示出相应UI)
     * id=问题id，需要问题id来取出答案的bean
     */
    @Override
    public void onQuestionInteraction(String id) {
        switch (mQuestionResponse.getData().get(mIndex).getType()) {
            case 0:
                addEditTextView(id);
                break;
            case 1:
                //这里数据不全，如果数据全了，不需要这里的else
                if (mAnswerResponse.getData().get(id) != null) {
                    if (mAnswerResponse.getData().get(id).size() == 2) {
                        addRadioView(id);
                    } else {
                        addRecyclerView(id);
                    }
                } else {
                    updateMainAdapter(getTestId(mIndex), getAnswerType(0), "测试", false);
                }
                break;
            case 2:
                addMultipleSelectionView(id);
                break;
            case 3:
                addUploadPhotoView();
                break;
            case 4:
                addUploadFileView();
                break;
            case 5:
                addAudioVoiceView(id);
                break;
            case 6:
                addCustomizeSignatureView(id);
                break;
            case 7:
                uploadContact(id);
                break;
            case 8:
                uploadPosition(id);
                break;
            case 9:
                addRegionalChoiceView(id);
                break;
            case 10:
                addTimeSelectView(id);
                break;
            case 11:
                //返回11，直接往下继续走，由于上一步还是问题类型，mIndex没有自增，只是滚动了NestScrollView没有让mIndex自增，这里的mIndex还是上一步的，所以需要加个1
                updateMainAdapter(mQuestionResponse.getData().get(mIndex + 1).getId(), getQuestionType(mQuestionResponse.getData().get(mIndex + 1).getType()), mQuestionResponse.getData().get(mIndex + 1).getLabel(), false);
                break;
            case 12:
                addAudioVoiceView(id);
                break;
            case 13:
                addFormView(id);
                break;
            case 14:
                String url = mQuestionResponse.getData().get(mIndex).getComments().toString();
                Intent intent = new Intent(QuestionAnswerActivity.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
                break;
            case 15:
                startLoanContractTask(mQuestionResponse.getData().get(mIndex));
                break;
            default:
                break;
        }
        updateNestScrollView(true);
    }

    /**
     * onAnswerInteraction是作为回答方数据加载完毕后，通知问答方做出反应的接口
     * (在用户回答答案后，用于继续问下一个问题)
     */
    @Override
    public void onAnswerInteraction() {
        startReportAnswer();

        //这里判断当前问题状态，如果问题是启用，正常往下走，如果问题是禁用，那么mIndex就自增，然后继续判断自增后的问题是否禁用
        //由于mIndex不能超过mQuestionResponse.getData().size()，所以，加个判断，防止下标越界
        while (!getCurrentQuestionStatus()) {
            mIndex++;
            if (mIndex > mQuestionResponse.getData().size() - 1) {
                mIndex--;
                break;
            }
        }

        //判断当前的最后一个item id是否和倒数第二个相等，如果相等，判断为最后一个问题，设置mIsOver为true，这样，下次就不会再继续问问题了
        if (mList.get(mList.size() - 1).getId().equals(mList.get(mList.size() - 2).getId())) {
            mIsOver = true;
        } else {
            if (!mIsOver) {
                updateMainAdapter(mQuestionResponse.getData().get(mIndex).getId(), getQuestionType(mQuestionResponse.getData().get(mIndex).getType()), mQuestionResponse.getData().get(mIndex).getLabel(), false);
            }
        }
        updateNestScrollView(false);
    }

    //根据BootPlanResponse返回的数组上报答案
    private void startReportAnswer() {
//        boolean isNeedReportAnswer = false;
//        for (int i = 0; i < mBootPlanList.size(); i++) {
//            if (mBootPlanList.get(i).get(mBootPlanList.get(i).size() - 1).equals(mQuestionResponse.getData().get(mIndex).getId())) {
//                isNeedReportAnswer = true;
//            }
//        }
//
//        if (isNeedReportAnswer) {
//            List<>
//            for (int i = 0; i < mList.size(); i++) {
//                if (mList.get(i).getType().contains("回答")) {
//
//                }
//            }
//
//            Map<String, String> params = new HashMap<>();
//            params.put("access_token", SPUtils.getInstance().getString(Constant.TOKEN));
//            params.put("answer", );
//            NewApiRetrofit.getInstance().reportAnswer()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe();
//        }
    }

    //获取当前问题的策略(开启还是关闭)
    private boolean getCurrentQuestionStatus() {
        //当前答案id是否在策略中
        boolean isDecisionMakingIn = false;
        int index = 0;
        //当前问题是否开启(默认所有问题都是开启的)
        boolean isEnable = true;
        //判断mList最后一个id是否在mDecisionMakingResponse.getData().get(i).getAnswer_ids()中
        //如果在的话，然后判断mDecisionMakingResponse.getData().get(i).getAnswer_ids()的长度是不是只有一个，如果只有一个，直接跳出循环
        //如果mDecisionMakingResponse.getData().get(i).getAnswer_ids()的长度不只有一个，那么就把所有的答案id拿出来，进行判断
        //看是不是答案id在mDecisionMakingResponse.getData().get(i).getAnswer_ids()集合中
        //如果mDecisionMakingResponse.getData().get(i).getAnswer_ids()集合被包含于答案id中，那么就可以继续往下走了
        for (int i = 0; i < mDecisionMakingResponse.getData().size(); i++) {
            if (mDecisionMakingResponse.getData().get(i).getAnswer_ids().contains(mList.get(mList.size() - 1).getId())) {
                if (mDecisionMakingResponse.getData().get(i).getAnswer_ids().size() == 1) {
                    isDecisionMakingIn = true;
                    index = i;
                    break;
                } else {
                    List<Integer> list = new ArrayList<>();
                    for (int j = 0; j < mList.size(); j++) {
                        if (mList.get(j).getType().contains("回答")) {
                            list.add(mList.get(j).getId());
                        }
                    }

                    if (list.containsAll(mDecisionMakingResponse.getData().get(i).getAnswer_ids())){
                        isDecisionMakingIn = true;
                        index = i;
                        break;
                    }
                }
            }
        }

        if (isDecisionMakingIn) {
            if (mDecisionMakingResponse.getData().get(index).getClose_questions().contains(mQuestionResponse.getData().get(mIndex).getId())) {
                isEnable = false;
            }

            if (mDecisionMakingResponse.getData().get(index).getOpen_questions().contains(mQuestionResponse.getData().get(mIndex).getId())) {
                isEnable = true;
            }
        }
        return isEnable;
    }

    //动态添加输入框的EditTextView
    private void addEditTextView(String id) {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_edit_view, null);
        ImageView imageView = view.findViewById(R.id.main_edit_view_determine_iv);
        EditText editText = view.findViewById(R.id.main_edit_view_input_et);
        editText.setHint(mQuestionResponse.getData().get(mIndex).getLabel());
        editText.setOnFocusChangeListener((view1, b) -> {
            if (b) {
                imageView.setImageResource(R.drawable.icon_edit_send);
            } else {
                imageView.setImageResource(R.drawable.icon_edit_unsend);
            }
        });

        imageView.setOnClickListener(view12 -> {
            if (!TextUtils.isEmpty(editText.getText())) {
                if (mAnswerResponse.getData().get(id) != null) {
                    updateMainAdapter(mAnswerResponse.getData().get(id).get(0).getId(), getAnswerType(0), editText.getText().toString(), false);
                } else {
                    updateMainAdapter(getTestId(mIndex), getAnswerType(0), editText.getText().toString(), false);
                }
            } else {
                Toast.makeText(QuestionAnswerActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        mLinearLayout.addView(view);
    }

    //动态添加单选的RecyclerView
    private void addRecyclerView(String id) {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_recycler_view, null);
        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view_rv);
        TextView textConfirm = view.findViewById(R.id.main_recycler_view_confirm_tv);
        LinearLayoutManager layoutManagerInfo = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerInfo);
        layoutManagerInfo.setOrientation(OrientationHelper.VERTICAL);

        List<RadioRecyclerViewBean> list = new ArrayList<>();
        for (int i = 0; i < mAnswerResponse.getData().get(id).size(); i++) {
            RadioRecyclerViewBean bean = new RadioRecyclerViewBean();
            bean.setId(mAnswerResponse.getData().get(id).get(i).getId());
            bean.setValue(mAnswerResponse.getData().get(id).get(i).getLabel());
            bean.setSelect(false);
            list.add(bean);
        }

        AddRecyclerViewAdapter adapter = new AddRecyclerViewAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemListener((isChecked, position) -> {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setSelect(false);
            }

            list.get(position).setSelect(isChecked);
            adapter.holderList.clear();
            adapter.notifyDataSetChanged();
        });

        textConfirm.setOnClickListener(view1 -> {
            Integer answerId = null;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelect()) {
                    answerId = list.get(i).getId();
                }
            }

            updateMainAdapter(answerId, getAnswerType(0), list.get(adapter.selectionItem()).getValue(), false);
        });

        mLinearLayout.addView(view);
    }

    //动态添加单选的RadioView
    private void addRadioView(String id) {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_radio_view, null);
        TextView textLeft = view.findViewById(R.id.main_radio_view_left_tv);
        textLeft.setText(mAnswerResponse.getData().get(id).get(0).getLabel());
        textLeft.setOnClickListener(view1 -> updateMainAdapter(mAnswerResponse.getData().get(id).get(0).getId(), getAnswerType(0), textLeft.getText().toString(), false));

        TextView textRight = view.findViewById(R.id.main_radio_view_right_tv);
        textRight.setText(mAnswerResponse.getData().get(id).get(1).getLabel());
        textRight.setOnClickListener(view12 -> updateMainAdapter(mAnswerResponse.getData().get(id).get(1).getId(), getAnswerType(0), textRight.getText().toString(), false));
        mLinearLayout.addView(view);
    }

    //动态添加多选的MultipleSelectionView
    private void addMultipleSelectionView(String id) {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_multiple_selection_view, null);
        RecyclerView recyclerView = view.findViewById(R.id.main_multiple_selection_rv);
        TextView textView = view.findViewById(R.id.main_multiple_selection_confirm_tv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // TODO: 2019/9/4 0004 暂时这样处理，因为数据不全
        List<MultipleSelectionBean> list = new ArrayList<>();
        if (mAnswerResponse.getData().get(id) != null) {
            for (int i = 0; i < mAnswerResponse.getData().get(id).size(); i++) {
                MultipleSelectionBean bean = new MultipleSelectionBean();
                bean.setId(mAnswerResponse.getData().get(id).get(i).getId());
                bean.setTitle(mAnswerResponse.getData().get(id).get(i).getLabel());
                bean.setSelect(false);
                list.add(bean);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                MultipleSelectionBean bean = new MultipleSelectionBean();
                bean.setId(i);
                bean.setTitle("多选项" + i);
                bean.setSelect(false);
                list.add(bean);
            }
        }

        MultipleSelectionViewAdapter adapter = new MultipleSelectionViewAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((i, isSelection) -> {
            list.get(i).setSelect(isSelection);
            adapter.notifyItemChanged(i);
        });

        textView.setOnClickListener(view1 -> {
            List<String> dataList = new ArrayList<>();
            Integer answerId = null;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelect()) {
                    dataList.add(list.get(i).getTitle());
                    answerId = list.get(i).getId();
                }
            }

            updateMainAdapter(answerId, getAnswerType(0), new Gson().toJson(dataList), false);
        });

        mLinearLayout.addView(view);
    }

    //动态添加上传图片的UploadView
    private void addUploadPhotoView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_upload_photo_view, null);
        LinearLayout linearAlbum = view.findViewById(R.id.main_upload_photo_view_album_ll);
        LinearLayout linearCamera = view.findViewById(R.id.main_upload_photo_view_camera_ll);

        linearAlbum.setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            } else {
                Matisse.from(this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(1)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .capture(false)
                        .imageEngine(new GlideEngine())
                        .forResult(OPEN_ALBUM);
            }
        });

        linearCamera.setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA);
            } else {
                openCamera();
            }
        });

        mLinearLayout.addView(view);
    }

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), "/question/images/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mUri = FileProvider.getUriForFile(this, "com.module.questionnaire.FileProvider", file);
        } else {
            mUri = Uri.fromFile(file);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(intent, OPEN_CAMERA);
    }

    //动态添加上传文件的UploadFileView
    private void addUploadFileView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_upload_file_view, null);
        LinearLayout linearFile = view.findViewById(R.id.main_upload_file_view_file_ll);

        linearFile.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, OPEN_FILE);
        });

        mLinearLayout.addView(view);
    }

    //动态添加录音的AudioVoiceView
    private void addAudioVoiceView(String id) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }

        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_audio_voice_view, null);
        VoiceView voiceView = view.findViewById(R.id.main_audio_voice_view_animation_vv);
        ImageView imageView = view.findViewById(R.id.main_audio_voice_view_iv);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/question/audio/" + System.currentTimeMillis() + ".mp4");
        imageView.setOnTouchListener((view1, motionEvent) -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
            } else {
                startAudioVoice(id, voiceView, motionEvent, file);
            }
            return true;
        });
        mLinearLayout.addView(view);
    }

    //开始长按录音
    private void startAudioVoice(String id, VoiceView voiceView, MotionEvent motionEvent, File file) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mMediaRecorder == null) {
                    mMediaRecorder = new MediaRecorder();
                }
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();

                    /* ②setAudioSource/setVedioSource */
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
                    /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    /*
                     * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
                     * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
                     */
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    /* ③准备 */
                    mMediaRecorder.setOutputFile(file.getAbsolutePath());
                    mMediaRecorder.setMaxDuration(MAX_LENGTH);
                    mMediaRecorder.prepare();
                    /* ④开始 */
                    mMediaRecorder.start();
                    mHandlerTiming.sendEmptyMessage(MSG_AUDIO_HAS_PREPARED);
                    voiceView.setVisibility(View.VISIBLE);
                    voiceView.startAnimation();
                } catch (IllegalStateException | IOException e) {
                    e.getMessage();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsRecording || mTimer < 1f) {
                    //当录制时长过短时，会走这里
                    Toast.makeText(this, "录音时间太短", Toast.LENGTH_SHORT).show();
                    file.delete();

                    try {
                        mMediaRecorder.stop();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        mMediaRecorder = null;
                    }
                    voiceView.setVisibility(View.INVISIBLE);
                    voiceView.stopAnimation();
                } else {
                    //正常录制结束
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;

                    voiceView.setVisibility(View.INVISIBLE);
                    voiceView.stopAnimation();

                    showVoiceHandleDialog(id, file);
                }

                mIsRecording = false;
                mTimer = 0;
                break;
            default:
                break;
        }
    }

    private void showVoiceHandleDialog(String id, File file) {
        Dialog dialog = new Dialog(this, R.style.ThemeLight);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_voice_handle, null);
        dialog.setContentView(contentView);
        dialog.setCancelable(false);

        try {
            //将dialog本身的黑色背景变成透明色
            ViewGroup parent = (ViewGroup) contentView.getParent();
            parent.setBackgroundResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = mLinearLayout.getMeasuredHeight();
        window.setAttributes(layoutParams);
        dialog.show();

        FrameLayout framePlayer = contentView.findViewById(R.id.dialog_voice_handle_player_fl);
        FrameLayout frameSend = contentView.findViewById(R.id.dialog_voice_handle_send_fl);
        FrameLayout frameCancel = contentView.findViewById(R.id.dialog_voice_handle_cancel_fl);

        framePlayer.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!mediaPlayer.isPlaying()) {
                framePlayer.setBackgroundResource(R.drawable.icon_voice_stop);
                mediaPlayer.start();
            } else {
                framePlayer.setBackgroundResource(R.drawable.icon_voice_player);
                mediaPlayer.reset();
                mediaPlayer.stop();
            }
        });

        frameCancel.setOnClickListener(view -> {
            file.delete();
            dialog.dismiss();
        });

        frameSend.setOnClickListener(view -> {
            dialog.dismiss();
            if (mAnswerResponse.getData().get(id) != null) {
                updateMainAdapter(mAnswerResponse.getData().get(id).get(0).getId(), getAnswerType(2), file.getAbsolutePath(), false);
            } else {
                updateMainAdapter(getTestId(mIndex), getAnswerType(2), file.getAbsolutePath(), false);
            }
        });
    }

    //判断当前触摸范围是否在长按说话图片内
    private boolean isInScope(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getRawX();
        int y = (int) motionEvent.getRawY();

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        return y >= top && y <= bottom && x >= left && x <= right;
    }

    //动态添加自定义签名的CustomizeSignatureView
    private void addCustomizeSignatureView(String id) {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_customize_signature_view, null);
        SignaturePad signaturePad = view.findViewById(R.id.main_customize_signature_view_sp);
        FrameLayout frameClear = view.findViewById(R.id.main_customize_signature_view_clear_fl);
        TextView textSubmit = view.findViewById(R.id.main_customize_signature_view_submit_tv);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                frameClear.setEnabled(true);
                textSubmit.setEnabled(true);
            }

            @Override
            public void onClear() {
                frameClear.setEnabled(false);
                textSubmit.setEnabled(false);
            }
        });

        frameClear.setOnClickListener(view1 -> signaturePad.clear());
        textSubmit.setOnClickListener(view12 -> {
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), signaturePad.getSignatureBitmap(), null, null));
            if (mAnswerResponse.getData().get(id) != null) {
                updateMainAdapter(mAnswerResponse.getData().get(id).get(0).getId(), getAnswerType(1), uri.toString(), false);
            } else {
                updateMainAdapter(getTestId(mIndex), getAnswerType(1), uri.toString(), false);
            }
        });

        mLinearLayout.addView(view);
    }

    //上传通讯录
    private void uploadContact(String id) {
        mAnswerId = id;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
        } else {
            readContacts(id);
        }
    }

    //读取联系人
    private void readContacts(String id) {
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactBean bean = new ContactBean();
                    bean.setName(displayName);
                    bean.setPhone(number);
                    mContactList.add(bean);
                }
            }

            if (mAnswerResponse.getData().get(id) != null) {
                updateMainAdapter(mAnswerResponse.getData().get(id).get(0).getId(), getAnswerType(0), "已经上传通讯录", false);
            } else {
                updateMainAdapter(getTestId(mIndex), getAnswerType(0), "已经上传通讯录", false);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //上传定位
    private void uploadPosition(String id) {
        mAnswerId = id;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS);
        } else {
            //获取地理位置管理器
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //获取所有可用的位置提供器
            List<String> providers = locationManager.getProviders(true);
            int counter = 0;
            for (String provider : providers) {
                counter++;
                Location location = locationManager.getLastKnownLocation(provider);
                setLatitudeLongitudeToLocation(id, location);
                if (counter == providers.size() && location == null) {
                    updateMainAdapter(null, getAnswerType(0), "获取不到您的定位，请检查网络或者GPS是否开启", false);
                }
            }
        }
    }

    //经纬度转换成地点
    private void setLatitudeLongitudeToLocation(String id, Location location) {
        if (location != null && !mIsLocation) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String position = GetAddressUtil.getAddress(this, latitude, longitude);

            if (mAnswerResponse.getData().get(id) != null) {
                updateMainAdapter(mAnswerResponse.getData().get(id).get(0).getId(), getAnswerType(0), position, false);
            } else {
                updateMainAdapter(getTestId(mIndex), getAnswerType(0), position, false);
            }

            mIsLocation = true;
        }
    }

    //动态添加选择省市区的RegionalChoiceView
    private void addRegionalChoiceView(String answerId) {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_regional_choice_view, null);
        TextView textFirst = view.findViewById(R.id.main_regional_choice_view_first_tv);
        View viewFirst = view.findViewById(R.id.main_regional_choice_view_first_v);

        TextView textSecond = view.findViewById(R.id.main_regional_choice_view_second_tv);
        View viewSecond = view.findViewById(R.id.main_regional_choice_view_second_v);

        TextView textThird = view.findViewById(R.id.main_regional_choice_view_third_tv);
        View viewThird = view.findViewById(R.id.main_regional_choice_view_third_v);

        RecyclerView recyclerView = view.findViewById(R.id.main_regional_choice_view_rv);
        LinearLayoutManager layoutManagerInfo = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerInfo);
        layoutManagerInfo.setOrientation(OrientationHelper.VERTICAL);
        mCurrentShowList = mFirstList;
        AddRegionalChoiceViewAdapter adapter = new AddRegionalChoiceViewAdapter(this, mCurrentShowList);
        recyclerView.setAdapter(adapter);
        List<String> list = new ArrayList<>();
        adapter.setOnItemListener((id, value) -> {
            //选择分区的逻辑是，用一个recyclerView展示，选择一级地区后，清空list，将二级地区遍历出来，然后刷新adapter，三级也是一样
            list.add(value);
            if (list.size() == 1) {
                List<RegionalChoiceBean> secondList = new ArrayList<>();
                for (RegionalChoiceBean bean : mAllRegionalChoiceList) {
                    if (bean.getPid().equals(id)) {
                        secondList.add(bean);
                    }
                }

                mCurrentShowList.clear();
                mCurrentShowList.addAll(secondList);

                textFirst.setText("已选择");
                textFirst.setTextColor(getResources().getColor(R.color.regional_choice_text));
                viewFirst.setVisibility(View.INVISIBLE);

                textSecond.setText("选择分区");
                textSecond.setTextColor(getResources().getColor(R.color.black));
                textSecond.setVisibility(View.VISIBLE);
                viewSecond.setVisibility(View.VISIBLE);
            } else if (list.size() == 2) {
                List<RegionalChoiceBean> thirdList = new ArrayList<>();
                for (RegionalChoiceBean bean : mAllRegionalChoiceList) {
                    if (bean.getPid().equals(id)) {
                        thirdList.add(bean);
                    }
                }

                mCurrentShowList.clear();
                mCurrentShowList.addAll(thirdList);

                textSecond.setText("已选择");
                textSecond.setTextColor(getResources().getColor(R.color.regional_choice_text));
                viewSecond.setVisibility(View.INVISIBLE);

                if (mCurrentShowList.size() == 0) {
                    StringBuilder text = new StringBuilder();
                    for (String s : list) {
                        text.append(s);
                    }

                    if (mAnswerResponse.getData().get(id) != null) {
                        updateMainAdapter(mAnswerResponse.getData().get(answerId).get(0).getId(), getAnswerType(0), text.toString(), false);
                    } else {
                        updateMainAdapter(getTestId(mIndex), getAnswerType(0), text.toString(), false);
                    }

                } else {
                    textThird.setText("选择分区");
                    textThird.setTextColor(getResources().getColor(R.color.black));
                    textThird.setVisibility(View.VISIBLE);
                    viewThird.setVisibility(View.VISIBLE);
                }
            } else if (list.size() == 3) {
                StringBuilder text = new StringBuilder();
                for (String s : list) {
                    text.append(s);
                }

                if (mAnswerResponse.getData().get(id) != null) {
                    updateMainAdapter(mAnswerResponse.getData().get(answerId).get(0).getId(), getAnswerType(0), text.toString(), false);
                } else {
                    updateMainAdapter(getTestId(mIndex), getAnswerType(0), text.toString(), false);
                }
            }

            adapter.notifyDataSetChanged();
        });

        mLinearLayout.addView(view);
    }

    //动态添加表单的FormView
    private void addFormView(String id) {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_form_view, null);
        List<QuestionResponse.DataBean> list = analyzeJson(mQuestionResponse.getData().get(mIndex).getComments());
        RecyclerView recyclerView = view.findViewById(R.id.main_form_view_rv);
        LinearLayoutManager layoutManagerInfo = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerInfo);
        layoutManagerInfo.setOrientation(OrientationHelper.VERTICAL);
        AddFormViewAdapter adapter = new AddFormViewAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemListener((editText, value) -> {
            mEditPhone = null;
            mEditPhone = editText;
            jumpContact();
        });

        TextView textConfirm = view.findViewById(R.id.main_form_view_confirm_tv);
        textConfirm.setOnClickListener(view1 -> {
            if (adapter.getAllEditText().equals("")) {
                Toast.makeText(QuestionAnswerActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mAnswerResponse.getData().get(id) != null) {
                updateMainAdapter(mAnswerResponse.getData().get(id).get(0).getId(), getAnswerType(0), adapter.getAllEditText(), false);
            } else {
                updateMainAdapter(getTestId(mIndex), getAnswerType(0), adapter.getAllEditText(), false);
            }
        });

        mLinearLayout.addView(view);
    }

    //解析QuestionResponse里面的comments
    private List<QuestionResponse.DataBean> analyzeJson(Object json) {
        String data = json.toString();
        List<QuestionResponse.DataBean> list = new Gson().fromJson(data, new TypeToken<List<QuestionResponse.DataBean>>() {
        }.getType());
        return list;
    }

    //跳转到通讯录选择联系人
    private void jumpContact() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, SELECTION_CONTACT);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            Uri uri = Uri.parse("content://contacts");
            intent.setData(uri);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, SELECTION_CONTACT);
        }
    }

    //请求当前借款合约内容
    private void startLoanContractTask(QuestionResponse.DataBean dataBean) {
        String url = dataBean.getComments().toString();
        NewApiRetrofit.getInstance().getLoanContract(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    if (baseResponse.isSuccess()) {
                        if (mIndex == mQuestionResponse.getData().size() - 1) {
                            updateMainAdapter(dataBean.getId(), getQuestionType(dataBean.getType()), baseResponse.getData(), true);
                        } else {
                            updateMainAdapter(getTestId(dataBean.getId()), getQuestionType(dataBean.getType()), baseResponse.getData(), true);
                        }
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandlerTiming = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_HAS_PREPARED:
                    mIsRecording = true;
                    new Thread(() -> {
                        while (mIsRecording) {
                            try {
                                Thread.sleep(100);
                                mTimer += 0.1f;
                                mHandlerTiming.sendEmptyMessage(MSG_VOLUME_UPDATED);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case MSG_VOLUME_UPDATED:
                    break;
                case MSG_DIALOG_DISMISS:
                    break;
                default:
                    break;
            }
        }
    };

    //动态添加时间选择器TimeSelectView
    private void addTimeSelectView(String id) {
        mLinearLayout.setVisibility(View.VISIBLE);
        TimePickerView timePickerView = new TimePickerBuilder(this, (date, v) -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String time = format.format(date);

            if (mAnswerResponse.getData().get(id) != null) {
                updateMainAdapter(mAnswerResponse.getData().get(id).get(0).getId(), getAnswerType(0), time, false);
            } else {
                updateMainAdapter(getTestId(mIndex), getAnswerType(0), time, false);
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .build();

        Dialog mDialog = timePickerView.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            timePickerView.getDialogContainerLayout().setLayoutParams(params);
            mDialog.setCancelable(false);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                //修改动画样式
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);
                //改成Bottom,底部显示
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialogWindow.setDimAmount(0.1f);
            }
        }

        timePickerView.findViewById(R.id.btnCancel).setVisibility(View.INVISIBLE);
        timePickerView.show(mLinearLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null && requestCode != OPEN_CAMERA) {
                Toast.makeText(this, "请重新选择", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = null;
            if (requestCode != OPEN_CAMERA) {
                uri = data.getData();
            }

            switch (requestCode) {
                case OPEN_ALBUM:
                    List<Uri> pathList = Matisse.obtainResult(data);
                    String path = pathList.get(0).toString();
                    updateMainAdapter(getTestId(mIndex - 1), getAnswerType(1), path, false);
                    break;
                case OPEN_CAMERA:
                    updateMainAdapter(getTestId(mIndex - 1), getAnswerType(1), mUri.toString(), false);
                    break;
                case OPEN_FILE:
                    updateMainAdapter(getTestId(mIndex - 1), getAnswerType(1), uri.toString(), false);
                    break;
                case SELECTION_CONTACT:
                    //获取内容解析者
                    ContentResolver resolver = getContentResolver();
                    //查询数据
                    Cursor cursor = resolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    if (cursor.moveToFirst()) {
                        String phone = cursor.getString(0);
                        mEditPhone.setText(phone);
                    }
                    cursor.close();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您可以选择相册了", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您将不能选择相册", Toast.LENGTH_SHORT).show();
                }
                break;
            case CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您可以进行拍照了", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您将不能进行拍照", Toast.LENGTH_SHORT).show();
                    updateMainAdapter(null, getAnswerType(0), "您将不能进行拍照", false);
                }
                break;
            case RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您可以开始发送语音了", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您将不能发送语音", Toast.LENGTH_SHORT).show();
                }
                break;
            case GPS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您已经获取定位权限了", Toast.LENGTH_SHORT).show();
                    uploadPosition(mAnswerId);
                } else {
                    Toast.makeText(this, "您将不能获取定位", Toast.LENGTH_SHORT).show();
                    updateMainAdapter(null, getAnswerType(0), "您没有同意上报位置", false);
                }
                break;
            case READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您已经获取通讯录权限了", Toast.LENGTH_SHORT).show();
                    uploadContact(mAnswerId);
                } else {
                    Toast.makeText(this, "您将不能获取通讯录", Toast.LENGTH_SHORT).show();
                    updateMainAdapter(null, getAnswerType(0), "您没有同意获取通讯录", false);
                }
                break;
            case SELECTION_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您已经获取通讯录权限了", Toast.LENGTH_SHORT).show();
                    jumpContact();
                } else {
                    Toast.makeText(this, "您将不能获取电话号码", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void updateMainAdapter(Integer id, String type, String label, boolean isContinuous) {
        QuestionAnswerBean bean = new QuestionAnswerBean();
        bean.setId(id);
        bean.setType(type);
        bean.setLabel(label);
        bean.setContinuous(isContinuous);
        mList.add(bean);
        mMainAdapter.notifyItemInserted(mList.size());
        mLinearLayout.removeAllViews();
        mLinearLayout.setVisibility(View.GONE);
    }

    //返回问题类型
    private String getQuestionType(int type) {
        String typeText;
        switch (type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 14:
                typeText = "文本问题";
                break;
            case 5:
            case 12:
                typeText = "语音问题";
                break;
            case 13:
                typeText = "文本问题";
                break;
            case 15:
                typeText = "接口问题";
                break;
            default:
                typeText = "文本问题";
                break;

        }
        return typeText;
    }

    //返回回答类型
    private String getAnswerType(int type) {
        String typeText;
        switch (type) {
            case 0:
                typeText = "文本回答";
                break;
            case 1:
                typeText = "图片回答";
                break;
            case 2:
                typeText = "语音回答";
                break;
            default:
                typeText = "文本回答";
                break;

        }
        return typeText;
    }

    //addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快，但是如果立即调用fullScroll，view可能还没有显示出来，所以会失败，应该通过handler在新线程中更新。
    private void updateNestScrollView(boolean isIncrement) {
        if (isIncrement && mIndex < mQuestionResponse.getData().size() - 1) {
            mIndex++;
        }
        Handler handler = new Handler();
        handler.post(() -> {
            mNestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
            handler.removeCallbacksAndMessages(null);
        });
    }

    //临时加的，因为目前，部分数据没有答案id，拿问题id来代替,并*1000，以作区分
    private int getTestId(int index) {
        return mQuestionResponse.getData().get(index).getId() * 1000;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent motionEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, motionEvent);
    }
}
