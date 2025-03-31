package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.TextViewHolder;
import com.m7.imkfsdk.view.PopupWindowList;
import com.moor.imkf.model.entity.FromToMessage;

/**
 * Created by longwei on 2016/3/9.
 */
public class XbotFormSubmitTxChatRow extends BaseChatRow {

    private Context context;
    private PopupWindowList mPopupWindowList;

    public XbotFormSubmitTxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        this.context = context;
        final TextViewHolder holder = (TextViewHolder) baseHolder;
        final FromToMessage message = detail;
        if (message != null) {
            View.OnClickListener listener = ((ChatActivity) context).getChatAdapter().getOnClickListener();
            getMsgStateResId(position, holder, message, listener);

        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_xbot_submit_tx, null);
            TextViewHolder holder = new TextViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }

        return convertView;
    }



    @Override
    public int getChatViewType() {
        return ChatRowType.XBOT_FORM_DATA_SUBMIT.ordinal();
    }


}
