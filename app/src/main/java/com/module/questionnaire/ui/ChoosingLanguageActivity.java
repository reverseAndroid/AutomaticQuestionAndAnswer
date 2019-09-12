package com.module.questionnaire.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.module.questionnaire.R;
import com.module.questionnaire.adapter.ChoosingLanguageAdapter;
import com.module.questionnaire.base.BaseActivity;
import com.module.questionnaire.bean.ChoosingLanguageBean;
import com.module.questionnaire.utils.Constant;
import com.module.questionnaire.utils.LogUtils;
import com.module.questionnaire.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChoosingLanguageActivity extends BaseActivity implements ChoosingLanguageAdapter.OnItemClickListener, View.OnClickListener {

    private TextView mTextPrompt;
    private RecyclerView mRecyclerView;
    private Button mButtonNext;

    private List<ChoosingLanguageBean> mList = new ArrayList<>();
    private ChoosingLanguageAdapter mAdapter;
    private Configuration config;

    @Override
    protected int initContentView() {
        return R.layout.activity_choosing_language;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView() {
        mTextPrompt = findViewById(R.id.choosing_language_prompt_tv);
        mRecyclerView = findViewById(R.id.choosing_language_rv);
        mButtonNext = findViewById(R.id.choosing_language_next_btn);
        mButtonNext.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mTextPrompt.setText("Choosing Language");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        ChoosingLanguageBean bean = new ChoosingLanguageBean();
        bean.setIcon(R.drawable.icon_language_e);
        bean.setLanguage("English");
        bean.setRendering("English");
        bean.setSelection(true);
        mList.add(bean);

        ChoosingLanguageBean bean1 = new ChoosingLanguageBean();
        bean1.setIcon(R.drawable.icon_language_g);
        bean1.setLanguage("ភាសាខ្មែរ");
        bean1.setRendering("Khmer");
        bean1.setSelection(false);
        mList.add(bean1);

        ChoosingLanguageBean bean2 = new ChoosingLanguageBean();
        bean2.setIcon(R.drawable.icon_language_c);
        bean2.setLanguage("中文");
        bean2.setRendering("Chinese");
        bean2.setSelection(false);
        mList.add(bean2);

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constant.LANGUAGE))) {
            int position = 0;
            switch (SPUtils.getInstance().getString(Constant.LANGUAGE)) {
                case "en":
                    position = 0;
                    break;
                case "kh":
                    position = 1;
                    break;
                case "zh":
                    position = 2;
                    break;
                default:
                    break;
            }

            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setSelection(false);
            }

            mList.get(position).setSelection(true);
        }

        mAdapter = new ChoosingLanguageAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelection(false);
        }
        mList.get(position).setSelection(true);
        mAdapter.notifyDataSetChanged();

        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config = resources.getConfiguration();
        switch (position) {
            case 0:
                config.locale = Locale.ENGLISH;
                mTextPrompt.setText("Choosing Language");
                break;
            case 1:
                config.locale = new Locale("kh");
                mTextPrompt.setText("fljalsfajhsdhgfa");
                break;
            case 2:
                config.locale = Locale.SIMPLIFIED_CHINESE;
                mTextPrompt.setText("您选择的语言是");
                break;
            default:
                break;
        }

        resources.updateConfiguration(config, dm);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.choosing_language_next_btn) {
            if (config == null) {
                Resources resources = getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                config = resources.getConfiguration();
                config.locale = Locale.ENGLISH;
                resources.updateConfiguration(config, dm);
            }
            SPUtils.getInstance().put(Constant.LANGUAGE, config.locale.getLanguage());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
