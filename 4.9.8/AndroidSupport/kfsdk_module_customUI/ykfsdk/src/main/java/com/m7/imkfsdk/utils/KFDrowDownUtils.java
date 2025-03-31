package com.m7.imkfsdk.utils;

import android.content.Context;
import android.graphics.Color;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.view.dropdownmenu.DropDownMenu;

import java.util.ArrayList;
import java.util.List;

public class KFDrowDownUtils {

    public static void setChatDropDownMenu(Context context, DropDownMenu mMenu,String[] stringArray) {
        mMenu.setmMenuCount(1);
        mMenu.setmShowCount(6);
        mMenu.setShowCheck(true);
        mMenu.setmMenuTitleTextSize(14);
        mMenu.setmMenuTitleTextColor(R.color.ykfsdk_all_black);
        mMenu.setmMenuListTextSize(14);
        mMenu.setmMenuListTextColor(Color.BLACK);
        mMenu.setmMenuPressedBackColor(Color.WHITE);
        mMenu.setmMenuPressedTitleTextColor(R.color.ykfsdk_all_black);

        mMenu.setmCheckIcon(R.drawable.ykfsdk_ico_make);

//        mMenu.setmUpArrow(R.drawable.arrow_up);
//        mMenu.setmDownArrow(R.drawable.arrow_down);
        final String[] strings = new String[]{stringArray[0]};
        mMenu.setDefaultMenuTitle(strings);
        mMenu.setShowDivider(false);
        mMenu.setmMenuListBackColor(context.getResources().getColor(R.color.ykfsdk_all_white));
        mMenu.setmMenuListSelectorRes(R.color.ykfsdk_all_white);
        mMenu.setmArrowMarginTitle(20);
        List<String[]> items = new ArrayList<>();
        items.add(stringArray);
        mMenu.setmMenuItems(items);
        mMenu.setIsDebug(false);
    }

}
