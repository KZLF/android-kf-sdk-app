package com.m7.imkfsdk.chat.listener;

import com.m7.imkfsdk.chat.model.RobotListBean;

/**
 * @ClassName OnClickRobotListener
 * @Description
 * @Author jiangbingxuan
 * @Date 2023/3/10 16:14
 * @Version 1.0
 */
public interface OnClickRobotListener {
    /**
     * 点击切换机器人
     * @param robotListBean
     */
    void onClickRobot(RobotListBean robotListBean);
}
