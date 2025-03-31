package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.YKFVideoActivity;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.FileIsVideoViewHolder;
import com.m7.imkfsdk.utils.MoorFileUtils;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.model.entity.FromToMessage;

/**
 * Created by longwei on 2016/3/10.
 */
public class FileIsVideoTxChatRow extends BaseChatRow {


    public FileIsVideoTxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        final FileIsVideoViewHolder holder = (FileIsVideoViewHolder) baseHolder;
        final FromToMessage message = detail;
        if (message != null) {
            String imgPath = message.filePath;
            if (TextUtils.isEmpty(imgPath) || !MoorFileUtils.isFileExists(imgPath)) {
                imgPath = message.message;
            }

            if ("true".equals(message.sendState) || "false".equals(message.sendState)) {
                holder.getChat_content_pb_progress().setVisibility(View.GONE);

            } else {
                holder.getChat_content_pb_progress().setVisibility(View.VISIBLE);
            }
            holder.getChat_content_pb_progress().setProgress(message.fileProgress);

            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadVideoImage(imgPath,
                        holder.getImageView(), 0, 0, 1000000, 0, null);
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }


            final String finalImgPath = imgPath;
            holder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, YKFVideoActivity.class);
                    intent.putExtra(YKFConstants.YKFVIDEOPATHURI, finalImgPath);
                    intent.putExtra(YKFConstants.YKFVIDEOFILENAME, message.fileName);
                    context.startActivity(intent);
                }
            });

            View.OnClickListener listener = ((ChatActivity) context).getChatAdapter().getOnClickListener();
            getMsgStateResId(position, holder, message, listener);
        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_fileisviedo_tx, null);
            FileIsVideoViewHolder holder = new FileIsVideoViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }

        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.SEND_FILE_IS_VIDEO.ordinal();
    }
}
