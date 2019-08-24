package com.module.questionnaire;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.module.questionnaire.adapter.AddRecyclerViewAdapter;
import com.module.questionnaire.adapter.MainAdapter;
import com.module.questionnaire.bean.JsonBean;
import com.module.questionnaire.bean.QuestionAnswerBean;
import com.module.questionnaire.utils.GetJsonDataUtil;
import com.module.questionnaire.utils.StringUtil;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainAdapter.ItemListener {

    private Toolbar mToolbar;
    private TextView mTextTitle;
    private TextView mTextRefresh;
    private NestedScrollView mNestedScrollView;
    private ImageView mImageCustomerServiceAvatar;
    private TextView mTextCustomerServiceName;
    private RatingBar mRatingBar;
    private TextView mTextCustomerServiceJob;
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearLayout;
    private MainAdapter mMainAdapter;

    private List<QuestionAnswerBean> mList = new ArrayList<>();
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private long mExitTime;

    private static final int RECORD_AUDIO = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColor(this, Color.WHITE, 0);
        //修改状态栏字体颜色变成黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initTitle();
        initView();
        initData();
    }

    private void initTitle() {
        mToolbar = findViewById(R.id.layout_title_tb);
        mToolbar.setNavigationOnClickListener(v -> finish());
        mTextTitle = findViewById(R.id.layout_title_title_tv);
        mTextRefresh = findViewById(R.id.layout_title_right_tv);
        mTextRefresh.setOnClickListener(this);
    }

    private void initView() {
        mNestedScrollView = findViewById(R.id.main_nsv);
        mImageCustomerServiceAvatar = findViewById(R.id.main_customer_service_avatar_iv);
        mTextCustomerServiceName = findViewById(R.id.main_customer_service_name_tv);
        mRatingBar = findViewById(R.id.main_customer_service_evaluation_rb);
        mTextCustomerServiceJob = findViewById(R.id.main_customer_service_job_tv);
        mRecyclerView = findViewById(R.id.main_conversation_rv);
        mLinearLayout = findViewById(R.id.main_answer_option_ll);

        Glide.with(this).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(mImageCustomerServiceAvatar);
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
        QuestionAnswerBean bean = new QuestionAnswerBean();
        bean.setId(0);
        bean.setType("问题1");
        bean.setLabel("问卷调查功能演示");
        mList.add(bean);

        mMainAdapter = new MainAdapter(this, mList);
        mRecyclerView.setAdapter(mMainAdapter);
        mMainAdapter.setOnItemListener(this);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.layout_title_right_tv) {
            Toast.makeText(this, "刷新", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * adapter监听
     * onInteraction是作为问答方数据加载完毕后，通知回答方做出反应的接口
     */
    @Override
    public void onInteraction(int id) {
        QuestionAnswerBean bean;
        switch (id) {
            case 0:
                bean = new QuestionAnswerBean();
                bean.setId(1);
                bean.setType("问题1");
                bean.setLabel("单选问题1");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 1:
                addRecyclerView();
                break;
            case 2:
                bean = new QuestionAnswerBean();
                bean.setId(3);
                bean.setType("问题1");
                bean.setLabel("单选问题2");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 3:
                addRadioView();
                break;
            case 4:
                bean = new QuestionAnswerBean();
                bean.setId(5);
                bean.setType("问题1");
                bean.setLabel("输入问题");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 5:
                addEditTextView();
                break;
            case 6:
                bean = new QuestionAnswerBean();
                bean.setId(7);
                bean.setType("问题1");
                bean.setLabel("上传图片");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 7:
                addUploadPhotoView();
                break;
            case 8:
                bean = new QuestionAnswerBean();
                bean.setId(9);
                bean.setType("问题1");
                bean.setLabel("省市区");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 9:
                addRegionalChoiceView();
                break;
            case 10:
                bean = new QuestionAnswerBean();
                bean.setId(11);
                bean.setType("问题1");
                bean.setLabel("自定义签名");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 11:
                addCustomizeSignatureView();
                break;
            case 12:
                bean = new QuestionAnswerBean();
                bean.setId(13);
                bean.setType("问题1");
                bean.setLabel("请根据语音回答问题");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 13:
                bean = new QuestionAnswerBean();
                bean.setId(14);
                bean.setType("问题2");
                bean.setLabel("test.mp3");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 14:
                addAudioVoiceView();
                break;
            case 15:
                bean = new QuestionAnswerBean();
                bean.setId(16);
                bean.setType("问题1");
                bean.setLabel("时间选择器");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 16:
                addTimeSelectView();
                break;
            case 17:
                bean = new QuestionAnswerBean();
                bean.setId(18);
                bean.setType("问题1");
                bean.setLabel("上传文件");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 18:
                addUploadFileView();
                break;
            case 19:
                Toast.makeText(this, "暂时完成", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        //addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快，但是如果立即调用fullScroll，view可能还没有显示出来，所以会失败，应该通过handler在新线程中更新。
        Handler handler = new Handler();
        handler.post(() -> mNestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN));
    }

    //动态添加单选的RecyclerView
    private void addRecyclerView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_recycler_view, null);
        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view_rv);
        LinearLayoutManager layoutManagerInfo = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerInfo);
        layoutManagerInfo.setOrientation(OrientationHelper.VERTICAL);

        List<QuestionAnswerBean.Item> itemList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            QuestionAnswerBean.Item item = new QuestionAnswerBean.Item();
            item.setId(1);
            item.setTitle("单选问题答案" + (i + 1));
            item.setValue(String.valueOf(i + 1));
            itemList.add(item);
        }

        AddRecyclerViewAdapter adapter = new AddRecyclerViewAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemListener(value -> {
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(2);
            bean.setType("回答1");
            bean.setLabel(value);
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        });

        mLinearLayout.addView(view);
    }

    //动态添加单选的RadioView
    private void addRadioView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_radio_view, null);
        TextView textLeft = view.findViewById(R.id.main_radio_view_left_tv);
        textLeft.setText("我是11111");
        textLeft.setOnClickListener(view1 -> {
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(4);
            bean.setType("回答1");
            bean.setLabel(textLeft.getText().toString());
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        });
        TextView textRight = view.findViewById(R.id.main_radio_view_right_tv);
        textRight.setText("我是33333");
        textRight.setOnClickListener(view1 -> {
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(4);
            bean.setType("回答1");
            bean.setLabel(textRight.getText().toString());
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        });
        mLinearLayout.addView(view);
    }

    //动态添加输入框的EditTextView
    private void addEditTextView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_edit_view, null);
        TextView textTitle = view.findViewById(R.id.main_edit_view_title_tv);
        TextView textDetermine = view.findViewById(R.id.main_edit_view_determine_tv);
        EditText editText = view.findViewById(R.id.main_edit_view_input_et);

        //这里的“请输入姓名”是一个变量,跟随接口获取而改变
        textTitle.setText("请输入姓名");
        textDetermine.setText("确定");
        textDetermine.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(editText.getText())) {
                QuestionAnswerBean bean = new QuestionAnswerBean();
                bean.setId(6);
                bean.setType("回答1");
                bean.setLabel(editText.getText().toString());
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                mLinearLayout.removeAllViews();
                mLinearLayout.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
            }
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
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(8);
            bean.setType("回答3");
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        });

        linearCamera.setOnClickListener(view1 -> {
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(8);
            bean.setType("回答3");
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        });

        mLinearLayout.addView(view);
    }

    //动态添加选择省市区的RegionalChoiceView
    private void addRegionalChoiceView() {
        initJsonData();
    }

    //初始化省市区数据
    private void initJsonData() {
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");
        ArrayList<JsonBean> jsonBean = parseData(JsonData);
        options1Items = jsonBean;
        //遍历省份
        for (int i = 0; i < jsonBean.size(); i++) {
            //该省的城市列表（第二级）
            ArrayList<String> cityList = new ArrayList<>();
            //该省的所有地区列表（第三极）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();
            //遍历该省份的所有城市
            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                //添加城市
                cityList.add(cityName);
                //该城市的所有地区列表
                ArrayList<String> city_AreaList = new ArrayList<>();
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                //添加该省所有地区数据
                province_AreaList.add(city_AreaList);
            }
            options2Items.add(cityList);
            options3Items.add(province_AreaList);
        }
        showPickerView();
    }

    private ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    //显示出省市区选择器
    private void showPickerView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            String opt1tx = options1Items.size() > 0 ? options1Items.get(options1).getPickerViewText() : "";
            String opt2tx = options2Items.size() > 0 && options2Items.get(options1).size() > 0 ? options2Items.get(options1).get(options2) : "";
            String opt3tx = options2Items.size() > 0 && options3Items.get(options1).size() > 0 && options3Items.get(options1).get(options2).size() > 0 ?
                    options3Items.get(options1).get(options2).get(options3) : "";
            String tx = opt1tx + opt2tx + opt3tx;

            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(10);
            bean.setType("回答1");
            bean.setLabel(tx);
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        }).setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        //三级选择器
        pvOptions.setPicker(options1Items, options2Items, options3Items);
        pvOptions.findViewById(R.id.btnCancel).setVisibility(View.INVISIBLE);
        pvOptions.show(mLinearLayout);
    }

    //动态添加自定义签名的CustomizeSignatureView
    private void addCustomizeSignatureView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_customize_signature_view, null);
        SignaturePad signaturePad = view.findViewById(R.id.main_customize_signature_view_sp);
        Button buttonClear = view.findViewById(R.id.main_customize_signature_view_clear_btn);
        Button buttonSubmit = view.findViewById(R.id.main_customize_signature_view_submit_btn);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                buttonClear.setEnabled(true);
                buttonSubmit.setEnabled(true);
            }

            @Override
            public void onClear() {
                buttonClear.setEnabled(false);
                buttonSubmit.setEnabled(false);
            }
        });

        buttonClear.setOnClickListener(view1 -> signaturePad.clear());
        buttonSubmit.setOnClickListener(view1 -> {
            Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(12);
            bean.setType("回答3");
            bean.setLabel(StringUtil.BitMapToString(signatureBitmap));
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        });

        mLinearLayout.addView(view);
    }

    //动态添加录音的AudioVoiceView
    private void addAudioVoiceView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_audio_voice_view, null);
        TextView textView = view.findViewById(R.id.main_audio_voice_view_tv);
        ImageView imageView = view.findViewById(R.id.main_audio_voice_view_iv);
        imageView.setOnLongClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
            } else {
                startAudioVoice(textView, imageView);
            }
            return false;
        });
        mLinearLayout.addView(view);
    }

    //开始长按录音
    private void startAudioVoice(TextView textView, ImageView imageView) {
        QuestionAnswerBean bean = new QuestionAnswerBean();
        bean.setId(15);
        bean.setType("回答4");
        mList.add(bean);
        mMainAdapter.notifyItemInserted(mList.size());
        mLinearLayout.removeAllViews();
        mLinearLayout.setVisibility(View.GONE);
    }

    //动态添加时间选择器TimeSelectView
    private void addTimeSelectView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        TimePickerView timePickerView = new TimePickerBuilder(this, (date, v) -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String time = format.format(date);
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(17);
            bean.setType("回答1");
            bean.setLabel(time);
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        }).setTimeSelectChangeListener(date -> Log.i("pvTime", "onTimeSelectChanged"))
                .setType(new boolean[]{true, true, true, false, false, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(view -> Log.i("pvTime", "onCancelClickListener"))
                .build();

        Dialog mDialog = timePickerView.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            timePickerView.getDialogContainerLayout().setLayoutParams(params);

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

    //动态添加上传文件的UploadFileView
    private void addUploadFileView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_upload_file_view, null);
        LinearLayout linearFile = view.findViewById(R.id.main_upload_file_view_file_ll);

        linearFile.setOnClickListener(view1 -> {
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(19);
            bean.setType("回答3");
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
        });

        mLinearLayout.addView(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您可以开始发送语音了", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "您将不能发送语音", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
