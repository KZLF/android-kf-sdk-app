package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.FileViewHolder;
import com.m7.imkfsdk.utils.FIleResourceUtil;
import com.m7.imkfsdk.utils.MimeTypesTools;
import com.m7.imkfsdk.utils.MoorFileUtils;
import com.m7.imkfsdk.utils.permission.PermissionConstants;
import com.m7.imkfsdk.utils.permission.PermissionXUtil;
import com.m7.imkfsdk.utils.permission.callback.OnRequestCallback;
import com.moor.imkf.db.dao.MessageDao;
import com.moor.imkf.lib.constants.MoorPathConstants;
import com.moor.imkf.lib.http.donwload.IMoorOnDownloadListener;
import com.moor.imkf.lib.http.donwload.MoorDownLoadUtils;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.utils.LogUtils;
import com.moor.imkf.utils.MoorKFfileUtils;

import java.io.File;

/**
 * Created by longwei on 2016/3/10.
 */
public class FileRxChatRow extends BaseChatRow {

    public FileRxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        final FileViewHolder holder = (FileViewHolder) baseHolder;
        final FromToMessage message = detail;
        if (message != null) {

            if (message.withDrawStatus) {
                holder.getWithdrawTextView().setVisibility(View.VISIBLE);
                holder.getContainer().setVisibility(View.GONE);
            } else {
                holder.getWithdrawTextView().setVisibility(View.GONE);
                holder.getContainer().setVisibility(View.VISIBLE);

                holder.getChat_content_tv_name().setText(message.fileName);
                holder.getChat_content_tv_size().setText(message.fileSize);
                holder.getChat_content_tv_status().setText(message.fileDownLoadStatus);
                holder.getChat_content_pb_progress().setProgress(message.fileProgress);
                //设置文件 类型 icon
                holder.getYkf_chat_file_icon().setImageResource(FIleResourceUtil.getFile_Icon(context, message.fileName));


                if ("success".equals(message.fileDownLoadStatus)) {
                    holder.getChat_content_pb_progress().setVisibility(View.GONE);
                    holder.getChat_content_tv_status().setVisibility(View.VISIBLE);
                    holder.getChat_content_tv_status().setText("/" + context.getResources().getString(R.string.ykfsdk_haddownload));
                    holder.getChat_content_iv_download().setVisibility(View.GONE);

                    holder.getBaseView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent intent = new Intent();
                                File file = new File(message.filePath);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.setAction(Intent.ACTION_VIEW);
                                    Uri contentUri = MoorKFfileUtils.fileToUri(file);
                                    intent.setDataAndType(contentUri, MimeTypesTools.getMimeType(context, message.fileName));
                                } else {
                                    intent.setDataAndType(Uri.fromFile(file), MimeTypesTools.getMimeType(context, message.fileName));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                                context.startActivity(Intent.createChooser(intent, null));
                            } catch (Exception e) {
                            }
                        }
                    });
                } else if ("failed".equals(message.fileDownLoadStatus)) {
                    holder.getChat_content_pb_progress().setVisibility(View.GONE);
                    holder.getChat_content_tv_status().setVisibility(View.GONE);
                    holder.getChat_content_iv_download().setVisibility(View.VISIBLE);
                } else if ("downloading".equals(message.fileDownLoadStatus)) {
                    holder.getChat_content_pb_progress().setVisibility(View.VISIBLE);
                    holder.getChat_content_tv_status().setVisibility(View.VISIBLE);
                    holder.getChat_content_tv_status().setText(R.string.ykfsdk_downloading);
                    holder.getChat_content_iv_download().setVisibility(View.GONE);
                }


                holder.getChat_content_iv_download().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String[] permission = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permission = new String[]{PermissionConstants.IMAGEBY_API33, PermissionConstants.VIDEOBY_API33};
                        } else {
                            permission = new String[]{PermissionConstants.STORE};
                        }

                        PermissionXUtil.checkPermission((ChatActivity) context, new OnRequestCallback() {
                            @Override
                            public void requestSuccess() {
                                holder.getChat_content_pb_progress().setVisibility(View.VISIBLE);
                                holder.getChat_content_tv_status().setVisibility(View.VISIBLE);
                                holder.getChat_content_tv_status().setText(R.string.ykfsdk_downloading);
                                holder.getChat_content_iv_download().setVisibility(View.GONE);


                                message.message = message.message.replaceAll("https://", "http://");
                                MoorDownLoadUtils.loadFile(message.message, message.fileName, new IMoorOnDownloadListener() {
                                    @Override
                                    public void onDownloadStart() {

                                    }

                                    @Override
                                    public void onDownloadSuccess(final String filePath) {
                                        boolean saveFile = MoorFileUtils.saveFile(context, new File(filePath), message.fileName);
                                        if (saveFile) {
                                            ((ChatActivity) context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LogUtils.aTag("loading", filePath);
                                                    message.filePath = MoorPathConstants.getStoragePath(MoorPathConstants.PATH_NAME_MOOR_DOWNLOAD_FILE) + message.fileName;
                                                    message.fileDownLoadStatus = "success";
                                                    message.fileProgress = 100;
                                                    MessageDao.getInstance().updateMsgToDao(message);
                                                    ((ChatActivity) context).getChatAdapter().notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onDownloading(final int progress) {
                                        ((ChatActivity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                message.fileProgress = progress;
                                                message.fileDownLoadStatus = "downloading";
                                                MessageDao.getInstance().updateMsgToDao(message);
                                                ((ChatActivity) context).getChatAdapter().notifyDataSetChanged();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onDownloadFailed() {
                                        ((ChatActivity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                message.fileProgress = 0;
                                                message.fileDownLoadStatus = "failed";
                                                MessageDao.getInstance().updateMsgToDao(message);
                                                ((ChatActivity) context).getChatAdapter().notifyDataSetChanged();
                                            }
                                        });

                                    }
                                });
                            }
                        }, permission);

                    }
                });
            }
        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_file_rx, null);
            FileViewHolder holder = new FileViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.FILE_ROW_RECEIVED.ordinal();
    }
}
