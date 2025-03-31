package com.m7.imkfsdk.chat.chatrow;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.YKFVideoActivity;
import com.m7.imkfsdk.chat.adapter.FlowAdapter;
import com.m7.imkfsdk.chat.adapter.UselessAdapter;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.holder.TextViewHolder;
import com.m7.imkfsdk.chat.model.MoorImageInfoBean;
import com.m7.imkfsdk.utils.AnimatedGifDrawable;
import com.m7.imkfsdk.utils.AnimatedImageSpan;
import com.m7.imkfsdk.utils.DensityUtil;
import com.m7.imkfsdk.utils.FIleResourceUtil;
import com.m7.imkfsdk.utils.HtmlTagHandler;
import com.m7.imkfsdk.utils.ImageHeightCache;
import com.m7.imkfsdk.utils.RegexUtils;
import com.m7.imkfsdk.utils.ToastUtils;
import com.m7.imkfsdk.utils.faceutils.FaceConversionUtil;
import com.m7.imkfsdk.view.AutoLineFeedLayoutManager;
import com.m7.imkfsdk.view.NumClickBottomSheetDialog;
import com.m7.imkfsdk.view.PopupWindowList;
import com.m7.imkfsdk.view.imageviewer.MoorImagePreview;
import com.m7.imkfsdk.view.widget.PagerGridLayoutManager;
import com.m7.imkfsdk.view.widget.PagerGridSnapHelper;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.db.dao.MessageDao;
import com.moor.imkf.event.TransferAgent;
import com.moor.imkf.http.HttpManager;
import com.moor.imkf.lib.jsoup.Jsoup;
import com.moor.imkf.lib.jsoup.nodes.Document;
import com.moor.imkf.lib.jsoup.nodes.Element;
import com.moor.imkf.lib.jsoup.select.Elements;
import com.moor.imkf.lib.utils.MoorLogUtils;
import com.moor.imkf.listener.IMoorImageLoaderListener;
import com.moor.imkf.model.entity.ChatSessionBean;
import com.moor.imkf.model.entity.FlowBean;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.RobotInfoByObject;
import com.moor.imkf.model.entity.YKFChatStatusEnum;
import com.moor.imkf.utils.LogUtils;
import com.moor.imkf.utils.MoorUtils;
import com.moor.imkf.utils.NullUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextRxChatRow extends BaseChatRow {
    private final String MoorFontSizeTag = "moorFont";//用于转换标签支持字号
    private final String ActionrobotTransferAgent = "moor_moor_m7_actionrobotTransferAgent.m7_data:";
    private final String ActiondatapPhoneHref = "moor_moor_m7_actiondata-phone-href.m7-data-tel:";
    private final String ActionXbotQuickQuestion = "moor_moor_m7_actionXbotQuickQuestionData.m7_data:";
    private final String ActionChangeXbot = "moor_moor_m7_actionActionChangeXbot.m7_data:";


    private Context context;
    private PopupWindowList mPopupWindowList;
    private final int mRows = 4;
    private final int mColumns = 2;
    private PagerGridLayoutManager mLayoutManager;
    private FlowAdapter flowAdapter;
    private String taglistName = "";
    private boolean showUseless;
    private boolean showTargeList;


    public TextRxChatRow(int type) {
        super(type);
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu, View targetView, FromToMessage detail) {
        return false;
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ykfsdk_kf_chat_row_text_rx, null);
            TextViewHolder holder = new TextViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public int getChatViewType() {
        return ChatRowType.TEXT_ROW_RECEIVED.ordinal();
    }

    @Override
    protected void buildChattingData(final Context context, BaseHolder baseHolder, final FromToMessage detail, int position) {
        this.context = context;
        final TextViewHolder holder = (TextViewHolder) baseHolder;
        if (detail != null) {
            holder.getDescLinearLayout().removeAllViews();
            holder.getLl_flow_multi().setVisibility(View.GONE);
            if (detail.withDrawStatus) {//消息撤回
                holder.getWithdrawTextView().setVisibility(View.VISIBLE);
                holder.getContainer().setVisibility(View.GONE);
            } else {
                //是否需要展示推联按钮
                if (detail.contactPushed) {
                    setPushBtn(detail, holder);
                } else {
                    holder.tv_push_copy.setVisibility(View.GONE);
                    holder.tv_push_scan.setVisibility(View.GONE);
                }


                holder.getWithdrawTextView().setVisibility(View.GONE);
                holder.getContainer().setVisibility(View.VISIBLE);
                //xbot2 改造逻辑
                if (detail.flowTip != null && !"".equals(detail.flowTip)) {//是xbot2新的展示方式
                    //如果是xbot的滑动按钮UI
                    if ("button".equals(detail.flowType)) {
                        if (detail.flowMultiSelect && !detail.isFlowSelect) {
                            holder.getLl_flow_multi().setVisibility(View.VISIBLE);
                        } else {
                            holder.getLl_flow_multi().setVisibility(View.GONE);
                        }


                        holder.ll_flow.setVisibility(View.VISIBLE);
                        holder.chat_rl_robot.setVisibility(View.GONE);
                        holder.chat_rl_robot_result.setVisibility(View.GONE);
                        LogUtils.aTag("messageflowlist", detail.flowList);

                        if (detail.showHtml) {
                            initImgandText(detail.flowTip, holder);//添加文字和图片
                        }

                        if ("0".equals(detail.flowStyle) || TextUtils.isEmpty(detail.flowStyle)
                                || "null".equals(detail.flowStyle)) {
                            //双列按钮
                            Type token = new TypeToken<ArrayList<FlowBean>>() {
                            }.getType();
                            final ArrayList<FlowBean> flowBeanArrayList = new Gson().fromJson(detail.flowList, token);

                            //添加多选已经选择的
                            final HashMap<Integer, FlowBean> chooseHash = new HashMap<Integer, FlowBean>();
                            for (int i = 0; i < flowBeanArrayList.size(); i++) {
                                FlowBean bean = flowBeanArrayList.get(i);
                                if (bean.isChoose()) {
                                    chooseHash.put(i, bean);
                                }
                            }
                            holder.getTv_multi_count().setText(chooseHash.size() + "");
                            holder.getTv_multi_save().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (chooseHash.size() > 0) {
//                                        【文字】、【你好】、【一列】
                                        String s = "";
                                        for (FlowBean value : chooseHash.values()) {
                                            s = s + "【" + value.getButton() + "】、";
                                        }
                                        ChatActivity context1 = (ChatActivity) context;
                                        context1.sendXbotTextMsg(s);
                                        MessageDao.getInstance().updateFlowList(detail._id, detail.flowList);
                                        MessageDao.getInstance().updateFlowChoose(detail._id, true);
                                    } else {
                                        ToastUtils.showShort(context, R.string.ykfsdk_ykf_please_choosemulit);
                                    }
                                }
                            });


                            //动态设置整体高度，以及recyclerView的高度
                            if (flowBeanArrayList.size() < 7 && flowBeanArrayList.size() > 4) {//三行
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(150);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(4, mColumns, PagerGridLayoutManager.VERTICAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new
                                                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(95));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);

                            } else if (flowBeanArrayList.size() < 5 && flowBeanArrayList.size() > 2) {//两行
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(120);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(4, mColumns, PagerGridLayoutManager.VERTICAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                DensityUtil.dp2px(80));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            } else if (flowBeanArrayList.size() < 3 && flowBeanArrayList.size() > 0) {//一行
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(60);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(4, mColumns, PagerGridLayoutManager.VERTICAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            } else if (flowBeanArrayList.size() < 9 && flowBeanArrayList.size() > 6) {//四行
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(200);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(4, mColumns, PagerGridLayoutManager.VERTICAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(110));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            } else {
                                //正常的
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(236);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(mRows, mColumns, PagerGridLayoutManager.HORIZONTAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new RecyclerView.
                                                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(200));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            }


                            // 设置滚动辅助工具
                            PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
                            if (holder.mRecyclerView.getOnFlingListener() == null) {
                                pageSnapHelper.attachToRecyclerView(holder.mRecyclerView);
                            }

                            flowAdapter = new FlowAdapter(context, flowBeanArrayList, detail.flowMultiSelect, detail, new FlowAdapter.OnItemClickListenr() {
                                @Override
                                public void setOnButtonClickListenr(int position, boolean is, String msg) {
//                                Toast.makeText(context, "item" + msg + " 被点击了", Toast.LENGTH_SHORT).show();
                                    if (detail.flowMultiSelect) {
                                        if (position < flowBeanArrayList.size()) {

                                            flowBeanArrayList.get(position).setChoose(is);
                                            String json = new Gson().toJson(flowBeanArrayList);
                                            detail.flowList = json;
                                            if (is) {
                                                chooseHash.put(position, flowBeanArrayList.get(position));
                                            } else {
                                                chooseHash.remove(position);
                                            }
                                            holder.getTv_multi_count().setText(chooseHash.size() + "");
                                        }
                                    } else {
                                        // TODO: 2019/9/2 单选发送消息
                                        ChatActivity context1 = (ChatActivity) context;
                                        context1.sendXbotTextMsg(msg);
                                    }
                                }
                            });
                            LogUtils.aTag("flowlist", flowBeanArrayList.size());
                            holder.mRecyclerView.setAdapter(flowAdapter);
                            int flowSize = flowBeanArrayList.size() / 8;
                            if (flowBeanArrayList.size() % 8 > 0) {
                                flowSize = flowSize + 1;
                            }

                            holder.pointBottomView.setFillColor(context.getResources().getColor(R.color.ykfsdk_pointed));//选中的颜色
//                        holder.pointBottomView.setStrokeColor(R.color.pointno);//未选中的颜色
                            holder.pointBottomView.initData(flowSize, 0);


                            // 水平分页布局管理器
                            mLayoutManager.setPageListener(new PagerGridLayoutManager.PageListener() {
                                @Override
                                public void onPageSizeChanged(int pageSize) {
//                                    mTotal = pageSize;
                                }

                                @Override
                                public void onPageSelect(int pageIndex) {
//                                    mCurrent = pageIndex;
                                    holder.pointBottomView.setCurrentPage(pageIndex);

                                }
                            });


                        } else if ("1".equals(detail.flowStyle)) {
                            //单列按钮
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.mRecyclerView.getLayoutParams();
                            layoutParams.width = (DensityUtil.dp2px(220));
                            holder.mRecyclerView.setLayoutParams(layoutParams);

                            Type token = new TypeToken<ArrayList<FlowBean>>() {
                            }.getType();
                            final ArrayList<FlowBean> flowBeanArrayList = new Gson().fromJson(detail.flowList, token);
                            //添加多选已经选择的
                            final HashMap<Integer, FlowBean> chooseHash = new HashMap<Integer, FlowBean>();
                            for (int i = 0; i < flowBeanArrayList.size(); i++) {
                                FlowBean bean = flowBeanArrayList.get(i);
                                if (bean.isChoose()) {
                                    chooseHash.put(i, bean);
                                }
                            }
                            holder.getTv_multi_count().setText(chooseHash.size() + "");

                            holder.getTv_multi_save().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (chooseHash.size() > 0) {
//                                        【文字】、【你好】、【一列】
                                        String s = "";
                                        for (FlowBean value : chooseHash.values()) {
                                            s = s + "【" + value.getButton() + "】、";
                                        }
                                        ChatActivity context1 = (ChatActivity) context;
                                        context1.sendXbotTextMsg(s);
                                        MessageDao.getInstance().updateFlowList(detail._id, detail.flowList);
                                        MessageDao.getInstance().updateFlowChoose(detail._id, true);
                                    } else {
                                        ToastUtils.showShort(context, R.string.ykfsdk_ykf_please_choosemulit);
                                    }
                                }
                            });


                            //动态设置整体高度，以及recyclerView的高度
                            if (flowBeanArrayList.size() == 3) {//三行
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(150);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(4, 1, PagerGridLayoutManager.VERTICAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new
                                                RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                DensityUtil.dp2px(95));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            } else if (flowBeanArrayList.size() == 2) {//两行
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(120);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(4, 1, PagerGridLayoutManager.VERTICAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams
                                                (ViewGroup.LayoutParams.WRAP_CONTENT,
                                                        DensityUtil.dp2px(80));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            } else if (flowBeanArrayList.size() == 1) {//一行
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(60);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(1, 1, PagerGridLayoutManager.VERTICAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams
                                                (ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dp2px(45));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            } else if (flowBeanArrayList.size() == 4) {//四行
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(200);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(4, 1, PagerGridLayoutManager.VERTICAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams
                                                (ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dp2px(110));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            } else {
                                //正常的
                                ViewGroup.LayoutParams lps;
                                lps = holder.ll_flow.getLayoutParams();
                                lps.height = DensityUtil.dp2px(236);
                                holder.ll_flow.setLayoutParams(lps);
                                mLayoutManager = new PagerGridLayoutManager(mRows, 1, PagerGridLayoutManager.HORIZONTAL) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        //宽高均设置为100
                                        RecyclerView.LayoutParams lp = new RecyclerView.
                                                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dp2px(200));
                                        return lp;
                                    }
                                };
                                holder.mRecyclerView.setLayoutManager(mLayoutManager);
                            }


                            // 设置滚动辅助工具
                            PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
                            if (holder.mRecyclerView.getOnFlingListener() == null) {
                                pageSnapHelper.attachToRecyclerView(holder.mRecyclerView);
                            }

                            flowAdapter = new FlowAdapter(context, flowBeanArrayList, detail.flowMultiSelect, detail, new FlowAdapter.OnItemClickListenr() {
                                @Override
                                public void setOnButtonClickListenr(int position, boolean is, String msg) {
//                                Toast.makeText(context, "item" + msg + " 被点击了", Toast.LENGTH_SHORT).show();
                                    if (detail.flowMultiSelect) {
                                        if (position < flowBeanArrayList.size()) {
                                            flowBeanArrayList.get(position).setChoose(is);
                                            String json = new Gson().toJson(flowBeanArrayList);
                                            detail.flowList = json;
//                                            MessageDao.getInstance().updateFlowList(detail._id,detail.flowList);

                                            if (is) {
                                                chooseHash.put(position, flowBeanArrayList.get(position));
                                            } else {
                                                chooseHash.remove(position);
                                            }

                                            holder.getTv_multi_count().setText(chooseHash.size() + "");

                                        }
                                    } else {
                                        // TODO: 2019/9/2 单选发送消息
                                        ChatActivity context1 = (ChatActivity) context;
                                        context1.sendXbotTextMsg(msg);
//                                        MessageDao.getInstance().updateFlowChoose(detail._id,true);
                                    }
                                }
                            });
                            LogUtils.aTag("flowlist", flowBeanArrayList.size());
                            holder.mRecyclerView.setAdapter(flowAdapter);
                            int flowSize = flowBeanArrayList.size() / 4;
                            if (flowBeanArrayList.size() % 4 > 0) {
                                flowSize = flowSize + 1;
                            }

                            holder.pointBottomView.setFillColor(context.getResources().getColor(R.color.ykfsdk_pointed));//选中的颜色
//                        holder.pointBottomView.setStrokeColor(R.color.pointno);//未选中的颜色
                            holder.pointBottomView.initData(flowSize, 0);


                            // 水平分页布局管理器
                            mLayoutManager.setPageListener(new PagerGridLayoutManager.PageListener() {
                                @Override
                                public void onPageSizeChanged(int pageSize) {
//                                    mTotal = pageSize;
                                }

                                @Override
                                public void onPageSelect(int pageIndex) {
//                                    mCurrent = pageIndex;
                                    holder.pointBottomView.setCurrentPage(pageIndex);

                                }
                            });


                        } else if ("2".equals(detail.flowStyle)) {
                            //文字列表

                            //"flowType": "list",
                            holder.ll_flow.setVisibility(View.GONE);
                            holder.chat_rl_robot.setVisibility(View.GONE);
                            holder.chat_rl_robot_result.setVisibility(View.GONE);

                            // 添加可点击列表
                            Type token = new TypeToken<ArrayList<FlowBean>>() {
                            }.getType();
                            ArrayList<FlowBean> flowBeanArrayList = new Gson().fromJson(detail.flowList, token);
                            for (int i = 0; i < flowBeanArrayList.size(); i++) {
                                TextView xbotView = new TextView(context);
                                String msg = i + 1 + ". " + flowBeanArrayList.get(i).getButton();
                                SpannableString spannableString = new SpannableString(msg);
                                XbotClickSpan clickSpan = new XbotClickSpan(flowBeanArrayList.get(i).getText(), ((ChatActivity) context));
                                spannableString.setSpan(clickSpan, 0, msg.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                                spannableString.setSpan(colorSpan, 0, msg.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                xbotView.setText(spannableString);
                                xbotView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                xbotView.setMovementMethod(LinkMovementMethod.getInstance());

                                holder.getDescLinearLayout().addView(xbotView);
                            }

                        }
                    } else {
                        //"flowType": "list",
                        holder.ll_flow.setVisibility(View.GONE);
                        holder.chat_rl_robot.setVisibility(View.GONE);
                        holder.chat_rl_robot_result.setVisibility(View.GONE);


                        if (detail.showHtml) {
                            initImgandText(detail.flowTip, holder);//添加文字和图片
                        }

                        // 添加可点击列表
                        Type token = new TypeToken<ArrayList<FlowBean>>() {
                        }.getType();
                        ArrayList<FlowBean> flowBeanArrayList = new Gson().fromJson(detail.flowList, token);
                        for (int i = 0; i < flowBeanArrayList.size(); i++) {
                            TextView xbotView = new TextView(context);
                            String msg = i + 1 + ". " + flowBeanArrayList.get(i).getButton();
                            SpannableString spannableString = new SpannableString(msg);
                            XbotClickSpan clickSpan = new XbotClickSpan(flowBeanArrayList.get(i).getText(), ((ChatActivity) context));
                            spannableString.setSpan(clickSpan, 0, msg.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                            spannableString.setSpan(colorSpan, 0, msg.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            xbotView.setText(spannableString);
                            xbotView.setMovementMethod(LinkMovementMethod.getInstance());

                            holder.getDescLinearLayout().addView(xbotView);
                        }
                    }

                } else {
                    holder.ll_flow.setVisibility(View.GONE);
//                    detail.showHtml=true;
                    if (detail.showHtml) {
//                        final List<String> data = getImgStr(message.message);
//                        String content = message.message.replaceAll("<(img|IMG)(.*?)(/>|></img>|>)", "---");
//                        String[] strings = content.split("---");
                        parserImg(detail, holder);

                        if (detail.showHtml) {
                            if (!TextUtils.isEmpty(detail.quoteContent)) {
                                View inflate = View.inflate(holder.getDescLinearLayout().getContext(),
                                        R.layout.ykfsdk_kf_chat_row_text_quote_rx, null);
                                TextView recover_content_tv = inflate.findViewById(R.id.recover_content_tv);
                                String message = null;
                                try {
                                    message = URLDecoder.decode(detail.quoteContent, "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                List<String> data = getImgStr(message);
                                String content = message.replaceAll("<(img|IMG|video|VIDEO)(.*?)(/>|></img>|></video>|>)", "---");
                                String[] strings = content.split("---");

                                for (int i = 0; i < strings.length; i++) {
                                    recover_content_tv.setTextColor(context.getResources().getColor(R.color.ykfsdk_color_151515));
                                    recover_content_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                    recover_content_tv.setLineSpacing(0f, 1.2f);
                                    if (message.contains("</a>") && (!message.contains("1："))) {

                                        String actionmsg = setA_String(strings[i]);
                                        actionmsg = actionmsg.replaceAll("<font", "<" + MoorFontSizeTag);
                                        actionmsg = actionmsg.replaceAll("</font>", "</" + MoorFontSizeTag + ">");
                                        Spanned string;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            string = Html.fromHtml(actionmsg, Html.FROM_HTML_MODE_COMPACT, null, new HtmlTagHandler(MoorFontSizeTag));
                                        } else {
                                            string = Html.fromHtml(actionmsg, null, new HtmlTagHandler(MoorFontSizeTag));
                                        }

                                        Spanned string_o = new SpannableStringBuilder(MoorUtils.trimTrailingWhitespace(string));

                                        SpannableStringBuilder spannableString = new SpannableStringBuilder(string_o);
                                        spannableString = getClickableHtml(spannableString);

                                        boolean ismatcher = false;
                                        //匹配a标签 自定义 点击️
                                        Pattern patten1 = Pattern.compile("\\[([^\\]]*)\\]\\(([^\\)]*)\\)", Pattern.CASE_INSENSITIVE);
                                        Matcher matcher1 = patten1.matcher(spannableString);
                                        while (matcher1.find()) {
                                            ismatcher = true;
                                            String m7_data = matcher1.group(1);
                                            String m7_name = matcher1.group(2);
                                            if (checkURL(m7_name)) {
                                                spannableString.replace(matcher1.start(), matcher1.end(), m7_data);
                                                AClickApan clickSpan = new AClickApan(m7_name);
                                                spannableString.setSpan(clickSpan, matcher1.start(), matcher1.start() + m7_data.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                                ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                                                spannableString.setSpan(colorSpan, matcher1.start(), matcher1.start() + m7_data.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                                matcher1 = patten1.matcher(spannableString);
                                            }
                                        }
                                        if (!ismatcher) {
                                            //匹配电话
                                            String regex = RegexUtils.isPhoneRegexp();
                                            Pattern patten3 = Pattern.
                                                    compile(regex,
                                                            Pattern.CASE_INSENSITIVE);
                                            Matcher matcher3 = patten3.matcher(spannableString);
                                            while (matcher3.find()) {
                                                String number = matcher3.group();
                                                int end = matcher3.start() + number.length();
                                                NumClickSpan clickSpan = new NumClickSpan(number);
                                                spannableString.setSpan(clickSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                                ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                                                spannableString.setSpan(colorSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                            }
                                        }
                                        recover_content_tv.setAutoLinkMask(Linkify.ALL);
                                        recover_content_tv.setText(spannableString);
                                        recover_content_tv.setLinkTextColor(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                                        recover_content_tv.setMovementMethod(LinkMovementMethod.getInstance());
                                    } else {
                                        List<AString> list = setAString(strings[i]);
                                        setNoImgView(recover_content_tv, strings[i], list);
                                    }
                                }
                                recover_content_tv.setMovementMethod(LinkMovementMethod.getInstance());
                                holder.getDescLinearLayout().addView(inflate);
                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) inflate.getLayoutParams();
                                layoutParams.bottomMargin = 20;
                                try {
                                    String sendContent=" ";
                                    if(!TextUtils.isEmpty(detail.sendContent)){
                                        sendContent=detail.sendContent;
                                    }
                                    initImgandText(URLDecoder.decode(sendContent, "utf-8"), holder);//添加文字和图片
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                initImgandText(detail.message, holder);//添加文字和图片
                            }
                        }


                        if (!"".equals(NullUtil.checkNull(detail.questionId))) {
                            holder.chat_rl_robot.setVisibility(View.VISIBLE);
                            if ("useful".equals(NullUtil.checkNull(detail.robotPingjia))) {
                                holder.chat_iv_robot_useful.setImageResource(R.drawable.ykfsdk_robot_useful_blue);
                                holder.chat_tv_robot_useful.setTextColor(context.getResources().getColor(R.color.ykfsdk_all_white));
                                holder.chat_iv_robot_useless.setImageResource(R.drawable.ykfsdk_kf_robot_useless_grey);
                                holder.chat_tv_robot_useless.setTextColor(context.getResources().getColor(R.color.ykfsdk_ykf_help_unno));
                                holder.chat_ll_robot_useful.setOnClickListener(null);
                                holder.chat_ll_robot_useless.setOnClickListener(null);
                                holder.chat_rl_robot_result.setVisibility(View.VISIBLE);
                                holder.useless_ll.setVisibility(View.VISIBLE);
                                holder.no_user_tips_rv.setVisibility(View.GONE);
                                holder.line1.setVisibility(View.GONE);
                                holder.line2.setVisibility(View.GONE);
                                holder.useless_et.setVisibility(View.GONE);
                                holder.submit_tv.setVisibility(View.GONE);
                                holder.remain_tv.setVisibility(View.GONE);
                                if (TextUtils.isEmpty(detail.fingerUp)) {
                                    holder.chat_tv_robot_result.setVisibility(View.VISIBLE);
                                    holder.chat_tv_robot_result.setText(R.string.ykfsdk_thinks_01);
                                } else {
                                    holder.chat_tv_robot_result.setVisibility(View.VISIBLE);
                                    holder.chat_tv_robot_result.setText(detail.fingerUp);
                                }


                            } else if ("useless".equals(NullUtil.checkNull(detail.robotPingjia))) {
                                //下面这些逻辑会走两次，进来的时候走一次，然后点击了无帮助或者有帮助按钮又会走一次
                                // 主要的判断分为，点踩之后既有标签又有点踩框的，只有标签的，只有点踩框的。
                                //  这里是需要新加的逻辑。
                                holder.line2.setVisibility(View.VISIBLE);
                                if (detail.alreadSubmit) {   // 这个是走历史的判断
                                    holder.useless_ll.setVisibility(View.VISIBLE);
                                    holder.remain_tv.setVisibility(View.VISIBLE);
                                    holder.getNoUserTipsRv().setVisibility(View.VISIBLE);
                                    UselessAdapter uselessAdapter = new UselessAdapter();
                                    AutoLineFeedLayoutManager layoutManager = new AutoLineFeedLayoutManager();
                                    holder.getNoUserTipsRv().setLayoutManager(layoutManager);
                                    uselessAdapter.setAllTagList(detail.allTaglist);
                                    uselessAdapter.setSelectTagList(detail.taglist);
                                    uselessAdapter.selectData();
                                    uselessAdapter.showHistory(true);
                                    holder.getNoUserTipsRv().setAdapter(uselessAdapter);
                                } else if (TextUtils.isEmpty(detail.taglist) && !TextUtils.isEmpty(detail.allTaglist)) {  // 这种情况事还没选，又发了一条数据的情况 ，或者是已经提交但是一个都没选
                                    unSelect(holder, detail, context);
                                    observerEd(holder, detail);
                                    submitUnless((ChatActivity) context, detail, holder);
                                }

                                // 这个也是历史的字段。
                                if (detail.enable_remarks == 1) {
                                    holder.getUselessEt().setClickable(false);
                                    holder.getUselessEt().setFocusableInTouchMode(true);
                                    holder.useless_ll.setVisibility(View.VISIBLE);
                                    // showTargeList 表示当前点击了无帮助，然后这里需要显示出来
                                    if (showTargeList) {
                                        holder.getNoUserTipsRv().setVisibility(View.VISIBLE);
                                        // 下面这个逻辑就是存粹展示历史的数据
                                    } else if (TextUtils.isEmpty(detail.taglist)) {
                                        holder.getNoUserTipsRv().setVisibility(View.GONE);
                                    }
                                    holder.getUselessEt().setVisibility(View.VISIBLE);
                                    holder.getUselessEt().setHint(detail.remarksHit);
                                    holder.getSubmitTv().setClickable(true);
                                    holder.remain_tv.setVisibility(View.VISIBLE);
                                    if (!TextUtils.isEmpty(detail.remarks)) {
                                        holder.getUselessEt().setText(detail.remarks);
                                        holder.getUselessEt().setClickable(false);
                                        holder.remain_tv.setText(detail.remarks_text_num);
                                    }
                                } else {
                                    holder.getLin1().setVisibility(View.GONE);
                                    holder.getUselessEt().setVisibility(View.GONE);
                                    holder.remain_tv.setVisibility(View.GONE);
                                }
                                if (detail.enable_taglist != 1 && detail.enable_remarks != 1) {
                                    holder.submit_tv.setVisibility(View.GONE);
                                    holder.chat_rl_robot_result.setVisibility(View.VISIBLE);
                                    holder.useless_ll.setVisibility(View.VISIBLE);
                                    holder.getNoUserTipsRv().setVisibility(View.GONE);
                                    if (TextUtils.isEmpty(detail.fingerDown)) {
                                        holder.chat_tv_robot_result.setText(R.string.ykfsdk_thinks_02);
                                    } else {
                                        holder.chat_tv_robot_result.setText(detail.fingerDown);
                                    }
                                }

                                holder.chat_iv_robot_useful.setImageResource(R.drawable.ykfsdk_kf_robot_useful_grey);
                                holder.chat_tv_robot_useful.setTextColor(context.getResources().getColor(R.color.ykfsdk_ykf_help_unyes));
                                holder.chat_iv_robot_useless.setImageResource(R.drawable.ykfsdk_kf_robot_useless_blue);
                                holder.chat_tv_robot_useless.setTextColor(context.getResources().getColor(R.color.ykfsdk_all_white));
                                holder.chat_ll_robot_useful.setOnClickListener(null);
                                holder.chat_ll_robot_useless.setOnClickListener(null);
                                if (!showUseless) {
                                    holder.chat_rl_robot_result.setVisibility(View.VISIBLE);
                                    if (TextUtils.isEmpty(detail.fingerDown)) {
                                        holder.chat_tv_robot_result.setText(R.string.ykfsdk_thinks_02);
                                    } else {
                                        holder.chat_tv_robot_result.setText(detail.fingerDown);
                                    }
                                }
                                // 这里原来的逻辑是选了 标签和 原因才能提交
//                                if (detail.taglist == null && TextUtils.isEmpty(detail.remarks)) {
//                                    holder.getSubmitTv().setVisibility(View.GONE);
//                                    if (showUseless) {
//                                        holder.getSubmitTv().setVisibility(View.VISIBLE);
//                                        holder.getSubmitTv().setText(holder.getSubmitTv().getResources().getString(R.string.ykfsdk_ykf_submit));
//                                    }
//                                } else {
//                                    holder.getSubmitTv().setVisibility(View.VISIBLE);
//                                    holder.getSubmitTv().setText(holder.getSubmitTv().getResources().getString(R.string.ykfsdk_ykf_already_submit));
//                                    if (showUseless) {
//                                        holder.getSubmitTv().setText(holder.getSubmitTv().getResources().getString(R.string.ykfsdk_ykf_submit));
//                                    }
//                                }
                                if (detail.alreadSubmit) {
                                    holder.chat_rl_robot_result.setVisibility(View.VISIBLE);
                                    if (TextUtils.isEmpty(detail.fingerDown)) {
                                        holder.chat_tv_robot_result.setText(R.string.ykfsdk_thinks_02);
                                    } else {
                                        holder.chat_tv_robot_result.setText(detail.fingerDown);
                                    }
                                    holder.getSubmitTv().setText(holder.getSubmitTv().getResources().getString(R.string.ykfsdk_ykf_already_submit));
                                    holder.getUselessEt().setClickable(false);
                                    holder.getUselessEt().setFocusableInTouchMode(false);
                                } else {
                                    holder.getSubmitTv().setText(holder.getSubmitTv().getResources().getString(R.string.ykfsdk_ykf_submit));
                                    holder.getSubmitTv().setClickable(true);
                                }
                            } else {
                                holder.line2.setVisibility(View.VISIBLE);
                                holder.chat_iv_robot_useful.setImageResource(R.drawable.ykfsdk_kf_robot_useful_grey);
                                holder.chat_tv_robot_useful.setTextColor(context.getResources().getColor(R.color.ykfsdk_ykf_help_unyes));
                                holder.chat_iv_robot_useless.setImageResource(R.drawable.ykfsdk_kf_robot_useless_grey);
                                holder.chat_tv_robot_useless.setTextColor(context.getResources().getColor(R.color.ykfsdk_ykf_help_unno));
                                holder.chat_rl_robot_result.setVisibility(View.GONE);
                                holder.chat_ll_robot_useful.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        detail.robotPingjia = "useful";
                                        MessageDao.getInstance().updateMsgToDao(detail);
//                                        ((ChatActivity) context).updateMessage();
                                        ((ChatActivity) context).getChatAdapter().notifyDataSetChanged();
                                        if (!"".equals(NullUtil.checkNull(detail.questionId))) {
                                            if ("xbot".equals(NullUtil.checkNull(detail.robotType))) {//如果是xbot机器人
                                                IMChatManager.getInstance().sendRobotCsr(NullUtil.checkNull(detail.questionId),
                                                        NullUtil.checkNull(detail.std_question),
                                                        NullUtil.checkNull(detail.robotType), NullUtil.checkNull(detail.robotId), "1",
                                                        NullUtil.checkNull(detail.sid), NullUtil.checkNull(detail.ori_question),
                                                        NullUtil.checkNull(detail.std_question), NullUtil.checkNull(detail.message),
                                                        NullUtil.checkNull(detail.confidence), NullUtil.checkNull(detail.sessionId), "", "");
                                            } else {
                                                IMChatManager.getInstance().sendRobotCsr(NullUtil.checkNull(detail.questionId), NullUtil.checkNull(detail.robotType), NullUtil.checkNull(detail.robotId), NullUtil.checkNull(detail.robotMsgId), "useful");
                                            }
                                        }
                                    }
                                });
                                holder.useless_ll.setVisibility(View.GONE);
                                //没有帮助
                                holder.chat_ll_robot_useless.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                unSelect(holder, detail, context);
                                                //  ((ChatActivity) context).updateMessage();
                                                ((ChatActivity) context).getChatAdapter().notifyDataSetChanged();
                                            }
                                        });
                                observerEd(holder, detail);
                                submitUnless((ChatActivity) context, detail, holder);
                            }
                        } else {
                            holder.chat_rl_robot.setVisibility(View.GONE);
                            holder.chat_rl_robot_result.setVisibility(View.GONE);
                            holder.useless_ll.setVisibility(View.GONE);
                        }

                    } else {
                        holder.useless_ll.setVisibility(View.GONE);  // 隐藏点踩标签和自定义点踩内容。
                        holder.chat_rl_robot.setVisibility(View.GONE);
                        holder.chat_rl_robot_result.setVisibility(View.GONE);  //  todo 2023.7.31
                        TextView textView = new TextView(context);
                        textView.setTextColor(context.getResources().getColor(R.color.ykfsdk_color_151515));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        textView.setLineSpacing(0f, 1.1f);
                        SpannableStringBuilder content = handler(textView,
                                detail.message);

                        String actionmsg = setA_String(content.toString());
                        SpannableStringBuilder spannableString = FaceConversionUtil.getInstace()
                                .getExpressionString(context, actionmsg + "", textView);

                        //        //匹配a标签 自定义 点击️
                        Pattern patten1 = Pattern.compile("\\[([^\\]]*)\\]\\(([^\\)]*)\\)", Pattern.CASE_INSENSITIVE);
                        Matcher matcher1 = patten1.matcher(spannableString);
                        while (matcher1.find()) {
                            String m7_data = matcher1.group(1);
                            String m7_name = matcher1.group(2);
                            if (checkURL(m7_name)) {
                                spannableString.replace(matcher1.start(), matcher1.end(), m7_data);
                                AClickApan clickSpan = new AClickApan(m7_name);
                                spannableString.setSpan(clickSpan, matcher1.start(), matcher1.start() + m7_data.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                                spannableString.setSpan(colorSpan, matcher1.start(), matcher1.start() + m7_data.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                matcher1 = patten1.matcher(spannableString);
                            }
                        }

                        Pattern patten2 = Pattern.
                                compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(((http[s]{0,1}|ftp)://|)((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)", Pattern.CASE_INSENSITIVE);
                        Matcher matcher2 = patten2.matcher(spannableString);
                        while (matcher2.find()) {

                            String number = matcher2.group();
                            int end = matcher2.start() + number.length();
                            HttpClickSpan clickSpan = new HttpClickSpan(number);
                            spannableString.setSpan(clickSpan, matcher2.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                            spannableString.setSpan(colorSpan, matcher2.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }


                        String regex = RegexUtils.isPhoneRegexp();
                        Pattern patten3 = Pattern.
                                compile(regex,
                                        Pattern.CASE_INSENSITIVE);
                        Matcher matcher3 = patten3.matcher(spannableString);
                        while (matcher3.find()) {
                            String number = matcher3.group();
                            int end = matcher3.start() + number.length();
                            NumClickSpan clickSpan = new NumClickSpan(number);
                            spannableString.setSpan(clickSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                            spannableString.setSpan(colorSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }

                        textView.setText(spannableString);
                        textView.setMovementMethod(LinkMovementMethod.getInstance());

                        holder.getDescLinearLayout().addView(textView);
                        textView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                showPopWindows(view, detail.message);
                                return true;
                            }
                        });
                    }

                }

            }
        }
    }


    private void observerEd(final TextViewHolder holder, final FromToMessage detail) {
        if (holder.useless_et.getVisibility() == View.VISIBLE) {
            holder.remain_tv.setVisibility(View.VISIBLE);
            final int[] editStart = new int[1];
            final int[] editEnd = new int[1];
            holder.useless_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    holder.remain_tv.setText(s.length() + "/400");
                    editStart[0] = holder.useless_et.getSelectionStart();
                    editEnd[0] = holder.useless_et.getSelectionEnd();
                    if (holder.useless_et.length() > 400) {
                        s.delete(editStart[0] - 1, editEnd[0]);
                        int tempSelection = editStart[0];
                        holder.useless_et.setText(s);
                        //suggest_ed.setSelection(tempSelection);
                        //光标放在末尾
                        holder.useless_et.setSelection(holder.useless_et.getText().length());
                    }
                    detail.remarks_text_num = holder.remain_tv.getText().toString();
                }
            });
        } else {
            holder.remain_tv.setVisibility(View.GONE);
        }
    }

    private void submitUnless(final ChatActivity context, final FromToMessage detail, final TextViewHolder holder) {
        holder.getSubmitTv().setClickable(true);
        holder.getSubmitTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detail.alreadSubmit) {
                    return;
                }
                showUseless = false;
                showTargeList = false;
                if (!"".equals(NullUtil.checkNull(detail.questionId))) {
                    if ("xbot".equals(NullUtil.checkNull(detail.robotType))) {
                        String tagDes = "";
                        if (taglistName.contains("##") && taglistName.split("##").length > 1) {
                            tagDes = taglistName;
                        } else if (taglistName.contains("##") && taglistName.split("##").length == 1) {
                            tagDes = taglistName.split("##")[0];
                        } else if (!tagDes.contains("##")) {
                            tagDes = taglistName;
                        }

                        String remark = holder.useless_et.getText().toString();
                        detail.taglist = tagDes;
                        detail.remarks = remark;
                        ChatSessionBean bean = IMChatManager.getInstance().getChatSession();
                        if (bean != null) {
                            if (bean.taglist != null) {
                                if (bean.taglist.size() > 0) {
                                    StringBuffer sb = new StringBuffer();
                                    for (String s : bean.taglist) {
                                        sb.append(s + "##");  //  todo  这里会报错
                                    }
                                    detail.allTaglist = sb.toString();
                                }
                            }
                        }
                        //如果是xbot机器人
                        IMChatManager.getInstance().sendRobotCsr(NullUtil.checkNull(detail.questionId),
                                NullUtil.checkNull(detail.std_question),
                                NullUtil.checkNull(detail.robotType), NullUtil.checkNull(detail.robotId), "0",
                                NullUtil.checkNull(detail.sid), NullUtil.checkNull(detail.ori_question),
                                NullUtil.checkNull(detail.std_question), NullUtil.checkNull(detail.message),
                                NullUtil.checkNull(detail.confidence), NullUtil.checkNull(detail.sessionId), holder.useless_et.getText().toString(), tagDes);
                    } else {//小七或者小莫
                        IMChatManager.getInstance().sendRobotCsr(NullUtil.checkNull(detail.questionId), NullUtil.checkNull(detail.robotType), NullUtil.checkNull(detail.robotId), NullUtil.checkNull(detail.robotMsgId), "useless");
                    }
                    detail.alreadSubmit = true;
                }
                taglistName = "";
                holder.chat_rl_robot_result.setVisibility(View.GONE);
                MessageDao.getInstance().updateMsgToDao(detail);
                //  context.updateMessage();
                ((ChatActivity) context).getChatAdapter().notifyDataSetChanged();
            }
        });
    }

    private void unSelect(final TextViewHolder holder, final FromToMessage detail, final Context context) {
        showUseless = true;
        holder.chat_rl_robot_result.setVisibility(View.GONE);
        holder.useless_ll.setVisibility(View.VISIBLE);
        UselessAdapter uselessAdapter = new UselessAdapter();
        ChatSessionBean bean = IMChatManager.getInstance().getChatSession();
        if (bean != null) {
            if (bean.enable_taglist == 1) {
                holder.getSubmitTv().setVisibility(View.VISIBLE);
                uselessAdapter.setDatas((ArrayList<String>) bean.taglist);
                showTargeList = true;
                // enable_multiple_choice：点踩标签是否允许多选 0关闭，1开启 默认0
                //enable_taglist：点踩标签是否开启 0关闭，1开启 默认0
                //remarks：点踩自定义原因提示语 默认空字符串
                //enable_remarks：点踩自定义原因是否开启 0关闭，1开启 默认0
                uselessAdapter.setMultiple_choice(bean.enable_multiple_choice);
                holder.getNoUserTipsRv().setVisibility(View.VISIBLE);
            } else {
                holder.getNoUserTipsRv().setVisibility(View.GONE);
            }
            if (bean.enable_remarks == 1) {
                holder.getUselessEt().setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(holder.getUselessEt().getText().toString())) {
                    holder.getUselessEt().setText("");
                }
                holder.useless_ll.setVisibility(View.VISIBLE);
                holder.getSubmitTv().setVisibility(View.VISIBLE);
                holder.getUselessEt().setHint(bean.remarks);
                holder.remain_tv.setVisibility(View.VISIBLE);
                holder.getLin1().setVisibility(View.VISIBLE);
            } else {
//                                                holder.getSubmitTv().setVisibility(View.GONE);
                holder.getLin1().setVisibility(View.GONE);
            }
        } else {
            holder.useless_ll.setVisibility(View.GONE);
        }

        AutoLineFeedLayoutManager layoutManager = new AutoLineFeedLayoutManager();
        holder.getNoUserTipsRv().setLayoutManager(layoutManager);
        holder.getNoUserTipsRv().setAdapter(uselessAdapter);
        uselessAdapter.setOnItemClickListener(new UselessAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int postion, String name) {
                taglistName = name;
            }
        });
        detail.robotPingjia = "useless";
        if (bean != null) {
            if (bean.enable_remarks == 1) {   // 上面的逻辑会二次刷新，所以这里需要赋值自定义的内容。
                detail.remarksHit = bean.remarks;  // 这里是自定义的提示语
                detail.enable_remarks = bean.enable_remarks;
            }
            if (bean.taglist != null) {
                if (bean.taglist.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (String s : bean.taglist) {
                        sb.append(s + "##");  //  todo  这里会报错
                    }
                    detail.allTaglist = sb.toString();
                }
            }
            detail.enable_taglist = bean.enable_taglist;
        }
        detail.taglist = "";
        MessageDao.getInstance().updateMsgToDao(detail);
        //   ((ChatActivity) context).updateMessage();
    }

    // todo 点无帮助  需求。
    public void setNoUserRecyclerViewParams(TextViewHolder holder, final float mHeight) {
        mLayoutManager = new PagerGridLayoutManager(4, mColumns, PagerGridLayoutManager.VERTICAL) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                //宽高均设置为100
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(mHeight));
                return lp;
            }
        };
        holder.no_user_tips_rv.setLayoutManager(mLayoutManager);
    }

    private void setNoImgView(TextView tv, String message, List<AString> list) {
        String msg = message.replaceAll("\\n", "\n");
        for (int i = 0; i < list.size(); i++) {
            AString aString = list.get(i);

            Document doc = Jsoup.parse(aString.a);
            Elements chatElements = doc.getElementsByTag("a");
            if (chatElements != null) {
                if (chatElements.size() > 0) {
                    for (int p = 0; p < chatElements.size(); p++) {
                        if (chatElements.get(p) != null) {
                            Element chatElement = chatElements.get(p);
                            aString.action = chatElement.attr("m7_action");
                            if (!TextUtils.isEmpty(aString.action)) {
                                if ("robotTransferAgent".equals(aString.action) | "transferAgent".equals(aString.action)) {
                                    //m7_action为 transferAgent 响应转人工
                                    //m7_action为 transferAgent 响应转人工，m7_data 取出技能组id
                                    aString.peerid = chatElement.attr("m7_data");
                                    msg = msg.replace(aString.a,
                                            "[" + aString.content + "](" + ActionrobotTransferAgent + aString.peerid + ".com)");
                                    aString.content = "------------___---------------";
                                } else if ("xbot-quick-question".equals(aString.action)) {
                                    //m7_action为 xbot-quick-question 是xbot提问内容，m7_data中是时机要发送的文案
                                    aString.xbotQuickQuestionData = chatElement.attr("m7_data");
                                    msg = msg.replace(aString.a,
                                            "[" + aString.content + "](" + ActionXbotQuickQuestion + aString.xbotQuickQuestionData + ".com)");
                                    aString.content = "------------___---------------";
                                } else if ("xbotTransferRobot".equals(aString.action)) {
                                    //m7_action为 xbotTransferRobot 是切换机器人，m7_data中是要切换的机器人ID
                                    aString.changeRobotId = chatElement.attr("m7_data");
                                    msg = msg.replace(aString.a,
                                            "[" + aString.content + "](" + ActionChangeXbot + aString.changeRobotId + ".com)");
                                }
                            }

                            //data-phone href 并且值为tel: 开头 有值为电话号
                            if (TextUtils.isEmpty(aString.action)) {
                                aString.action = chatElement.attr("href");
                                if (!TextUtils.isEmpty(aString.action)) {
                                    if (aString.action.startsWith("tel:")) {
                                        msg = msg.replace(aString.a,
                                                "[" + aString.content + "](" + ActiondatapPhoneHref + aString.content + ".com)");
                                        aString.content = "------------___---------------";
                                    } else {
                                        msg = msg.replaceAll(aString.a, aString.content);
                                    }
                                } else {
                                    msg = msg.replaceAll(aString.a, aString.content);
                                }
                            }
                        } else {

                        }
                    }
                }
            }
        }


//        msg = msg.replaceAll("<p>", "");
//        msg = msg.replaceAll("</p>", "\n");

//        msg = msg.replaceAll("<p .*?>", "\r\n");
//        // <br><br/>替换为换行
//        msg = msg.replaceAll("<br\\s*/?>", "\r\n");
//        // 去掉其它的<>之间的东西
//        msg = msg.replaceAll("\\<.*?>", "");
//        msg = MoorUtils.trimTrailingWhitespace(msg).toString();

        //由于Html.fromHtml不支持/n 换行符，将其替换为<br />
        msg = msg.replaceAll("\n", "<br />");

        msg = msg.replaceAll("<font", "<" + MoorFontSizeTag);
        msg = msg.replaceAll("</font>", "</" + MoorFontSizeTag + ">");

        Spanned string;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            string = Html.fromHtml(msg, Html.FROM_HTML_MODE_COMPACT, null, new HtmlTagHandler(MoorFontSizeTag));
        } else {
            string = Html.fromHtml(msg, null, new HtmlTagHandler(MoorFontSizeTag));
        }
        SpannableStringBuilder spannableString = new SpannableStringBuilder(MoorUtils.trimTrailingWhitespace(string));
        spannableString = getClickableHtml(spannableString);


//        SpannableStringBuilder spannableString = new SpannableStringBuilder(msg);
//        Pattern patten = Pattern.compile("\\d+[：].*+\\n", Pattern.CASE_INSENSITIVE);

        Pattern patten = Pattern.compile("\\d+[：].*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String number = matcher.group();
            int end = matcher.start() + number.length();
            RobotClickSpan clickSpan = new RobotClickSpan(number, ((ChatActivity) context));
            spannableString.setSpan(clickSpan, matcher.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
            spannableString.setSpan(colorSpan, matcher.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        Pattern patten2 = Pattern.compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(((http[s]{0,1}|ftp)://|)((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher2 = patten2.matcher(spannableString);
        while (matcher2.find()) {
            String number = matcher2.group();
            int end = matcher2.start() + number.length();
            HttpClickSpan clickSpan = new HttpClickSpan(number);
            spannableString.setSpan(clickSpan, matcher2.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
            spannableString.setSpan(colorSpan, matcher2.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }


        for (int i = 0; i < list.size(); i++) {
            AString aString = list.get(i);
            Pattern patten3 = Pattern.compile(aString.content, Pattern.CASE_INSENSITIVE);
            Matcher matcher3 = patten3.matcher(spannableString);
            while (matcher3.find()) {
                String number = matcher3.group();
                int end = matcher3.start() + number.length();
                HttpClickSpan clickSpan = new HttpClickSpan(aString.url);
                spannableString.setSpan(clickSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                spannableString.setSpan(colorSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }


        String regex = RegexUtils.isPhoneRegexp();
        Pattern patten3 = Pattern.
                compile(regex,
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher3 = patten3.matcher(spannableString);
        while (matcher3.find()) {
            String number = matcher3.group();
            int end = matcher3.start() + number.length();
            NumClickSpan clickSpan = new NumClickSpan(number);
            spannableString.setSpan(clickSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
            spannableString.setSpan(colorSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        //匹配a标签 自定义 点击️
        Pattern patten1 = Pattern.compile("\\[([^\\]]*)\\]\\(([^\\)]*)\\)", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = patten1.matcher(spannableString);
        while (matcher1.find()) {
            String m7_data = matcher1.group(1);
            String m7_name = matcher1.group(2);
            if (checkURL(m7_name)) {
                spannableString.replace(matcher1.start(), matcher1.end(), m7_data);
                AClickApan clickSpan = new AClickApan(m7_name);
                spannableString.setSpan(clickSpan, matcher1.start(), matcher1.start() + m7_data.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                spannableString.setSpan(colorSpan, matcher1.start(), matcher1.start() + m7_data.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                matcher1 = patten1.matcher(spannableString);
            }
        }


        tv.setText(spannableString);
        tv.setMovementMethod(LinkMovementMethod.getInstance());

    }


    private SpannableStringBuilder handler(final TextView gifTextView, String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring(
                        "#[face/png/f_static_".length(), tempText.length()
                                - ".png]#".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif 否则说明gif找不到，则显示png
                 * */
                InputStream is = context.getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,
                                new AnimatedGifDrawable.UpdateListener() {
                                    @Override
                                    public void update() {
                                        gifTextView.postInvalidate();
                                    }
                                })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("#[".length(),
                        tempText.length() - "]#".length());
                try {
                    sb.setSpan(
                            new ImageSpan(context,
                                    BitmapFactory.decodeStream(context
                                            .getAssets().open(png))),
                            m.start(), m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return sb;
    }


    /**
     * 机器人问题列表点击
     */
    class RobotClickSpan extends ClickableSpan {
        String msg;
        ChatActivity chatActivity;

        public RobotClickSpan(String msg, ChatActivity chatActivity) {
            this.msg = msg;
            this.chatActivity = chatActivity;
        }

        @Override
        public void onClick(View view) {
            String msgStr = "";
            try {
                msgStr = msg.split("：", 2)[1].trim();

            } catch (Exception e) {
            }
            chatActivity.sendXbotTextMsg(msgStr);

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }
    }

    /**
     * xbot flow list按钮点击
     */
    class XbotClickSpan extends ClickableSpan {
        String msg;
        ChatActivity chatActivity;

        public XbotClickSpan(String msg, ChatActivity chatActivity) {
            this.msg = msg;
            this.chatActivity = chatActivity;
        }

        @Override
        public void onClick(View view) {
            chatActivity.sendXbotTextMsg(msg);

        }
    }

    class NumClickSpan extends ClickableSpan {
        String msg;

        public NumClickSpan(String msg) {
            this.msg = msg;
        }

        @Override
        public void onClick(View widget) {
            NumClickBottomSheetDialog dialog = NumClickBottomSheetDialog.instance(RegexUtils.regexNumber(msg));
            dialog.show(((ChatActivity) context).getSupportFragmentManager(), "");
        }
    }

    //A标签点击事件
    class AClickApan extends ClickableSpan {
        String msg;

        public AClickApan(String msg) {
            this.msg = msg;
        }

        @Override
        public void onClick(View widget) {
            String action_str = msg + "";
            if (!TextUtils.isEmpty(action_str)) {
                if (action_str.startsWith(ActionrobotTransferAgent)) {

                    if (IMChatManager.getInstance().getYkfChatStatusEnum() != YKFChatStatusEnum.KF_Robot_Status) {
                        //如果不是机器人状态下 不在执行
                        ToastUtils.showShort(context, R.string.ykfsdk_no_robot);
                        return;
                    }

                    //第一种情况 转人工
                    //moor_moor_m7_actionrobotTransferAgent.m7_data:"+技能组id+".com
                    //以 ActionrobotTransferAgent 为开头 取技能组id 转人工。
                    action_str = action_str.replace(ActionrobotTransferAgent, "");
                    action_str = action_str.replace(".com", "");
                    TransferAgent transferAgent = new TransferAgent();
                    transferAgent.peerid = action_str;
                    transferAgent.type = "13";
                    EventBus.getDefault().post(transferAgent);
                }
                if (action_str.startsWith(ActiondatapPhoneHref)) {
                    //第二种情况 电话号
                    //moor_moor_m7_actiondata-phone-href.m7-data-tel:电话号.com
                    //以 ActiondatapPhoneHref 为开头 电话号 弹窗。
                    action_str = action_str.replace(ActiondatapPhoneHref, "");
                    action_str = action_str.replace(".com", "");
                    NumClickBottomSheetDialog dialog = NumClickBottomSheetDialog.instance(RegexUtils.regexNumber(action_str));
                    dialog.show(((ChatActivity) context).getSupportFragmentManager(), "");
                }

                if (action_str.startsWith(ActionXbotQuickQuestion)) {
                    //第三种情况 发送xbot问题
                    //moor_moor_m7_actionXbotQuickQuestionData.m7_data:"+问题+".com
                    //以 ActionXbotQuickQuestion 开头取出中间文案作为消息发送
                    action_str = action_str.replace(ActionXbotQuickQuestion, "");
                    action_str = action_str.replace(".com", "");
                    if (!TextUtils.isEmpty(action_str)) {
                        ChatActivity context1 = (ChatActivity) context;
                        context1.sendXbotTextMsg(action_str);
                    }
                }

                if (action_str.startsWith(ActionChangeXbot)) {
                    //第四种情况 切换xbot
                    //moor_moor_m7_actionActionChangeXbot.m7_data:"+xbotid+".com
                    //以 ActionChangeXbot 开头取出中间文案作为消息发送
                    action_str = action_str.replace(ActionChangeXbot, "");
                    action_str = action_str.replace(".com", "");
                    if (!TextUtils.isEmpty(action_str)) {
                        ChatActivity context1 = (ChatActivity) context;
                        context1.changeRobot(action_str);
                    }
                }
            }
        }
    }

    class HttpClickSpan extends ClickableSpan {
        String msg;

        public HttpClickSpan(String msg) {
            this.msg = msg;
        }

        @Override
        public void onClick(View widget) {
            try {
                if (!msg.contains("http") && !msg.contains("https")) {
                    msg = "http://" + msg;
                } else {
                    if (msg.startsWith("http://tel:")) {
                        String tel = msg.replaceAll("http://tel:", "");
                        NumClickBottomSheetDialog dialog = NumClickBottomSheetDialog.instance(RegexUtils.regexNumber(tel));
                        dialog.show(((ChatActivity) context).getSupportFragmentManager(), "");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(msg);
                    intent.setData(content_url);
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                Toast.makeText(context, R.string.ykfsdk_url_failure, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static List<String> getImgStr(String htmlStr) {
        List<String> pics = new ArrayList<String>();
        String regEx_img = "(<img.*src\\s*=\\s*(.*?)[^>]*?>|<p><video.*src\\s*=\\s*(.*?)[^>]*?</video></p>)";
//        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        Pattern p_image = Pattern.compile
                (regEx_img, Pattern.CASE_INSENSITIVE);
        Matcher m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            // 得到<img />或<video数据
            String img = m_image.group();
//            Matcher m = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')").matcher(img);
            Matcher m = Pattern.compile("(<img.*?src|<img.*?SRC|<video.*?src)=(\"|')(.*?)(\"|')").matcher(img);
            while (m.find()) {
                String g = m.group();
                if (g.startsWith("<video")) {
                    //如果是
                    pics.add("<video" + m.group(3));
                } else {
                    pics.add(m.group(3));
                }

            }
        }
        return pics;
    }

    class AString {
        public String a;
        public String content;
        public String url;
        public String action;
        public String peerid;
        public String xbotQuickQuestionData;
        public String changeRobotId;
    }

    private List<AString> setAString(String message) {

        SpannableString spannableString = new SpannableString(message);

        List<AString> list = new ArrayList<>();

        Pattern patten = Pattern.compile("<a[^>]*>([^<]*)</a>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            AString atring = new AString();
            //a标签
            atring.a = matcher.group();
            //文字
            atring.content = matcher.group(1);
            Matcher m = Pattern.compile("(href|HREF)=(\"|')(.*?)(\"|')").matcher(matcher.group());
            //链接
            while (m.find()) {
                atring.url = m.group(3);
            }
            list.add(atring);
        }

        return list;
    }

    /**
     * 展示图片
     */
    private void showImage(final String imageurl, TextViewHolder holder) {
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DensityUtil.dp2px(200),
//                DensityUtil.dp2px(200));

        if (imageurl.startsWith("<video")) {
            addVideo(imageurl, holder);
        } else {
            LinearLayout.LayoutParams params;

            int h_Cache = ImageHeightCache.getInstance().getImgH(imageurl);
            h_Cache = DensityUtil.dp2px(h_Cache);
            if (h_Cache > DensityUtil.dp2px(200)) {
                h_Cache = DensityUtil.dp2px(200);
            }
            if (h_Cache == 0) {

                params = new LinearLayout.LayoutParams(
                        DensityUtil.getScreenWidth(context) - DensityUtil.dp2px(98),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {

                params = new LinearLayout.LayoutParams(
                        DensityUtil.getScreenWidth(context) - DensityUtil.dp2px(98),
                        h_Cache);
            }

            final ImageView iv = new ImageView(context);
            iv.setAdjustViewBounds(true);
            iv.setMaxWidth(DensityUtil.getScreenWidth(context) - DensityUtil.dp2px(98));
            iv.setMaxHeight(DensityUtil.dp2px(200));
            iv.setScaleType(ImageView.ScaleType.FIT_START);

            params.setMargins(0, DensityUtil.dp2px(4), 0, DensityUtil.dp2px(4));
            iv.setLayoutParams(params);


            if (IMChatManager.getInstance().getImageLoader() != null) {

                IMChatManager.getInstance().getImageLoader().loadImage(false, false, imageurl,
                        iv, 0, 0, 5, null, null, new IMoorImageLoaderListener() {
                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onLoadComplete(@NonNull Bitmap bitmap) {
                                ImageHeightCache.getInstance().putImgH(imageurl, bitmap.getHeight());
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                iv.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResourceReady(@NonNull File resource) {

                            }
                        });
            } else {
                MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
            }

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MoorImageInfoBean bean = new MoorImageInfoBean();
                    bean.setFrom("in").setPath(imageurl);
                    MoorImagePreview.getInstance()
                            .setContext(context)
                            .setIndex(0)
                            .setImage(bean)
                            .start();
                }
            });


            holder.getDescLinearLayout().addView(iv);
            MoorLogUtils.eTag("ImageView", iv.toString());

        }
    }

    private void showPopWindows(View view, final String srcTxt) {
        List<String> dataList = new ArrayList<>();
        dataList.add(context.getString(R.string.ykfsdk_ykf_copy));
        if (mPopupWindowList == null) {
            mPopupWindowList = new PopupWindowList(view.getContext());
        }
        mPopupWindowList.setAnchorView(view);
        mPopupWindowList.setItemData(dataList);
        mPopupWindowList.setModal(true);
        mPopupWindowList.show();
        mPopupWindowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                copyTxt(srcTxt);
                ToastUtils.showShort(context, context.getString(R.string.ykfsdk_ykf_copy_success));
                mPopupWindowList.hide();
            }
        });
    }

    /**
     * 复制文本
     */
    public static void copyTxt(String srcTxt) {
        ClipboardManager clipboardManager = (ClipboardManager) IMChatManager.getInstance().getApplicationAgain().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", srcTxt);
        clipboardManager.setPrimaryClip(clipData);
    }


    private void parserImg(FromToMessage message, TextViewHolder holder) {
        //获取内容的图片src
        final List<String> data = getImgStr(message.message);
        String content = message.message.replaceAll("<(img|IMG|video|VIDEO)(.*?)(/>|></img>|></video>|>)", "---");
        String[] strings = content.split("---");
        if (strings.length == 0) {
            if (data.size() > 0) {
                showImage(data.get(0), holder);
            }
        }
    }

    private void initImgandText(final String message, TextViewHolder holder) {
        List<String> data = getImgStr(message);
        String content = message.replaceAll("<(img|IMG|video|VIDEO)(.*?)(/>|></img>|></video>|>)", "---");
        String[] strings = content.split("---");

        for (int i = 0; i < strings.length; i++) {
            TextView tv1 = new TextView(context);
            tv1.setTextColor(context.getResources().getColor(R.color.ykfsdk_color_151515));
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv1.setLineSpacing(0f, 1.2f);
            if (message.contains("</a>") && (!message.contains("1："))) {

                String actionmsg = setA_String(strings[i]);
                actionmsg = actionmsg.replaceAll("<font", "<" + MoorFontSizeTag);
                actionmsg = actionmsg.replaceAll("</font>", "</" + MoorFontSizeTag + ">");
                Spanned string;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    string = Html.fromHtml(actionmsg, Html.FROM_HTML_MODE_COMPACT, null, new HtmlTagHandler(MoorFontSizeTag));
                } else {
                    string = Html.fromHtml(actionmsg, null, new HtmlTagHandler(MoorFontSizeTag));
                }

                Spanned string_o = new SpannableStringBuilder(MoorUtils.trimTrailingWhitespace(string));

                SpannableStringBuilder spannableString = new SpannableStringBuilder(string_o);
                spannableString = getClickableHtml(spannableString);

                boolean ismatcher = false;
                //匹配a标签 自定义 点击️
                Pattern patten1 = Pattern.compile("\\[([^\\]]*)\\]\\(([^\\)]*)\\)", Pattern.CASE_INSENSITIVE);
                Matcher matcher1 = patten1.matcher(spannableString);
                while (matcher1.find()) {
                    ismatcher = true;
                    String m7_data = matcher1.group(1);
                    String m7_name = matcher1.group(2);
                    if (checkURL(m7_name)) {
                        spannableString.replace(matcher1.start(), matcher1.end(), m7_data);
                        AClickApan clickSpan = new AClickApan(m7_name);
                        spannableString.setSpan(clickSpan, matcher1.start(), matcher1.start() + m7_data.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                        spannableString.setSpan(colorSpan, matcher1.start(), matcher1.start() + m7_data.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        matcher1 = patten1.matcher(spannableString);
                    }

                }

                if (!ismatcher) {
                    //匹配电话
                    String regex = RegexUtils.isPhoneRegexp();
                    Pattern patten3 = Pattern.
                            compile(regex,
                                    Pattern.CASE_INSENSITIVE);
                    Matcher matcher3 = patten3.matcher(spannableString);
                    while (matcher3.find()) {
                        String number = matcher3.group();
                        int end = matcher3.start() + number.length();
                        NumClickSpan clickSpan = new NumClickSpan(number);
                        spannableString.setSpan(clickSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                        spannableString.setSpan(colorSpan, matcher3.start(), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }
                tv1.setAutoLinkMask(Linkify.ALL);
                tv1.setText(spannableString);
                tv1.setLinkTextColor(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
                tv1.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                List<AString> list = setAString(strings[i]);
                setNoImgView(tv1, strings[i], list);
            }


            holder.getDescLinearLayout().addView(tv1);

            if (data.size() > i) {
                showImage(data.get(i), holder);
            }
            if (data.size() == 0) {//无图片的情况可以复制
                holder.getDescLinearLayout().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showPopWindows(view, message);
                        return true;
                    }
                });
            }

        }

    }


    //解析A标签，替换我们要的字符串类型
    private String setA_String(String message) {
        SpannableString spannableString = new SpannableString(message);
        Pattern patten = Pattern.compile("<a[^>]*>([^<]*)</a>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            AString atring = new AString();
            //a标签
            atring.a = matcher.group();
            //文字
            atring.content = matcher.group(1);

            Document doc = Jsoup.parse(atring.a);
            Elements chatElements = doc.getElementsByTag("a");

            if (chatElements != null) {
                if (chatElements.size() > 0) {
                    for (int i = 0; i < chatElements.size(); i++) {
                        if (chatElements.get(i) != null) {
                            Element chatElement = chatElements.get(i);
                            atring.action = chatElement.attr("m7_action");
                            if (!TextUtils.isEmpty(atring.action)) {
                                if ("robotTransferAgent".equals(atring.action) | "transferAgent".equals(atring.action)) {
                                    //m7_action为 transferAgent 响应转人工
                                    //m7_action为 transferAgent 响应转人工，m7_data 取出技能组id
                                    atring.peerid = chatElement.attr("m7_data");
                                    message = message.replace(atring.a,
                                            "[" + atring.content + "](" + ActionrobotTransferAgent + atring.peerid + ".com)");
                                } else if ("xbot-quick-question".equals(atring.action)) {
                                    //m7_action为 xbot-quick-question 是xbot提问内容，m7_data中是时机要发送的文案
                                    atring.xbotQuickQuestionData = chatElement.attr("m7_data");
                                    message = message.replace(atring.a,
                                            "[" + atring.content + "](" + ActionXbotQuickQuestion + atring.xbotQuickQuestionData + ".com)");
                                } else if ("xbotTransferRobot".equals(atring.action)) {
                                    //m7_action为 xbotTransferRobot 是切换机器人，m7_data中是要切换的机器人ID
                                    atring.changeRobotId = chatElement.attr("m7_data");
                                    message = message.replace(atring.a,
                                            "[" + atring.content + "](" + ActionChangeXbot + atring.changeRobotId + ".com)");
                                }
                            }

                            //data-phone href 并且值为tel: 开头 有值为电话号
                            if (TextUtils.isEmpty(atring.action)) {
                                atring.action = chatElement.attr("href");
                                if (!TextUtils.isEmpty(atring.action)) {
                                    if (atring.action.startsWith("tel:")) {
                                        message = message.replace(atring.a,
                                                "[" + atring.content + "](" + ActiondatapPhoneHref + atring.content + ".com)");
                                    }
                                }
                            }
                        } else {

                        }
                    }

                }
            }
        }
        return message;
    }


    private void addVideo(final String videourl, TextViewHolder holder) {
        View view = View.inflate(context, R.layout.ykfsdk_ykf_textrx_video, null);
        ImageView thumb_one = view.findViewById(R.id.iv_textrx_video);


        final String video_url = videourl.replace("<video", "");

        if (IMChatManager.getInstance().getImageLoader() != null) {
            IMChatManager.getInstance().getImageLoader().loadImage(false, false, video_url,
                    thumb_one, 0, 0, 8, null, null, null);
        } else {
            MoorLogUtils.eTag("ImageLoader", "ImageLoader is null");
        }

        thumb_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, YKFVideoActivity.class);
                //处理一个视频名称
                String videoName=video_url;
                String[] split=video_url.split("/");
                if(split.length>0){
                    videoName=split[split.length-1];
                }
                intent.putExtra(YKFConstants.YKFVIDEOFILENAME, videoName);
                intent.putExtra(YKFConstants.YKFVIDEOPATHURI, video_url);
                context.startActivity(intent);
            }
        });


        holder.getDescLinearLayout().addView(view);
    }


    private boolean checkURL(String url) {


//        return url.startsWith("https")
//                | url.startsWith("http")
//                | url.startsWith("ftp")
//                | url.startsWith("www");
        return url.startsWith("moor_moor_m7_action");

    }


    private void setLinkClickable(final SpannableStringBuilder clickableHtmlBuilder, final URLSpan urlSpan) {
        int start = clickableHtmlBuilder.getSpanStart(urlSpan);
        int end = clickableHtmlBuilder.getSpanEnd(urlSpan);
        int flags = clickableHtmlBuilder.getSpanFlags(urlSpan);
        HttpClickSpan clickableSpan = new HttpClickSpan(urlSpan.getURL());
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(FIleResourceUtil.getCurrentColor(context, R.attr.ykfsdk_ykf_theme_color_default));
        clickableHtmlBuilder.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private SpannableStringBuilder getClickableHtml(Spanned spannedHtml) {
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml);
        URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for (final URLSpan span : urls) {
            setLinkClickable(clickableHtmlBuilder, span);
        }
        return clickableHtmlBuilder;
    }

    /**
     * 设置xbot推联按钮
     *
     * @param detail
     * @param holder
     */
    private void setPushBtn(FromToMessage detail, TextViewHolder holder) {
        final RobotInfoByObject info = IMChatManager.getInstance().getRobotInfoByObject();
        if (info != null) {
            if (info.PushContactInfo != null) {
                //两个按钮先都不显示
                holder.tv_push_copy.setVisibility(View.GONE);
                holder.tv_push_scan.setVisibility(View.GONE);


                //打开复制
                if (info.PushContactInfo.enable_copy == 1) {
                    holder.tv_push_copy.setVisibility(View.VISIBLE);
                    holder.tv_push_copy.setText(info.PushContactInfo.copy_prompt);
                    holder.tv_push_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            cm.setText(info.PushContactInfo.number);
                            ToastUtils.showShort(context, context.getString(R.string.ykfsdk_ykf_copyok));
                            HttpManager.sdkClickContactConfirm(info.PushContactInfo.copy_prompt, true, false, null);
                        }
                    });
                }

                //打开扫码
                if (info.PushContactInfo.enable_scan == 1) {
                    holder.tv_push_scan.setVisibility(View.VISIBLE);
                    holder.tv_push_scan.setText(info.PushContactInfo.scan_prompt);
                    holder.tv_push_scan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MoorImageInfoBean bean = new MoorImageInfoBean();
                            bean.setFrom("in").setPath(info.PushContactInfo.qr_code);
                            MoorImagePreview.getInstance()
                                    .setContext(context)
                                    .setIndex(0)
                                    .setImage(bean)
                                    .start();
                            HttpManager.sdkClickContactConfirm(info.PushContactInfo.scan_prompt, false, true, null);
                        }
                    });
                }
            } else {
                holder.tv_push_scan.setVisibility(View.GONE);
                holder.tv_push_copy.setVisibility(View.GONE);
            }
        } else {
            holder.tv_push_scan.setVisibility(View.GONE);
            holder.tv_push_copy.setVisibility(View.GONE);
        }
    }

}
