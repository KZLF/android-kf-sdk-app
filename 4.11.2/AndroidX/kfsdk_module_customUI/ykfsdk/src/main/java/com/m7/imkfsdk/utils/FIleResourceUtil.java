package com.m7.imkfsdk.utils;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.AttrRes;
import android.text.TextUtils;
import android.util.TypedValue;

import com.m7.imkfsdk.R;


/**
 * @Description:
 * @Author: chenbo
 * @Date: 2020/9/29
 */
public class FIleResourceUtil {

    private static int getColorByAttributeId(Context context, @AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    public static int getCurrentColor(Context context, int color) {
        return getColorByAttributeId(context, color);
    }


    public static int getFile_Icon(Context context,String fileName){
        int resId;
        if(!TextUtils.isEmpty(fileName)){
            String endStr = fileName.toLowerCase().substring(fileName.lastIndexOf(".") + 1);
            switch (endStr) {
                case "zip":
                case "rar":
                    resId = R.drawable.ykfsdk_ykf_icon_file_default;
                    break;
                case "word":
                case "doc":
                case "docx":
                    resId = R.drawable.ykfsdk_ykf_icon_file_word;
                    break;
                case "ppt":
                case "pptx":
                    resId = R.drawable.ykfsdk_ykf_icon_file_ppt;
                    break;
                case "pdf":
                    resId = R.drawable.ykfsdk_ykf_icon_file_pdf;
                    break;
                case "excel":
                case "xls":
                case "xlsx":
                    resId = R.drawable.ykfsdk_ykf_icon_file_xls;
                    break;
                case "mp4":
                case "mov":
                case "avi":
                    resId = R.drawable.ykfsdk_ykf_icon_file_video;
                    break;
                case "mp3":
                case "wav":
                    resId = R.drawable.ykfsdk_ykf_icon_file_music;
                    break;
                case "jpg":
                case "png":
                case "bmp":
                case "jpeg":
                    resId = R.drawable.ykfsdk_ykf_icon_file_jpg;
                    break;
                default:
                    resId = R.drawable.ykfsdk_ykf_icon_file_default;
                    break;
            }
        }else{
            resId = R.drawable.ykfsdk_ykf_icon_file_default;
        }
        return  resId;
    }
}
