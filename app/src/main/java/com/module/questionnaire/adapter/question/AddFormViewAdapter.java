package com.module.questionnaire.adapter.question;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.module.questionnaire.R;
import com.module.questionnaire.bean.response.QuestionResponse;
import com.module.questionnaire.widget.DrawableEditText;

import java.util.ArrayList;
import java.util.List;

public class AddFormViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<QuestionResponse.DataBean> mList;
    private List<AddFormViewHolder> mHolderList = new ArrayList<>();

    public AddFormViewAdapter(Context context, List<QuestionResponse.DataBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    private ItemClickListener mOnItemClickListener;

    public interface ItemClickListener {

        void onItemClick(EditText editText, String value);
    }

    public void setOnItemListener(ItemClickListener itemListener) {
        this.mOnItemClickListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        AddFormViewHolder holder = new AddFormViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_form_view, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        AddFormViewHolder holder = (AddFormViewHolder) viewHolder;
        mHolderList.add(holder);
        holder.editText.setHint(mList.get(i).getLabel());
        if (mList.get(i).getLabel().contains("电话")) {
            Drawable drawable = holder.editText.getContext().getResources().getDrawable(R.drawable.icon_form_contact);
            drawable.setBounds(0, 0, 45, 45);
            holder.editText.setCompoundDrawables(null, null, drawable, null);
            holder.editText.setOnDrawableRightListener(() -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.editText, "0");
                }
            });
        } else {
            holder.editText.setCompoundDrawables(null, null, null, null);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public String getAllEditText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < mList.size(); i++) {
            if (!TextUtils.isEmpty(mHolderList.get(i).editText.getText())) {
                text.append(mList.get(i).getLabel()).append(mHolderList.get(i).editText.getText());
            } else {
                return "";
            }
        }
        return text.toString();
    }

    private class AddFormViewHolder extends RecyclerView.ViewHolder {

        private DrawableEditText editText;

        public AddFormViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.item_add_form_view_et);
        }
    }
}
