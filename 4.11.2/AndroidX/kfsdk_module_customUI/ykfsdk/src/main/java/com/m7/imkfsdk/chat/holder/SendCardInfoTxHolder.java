package com.m7.imkfsdk.chat.holder;

import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m7.imkfsdk.R;

/**
*@Description 发出的卡片信息holder
*@author R-D
*@date 2019-12-24
*/
public class SendCardInfoTxHolder extends BaseHolder {

    public ImageView iv_child_img;
    public TextView tv_child_title;
    public TextView tv_child_;
    public TextView tv_child_num;
    public TextView tv_child_price;
    public TextView tv_child_state;
    public  LinearLayout kf_chat_rich_lin;
    public LinearLayout ll_other_title;
    public ConstraintLayout chatting_text_root; // 新加的。
    public View line;
    public TextView tv_logistics_tx_price;
    public TextView tv_logistics_tx_num;
    public TextView tv_logistics_tx_state;

    public SendCardInfoTxHolder(int type) {
        super(type);
    }


    public BaseHolder initBaseHolder(View baseView, boolean isReceive) {
        super.initBaseHolder(baseView);
        iv_child_img = baseView.findViewById(R.id.iv_child_img);
        tv_child_title = baseView.findViewById(R.id.tv_child_title);
        tv_child_ = baseView.findViewById(R.id.tv_child_);
        tv_child_num = baseView.findViewById(R.id.tv_child_num);
        tv_child_price = baseView.findViewById(R.id.tv_child_price);
        tv_child_state = baseView.findViewById(R.id.tv_child_state);
        progressBar =  baseView.findViewById(R.id.uploading_pb);
        kf_chat_rich_lin=baseView.findViewById(R.id.kf_chat_rich_lin);
        ll_other_title = baseView.findViewById(R.id.ll_other_title);
        progressBar =  baseView.findViewById(R.id.uploading_pb);
        chatting_text_root = baseView.findViewById(R.id.chatting_text_root);
        line = baseView.findViewById(R.id.line);
        tv_logistics_tx_price = baseView.findViewById(R.id.tv_logistics_tx_price);
        tv_logistics_tx_num = baseView.findViewById(R.id.tv_logistics_tx_num);
        tv_logistics_tx_state = baseView.findViewById(R.id.tv_logistics_tx_state);
        return this;
    }
}