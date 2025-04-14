package com.m7.imkfsdk.chat.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m7.imkfsdk.R;

/**
 * Created by long on 2015/7/6.
 */
public class LoadingFragmentDialog extends DialogFragment {
    private boolean canTouchOutside = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ykfsdk_kf_dialog_loading, null);
        TextView title = view
                .findViewById(R.id.id_dialog_loading_msg);
        title.setText(getActivity().getString(R.string.ykfsdk_ykf_wait));
        Dialog dialog = new Dialog(getActivity(), R.style.ykfsdk_dialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(canTouchOutside);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void setCanceledOnTouchOutside(boolean canTouchOutside) {
        this.canTouchOutside = canTouchOutside;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (!this.isAdded()) {
            try {
                super.show(manager, tag);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
        }

    }

}
