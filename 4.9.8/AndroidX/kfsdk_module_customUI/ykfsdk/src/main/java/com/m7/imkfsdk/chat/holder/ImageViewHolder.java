package com.m7.imkfsdk.chat.holder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.m7.imkfsdk.R;

/**
 * Created by longwei on 2016/3/10.
 */
public class ImageViewHolder extends BaseHolder {

    private ImageView iv_content;

    private FrameLayout imageRoot;

    public ImageViewHolder(int type) {
        super(type);
    }

    public BaseHolder initBaseHolder(View baseView, boolean isReceive) {
        super.initBaseHolder(baseView);

        //通过baseview找到对应组件
        iv_content = baseView.findViewById(R.id.chat_content_iv);
        if(isReceive) {
            type = 3;
            return this;
        }
        progressBar = baseView.findViewById(R.id.uploading_pb);
        type = 4;

        imageRoot = baseView.findViewById(R.id.chat_from_layout_img);
        return this;
    }

    public ImageView getImageView() {
        if(iv_content == null) {
            iv_content = getBaseView().findViewById(R.id.chat_content_iv);
        }
        return iv_content;
    }

    public FrameLayout getImageRoot() {
        if (imageRoot == null) {
            imageRoot = baseView.findViewById(R.id.chat_layout_img);
        }
        return imageRoot;
    }
}
