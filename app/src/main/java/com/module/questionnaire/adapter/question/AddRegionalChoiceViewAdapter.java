package com.module.questionnaire.adapter.question;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.module.questionnaire.R;
import com.module.questionnaire.bean.RegionalChoiceBean;

import java.util.List;

public class AddRegionalChoiceViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<RegionalChoiceBean> mList;

    public AddRegionalChoiceViewAdapter(Context context, List<RegionalChoiceBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    private ItemClickListener mOnItemClickListener;

    public interface ItemClickListener {

        void onItemClick(String id, String value);
    }

    public void setOnItemListener(ItemClickListener itemListener) {
        this.mOnItemClickListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        AddRegionalChoiceViewHolder holder = new AddRegionalChoiceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_regional_choice_view, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        AddRegionalChoiceViewHolder holder = (AddRegionalChoiceViewHolder) viewHolder;
        holder.textView.setText(mList.get(position).getOther_name());
        holder.itemView.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mList.get(position).getId(), holder.textView.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class AddRegionalChoiceViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public AddRegionalChoiceViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_add_regional_choice_view_value_tv);
        }
    }
}
