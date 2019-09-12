package com.module.questionnaire.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import java.util.Locale;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity implements View.OnClickListener, DrawableEditText.OnDrawableRightListener {

    private Toolbar mToolbar;
    private FrameLayout mFrameMe;
    private ImageView mImageMe;

    private DrawableEditText mEditPhone;
    private DrawableEditText mEditPassword;
    private Button mButtonLogin;
    private TextView mTextRegister, mTextForget;

    private boolean isHidden;
    private PopupWindow mPopupWindow;

    @Override
    protected int initContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initTitle() {
        mToolbar = findViewById(R.id.layout_title_tb);
        mToolbar.setNavigationIcon(R.drawable.icon_exit);
        mToolbar.setNavigationOnClickListener(view -> finish());

        mFrameMe = findViewById(R.id.layout_title_right_fl);
        mFrameMe.setOnClickListener(this);
        mImageMe = findViewById(R.id.layout_title_right_iv);
    }

    @Override
    protected void initView() {
        mEditPhone = findViewById(R.id.login_phone_et);
        mEditPassword = findViewById(R.id.login_password_et);
        Drawable drawable = mEditPassword.getContext().getResources().getDrawable(R.drawable.icon_login_password_default);
        drawable.setBounds(0, 0, 45, 45);
        mEditPassword.setCompoundDrawables(null, null, drawable, null);
        mEditPassword.setOnDrawableRightListener(this);
        mButtonLogin = findViewById(R.id.login_login_btn);
        mButtonLogin.setOnClickListener(this);
        mTextRegister = findViewById(R.id.login_register_tv);
        mTextRegister.setOnClickListener(this);
        mTextForget = findViewById(R.id.login_forget_tv);
        mTextForget.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constant.LANGUAGE))) {
            switch (SPUtils.getInstance().getString(Constant.LANGUAGE)) {
                case "en":
                    mImageMe.setImageResource(R.drawable.icon_login_language_e);
                    break;
                case "kh":
                    mImageMe.setImageResource(R.drawable.icon_login_language_g);
                    break;
                case "zh":
                    mImageMe.setImageResource(R.drawable.icon_login_language_c);
                    break;
                default:
                    break;
            }
        } else {
            mImageMe.setImageResource(R.drawable.icon_login_language_e);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.layout_title_right_fl) {
            showLanguagePopupWindow();
        } else if (view.getId() == R.id.login_login_btn) {
            startLoginTask();
        } else if (view.getId() == R.id.login_register_tv) {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.login_forget_tv) {
            intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        }
    }

    private void showLanguagePopupWindow() {
        // 设置布局文件
        mPopupWindow = new PopupWindow(this);
        mPopupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.popup_window_login_change_language, null));
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
        mPopupWindow.showAsDropDown(mToolbar, 0, 0, Gravity.END);

        View view = mPopupWindow.getContentView();
        LinearLayout linearEnglish = view.findViewById(R.id.popup_window_login_change_language_e_ll);
        linearEnglish.setOnClickListener(view1 -> {
            mImageMe.setImageResource(R.drawable.icon_login_language_e);
            mPopupWindow.dismiss();
            updateLanguage(0);
        });
        LinearLayout linearCambodian = view.findViewById(R.id.popup_window_login_change_language_g_ll);
        linearCambodian.setOnClickListener(view1 -> {
            mImageMe.setImageResource(R.drawable.icon_login_language_g);
            mPopupWindow.dismiss();
            updateLanguage(1);
        });
        LinearLayout linearChinese = view.findViewById(R.id.popup_window_login_change_language_c_ll);
        linearChinese.setOnClickListener(view1 -> {
            mImageMe.setImageResource(R.drawable.icon_login_language_c);
            mPopupWindow.dismiss();
            updateLanguage(2);
        });
    }

    private void updateLanguage(int i) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        switch (i) {
            case 0:
                config.locale = Locale.ENGLISH;
                break;
            case 1:
                config.locale = new Locale("kh");
                break;
            case 2:
                config.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            default:
                break;
        }
        resources.updateConfiguration(config, dm);
        SPUtils.getInstance().put(Constant.LANGUAGE, config.locale.getLanguage());
    }

    private void startLoginTask() {
        if (TextUtils.isEmpty(mEditPhone.getText().toString())) {
            Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mEditPassword.getText().toString())) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("mobile", mEditPhone.getText().toString());
        params.put("password", mEditPassword.getText().toString());
        NewApiRetrofit.getInstance().login(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    if (loginResponse.isSuccess()) {
                        SPUtils.getInstance().put(Constant.TOKEN, loginResponse.getData().getAccess_token());
                        SPUtils.getInstance().put(Constant.USER_NAME, loginResponse.getData().getUser().getFirst_name());
                        setResult(RESULT_OK);
                        finish();
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
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
}
