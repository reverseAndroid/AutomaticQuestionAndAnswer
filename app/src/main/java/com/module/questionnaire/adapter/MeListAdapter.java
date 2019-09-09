package com.module.questionnaire.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.module.questionnaire.R;
import com.module.questionnaire.bean.response.MeListResponse;

import java.util.List;

public class MeListAdapter extends RecyclerView.Adapter<MeListAdapter.ViewHolder> {

    private Context mContext;
    private List<List<MeListResponse.DataBean>> mList;
    public ItemMeListAdapter mItemAdapter;

    public MeListAdapter(Context context, List<List<MeListResponse.DataBean>> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_me_list, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ViewHolder holder = viewHolder;
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        holder.recyclerView.setLayoutManager(layoutManager);
        mItemAdapter = new ItemMeListAdapter(mContext, mList.get(i));
        holder.recyclerView.setAdapter(mItemAdapter);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.item_me_list_rv);
        }
    }
}
