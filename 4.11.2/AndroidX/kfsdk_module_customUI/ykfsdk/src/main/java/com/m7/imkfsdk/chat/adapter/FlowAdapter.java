package com.m7.imkfsdk.chat.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.model.entity.FlowBean;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.YKFChatStatusEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FlowAdapter extends RecyclerView.Adapter<FlowAdapter.MyViewHolder> {
    private final OnItemClickListenr onItemClickListenr;
    public List<FlowBean> data = new ArrayList<>();
    public HashMap<Integer,Boolean> map_flow=new HashMap<Integer,Boolean>();
    private final Context context;
    private boolean isMulit=false;//是否多选
    private final FromToMessage detail;

    public FlowAdapter(Context context, List<FlowBean> strings,boolean isMulit, FromToMessage detail,OnItemClickListenr onItemClickListenr) {
        this.onItemClickListenr = onItemClickListenr;
        this.data = strings;
        this.context = context;
        this.isMulit=isMulit;
        this.detail=detail;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //    Log.i("GCS", "onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ykfsdk_layout_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //   Log.i("GCS","onBindViewHolder = "+position);
        final FlowBean flowBean = data.get(position);
        holder.tv_flowItem.setText(flowBean.getButton());

        if(flowBean.isChoose()){
            holder.iv_choose_flow.setVisibility(View.VISIBLE);
            holder.tv_flowItem.setBackgroundResource(R.drawable.ykfsdk_ykf_bg_flow_btn);
        }else{
            holder.iv_choose_flow.setVisibility(View.GONE);
            holder.tv_flowItem.setBackgroundResource(R.drawable.ykfsdk_bg_flow_item);
        }


//        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) holder.tv_flowItem.getLayoutParams();
//        params.width=flow_itemwidth;
//        holder.tv_flowItem.setLayoutParams(params);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IMChatManager.getInstance().getYkfChatStatusEnum()!= YKFChatStatusEnum.KF_Robot_Status) {
                    //如果不是机器人不能点击xbot按钮
                    return;
                }
                if(detail.isFlowSelect){
                    return;
                }
                flowBean.setChoose(!flowBean.isChoose());
                notifyDataSetChanged();
                onItemClickListenr.setOnButtonClickListenr(position,flowBean.isChoose(),flowBean.getText());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_flowItem;
        ImageView iv_choose_flow;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_flowItem = itemView.findViewById(R.id.tv_flowItem);//文字item
            iv_choose_flow= itemView.findViewById(R.id.iv_choose_flow);//选择图标
        }
    }

    public interface OnItemClickListenr {
        void setOnButtonClickListenr(int position , boolean is,String msg);
    }
}
