package com.module.questionnaire.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(initContentView(), container, false);
        //修改状态栏字体颜色变成黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getActivity().getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            decor.setFitsSystemWindows(true);
        }
        return mRootView;
    }

    protected abstract int initContentView();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTitle(mRootView);
        initView(mRootView);
        initData();
    }

    protected abstract void initTitle(View contentView);

    protected abstract void initView(View contentView);

    protected abstract void initData();
}
