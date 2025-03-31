package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.LogisticsInfoTxHolder;
import com.m7.imkfsdk.chat.holder.ViewHolderTag;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.NewCardInfo;
import com.moor.imkf.utils.MoorUtils;
import com.moor.imkf.utils.NullUtil;

import java.lang.reflect.Type;

/**
 * @author R-D
 * @Description 发送的订单信息类型
 * @date 2019/12/01
 */
public class LogisticsInfoTxChatRow extends BaseChatRow {
    public LogisticsInfoTxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, final FromToMessage detail, int position) {
        LogisticsInfoTxHolder holder = (LogisticsInfoTxHolder) baseHolder;
        if (detail != null && detail.newCardInfo != null) {
            Type token = new TypeToken<NewCardInfo>() {
            }.getType();
            NewCardInfo orderInfoBean = new Gson().fromJson(detail.newCardInfo, token);
            //移除不需要访客端展示的数据
            orderInfoBean = MoorUtils.removeBtnTag(orderInfoBean);
            if (NullUtil.checkNULL(orderInfoBean.getTitle())) {
                holder.tv_logistics_tx_title.setText(orderInfoBean.getTitle());
            }
            if (NullUtil.checkNULL(orderInfoBean.getSub_title())) {
                holder.tv_logistics_tx_.setText(orderInfoBean.getSub_title());
            }
            if (NullUtil.checkNULL(orderInfoBean.getAttr_one().getColor())) {
                String color = orderInfoBean.getAttr_one().getColor();
                if (color.contains("#")) {
                    try {
                        holder.tv_logistics_tx_num.setTextColor(Color.parseColor(color));
                    } catch (Exception e) {

                    }
                }
            }
            if (NullUtil.checkNULL(orderInfoBean.getAttr_two().getColor())) {
                String color = orderInfoBean.getAttr_two().getColor();
                if (color.contains("#")) {
                    try {
                        holder.tv_logistics_tx_state.setTextColor(Color.parseColor(color));
                    } catch (Exception e) {

                    }
                }
            }
            if (NullUtil.checkNULL(orderInfoBean.getAttr_one().getContent())) {
                holder.tv_logistics_tx_num.setText(orderInfoBean.getAttr_one().getContent());
            }
            if (NullUtil.checkNULL(orderInfoBean.getAttr_two().getContent())) {
                holder.tv_logistics_tx_state.setText(orderInfoBean.getAttr_two().getContent());
            }
            if (NullUtil.checkNULL(orderInfoBean.getPrice())) {
                holder.tv_logistics_tx_price.setText(orderInfoBean.getPrice());
            }


            if (NullUtil.checkNULL(orderInfoBean.getOther_title_three())) {
                holder.tv_logistics_tx_second.setText(orderInfoBean.getOther_title_three());
            }
            if (NullUtil.checkNULL(orderInfoBean.getOther_title_two())) {
                holder.tv_logistics_tx_second.setText(orderInfoBean.getOther_title_two());
            }
            if (NullUtil.checkNULL(orderInfoBean.getOther_title_one())) {
                holder.tv_logistics_tx_second.setText(orderInfoBean.getOther_title_one());
            }

            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadImage(false, false,orderInfoBean.getImg(),
                        holder.iv_logistics_tx_img, 0, 0, 2, null, null, null);
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }

            View.OnClickListener listener = ((ChatActivity) context).getChatAdapter().getOnClickListener();
            getMsgStateResId(position, holder, detail, listener);

            ViewHolderTag holderTag = ViewHolderTag.createTag(orderInfoBean.getTarget_url(), ViewHolderTag.TagType.TAG_CLICK_LOGISTICS_RX_CARD);
            holder.kf_chat_rich_lin.setTag(holderTag);
            holder.kf_chat_rich_lin.setOnClickListener(listener);
        }

    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_logistics_tx, null);
            LogisticsInfoTxHolder holder = new LogisticsInfoTxHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.LOGISTICS_INFORMATION_ROW_TRANSMIT.ordinal();
    }
}

