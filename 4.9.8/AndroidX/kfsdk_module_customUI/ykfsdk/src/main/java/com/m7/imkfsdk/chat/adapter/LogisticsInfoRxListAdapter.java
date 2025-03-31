package com.m7.imkfsdk.chat.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.holder.OrderInfoHolder;
import com.m7.imkfsdk.chat.holder.OrderShopHolder;
import com.m7.imkfsdk.chat.holder.ViewHolderTag;
import com.m7.imkfsdk.chat.model.OrderInfoBean;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.utils.NullUtil;

import java.util.List;

/**
 * @Description: 查看更多订单信息的adapter
 * @Author: R-D
 * @Date: 2019-12-25
 */
public class LogisticsInfoRxListAdapter extends RecyclerView.Adapter {
    List<OrderInfoBean> mData;
    /*
         父类型布局
          */
    private static final int SHOP_ITEM_LAYOUT = R.layout.ykfsdk_item_shop_group;
    /*
    子类型布局
     */
    private static final int ORDER_ITEM_LAYOUT = R.layout.ykfsdk_item_shop_child;
    private Context context;
    private final String current;
    private final String _id;
    //true：查看更多订单信息；false：会话中的订单信息
    private final boolean isFromMoreLogistics;

    private int showCount = 0;

    public LogisticsInfoRxListAdapter(List<OrderInfoBean> mData, String current, boolean isFromMoreLogistics, String _id, int showCount) {
        this.mData = mData;
        this.current = current;
        this._id = _id;
        this.isFromMoreLogistics = isFromMoreLogistics;
        this.showCount = showCount;

        if (this.showCount < mData.size()) {
            int poision = 0;
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).getItem_type().equals("0")) {
                    if (poision == this.showCount) {
                        poision = i;
                        break;
                    }
                    poision++;
                }
            }
            this.showCount = poision;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(SHOP_ITEM_LAYOUT, parent, false);
            return new OrderShopHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(ORDER_ITEM_LAYOUT, parent, false);
            return new OrderInfoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        OrderInfoBean orderInfoBean = mData.get(position);
        View.OnClickListener listener = ((ChatActivity) context).getChatAdapter().getOnClickListener();
        if (viewType == 1) {
            OrderShopHolder shopHolder = (OrderShopHolder) holder;
            shopHolder.tv_shop_group_name.setText(orderInfoBean.getTitle());
            shopHolder.tv_shop_group_state.setText(orderInfoBean.getStatus());

            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadImage(false, false, orderInfoBean.getImg(),
                        shopHolder.iv_shop_group_img, 0, 0, 2, null, null, null);
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }

            //店铺点击事件
            if ("self".equals(orderInfoBean.getTarget())) {

            } else if ("url".equals(orderInfoBean.getTarget())) {
                //跳转链接
                ViewHolderTag holderTag = ViewHolderTag.createTag(orderInfoBean.getTarget_url(), ViewHolderTag.TagType.TAG_CLICK_LOGISTICS_RX_SHOP);
                shopHolder.rl_shop_main.setTag(holderTag);
                shopHolder.rl_shop_main.setOnClickListener(listener);
            }

        } else {
            OrderInfoHolder childHolder = (OrderInfoHolder) holder;
            if (NullUtil.checkNULL(orderInfoBean.getTitle())) {
                childHolder.tv_child_title.setText(orderInfoBean.getTitle());
            }
            if (NullUtil.checkNULL(orderInfoBean.getSub_title())) {
                childHolder.tv_child_.setText(orderInfoBean.getSub_title());
            }
            if (NullUtil.checkNULL(orderInfoBean.getAttr_one().getColor())) {
                String color = orderInfoBean.getAttr_one().getColor();
                if (color.contains("#")) {
                    try {
                        childHolder.tv_child_num.setTextColor(Color.parseColor(color));
                    } catch (Exception e) {

                    }
                }
            }
            if (NullUtil.checkNULL(orderInfoBean.getAttr_two().getColor())) {
                String color = orderInfoBean.getAttr_two().getColor();
                if (color.contains("#")) {
                    try {
                        childHolder.tv_child_state.setTextColor(Color.parseColor(color));
                    } catch (Exception e) {

                    }
                }
            }
            if (NullUtil.checkNULL(orderInfoBean.getAttr_one().getContent())) {
                childHolder.tv_child_num.setText(orderInfoBean.getAttr_one().getContent());
            }
            if (NullUtil.checkNULL(orderInfoBean.getAttr_two().getContent())) {
                childHolder.tv_child_state.setText(orderInfoBean.getAttr_two().getContent());
            }
            if (NullUtil.checkNULL(orderInfoBean.getPrice())) {
                childHolder.tv_child_price.setText(orderInfoBean.getPrice());
            }


            if (NullUtil.checkNULL(orderInfoBean.getOther_title_three())) {
                childHolder.tv_child_second.setText(orderInfoBean.getOther_title_three());
            }
            if (NullUtil.checkNULL(orderInfoBean.getOther_title_two())) {
                childHolder.tv_child_second.setText(orderInfoBean.getOther_title_two());
            }
            if (NullUtil.checkNULL(orderInfoBean.getOther_title_one())) {
                childHolder.tv_child_second.setText(orderInfoBean.getOther_title_one());
            }



            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadImage(false, false, orderInfoBean.getImg(),
                        childHolder.iv_child_img, 0, 0, 2, null, null, null);
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }

            //item点击事件
            if (orderInfoBean.getParams() != null) {
                if (NullUtil.checkNULL(orderInfoBean.getParams().getOrderNo())) {
                    if ("url".equals(orderInfoBean.getTarget())) {
                        //跳转链接
                        ViewHolderTag holderTag = ViewHolderTag.createTag(orderInfoBean.getTarget_url(), ViewHolderTag.TagType.TAG_CLICK_LOGISTICS_RX_SHOP);
                        childHolder.rl_child_main.setTag(holderTag);
                        childHolder.rl_child_main.setOnClickListener(listener);
                    } else if ("next".equals(orderInfoBean.getTarget())) {
                        //发送卡片
                        ViewHolderTag holderTag = ViewHolderTag.createTag(current, _id, orderInfoBean
                                , ViewHolderTag.TagType.TAG_CLICK_LOGISTICS_RX_ITEM);
                        childHolder.rl_child_main.setTag(holderTag);
                        childHolder.rl_child_main.setOnClickListener(listener);
                    } else if ("self".equals(orderInfoBean.getTarget())) {

                    }

                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getItem_type().equals("1")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            if (isFromMoreLogistics) {
                return mData.size();
            } else {
                if (mData.size() > showCount) {
                    if (mData.get(showCount - 1).getItem_type().equals("1")) {
                        return showCount - 1;
                    }
                    return showCount;
                } else if (mData.size() == showCount && mData.get(mData.size() - 1).getItem_type().equals("1")) {
                    return showCount - 1;
                } else {
                    return mData.size();
                }
            }
        } else {
            return 0;
        }
    }
}
