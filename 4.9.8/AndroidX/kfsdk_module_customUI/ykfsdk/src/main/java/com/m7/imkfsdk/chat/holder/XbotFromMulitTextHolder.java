package com.m7.imkfsdk.chat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.m7.imkfsdk.R;


public class XbotFromMulitTextHolder extends RecyclerView.ViewHolder {

    public final TextView tv_title;
    public final TextView tv_required;
    public final EditText et_mulit;
    public XbotFromMulitTextHolder(View itemView) {
        super(itemView);
        this.et_mulit = itemView.findViewById(R.id.et_mulit);
        this.tv_title = itemView.findViewById(R.id.tv_title);
        this.tv_required = itemView.findViewById(R.id.tv_required);
    }

}
