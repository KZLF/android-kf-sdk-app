package com.m7.imkfsdk.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.chatrow.BaseChatRow;
import com.m7.imkfsdk.chat.chatrow.BreakTipChatRow;
import com.m7.imkfsdk.chat.chatrow.CardRxChatBox;
import com.m7.imkfsdk.chat.chatrow.ChatRowType;
import com.m7.imkfsdk.chat.chatrow.ChatRowUtils;
import com.m7.imkfsdk.chat.chatrow.FileIsVideoRxChatRow;
import com.m7.imkfsdk.chat.chatrow.FileIsVideoTxChatRow;
import com.m7.imkfsdk.chat.chatrow.FileRxChatRow;
import com.m7.imkfsdk.chat.chatrow.FileTxChatRow;
import com.m7.imkfsdk.chat.chatrow.IChatRow;
import com.m7.imkfsdk.chat.chatrow.IframeRxChatRow;
import com.m7.imkfsdk.chat.chatrow.ImageRxChatRow;
import com.m7.imkfsdk.chat.chatrow.ImageTxChatRow;
import com.m7.imkfsdk.chat.chatrow.InvestigateCancelRxChatRow;
import com.m7.imkfsdk.chat.chatrow.InvestigateSuccessTxChatRow;
import com.m7.imkfsdk.chat.chatrow.LogisticsInfoRxChatRow;
import com.m7.imkfsdk.chat.chatrow.LogisticsInfoTxChatRow;
import com.m7.imkfsdk.chat.chatrow.NewCardInfoTxChatRow;
import com.m7.imkfsdk.chat.chatrow.QuickMenuListRxRow;
import com.m7.imkfsdk.chat.chatrow.ReceivedCardInfoRxChatRow;
import com.m7.imkfsdk.chat.chatrow.RichRxChatRow;
import com.m7.imkfsdk.chat.chatrow.RichTxChatRow;
import com.m7.imkfsdk.chat.chatrow.SendCardInfoTxChatRow;
import com.m7.imkfsdk.chat.chatrow.TabQuestionRxRow;
import com.m7.imkfsdk.chat.chatrow.TextRxChatRow;
import com.m7.imkfsdk.chat.chatrow.TextTxChatRow;
import com.m7.imkfsdk.chat.chatrow.UnknownRxChatRow;
import com.m7.imkfsdk.chat.chatrow.VideoRxChatRow;
import com.m7.imkfsdk.chat.chatrow.VideoTxChatRow;
import com.m7.imkfsdk.chat.chatrow.VoiceRxChatRow;
import com.m7.imkfsdk.chat.chatrow.VoiceTxChatRow;
import com.m7.imkfsdk.chat.chatrow.XbotFormRxChatRow;
import com.m7.imkfsdk.chat.chatrow.XbotFormSubmitTxChatRow;
import com.m7.imkfsdk.chat.holder.BaseHolder;
import com.m7.imkfsdk.chat.listener.ChatListClickListener;
import com.m7.imkfsdk.utils.DateUtil;
import com.m7.imkfsdk.utils.MediaPlayTools;
import com.moor.imkf.model.entity.FromToMessage;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Package Name:com.m7.imkfsdk.chat.adapter
 * </p>
 * <p>
 * Class Name:ChatAdapter
 * <p>
 * Description:
 * </p>
 *
 * @Author longwei
 * @Version 1.0 2016/3/9 Release
 * @Reviser: Martin
 * @Modification Time:2018/11/19 3:24 PM
 */
public class ChatAdapter extends BaseAdapter {

    private List<FromToMessage> messageList;

    private final Context context;

    private final HashMap<Integer, IChatRow> chatRowHashMap;
    public int mVoicePosition = -1;

    protected View.OnClickListener mOnClickListener;
    protected View.OnLongClickListener mOnLongClickListener;//长点击时间包装

    public ChatAdapter(Context context, List<FromToMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
        chatRowHashMap = new HashMap<>();
        mOnClickListener = new ChatListClickListener((ChatActivity) context, null);
        mOnLongClickListener = new ChatListClickListener((ChatActivity) context, null);
        initRowItems();
    }

    public void setMessageList(List<FromToMessage> list) {
        this.messageList = list;
    }

    void initRowItems() {
        //Rx代表收到的消息；Tx代表发出的消息

        /**
         收到和发出的 文本消息
         {@link FromToMessage#MSG_TYPE_TEXT}
         */
        chatRowHashMap.put(1, new TextRxChatRow(1));
        chatRowHashMap.put(2, new TextTxChatRow(2));
        /**
         收到和发出的 图片消息
         {@link FromToMessage#MSG_TYPE_IMAGE}
         */
        chatRowHashMap.put(3, new ImageRxChatRow(3));
        chatRowHashMap.put(4, new ImageTxChatRow(4));
        /**
         收到和发出的 语音消息
         {@link FromToMessage#MSG_TYPE_AUDIO}
         */
        chatRowHashMap.put(5, new VoiceRxChatRow(5));
        chatRowHashMap.put(6, new VoiceTxChatRow(6));
        /**
         收到和发出的 文件消息
         {@link FromToMessage#MSG_TYPE_FILE}
         */
        chatRowHashMap.put(8, new FileRxChatRow(8));
        chatRowHashMap.put(9, new FileTxChatRow(9));
        /**
         收到的 iframe消息
         {@link FromToMessage#MSG_TYPE_IFRAME}
         */
        chatRowHashMap.put(10, new IframeRxChatRow(10));
        /**
         本地构建的 无响应断开提示消息
         {@link FromToMessage#MSG_TYPE_BREAK_TIP}
         */
        chatRowHashMap.put(11, new BreakTipChatRow(11));
        /**
         收到和发出的 富文本消息
         {@link FromToMessage#MSG_TYPE_RICHTEXT}
         */
        chatRowHashMap.put(13, new RichRxChatRow(13));
        chatRowHashMap.put(14, new RichTxChatRow(14));
        /**
         本地构建的 用于发送cardInfo类型的卡片消息
         {@link FromToMessage#MSG_TYPE_CARD}
         */
        chatRowHashMap.put(15, new CardRxChatBox(15));
        /**
         收到和发出的 视频会话
         {@link FromToMessage#MSG_TYPE_VIDEO}
         */
        chatRowHashMap.put(16, new VideoTxChatRow(16));
        chatRowHashMap.put(17, new VideoRxChatRow(17));
        /**
         收到和发出的 xbot商品物流/订单列表消息
         {@link FromToMessage#MSG_TYPE_LOGISTICS_INFO_LIST}
         */
        chatRowHashMap.put(18, new LogisticsInfoRxChatRow(18));
        chatRowHashMap.put(19, new LogisticsInfoTxChatRow(19));
        /**
         本地构建的 用于发送newCardInfo类型的卡片消息
         {@link FromToMessage#MSG_TYPE_NEW_CARD}
         */
        chatRowHashMap.put(20, new NewCardInfoTxChatRow(20));//要发给坐席的卡片类型
        /**
         收到和发出的 新卡片类型消息
         {@link FromToMessage#MSG_TYPE_NEW_CARD_INFO}
         */
        chatRowHashMap.put(21, new SendCardInfoTxChatRow(21));
        chatRowHashMap.put(22, new ReceivedCardInfoRxChatRow(22));
        /**
         本地构建的 用户没完成评价 点击取消时插入的点击评价消息
         {@link FromToMessage#MSG_TYPE_INVESTIGATE_CANCEL}
         */
        chatRowHashMap.put(23, new InvestigateCancelRxChatRow(23));
        /**
         本地构建的 用户完成评价 插入的评价内容消息
         {@link FromToMessage#MSG_TYPE_INVESTIGATE_SUCCESS}
         */
        chatRowHashMap.put(24, new InvestigateSuccessTxChatRow(24));
        /**
         收到的 xbot分组常见问题
         {@link FromToMessage#MSG_TYPE_TAB_QUESTION}
         */
        chatRowHashMap.put(25, new TabQuestionRxRow(25));
        /**
         收到和发出的 xbot表单类型消息
         {@link FromToMessage#MSG_TYPE_XBOT_FORM_DATA}
         {@link FromToMessage#MSG_TYPE_XBOT_FORM_SUBMIT}
         */
        chatRowHashMap.put(26, new XbotFormRxChatRow(26));
        chatRowHashMap.put(27, new XbotFormSubmitTxChatRow(27));
        /**
         收到的 来自newchat返回的横向快捷按钮类型
         {@link FromToMessage#QUICK_MENU_LIST}
         */
        chatRowHashMap.put(28, new QuickMenuListRxRow(28));
        /**
         收到和发出的 文件是视频消息 处理本来为File类型的转换为此类型
         {@link FromToMessage#MSG_TYPE_FILE_IS_VIDEO}
         */
        chatRowHashMap.put(29, new FileIsVideoTxChatRow(29));
        chatRowHashMap.put(30, new FileIsVideoRxChatRow(30));
        /**
         * 位置消息类型 空白显示 忽略
         *{@link FromToMessage#MSG_TYPE_UNKNOWN}
         */
        chatRowHashMap.put(100, new UnknownRxChatRow(100));
    }

    public void setVoicePosition(int position) {
        mVoicePosition = position;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public synchronized FromToMessage getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //根据该条消息获得类型的数字(在枚举类型中的ordinal)
    @Override
    public synchronized int getItemViewType(int position) {
        FromToMessage message = getItem(position);
        int type = getBaseChatRow(ChatRowUtils.getChattingMessageType(message), message.userType.equals("0")).getChatViewType();
        return type;
    }

    //消息类型的数量
    @Override
    public int getViewTypeCount() {
        return ChatRowType.values().length;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FromToMessage message = getItem(position);

        if (message == null) {
            return null;
        }


        //构建消息的view
        Integer messageType = ChatRowUtils.getChattingMessageType(message);
        BaseChatRow chatRow = getBaseChatRow(messageType, message.userType.equals("0"));
        View chatView = chatRow.buildChatView(LayoutInflater.from(context), convertView);
        BaseHolder baseHolder = (BaseHolder) chatView.getTag();

        //显示时间
        boolean showTimer = position == 0;
        if (position != 0) {
            FromToMessage previousItem = getItem(position - 1);
            if ((message.when - previousItem.when >= 180000L)) {
                showTimer = true;
            }
        }
        if (baseHolder.getChattingTime() != null) {
            if (showTimer) {
                baseHolder.getChattingTime().setVisibility(View.VISIBLE);
                baseHolder.getChattingTime().setText(DateUtil.getDateString(message.when, DateUtil.SHOW_TYPE_CALL_LOG, context).trim());
//            baseHolder.getChattingTime().setTextColor(Color.WHITE);
//            baseHolder.getChattingTime().setBackgroundResource(R.color.lightgrey);
                baseHolder.getChattingTime().setPadding(6, 2, 6, 25);
            } else {
                baseHolder.getChattingTime().setVisibility(View.GONE);
                baseHolder.getChattingTime().setShadowLayer(0.0F, 0.0F, 0.0F, 0);
//            baseHolder.getChattingTime().setBackgroundResource(0);
            }
        }

        //填充消息的数据
        chatRow.buildChattingBaseData(context, baseHolder, message, position);

        return chatView;
    }


    /**
     * 根据消息类型返回相对应的消息Item
     *
     * @param rowType
     * @param isSend
     * @return
     */
    public BaseChatRow getBaseChatRow(int rowType, boolean isSend) {
        StringBuilder builder = new StringBuilder("C").append(rowType);
        if (isSend) {
            builder.append("T");
        } else {
            builder.append("R");
        }
        ChatRowType fromValue = ChatRowType.fromValue(builder.toString());
        IChatRow iChatRow = chatRowHashMap.get(fromValue.getId());
        return (BaseChatRow) iChatRow;
    }

    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }
//    public View.OnClickListener getLongClickListener() {
//        return mOnLongClickListener;
//    }

    public void onPause() {
        mVoicePosition = -1;
        MediaPlayTools.getInstance().stop();
    }

}
