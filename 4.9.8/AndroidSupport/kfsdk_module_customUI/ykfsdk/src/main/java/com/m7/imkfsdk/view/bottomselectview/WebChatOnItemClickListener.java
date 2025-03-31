package com.m7.imkfsdk.view.bottomselectview;

import com.moor.imkf.model.entity.WebChatInterface;

/**
 * Author: Blincheng.
 * Date: 2017/5/9.
 * Description:
 */

public interface WebChatOnItemClickListener {
    /**
     * @param city 返回地址列表对应点击的对象
     * @param tabPosition 对应tab的位置
     * */
    void itemClick(WebChatSelector webChatSelector, WebChatInterface city, int tabPosition);
}
