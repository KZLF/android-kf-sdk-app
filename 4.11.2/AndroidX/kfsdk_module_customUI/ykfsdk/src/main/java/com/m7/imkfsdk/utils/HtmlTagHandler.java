package com.m7.imkfsdk.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.moor.imkf.utils.LogUtils;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Stack;

/**
 * @ClassName HtmlTagHandler
 * @Description TODO
 * @Author jiangbingxuan
 * @Date 2022/9/9 13:46
 * @Version 1.0
 */
public class HtmlTagHandler implements Html.TagHandler {
    // 自定义标签名称
    private final String tagName;

    // 标签开始索引
    private Stack<Integer> startIndex = new Stack<>();
    // 标签结束索引
    private Stack<Integer> endIndex = new Stack<>();
    // 存放标签所有属性键值对
    final HashMap<String, Stack<String>> attributes = new HashMap<>();

    public HtmlTagHandler(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // 判断是否是当前需要的tag
        if (tag.equalsIgnoreCase(tagName)) {

            // 解析所有属性值
            parseAttributes(xmlReader);
            if (opening) {
                startHandleTag(tag, output, xmlReader);
            } else {
                endEndHandleTag(tag, output, xmlReader);
            }
        }
    }

    public void startHandleTag(String tag, Editable output, XMLReader xmlReader) {
        LogUtils.dTag("startHandleTag","startIndexBefore:" + startIndex);
        startIndex.push(output.length());
        LogUtils.dTag("startHandleTag","startIndexAfter:" + startIndex);
    }

    public void endEndHandleTag(String tag, Editable output, XMLReader xmlReader) {
        LogUtils.dTag("endEndHandleTag","endIndexBefore:" + endIndex);
        int currElementStartIndex = startIndex.peek();
        //处理连续的标签
        //例如<li><font color=\"#808080\"><font color=\"#000000\">显示评价征集 <\/font>写优质评价赢多重好礼<\/font><\/li>
        if(endIndex.size() == startIndex.size()) {
            startIndex.pop();
            currElementStartIndex = endIndex.pop();
        } else {
            startIndex.pop();
        }
        endIndex.push(output.length());
        LogUtils.dTag("endEndHandleTag","endIndexAfter:" + endIndex);

        // 获取对应的属性值
        Stack<String> color = attributes.get("color");
        Stack<String> size = attributes.get("size");
//        size = size.split("px")[0];
        int currElementEndIndex = endIndex.peek();
        LogUtils.dTag("endEndHandleTag","startIndex:" + currElementStartIndex,"endIndex:" + currElementEndIndex,"output:" + output,"color:" + color);
        boolean hasMultiple = false;
        // 设置颜色
        if (color != null && !color.empty()) {
            if(color.size() > 1) {
                hasMultiple = true;
            }
            output.setSpan(new ForegroundColorSpan(Color.parseColor(color.pop())), currElementStartIndex, currElementEndIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        // 设置字体大小
        if (size != null && !size.empty()) {
            if(size.size() > 1) {
                hasMultiple = true;
            }
            output.setSpan(new AbsoluteSizeSpan(displaySize(size.pop()), true), currElementStartIndex, currElementEndIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //如果是连续标签，则把endIndex保留到下次startIndex使用
        if(!hasMultiple) {
            endIndex.pop();
        }
    }

    /**
     * 解析所有属性值
     *
     * @param xmlReader
     */
    private void parseAttributes(final XMLReader xmlReader) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            for (int i = 0; i < len; i++) {
                String key = data[i * 5 + 1];
                String value = data[i * 5 + 4];
                LogUtils.dTag("parseAttributes","key:" + key, "value:" + value,"this:" + this);
                Stack<String> values = attributes.get(key);
                if(values == null) {
                    values = new Stack<>();
                    attributes.put(key, values);
                }
                values.push(value);
            }
        } catch (Exception e) {

        }
    }


    private int displaySize(String size) {
        int displaySize = 16;
        try {
            if (!TextUtils.isEmpty(size)) {
                int originSize = Integer.parseInt(size);
                if (originSize <= 0) {
                    //如果原始字号<=0 那么返回默认
                    return displaySize;
                }
                if (originSize == 1) {
                    return 10;
                }
                if (originSize == 2) {
                    return 12;
                }
                if (originSize == 3) {
                    return displaySize;
                }
                if (originSize == 4) {
                    return 18;
                }
                if (originSize == 5) {
                    return 22;
                }
                if (originSize == 6) {
                    return 30;
                }
                if (originSize == 7) {
                    return 38;
                }
                if (originSize > 7) {
                    return displaySize + (originSize * 3);
                }
            }
        } catch (Exception e) {

        }

        return displaySize;
    }
}
