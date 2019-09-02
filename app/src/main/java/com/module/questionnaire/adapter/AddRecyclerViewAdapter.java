package com.module.questionnaire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.module.questionnaire.R;
import com.module.questionnaire.bean.RadioRecyclerViewBean;

import java.util.ArrayList;
import java.util.List;

public class AddRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<RadioRecyclerViewBean> mList;
    public List<AddRecyclerViewHolder> holderList = new ArrayList<>();

    public AddRecyclerViewAdapter(Context context, List<RadioRecyclerViewBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    private ItemClickListener mOnItemClickListener;

    public interface ItemClickListener {

        void onItemClick(boolean isChecked, int position);
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
        holderList.add(holder);
        holder.textView.setText(mList.get(position).getValue());
        holder.radioButton.setChecked(mList.get(position).isSelect());
        holder.radioButton.setClickable(false);
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                if (!holder.radioButton.isChecked()) {
                    holder.radioButton.setChecked(true);
                } else {
                    holder.radioButton.setChecked(false);
                }
                mOnItemClickListener.onItemClick(holder.radioButton.isChecked(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int selectionItem() {
        int position = 0;
        for (int i = 0; i < holderList.size(); i++) {
            if (holderList.get(i).radioButton.isChecked()) {
                position = i;
            }
        }
        return position;
    }

    private class AddRecyclerViewHolder extends RecyclerView.ViewHolder {

        private RadioButton radioButton;
        private TextView textView;

        public AddRecyclerViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.item_main_recycler_view_rb);
            textView = itemView.findViewById(R.id.item_main_recycler_view_value_tv);
        }
    }
}
