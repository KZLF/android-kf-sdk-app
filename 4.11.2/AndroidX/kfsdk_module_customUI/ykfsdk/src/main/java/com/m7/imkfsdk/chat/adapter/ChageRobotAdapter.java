package com.m7.imkfsdk.chat.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.dialog.BottomChangeRobotDialog;
import com.m7.imkfsdk.chat.listener.OnClickRobotListener;
import com.m7.imkfsdk.chat.model.RobotListBean;
import com.moor.imkf.IMChatManager;

import java.util.ArrayList;

/**
 * @ClassName ChageRobotAdapter
 * @Description
 * @Author jiangbingxuan
 * @Date 2023/3/10 14:58
 * @Version 1.0
 */
public class ChageRobotAdapter extends RecyclerView.Adapter<ChageRobotAdapter.ViewHolder> {
    private OnClickRobotListener listener;
    private ArrayList<RobotListBean> robotListBeans;
    private Context context;
    private BottomChangeRobotDialog bottomChangeRobotDialog;
    public ChageRobotAdapter(Context context, ArrayList<RobotListBean> robotListBeans,
                             OnClickRobotListener listener, BottomChangeRobotDialog bottomChangeRobotDialog) {
        this.robotListBeans = robotListBeans;
        this.context = context;
        this.listener = listener;
        this.bottomChangeRobotDialog=bottomChangeRobotDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ykfsdk_robot_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final RobotListBean bean = robotListBeans.get(i);
        String name = bean.switchRobotName;
        if (name.length() > 4) {
            name = name.subSequence(0, 4).toString();
            name = name + "...";
        }
        viewHolder.tv_robotname.setText(name);
        IMChatManager.getInstance().getImageLoader().loadImage(false, false, bean.switchRobotImg,
                viewHolder.img_robot, 0, 0, 8,
                context.getResources().getDrawable(R.drawable.ykfsdk_kf_head_default_robot),
                context.getResources().getDrawable(R.drawable.ykfsdk_kf_head_default_robot), null);

        viewHolder.img_robot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickRobot(bean);
                    if(bottomChangeRobotDialog!=null){
                        bottomChangeRobotDialog.dismiss();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return robotListBeans.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_robot;
        public TextView tv_robotname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_robot = itemView.findViewById(R.id.img_robot);
            tv_robotname = itemView.findViewById(R.id.tv_robotname);

        }
    }
}
