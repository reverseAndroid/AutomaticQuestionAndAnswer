package com.module.questionnaire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.module.questionnaire.R;
import com.module.questionnaire.bean.QuestionAnswerBean;

import java.util.List;

public class AddRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<QuestionAnswerBean.Item> mList;

    public AddRecyclerViewAdapter(Context context, List<QuestionAnswerBean.Item> list) {
        this.mContext = context;
        this.mList = list;
    }

    private ItemClickListener mOnItemClickListener;

    public interface ItemClickListener {

        void onItemClick(String value);
    }

    public void setOnItemListener(ItemClickListener itemListener) {
        this.mOnItemClickListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        AddRecyclerViewHolder holder = new AddRecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_recycler_view, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AddRecyclerViewHolder holder = (AddRecyclerViewHolder) viewHolder;
        holder.textView.setText(mList.get(position).getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mList.get(position).getValue());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class AddRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public AddRecyclerViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_main_recycler_view_value_tv);
        }
    }
}
