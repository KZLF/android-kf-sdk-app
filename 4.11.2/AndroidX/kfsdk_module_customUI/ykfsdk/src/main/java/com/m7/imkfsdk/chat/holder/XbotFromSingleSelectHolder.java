package com.m7.imkfsdk.chat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.view.dropdownmenu.DropDownMenu;


public class XbotFromSingleSelectHolder extends RecyclerView.ViewHolder {

    public final TextView tv_title;
    public final TextView tv_required;
    public final DropDownMenu drop_single;
    public XbotFromSingleSelectHolder(View itemView) {
        super(itemView);
        this.drop_single = itemView.findViewById(R.id.drop_single);
        this.tv_title = itemView.findViewById(R.id.tv_title);
        this.tv_required = itemView.findViewById(R.id.tv_required);
    }

}
