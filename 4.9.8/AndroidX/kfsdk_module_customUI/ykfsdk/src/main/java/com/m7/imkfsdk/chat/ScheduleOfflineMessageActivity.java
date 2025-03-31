package com.m7.imkfsdk.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.dialog.LoadingFragmentDialog;
import com.m7.imkfsdk.utils.statusbar.StatusBarUtils;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.lib.analytics.MoorAnalyticsUtils;
import com.moor.imkf.listener.OnLeaveMsgConfigListener;
import com.moor.imkf.listener.OnSubmitOfflineMessageListener;
import com.moor.imkf.model.entity.LeaveMsgField;

import java.util.HashMap;
import java.util.List;

/**
 * 日程留言页面
 * Created by pangw on 2018/3/7.
 */

public class ScheduleOfflineMessageActivity extends KFBaseActivity {
    EditText id_et_content;
    Button btn_submit;
    TextView back;
    private String ToPeer;
    private String LeavemsgNodeId;
    LoadingFragmentDialog loadingFragmentDialog;
    TextView inviteLeavemsgTipTv;
    private LinearLayout offline_ll_custom_field;
    List<LeaveMsgField> lmfList;

    private String inviteLeavemsgTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ykfsdk_kf_dialog_offline);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.ykfsdk_all_white));
        loadingFragmentDialog = new LoadingFragmentDialog();

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        id_et_content = findViewById(R.id.id_et_content);
        inviteLeavemsgTipTv = findViewById(R.id.inviteLeavemsgTip);
        btn_submit = findViewById(R.id.id_btn_submit);
        offline_ll_custom_field = findViewById(R.id.offline_ll_custom_field);

        Intent intent = getIntent();
        ToPeer = intent.getStringExtra("ToPeer");
        LeavemsgNodeId = intent.getStringExtra("LeavemsgNodeId");
        inviteLeavemsgTip = intent.getStringExtra("inviteLeavemsgTip");


        if (inviteLeavemsgTip != null && !"".equals(inviteLeavemsgTip)) {
            inviteLeavemsgTipTv.setText(inviteLeavemsgTip);
        } else {
            inviteLeavemsgTipTv.setText(getString(R.string.ykfsdk_ykf_please_leavemessage_replay));
        }

        IMChatManager.getInstance().getScheduleLeaveMsgConfig(id_et_content, LeavemsgNodeId, new OnLeaveMsgConfigListener() {
            @Override
            public void onSuccess(String scheduleTip, List<LeaveMsgField> fieldList) {
                //日程留言标题
                if (!TextUtils.isEmpty(scheduleTip)) {
                    inviteLeavemsgTipTv.setText(scheduleTip);
                } else {
                    inviteLeavemsgTipTv.setText("请留言，我们将尽快联系您");
                }
                //留言字段
                if (fieldList != null && fieldList.size() > 0) {
                    lmfList = fieldList;
                    for (int i = 0; i < fieldList.size(); i++) {
                        LeaveMsgField leaveMsgField = fieldList.get(i);
                        if (leaveMsgField.enable) {
                            RelativeLayout singleView = (RelativeLayout) LayoutInflater.from(ScheduleOfflineMessageActivity.this).inflate(R.layout.ykfsdk_kf_offline_edittext, offline_ll_custom_field, false);
                            EditText erp_field_single_et_value = singleView.findViewById(R.id.erp_field_data_et_value);
                            erp_field_single_et_value.setTag(R.id.ykfsdk_offline_id_tag, leaveMsgField._id);
                            erp_field_single_et_value.setTag(R.id.ykfsdk_offline_required_tag, leaveMsgField.required);
                            erp_field_single_et_value.setHint(leaveMsgField.name);
                            offline_ll_custom_field.addView(singleView);
                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailed() {

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = id_et_content.getText().toString().trim();
                int childSize = offline_ll_custom_field.getChildCount();
                HashMap<String, String> datas = new HashMap<>();
                for (int i = 0; i < childSize; i++) {
                    RelativeLayout childView = (RelativeLayout) offline_ll_custom_field.getChildAt(i);
                    EditText et = (EditText) childView.getChildAt(0);
                    String id = (String) et.getTag(R.id.ykfsdk_offline_id_tag);
                    Boolean required = (Boolean) et.getTag(R.id.ykfsdk_offline_required_tag);
                    String value = et.getText().toString().trim();
                    String fieldName = et.getHint().toString();
                    if (required) {
                        if ("".equals(value)) {
                            Toast.makeText(ScheduleOfflineMessageActivity.this, fieldName + getString(R.string.ykfsdk_ykf_please_required), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    datas.put(id, value);

                }

                if (!"".equals(content)) {
                    loadingFragmentDialog.show(getFragmentManager(), "");
                    IMChatManager.getInstance().submitOfflineMessage(
                            getString(R.string.ykfsdk_ykf_leave_msg),
                            getString(R.string.ykfsdk_ykf_leave_content),
                            ToPeer, content, datas, lmfList, new OnSubmitOfflineMessageListener() {
                                @Override
                                public void onSuccess() {
                                    loadingFragmentDialog.dismiss();
                                    Toast.makeText(ScheduleOfflineMessageActivity.this, getString(R.string.ykfsdk_ykf_up_leavemessageok), Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailed() {
                                    loadingFragmentDialog.dismiss();
                                    Toast.makeText(ScheduleOfflineMessageActivity.this, getString(R.string.ykfsdk_ykf_up_leavemessage_fail), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                } else {
                    Toast.makeText(ScheduleOfflineMessageActivity.this, getString(R.string.ykfsdk_ykf_put_edit), Toast.LENGTH_SHORT).show();
                }
            }
        });

        MoorAnalyticsUtils.addRecordAnalytics("进入日程留言", IMChatManager.getInstance().getAnalyticsCuid(),
                IMChatManager.getInstance().getinitTime(),
                IMChatManager.getInstance().getSdkVersion(), null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("ToPeer") != null) {
            ToPeer = intent.getStringExtra("ToPeer");
        }
    }

    @Override
    protected void onDestroy() {
        IMChatManager.getInstance().quitSDk();
        super.onDestroy();
    }
}
