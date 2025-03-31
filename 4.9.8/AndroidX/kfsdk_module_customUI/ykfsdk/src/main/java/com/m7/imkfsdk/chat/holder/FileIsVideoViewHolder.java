package com.m7.imkfsdk.chat.holder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.m7.imkfsdk.R;

/**
 * Created by longwei on 2016/3/10.
 */
public class FileIsVideoViewHolder extends BaseHolder {

    private ImageView iv_content;
    private ImageView iv_play;
    private View view_floating_layer;
    private FrameLayout imageRoot;
    private ProgressBar chat_content_pb_progress;
    public FileIsVideoViewHolder(int type) {
        super(type);
    }

    public BaseHolder initBaseHolder(View baseView, boolean isReceive) {
        super.initBaseHolder(baseView);

        //通过baseview找到对应组件
        iv_content = baseView.findViewById(R.id.chat_content_iv);
        iv_play = baseView.findViewById(R.id.iv_play);
        chat_content_pb_progress = baseView.findViewById(R.id.chat_content_pb_progress);
        view_floating_layer=baseView.findViewById(R.id.view_floating_layer);
        if(isReceive) {
            type = 29;
            return this;
        }
        progressBar = baseView.findViewById(R.id.uploading_pb);
        type = 30;

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
            imageRoot = baseView.findViewById(R.id.chat_from_layout_img);
        }
        return imageRoot;
    }
    public ProgressBar getChat_content_pb_progress() {
        if (chat_content_pb_progress == null) {
            chat_content_pb_progress = getBaseView().findViewById(R.id.chat_content_pb_progress);
        }
        return chat_content_pb_progress;
    }
    public View getfloatingView() {
        if (view_floating_layer == null) {
            view_floating_layer = baseView.findViewById(R.id.view_floating_layer);
        }
        return view_floating_layer;
    }
    public ImageView getPlayView() {
        if(iv_play == null) {
            iv_play = getBaseView().findViewById(R.id.iv_play);
        }
        return iv_play;
    }
}
