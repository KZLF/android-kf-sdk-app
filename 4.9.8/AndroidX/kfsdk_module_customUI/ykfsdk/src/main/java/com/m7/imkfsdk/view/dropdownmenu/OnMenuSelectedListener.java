package com.m7.imkfsdk.view.dropdownmenu;

import android.view.View;

/**
 * <p>
 * Package Name:com.moor.cc.views.dropdownmenu
 * </p>
 * <p>
 * Class Name:OnMenuSelectedListener
 * <p>
 * Description: <a href="http://fangjie.info">JayFang</a>
 *              Email:JayFang1993@gmail.com
 *              GitHub:github.com/JayFang1993
 * </p>
 *s
 * @Author JayFang
 * @Version 1.0
 * @Reviser:  Martin
 * @Modification Time:2018/7/31 16:19
 */
public interface OnMenuSelectedListener {

    /**
     * Menu选择监听器
     * @param listview current clicked listview
     * @param RowIndex list的索引
     * @param ColumnIndex menu的索引
     */
    void onSelected(View listview, int RowIndex, int ColumnIndex);

}
