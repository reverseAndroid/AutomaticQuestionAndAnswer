package com.module.questionnaire.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.module.questionnaire.App;
import com.module.questionnaire.R;
import com.module.questionnaire.adapter.ItemMeListAdapter;
import com.module.questionnaire.adapter.MeListAdapter;
import com.module.questionnaire.base.BaseFragment;
import com.module.questionnaire.bean.response.MeListResponse;
import com.module.questionnaire.utils.Constant;
import com.module.questionnaire.utils.LogUtils;
import com.module.questionnaire.utils.SPUtils;
import com.module.questionnaire.utils.http.NewApiRetrofit;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener, ItemMeListAdapter.OnItemClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    private TextView mTextTitle;
    private FrameLayout mFrameMe;
    private ImageView mImageMe;

    private ImageView mImageAvatar;
    private TextView mTextName;
    private RecyclerView mRecyclerView;

    private MeListAdapter mAdapter;

    @Override
    protected int initContentView() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initTitle(View contentView) {
        mTextTitle = contentView.findViewById(R.id.layout_title_title_tv);
        mTextTitle.setText(R.string.me_name);
        mFrameMe = contentView.findViewById(R.id.layout_title_right_fl);
        mFrameMe.setOnClickListener(this);
        mImageMe = contentView.findViewById(R.id.layout_title_right_iv);
        mImageMe.setImageResource(R.drawable.icon_title_me);
    }

    @Override
    protected void initView(View contentView) {
        mImageAvatar = contentView.findViewById(R.id.me_avatar_iv);
        Glide.with(App.getContext()).load(R.drawable.icon_default_user_avatar).apply(new RequestOptions().placeholder(R.drawable.icon_default_user_avatar)).into(mImageAvatar);

        mTextName = contentView.findViewById(R.id.me_name_tv);
        mRecyclerView = contentView.findViewById(R.id.me_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void initData() {
        Map<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constant.TOKEN))) {
            params.put("user", "false");
        } else {
            params.put("access_token", SPUtils.getInstance().getString(Constant.TOKEN));
            params.put("user", "true");
        }

        NewApiRetrofit.getInstance().getMeList(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(meListResponse -> {
                    if (meListResponse.isSuccess()) {
                        mAdapter = new MeListAdapter(getContext(), meListResponse.getData());
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);
                    }
                }, throwable -> LogUtils.e(throwable.getMessage()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_title_right_fl) {
            Toast.makeText(getActivity(), "我的", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(MeListResponse.DataBean bean) {
        Toast.makeText(getContext(), bean.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGlobalLayout() {
        mAdapter.mItemAdapter.setOnItemClickListener(this);
    }
}
