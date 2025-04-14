package com.m7.imkfsdk.chat.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.moor.imkf.IMChat;
import com.moor.imkf.model.entity.UploadFileBean;


public class CustomerUploadFileDialog extends AppCompatDialogFragment {

    OnFileUploadCompletedListener listener;
    private TextView tv_precent;
    private ProgressBar progressBar;
    private ImageView iv_kf_closeup;
    private TextView tv_fileName;
    private TextView tv_fileSize;
    private  String fileSizeStr,fileName,filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View uploadView = inflater.inflate(R.layout.ykfsdk_kf_field_file_uploading, null);
        tv_fileName = uploadView.findViewById(R.id.erp_field_file_upload_tv_filename);
        tv_fileSize = uploadView.findViewById(R.id.erp_field_file_upload_tv_filesize);
        tv_precent = uploadView.findViewById(R.id.erp_field_file_upload_tv_precent);
        progressBar = uploadView.findViewById(R.id.erp_field_file_upload_pb);
        iv_kf_closeup=uploadView.findViewById(R.id.iv_kf_closeup);

        Bundle b = getArguments();
        fileName=b.getString("fileName");
        fileSizeStr = b.getString("fileSize");
        filePath=b.getString("filePath");

        tv_fileName.setText(fileName);
        tv_fileSize.setText(fileSizeStr);

        Dialog dialog = new Dialog(getActivity(), R.style.ykfsdk_dialog);
        dialog.setContentView(uploadView);
        dialog.setCanceledOnTouchOutside(false);

        sendFile(filePath,fileName);

        iv_kf_closeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMChat.getInstance().setCancel(true);
                if (listener!=null){
                    listener.onFailed("setCancel");
                }
            }
        });


        return dialog;
    }


    private void sendFile(final String filePath, final String fileName) {
        IMChat.getInstance().upLoadXbotFromFile(filePath, fileName, new IMChat.onXbotFormUpFileListener() {
            @Override
            public void onisOK(String netfilepath,String filekey) {
                if (listener != null) {
                    UploadFileBean uploadFileBean = new UploadFileBean();
                    uploadFileBean.setName(fileName);
                    uploadFileBean.setUrl(netfilepath);
                    uploadFileBean.setFileKey(filekey);
                    uploadFileBean.setLocalUrl(filePath);
                    listener.onCompleted(uploadFileBean);
                }
            }

            @Override
            public void onUpLoading(int progress) {
                progressBar.setProgress(progress);
                tv_precent.setText(progress+ "%");
            }

            @Override
            public void onFailed(String filepath) {
                if (listener != null) {
                    listener.onFailed(filepath);
                }
            }
        });
    }


    public interface OnFileUploadCompletedListener {
        void onCompleted(UploadFileBean uploadFileBean);

        void onFailed(String error);
    }

    public void setOnFileUploadCompletedListener(OnFileUploadCompletedListener listener) {
        this.listener = listener;
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
