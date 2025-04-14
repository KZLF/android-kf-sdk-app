package com.m7.imkfsdk.chat.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m7.imkfsdk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UselessAdapter extends RecyclerView.Adapter<UselessAdapter.UselessHolder> {

    ArrayList<String> dataList = new ArrayList<String>();
    int lastPostion = -2;
    int multiple_choice = 0;  //默认关闭
    HashMap<Integer, String> muilclickdatas = new HashMap<>();
    ArrayList<String> allTagList = new ArrayList<>();
    ArrayList<String> selectTagList = new ArrayList<>();
    ArrayList<Integer> selectNum = new ArrayList<>();

    boolean isHistory;

    String lastTarglis = "";


    @NonNull
    @Override
    public UselessHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ykfsdk_useless_item_view, viewGroup, false);
        return new UselessHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final UselessHolder holder, final int i) {
        //  展示历史的机器人点踩选择的数据。
        if (isHistory) {
            holder.tv_tip.setOnClickListener(null);
            holder.tv_tip.setText(allTagList.get(i));
            if (allTagList.get(i).contains("##")) {
                if (!TextUtils.isEmpty(allTagList.get(i).split("##")[0])) {
                    holder.tv_tip.setText(allTagList.get(i).split("##")[0]);
                    holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_select));
                    holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_all_white));
                }
            } else {
                holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_unselect));
                holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_kf_tag_unselect));
            }
        } else {
            // 下面这个是当前机器人评价点踩之后选择的数据
            holder.tv_tip.setText(dataList.get(i));
            // 单选
            if (multiple_choice == 0) {
                // 重新刷新数据
                if (lastPostion == i) {
                    holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_select));
                    holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_all_white));
                } else {
                    holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_unselect));
                    holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_kf_tag_unselect));
                }
                final int num = i;
                if (onItemClickListener != null) {
                    holder.tv_tip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (lastPostion == num) {
                                lastTarglis = "";
                                holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_unselect));
                                holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_kf_tag_unselect));
                                lastPostion = -2;
                            } else if (lastPostion != num) {
                                lastTarglis = holder.tv_tip.getText().toString();
                                holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_select));
                                holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_all_white));
                                lastPostion = num;
                                notifyDataSetChanged();
                            }
                            onItemClickListener.onItemClick(num, lastTarglis);
                        }
                    });
                }
                // 多选
            } else if (multiple_choice == 1) {
                final int num = i;
                holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_unselect));
                holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_kf_tag_unselect));
                if (onItemClickListener != null) {
                    holder.tv_tip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 已经点击过的
                            if (muilclickdatas.get(num) != null) {
                                holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_unselect));
                                holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_kf_tag_unselect));
                                muilclickdatas.remove(num);
                                onItemClickListener.onItemClick(num, getMuilTagList(muilclickdatas));
                            } else {
                                muilclickdatas.put(num, holder.tv_tip.getText().toString());
                                onItemClickListener.onItemClick(num, getMuilTagList(muilclickdatas));
                                holder.tv_tip.setBackground(holder.tv_tip.getResources().getDrawable(R.drawable.ykfsdk_useless_textview_select));
                                holder.tv_tip.setTextColor(holder.tv_tip.getResources().getColor(R.color.ykfsdk_all_white));
                            }
                        }
                    });
                }
            }
        }
    }

    public String getMuilTagList(HashMap<Integer, String> datas) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<Integer, String>> en = datas.entrySet();
        for (Map.Entry<Integer, String> entry : en) {
            String value = entry.getValue();
            sb.append(value);
            sb.append("##");
        }
        return sb.toString();
    }

    public void setDatas(ArrayList<String> datas) {
        dataList = datas;
    }

    public void setAllTagList(String allTag) {
        if (!TextUtils.isEmpty(allTag)) {
            String[] all = allTag.split("##");
            for (String s : all) {
                allTagList.add(s);
            }
        }
    }

    public void setSelectTagList(String select) {
        if (!TextUtils.isEmpty(select)) {
            String[] selectList = select.split("##");
            for (String s : selectList) {
                selectTagList.add(s);
            }
        }
    }

    public void selectData() {
        for (int i = 0; i < allTagList.size(); i++) {
            for (int j = 0; j < selectTagList.size(); j++) {
                if (allTagList.get(i).equals(selectTagList.get(j))) {
                    selectNum.add(i);
                    allTagList.set(i, allTagList.get(i) + "##");
                }
            }
        }
    }

    public void showHistory(boolean history) {
        isHistory = history;
        notifyDataSetChanged();
    }

    public void setMultiple_choice(int multiple_choice) {
        this.multiple_choice = multiple_choice;
    }

    @Override
    public int getItemCount() {
        if (dataList.size() > 0) {
            return dataList.size();   // 这个是正常的数据
        } else if (allTagList.size() > 0) {
            return allTagList.size();  // 这个是历史数据
        } else {
            return dataList.size();
        }
    }

    class UselessHolder extends RecyclerView.ViewHolder {
        TextView tv_tip;

        public UselessHolder(@NonNull final View itemView) {
            super(itemView);
            tv_tip = itemView.findViewById(R.id.tv_tip);
        }
    }

    onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(int postion, String name);
    }
}
