package com.m7.imkfsdk.view.imageviewer.listener;

import android.app.Activity;
import android.view.View;

/**
 * <pre>
 *     @author : Trial
 *     @time   : 2/23/21
 *     @desc   :
 *     @version: 1.0
 * </pre>
 */
public abstract class OnDownloadClickListener {

    /**
     * 点击事件
     * 是否拦截下载行为
     */
    public abstract void onClick(Activity activity, View view, int position);

    /**
     * 是否拦截下载
     * @return
     */
    public abstract boolean isInterceptDownload();
}