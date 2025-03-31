package com.m7.imkfsdk.chat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.m7.imkfsdk.R;


public class XbotHeadNoteHolder extends RecyclerView.ViewHolder {

     public final TextView tv_formNotes;


    public XbotHeadNoteHolder(View itemView) {
        super(itemView);
        tv_formNotes= itemView.findViewById(R.id.tv_formNotes);
      }

}
