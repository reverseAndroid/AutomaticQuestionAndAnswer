package com.module.questionnaire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.module.questionnaire.R;
import com.module.questionnaire.bean.response.MeListResponse;

import java.util.List;

public class ItemMeListAdapter extends RecyclerView.Adapter<ItemMeListAdapter.ViewHolder> {

    private Context mContext;
    private List<MeListResponse.DataBean> mList;

    public ItemMeListAdapter(Context context, List<MeListResponse.DataBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(MeListResponse.DataBean bean);
    }

    public void setOnItemClickListener(OnItemClickListener itemListener) {
        this.mOnItemClickListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_me_list_info, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ViewHolder holder = viewHolder;
        Glide.with(mContext).load(mList.get(i).getDesc()).apply(new RequestOptions().placeholder(R.drawable.icon_default_user_avatar)).into(holder.imageIcon);
        holder.textTitle.setText(mList.get(i).getTitle());
        holder.itemView.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageIcon;
        TextView textTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.item_me_list_info_icon_iv);
            textTitle = itemView.findViewById(R.id.item_me_list_info_title_tv);
        }
    }
}
