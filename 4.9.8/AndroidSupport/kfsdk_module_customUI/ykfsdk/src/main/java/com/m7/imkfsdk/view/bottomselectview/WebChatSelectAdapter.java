package com.m7.imkfsdk.view.bottomselectview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.moor.imkf.model.entity.AddressData;

import java.util.List;

/**
 * Created by pangw on 2018/6/14.
 */

public class WebChatSelectAdapter extends BaseAdapter {
    public Context mContext;
    public List<AddressData> list;
    public int selected = -1;

    public int getSelected() {
        return selected;
    }

    public WebChatSelectAdapter(Context mContext, List<AddressData> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public AddressData getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ykfsdk_kf_item_view_chatselect, null);
            holder.lin = convertView.findViewById(R.id.item_view_address_lin);

            holder.name = convertView.findViewById(R.id.item_view_address_name);
            holder.select = convertView.findViewById(R.id.item_view_address_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AddressData bean = getItem(i);
        holder.name.setText(bean.getCityName());
        final ViewHolder finalHolder = holder;
        holder.lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selected==i){
                    finalHolder.select.setVisibility(View.GONE);
                    selected = -1;
                }else{
                    finalHolder.select.setVisibility(View.VISIBLE);
                    selected = i;
                }
                notifyDataSetChanged();

            }
        });
        if(selected== i){
            holder.select.setVisibility(View.VISIBLE);
        }else{
            holder.select.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        RelativeLayout lin;
        TextView name;
        TextView select;
    }
}
