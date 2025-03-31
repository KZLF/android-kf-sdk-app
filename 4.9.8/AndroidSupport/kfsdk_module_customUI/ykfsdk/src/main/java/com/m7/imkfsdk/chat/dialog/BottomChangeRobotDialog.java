package com.m7.imkfsdk.chat.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.adapter.ChageRobotAdapter;
import com.m7.imkfsdk.chat.listener.OnClickRobotListener;
import com.m7.imkfsdk.chat.model.RobotListBean;
import com.m7.imkfsdk.utils.DensityUtil;
import com.moor.imkf.db.dao.GlobalSetDao;
import com.moor.imkf.model.entity.GlobalSet;

import java.util.ArrayList;

/**
 * @author rd
 * @Description 切换机器人弹框
 * @date 2023/3/10
 */
@SuppressLint("ValidFragment")
public class BottomChangeRobotDialog extends BottomSheetDialogFragment {

    protected Context mContext;
    protected View rootView;
    protected BottomSheetDialog dialog;
    protected BottomSheetBehavior mBehavior;
    private ChageRobotAdapter adapter;
    private ArrayList<RobotListBean> mList;
    private OnClickRobotListener listener;
    private TextView tv_kfdialog_tittle;

    public BottomChangeRobotDialog() {
    }


    public BottomChangeRobotDialog(Context context, ArrayList<RobotListBean> list, OnClickRobotListener onClickRobotListener) {
        this.mList = list;
        this.mContext = context;
        this.listener = onClickRobotListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        Bundle arguments = getArguments();
        assert arguments != null;
        if (rootView == null) {
            rootView = View.inflate(mContext, R.layout.ykfsdk_layout_changerobot_bottomsheet, null);
            tv_kfdialog_tittle = rootView.findViewById(R.id.tv_kfdialog_tittle);
            ImageView ivBottomClose = rootView.findViewById(R.id.iv_bottom_close);
            ivBottomClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close(false);
                }
            });
            RecyclerView recyclerView = rootView.findViewById(R.id.rv_robotlist);
            adapter = new ChageRobotAdapter(mContext, mList, listener, this);
            recyclerView.setAdapter(adapter);

            GlobalSet set= GlobalSetDao.getInstance().getGlobalSet();
            if(set!=null){
                if(!TextUtils.isEmpty(set.changeRobotTips)){
                    tv_kfdialog_tittle.setText(set.changeRobotTips);
                }
            }
        }

        dialog.setContentView(rootView);
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        mBehavior.setSkipCollapsed(true);
        mBehavior.setHideable(true);
        View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        bottomSheet.setBackgroundColor(mContext.getResources().getColor(R.color.ykfsdk_transparent));
        //重置高度
        if (dialog != null) {
//            mBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            bottomSheet.getLayoutParams().height = DensityUtil.dp2px(200);
        }
        rootView.post(new Runnable() {
            @Override
            public void run() {
                mBehavior.setPeekHeight(rootView.getHeight());
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
            dismiss();
        }
    }


}

