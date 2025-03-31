package com.m7.imkfsdk.chat.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.adapter.XbotFormAdapter;
import com.m7.imkfsdk.utils.DensityUtil;
import com.m7.imkfsdk.utils.FileUtils;
import com.m7.imkfsdk.utils.PickUtils;
import com.m7.imkfsdk.utils.ToastUtils;
import com.moor.imkf.db.dao.MessageDao;
import com.moor.imkf.event.XbotFormEvent;
import com.moor.imkf.http.HttpManager;
import com.moor.imkf.model.entity.AddressResult;
import com.moor.imkf.model.entity.UploadFileBean;
import com.moor.imkf.model.entity.XbotForm;
import com.moor.imkf.utils.NullUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//Xbot表单填写
@SuppressLint("ValidFragment")
public class BottomXbotFormDialog extends BottomSheetDialogFragment {
    private String _id;
    private static final int PICK_FILE_BottomXbotFormDialog_REQUEST_CODE = 500;
    protected Context mContext;
    protected View rootView;
    protected BottomSheetDialog dialog;
    protected BottomSheetBehavior mBehavior;
    private String title = "";
    private XbotForm xbotForm;
    private CustomerUploadFileDialog fileDialog;

    private XbotForm.FormInfoBean fileBean;//xbotForm中附件条目 数据
    private int position;//xbotForm中附件条目 位置
    private XbotFormAdapter adapter;
    private boolean isClickClose = false;//isClickClose为true 代表用户关闭，需要删除一些无用文件
    private boolean isDataHasFile = false;//isDataHasFile 为true 代表原来有文件，这时不再删除

    public static BottomXbotFormDialog init(String title, XbotForm xbotForm, String _id) {
        BottomXbotFormDialog dialog = new BottomXbotFormDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("xbotForm", xbotForm);
        args.putString("_id", _id);
        dialog.setArguments(args);
        return dialog;
    }

    public BottomXbotFormDialog() {

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        title = getArguments().getString("title");
        xbotForm = (XbotForm) getArguments().getSerializable("xbotForm");
        _id = getArguments().getString("_id");

        for (int i = 0; i < xbotForm.formInfo.size(); i++) {
            if (XbotForm.Type_DataFile.equals(xbotForm.formInfo.get(i).type)) {
                ArrayList<UploadFileBean> filelist = xbotForm.formInfo.get(i).filelist;
                if (filelist.size() > 0) {
                    isDataHasFile = true;//为true 代表原来有文件，这时不再删除
                }
            }
        }
        AddressResult addressResult =
                new Gson().fromJson(FileUtils.getOriginalFundData(getContext()), AddressResult.class);//省市县数据

        if (rootView == null) {
            rootView = View.inflate(mContext, R.layout.ykfsdk_layout_xbot_formfragment, null);

            TextView id_dialog_question_title = rootView.findViewById(R.id.id_dialog_question_title);
            id_dialog_question_title.setText(title);

            RelativeLayout ivBottomClose = rootView.findViewById(R.id.iv_bottom_close);
            ivBottomClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClickClose = true;
                    dialog.dismiss();

                }
            });

            RecyclerView rv_xbotform = rootView.findViewById(R.id.rv_xbotform);
            rv_xbotform.setLayoutManager(new LinearLayoutManager(mContext));

            if (!TextUtils.isEmpty(xbotForm.formNotes)) {
                //添加最上方备注
                XbotForm.FormInfoBean bean = new XbotForm.FormInfoBean();
                bean.type = XbotForm.Type_HeadNote;
                bean.name = xbotForm.formNotes;
                xbotForm.formInfo.add(0, bean);
            }

            adapter = new XbotFormAdapter(getContext(), xbotForm.formInfo, addressResult, isDataHasFile);
            rv_xbotform.setAdapter(adapter);

            adapter.setSubmitClickListener(new XbotFormAdapter.onSubmitClickListener() {
                @Override
                public void OnSubmitClick(List<XbotForm.FormInfoBean> formInfos) {
                    //提交按钮事件
                    isClickClose = false;
                    if (xbotForm.formInfo.get(0).type.equals(XbotForm.Type_HeadNote)) {
                        xbotForm.formInfo.remove(0);//移除备注
                    }
                    String s = new Gson().toJson(xbotForm);
                    XbotFormEvent formEvent = new XbotFormEvent();
                    formEvent.xbotForm = s;
                    EventBus.getDefault().post(formEvent);
                    MessageDao.getInstance().updateXbotForm(_id);
                    dismiss();
                }

                @Override
                public void OnAddFileClick(int i, XbotForm.FormInfoBean Infobean) {
                    //点击添加文件
                    position = i;
                    fileBean = Infobean;
                    openFile();
                }
            });
        }

        dialog.setContentView(rootView);
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        mBehavior.setSkipCollapsed(true);
        mBehavior.setHideable(true);
        setCancelable(false);
        View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        bottomSheet.setBackgroundColor(mContext.getResources().getColor(R.color.ykfsdk_transparent));
        //重置高度
        if (dialog != null) {
            bottomSheet.getLayoutParams().height = DensityUtil.getScreenHeight(getContext()) * 4 / 5;
        }
        rootView.post(new Runnable() {
            @Override
            public void run() {
                mBehavior.setPeekHeight(rootView.getHeight());
            }
        });
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((ViewGroup) (rootView.getParent())).removeView(rootView);

        if (fileDialog != null && fileDialog.isAdded()) {
            fileDialog.dismiss();
        }

        if (isClickClose) {
            if (!isDataHasFile) {
                if (xbotForm.formInfo != null) {
                    ArrayList<String> keys = new ArrayList<String>();
                    for (int i = 0; i < xbotForm.formInfo.size(); i++) {
                        if (XbotForm.Type_DataFile.equals(xbotForm.formInfo.get(i).type)) {
                            ArrayList<UploadFileBean> filelist = xbotForm.formInfo.get(i).filelist;
                            for (int j = 0; j < filelist.size(); j++) {
//                                String a = filelist.get(j).getUrl().replace(RequestUrl.QINIU_HTTP, "");
                                String a = filelist.get(j).getFileKey();
                                keys.add(a);
                            }
                        }
                    }
                    if (keys.size() != 0) {
                        HttpManager.delXbotFormFile(keys, null);
                    }
                }
            }
        }
    }


    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public void close(boolean isAnimation) {
        if (isAnimation) {
            if (mBehavior != null) {
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        } else {
            if (fileDialog != null && fileDialog.isAdded()) {
                fileDialog.dismiss();
            }
            dismiss();
        }
    }

    /**
     * 打开文件选择
     */
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_BottomXbotFormDialog_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_BottomXbotFormDialog_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String path = PickUtils.getPath(mContext, uri);
            if (!NullUtil.checkNULL(path)) {
                Toast.makeText(mContext, getString(R.string.ykfsdk_ykf_not_support_file), Toast.LENGTH_SHORT).show();
                return;
            }
            File file = new File(path);
            String fileSizeStr = "";
            if (file.exists()) {
                long fileSize = file.length();
                if ((fileSize / 1024 / 1024) > 20.0) {
                    //大于20M不能上传
                    Toast.makeText(mContext, getString(R.string.ykfsdk_sendfiletoobig) + "20MB", Toast.LENGTH_SHORT).show();
                } else {
                    fileSizeStr = FileUtils.formatFileLength(fileSize);
                    String fileName = path.substring(path.lastIndexOf("/") + 1);

                    fileDialog = new CustomerUploadFileDialog();
                    Bundle b = new Bundle();
                    b.putString("fileSize", fileSizeStr);
                    b.putString("filePath", path);
                    b.putString("fileName", fileName);
                    fileDialog.setArguments(b);
                    fileDialog.setOnFileUploadCompletedListener(fileUploadCompletedListener);
                    fileDialog.show(getFragmentManager(), "");
                }
            }
        }
    }

    CustomerUploadFileDialog.OnFileUploadCompletedListener fileUploadCompletedListener =
            new CustomerUploadFileDialog.OnFileUploadCompletedListener() {

                @Override
                public void onFailed(String f) {
                    if (fileDialog != null && fileDialog.isAdded()) {
                        if ("setCancel".equals(f)) {
                            fileDialog.dismiss();
                        } else {
                            ToastUtils.showShort(mContext, getString(R.string.ykfsdk_ykf_upfilefail_form));
                            fileDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onCompleted(UploadFileBean uploadFileBean) {
                    if (fileDialog != null && fileDialog.isAdded()) {
                        fileDialog.dismiss();
                    }

                    if (uploadFileBean != null) {
                        if (fileBean != null) {
                            fileBean.filelist.add(uploadFileBean);
                            adapter.notifyItemChanged(position, fileBean);
                        }
                    }
                }
            };

}

