package com.m7.imkfsdk.view.dropdownmenu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.utils.FIleResourceUtil;


public class MenuListAdapter extends BaseAdapter
{

    private final Context context;
    private final String[] strs;

    private int SelectIndex;
    private int TextSize;
    private int TextColor;
    private boolean showCheck;
    private int CheckIcon;
    private int mTextColorSelected;

    public MenuListAdapter(Context context, String[] strs)
    {
        this.context = context;
        this.strs = strs;
        this.TextColor = Color.BLACK;
        this.TextSize = 15;
    }

    public void setSelectIndex(int selectIndex)
    {
        SelectIndex = selectIndex;
    }


    public void setShowCheck(boolean showCheck)
    {
        this.showCheck = showCheck;
    }

    public void setTextSize(int textSize)
    {
        TextSize = textSize;
    }

    public void setTextColor(int textColor)
    {
        TextColor = textColor;
    }

    public void setCheckIcon(int checkIcon)
    {
        CheckIcon = checkIcon;
    }

    public void setTextColorSelected(int textColor)
    {
        mTextColorSelected = textColor;
    }

    @Override
    public int getCount()
    {
        return strs.length;
    }

    @Override
    public Object getItem(int position)
    {
        return strs[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.ykfsdk_kf_menu_list_item, parent, false);
        TextView textView = v.findViewById(R.id.tv_menu_item);
        textView.setTextSize(TextSize);
        textView.setTextColor(TextColor);
        textView.setText(strs[position]);

        if (showCheck && SelectIndex == position)
        {
            ImageView imageView = v.findViewById(R.id.iv_menu_select);
            imageView.setVisibility(View.VISIBLE);
            if (mTextColorSelected != 0 &&
                    mTextColorSelected != FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default))
            {
                textView.setTextColor(mTextColorSelected);
            } else
            {
                textView.setTextColor(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
            }
            if (CheckIcon != 0) {
                imageView.setImageResource(CheckIcon);
            }
        }
        return v;
    }

}