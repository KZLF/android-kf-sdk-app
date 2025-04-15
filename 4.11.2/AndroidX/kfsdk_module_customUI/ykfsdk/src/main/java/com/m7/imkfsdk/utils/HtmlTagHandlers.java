package com.m7.imkfsdk.utils;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * @ClassName HtmlTagHandlers
 * @Description
 * @Author zhangheng
 * @Date 2024/12/17
 * @Version 1.0
 */
public class HtmlTagHandlers implements Html.TagHandler{
    private final String MoorFontSizeTag = "moorFont";//用于转换标签支持字号
    private final HtmlTagHandler fontHandler = new HtmlTagHandler(MoorFontSizeTag);
    private final ListTagHandler listHandler = new ListTagHandler();
    @Override
    public void handleTag(boolean opening, String tag, Editable editable, XMLReader xmlReader) {
        fontHandler.handleTag(opening,tag,editable,xmlReader);
        listHandler.handleTag(opening,tag,editable,xmlReader);
    }
}
