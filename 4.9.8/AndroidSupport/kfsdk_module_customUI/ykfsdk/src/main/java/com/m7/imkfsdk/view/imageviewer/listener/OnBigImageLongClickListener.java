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
public interface OnBigImageLongClickListener {

    /**
     * 长按事件
     */
    boolean onLongClick(Activity activity, View view, int position);
}