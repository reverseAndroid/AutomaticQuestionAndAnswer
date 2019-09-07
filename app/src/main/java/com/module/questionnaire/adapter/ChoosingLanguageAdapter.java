package com.module.questionnaire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.module.questionnaire.R;
import com.module.questionnaire.bean.ChoosingLanguageBean;

import java.util.List;

public class ChoosingLanguageAdapter extends RecyclerView.Adapter<ChoosingLanguageAdapter.ViewHolder> {

    private Context mContext;
    private List<ChoosingLanguageBean> mList;

    public ChoosingLanguageAdapter(Context context, List<ChoosingLanguageBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemListener) {
        this.mOnItemClickListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_choosing_language, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.imageIcon.setImageResource(mList.get(position).getIcon());
        viewHolder.textLanguage.setText(mList.get(position).getLanguage());
        viewHolder.textRendering.setText(mList.get(position).getRendering());

        if (mList.get(position).isSelection()) {
            viewHolder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.choosing_language_select_bg));
        } else {
            viewHolder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.choosing_language_unselect_bg));
        }

        viewHolder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageIcon;
        TextView textLanguage;
        TextView textRendering;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.choosing_language_icon_iv);
            textLanguage = itemView.findViewById(R.id.choosing_language_language_tv);
            textRendering = itemView.findViewById(R.id.choosing_language_rendering_tv);
        }
    }
}
