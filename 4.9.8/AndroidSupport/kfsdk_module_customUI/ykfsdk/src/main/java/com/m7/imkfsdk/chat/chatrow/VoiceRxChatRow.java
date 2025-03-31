package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.VoiceViewHolder;
import com.moor.imkf.db.dao.MessageDao;
import com.moor.imkf.lib.http.donwload.IMoorOnDownloadListener;
import com.moor.imkf.lib.http.donwload.MoorDownLoadUtils;
import com.moor.imkf.model.entity.FromToMessage;

import java.util.UUID;

/**
 * Created by longwei on 2016/3/9.
 */
public class VoiceRxChatRow extends BaseChatRow {

    private String dirStr;



    public VoiceRxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, final FromToMessage detail, final int position) {
        final VoiceViewHolder holder = (VoiceViewHolder) baseHolder;
        final FromToMessage message = detail;
        if (message != null) {
            if (message.withDrawStatus) {
                holder.getWithdrawTextView().setVisibility(View.VISIBLE);
                holder.getContainer().setVisibility(View.GONE);
            } else {
                holder.getWithdrawTextView().setVisibility(View.GONE);
                holder.getContainer().setVisibility(View.VISIBLE);
                if (message.unread2 != null && message.unread2.equals("1")) {
                    ((VoiceViewHolder) baseHolder).voiceUnread.setVisibility(View.VISIBLE);
                } else {
                    ((VoiceViewHolder) baseHolder).voiceUnread.setVisibility(View.GONE);
                }
                if (message.filePath == null || message.filePath.equals("")) {

                    message.message = message.message.replaceAll("https://", "http://");
                    String name="7moor_record_" + UUID.randomUUID() + ".amr";

                    MoorDownLoadUtils.loadFile(message.message, name, new IMoorOnDownloadListener() {
                        @Override
                        public void onDownloadStart() {

                        }

                        @Override
                        public void onDownloadSuccess(final String filePath) {
                            ((ChatActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    message.filePath = filePath;
                                    MessageDao.getInstance().updateMsgToDao(message);
                                    VoiceViewHolder.initVoiceRow(holder, detail, position, (ChatActivity) context, true);
                                }
                            });

                        }

                        @Override
                        public void onDownloading(int progress) {

                        }

                        @Override
                        public void onDownloadFailed() {

                        }
                    });
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        assert IMChatManager.getInstance().getApplicationAgain() != null;
//                        dirStr = IMChatManager.getInstance().getApplicationAgain().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "moor";
//
//                    } else {
//
//                        dirStr = Environment.getExternalStorageDirectory() + File.separator + "cc/downloadfile/";
//                    }
//
//                    File dir = new File(dirStr);
//                    if (!dir.exists()) {
//                        dir.mkdirs();
//                    }
//                    File file = new File(dir, "7moor_record_" + UUID.randomUUID() + ".amr");
//
//                    if (file.exists()) {
//                        file.delete();
//                    }
//
//
//
//                    HttpManager.downloadFile(message.message, file, new FileDownLoadListener() {
//                        @Override
//                        public void onSuccess(File file) {
//                            String filePath = file.getAbsolutePath();
//                            message.filePath = filePath;
//                            MessageDao.getInstance().updateMsgToDao(message);
//                            VoiceViewHolder.initVoiceRow(holder, detail, position, (ChatActivity) context, true);
//
//                        }
//
//                        @Override
//                        public void onFailed() {
//
//                        }
//
//                        @Override
//                        public void onProgress(int progress) {
//
//                        }
//                    });

                } else {
                    VoiceViewHolder.initVoiceRow(holder, detail, position, (ChatActivity) context, true);
                }
            }


        }
    }



    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_voice_rx, null);
            VoiceViewHolder holder = new VoiceViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.VOICE_ROW_RECEIVED.ordinal();
    }
}
