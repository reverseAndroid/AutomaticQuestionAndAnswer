package com.module.questionnaire.adapter;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ldoublem.loadingviewlib.view.LVCircularJump;
import com.module.questionnaire.R;
import com.module.questionnaire.bean.QuestionAnswerBean;
import com.module.questionnaire.utils.StringBitmapUtil;

import java.io.IOException;
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
    private ItemUpdateListener mItemUpdateListener;

    public interface ItemUpdateListener {
        void onQuestionInteraction(String id);

        void onAnswerInteraction();
    }

    public void setItemUpdateListener(ItemUpdateListener itemUpdateListener) {
        this.mItemUpdateListener = itemUpdateListener;
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
            if (mItemUpdateListener != null) {
                if (mList.get(position).isContinuous()) {
                    mItemUpdateListener.onAnswerInteraction();
                } else {
                    mItemUpdateListener.onQuestionInteraction(String.valueOf(mList.get(position).getId()));
                }
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
            if (mItemUpdateListener != null) {
                mItemUpdateListener.onQuestionInteraction(String.valueOf(mList.get(position).getId()));
            }

            if (position == 0) {
                Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageAvatar);
            } else {
                if (!mList.get(position - 1).getType().contains("问题")) {
                    Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageAvatar);
                }
            }

            final boolean[] isInit = {false};
            MediaPlayer mediaPlayer = new MediaPlayer();
            //给imageAudioPlayer设置背景喇叭图，必须在代码中设置，在布局中设置会导致animationDrawable空指针
            holder.imageAudioPlayer.setImageResource(R.drawable.audio_player_question);
            //将imageAudioPlayer的背景喇叭图赋值给animationDrawable
            AnimationDrawable animationDrawable = (AnimationDrawable) holder.imageAudioPlayer.getDrawable();
            try {
                AssetFileDescriptor fd = mContext.getAssets().openFd("test.mp3");
                mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                mediaPlayer.prepare();
                isInit[0] = false;
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.mLoadingView.stopAnim();
            holder.mLoadingView.setVisibility(View.GONE);
            holder.textView.setText(Double.valueOf(mediaPlayer.getDuration() / 1000).intValue() + "s");
            holder.itemView.setOnClickListener(view -> {
                try {
                    if (!mediaPlayer.isPlaying()) {
                        animationDrawable.start();
                        //重新加载音频文件，如果不重新加载，无法再次播放(等到真实环境，这里是网络资源文件，需要使用通过异步的方式装载媒体资源mediaPlayer.prepareAsync())
                        if (isInit[0]) {
                            AssetFileDescriptor fd = mContext.getAssets().openFd("test.mp3");
                            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                            mediaPlayer.prepare();
                        }
                        mediaPlayer.start();
                    } else {
                        animationDrawable.stop();
                        animationDrawable.selectDrawable(0);
                        mediaPlayer.reset();
                        mediaPlayer.stop();
                        isInit[0] = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                if (!mediaPlayer1.isPlaying()) {
                    mediaPlayer.reset();
                    mediaPlayer.stop();
                    animationDrawable.stop();
                    animationDrawable.selectDrawable(0);
                    isInit[0] = true;
                }
            });
        }, 2000);
    }

    private void bindAnswerHolder1(RecyclerView.ViewHolder viewHolder, int position) {
        AnswerHolder1 holder = (AnswerHolder1) viewHolder;
        Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageView);
        holder.textView.setText(mList.get(position).getLabel());
        if (mItemUpdateListener != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> mItemUpdateListener.onAnswerInteraction(), 500);
        }
    }

    private void bindAnswerHolder2(RecyclerView.ViewHolder viewHolder, int position) {
        AnswerHolder2 holder = (AnswerHolder2) viewHolder;
        Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageView);
        Glide.with(mContext).load(StringBitmapUtil.StringToBitMap(mList.get(position).getLabel())).apply(new RequestOptions().error(R.mipmap.ic_launcher)).into(holder.imagePhoto);
        if (mList.get(position).getId() == 19) {
            Toast.makeText(mContext, mList.get(position).getLabel(), Toast.LENGTH_SHORT).show();
        }
        if (mItemUpdateListener != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> mItemUpdateListener.onAnswerInteraction(), 500);
        }
    }

    private void bindAnswerHolder3(RecyclerView.ViewHolder viewHolder, int position) {
        AnswerHolder3 holder = (AnswerHolder3) viewHolder;
        Glide.with(mContext).load(R.mipmap.ic_launcher).apply(new RequestOptions().circleCrop()).into(holder.imageAvatar);

        final boolean[] isInit = {false};
        MediaPlayer mediaPlayer = new MediaPlayer();
        //给imageAudioPlayer设置背景喇叭图，必须在代码中设置，在布局中设置会导致animationDrawable空指针
        holder.imageAudioPlayer.setImageResource(R.drawable.audio_player_answer);
        //将imageAudioPlayer的背景喇叭图赋值给animationDrawable
        AnimationDrawable animationDrawable = (AnimationDrawable) holder.imageAudioPlayer.getDrawable();
        try {
            mediaPlayer.setDataSource(mList.get(position).getLabel());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.textView.setText(Double.valueOf(mediaPlayer.getDuration() / 1000).intValue() + "s");
        holder.itemView.setOnClickListener(view -> {
            try {
                if (!mediaPlayer.isPlaying()) {
                    animationDrawable.start();
                    //重新加载音频文件，如果不重新加载，无法再次播放(等到真实环境，这里是网络资源文件，需要使用通过异步的方式装载媒体资源mediaPlayer.prepareAsync())
                    if (isInit[0]) {
                        mediaPlayer.setDataSource(mList.get(position).getLabel());
                        mediaPlayer.prepare();
                    }
                    mediaPlayer.start();
                } else {
                    animationDrawable.stop();
                    animationDrawable.selectDrawable(0);
                    mediaPlayer.reset();
                    mediaPlayer.stop();
                    isInit[0] = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            if (!mediaPlayer1.isPlaying()) {
                mediaPlayer.reset();
                mediaPlayer.stop();
                animationDrawable.stop();
                animationDrawable.selectDrawable(0);
                isInit[0] = true;
            }
        });

        if (mItemUpdateListener != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> mItemUpdateListener.onAnswerInteraction(), 500);
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
            case "文本问题":
                type = 0;
                break;
            case "语音问题":
                type = 1;
                break;
            case "文本回答":
                type = 2;
                break;
            case "图片回答":
                type = 3;
                break;
            case "语音回答":
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

        private ImageView imageAvatar;
        private ImageView imageAudioPlayer;
        private TextView textView;
        private LVCircularJump mLoadingView;

        public QuestionHolder2(View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.item_main_question2_avatar_iv);
            imageAudioPlayer = itemView.findViewById(R.id.item_main_question2_audio_player_iv);
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

        private ImageView imageView;
        private ImageView imagePhoto;

        public AnswerHolder2(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_main_answer3_avatar_iv);
            imagePhoto = itemView.findViewById(R.id.item_main_answer3_photo_iv);
        }
    }

    private class AnswerHolder3 extends RecyclerView.ViewHolder {

        private ImageView imageAvatar;
        private ImageView imageAudioPlayer;
        private TextView textView;

        public AnswerHolder3(View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.item_main_answer4_avatar_iv);
            imageAudioPlayer = itemView.findViewById(R.id.item_main_answer4_audio_player_iv);
            textView = itemView.findViewById(R.id.item_main_answer4_content_tv);
        }
    }
}
