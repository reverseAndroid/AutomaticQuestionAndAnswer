package com.module.questionnaire.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jaeger.library.StatusBarUtil;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initContentView());
        initStatusBar();
        initTitle();
        initView();
        initData();
    }

    private void initStatusBar() {
        switch (getClass().getSimpleName()) {
            case "SplashActivity":
                StatusBarUtil.setTransparent(this);
                break;
            case "ChoosingLanguageActivity":
                StatusBarUtil.setTranslucentForImageView(this, 0, null);
                break;
            default:
                StatusBarUtil.setColor(this, Color.WHITE, 0);
                break;
        }

        //修改状态栏字体颜色变成黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    protected abstract int initContentView();

    protected abstract void initTitle();

    protected abstract void initView();

    protected abstract void initData();
}
