package com.m7.imkfsdk.chat.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.adapter.LogisticsProgressListAdapter;
import com.m7.imkfsdk.chat.model.OrderInfoBean;
import com.m7.imkfsdk.utils.DensityUtil;

import java.util.ArrayList;

/**
 * @Description: 查看完整物流信息弹出框
 * @Author:R-D
 * @CreatDate: 2019-12-25 16:27
 */
@SuppressLint("ValidFragment")
public class BottomSheetLogisticsProgressDialog extends BottomSheetDialogFragment {
    private ArrayList<OrderInfoBean> list;
    private String title, progress_title;
    private String num;
    protected Context mContext;
    protected View rootView;
    protected BottomSheetDialog dialog;
    protected BottomSheetBehavior mBehavior;

    public static BottomSheetLogisticsProgressDialog init(String title, String num, String progress_title, ArrayList<OrderInfoBean> list) {
        BottomSheetLogisticsProgressDialog dialog = new BottomSheetLogisticsProgressDialog();
        Bundle args = new Bundle();
        args.putSerializable("list", list);
        args.putString("num", num);
        args.putString("title", title);
        args.putString("progress_title", progress_title);
        dialog.setArguments(args);
        return dialog;
    }

    public BottomSheetLogisticsProgressDialog() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        Bundle arguments = getArguments();
        assert arguments != null;
        list = (ArrayList<OrderInfoBean>) arguments.getSerializable("list");
        num = arguments.getString("num");
        title = arguments.getString("title");
        progress_title = arguments.getString("progress_title");

        if (rootView == null) {
            rootView = View.inflate(mContext, R.layout.ykfsdk_layout_bottomsheet_progress, null);
            TextView tv_no_data = rootView.findViewById(R.id.tv_no_data);
            TextView tv_express_name = rootView.findViewById(R.id.tv_express_name);
            TextView tv_express_num = rootView.findViewById(R.id.tv_express_num);
            TextView tv_logistics_progress_title = rootView.findViewById(R.id.tv_logistics_progress_title);
            ImageView ivBottomClose = rootView.findViewById(R.id.iv_bottom_close);
            ivBottomClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            RecyclerView rv_switch = rootView.findViewById(R.id.rv_switch);
//            rv_switch.addItemDecoration(new DividerItemDecoration(mContext));
            rv_switch.setLayoutManager(new LinearLayoutManager(mContext));

            LogisticsProgressListAdapter adapter = new LogisticsProgressListAdapter(list, true);
            rv_switch.setAdapter(adapter);

            if (list != null && list.size() == 0) {
                tv_no_data.setVisibility(View.VISIBLE);
                rv_switch.setVisibility(View.GONE);
            } else {
                tv_no_data.setVisibility(View.GONE);
                rv_switch.setVisibility(View.VISIBLE);
            }
            tv_express_name.setMaxWidth(DensityUtil.getScreenWidth(mContext)/ 5 * 2);
            tv_express_num.setMaxWidth(DensityUtil.getScreenWidth(mContext)/ 5 * 3);

            if (!TextUtils.isEmpty(title)) {
                tv_express_name.setVisibility(View.VISIBLE);
                tv_express_name.setText(title);
            } else {
                tv_express_name.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(num)) {
                tv_express_num.setVisibility(View.VISIBLE);
                tv_express_num.setText(num);
            } else {
                tv_express_num.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(progress_title)) {
                tv_logistics_progress_title.setText(progress_title);
            } else {
                tv_logistics_progress_title.setText(getString(R.string.ykfsdk_ykf_logistics_information));
            }

        }

        dialog.setContentView(rootView);
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        mBehavior.setSkipCollapsed(true);
        mBehavior.setHideable(true);

//        mBottomSheetBehavior = (BottomSheetBehavior) mBehavior;
        //重置高度
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
//            mBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            bottomSheet.getLayoutParams().height = DensityUtil.getScreenHeight(getContext()) * 4 / 5;
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

