package com.m7.imkfsdk.chat.chatrow;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.adapter.MoorFastBtnHorizontalAdapter;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.QuickMenuViewHolder;
import com.m7.imkfsdk.utils.DensityUtil;
import com.m7.imkfsdk.view.SpaceItemDecoration;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.MoorFastBtnBean;

import java.util.ArrayList;

public class QuickMenuListRxRow extends BaseChatRow {
    public QuickMenuListRxRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, FromToMessage detail, int position) {
        QuickMenuViewHolder holder = (QuickMenuViewHolder) baseHolder;

        if (!TextUtils.isEmpty(detail.message)) {
            ArrayList<MoorFastBtnBean> fastList = new Gson().fromJson(detail.message, new TypeToken<ArrayList<MoorFastBtnBean>>() {
            }.getType());

            if (fastList != null && fastList.size() > 0) {

                MoorFastBtnHorizontalAdapter adapter = new MoorFastBtnHorizontalAdapter(context ,fastList);

                adapter.setOnItemClickListener(new MoorFastBtnHorizontalAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(View v, MoorFastBtnBean fastBtnBean) {
                        if(fastBtnBean!=null){
                            ((ChatActivity) context).handle_QuickItemClick(fastBtnBean);
                        }
                    }
                });

                RecyclerView rlRobotFlowlist = new RecyclerView(holder.getLl_fast().getContext());
                rlRobotFlowlist.setPadding(0, 0, DensityUtil.dp2px(5f), 0);
                rlRobotFlowlist.setLayoutManager(new LinearLayoutManager(holder.getLl_fast().getContext(), LinearLayoutManager.HORIZONTAL, false));
                rlRobotFlowlist.addItemDecoration(new SpaceItemDecoration(DensityUtil.dp2px(12f), 0));
                rlRobotFlowlist.setAdapter(adapter);

                holder.getLl_fast().removeAllViews();
                holder.getLl_fast().addView(rlRobotFlowlist);
            }

        }
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_row_fastbtn_recevied, null);
            QuickMenuViewHolder holder = new QuickMenuViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.QUICK_MENU_LIST.ordinal();
    }
}
