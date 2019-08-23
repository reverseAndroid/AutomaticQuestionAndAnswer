package com.module.questionnaire.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ldoublem.loadingviewlib.view.LVCircularJump;
import com.module.questionnaire.R;
import com.module.questionnaire.bean.QuestionAnswerBean;
import com.module.questionnaire.utils.StringUtil;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<QuestionAnswerBean> mList;

    public MainAdapter(Context context, List<QuestionAnswerBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    /**
     * adapter监听
     * onInteraction是作为问答方数据加载完毕后，通知回答方做出反应的接口
     */
    private ItemListener mItemListener;

    public interface ItemListener {
        void onInteraction(int id);
    }

    public void setOnItemListener(ItemListener itemListener) {
        this.mItemListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder mViewHolder = null;
        switch (viewType) {
            case 0:
                mViewHolder = new QuestionHolder1(inflater.inflate(R.layout.item_main_question, parent, false));
                break;
            case 1:
                mViewHolder = new QuestionHolder2(inflater.inflate(R.layout.item_main_question2, parent, false));
                break;
            case 2:
                mViewHolder = new AnswerHolder1(inflater.inflate(R.layout.item_main_answer, parent, false));
                break;
            case 3:
                mViewHolder = new AnswerHolder2(inflater.inflate(R.layout.item_main_answer2, parent, false));
                break;
            case 4:
                mViewHolder = new AnswerHolder3(inflater.inflate(R.layout.item_main_answer3, parent, false));
                break;
            default:
                break;
        }
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                bindQuestionHolder1(viewHolder, position);
                break;
            case 1:
                bindQuestionHolder2(viewHolder, position);
                break;
            case 2:
                bindAnswerHolder1(viewHolder, position);
                break;
            case 3:
                bindAnswerHolder2(viewHolder, position);
                break;
            case 4:
                bindAnswerHolder3(viewHolder, position);
                break;
            default:
                break;
        }
    }

    private void bindQuestionHolder1(RecyclerView.ViewHolder viewHolder, int position) {
        QuestionHolder1 holder = (QuestionHolder1) viewHolder;
        holder.mLoadingView.setViewColor(mContext.getResources().getColor(R.color.black));
        holder.mLoadingView.startAnim();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (mItemListener != null) {
                mItemListener.onInteraction(mList.get(position).getId());
            }

            holder.mLoadingView.stopAnim();
            holder.mLoadingView.setVisibility(View.GONE);

            if (position == 0) {
                Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageView);
            } else {
                if (!mList.get(position - 1).getType().contains("问题")) {
                    Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageView);
                }
            }

            holder.textView.setText(mList.get(position).getLabel());
        }, 2000);
    }

    private void bindQuestionHolder2(RecyclerView.ViewHolder viewHolder, int position) {
        QuestionHolder2 holder = (QuestionHolder2) viewHolder;
        holder.mLoadingView.setViewColor(mContext.getResources().getColor(R.color.black));
        holder.mLoadingView.startAnim();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (mItemListener != null) {
                mItemListener.onInteraction(mList.get(position).getId());
            }

            if (position == 0) {
                Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageView);
            } else {
                if (!mList.get(position - 1).getType().contains("问题")) {
                    Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageView);
                }
            }

            holder.mLoadingView.stopAnim();
            holder.mLoadingView.setVisibility(View.GONE);
            holder.textView.setText(mList.get(position).getLabel());
        }, 2000);
    }

    private void bindAnswerHolder1(RecyclerView.ViewHolder viewHolder, int position) {
        AnswerHolder1 holder = (AnswerHolder1) viewHolder;
        Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageView);
        holder.textView.setText(mList.get(position).getLabel());
        if (mItemListener != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> mItemListener.onInteraction(mList.get(position).getId()), 500);
        }
    }

    private void bindAnswerHolder2(RecyclerView.ViewHolder viewHolder, int position) {
        AnswerHolder2 holder = (AnswerHolder2) viewHolder;
        holder.textView.setText(mList.get(position).getLabel());
    }

    private void bindAnswerHolder3(RecyclerView.ViewHolder viewHolder, int position) {
        AnswerHolder3 holder = (AnswerHolder3) viewHolder;
        Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageView);
        Glide.with(mContext).load(StringUtil.StringToBitMap(mList.get(position).getLabel())).apply(new RequestOptions().error(R.mipmap.ic_launcher)).into(holder.imagePhoto);
        if (mItemListener != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> mItemListener.onInteraction(mList.get(position).getId()), 500);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        switch (mList.get(position).getType()) {
            case "问题1":
                type = 0;
                break;
            case "问题2":
                type = 1;
                break;
            case "回答1":
                type = 2;
                break;
            case "回答2":
                type = 3;
                break;
            case "回答3":
                type = 4;
                break;
            default:
                type = 0;
                break;
        }
        return type;
    }

    public class QuestionHolder1 extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        private LVCircularJump mLoadingView;

        public QuestionHolder1(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_main_question_avatar_iv);
            textView = itemView.findViewById(R.id.item_main_question_content_tv);
            mLoadingView = itemView.findViewById(R.id.item_main_question_loading_lvcj);
        }
    }

    private class QuestionHolder2 extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        private LVCircularJump mLoadingView;

        public QuestionHolder2(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_main_question2_avatar_iv);
            textView = itemView.findViewById(R.id.item_main_question2_content_tv);
            mLoadingView = itemView.findViewById(R.id.item_main_question2_loading_lvcj);
        }
    }

    public class AnswerHolder1 extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public AnswerHolder1(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_main_answer_avatar_iv);
            textView = itemView.findViewById(R.id.item_main_answer_content_tv);
        }
    }

    private class AnswerHolder2 extends RecyclerView.ViewHolder {

        private TextView textView;

        public AnswerHolder2(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_main_answer2_content_tv);
        }
    }

    private class AnswerHolder3 extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageView imagePhoto;

        public AnswerHolder3(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_main_answer3_avatar_iv);
            imagePhoto = itemView.findViewById(R.id.item_main_answer3_photo_iv);
        }
    }
}
