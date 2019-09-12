package com.module.questionnaire.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.module.questionnaire.R;
import com.module.questionnaire.base.BaseActivity;
import com.module.questionnaire.utils.Constant;
import com.module.questionnaire.utils.LogUtils;
import com.module.questionnaire.utils.SPUtils;
import com.module.questionnaire.utils.http.NewApiRetrofit;
import com.module.questionnaire.widget.DrawableEditText;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterActivity extends BaseActivity implements DrawableEditText.OnDrawableRightListener, View.OnClickListener {

    private Toolbar mToolbar;
    private DrawableEditText mEditPhone;
    private DrawableEditText mEditPassword;
    private DrawableEditText mEditCode;
    private TextView mTextCode;
    private Button mButtonRegister;
    private RadioButton mRadioButton;
    private TextView mTextAgree;

    private boolean isHidden;

    @Override
    protected int initContentView() {
        return R.layout.activity_register;
    }

    @Override
    protected void initTitle() {
        mToolbar = findViewById(R.id.layout_title_tb);
        mToolbar.setNavigationIcon(R.drawable.icon_back);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    protected void initView() {
        mEditPhone = findViewById(R.id.register_phone_et);
        mEditPassword = findViewById(R.id.register_password_et);
        Drawable drawable = mEditPassword.getContext().getResources().getDrawable(R.drawable.icon_login_password_default);
        drawable.setBounds(0, 0, 45, 45);
        mEditPassword.setCompoundDrawables(null, null, drawable, null);
        mEditPassword.setOnDrawableRightListener(this);
        mEditCode = findViewById(R.id.register_code_et);
        mTextCode = findViewById(R.id.register_code_tv);
        mTextCode.setOnClickListener(this);
        mButtonRegister = findViewById(R.id.register_register_btn);
        mButtonRegister.setOnClickListener(this);
        mRadioButton = findViewById(R.id.register_agree_rb);
        mTextAgree = findViewById(R.id.register_agree_tv);
        mTextAgree.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDrawableRightClick() {
        if (!isHidden) {
            Drawable drawable = mEditPassword.getContext().getResources().getDrawable(R.drawable.icon_login_password_show);
            drawable.setBounds(0, 0, 45, 45);
            mEditPassword.setCompoundDrawables(null, null, drawable, null);
            mEditPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isHidden = true;
        } else {
            Drawable drawable = mEditPassword.getContext().getResources().getDrawable(R.drawable.icon_login_password_default);
            drawable.setBounds(0, 0, 45, 45);
            mEditPassword.setCompoundDrawables(null, null, drawable, null);
            mEditPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isHidden = false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register_code_tv) {
            Toast.makeText(this, "获取验证码", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.register_register_btn) {
            startRegister();
        } else if (view.getId() == R.id.register_agree_tv) {
            Intent intent = new Intent(RegisterActivity.this, RegisterAgreementActivity.class);
            startActivity(intent);
        }
    }

    private void startRegister() {
        if (TextUtils.isEmpty(mEditPhone.getText().toString())) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mEditPassword.getText().toString())) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("mobile", mEditPhone.getText().toString());
        params.put("password", mEditPassword.getText().toString());
        NewApiRetrofit.getInstance().register(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.e("完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
