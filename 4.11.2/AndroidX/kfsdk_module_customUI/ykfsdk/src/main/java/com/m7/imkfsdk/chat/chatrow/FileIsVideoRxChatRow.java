package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.YKFVideoActivity;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.FileIsVideoViewHolder;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.model.entity.FromToMessage;

/**
 * Created by longwei on 2016/3/10.
 */
public class FileIsVideoRxChatRow extends BaseChatRow {

    public FileIsVideoRxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        FileIsVideoViewHolder holder = (FileIsVideoViewHolder) baseHolder;
        final FromToMessage message = detail;
        if (message != null) {
            if (message.withDrawStatus) {
                holder.getWithdrawTextView().setVisibility(View.VISIBLE);
                holder.getContainer().setVisibility(View.GONE);
            } else {
                holder.getWithdrawTextView().setVisibility(View.GONE);
                holder.getContainer().setVisibility(View.VISIBLE);



                if (IMChatManager.getInstance().getImageLoader() != null) {
                    IMChatManager.getInstance().getImageLoader().loadVideoImage(message.message,holder.getImageView(),0,0,1000000,8,null);
                } else {
                    MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
                }




                holder.getImageView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, YKFVideoActivity.class);
                        intent.putExtra(YKFConstants.YKFVIDEOPATHURI, message.message);
                        intent.putExtra(YKFConstants.YKFVIDEOFILENAME, message.fileName);
                        context.startActivity(intent);
                    }
                });

            }

        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_fileisvideo_rx, null);
            FileIsVideoViewHolder holder = new FileIsVideoViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.RECEIVED_FILE_IS_VIDEO.ordinal();
    }
}
