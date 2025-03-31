package com.m7.imkfsdk.recordbutton;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.m7.imkfsdk.R;

/**
 * Created by LongWei
 */
public class DialogManager {

    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVoice;
    private TextView textView;
    private TextView mTimeTextView;

    private final Context context;

    private boolean isExceed = false;

    public DialogManager(Context context) {
        this.context = context;
    }

    public void showDialog() {
        mDialog = new Dialog(context, R.style.ykfsdk_Theme_AudioDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.ykfsdk_kf_dialog_recorder, null);
        mDialog.setContentView(view);
        mIcon = view.findViewById(R.id.dialog_recorder_iv_rd);
        mVoice = view.findViewById(R.id.dialog_recorder_iv_voice);
        textView = view.findViewById(R.id.dialog_recorder_tv);
        mTimeTextView = view.findViewById(R.id.dialog_recorder_tv_time);
        mDialog.show();
        isExceed = false;
    }

    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            if (isExceed) {
                mTimeTextView.setVisibility(View.VISIBLE);
                mIcon.setVisibility(View.INVISIBLE);
                mVoice.setVisibility(View.INVISIBLE);
            } else {
                mIcon.setVisibility(View.VISIBLE);
                mVoice.setVisibility(View.VISIBLE);
            }
            textView.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.ykfsdk_recorder);
            textView.setText(R.string.ykfsdk_cancelsend);
            mTimeTextView.setVisibility(View.VISIBLE);
        }
    }

    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            if (isExceed) {
                mTimeTextView.setVisibility(View.GONE);

            }
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.ykfsdk_kf_cancel);
            textView.setText(R.string.ykfsdk_recorder_want_cancel);

        }
    }

    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.ykfsdk_kf_voice_to_short);
            textView.setText(R.string.ykfsdk_ykf_recordingtime_istooshort);
        }
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
            try {
                int resId = context.getResources().getIdentifier("ykfsdk_kf_v" + level, "drawable", context.getPackageName());
                mVoice.setImageResource(resId);
            } catch (Exception e) {
                int resId = context.getResources().getIdentifier("ykfsdk_kf_v1", "drawable", context.getPackageName());
                mVoice.setImageResource(resId);
            }

        }
    }

    /**
     * 剩余十秒时调用的方法
     *
     * @param leftTime 剩余的时间
     */
    public void secondLeft(int leftTime) {
        isExceed = true;
        if (mDialog != null && mDialog.isShowing()) {
            if (leftTime == 10) {
                mIcon.setVisibility(View.INVISIBLE);
                mVoice.setVisibility(View.INVISIBLE);
                mTimeTextView.setVisibility(View.VISIBLE);
            }
            mTimeTextView.setText(leftTime + "");
        }
    }

    /**
     * 超时提醒
     */
    public void exceedTime() {
        if (mDialog != null && mDialog.isShowing()) {
            mTimeTextView.setText("");
            mTimeTextView.setVisibility(View.VISIBLE);
            mTimeTextView.setBackgroundResource(R.drawable.ykfsdk_kf_voice_to_short);
            textView.setText(R.string.ykfsdk_ykf_talking_too_long);
            mIcon.setVisibility(View.GONE);
            mVoice.setVisibility(View.GONE);
        }
    }

}
