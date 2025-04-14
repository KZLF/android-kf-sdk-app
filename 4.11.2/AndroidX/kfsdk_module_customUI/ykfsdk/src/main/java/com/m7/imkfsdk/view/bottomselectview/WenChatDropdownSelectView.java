package com.m7.imkfsdk.view.bottomselectview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.utils.ToastUtils;
import com.moor.imkf.model.entity.AddressData;
import com.moor.imkf.model.entity.AddressResult;
import com.moor.imkf.model.entity.WebChatInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pangw on 2018/6/12.
 */

public class WenChatDropdownSelectView {
    private final Context mContext;
    private Dialog mCameraDialog;
    private OnClickItemListent listen;
    private final AddressResult maoption;
    private final HashMap<Integer, AddressData> selectMap;
    private WebChatSelectAdapter adapter;
    private final List<AddressData> options;
    private final int count;
    public WenChatDropdownSelectView(Context context, AddressResult maoption, int count) {
        mContext = context;
        this.maoption = maoption;
        this.count=count;
        selectMap = new HashMap<>();
        options = new ArrayList<>();
        initView();
    }


    private void initView() {

        mCameraDialog = new Dialog(mContext, R.style.ykfsdk_BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.ykfsdk_view_chat_dropdown, null);
        //初始化视图
        TextView cancel = root.findViewById(R.id.view_bottom_dropdown_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen.cancel();
                cancel();
            }
        });


        TextView save = root.findViewById(R.id.view_bottom_dropdown_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (count >= 1) {
                    // TODO: 2019-10-09 加了这个判断，是否能拦住没有填完就提交的情况？
//                    if (selectMap.size() != count) {
//                        ToastUtils.showShort("请您填写完整");
//                        return;
//                    }

                    Object[] key_arr = selectMap.keySet().toArray();
                    Arrays.sort(key_arr);

                    for (int i = 0; i < key_arr.length; i++) {
                        options.add(selectMap.get(i));
                    }
                }
                if(options.size()-1<0){
                    ToastUtils.showShort(mContext,mContext.getString(R.string.ykfsdk_ykf_please_edit_complete));
                    return;
                }
                if(options.get(options.size()-1).children.size()>0){
                    ToastUtils.showShort(mContext,mContext.getString(R.string.ykfsdk_ykf_please_edit_complete));
                    return;
                }
                listen.save(options);
                cancel();
            }
        });

        WebChatSelector webChatSelector = root.findViewById(R.id.view_bottom_dropdown_addressselect);

        ListView listView = root.findViewById(R.id.view_bottom_dropdown_listview);

            int typeCount = count;

            //级联
            webChatSelector.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            final ArrayList<AddressData> data = new ArrayList<>();
            final ArrayList<AddressData> data01 = new ArrayList<>();
            final ArrayList<AddressData> data02 = new ArrayList<>();
            final ArrayList<AddressData> data03 = new ArrayList<>();
            final ArrayList<AddressData> data04 = new ArrayList<>();
            final ArrayList<AddressData> data05 = new ArrayList<>();

            webChatSelector.setTabAmount(typeCount);

            data.clear();
            data.addAll(maoption.addressDatas);

            webChatSelector.setCities(data);
            webChatSelector.setWebChatOnItemClickListener(new WebChatOnItemClickListener() {
                @Override
                public void itemClick(WebChatSelector webChatSelector, WebChatInterface city, int tabPosition) {

                    AddressData o = new AddressData();
                    o.label = city.getCityName();
                    o.value = city.getCityId();
                    o.children=city.getOption();
                    selectMap.put(tabPosition, o);

                    switch (tabPosition) {
                        case 0:
                            String key0 = getKeyFromCityName(data, city.getCityName());
                            List<AddressData> tempOptions = getOptionsByKey(data, key0);
                            data01.clear();
                            if (tempOptions != null) {
                                data01.addAll(tempOptions);
                            }
                            webChatSelector.setCities(data01);
                            break;
                        case 1:
                            String key1 = getKeyFromCityName(data01, city.getCityName());
                            List<AddressData> tempOptions01 = getOptionsByKey(data01, key1);
                            data02.clear();
                            if (tempOptions01 != null) {
                                data02.addAll(tempOptions01);
                            }
                            webChatSelector.setCities(data02);
                            break;
                        case 2:
                            String key2 = getKeyFromCityName(data02, city.getCityName());
                            List<AddressData> tempOptions02 = getOptionsByKey(data02, key2);
                            data03.clear();
                            if (tempOptions02 != null) {
                                data03.addAll(tempOptions02);
                            }

                            webChatSelector.setCities(data03);
                            break;
                        case 3:
                            String key3 = getKeyFromCityName(data03, city.getCityName());
                            List<AddressData> tempOptions03 = getOptionsByKey(data03, key3);
                            data04.clear();
                            if (tempOptions03 != null) {
                                data04.addAll(tempOptions03);
                            }

                            webChatSelector.setCities(data04);
                            break;
                        case 4:
                            webChatSelector.setCities(data05);
                            break;

                    }
                }
            });
            webChatSelector.setOnTabSelectedListener(new WebChatSelector.OnTabSelectedListener() {
                @Override
                public void onTabSelected(WebChatSelector webChatSelector, WebChatSelector.Tab tab) {
                    switch (tab.getIndex()) {
                        case 0:
                            webChatSelector.setCities(data);
                            break;
                        case 1:
                            webChatSelector.setCities(data01);
                            break;
                        case 2:
                            webChatSelector.setCities(data02);
                            break;
                        case 3:
                            webChatSelector.setCities(data03);
                            break;
                        case 4:
                            webChatSelector.setCities(data04);
                            break;
                    }
                }

                @Override
                public void onTabReselected(WebChatSelector webChatSelector, WebChatSelector.Tab tab) {

                }
            });





        //常量
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
    }

//    private List<Option> isAddState(List<Option> oss) {
//        List<Option> datas = new ArrayList<>();
//        for (Option o : oss) {
//            if (o.state.equals("2")) {
//                datas.add(o);
//            }
//        }
//        return datas;
//    }

    private String getKeyFromCityName(ArrayList<AddressData> data, String cityName) {
        String key = "";
        for (AddressData o : data) {
            if (o.label.equals(cityName)) {
                key = o.value;
            }
        }
        return key;
    }

    public void show() {
        if (mCameraDialog != null) {
            mCameraDialog.show();
        }
    }

    public void cancel() {
        if (mCameraDialog != null) {
            mCameraDialog.cancel();
        }
    }

    public interface OnClickItemListent {
        void save(List<AddressData> options);

        void cancel();
    }

    public void setOnClickListent(OnClickItemListent listen) {
        this.listen = listen;
    }

    private List<AddressData> getOptionsByKey(List<AddressData> o, String key) {
        for (int i = 0; i < o.size(); i++) {
            if (key != null && key.equals(o.get(i).value)) {
                return o.get(i).children;
            }
        }
        return new ArrayList<AddressData>();
    }
}
