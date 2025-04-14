package com.m7.imkfsdk.chat.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.listener.SubmitPingjiaListener;
import com.m7.imkfsdk.chat.model.Option;
import com.m7.imkfsdk.utils.FIleResourceUtil;
import com.m7.imkfsdk.view.TagView;
import com.m7.imkfsdk.view.rattingbar.StarRatingBar;
import com.m7.imkfsdk.view.rattingbar.StarRatingListener;
import com.moor.imkf.IMChat;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.lib.analytics.MoorAnalyticsUtils;
import com.moor.imkf.lib.utils.MoorAntiShakeUtils;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.lib.utils.sharedpreferences.MoorSPUtils;
import com.moor.imkf.listener.SubmitInvestigateListener;
import com.moor.imkf.model.entity.Investigate;
import com.moor.imkf.utils.MoorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 评价列表界面
 */
@SuppressLint("ValidFragment")
public class InvestigateDialog extends DialogFragment {

    private RadioGroup investigateRadioGroup;//标签单选
    private TagView investigateTag;
    private EditText investigateEt;
    private final SubmitPingjiaListener submitListener;
    private List<Investigate> investigates = new ArrayList<>();
    private Investigate  finalinvestigate=new Investigate();
    private String name, value;
    private List<Option> selectLabels = new ArrayList<>();
    private boolean labelRequired;
    private boolean proposalRequired;
    private final String way;//in:访客主动评价，out：坐席推送或者是系统（点击注销或者是返回键）评价
    private final String connectionId;
    private final String sessionId;
    LoadingFragmentDialog loadingFragmentDialog;
    private Context mContext;


//    private LinearLayout ll_invew_help;//是否解决 布局
//    private LinearLayout ll_ishelp;//已解决
//    private ImageView iv_ishelp;//已解决logo
//    private TextView tv_ishelp;//已解决文案
//    private LinearLayout ll_unhelp;//未解决
//    private ImageView iv_unhelp;//未解决logo
//    private TextView tv_unhelp;//未解决文案


    private LinearLayout ll_invew_second;//二级 布局
    private LinearLayout ll_second_help;//二级 满意
    private ImageView iv_second_hlep;//二级满意logo
    private TextView tv_second_help;// 二级满意 文案
    private LinearLayout ll_second_unhelp;//二级不满意
    private ImageView iv_second_unhelp;//二级 不满意logo
    private TextView tv_second_unhelp;//二级 不满意文案


    private LinearLayout ll_rating;//星星布局
    private StarRatingBar ratting_bar;//星星打分控件
    private TextView tv_star_title;//星星点击文案


    private String csrDetailType;//评价模式
    private String satisfyComment;//备注提示语

    private String chatType;//xbot评价机器人会话 还是评价 人工会话

    @SuppressLint("ValidFragment")
    private InvestigateDialog(String type, String connectionId, String sessionId, SubmitPingjiaListener submitListener, String chatType) {
        super();
        this.submitListener = submitListener;
        this.way = type;
        this.connectionId = connectionId;
        this.sessionId = sessionId;
        this.chatType = chatType;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadingFragmentDialog = new LoadingFragmentDialog();
        getDialog().setTitle(R.string.ykfsdk_ykf_submit_review);

        getDialog().setCanceledOnTouchOutside(false);
        //屏蔽返回键
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        // Get the layout inflater
        View view = inflater.inflate(R.layout.ykfsdk_kf_dialog_investigate, null);
        TextView investigateTitleTextView = view.findViewById(R.id.investigate_title);
        investigateRadioGroup = view.findViewById(R.id.investigate_rg);
        investigateTag = view.findViewById(R.id.investigate_second_tg);
        Button investigateOkBtn = view.findViewById(R.id.investigate_save_btn);
        Button investigateCancelBtn = view.findViewById(R.id.investigate_cancel_btn);
        investigateEt = view.findViewById(R.id.investigate_et);

//        ll_invew_help = view.findViewById(R.id.ll_invew_help);
//        ll_ishelp = view.findViewById(R.id.ll_ishelp);
//        iv_ishelp = view.findViewById(R.id.iv_ishelp);
//        tv_ishelp = view.findViewById(R.id.tv_ishelp);
//        ll_unhelp = view.findViewById(R.id.ll_unhelp);
//        iv_unhelp = view.findViewById(R.id.iv_unhelp);
//        tv_unhelp = view.findViewById(R.id.tv_unhelp);


        ll_invew_second = view.findViewById(R.id.ll_invew_second);
        ll_second_help = view.findViewById(R.id.ll_second_help);
        iv_second_hlep = view.findViewById(R.id.iv_second_hlep);
        tv_second_help = view.findViewById(R.id.tv_second_help);
        ll_second_unhelp = view.findViewById(R.id.ll_second_unhelp);
        iv_second_unhelp = view.findViewById(R.id.iv_second_unhelp);
        tv_second_unhelp = view.findViewById(R.id.tv_second_unhelp);

        ll_rating = view.findViewById(R.id.ll_rating);
        ratting_bar = view.findViewById(R.id.ratting_bar);
        tv_star_title = view.findViewById(R.id.tv_star_title);


//        //是否解决:解决点击
//        ll_ishelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setHelpBtn(1);
//            }
//        });
//        //是否解决:未解决点击
//        ll_unhelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setHelpBtn(2);
//            }
//        });

        String satisfyTitle = "";
        String satifyThank = "";

        if ("xbot".equals(chatType)) {
            csrDetailType = "star";//xbot都是星级模式
            satisfyComment = MoorSPUtils.getInstance().getString(YKFConstants.XBOT_SATISFY_COMMENT, "");
//            investigates = IMChatManager.getInstance().getInvestigate();

            String array = MoorSPUtils.getInstance().getString(YKFConstants.XBOT_INVESTIGATE);
            Gson gson = new Gson();
            investigates = gson.fromJson(array,
                    new TypeToken<List<Investigate>>() {
                    }.getType());

            satisfyTitle = MoorSPUtils.getInstance().getString(YKFConstants.XBOT_SATISFYTITLE, mContext.getString(R.string.ykfsdk_ykf_submit_thanks));
            satifyThank = MoorSPUtils.getInstance().getString(YKFConstants.XBOT_SATISFYTHANK, mContext.getString(R.string.ykfsdk_ykf_submit_thankbay));
        } else {
            csrDetailType = MoorSPUtils.getInstance().getString(YKFConstants.CSR_DETAIL_TYPE, "text");
            satisfyComment = MoorSPUtils.getInstance().getString(YKFConstants.SATISFY_COMMENT, "");
            investigates = IMChatManager.getInstance().getInvestigate();
            satisfyTitle = MoorSPUtils.getInstance().getString(YKFConstants.SATISFYTITLE, mContext.getString(R.string.ykfsdk_ykf_submit_thanks));
            satifyThank = MoorSPUtils.getInstance().getString(YKFConstants.SATISFYTHANK, mContext.getString(R.string.ykfsdk_ykf_submit_thankbay));
        }


        if (!TextUtils.isEmpty(satisfyComment)) {
            investigateEt.setHint(satisfyComment);
        }

        //预先处理数据,如果是星级模式并且小于2 那么转换为文本模式
        if ("star".equals(csrDetailType)) {
            if (investigates.size() < 2) {
                MoorSPUtils.getInstance().put(YKFConstants.CSR_DETAIL_TYPE, "text", true);
                csrDetailType = "text";
            }
        }

        //在判断当前满意度评价的模式
        if ("star".equals(csrDetailType)) {
            //星星模式:
            if (investigates.size() == 2) {
                twoInvestigate();
            } else {
                starMode();
            }
        } else {
            //标签模式
            investigateRadioGroup.setVisibility(View.VISIBLE);
            ll_invew_second.setVisibility(View.GONE);
            ratting_bar.setVisibility(View.GONE);
            tv_star_title.setVisibility(View.GONE);
            initTagView();
        }


        investigateTag.setOnSelectedChangeListener(new TagView.OnSelectedChangeListener() {
            @Override
            public void getTagList(List<Option> options) {
                if (options != null) {
                    selectLabels = options;
                } else {
                    selectLabels.clear();
                }
            }
        });


        if ("".equals(satisfyTitle)) {
            satisfyTitle = mContext.getString(R.string.ykfsdk_ykf_submit_thanks);
        }
        investigateTitleTextView.setText(satisfyTitle);

        if ("".equals(satifyThank)) {
            satifyThank = mContext.getString(R.string.ykfsdk_ykf_submit_thankbay);
        }

        final String finalSatifyThank = satifyThank;


        investigateOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MoorUtils.isNetWorkConnected(mContext)) {
                    Toast.makeText(mContext, mContext.getString(R.string.ykfsdk_notnetwork), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (proposalRequired) {
                    if (investigateEt.getText().toString().trim().length() == 0) {
                        Toast.makeText(mContext, R.string.ykfsdk_ykf_submit_reviewreason, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                final List<String> labels = new ArrayList<>();
                if (selectLabels.size() > 0) {
                    for (Option option : selectLabels) {
                        labels.add(option.name);
                    }
                }
                if (labelRequired) {
                    if (labels.size() == 0) {
                        Toast.makeText(mContext, R.string.ykfsdk_ykf_submit_reviewtag, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (name == null) {
                    Toast.makeText(mContext, R.string.ykfsdk_ykf_submit_reviewchoose, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (MoorAntiShakeUtils.getInstance().check()) {
                    return;
                }
                loadingFragmentDialog.show(getFragmentManager(), "");


                IMChatManager.getInstance().submitInvestigate(chatType,sessionId, connectionId, way, name, value, labels, investigateEt.getText().toString().trim(), new SubmitInvestigateListener() {
                    @Override
                    public void onSuccess() {
                        StringBuilder label = new StringBuilder();
                        if (labels.size() > 0) {
                            for (int i = 0; i < labels.size(); i++) {
                                label.append(labels.get(i));
                                if (i != labels.size() - 1) {
                                    label.append(",");
                                }
                            }
                        }
                        // String content = "用户已评价: " + name + "; 标签: " + label + "; 详细信息: " + investigateEt.getText().toString().trim();
                        String content = getString(R.string.ykfsdk_ykf_user_commented) + ": " + name + "; " + getString(R.string.ykfsdk_ykf_investigate_lable) + ": " + label + "; " + getString(R.string.ykfsdk_ykf_detailed_information) + ": " + investigateEt.getText().toString().trim();
                        submitListener.OnSubmitSuccess(content, finalSatifyThank);
                        loadingFragmentDialog.dismiss();
                        dismiss();
                    }

                    @Override
                    public void onFailed() {
                        loadingFragmentDialog.dismiss();
                        submitListener.OnSubmitFailed();
                        Toast.makeText(mContext, R.string.ykfsdk_ykf_submit_reviewfail, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                },finalinvestigate.xbotSatisfaction);
            }
        });

        investigateCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitListener.OnSubmitCancle();
                dismiss();
            }
        });
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        return view;
    }


    /**
     * 标签模式填放数据
     */
    private void initTagView() {

        for (int i = 0; i < investigates.size(); i++) {
            final Investigate investigate = investigates.get(i);
            RadioButton radioButton = new RadioButton(mContext);
            radioButton.setMaxEms(50);
            radioButton.setId(i);
            radioButton.setEllipsize(TextUtils.TruncateAt.END);
            radioButton.setText(" " + investigate.name + "  ");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(7, 7, 10, 7);
            radioButton.setLayoutParams(params);
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ykfsdk_kf_radiobutton_selector);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            radioButton.setCompoundDrawables(drawable, null, null, null);
            radioButton.setButtonDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                radioButton.setBackground(null);
            }
            if (investigate.isDefaultSelected) {
                radioButton.setChecked(true);
                deatilInvestigate(i);
            }
            investigateRadioGroup.addView(radioButton);
        }
        investigateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                deatilInvestigate(checkedId);
            }
        });
    }


    /**
     * 星级模式下 ->二级 点赞点踩
     */
    private void twoInvestigate() {
        investigateRadioGroup.setVisibility(View.GONE);
        ll_invew_second.setVisibility(View.VISIBLE);
        ratting_bar.setVisibility(View.GONE);
        tv_star_title.setVisibility(View.VISIBLE);
        //二级 满意
        ll_second_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSecondBtn(1);
                deatilInvestigate(0);
                finalinvestigate=investigates.get(0);
                tv_star_title.setText(investigates.get(0).name);
            }
        });
        //二级 不满意
        ll_second_unhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSecondBtn(2);
                deatilInvestigate(1);
                finalinvestigate=investigates.get(1);
                tv_star_title.setText(investigates.get(1).name);
            }
        });

        //默认选中 满意
        if (investigates.get(0).isDefaultSelected) {
            setSecondBtn(1);
            deatilInvestigate(0);
            finalinvestigate=investigates.get(0);
            tv_star_title.setText(investigates.get(0).name);
        }
        //默认选中 不满意
        if (investigates.get(1).isDefaultSelected) {
            setSecondBtn(2);
            deatilInvestigate(1);
            finalinvestigate=investigates.get(1);
            tv_star_title.setText(investigates.get(1).name);
        }
    }


    /**
     * 星级模式
     */
    private void starMode() {

        Collections.reverse(investigates);//星级模式需要颠倒一下list

        investigateRadioGroup.setVisibility(View.GONE);
        ll_invew_second.setVisibility(View.GONE);
        ratting_bar.setVisibility(View.VISIBLE);
        tv_star_title.setVisibility(View.VISIBLE);

        ratting_bar.setStarNum(investigates.size());
        //星星打分控件监听
        ratting_bar.setStarRatingListener(new StarRatingListener() {
            @Override
            public void onSelectStar(float i) {
                MoorLogUtils.i("onRatingChanged", i);

                int sel = Math.round(i);

                ratting_bar.setRating((float) sel);

                if (sel > 0) {
                    sel = sel - 1;
                }
                deatilInvestigate(sel);
                finalinvestigate=investigates.get(sel);
                tv_star_title.setText(investigates.get(sel).name);
            }
        });

        //处理默认选中
        for (int i = 0; i < investigates.size(); i++) {
            if (investigates.get(i).isDefaultSelected) {
                ratting_bar.setRating(i + 1);
                deatilInvestigate(i);
                finalinvestigate=investigates.get(i);
                tv_star_title.setText(investigates.get(i).name);
            }
        }

    }


    private void deatilInvestigate(final int finalI) {
        selectLabels.clear();
        finalinvestigate=investigates.get(finalI);
        List<Option> options = new ArrayList<>();
        for (String reason : investigates.get(finalI).reason) {
            Option option = new Option();
            option.name = reason;
            options.add(option);
            name = investigates.get(finalI).name;
            value = investigates.get(finalI).value;
            labelRequired = investigates.get(finalI).labelRequired;
            proposalRequired = investigates.get(finalI).proposalRequired;
        }
        if (investigates.get(finalI).reason.size() == 0) {
            name = investigates.get(finalI).name;
            value = investigates.get(finalI).value;
            labelRequired = investigates.get(finalI).labelRequired;
            proposalRequired = investigates.get(finalI).proposalRequired;
        }
        investigateTag.initTagView(options, 1);
    }

    @Override
    public void show(android.app.FragmentManager manager, String tag) {
        if (!this.isAdded()) {
            try {
                super.show(manager, tag);

                MoorAnalyticsUtils.addRecordAnalytics("评价框弹出-标准满意度", IMChatManager.getInstance().getAnalyticsCuid(),
                        IMChatManager.getInstance().getinitTime(),
                        IMChatManager.getInstance().getSdkVersion(), null);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void dismiss() {
        try {
            IMChat.getInstance().setNewinvestigate("");//删除本次坐席主动推送字段
            super.dismiss();
        } catch (Exception e) {
        }
    }


    /**
     *
     * 设置是否解决按钮
     *
     * @param type 1已解决 ,2未解决
     */
//    private void setHelpBtn(int type) {
//        if (type == 1) {
//            ll_ishelp.setBackgroundResource(R.drawable.ykfsdk_kf_bg_coner_inves_help);
//            iv_ishelp.setImageResource(R.drawable.ykfsdk_ic_ishelp_true);
//            tv_ishelp.setTextColor(FIleResourceUtil.getCurrentColor(mContext, R.attr.ykfsdk_ykf_theme_color_default));
//
//            ll_unhelp.setBackgroundResource(R.drawable.ykfsdk_kf_bg_coner_inves_unhelp);
//            iv_unhelp.setImageResource(R.drawable.ykfsdk_ic_unhelp);
//            tv_unhelp.setTextColor(mContext.getResources().getColor(R.color.ykfsdk_color_999999));
//
//        } else if (type == 2) {
//            ll_ishelp.setBackgroundResource(R.drawable.ykfsdk_kf_bg_coner_inves_unhelp);
//            iv_ishelp.setImageResource(R.drawable.ykfsdk_ic_ishelp);
//            tv_ishelp.setTextColor(mContext.getResources().getColor(R.color.ykfsdk_color_999999));
//
//            ll_unhelp.setBackgroundResource(R.drawable.ykfsdk_kf_bg_coner_inves_help);
//            iv_unhelp.setImageResource(R.drawable.ykfsdk_ic_unhelp_true);
//            tv_unhelp.setTextColor(FIleResourceUtil.getCurrentColor(mContext, R.attr.ykfsdk_ykf_theme_color_default));
//
//        }
//    }


    /**
     * 当满意度为二级时的点击 ,满意和不满意
     *
     * @param type 1满意,2不满意
     */
    private void setSecondBtn(int type) {
        if (type == 1) {
            ll_second_help.setBackgroundResource(R.drawable.ykfsdk_kf_bg_coner_inves_help);
            iv_second_hlep.setImageResource(R.drawable.ykfsdk_ic_ishelp_true);
            tv_second_help.setTextColor(FIleResourceUtil.getCurrentColor(mContext, R.attr.ykfsdk_ykf_theme_color_default));

            ll_second_unhelp.setBackgroundResource(R.drawable.ykfsdk_kf_bg_coner_inves_unhelp);
            iv_second_unhelp.setImageResource(R.drawable.ykfsdk_ic_unhelp);
            tv_second_unhelp.setTextColor(mContext.getResources().getColor(R.color.ykfsdk_color_999999));
        } else if (type == 2) {
            ll_second_help.setBackgroundResource(R.drawable.ykfsdk_kf_bg_coner_inves_unhelp);
            iv_second_hlep.setImageResource(R.drawable.ykfsdk_ic_ishelp);
            tv_second_help.setTextColor(mContext.getResources().getColor(R.color.ykfsdk_color_999999));

            ll_second_unhelp.setBackgroundResource(R.drawable.ykfsdk_kf_bg_coner_inves_help);
            iv_second_unhelp.setImageResource(R.drawable.ykfsdk_ic_unhelp_true);
            tv_second_unhelp.setTextColor(FIleResourceUtil.getCurrentColor(mContext, R.attr.ykfsdk_ykf_theme_color_default));
        }
    }


    public static final class Builder {
        private String type;
        private String connectionId;
        private String sessionId;
        private SubmitPingjiaListener submitListener;
        private String chatType;//xbot评价机器人会话 还是评价 人工会话

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setConnectionId(String connectionId) {
            this.connectionId = connectionId;
            return this;
        }

        public Builder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder setSubmitListener(SubmitPingjiaListener submitListener) {
            this.submitListener = submitListener;
            return this;
        }

        public Builder setChatType(String chatType) {
            this.chatType = chatType;
            return this;
        }

        public Builder() {

        }

        public InvestigateDialog build() {
            return new InvestigateDialog(type, connectionId, sessionId, submitListener, chatType);
        }
    }


}
