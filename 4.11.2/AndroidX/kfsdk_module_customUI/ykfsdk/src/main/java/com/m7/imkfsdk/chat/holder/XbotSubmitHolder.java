package com.m7.imkfsdk.chat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.m7.imkfsdk.R;


public class XbotSubmitHolder extends RecyclerView.ViewHolder {

     public final Button bt_form_submit;


    public XbotSubmitHolder(View itemView) {
        super(itemView);
        bt_form_submit= itemView.findViewById(R.id.bt_form_submit);
      }

}
