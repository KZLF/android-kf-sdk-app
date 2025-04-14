package com.m7.imkfsdk.chat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.view.PointBottomView;

/**
 * Created by longwei on 2016/3/9.
 */
public class TextViewHolder extends BaseHolder {

    private TextView tv_content;

    public RelativeLayout chat_rl_robot, chat_rl_robot_result;
    public RelativeLayout chat_ll_robot_useless, chat_ll_robot_useful;
    public LinearLayout lin_content;
    public LinearLayout ll_flow;


    public LinearLayout ll_flow_multi;
    public ImageView chat_iv_robot_useless, chat_iv_robot_useful;
    public TextView chat_tv_robot_useless;
    public TextView chat_tv_robot_useful;
    public TextView chat_tv_robot_result;


    public TextView tv_multi_save;
    public TextView tv_multi_count;
    public PointBottomView pointBottomView;
    public RecyclerView mRecyclerView;
    public RecyclerView no_user_tips_rv;
    public RelativeLayout chat_root_rl;
    public EditText useless_et;
    public TextView submit_tv;
    public LinearLayout useless_ll;
//    public TextView robot_result_new;

    public View line1, line2;
    public TextView remain_tv;

    public TextView tv_push_copy;
    public TextView tv_push_scan;

    public TextViewHolder(int type) {
        super(type);
    }

    public BaseHolder initBaseHolder(View baseView, boolean isReceive) {
        super.initBaseHolder(baseView);

        if (isReceive) {
            type = 1;
            chat_rl_robot = (RelativeLayout) baseView.findViewById(R.id.chat_rl_robot);
            chat_rl_robot_result = (RelativeLayout) baseView.findViewById(R.id.chat_rl_robot_result);
            chat_ll_robot_useless = (RelativeLayout) baseView.findViewById(R.id.chat_ll_robot_useless);
            chat_ll_robot_useful = (RelativeLayout) baseView.findViewById(R.id.chat_ll_robot_useful);
            lin_content = (LinearLayout) baseView.findViewById(R.id.chart_content_lin);
            chat_iv_robot_useless = (ImageView) baseView.findViewById(R.id.chat_iv_robot_useless);
            chat_iv_robot_useful = (ImageView) baseView.findViewById(R.id.chat_iv_robot_useful);
            chat_tv_robot_useless = (TextView) baseView.findViewById(R.id.chat_tv_robot_useless);
            chat_tv_robot_useful = (TextView) baseView.findViewById(R.id.chat_tv_robot_useful);
            chat_tv_robot_result = (TextView) baseView.findViewById(R.id.chat_tv_robot_result);
            mRecyclerView = (RecyclerView) baseView.findViewById(R.id.recycler_view);
            pointBottomView = (PointBottomView) baseView.findViewById(R.id.point);//圆点指示器
            ll_flow = (LinearLayout) baseView.findViewById(R.id.ll_flow);//圆点指示器
            ll_flow_multi = (LinearLayout) baseView.findViewById(R.id.ll_flow_multi);//多选按钮 确定布局
            tv_multi_save = (TextView) baseView.findViewById(R.id.tv_multi_save);//多选按钮确定按钮
            tv_multi_count = (TextView) baseView.findViewById(R.id.tv_multi_count);//多选个数
            no_user_tips_rv = baseView.findViewById(R.id.no_user_tips_rv);
            chat_root_rl = baseView.findViewById(R.id.chat_root_rl);
            useless_et = baseView.findViewById(R.id.useless_et);
            submit_tv = baseView.findViewById(R.id.submit_tv);
            useless_ll = baseView.findViewById(R.id.useless_ll);
            remain_tv = baseView.findViewById(R.id.remain_tv);
//            robot_result_new =  baseView.findViewById(R.id.robot_result_new);

            line1 = baseView.findViewById(R.id.lin1);
            line2 = baseView.findViewById(R.id.lin2);


            tv_push_copy = baseView.findViewById(R.id.tv_push_copy);//推联按钮-复制
            tv_push_scan = baseView.findViewById(R.id.tv_push_scan);//推联按钮-图片

            return this;
        } else {
            //通过baseview找到对应组件
            tv_content = baseView.findViewById(R.id.chat_content_tv);
        }
        progressBar = baseView.findViewById(R.id.uploading_pb);
        type = 2;
        return this;
    }

    public View getLin1() {
        if (line1 == null) {
            line1 = (View) getBaseView().findViewById(R.id.lin1);
        }
        return line1;
    }

    public TextView getDescTextView() {
        if (tv_content == null) {
            tv_content = getBaseView().findViewById(R.id.chat_content_tv);
        }
        return tv_content;
    }

    public LinearLayout getDescLinearLayout() {
        if (lin_content == null) {
            lin_content = baseView.findViewById(R.id.chart_content_lin);
        }
        return lin_content;
    }

    public LinearLayout getFlowLinearLayout() {
        if (ll_flow == null) {
            ll_flow = baseView.findViewById(R.id.ll_flow);
        }
        return ll_flow;
    }

    public RecyclerView getmRecyclerView() {
        if (mRecyclerView == null) {
            mRecyclerView = getBaseView().findViewById(R.id.recycler_view);
        }
        return mRecyclerView;
    }

    public PointBottomView getPointBottomView() {
        if (pointBottomView == null) {
            pointBottomView = getBaseView().findViewById(R.id.point);
        }
        return pointBottomView;

    }

    public RecyclerView getNoUserTipsRv() {
        no_user_tips_rv = (RecyclerView) getBaseView().findViewById(R.id.no_user_tips_rv);
        return no_user_tips_rv;
    }

    public RelativeLayout getChatRootRl() {
        chat_root_rl = (RelativeLayout) getBaseView().findViewById(R.id.chat_root_rl);
        return chat_root_rl;
    }

    public EditText getUselessEt() {
        useless_et = (EditText) getBaseView().findViewById(R.id.useless_et);
        return useless_et;
    }

    public TextView getSubmitTv() {
        submit_tv = (TextView) getBaseView().findViewById(R.id.submit_tv);
        return submit_tv;
    }


//    public TextView getRobotResultNew(){
//        robot_result_new = (TextView) getBaseView().findViewById(R.id.robot_result_new);
//        return robot_result_new;
//    }


    public LinearLayout getLl_flow_multi() {
        return ll_flow_multi;
    }

    public TextView getTv_multi_save() {
        return tv_multi_save;
    }

    public TextView getTv_multi_count() {
        return tv_multi_count;
    }
}
