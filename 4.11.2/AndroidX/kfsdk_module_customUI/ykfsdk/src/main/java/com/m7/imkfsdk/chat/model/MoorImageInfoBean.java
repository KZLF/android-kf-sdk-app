package com.m7.imkfsdk.chat.model;

import androidx.annotation.NonNull;

/**
 * <pre>
 *     @author : Trial
 *     @time   : 2021/02/04
 *     @desc   :
 *     @version: 1.0
 * </pre>
 */
public class MoorImageInfoBean {
    private String id;
    private String path;
    @NonNull
    private String from;

    /**
     * 布局尺寸的宽
     */
    private int layoutW = 0;
    /**
     * 布局尺寸的高
     */
    private int layoutH = 0;
    /**
     * 根据比例计算的真正的尺寸，交给glide加载使用
     */
    private int realSize = 0;
    /**
     * 类型:方图或默认: 0;横图: 1;竖图: 2;
     */
    private MoorImageType type;
    private float startScale;
    public String getId() {
        return id;
    }

    public MoorImageInfoBean setId(String id) {
        this.id = id;
        return this;
    }

    public String getPath() {
        return path;
    }

    public MoorImageInfoBean setPath(String path) {
        this.path = path;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public MoorImageInfoBean setFrom(String from) {
        this.from = from;
        return this;
    }

    public int getLayoutW() {
        return layoutW;
    }

    public MoorImageInfoBean setLayoutW(int layoutW) {
        this.layoutW = layoutW;
        return this;
    }

    public int getLayoutH() {
        return layoutH;
    }

    public MoorImageInfoBean setLayoutH(int layoutH) {
        this.layoutH = layoutH;
        return this;
    }

    public int getRealSize() {
        return realSize;
    }

    public MoorImageInfoBean setRealSize(int realSize) {
        this.realSize = realSize;
        return this;
    }

    public MoorImageType getType() {
        return type;
    }

    public MoorImageInfoBean setType(MoorImageType type) {
        this.type = type;
        return this;
    }

    public float getStartScale() {
        return startScale;
    }

    public MoorImageInfoBean setStartScale(float startScale) {
        this.startScale = startScale;
        return this;
    }
}
