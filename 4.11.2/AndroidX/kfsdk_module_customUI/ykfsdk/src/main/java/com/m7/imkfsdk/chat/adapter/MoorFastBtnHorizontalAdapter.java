package com.m7.imkfsdk.chat.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m7.imkfsdk.R;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.model.entity.MoorFastBtnBean;

import java.util.ArrayList;


public class MoorFastBtnHorizontalAdapter extends RecyclerView.Adapter<MoorFastBtnHorizontalAdapter.FastViewHolder> {
    private final Context context;
    ArrayList<MoorFastBtnBean> datas;

    public MoorFastBtnHorizontalAdapter(Context mContext,ArrayList<MoorFastBtnBean> datas) {
        this.datas = datas;
        this.context=mContext;
    }


    @Override
    public FastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ykfsdk_item_fast_btn, parent, false);
        return new FastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FastViewHolder holder, final int position) {
        holder.tv_fast_text.setText(datas.get(position).getName());
        holder.sl_fast_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.OnItemClick(holder.sl_fast_btn, datas.get(holder.getAdapterPosition()));
                }
            }
        });
        if (IMChatManager.getInstance().getImageLoader() != null) {
            IMChatManager.getInstance().getImageLoader().loadImage(false, false, datas.get(position).getIcon(),
                    holder.iv_fast_btn, 0, 0, 0, null, null, null);
        } else {
            MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    static class FastViewHolder extends RecyclerView.ViewHolder {
        TextView tv_fast_text;
        RelativeLayout sl_fast_btn;
        ImageView iv_fast_btn;

        public FastViewHolder(View itemView) {
            super(itemView);
            sl_fast_btn = itemView.findViewById(R.id.sl_fast_btn);
            tv_fast_text = itemView.findViewById(R.id.tv_fast_text);
            iv_fast_btn=itemView.findViewById(R.id.iv_fast_btn);
        }
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void OnItemClick(View v, MoorFastBtnBean fastBtnBean);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
