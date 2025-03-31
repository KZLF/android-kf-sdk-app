package com.m7.imkfsdk.chat.dialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.listener.QuitQueueDailogListener;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.lib.utils.sharedpreferences.MoorSPUtils;

/**
 * @ClassName QuickQueueDialog
 * @Description 排队状态中
 * @Author jiangbingxuan
 * @Date 2022/4/25 17:41
 * @Version 1.0
 */
@SuppressLint("ValidFragment")
public class QuitQueueDialog extends AppCompatDialogFragment {
    private final QuitQueueDailogListener quitQueueDailogListener;

    @SuppressLint("ValidFragment")
    private QuitQueueDialog(QuitQueueDailogListener quitQueueDailogListener) {
        this.quitQueueDailogListener = quitQueueDailogListener;
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

        View view = inflater.inflate(R.layout.ykfsdk_dialog_quickquit, null);

        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        TextView tv_sure = view.findViewById(R.id.tv_sure);
        TextView tv_keep_text = view.findViewById(R.id.tv_keep_text);

        boolean keep = MoorSPUtils.getInstance().getBoolean(YKFConstants.QUEUEKEEP, false);
        if (keep) {
            tv_keep_text.setText(getString(R.string.ykfsdk_queue_quit_content_keep));
        } else {
            tv_keep_text.setText(getString(R.string.ykfsdk_queue_quit_content_nokeep));
        }


        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消弹窗
                if (quitQueueDailogListener != null) {
                    dismiss();
                    quitQueueDailogListener.clickCancle();
                }
            }
        });

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定 离开会话页面
                if (quitQueueDailogListener != null) {
                    dismiss();
                    quitQueueDailogListener.clickQuit();
                }
            }
        });


        return view;
    }

    public static final class Builder {
        private QuitQueueDailogListener quitQueueDailogListener;

        public QuitQueueDialog.Builder setSubmitListener(QuitQueueDailogListener quitQueueDailogListener) {
            this.quitQueueDailogListener = quitQueueDailogListener;
            return this;
        }

        public Builder() {

        }

        public QuitQueueDialog build() {
            return new QuitQueueDialog(quitQueueDailogListener);
        }
    }
}
