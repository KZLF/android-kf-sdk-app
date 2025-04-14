package com.m7.imkfsdk.chat.holder;

import android.view.View;
import android.widget.LinearLayout;

import com.m7.imkfsdk.R;

public class QuickMenuViewHolder extends BaseHolder{

    private LinearLayout ll_fast;

    public QuickMenuViewHolder(int type) {
        super(type);
    }

    public BaseHolder initBaseHolder(View baseView, boolean isReceive) {
        super.initBaseHolder(baseView);

        //通过baseview找到对应组件
        ll_fast = baseView.findViewById(R.id.ll_fast);

        return this;
    }

    public LinearLayout getLl_fast() {
        return ll_fast;
    }



}
