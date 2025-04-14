package com.ykfsdk.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.m7.imkfsdk.utils.DensityUtil;
import com.moor.imkf.listener.IMoorImageLoader;
import com.moor.imkf.listener.IMoorImageLoaderListener;
import com.moor.imkf.utils.YKFUtils;

import java.io.File;

/**
 * @author : 7M
 * @time : 1/25/23
 * @desc : 指定sdk中图片加载方式，实现IMoorImageLoader接口,更换自己项目的图片加载框架
 * 此处demo使用Glide 4.9.0方式为例
 * @version: 1.0
 */
public class GlideImageLoader implements IMoorImageLoader {
    private final Context context;

    public GlideImageLoader() {
        this.context = YKFUtils.getInstance().getApplication();
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadImage(boolean isGif, boolean isDownload, final String uri, final ImageView imageView, int width, int height,
                          float rounder, Drawable holderDraw, Drawable errorDraw, final IMoorImageLoaderListener listener) {
        if (context == null) {
            return;
        }

        if (isDownload) {
            //需要下载 返回file
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FutureTarget<File> target = Glide.with(context)
                            .asFile()
                            .load(uri)
                            .error(R.drawable.ykfsdk_image_download_fail_icon)
                            .submit();

                    try {
                        File file = target.get();
                        if (listener != null) {
                            listener.onResourceReady(file);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return;
        }


        final RequestOptions options = new RequestOptions()
                .dontAnimate()
                .fitCenter();
        if (holderDraw != null) {
            options.placeholder(holderDraw);
        } else {
            options.placeholder(R.drawable.ykfsdk_kf_pic_thumb_bg);
        }
        if (errorDraw != null) {
            options.error(errorDraw);
        } else {
            options.error(R.drawable.ykfsdk_image_download_fail_icon);
        }
        if (rounder > 0) {
            //需要圆角
            options.apply(RequestOptions.bitmapTransform(new RoundedCorners(DensityUtil.dp2px(rounder))));
        }


        if (isGif) {
            //是gif图
            options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(context)
                    .asGif()
                    .load(uri)
                    .apply(options)
                    .into(imageView);
        } else {
            options.dontAnimate();
            if (width != 0 && height != 0) {
                options.override(width, height);
            }
            Glide.with(context)
                    .asBitmap()
                    .load(uri)
                    .apply(options)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (imageView != null) {
                                imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(resource);
                                    }
                                });
                            }

                            if (listener != null) {
                                listener.onLoadComplete(resource);
                            }

                        }

                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            if (listener != null) {
                                listener.onLoadStarted(placeholder);
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            if (listener != null) {
                                listener.onLoadFailed(errorDrawable);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }

    @Override
    public void loadVideoImage(String uri, ImageView imageView, int width, int height, int frame, float rounder, IMoorImageLoaderListener listener) {
        final RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ykfsdk_kf_pic_thumb_bg)
                .error(R.drawable.ykfsdk_image_download_fail_icon)
                .fitCenter();

        Glide.with(context)
                .load(uri)
                .frame(frame)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void clear(ImageView imageView) {
        Glide.with(context).clear(imageView);
    }
}
