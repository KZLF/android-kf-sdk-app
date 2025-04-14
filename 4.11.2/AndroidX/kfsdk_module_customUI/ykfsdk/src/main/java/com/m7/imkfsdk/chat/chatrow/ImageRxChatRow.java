package com.m7.imkfsdk.chat.chatrow;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.ImageViewHolder;
import com.m7.imkfsdk.chat.model.MoorImageInfoBean;
import com.m7.imkfsdk.utils.DensityUtil;
import com.m7.imkfsdk.view.imageviewer.MoorImagePreview;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.model.entity.FromToMessage;

/**
 * Created by longwei on 2016/3/10.
 */
public class ImageRxChatRow extends BaseChatRow {

    public ImageRxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        ImageViewHolder holder = (ImageViewHolder) baseHolder;
        final FromToMessage message = detail;
        if (message != null) {
            if (message.withDrawStatus) {
                holder.getWithdrawTextView().setVisibility(View.VISIBLE);
                holder.getContainer().setVisibility(View.GONE);
            } else {
                holder.getWithdrawTextView().setVisibility(View.GONE);
                holder.getContainer().setVisibility(View.VISIBLE);


                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.getImageView().getLayoutParams();

                int screenWidth = DensityUtil.getScreenWidth(context);
                int screenHeight = DensityUtil.getScreenHeight(context);
                holder.getImageView().setMaxWidth(screenWidth / 2);
                holder.getImageView().setMaxHeight(screenHeight / 3);
                lp.width = screenWidth / 2;
                lp.height = WRAP_CONTENT;
                holder.getImageView().setLayoutParams(lp);


                if (IMChatManager.getInstance().getImageLoader() != null) {
                    IMChatManager.getInstance().getImageLoader().loadImage(false, false, message.message,
                            holder.getImageView(), 0, 0, 8, null, null, null);
                } else {
                    MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
                }


                holder.getImageRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MoorImageInfoBean bean = new MoorImageInfoBean();
                        bean.setFrom("in").setPath(message.message);
                        MoorImagePreview.getInstance()
                                .setContext(context)
                                .setIndex(0)
                                .setImage(bean)
                                .start();
                    }
                });

                holder.getImageView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(context, ImageViewLookActivity.class);
//                        intent.putExtra("imagePath", message.message);
//                        intent.putExtra("fromwho", 0);//0代表对方发送的
//                        context.startActivity(intent);

                        MoorImageInfoBean bean = new MoorImageInfoBean();
                        bean.setFrom("in").setPath(message.message);
                        MoorImagePreview.getInstance()
                                .setContext(context)
                                .setIndex(0)
                                .setImage(bean)
                                .start();
                    }
                });
            }

        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_image_rx, null);
            ImageViewHolder holder = new ImageViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.IMAGE_ROW_RECEIVED.ordinal();
    }
}
