package com.m7.imkfsdk.chat.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m7.imkfsdk.R;

/**
 * Created by longwei on 2016/3/9.
 */
public class BaseHolder {

    protected int type;

    /**
     * for upload message .
     */
    protected ProgressBar progressBar;
    protected ImageView chattingAvatar;
    protected TextView chattingTime;
    protected CheckBox checkBox;

    protected ImageView uploadState;
    protected View baseView;
    protected View clickAreaView;
    protected View chattingMaskView;
    protected TextView chatting_withdraw_tv;
    protected RelativeLayout chart_from_container;

    protected TextView tv_un_read;//消息已读未读 textview

    public BaseHolder(int type) {
        this.type = type;
    }

    /**
     * @param baseView
     */
    public BaseHolder(View baseView) {
        super();
        this.baseView = baseView;
    }

    public void initBaseHolder(View baseView) {
        this.baseView = baseView;
        chattingTime = baseView.findViewById(R.id.chatting_time_tv);
        chattingAvatar = baseView.findViewById(R.id.chatting_avatar_iv);
//        clickAreaView = baseView.findViewById(R.id.chatting_click_area);
        uploadState  = baseView.findViewById(R.id.chatting_state_iv);

        chatting_withdraw_tv = baseView.findViewById(R.id.chatting_withdraw_tv);
        chart_from_container = baseView.findViewById(R.id.chart_from_container);
        tv_un_read= baseView.findViewById(R.id.tv_read_un);
    }

    //获取已读未读 view
    public  TextView getTv_un_read(){
        if(tv_un_read==null){
            tv_un_read= baseView.findViewById(R.id.tv_read_un);
        }
        return  tv_un_read;
    }

    public TextView getWithdrawTextView() {
        if(chatting_withdraw_tv == null) {
            chatting_withdraw_tv = getBaseView().findViewById(R.id.chatting_withdraw_tv);
        }
        return chatting_withdraw_tv;
    }

    public RelativeLayout getContainer() {
        if(chart_from_container == null) {
            chart_from_container = getBaseView().findViewById(R.id.chart_from_container);
        }
        return chart_from_container;
    }
    /**
     *
     * @param edit
     */
    public void setEditMode(boolean edit) {
        int visibility = edit? View.VISIBLE: View.GONE;
        if(checkBox != null && checkBox.getVisibility() != visibility) {
            checkBox.setVisibility(visibility);
        }

        if(chattingMaskView != null && chattingMaskView.getVisibility() != visibility) {
            chattingMaskView.setVisibility(visibility);
        }

    }

    /**
     * @return the baseView
     */
    public View getBaseView() {
        return baseView;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the progressBar
     */
    public ProgressBar getUploadProgressBar() {
        return progressBar;
    }

    /**
     * @return the chattingAvatar
     */
    public ImageView getChattingAvatar() {
        return chattingAvatar;
    }

    /**
     * @return the chattingTime
     */
    public TextView getChattingTime() {
        return chattingTime;
    }

    /**
     * @param chattingTime the chattingTime to set
     */
    public void setChattingTime(TextView chattingTime) {
        this.chattingTime = chattingTime;
    }

    /**
     * @return the checkBox
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * @return the uploadState
     */
    public ImageView getUploadState() {
        return uploadState;
    }

    /**
     * @return the clickAreaView
     */
    public View getClickAreaView() {
        return clickAreaView;
    }

    /**
     * @return the chattingMaskView
     */
    public View getChattingMaskView() {
        return chattingMaskView;
    }


}
