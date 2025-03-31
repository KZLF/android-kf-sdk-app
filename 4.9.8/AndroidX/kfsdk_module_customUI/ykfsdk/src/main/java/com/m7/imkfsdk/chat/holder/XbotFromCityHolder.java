package com.m7.imkfsdk.chat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.m7.imkfsdk.R;


public class XbotFromCityHolder extends RecyclerView.ViewHolder {

    public final TextView tv_title;
    public final TextView tv_city;
    public final TextView tv_required;

    public XbotFromCityHolder(View itemView) {
        super(itemView);
        this.tv_title = itemView.findViewById(R.id.tv_title);
        this.tv_required = itemView.findViewById(R.id.tv_required);
        this.tv_city=itemView.findViewById(R.id.tv_city);
    }

}
