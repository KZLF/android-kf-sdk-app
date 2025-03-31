package com.m7.imkfsdk.chat.chatrow;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
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
public class ImageTxChatRow extends BaseChatRow {


    public ImageTxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        final ImageViewHolder holder = (ImageViewHolder) baseHolder;
        final FromToMessage message = detail;
        if (message != null) {
            FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) holder.getImageView().getLayoutParams();
            int screenWidth= DensityUtil.getScreenWidth(context);
            int screenHeight= DensityUtil.getScreenHeight(context);
            holder.getImageView().setMaxWidth(screenWidth/2);
            holder.getImageView().setMaxHeight(screenHeight/3);
            layoutParams.width = screenWidth/2;
            layoutParams.height = WRAP_CONTENT;
            holder.getImageView().setLayoutParams(layoutParams);


            if (IMChatManager.getInstance().getImageLoader() != null) {
                IMChatManager.getInstance().getImageLoader().loadImage(false, false, message.filePath,
                        holder.getImageView(), 0, 0, 8, null, null, null);
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }

            holder.getImageRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MoorImageInfoBean bean = new MoorImageInfoBean();
                    bean.setFrom("out").setPath(message.filePath);
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
//                    Intent intent = new Intent(context, ImageViewLookActivity.class);
//                    intent.putExtra("fromwho", 1);//0代表对方发送的
//                    intent.putExtra("imagePath", message.filePath);
//                    context.startActivity(intent);

                    MoorImageInfoBean bean = new MoorImageInfoBean();
                    bean.setFrom("out").setPath(message.filePath);
                    MoorImagePreview.getInstance()
                            .setContext(context)
                            .setIndex(0)
                            .setImage(bean)
                            .start();
                }
            });
            View.OnClickListener listener = ((ChatActivity) context).getChatAdapter().getOnClickListener();
            getMsgStateResId(position, holder, message, listener);
        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_image_tx, null);
            ImageViewHolder holder = new ImageViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }

        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.IMAGE_ROW_TRANSMIT.ordinal();
    }
}
