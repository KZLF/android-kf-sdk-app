package com.m7.imkfsdk.view.imageviewer.listener;

import android.view.View;

/**
 * <pre>
 *     @author : Trial
 *     @time   : 2/23/21
 *     @desc   : 原图加载百分比接口
 *     @version: 1.0
 * </pre>
 */
public interface OnOriginProgressListener {

    /**
     * 加载中
     */
    void progress(View parentView, int progress);

    /**
     * 加载完成
     */
    void finish(View parentView);
}