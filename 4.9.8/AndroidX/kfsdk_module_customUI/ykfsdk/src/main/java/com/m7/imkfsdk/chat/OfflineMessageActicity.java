package com.m7.imkfsdk.chat;

import android.content.Intent;
import android.os.Bundle;
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
 * 技能组留言页面
 * Created by longwei on 16/8/15.
 */
public class OfflineMessageActicity extends KFBaseActivity {
    EditText id_et_content;
    Button btn_submit;
    TextView back, inviteLeavemsgTipTv;
    private String peerId;
    private String leavemsgTip, inviteLeavemsgTip;
    LoadingFragmentDialog loadingFragmentDialog;

    private LinearLayout offline_ll_custom_field;
    List<LeaveMsgField> lmfList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ykfsdk_kf_dialog_offline);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.ykfsdk_all_white));
        loadingFragmentDialog = new LoadingFragmentDialog();

        back = findViewById(R.id.back);
        inviteLeavemsgTipTv = findViewById(R.id.inviteLeavemsgTip);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        id_et_content = findViewById(R.id.id_et_content);

        btn_submit = findViewById(R.id.id_btn_submit);
        offline_ll_custom_field = findViewById(R.id.offline_ll_custom_field);

        Intent intent = getIntent();
        peerId = intent.getStringExtra("PeerId");
        leavemsgTip = intent.getStringExtra("leavemsgTip");
        inviteLeavemsgTip = intent.getStringExtra("inviteLeavemsgTip");

        if (leavemsgTip != null && !"".equals(leavemsgTip)) {
            id_et_content.setHint(leavemsgTip);
        } else {
            id_et_content.setHint(getString(R.string.ykfsdk_ykf_please_leavemessage));
        }

        if (inviteLeavemsgTip != null && !"".equals(inviteLeavemsgTip)) {
            inviteLeavemsgTipTv.setText(inviteLeavemsgTip);
        } else {
            inviteLeavemsgTipTv.setText(getString(R.string.ykfsdk_ykf_please_leavemessage_replay));
        }

        IMChatManager.getInstance().getLeaveMsgConfig(new OnLeaveMsgConfigListener() {
            @Override
            public void onSuccess(String scheduleTip, List<LeaveMsgField> fieldList) {
                if (fieldList != null && fieldList.size() > 0) {
                    lmfList = fieldList;
                    for (int i = 0; i < fieldList.size(); i++) {
                        LeaveMsgField leaveMsgField = fieldList.get(i);
                        if (leaveMsgField.enable) {
                            RelativeLayout singleView = (RelativeLayout) LayoutInflater.from(OfflineMessageActicity.this).inflate(R.layout.ykfsdk_kf_offline_edittext, offline_ll_custom_field, false);
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
                            Toast.makeText(OfflineMessageActicity.this, fieldName + getString(R.string.ykfsdk_ykf_please_required), Toast.LENGTH_SHORT).show();
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
                            peerId, content, datas, lmfList, new OnSubmitOfflineMessageListener() {
                                @Override
                                public void onSuccess() {

//                                FromToMessage fromToMessage = IMMessage.createTxtMessage(msgStr);

                                    loadingFragmentDialog.dismiss();
                                    Toast.makeText(OfflineMessageActicity.this, getString(R.string.ykfsdk_ykf_up_leavemessageok), Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailed() {
                                    loadingFragmentDialog.dismiss();
                                    Toast.makeText(OfflineMessageActicity.this, getString(R.string.ykfsdk_ykf_up_leavemessage_fail), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                } else {
                    Toast.makeText(OfflineMessageActicity.this, getString(R.string.ykfsdk_ykf_put_edit), Toast.LENGTH_SHORT).show();
                }
            }
        });


        MoorAnalyticsUtils.addRecordAnalytics("进入技能组留言", IMChatManager.getInstance().getAnalyticsCuid(),
                IMChatManager.getInstance().getinitTime(),
                IMChatManager.getInstance().getSdkVersion(), null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("PeerId") != null) {
            peerId = intent.getStringExtra("PeerId");
        }
    }

    @Override
    protected void onDestroy() {
        IMChatManager.getInstance().quitSDk();
        super.onDestroy();
    }

}
