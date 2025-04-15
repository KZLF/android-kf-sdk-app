package com.m7.imkfsdk.chat.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.moor.imkf.model.entity.FlowBean;

import java.util.List;

/**
 * <p>
 * Package Name:com.m7.imkfsdk.chat.adapter$
 * </p>
 * <p>
 * Class Name:ChatTagLabelsAdapter$
 * <p>
 * Description:聊天页面底部的横向滚动推荐条
 * </p>
 *
 * @Author ChenBo
 * @Version 1.0 2019/9/19$ 11:00$ Release
 * @Reviser:
 * @Modification Time:2019/9/19$ 11:00$
 */
public class ChatTagLabelsAdapter extends RecyclerView.Adapter<ChatTagLabelsAdapter.ChatTagViewHolder> {
    List<FlowBean> datas;

    public ChatTagLabelsAdapter(List<FlowBean> datas) {
        this.datas = datas;
    }

    public void refreshData(List<FlowBean> datas) {
        this.datas.clear();
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public ChatTagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.ykfsdk_item_chat_tag_label, null);
        return new ChatTagViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatTagViewHolder holder, final int position) {
        final FlowBean data = datas.get(position);
        holder.tvFlowItem.setText(data.getButton());
        holder.itemView.setBackgroundResource(data.getBadge_flag() == 1
                ? R.drawable.ykfsdk_kf_chat_bottom_button_enhanced
                : R.drawable.ykfsdk_kf_chat_input_bg);
        holder.vDot.setVisibility(data.getBadge_flag() == 1 ? View.VISIBLE : View.GONE);
        holder.tvFlowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.OnItemClick(data);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    static class ChatTagViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlowItem;
        View vDot;

        public ChatTagViewHolder(View itemView) {
            super(itemView);
            tvFlowItem = itemView.findViewById(R.id.tv_flowItem);
            vDot = itemView.findViewById(R.id.v_flow_item_dot);
        }
    }

    private onItemClickListener mListener;

    public interface onItemClickListener {
        void OnItemClick(FlowBean flowBean);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }
}
