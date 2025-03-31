package com.m7.imkfsdk.utils;

import android.content.Context;
import android.widget.Toast;

import com.moor.imkf.lib.utils.toast.MoorToastCompat;
/**
 * @author jiangbingxuan
 * 吐司相关工具类，实际调用MoorToastCompat
 */
public final class ToastUtils {
    private ToastUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 安全地显示短时吐司
     */
    public static void showShort(Context context, final CharSequence text) {
        MoorToastCompat.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源Id
     */
    public static void showShort(Context context, final int resId) {
        MoorToastCompat.makeText(context.getApplicationContext(), context.getString(resId), Toast.LENGTH_SHORT).show();
    }


    /**
     * 安全地显示长时吐司
     *
     * @param text 文本
     */
    public static void showLong(Context context, final CharSequence text) {
        MoorToastCompat.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }


}