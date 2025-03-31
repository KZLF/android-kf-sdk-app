package com.m7.imkfsdk.utils;

import android.os.Environment;

import com.moor.imkf.lib.utils.MoorSdkVersionUtil;

import java.io.File;

/**
 * <pre>
 *     @author : Trial
 *     @time   : 2021/08/05
 *     @desc   : 获取存储路径
 *     @version: 1.0
 * </pre>
 */
public class MoorPathUtil {

    //创建在DOWNLOADS文件中的名称
    public static String SDK_FILE_SEPARATOR="moor_kfSdk";

    public static String getImageDownLoadPath() {
        String path = "";
        if (MoorSdkVersionUtil.over29()) {
            path = Environment.DIRECTORY_PICTURES + File.separator + SDK_FILE_SEPARATOR + File.separator;
        } else {
            path = Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + SDK_FILE_SEPARATOR + File.separator;
            if (!MoorFileUtils.isFileExists(path)) {
                MoorFileUtils.createOrExistsDir(new File(path));
            }
        }
        return path;
    }

    public static String getFileDownLoadPath() {
        String path = "";
        if (MoorSdkVersionUtil.over29()) {
            path = Environment.DIRECTORY_DOWNLOADS + File.separator + SDK_FILE_SEPARATOR + File.separator;
        } else {
            path = Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + SDK_FILE_SEPARATOR + File.separator;
            if (!MoorFileUtils.isFileExists(path)) {
                MoorFileUtils.createOrExistsDir(new File(path));
            }
        }
        return path;
    }
}
