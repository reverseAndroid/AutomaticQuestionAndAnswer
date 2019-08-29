package com.module.questionnaire;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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
import com.module.questionnaire.adapter.MultipleSelectionViewAdapter;
import com.module.questionnaire.bean.ContactBean;
import com.module.questionnaire.bean.JsonBean;
import com.module.questionnaire.bean.MultipleSelectionBean;
import com.module.questionnaire.bean.QuestionAnswerBean;
import com.module.questionnaire.utils.GetAddressUtil;
import com.module.questionnaire.utils.GetJsonDataUtil;
import com.module.questionnaire.utils.GlideEngine;
import com.module.questionnaire.utils.StringBitmapUtil;
import com.module.questionnaire.widget.DrawableEditText;
import com.module.questionnaire.widget.VoiceView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainAdapter.ItemUpdateListener {

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
    private Uri mUri;
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private MediaRecorder mMediaRecorder;
    //判断是否开始录音
    private boolean mIsRecording = false;
    //录音计时
    private float mTimer;
    //如果有好几种定位提供方式，当前只取第一次定位到的地址，后面的不管
    private boolean mIsLocation = false;
    private long mExitTime;

    private static final int WRITE_EXTERNAL_STORAGE = 1000;
    private static final int OPEN_ALBUM = 1001;
    private static final int CAMERA = 1003;
    private static final int OPEN_CAMERA = 1004;
    private static final int RECORD_AUDIO = 1005;
    private static final int OPEN_FILE = 1006;
    private static final int GPS = 1007;
    private static final int SETTING_GPS = 1008;
    private static final int READ_CONTACTS = 1009;

    private static final int MSG_AUDIO_HAS_PREPARED = 101;
    private static final int MSG_VOLUME_UPDATED = 102;
    private static final int MSG_DIALOG_DISMISS = 103;
    // 最大录音时长1000*60*10;
    private static final int MAX_LENGTH = 1000 * 60 * 10;

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
        mMainAdapter.setItemUpdateListener(this);
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
                bean = new QuestionAnswerBean();
                bean.setId(20);
                bean.setType("问题1");
                bean.setLabel("表单问题");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 20:
                addFormView();
                break;
            case 21:
                bean = new QuestionAnswerBean();
                bean.setId(22);
                bean.setType("问题1");
                bean.setLabel("多选问题");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 22:
                addMultipleSelectionView();
                break;
            case 23:
                bean = new QuestionAnswerBean();
                bean.setId(24);
                bean.setType("问题1");
                bean.setLabel("上传定位");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 24:
                uploadPosition();
                break;
            case 25:
                bean = new QuestionAnswerBean();
                bean.setId(26);
                bean.setType("问题1");
                bean.setLabel("上传通讯录");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            case 26:
                uploadContact();
                break;
            case 27:
                bean = new QuestionAnswerBean();
                bean.setId(28);
                bean.setType("问题1");
                bean.setLabel("暂无问题");
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                break;
            default:
                break;
        }
        updateNestScrollView();
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
            updateNestScrollView();
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
            updateNestScrollView();
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
            updateNestScrollView();
        });
        mLinearLayout.addView(view);
    }

    //动态添加输入框的EditTextView
    private void addEditTextView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_edit_view, null);
        ImageView imageView = view.findViewById(R.id.main_edit_view_determine_iv);
        EditText editText = view.findViewById(R.id.main_edit_view_input_et);

        //这里的“请输入姓名”是一个变量,跟随接口获取而改变
        editText.setHint("请输入姓名");
        imageView.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(editText.getText())) {
                QuestionAnswerBean bean = new QuestionAnswerBean();
                bean.setId(6);
                bean.setType("回答1");
                bean.setLabel(editText.getText().toString());
                mList.add(bean);
                mMainAdapter.notifyItemInserted(mList.size());
                mLinearLayout.removeAllViews();
                mLinearLayout.setVisibility(View.GONE);
                updateNestScrollView();
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
            updateNestScrollView();
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
            bean.setLabel(StringBitmapUtil.BitMapToString(signatureBitmap));
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
            updateNestScrollView();
        });

        mLinearLayout.addView(view);
    }

    //动态添加录音的AudioVoiceView
    private void addAudioVoiceView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_audio_voice_view, null);
        VoiceView voiceView = view.findViewById(R.id.main_audio_voice_view_animation_vv);
        TextView textView = view.findViewById(R.id.main_audio_voice_view_tv);
        ImageView imageView = view.findViewById(R.id.main_audio_voice_view_iv);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/question/audio/" + System.currentTimeMillis() + ".mp4");
        imageView.setOnTouchListener((view1, motionEvent) -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
            } else {
                startAudioVoice(voiceView, textView, motionEvent, file, view1);
            }
            return true;
        });
        mLinearLayout.addView(view);
    }

    //开始长按录音
    private void startAudioVoice(VoiceView voiceView, TextView textView, MotionEvent motionEvent, File file, View view) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                textView.setText("开始录音");
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
                    mHandler.sendEmptyMessage(MSG_AUDIO_HAS_PREPARED);
                    voiceView.setVisibility(View.VISIBLE);
                    voiceView.startAnimation();
                } catch (IllegalStateException | IOException e) {
                    e.getMessage();
                }
                break;
            case MotionEvent.ACTION_UP:
                textView.setText("长按开始录制");

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

                    showVoiceHandleDialog(file);
                }

                mIsRecording = false;
                mTimer = 0;
                break;
            default:
                break;
        }
    }

    private void showVoiceHandleDialog(File file) {
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

        FrameLayout frameCancel = contentView.findViewById(R.id.dialog_voice_handle_cancel_fl);
        FrameLayout frameSend = contentView.findViewById(R.id.dialog_voice_handle_send_fl);

        frameCancel.setOnClickListener(view -> {
            file.delete();
            dialog.dismiss();
        });

        frameSend.setOnClickListener(view -> {
            dialog.dismiss();
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(15);
            bean.setType("回答4");
            bean.setLabel(file.getAbsolutePath());
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
            updateNestScrollView();
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
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
                                mHandler.sendEmptyMessage(MSG_VOLUME_UPDATED);
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
            updateNestScrollView();
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
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, OPEN_FILE);
        });

        mLinearLayout.addView(view);
    }

    //动态添加表单的FormView
    private void addFormView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_form_view, null);
        DrawableEditText editName = view.findViewById(R.id.main_form_view_name_et);
        editName.setOnDrawableRightListener(() -> editName.setText(""));
        DrawableEditText editPhone = view.findViewById(R.id.main_form_view_phone_et);
        editPhone.setOnDrawableRightListener(() -> editPhone.setText(""));
        DrawableEditText editAddress = view.findViewById(R.id.main_form_view_address_et);
        editAddress.setOnDrawableRightListener(() -> editAddress.setText(""));
        TextView textConfirm = view.findViewById(R.id.main_form_view_confirm_tv);

        textConfirm.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(editName.getText())) {
                Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(editPhone.getText())) {
                Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(editAddress.getText())) {
                Toast.makeText(this, "家庭住址不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(21);
            bean.setType("回答1");
            bean.setLabel("姓名:" + editName.getText().toString() + "  手机号码:" + editPhone.getText().toString() + "  家庭住址:" + editAddress.getText().toString());
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
            updateNestScrollView();
        });

        mLinearLayout.addView(view);
    }

    //动态添加多选的MultipleSelectionView
    private void addMultipleSelectionView() {
        mLinearLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.main_multiple_selection_view, null);
        RecyclerView recyclerView = view.findViewById(R.id.main_multiple_selection_rv);
        TextView textView = view.findViewById(R.id.main_multiple_selection_confirm_tv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<MultipleSelectionBean> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            MultipleSelectionBean bean = new MultipleSelectionBean();
            bean.setTitle("多选项" + i);
            bean.setSelect(false);
            list.add(bean);
        }

        MultipleSelectionViewAdapter adapter = new MultipleSelectionViewAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((i, isSelection) -> {
            list.get(i).setSelect(isSelection);
            adapter.notifyItemChanged(i);
        });

        textView.setOnClickListener(view1 -> {
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(23);
            bean.setType("回答1");
            bean.setLabel(new Gson().toJson(list));
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
            updateNestScrollView();
        });

        mLinearLayout.addView(view);
    }

    //上传定位
    private void uploadPosition() {
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
                setLatitudeLongitudeToLocation(location);
                if (counter == providers.size() && location == null) {
                    QuestionAnswerBean bean = new QuestionAnswerBean();
                    bean.setId(25);
                    bean.setType("回答1");
                    bean.setLabel("获取不到您的定位，请检查网络或者GPS是否开启");
                    mList.add(bean);
                    mMainAdapter.notifyItemInserted(mList.size());
                    mLinearLayout.removeAllViews();
                    mLinearLayout.setVisibility(View.GONE);
                    updateNestScrollView();
                }
            }
        }
    }

    //经纬度转换成地点
    private void setLatitudeLongitudeToLocation(Location location) {
        if (location != null && !mIsLocation) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String position = GetAddressUtil.getAddress(this, latitude, longitude);
            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(25);
            bean.setType("回答1");
            bean.setLabel(position);
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
            updateNestScrollView();
            mIsLocation = true;
        }
    }

    //上传通讯录
    private void uploadContact() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
        } else {
            readContacts();
        }
    }

    //读取联系人
    private void readContacts() {
        List<ContactBean> list = new ArrayList<>();
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactBean bean = new ContactBean();
                    bean.setName(displayName);
                    bean.setPhone(number);
                    list.add(bean);
                }
            }

            QuestionAnswerBean bean = new QuestionAnswerBean();
            bean.setId(27);
            bean.setType("回答1");
            bean.setLabel(new Gson().toJson(list));
            mList.add(bean);
            mMainAdapter.notifyItemInserted(mList.size());
            mLinearLayout.removeAllViews();
            mLinearLayout.setVisibility(View.GONE);
            updateNestScrollView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            QuestionAnswerBean bean;
            switch (requestCode) {
                case OPEN_ALBUM:
                    List<Uri> pathList = Matisse.obtainResult(data);
                    String path = StringBitmapUtil.BitMapToString(StringBitmapUtil.UriToBitmap(this, pathList.get(0)));
                    bean = new QuestionAnswerBean();
                    bean.setId(8);
                    bean.setType("回答3");
                    bean.setLabel(path);
                    mList.add(bean);
                    mMainAdapter.notifyItemInserted(mList.size());
                    mLinearLayout.removeAllViews();
                    mLinearLayout.setVisibility(View.GONE);
                    updateNestScrollView();
                    break;
                case OPEN_CAMERA:
                    Bitmap bitmap = StringBitmapUtil.UriToBitmap(this, mUri);
                    bean = new QuestionAnswerBean();
                    bean.setId(8);
                    bean.setType("回答3");
                    bean.setLabel(StringBitmapUtil.BitMapToString(bitmap));
                    mList.add(bean);
                    mMainAdapter.notifyItemInserted(mList.size());
                    mLinearLayout.removeAllViews();
                    mLinearLayout.setVisibility(View.GONE);
                    updateNestScrollView();
                    break;
                case OPEN_FILE:
                    Uri uri = data.getData();
                    bean = new QuestionAnswerBean();
                    bean.setId(19);
                    bean.setType("回答3");
                    bean.setLabel(uri.getPath());
                    mList.add(bean);
                    mMainAdapter.notifyItemInserted(mList.size());
                    mLinearLayout.removeAllViews();
                    mLinearLayout.setVisibility(View.GONE);
                    updateNestScrollView();
                    break;
                case SETTING_GPS:
                    uploadPosition();
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
                    uploadPosition();
                } else {
                    Toast.makeText(this, "您将不能获取定位", Toast.LENGTH_SHORT).show();
                    QuestionAnswerBean bean = new QuestionAnswerBean();
                    bean.setId(25);
                    bean.setType("回答1");
                    bean.setLabel("您没有同意上报位置");
                    mList.add(bean);
                    mMainAdapter.notifyItemInserted(mList.size());
                    mLinearLayout.removeAllViews();
                    mLinearLayout.setVisibility(View.GONE);
                    updateNestScrollView();
                }
                break;
            case READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您已经获取通讯录权限了", Toast.LENGTH_SHORT).show();
                    uploadContact();
                } else {
                    Toast.makeText(this, "您将不能获取通讯录", Toast.LENGTH_SHORT).show();
                    QuestionAnswerBean bean = new QuestionAnswerBean();
                    bean.setId(27);
                    bean.setType("回答1");
                    bean.setLabel("您没有同意获取通讯录");
                    mList.add(bean);
                    mMainAdapter.notifyItemInserted(mList.size());
                    mLinearLayout.removeAllViews();
                    mLinearLayout.setVisibility(View.GONE);
                    updateNestScrollView();
                }
                break;
            default:
                break;
        }
    }

    //addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快，但是如果立即调用fullScroll，view可能还没有显示出来，所以会失败，应该通过handler在新线程中更新。
    private void updateNestScrollView() {
        Handler handler = new Handler();
        handler.post(() -> {
            mNestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
            handler.removeCallbacksAndMessages(null);
        });
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
