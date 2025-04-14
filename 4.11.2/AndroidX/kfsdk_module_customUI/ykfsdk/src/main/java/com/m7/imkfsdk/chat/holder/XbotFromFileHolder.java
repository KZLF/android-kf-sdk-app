package com.m7.imkfsdk.chat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m7.imkfsdk.R;


public class XbotFromFileHolder extends RecyclerView.ViewHolder {

    public final TextView tv_title;
    public final TextView tv_required;
    public final LinearLayout ll_xbot_file;
    public final RelativeLayout rl_xbot_form_addfile;

    public XbotFromFileHolder(View itemView) {
        super(itemView);
        this.rl_xbot_form_addfile=itemView.findViewById(R.id.rl_xbot_form_addfile);
        this.ll_xbot_file=itemView.findViewById(R.id.ll_xbot_file);
        this.tv_title = itemView.findViewById(R.id.tv_title);
        this.tv_required = itemView.findViewById(R.id.tv_required);
    }

}
