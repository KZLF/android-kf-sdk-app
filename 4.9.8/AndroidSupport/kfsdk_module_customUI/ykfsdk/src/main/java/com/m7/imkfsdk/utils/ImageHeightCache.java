package com.m7.imkfsdk.utils;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * cc sdk xbot富文本高度缓存
 * imageview 对象缓存
 *
 * @ClassName ImageHeightCache
 * @Description 富文本中图片view高度缓存，较少列表跳动
 * @Author jiangbingxuan
 * @Date 1/10/22 5:45 PM
 * @Version 1.0
 */
public class ImageHeightCache {

    /**
     * key 图片url
     * value imageview高度
     */
    private HashMap<String, Integer> cacheImage = new HashMap<String, Integer>();


    public static ImageHeightCache getInstance() {
        return ImageHeightCache.ImageHeightCacheClassHolder.instance;
    }

    private static class ImageHeightCacheClassHolder {
        private static final ImageHeightCache instance = new ImageHeightCache();
    }

    private ImageHeightCache() {
    }

    /**
     * @param url 图片url
     * @param h   imageview高度
     */
    public void putImgH(String url, int h) {
        if (cacheImage == null) {
            cacheImage = new HashMap<String, Integer>();
        }
        cacheImage.put(url, h);
    }

    /**
     * @param url 图片url
     * @return imageview高度
     */
    public int getImgH(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (cacheImage != null) {
                if (cacheImage.get(url) != null) {
                    return cacheImage.get(url);
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }


}
