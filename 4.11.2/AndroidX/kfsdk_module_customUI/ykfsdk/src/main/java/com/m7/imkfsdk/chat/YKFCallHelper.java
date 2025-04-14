package com.m7.imkfsdk.chat;

import android.app.Notification;
import android.content.Intent;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.listener.OnCancelDialogListener;
import com.m7.imkfsdk.constant.NotifyConstants;
import com.m7.imkfsdk.utils.NotificationUtils;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.listener.OnCallEventListener;
import com.moor.imkf.model.entity.YKFCallInfoBean;
import com.moor.imkf.utils.LogUtils;
import com.moor.imkf.utils.YKFUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <pre>
 *     @author : Trial
 *     @time   : 2022/02/10
 *     @desc   : 通话功能帮助类
 *     @version: 1.0
 * </pre>
 */
public class YKFCallHelper {
    public static String YKFCALLMANAGER_CLASS="com.m7.imkfsdk.video.YKFCallManager";
    private static OnCancelDialogListener mListener;

    /**
     * 通话中的回调
     */
    private static class KfVideoHandle implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("createNotify".equals(method.getName())) {
                //boolean isVideo, boolean isCallIn
                LogUtils.d("==createNotify==" + args[0] + "===" + args[1]);
                Class jitsiMeetActivity = Class.forName("org.jitsi.meet.sdk.JitsiMeetActivity");
                if ((boolean) args[1]) {
                    //收到通话
                    String title;
                    if ((boolean) args[0]) {
                        title = NotifyConstants.TITLE_VIDEO_INVITED;
                    } else {
                        title = NotifyConstants.TITLE_VOICE_INVITED;
                    }


                    NotificationUtils.getInstance(YKFUtils.getInstance().getCurrentActivity())
                            .setClass(jitsiMeetActivity)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setContentIntent("")
                            .setTicker(title)
                            .setWhen(System.currentTimeMillis())
                            .setFullScreen(true)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setOngoing(true)
                            .setOnlyAlertOnce(false)
                            .sendNotification(NotifyConstants.INVITED_VIDEO, title, NotifyConstants.CONTENT_DETAIL, R.drawable.ykfsdk_kf_ic_launcher);
                } else {
                    NotificationUtils notificationUtils = NotificationUtils.getInstance(YKFUtils.getInstance().getCurrentActivity())
                            .setClass(jitsiMeetActivity)
                            .setContentIntent("video")
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setTicker(NotifyConstants.TICKER_VIDEO)
                            .setWhen(System.currentTimeMillis())
                            .setPriority(Notification.PRIORITY_MAX)
                            .setOngoing(true)
                            .setOnlyAlertOnce(false)
                            .setAutoCancel(false);

                    if ((boolean) args[0]) {
                        notificationUtils.sendNotification(YKFConstants.NOTIFYID_VIDEO, NotifyConstants.TITLE_VIDEO, NotifyConstants.CONTENT_VIDEO, R.drawable.ykfsdk_kf_ic_launcher);
                    } else {
                        notificationUtils.sendNotification(YKFConstants.NOTIFYID_VIDEO, NotifyConstants.TITLE_VOICE, NotifyConstants.CONTENT_VOICE, R.drawable.ykfsdk_kf_ic_launcher);
                    }
                }

            } else if ("cancelNotify".equals(method.getName())) {
                LogUtils.d("==cancelNotify==");
                NotificationUtils.getInstance(YKFUtils.getInstance().getCurrentActivity()).getManager().cancel((int) args[0]);
            } else if ("cancelLoadingDialog".equals(method.getName())) {
                LogUtils.d("==cancelLoadingDialog==");
                if (mListener != null) {
                    mListener.cancelDialog();
                }
            }
            return null;
        }
    }

    /**
     * 呼入通话
     *
     * @param callInfoBean
     */
    public static void receivedCall(YKFCallInfoBean callInfoBean) {
        try {
            KfVideoHandle handle = new KfVideoHandle();

            Class clz = Class.forName(YKFCALLMANAGER_CLASS);
            Class<OnCallEventListener> listenerClass = OnCallEventListener.class;
            Method method = clz.getMethod("getInstance");

            Object instance = method.invoke(new Object());

            Object proxyInstance = Proxy.newProxyInstance(ChatActivity.class.getClassLoader(), new Class[]{listenerClass}, handle);
            method = clz.getMethod("setCallBack", listenerClass);
            method.invoke(instance, proxyInstance);

            method = clz.getMethod("ReceivedCall", YKFCallInfoBean.class);

            method.invoke(instance, callInfoBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 外呼通话
     *
     * @param callInfoBean
     */
    public static void openCall(YKFCallInfoBean callInfoBean) {
        try {
            KfVideoHandle handle = new KfVideoHandle();

            Class clz = Class.forName(YKFCALLMANAGER_CLASS);
            Class<OnCallEventListener> listenerClass = OnCallEventListener.class;
            Method method = clz.getMethod("getInstance");

            Object instance = method.invoke(new Object());

            Object proxyInstance = Proxy.newProxyInstance(ChatActivity.class.getClassLoader(), new Class[]{listenerClass}, handle);
            method = clz.getMethod("setCallBack", listenerClass);
            method.invoke(instance, proxyInstance);

            method = clz.getMethod("openCall", YKFCallInfoBean.class);

            method.invoke(instance, callInfoBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setOnCancelDialogListener(OnCancelDialogListener listener) {
        mListener = listener;
    }

    public static void setInvitedIntentNull() {
        try {
            Class clz = Class.forName(YKFCALLMANAGER_CLASS);
            Method method = clz.getMethod("getInstance");
            Object instance = method.invoke(new Object());
            method = clz.getMethod("setInvitedIntentNull");
            method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void openActivity() {
        try {
            Class clz = Class.forName(YKFCALLMANAGER_CLASS);
            Method method = clz.getMethod("getInstance");
            Object instance = method.invoke(new Object());
            method = clz.getMethod("openActivity");
            method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 离开
     *
     * @param leaveNow
     */
    public static void leave(boolean leaveNow) {
        try {
            Class clz = Class.forName(YKFCALLMANAGER_CLASS);
            Method method = clz.getMethod("getInstance");
            Object instance = method.invoke(new Object());
            method = clz.getMethod("leave", boolean.class);
            method.invoke(instance, leaveNow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否存在进行中的通话
     *
     * @return
     */
    public static boolean existVideo() {
        try {
            Class clz = Class.forName(YKFCALLMANAGER_CLASS);
            Method method = clz.getMethod("getInstance");
            Object instance = method.invoke(new Object());
            method = clz.getMethod("existVideo");
            return (boolean) method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
