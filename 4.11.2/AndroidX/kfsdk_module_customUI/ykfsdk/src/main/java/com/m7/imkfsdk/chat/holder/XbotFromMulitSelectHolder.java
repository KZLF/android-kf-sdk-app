package com.m7.imkfsdk.chat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.view.MulitTagView;


public class XbotFromMulitSelectHolder extends RecyclerView.ViewHolder {

    public final TextView tv_title;
    public final TextView tv_required;
    public final MulitTagView tagView;

    public XbotFromMulitSelectHolder(View itemView) {
        super(itemView);

        this.tv_title = itemView.findViewById(R.id.tv_title);
        this.tv_required = itemView.findViewById(R.id.tv_required);
        this.tagView=itemView.findViewById(R.id.mtv_tagview);
    }

}
