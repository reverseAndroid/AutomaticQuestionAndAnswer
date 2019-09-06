package com.module.questionnaire.ui.fragment;

public class FragmentFactory {

    private static FragmentFactory mInstance;
    private HomeFragment mHomeFragment;
    private QuestionFragment mQuestionFragment;
    private NewsFragment mNewsFragment;
    private MeFragment mMeFragment;

    private FragmentFactory() {

    }

    public static FragmentFactory getInstance() {
        if (mInstance == null) {
            synchronized (FragmentFactory.class) {
                if (mInstance == null) {
                    mInstance = new FragmentFactory();
                }
            }
        }
        return mInstance;
    }

    public HomeFragment getHomeFragment() {
        if (mHomeFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                }
            }
        }
        return mHomeFragment;
    }

    public QuestionFragment getQuestionFragment() {
        if (mQuestionFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mQuestionFragment == null) {
                    mQuestionFragment = new QuestionFragment();
                }
            }
        }
        return mQuestionFragment;
    }

    public NewsFragment getNewsFragment() {
        if (mNewsFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mNewsFragment == null) {
                    mNewsFragment = new NewsFragment();
                }
            }
        }
        return mNewsFragment;
    }

    public MeFragment getMeFragment() {
        if (mMeFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mMeFragment == null) {
                    mMeFragment = new MeFragment();
                }
            }
        }
        return mMeFragment;
    }
}
