package com.module.questionnaire.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.module.questionnaire.R;
import com.module.questionnaire.base.BaseActivity;
import com.module.questionnaire.ui.fragment.FragmentFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements TabLayout.BaseOnTabSelectedListener {

    private FrameLayout mFrameLayout;
    private TabLayout mTabLayout;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private long mExitTime;
    private int mLastFragment;

    @Override
    protected int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView() {
        mFrameLayout = findViewById(R.id.main_fl);
        mTabLayout = findViewById(R.id.main_tl);
        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.bottom_navigation_item_home)));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.bottom_navigation_item_question_bank)));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.bottom_navigation_item_news)));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.bottom_navigation_item_me)));
        mTabLayout.addOnTabSelectedListener(this);
    }

    @Override
    protected void initData() {
        mFragmentList.add(FragmentFactory.getInstance().getQuestionFragment());
        mFragmentList.add(FragmentFactory.getInstance().getQuestionBankFragment());
        mFragmentList.add(FragmentFactory.getInstance().getNewsFragment());
        mFragmentList.add(FragmentFactory.getInstance().getMeFragment());
        mLastFragment = 0;
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fl, mFragmentList.get(0)).show(mFragmentList.get(0)).commit();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                if (mLastFragment != 0) {
                    setFragmentPosition(mLastFragment, 0);
                    mLastFragment = 0;
                }
                break;
            case 1:
                if (mLastFragment != 1) {
                    setFragmentPosition(mLastFragment, 1);
                    mLastFragment = 1;
                }
                break;
            case 2:
                if (mLastFragment != 2) {
                    setFragmentPosition(mLastFragment, 2);
                    mLastFragment = 2;
                }
                break;
            case 3:
                if (mLastFragment != 3) {
                    setFragmentPosition(mLastFragment, 3);
                    mLastFragment = 3;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void setFragmentPosition(int lastFragment, int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!mFragmentList.get(position).isAdded()) {
            transaction.add(R.id.main_fl, mFragmentList.get(position));
        }
        transaction.hide(mFragmentList.get(lastFragment)).show(mFragmentList.get(position)).commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.again_quit, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
