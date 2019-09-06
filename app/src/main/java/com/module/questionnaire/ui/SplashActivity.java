package com.module.questionnaire.ui;

import android.content.Intent;
import android.os.Handler;

import com.module.questionnaire.R;
import com.module.questionnaire.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected int initContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }
}
