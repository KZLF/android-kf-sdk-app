package com.m7.imkfsdk.view.bottomselectview;

import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.moor.imkf.model.entity.WebChatInterface;

import java.util.ArrayList;

/**
 * Author: Blincheng.
 * Date: 2017/5/9.
 * Description:收货地址选择器
 */

public class WebChatSelector extends LinearLayout implements View.OnClickListener {
    private int TextSelectedColor;
    private int TextEmptyColor;
    //顶部的tab集合
    private ArrayList<Tab> tabs;
    //列表的适配器
    private AddressAdapter addressAdapter;
    private ArrayList<WebChatInterface> cities;
    private WebChatOnItemClickListener webChatOnItemClickListener;
    private OnTabSelectedListener onTabSelectedListener;
    private RecyclerView list;
    //tabs的外层layout
    private LinearLayout tabs_layout;
    //会移动的横线布局
    private Line line;
    private Context mContext;
    //总共tab的数量
    private int tabAmount = 3;
    //当前tab的位置
    private int tabIndex = 0;
    //分隔线
    private View grayLine;
    //列表文字大小
    private int listTextSize = -1;
    //列表文字颜色
    private int listTextNormalColor;
    //列表文字选中的颜色
    private int listTextSelectedColor;
    //列表icon资源
    private int listItemIcon = -1;

    public WebChatSelector(Context context) {
        super(context);
        init(context);
    }

    public WebChatSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebChatSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        removeAllViews();
        this.mContext = context;

        this.TextSelectedColor = mContext.getResources().getColor(R.color.ykfsdk_selected_color);
        this.TextEmptyColor = mContext.getResources().getColor(R.color.ykfsdk_color_333333);
        this.listTextNormalColor = mContext.getResources().getColor(R.color.ykfsdk_color_333333);
        this.listTextSelectedColor = mContext.getResources().getColor(R.color.ykfsdk_selected_color);

        setOrientation(VERTICAL);
        tabs_layout = new LinearLayout(mContext);
        tabs_layout.setWeightSum(tabAmount);
        tabs_layout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        tabs_layout.setOrientation(HORIZONTAL);
        addView(tabs_layout);
        tabs = new ArrayList<>();
        Tab tab = newTab("请选择", true);
        tabs_layout.addView(tab);
        tabs.add(tab);
        for (int i = 1; i < tabAmount; i++) {
            Tab _tab = newTab("", false);
            _tab.setIndex(i);
            tabs_layout.addView(_tab);
            tabs.add(_tab);
        }
        line = new Line(mContext);
        line.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, 6));
        line.setSum(tabAmount);
        addView(line);
        grayLine = new View(mContext);
        grayLine.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, 2));
        grayLine.setBackgroundColor(mContext.getResources().getColor(R.color.ykfsdk_ykf_line_DDDDDD));
        addView(grayLine);
        list = new RecyclerView(mContext);
        list.setLayoutParams(new ViewGroup.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        list.setLayoutManager(new LinearLayoutManager(mContext));
        addView(list);
    }

    /**
     * 得到一个新的tab对象
     */
    private Tab newTab(CharSequence text, boolean isSelected) {
        Tab tab = new Tab(mContext);
        tab.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        tab.setGravity(Gravity.CENTER);
        tab.setPadding(0, 20, 0, 20);
        tab.setSelected(isSelected);
        tab.setText(text);
        tab.setTextEmptyColor(TextEmptyColor);
        tab.setTextSelectedColor(TextSelectedColor);
        tab.setOnClickListener(this);

        tab.setSingleLine(true);
        tab.setEllipsize(TextUtils.TruncateAt.END);

        return tab;
    }

    /**
     * 设置tab的数量，默认3个,不小于1个
     *
     * @param tabAmount tab的数量
     */
    public void setTabAmount(int tabAmount) {
        if (tabAmount >= 1) {
            this.tabAmount = tabAmount;
            init(mContext);
        } else {
            throw new RuntimeException("AddressSelector tabAmount can not less-than 1 !");
        }
    }

    /**
     * 设置列表的点击事件回调接口
     */
    public void setWebChatOnItemClickListener(WebChatOnItemClickListener webChatOnItemClickListener) {
        this.webChatOnItemClickListener = webChatOnItemClickListener;
    }

    /**
     * 设置列表的数据源，设置后立即生效
     */
    public void setCities(ArrayList cities) {
        if (cities == null || cities.size() <= 0) {
            return;
        }
        if (cities.get(0) instanceof WebChatInterface) {
            this.cities = cities;
            if (addressAdapter == null) {
                addressAdapter = new AddressAdapter();
                list.setAdapter(addressAdapter);
            }
            addressAdapter.notifyDataSetChanged();
        } else {
            throw new RuntimeException("AddressSelector cities must implements CityInterface");
        }
    }

    /**
     * 设置顶部tab的点击事件回调
     */
    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }

    @Override
    public void onClick(View v) {
        Tab tab = (Tab) v;
        //如果点击的tab大于index则直接跳出
        if (tab.index > tabIndex) {
            return;
        }
        tabIndex = tab.index;
        if (onTabSelectedListener != null) {
            if (tab.isSelected) {
                onTabSelectedListener.onTabReselected(WebChatSelector.this, tab);
            } else {
                onTabSelectedListener.onTabSelected(WebChatSelector.this, tab);
            }
        }
        resetAllTabs(tabIndex);
        line.setIndex(tabIndex);
        tab.setSelected(true);
    }

    private void resetAllTabs(int tabIndex) {
        if (tabs != null) {
            for (int i = 0; i < tabs.size(); i++) {
                tabs.get(i).resetState();
                if (i > tabIndex) {
                    tabs.get(i).setText("");
                }
            }
        }
    }

    /**
     * 设置Tab文字选中的颜色
     */
    public void setTextSelectedColor(int textSelectedColor) {
        TextSelectedColor = textSelectedColor;
    }

    /**
     * 设置Tab文字默认颜色
     */
    public void setTextEmptyColor(int textEmptyColor) {
        TextEmptyColor = textEmptyColor;
    }

    /**
     * 设置Tab横线的颜色
     */
    public void setLineColor(int lineColor) {
        line.setSelectedColor(lineColor);
    }

    /**
     * 设置tab下方分隔线的颜色
     */
    public void setGrayLineColor(int grayLineColor) {
        grayLine.setBackgroundColor(grayLineColor);
    }

    /**
     * 设置列表文字大小
     */
    public void setListTextSize(int listTextSize) {
        this.listTextSize = listTextSize;
    }

    /**
     * 设置列表文字颜色
     */
    public void setListTextNormalColor(int listTextNormalColor) {
        this.listTextNormalColor = listTextNormalColor;
    }

    /**
     * 设置列表选中文字颜色
     */
    public void setListTextSelectedColor(int listTextSelectedColor) {
        this.listTextSelectedColor = listTextSelectedColor;
    }

    /**
     * 设置列表icon资源
     */
    public void setListItemIcon(int listItemIcon) {
        this.listItemIcon = listItemIcon;
    }

    /**
     * 标签控件
     */
    public class Tab extends TextView {
        private int index = 0;
        private int TextSelectedColor;
        private int TextEmptyColor;
        /**
         * 是否选中状态
         */
        private boolean isSelected = false;

        public Tab(Context context) {
            super(context);
            init();
        }

        public Tab(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public Tab(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            this.TextSelectedColor = mContext.getResources().getColor(R.color.ykfsdk_selected_color);
            this.TextEmptyColor = mContext.getResources().getColor(R.color.ykfsdk_color_333333);
            setTextSize(15);
        }

        @Override
        public void setText(CharSequence text, BufferType type) {
            if (isSelected) {
                setTextColor(TextSelectedColor);
            } else {
                setTextColor(TextEmptyColor);
            }
            super.setText(text, type);
        }

        @Override
        public void setSelected(boolean selected) {
            isSelected = selected;
            setText(getText());
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void resetState() {
            isSelected = false;
            setText(getText());
        }

        public void setTextSelectedColor(int textSelectedColor) {
            TextSelectedColor = textSelectedColor;
        }

        public void setTextEmptyColor(int textEmptyColor) {
            TextEmptyColor = textEmptyColor;
        }
    }

    /**
     * 横线控件
     */
    private class Line extends LinearLayout {
        private int sum = 3;
        private final int oldIndex = 0;
        private int nowIndex = 0;
        private View indicator;
        private int SelectedColor;

        public Line(Context context) {
            super(context);
            init(context);
        }

        public Line(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public Line(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            this.SelectedColor = mContext.getResources().getColor(R.color.ykfsdk_selected_color);

            setOrientation(HORIZONTAL);
            setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, 6));
            setWeightSum(tabAmount);
            indicator = new View(context);
            indicator.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
            indicator.setBackgroundColor(SelectedColor);
            addView(indicator);
        }

        public void setIndex(int index) {
            int onceWidth = getWidth() / sum;
            this.nowIndex = index;
            ObjectAnimator animator = ObjectAnimator.ofFloat(indicator, "translationX",
                    indicator.getTranslationX(), (nowIndex - oldIndex) * onceWidth);
            animator.setDuration(300);
            animator.start();
        }

        public void setSum(int sum) {
            this.sum = sum;
        }

        public void setSelectedColor(int selectedColor) {
            SelectedColor = selectedColor;
        }
    }

    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder =
                    new MyViewHolder(LayoutInflater.from(mContext).inflate(
                            R.layout.ykfsdk_kf_item_chataddress, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (listItemIcon != -1) {
                holder.img.setImageResource(listItemIcon);
            }
            if (listTextSize != -1) {
                holder.tv.setTextSize(listTextSize);
            }
            if (TextUtils.equals(tabs.get(tabIndex).getText(), cities.get(position).getCityName())) {
                holder.img.setVisibility(View.VISIBLE);
                holder.tv.setTextColor(listTextSelectedColor);
            } else {
                holder.img.setVisibility(View.INVISIBLE);
                holder.tv.setTextColor(listTextNormalColor);
            }
            holder.tv.setText(cities.get(position).getCityName());
            holder.itemView.setTag(cities.get(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (webChatOnItemClickListener != null) {
                        webChatOnItemClickListener.itemClick(WebChatSelector.this, (WebChatInterface) v.getTag(), tabIndex);
                        WebChatInterface cityInterface = (WebChatInterface) v.getTag();

                        if (cityInterface.getOption().size() > 0) {
                            tabs.get(tabIndex).setText(((WebChatInterface) v.getTag()).getCityName());
                            tabs.get(tabIndex).setTag(v.getTag());
                            if (tabIndex + 1 < tabs.size()) {
                                tabIndex++;
                                resetAllTabs(tabIndex);
                                line.setIndex(tabIndex);
                                tabs.get(tabIndex).setText("请选择");
                                tabs.get(tabIndex).setSelected(true);
                            }
                        } else {
                            tabs.get(tabIndex).setText(((WebChatInterface) v.getTag()).getCityName());
                            tabs.get(tabIndex).setTag(v.getTag());
                            if (tabIndex + 1 < tabs.size()) {
//                                 tabIndex ++;
                                resetAllTabs(tabIndex);
                                notifyDataSetChanged();
//                                 line.setIndex(tabIndex);
//                                 tabs.get(tabIndex).setText("请选择");
//                                 tabs.get(tabIndex).setSelected(true);
                            }
                        }

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return cities.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv;
            public ImageView img;
            public View itemView;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                tv = itemView.findViewById(R.id.item_address_tv);
                img = itemView.findViewById(R.id.item_address_img);
            }
        }
    }

    public interface OnTabSelectedListener {
        void onTabSelected(WebChatSelector webChatSelector, Tab tab);

        void onTabReselected(WebChatSelector webChatSelector, Tab tab);
    }
}
