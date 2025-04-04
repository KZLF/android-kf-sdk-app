package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.VideoViewHolder;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.utils.NullUtil;
import com.moor.imkf.utils.TimeUtil;


/**
 * Created by longwei on 2016/3/9.
 */
public class VideoRxChatRow extends BaseChatRow {

    private Context context;

    public VideoRxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        this.context = context;
        VideoViewHolder holder = (VideoViewHolder) baseHolder;
        FromToMessage message = detail;
        if(message != null) {

            if(message.withDrawStatus) {
                holder.getWithdrawTextView().setVisibility(View.VISIBLE);
                holder.getContainer().setVisibility(View.GONE);
            }else {
                holder.getWithdrawTextView().setVisibility(View.GONE);
                holder.getContainer().setVisibility(View.VISIBLE);

                if("Hangup".equals(NullUtil.checkNull(message.videoStatus)) || "hangup".equals(NullUtil.checkNull(message.videoStatus))) {
                    //接听
                    String time = "";
                    try {
                        time = TimeUtil.getVideoTime(Long.parseLong(NullUtil.checkNull(message.message))/1000);
                    }catch (Exception e){}
                    holder.getDescTextView().setText(context.getString(R.string.ykfsdk_ykf_call_time)+ time);
                    holder.getDescTextView().setTextColor(context.getResources().getColor(R.color.ykfsdk_all_black));
                    holder.getVideoIcon().setImageResource(R.drawable.ykfsdk_kf_chatrow_video);

                }else if("cancel".equals(NullUtil.checkNull(message.videoStatus))) {
                    //取消
                    holder.getDescTextView().setText(context.getString(R.string.ykfsdk_ykf_video_cancle));
                    holder.getDescTextView().setTextColor(context.getResources().getColor(R.color.ykfsdk_all_black));
                    holder.getVideoIcon().setImageResource(R.drawable.ykfsdk_kf_chatrow_video);

                }else if("refuse".equals(NullUtil.checkNull(message.videoStatus))) {
                    //拒绝
                    holder.getDescTextView().setText(context.getString(R.string.ykfsdk_ykf_video_refuse));
                    holder.getDescTextView().setTextColor(context.getResources().getColor(R.color.ykfsdk_all_black));
                    holder.getVideoIcon().setImageResource(R.drawable.ykfsdk_kf_chatrow_video);
                }

            }
        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_video_rx, null);
            VideoViewHolder holder = new VideoViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.VIDEO_ROW_RECEIVED.ordinal();
    }
}
