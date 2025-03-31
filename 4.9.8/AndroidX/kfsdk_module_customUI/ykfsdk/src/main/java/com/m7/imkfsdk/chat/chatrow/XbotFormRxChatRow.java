package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.dialog.BottomXbotFormDialog;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.XbotFormRxHolder;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.XbotForm;

/**
 * Created by longwei on 2017/12/11.
 */

public class XbotFormRxChatRow extends BaseChatRow{


    public XbotFormRxChatRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_xbot_form_rx, null);
            XbotFormRxHolder holder = new XbotFormRxHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.XBOT_FORM_DATA_RECEIVED.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, FromToMessage detail, int position) {

        XbotFormRxHolder holder = (XbotFormRxHolder) baseHolder;
        final FromToMessage message = detail;
        if (message != null) {
            if(!TextUtils.isEmpty(message.xbotForm)){
                final XbotForm xbotForm=new Gson().fromJson(message.xbotForm,XbotForm.class);


                if(!TextUtils.isEmpty(xbotForm.formPrompt)){
                    holder.getformPromptView().setText(xbotForm.formPrompt);
                }


                if(message.isSubmitXbotForm){
                    holder.getformNameView().setVisibility(View.GONE);
                }else {
                    holder.getformNameView().setVisibility(View.VISIBLE);
                }

                if(!TextUtils.isEmpty(xbotForm.formName)){
                    holder.getformNameView().setText(xbotForm.formName);

                    holder.getformNameView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //弹出填写表单 ，数据源来自新转换的对象。
                            XbotForm xbotForm_data=new Gson().fromJson(message.xbotForm,XbotForm.class);

                            BottomXbotFormDialog xbotFormDialog= BottomXbotFormDialog.init(xbotForm_data.formName,xbotForm_data,message._id);
                            xbotFormDialog.show(((ChatActivity)context).getSupportFragmentManager(),"");
                        }
                    });
                }

            }

        }
    }
}
