package com.m7.imkfsdk.chat.dialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.listener.TimerQuitListener;

import java.lang.reflect.Field;

/**
 * @ClassName TimerQuitDialog
 * @Description 访客没回复计时结束
 * @Author jiangbingxuan
 * @Date 2023/2/22 16:51
 * @Version 1.0
 */
@SuppressLint("ValidFragment")
public class TimerQuitDialog extends AppCompatDialogFragment {
    private final TimerQuitListener timerQuitListener;
    private String timer_text;

    @SuppressLint("ValidFragment")
    private TimerQuitDialog(TimerQuitListener timerQuitListener, String timer_text) {
        this.timerQuitListener = timerQuitListener;
        this.timer_text = timer_text;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.ykfsdk_dialog_timer_quit, null);

        TextView tv_close_session = view.findViewById(R.id.tv_close_session);
        TextView tv_timer_text = view.findViewById(R.id.tv_timer_text);


        if (!TextUtils.isEmpty(timer_text)) {
            tv_timer_text.setText(timer_text);
        }


        tv_close_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //离开会话页面
                if (timerQuitListener != null) {
                    dismiss();
                    timerQuitListener.closeSession();
                }
            }
        });


        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            Field mDismissed = this.getClass().getSuperclass().getDeclaredField("mDismissed");
            Field mShownByMe = this.getClass().getSuperclass().getDeclaredField("mShownByMe");
            mDismissed.setAccessible(true);
            mShownByMe.setAccessible(true);
            mDismissed.setBoolean(this, false);
            mShownByMe.setBoolean(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public static final class Builder {
        private TimerQuitListener timerQuitListener;
        private String timer_text;

        public TimerQuitDialog.Builder setSubmitListener(TimerQuitListener quitQueueDailogListener) {
            this.timerQuitListener = quitQueueDailogListener;
            return this;
        }

        /**
         * 配置dialing提示语
         *
         * @param str
         * @return
         */
        public TimerQuitDialog.Builder setCloseText(String str) {
            this.timer_text = str;
            return this;
        }

        public Builder() {

        }

        public TimerQuitDialog build() {
            return new TimerQuitDialog(timerQuitListener, timer_text);
        }
    }
}
