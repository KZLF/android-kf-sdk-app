package com.m7.imkfsdk.chat.holder;

import android.view.View;

/**
 * Created by longwei on 2017/12/11.
 */

public class UnknownHolder extends BaseHolder{


    public UnknownHolder(int type) {
        super(type);
    }

    public BaseHolder initBaseHolder(View baseView, boolean isReceive) {
        super.initBaseHolder(baseView);
        return this;
    }


}
