package com.m7.imkfsdk.chat.holder;

import android.view.View;
import android.widget.TextView;

import com.m7.imkfsdk.R;

/**
 * Created by longwei on 2016/3/9.
 */
public class InvestigateCancelViewHolder extends BaseHolder {

    public TextView contentTv;
    public TextView chatting_tv_to_investigate;
    public TextView tv_investigate_content;

    /**
     * @param type
     */
    public InvestigateCancelViewHolder(int type) {
        super(type);

    }

    public BaseHolder initBaseHolder(View baseView, boolean receive) {
        super.initBaseHolder(baseView);
        chattingTime = baseView.findViewById(R.id.chatting_time_tv);
        if (!receive) {
            tv_investigate_content = baseView.findViewById(R.id.tv_investigate_content);
        } else {
            chatting_tv_to_investigate = baseView.findViewById(R.id.chatting_tv_to_investigate);
            uploadState = baseView.findViewById(R.id.chatting_state_iv);
            contentTv = baseView.findViewById(R.id.chatting_content_itv);
        }
        return this;
    }


}
