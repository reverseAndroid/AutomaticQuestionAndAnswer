package com.module.questionnaire.adapter.question;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.module.questionnaire.R;
import com.module.questionnaire.bean.MultipleSelectionBean;

import java.util.List;

public class MultipleSelectionViewAdapter extends RecyclerView.Adapter<MultipleSelectionViewAdapter.ViewHolder> {

    private Context mContext;
    private List<MultipleSelectionBean> mList;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int i, boolean isSelection);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public MultipleSelectionViewAdapter(Context context, List<MultipleSelectionBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public MultipleSelectionViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.item_multiple_selection, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MultipleSelectionViewAdapter.ViewHolder viewHolder, int i) {
        ViewHolder holder = viewHolder;
        holder.textView.setText(mList.get(i).getTitle());
        if (mList.get(i).isSelect()) {
            holder.imageView.setBackgroundResource(R.drawable.icon_multiple_selection);
        } else {
            holder.imageView.setBackgroundResource(R.drawable.icon_multiple_unselection);
        }

        holder.itemView.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                if (mList.get(i).isSelect()) {
                    mOnItemClickListener.onItemClick(i, false);
                } else {
                    mOnItemClickListener.onItemClick(i, true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_multiple_selection_title_tv);
            imageView = itemView.findViewById(R.id.item_multiple_selection_iv);
        }
    }
}
