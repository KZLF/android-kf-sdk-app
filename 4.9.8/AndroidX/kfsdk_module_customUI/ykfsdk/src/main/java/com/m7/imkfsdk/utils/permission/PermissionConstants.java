package com.m7.imkfsdk.utils.permission;

import android.Manifest;

import java.util.HashMap;


/**
 * @Description:
 * @Author: chenbo
 * @Date: 2020/6/17
 */
public class PermissionConstants {
    private static PermissionConstants instance;
    private static final HashMap<String, String> perMap = new HashMap<>();

    public static final String STORE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;

    //针对api33 需要处理的权限
    public static final String IMAGEBY_API33 = Manifest.permission.READ_MEDIA_IMAGES;
    public static final String AUDIOBY_API33 = Manifest.permission.READ_MEDIA_AUDIO;
    public static final String VIDEOBY_API33 = Manifest.permission.READ_MEDIA_VIDEO;

    public PermissionConstants() {
        perMap.put(STORE, "存储");
        perMap.put(CAMERA, "相机");
        perMap.put(RECORD_AUDIO, "麦克风");
    }

    public static synchronized PermissionConstants getInstance() {
        if (instance == null) {
            synchronized (PermissionConstants.class) {
                if (instance == null)
                    instance = new PermissionConstants();
            }
        }
        return instance;
    }

    public String getPermissionName(String permission) {
        return perMap.get(permission);
    }
}
