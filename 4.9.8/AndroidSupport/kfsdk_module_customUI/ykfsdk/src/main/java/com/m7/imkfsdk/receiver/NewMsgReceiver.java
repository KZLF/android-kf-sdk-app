package com.m7.imkfsdk.receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.constant.NotifyConstants;
import com.m7.imkfsdk.utils.NotificationUtils;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.utils.YKFUtils;

/**
 * 新消息接收器
 */
public class NewMsgReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(IMChatManager.NEW_MSG_ACTION)) {
            Intent msgIntent = new Intent("com.m7.imkfsdk.msgreceiver");
            msgIntent.setPackage(context.getPackageName());
            context.sendBroadcast(msgIntent);
            //应用退到后台，再显示通知栏消息；此处判断可根据具体业务需要调整
            if (!YKFUtils.getInstance().isForeground()) {
                NotificationUtils.getInstance(context)
                        .setClass(ChatActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setContentIntent("peedId")
                        .setTicker(NotifyConstants.TICKER_NEWMSG)
                        .setWhen(System.currentTimeMillis())
                        .setFullScreen(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setOngoing(true)
                        .setOnlyAlertOnce(false)
                        .sendNotification(NotifyConstants.NOTIFYID_CHAT, NotifyConstants.TITLE_NEWMSG, NotifyConstants.CONTENT_NEWMSG, R.drawable.ykfsdk_kf_ic_launcher);
            }

        }
    }
}
