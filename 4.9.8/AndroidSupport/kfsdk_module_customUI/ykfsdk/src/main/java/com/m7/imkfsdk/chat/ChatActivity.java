package com.m7.imkfsdk.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.ContentScrollMeasurer;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.m7.imkfsdk.KfStartHelper;
import com.m7.imkfsdk.MoorWebCenter;
import com.m7.imkfsdk.R;
import com.m7.imkfsdk.chat.adapter.ChatAdapter;
import com.m7.imkfsdk.chat.adapter.ChatTagLabelsAdapter;
import com.m7.imkfsdk.chat.dialog.BottomChangeRobotDialog;
import com.m7.imkfsdk.chat.dialog.BottomSheetLogisticsInfoDialog;
import com.m7.imkfsdk.chat.dialog.BottomSheetLogisticsProgressDialog;
import com.m7.imkfsdk.chat.dialog.BottomTabQuestionDialog;
import com.m7.imkfsdk.chat.dialog.BottomXbotFormDialog;
import com.m7.imkfsdk.chat.dialog.CommonBottomSheetDialog;
import com.m7.imkfsdk.chat.dialog.InvestigateDialog;
import com.m7.imkfsdk.chat.dialog.LoadingFragmentDialog;
import com.m7.imkfsdk.chat.dialog.QuitQueueDialog;
import com.m7.imkfsdk.chat.dialog.TimerQuitDialog;
import com.m7.imkfsdk.chat.emotion.EmotionPagerView;
import com.m7.imkfsdk.chat.emotion.Emotions;
import com.m7.imkfsdk.chat.listener.ChatListClickListener;
import com.m7.imkfsdk.chat.listener.CheckRobotViewTouchListener;
import com.m7.imkfsdk.chat.listener.OnCancelDialogListener;
import com.m7.imkfsdk.chat.listener.OnClickRobotListener;
import com.m7.imkfsdk.chat.listener.QuitQueueDailogListener;
import com.m7.imkfsdk.chat.listener.SubmitPingjiaListener;
import com.m7.imkfsdk.chat.listener.TimerQuitListener;
import com.m7.imkfsdk.chat.model.MsgTaskBean;
import com.m7.imkfsdk.chat.model.MsgTaskItemBean;
import com.m7.imkfsdk.chat.model.OrderBaseBean;
import com.m7.imkfsdk.chat.model.OrderInfoBean;
import com.m7.imkfsdk.chat.model.OrderInfoParams;
import com.m7.imkfsdk.chat.model.RobotListBean;
import com.m7.imkfsdk.constant.MoorDemoConstants;
import com.m7.imkfsdk.recordbutton.AudioRecorderButton;
import com.m7.imkfsdk.utils.DensityUtil;
import com.m7.imkfsdk.utils.FileUtils;
import com.m7.imkfsdk.utils.MoorFileUtils;
import com.m7.imkfsdk.utils.MoorTakePicturesHelper;
import com.m7.imkfsdk.utils.PickUtils;
import com.m7.imkfsdk.utils.RegexUtils;
import com.m7.imkfsdk.utils.ToastUtils;
import com.m7.imkfsdk.utils.permission.PermissionConstants;
import com.m7.imkfsdk.utils.permission.PermissionXUtil;
import com.m7.imkfsdk.utils.permission.callback.OnRequestCallback;
import com.m7.imkfsdk.utils.statusbar.StatusBarUtils;
import com.m7.imkfsdk.view.BottomSheetVideoOrVoiceDialog;
import com.m7.imkfsdk.view.ChatListView;
import com.m7.imkfsdk.view.SpaceItemDecoration;
import com.moor.imkf.IMChat;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.IMMessage;
import com.moor.imkf.SocketService;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.YKFErrorCode;
import com.moor.imkf.db.dao.GlobalSetDao;
import com.moor.imkf.db.dao.InfoDao;
import com.moor.imkf.db.dao.MessageDao;
import com.moor.imkf.event.KFBreakTimerEvent;
import com.moor.imkf.event.MsgEvent;
import com.moor.imkf.event.MsgunReadToReadEvent;
import com.moor.imkf.event.ReSendMessage;
import com.moor.imkf.event.TcpBreakEvent;
import com.moor.imkf.event.TransferAgent;
import com.moor.imkf.event.UnAssignEvent;
import com.moor.imkf.event.VoiceToTextEvent;
import com.moor.imkf.event.XbotFormEvent;
import com.moor.imkf.http.HttpManager;
import com.moor.imkf.lib.socket.websocket.WebSocketHandler;
import com.moor.imkf.lib.utils.MoorAntiShakeUtils;
import com.moor.imkf.lib.utils.MoorSdkVersionUtil;
import com.moor.imkf.lib.utils.sharedpreferences.MoorSPUtils;
import com.moor.imkf.listener.AcceptOtherAgentListener;
import com.moor.imkf.listener.ChatListener;
import com.moor.imkf.listener.HttpResponseListener;
import com.moor.imkf.listener.OnConvertManualListener;
import com.moor.imkf.listener.OnSessionBeginListener;
import com.moor.imkf.listener.onResponseListener;
import com.moor.imkf.model.construct.JsonBuild;
import com.moor.imkf.model.entity.CardInfo;
import com.moor.imkf.model.entity.ChatSessionBean;
import com.moor.imkf.model.entity.FlowBean;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.GlobalSet;
import com.moor.imkf.model.entity.MoorFastBtnBean;
import com.moor.imkf.model.entity.NewCardInfo;
import com.moor.imkf.model.entity.XbotForm;
import com.moor.imkf.model.entity.YKFCallInfoBean;
import com.moor.imkf.model.entity.YKFChatStatusEnum;
import com.moor.imkf.model.parser.HttpParser;
import com.moor.imkf.utils.LogUtils;
import com.moor.imkf.utils.MoorUtils;
import com.moor.imkf.utils.NullUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 聊天界面
 *
 * @author LongWei
 */
public class ChatActivity extends KFBaseActivity implements OnClickListener
        , ChatListView.OnRefreshListener, AudioRecorderButton.RecorderFinishListener {
    private static final String tag = "ChatActivity";
    private static final int HANDLER_MSG = 1;
    private static final int HANDLER_MSG_MORE = 2;
    private static final int HANDLER_ROBOT = 0x111;
    private static final int HANDLER_ONLINE = 0x222;
    private static final int HANDLER_OFFNLINE = 0x333;
    private static final int HANDLER_INVESTIGATE = 0x444;
    private static final int HANDLER_QUEUENUM = 0x555;
    private static final int HANDLER_CLIAM = 0x666;
    private static final int HANDLER_FINISH = 0x777;
    private static final int HANDLER_BREAK = 0x888;
    private static final int HANDLER_BREAK_TIP = 0x999;
    private static final int HANDLER_VIPASSIGNFAIL = 0x1000;
    private static final int HANDLER_LEAVEMSG = 0x1100;
    private static final int HANDLER_WRITING = 0x1200;
    private static final int HANDLER_NO_WRITING = 0x1300;
    public static boolean isCustomerRead;//fasle 隐藏已读未读ui，true 显示ui


    private int pageSize = 2;

    private boolean isFront = false;//记录当前应用是否在前台
    private boolean isListBottom = false;//list是否在底部
    private boolean JZflag = true;
    private boolean isZXResply = false;//坐席是否回复过访客，默认没有回复过（tcp推送变为true，接口校验也变为true ）
    private boolean NotAllowCustomerPushCsr = false;//    访客不能主动发评价 false 展示 true隐藏
    private boolean NotAllowCustomerCloseCsr = false;//   点击注销  窗口弹出满意度评价 false弹   true不弹出
    private boolean robotEvaluationFinish = false;    //小陌机器人评价是否完成
    private boolean hasSendRobotMsg = false;    //是否和机器人发送过消息
    private boolean hasSendPersonMsg = false;    //是否和人工坐席发过消息
    private boolean isInvestigate = true;//是否已经评价过了 true代表没有评价过，false代表评价过了
    private boolean convesationIsLive = true;// 会话是有存在或者有效。 true代表存在，false代表不存在
    private boolean hasSet = true;//是否配置了满意度列表
    private boolean conversationOver = false;//pc是否已经关闭会话 true关闭了
    private boolean INVITATION_INVESTIGATE = false;//坐席邀请评价;true：邀请了
    private boolean showInviteButton;//是否显示评价按钮
    private boolean sdkTypeNoticeFlag; //访客输入状态是否要启用


    private String peerId = "";//技能组ID
    private String left_text;//左上角按钮文案
    private String type = "";//当前是日程还是技能组peedId/schedule
    private String scheduleId = "";//日程id
    private String schedule_id = "";//跳转留言日程id
    private String processId = "";
    private String currentNodeId = "";
    private String schedule_topeer = "";
    private String processType = "";
    private String titleName = "";
    private String entranceId = "";
    private String break_tips;//断开提示语
    private String break_end;//断开结束语
    private String chatId = "";//会话id
    private String exten;//工号
    private String userName;//昵称
    private String userIcon;


    private ArrayList<FromToMessage> fromToMessage;
    private final ArrayList<FromToMessage> descFromToMessage = new ArrayList<FromToMessage>();//listview绑定的消息体数据源
    private final ArrayList<FlowBean> flowBeanList = new ArrayList<>();

    private final Set<String> mHashSet = new HashSet<>();//用来存储订单消息的_id，在onDestroy()中将 showOrderInfo 字段全部置为“2”

    private ChatHandler handler;
    private CountDownTimer mVoiceToTextTimer;//语音转文字计时器
    private MsgReceiver msgReceiver;//新消息接收器
    private KeFuStatusReceiver keFuStatusReceiver;//客服状态等操作接收器
    private DelaySendTask delaySendTask;//延迟调用正在输入接口

    private boolean serviceShowVoice;//来自后台控制的语音按钮开关,false则都隐藏相当于没有语音和文本的切换

    private ChatListView mChatList;
    private Button mChatSend, mChatSetModeVoice, mChatSetModeKeyboard, mChatMore;
    private TextView chat_tv_back, chat_tv_convert, mOtherName, chat_queue_tv;
    private EditText mChatInput;
    private ChatAdapter chatAdapter;
    private RelativeLayout mChatEdittextLayout;
    private AudioRecorderButton mRecorderButton;
    private ImageView mChatEmojiNormal, ivDeleteEmoji;
    private View header;
    private LinearLayout chat_queue_ll, bar_bottom, ll_hintView, rl_bottom, ll_bottom_intercept, ll_invite;
    private RecyclerView rvTagLabel;
    private ChatTagLabelsAdapter tagLabeAdapter;
    private LoadingFragmentDialog loadingDialog;
    private InvestigateDialog dialog;
    private BottomSheetLogisticsInfoDialog moreOrderInfoDialog;
    private EmotionPagerView pagerView;
    private RelativeLayout check_robot_float;//切换机器人悬浮按钮
    private RelativeLayout rl_container;
    private PanelSwitchHelper mHelper;
    private int unfilledHeight = 0;

    /**
     * 消息接收Handler
     */
    private static class ChatHandler extends Handler {
        private final WeakReference<ChatActivity> mActivty;
        StringBuilder fullResult = new StringBuilder();

        public void clearResult() {
            this.fullResult = new StringBuilder();
        }

        ChatHandler(ChatActivity Activty) {
            this.mActivty = new WeakReference<>(Activty);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatActivity chatActivity = mActivty.get();
            if (chatActivity != null) {
                chatActivity.handleMessage(msg, fullResult);
            }
        }
    }

    /**
     * 处理各类消息
     */
    public void handleMessage(Message msg, StringBuilder fullResult) {
        if (msg.what == HANDLER_MSG) {
            updateMessage();    // 这里就会刷新数据。
        } else if (msg.what == HANDLER_MSG_MORE) {
            // 加载更多的时候
            JZMoreMessage();
        } else if (msg.what == HANDLER_ROBOT) {
            //当前是机器人
            ToastUtils.showShort(this, R.string.ykfsdk_now_robit);
            if (IMChatManager.getInstance().isShowTransferBtn()) {
                chat_tv_convert.setVisibility(View.VISIBLE);
                LogUtils.dTag("handleMessage==", "当前是机器人-显示按钮");
            } else {
                chat_tv_convert.setVisibility(View.GONE);
                LogUtils.dTag("handleMessage==", "当前是机器人-隐藏按钮");
            }
            bar_bottom.setVisibility(View.VISIBLE);
            setChatInviteBtn();
            setRobotBottomList(false);
            setRobotQuickBtn();
            changeRobotVisiable();

//            //机器人 是否有自动发送的newcard,处理自动发送
//            autoSendNewCard();

        } else if (msg.what == HANDLER_ONLINE) {
            //当前是客服
            chat_tv_convert.setVisibility(View.GONE);
        } else if (msg.what == HANDLER_WRITING) {
            //对方当前正在输入
            mOtherName.setText(R.string.ykfsdk_other_writing);
        } else if (msg.what == HANDLER_NO_WRITING) {
            mOtherName.setText(titleName);
        } else if (msg.what == HANDLER_OFFNLINE) {
            ToastUtils.showShort(this, R.string.ykfsdk_people_not_online);
            if (IMChatManager.getInstance().isShowTransferBtn()) {
                chat_tv_convert.setVisibility(View.VISIBLE);
            } else {
                chat_tv_convert.setVisibility(View.GONE);
            }
            //坐席不在线 跳转留言
            showOffLineDialog();
        } else if (msg.what == HANDLER_INVESTIGATE) {
            //坐席邀请访客评价
            INVITATION_INVESTIGATE = true;
            openInvestigateDialog(false, YKFConstants.INVESTIGATE_TYPE_OUT, null, false);
        } else if (msg.what == HANDLER_QUEUENUM) {
            String queueNem = (String) msg.obj;
            showQueueNumLabel(queueNem);
            //排队
            setChatInviteBtn();
            changeRobotVisiable();
        } else if (msg.what == HANDLER_CLIAM) {
            //人工接入
            chat_queue_ll.setVisibility(View.GONE);
            chat_tv_convert.setVisibility(View.GONE);
            bar_bottom.setVisibility(View.VISIBLE);
            //校验是否有效会话
            checkConverstaion();
            Toast.makeText(getApplicationContext(), R.string.ykfsdk_people_now, Toast.LENGTH_SHORT).show();
            rvTagLabel.setVisibility(View.GONE);
//            IMChatManager.getInstance().resetBreakTimer();
            changeRobotVisiable();


//            //转人工后 是否有自动发送的newcard,处理自动发送
//            autoSendNewCard();


        } else if (msg.what == HANDLER_FINISH) {
            mOtherName.setText(R.string.ykfsdk_people_isleave);
            titleName = getString(R.string.ykfsdk_people_isleave);
            chat_tv_convert.setVisibility(View.GONE);
            conversationOver = true;
            ll_bottom_intercept.setVisibility(View.VISIBLE);
        } else if (msg.what == HANDLER_LEAVEMSG) {
            //跳留言
            startScheduleOffline();
        } else if (msg.what == HANDLER_BREAK) {
            LogUtils.dTag("BreakTimer", "HANDLER_BREAK===断开会话");
            //断开会话
            leaveChatActivity();
        } else if (msg.what == HANDLER_BREAK_TIP) {
            LogUtils.dTag("BreakTimer", "HANDLER_BREAK_TIP===断开会话前提示");
            //断开会话前提示
            IMChat.getInstance().createBreakTipMsg(break_tips);
            updateMessage();
        } else if (msg.what == HANDLER_VIPASSIGNFAIL) {
            //专属座席不在线
            showVipAssignFailDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ykfsdk_kf_activity_chat);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.ykfsdk_all_white));
        getIntentData(getIntent());
        handler = new ChatHandler(this);
        //将timestamp参数置空；
        MoorSPUtils.getInstance().put(YKFConstants.SERVERTIMESTAMP, "", true);
        left_text = MoorSPUtils.getInstance().getString(YKFConstants.CHATACTIVITYLEFTTEXT, "");

        titleName = getString(R.string.ykfsdk_wait_link);
        registerRec();
        EventBus.getDefault().register(this);

        //初始化ui
        initView();
        //注册按钮监听方法
        registerListener();
        chatAdapter = new ChatAdapter(ChatActivity.this, descFromToMessage);
        mChatList.setAdapter(chatAdapter);
        //查询数据库更新页面
        updateMessage();
        //是否配置了满意度
        hasSet = IMChatManager.getInstance().getInvestigate().size() > 0;
        loadingDialog = new LoadingFragmentDialog();
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show(this.getFragmentManager(), "");

        //开启回话
        beginChatSession();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .addEditTextFocusChangeListener(new OnEditFocusChangeListener() {
                        @Override
                        public void onFocusChange(@Nullable View view, boolean hasFocus) {
                            if (hasFocus) {
                                scrollToBottom();
                            }
                        }
                    })
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {
                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return defaultDistance - unfilledHeight;
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.chat_list;
                        }
                    })
                    .addPanelChangeListener(new OnPanelChangeListener() {
                        @Override
                        public void onKeyboard() {
//                            scrollToBottom();
                            mChatEmojiNormal.setSelected(false);
                            mChatMore.setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            mChatEmojiNormal.setSelected(false);
                            mChatMore.setSelected(false);
                        }

                        @Override
                        public void onPanel(IPanelView view) {
//                            scrollToBottom();
                            if (view instanceof PanelView) {
                                boolean selected1 = ((PanelView) view).getId() == R.id.panel_emotion;
                                mChatEmojiNormal.setSelected(selected1);
                                boolean selected = ((PanelView) view).getId() == R.id.panel_addition;
                                if (selected) {
                                    mChatEdittextLayout.setVisibility(View.VISIBLE);
                                    mRecorderButton.setVisibility(View.GONE);
                                    voiceAndTextBtnVisibility(serviceShowVoice, true, false);
                                }
                                mChatMore.setSelected(selected);
                            }
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if (panelView instanceof PanelView) {
                                if (((PanelView) panelView).getId() == R.id.panel_emotion) {
                                    int viewPagerSize = height - DensityUtil.dp2px(20f);
                                    pagerView.buildEmotionViews(
                                            mChatInput,
                                            Emotions.getEmotions(), width, viewPagerSize);
                                } else if (((PanelView) panelView).getId() == R.id.panel_addition) {
                                    dealAddMoreViewClickEvent((PanelView) panelView);
                                }
                            }
                        }
                    })
                    .logTrack(false)
                    .build();
            mChatList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    switch (scrollState) {
                        case SCROLL_STATE_IDLE:
                            isListBottom = isListViewReachBottomEdge(view);
                            break;
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    ChatListView.firstItemIndex = firstVisibleItem;
                    int childCount = mChatList.getChildCount();
                    if (childCount > 0) {
                        View lastChildView = mChatList.getChildAt(childCount - 1);
                        int bottom = lastChildView.getBottom();
                        int listHeight = mChatList.getHeight() - mChatList.getPaddingBottom() - rvTagLabel.getHeight();
                        unfilledHeight = listHeight - bottom;
                    }
                }
            });
        }
    }

    /**
     * 初始化页面ui控件
     */
    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        rvTagLabel = findViewById(R.id.rv_tag_label); //底部推荐标签
        rvTagLabel.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<FlowBean> datas = new ArrayList<>();
        tagLabeAdapter = new ChatTagLabelsAdapter(datas);
        rvTagLabel.setAdapter(tagLabeAdapter);
        rvTagLabel.addItemDecoration(new SpaceItemDecoration(DensityUtil.dp2px(10), 0));
        tagLabeAdapter.setOnItemClickListener(LabelsClickListener);
        rvTagLabel.setVisibility(View.GONE);//默认不展示滑动list

        mChatSend = this.findViewById(R.id.chat_send);
        chat_tv_back = this.findViewById(R.id.chat_tv_back);
        mRecorderButton = findViewById(R.id.chat_press_to_speak);
        mRecorderButton.setRecordFinishListener(this);
        mChatInput = this.findViewById(R.id.chat_input);
        ll_hintView = this.findViewById(R.id.ll_hintView);
        rl_bottom = this.findViewById(R.id.rl_bottom);
        mChatEdittextLayout = this.findViewById(R.id.chat_edittext_layout);
        mChatEmojiNormal = this.findViewById(R.id.chat_emoji_normal);
        //删除表情按钮
        ivDeleteEmoji = findViewById(R.id.iv_delete_emoji);
        mChatMore = findViewById(R.id.chat_more);
        mChatSetModeVoice = this.findViewById(R.id.chat_set_mode_voice);
        mChatSetModeKeyboard = this.findViewById(R.id.chat_set_mode_keyboard);
        //转人工服务按钮，判断是否需要显示
        chat_tv_convert = this.findViewById(R.id.chat_tv_convert);
        chat_queue_ll = findViewById(R.id.chat_queue_ll);
        chat_queue_tv = findViewById(R.id.chat_queue_tv);
        bar_bottom = findViewById(R.id.bar_bottom);
        mOtherName = this.findViewById(R.id.other_name);
        mChatList = this.findViewById(R.id.chat_list);
        pagerView = findViewById(R.id.view_pager);
        ll_bottom_intercept = findViewById(R.id.ll_bottom_intercept);
        rl_container = findViewById(R.id.root_layout);
        check_robot_float = findViewById(R.id.check_robot_float);
        setCheckRobotView();


        if ("schedule".equals(type) && !"robot".equals(processType)) {
            chat_tv_convert.setVisibility(View.GONE);
        }
        //可以删除的逻辑
        if (!IMChatManager.getInstance().isShowTransferBtn()) {
            chat_tv_convert.setVisibility(View.GONE);
            LogUtils.dTag("handleMessage==", "可以删除的逻辑-隐藏按钮");
        }

        if (TextUtils.isEmpty(left_text)) {
            chat_tv_back.setText(getString(R.string.ykfsdk_logout));
        } else {
            chat_tv_back.setText(left_text);
        }

        //是否显示emoji表情-后台配置管理
        boolean show_emoji = GlobalSetDao.getInstance().getGlobalSet().showEmojiButton;
        if (show_emoji) {
            mChatEmojiNormal.setVisibility(View.VISIBLE);
        } else {
            mChatEmojiNormal.setVisibility(View.GONE);
        }

        serviceShowVoice = GlobalSetDao.getInstance().getGlobalSet().showVoiceButton;
        voiceAndTextBtnVisibility(serviceShowVoice, true, false);


        mChatInput.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                        !IMChatManager.getInstance().getSocketConnect()) {
//                    Toast.makeText(getApplicationContext(), "检测到您网络异常啦~", Toast.LENGTH_SHORT).show();
                    LogUtils.aTag("第五个地方break");
                    startReStartDialog3();
                    return;
                }

                if (IMChatManager.getInstance().isFinishWhenReConnect) {
                    // beginSession();
                    startReStartDialog();
                } else {
                    mChatEmojiNormal.setVisibility(View.VISIBLE);
                    mChatEmojiNormal.setSelected(false);
                }
            }
        });

        // 监听文字框

        mChatInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    mChatMore.setVisibility(View.GONE);
                    mChatSend.setVisibility(View.VISIBLE);
                } else {
                    mChatMore.setVisibility(View.VISIBLE);
                    mChatSend.setVisibility(View.GONE);
                }
                // 延迟
                if (delaySendTask != null) {
                    delaySendTask.setCanceled(true);
                    handler.removeCallbacks(delaySendTask);
                } else {
                    delaySendTask = new DelaySendTask();
                }
                delaySendTask.setCanceled(false);
                delaySendTask.setText(s.toString());
                handler.postDelayed(delaySendTask, 1000);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (!TextUtils.isEmpty(s)) {
                    if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status) {
                        if (IMChat.getInstance().getLianXiangOn()) {
                            //如果开启联想功能,xbot机器人
                            HttpManager.queryLianXiangData(InfoDao.getInstance().getConnectionId(),
                                    IMChat.getInstance().getRobotType(), s.toString(), new GetLianXiangDataResponeHandler());
                        }
                    } else {
                        if (IMChat.getInstance().isHumanLianXiangOn()) {
                            //人工状态下是否联想
                            HttpManager.queryLianXiangData(InfoDao.getInstance().getConnectionId(),
                                    IMChat.getInstance().getRobotType(), s.toString(), new GetLianXiangDataResponeHandler());
                        }
                    }
                } else {
                    ll_hintView.setVisibility(View.GONE);
                }
            }
        });


        header = View.inflate(this, R.layout.ykfsdk_kf_chatlist_header, null);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        header.measure(w, h);


        //点击列表收起键盘
        mChatList.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //用户点击列表的时候，如果显示面板，则需要隐藏
                return mHelper != null && mHelper.hookSystemBackByPanelSwitcher();
            }
        });

    }

    /**
     * 开启回话
     */
    private void beginChatSession() {
        if ("peedId".equals(type)) {
            beginSession(peerId);
        }
        if ("schedule".equals(type)) {
            beginScheduleSession(scheduleId, processId, currentNodeId, entranceId);
        }
    }


    /**
     * 注册广播
     */
    private void registerRec() {
        IntentFilter intentFilter = new IntentFilter("com.m7.imkfsdk.msgreceiver");
        msgReceiver = new MsgReceiver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(msgReceiver, intentFilter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(msgReceiver, intentFilter);
        }

        IntentFilter kefuIntentFilter = new IntentFilter();
        kefuIntentFilter.addAction(IMChatManager.ROBOT_ACTION);
        kefuIntentFilter.addAction(IMChatManager.ONLINE_ACTION);
        kefuIntentFilter.addAction(IMChatManager.OFFLINE_ACTION);
        kefuIntentFilter.addAction(IMChatManager.CLIAM_ACTION);
        kefuIntentFilter.addAction(IMChatManager.INVESTIGATE_ACTION);
        kefuIntentFilter.addAction(IMChatManager.QUEUENUM_ACTION);
        kefuIntentFilter.addAction(IMChatManager.LEAVEMSG_ACTION);
        kefuIntentFilter.addAction(IMChatManager.FINISH_ACTION);
        kefuIntentFilter.addAction(IMChatManager.USERINFO_ACTION);
        kefuIntentFilter.addAction(IMChatManager.VIPASSIGNFAIL_ACTION);
        kefuIntentFilter.addAction(IMChatManager.CANCEL_ROBOT_ACCESS_ACTION);
        kefuIntentFilter.addAction(IMChatManager.WITHDRAW_ACTION);
        kefuIntentFilter.addAction(IMChatManager.WRITING_ACTION);
        kefuIntentFilter.addAction(IMChatManager.ROBOT_SWITCH_ACTION);
        kefuIntentFilter.addAction(IMChatManager.TCP_ACTION);
        kefuIntentFilter.addAction(IMChatManager.ZXMSG_ACTION);

        kefuIntentFilter.addAction(IMChatManager.VIDEO_INVITED_ACTION);
        kefuIntentFilter.addAction(IMChatManager.VIDEO_ACCEPT_ACTION);
        kefuIntentFilter.addAction(IMChatManager.VIDEO_PC_HANGUP_ACTION);
        kefuIntentFilter.addAction(IMChatManager.VIDEO_PC_CANCEL_ACTION);
        kefuIntentFilter.addAction(IMChatManager.VIDEO_REFUSE_ACTION);

        kefuIntentFilter.addAction(IMChatManager.STOP_TIMER);
        kefuIntentFilter.addAction(IMChatManager.START_TIMER);
        keFuStatusReceiver = new KeFuStatusReceiver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(keFuStatusReceiver, kefuIntentFilter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(keFuStatusReceiver, kefuIntentFilter);
        }
    }

    /**
     * 专属坐席转接失败
     * 弹窗可选择是否接受其他坐席
     */
    private void showVipAssignFailDialog() {
        new AlertDialog.Builder(this, R.style.ykfsdk_Dialog_style).setTitle(R.string.ykfsdk_warm_prompt)
                .setMessage(R.string.ykfsdk_doyouneedother)
                .setPositiveButton(R.string.ykfsdk_need, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        IMChatManager.getInstance().acceptOtherAgent(peerId, new AcceptOtherAgentListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ChatActivity.this, getString(R.string.ykfsdk_ykf_notify_otheragent), Toast.LENGTH_SHORT).show();
                                //专属坐席不在线，重新请求会话
                                if (type.equals("peedId")) {
                                    HttpManager.beginNewVipOfflineSession(InfoDao.getInstance().getConnectionId()
                                            , IMChatManager.getInstance().getIsNewVisitor()
                                            , peerId
                                            , ""
                                            , getResponseListener());
                                }
                                if (type.equals("schedule")) {
                                    HttpManager.beginNewVipOfflineScheduleChatSession(InfoDao.getInstance().getConnectionId()
                                            , IMChatManager.getInstance().getIsNewVisitor()
                                            , scheduleId
                                            , processId
                                            , currentNodeId
                                            , entranceId
                                            , ""
                                            , getResponseListener());
                                }

                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(ChatActivity.this, getString(R.string.ykfsdk_ykf_notify_otheragent_fail), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.ykfsdk_noneed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        leaveChatActivity();
                    }
                })
                .setCancelable(false)
                .create()
                .show();

    }

    /**
     * 显示排队数
     */
    private void showQueueNumLabel(String queueNum) {
        if (Integer.parseInt(queueNum) > 0) {
            chat_queue_ll.setVisibility(View.VISIBLE);
            try {
                String queueNumText = GlobalSetDao.getInstance().getGlobalSet().queueNumText;
                int beginIndex = queueNumText.indexOf("{");
                int endIndex = queueNumText.indexOf("}");
                String newString = queueNumText.replace(queueNumText.substring(beginIndex, endIndex + 1), queueNum);
                SpannableString ss = new SpannableString(newString);
                ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ykfsdk_ykf_color_default)), beginIndex,
                        beginIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                chat_queue_tv.setText(ss);
            } catch (Exception e) {

                chat_queue_tv.setText(getResources().getText(R.string.ykfsdk_numbers01) + queueNum + getResources().getText(R.string.ykfsdk_number02));
            }
        } else {
            chat_queue_ll.setVisibility(View.GONE);
        }
    }


    /**
     * 设置全局配置
     */
    private void setGlobalConfig() {
        final GlobalSet globalSet = GlobalSetDao.getInstance().getGlobalSet();
        if (globalSet != null) {
            isCustomerRead = globalSet.isCustomerRead;
            sdkTypeNoticeFlag = globalSet.sdkTypeNoticeFlag;
            break_tips = globalSet.break_tips;//断开前提示内容
            break_end = globalSet.finishText;//断开提示语

            changeRobotVisiable();

        }
    }

    /**
     * xbot发送文本消息
     * 如果当前不是机器人状态，则跳出，不发送
     *
     * @param msgStr
     */
    public void sendXbotTextMsg(String msgStr) {
        if (IMChatManager.getInstance().getYkfChatStatusEnum() != YKFChatStatusEnum.KF_Robot_Status) {
            ToastUtils.showShort(this, getString(R.string.ykfsdk_ykf_not_robot_send));
            return;
        }
        sendTextMsg(msgStr);
    }

    /**
     * 发送文本消息
     *
     * @param msgStr 最终需要发送的string
     */
    public void sendTextMsg(String msgStr) {
        //如果会话已经结束了，就不让点击常见问题了
        if (conversationOver) {
            startReStartDialog();
            return;
        }

        //访客没发过消息，显示评论按钮。 2 访客发过消息后，就不再校验了。
        if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status && !hasSendRobotMsg) {
            hasSendRobotMsg = true;
            setChatInviteBtn();
        }
        if (IMChatManager.getInstance().getYkfChatStatusEnum() != YKFChatStatusEnum.KF_Robot_Status && !hasSendPersonMsg) {
            hasSendPersonMsg = true;
            setChatInviteBtn();
        }
        FromToMessage fromToMessage = IMMessage.createTxtMessage(msgStr);
        //界面显示
        sendSingleMessage(fromToMessage);
    }


    /**
     * 访客未说话倒计时结束 提示操作
     * 注意:此时sdk内部会断开ws ,该弹窗为不可取消的设置,点击即离开页面.继续咨询请重新初始化进入逻辑
     *
     * @param finishText
     */
    private void timerClose(String finishText) {
        TimerQuitDialog timerQuitDialog = new TimerQuitDialog.Builder().setCloseText(finishText).setSubmitListener(new TimerQuitListener() {
            @Override
            public void closeSession() {
                checkInvestigateBack();
            }
        }).build();
        if (!isFinishing()) {
            timerQuitDialog.show(getSupportFragmentManager(), "timerQuitDialog");
        }
    }


    /**
     * 查询数据库更新页面
     */
    public void updateMessage() {
        pageSize = 2;
        fromToMessage = (ArrayList<FromToMessage>) IMChatManager.getInstance().getMessages(1);

        Iterator<FromToMessage> iterator = fromToMessage.iterator();
        while (iterator.hasNext()) {
            FromToMessage msg = iterator.next();

            if (FromToMessage.MSG_TYPE_NEW_CARD.equals(msg.msgType)) {
                //不显示newcardinfo配置自动发送类型的样式
                Type token = new TypeToken<NewCardInfo>() {
                }.getType();
                final NewCardInfo newCardInfo = new Gson().fromJson(msg.newCardInfo, token);
                if ("true".equals(newCardInfo.getAutoCardSend())) {
//                    fromToMessage.remove(msg);
                    iterator.remove();   //注意这个地方
                }
            }
        }


        for (FromToMessage msg : fromToMessage) {
            if (FromToMessage.MSG_TYPE_NEW_CARD.equals(msg.msgType)) {
                //不显示newcardinfo配置自动发送类型的样式
                Type token = new TypeToken<NewCardInfo>() {
                }.getType();
                final NewCardInfo newCardInfo = new Gson().fromJson(msg.newCardInfo, token);
                if ("true".equals(newCardInfo.getAutoCardSend())) {
                    fromToMessage.remove(msg);
                }
            }
        }


        descFromToMessage.clear();
        Collections.reverse(fromToMessage);
        descFromToMessage.addAll(fromToMessage);    //  第一步就是这里添加，添加完了之后直接就有值了。
        // 是否有数据,显示/隐藏刷新头
        if (IMChatManager.getInstance().isReachEndMessage(descFromToMessage.size())) {
            mChatList.dismiss();
        } else {
            mChatList.visible();
        }

        chatAdapter.notifyDataSetChanged();
        scrollToBottom();

        //刷新标题
        mOtherName.setText(titleName);
        if (handler.hasMessages(HANDLER_NO_WRITING)) {
            handler.removeMessages(HANDLER_NO_WRITING);
        }
        //xbot收到表单消息自动弹出
        showXbotfrom();


        //如果最后一个消息是左侧并且不是机器人或者系统的，就重新计时,不是的话就停止
//        int last = descFromToMessage.size() - 1;
//        if (last <= 0) {
//            return;
//        }
//        if (descFromToMessage.get(last).userType.equals("1")) {
//            String user = descFromToMessage.get(last).user;
//            if (!"robot".equals(user) && !"system".equals(user)) {
//                if (!descFromToMessage.get(last).msgType.equals(FromToMessage.MSG_TYPE_BREAK_TIP)) {
//                    IMChatManager.getInstance().resetBreakTimer();
//                }
//            } else {
//                IMChatManager.getInstance().cancelBreakTimer();
//            }
//        }
    }

    /**
     * 会话被结束掉了，点击输入框，重新开始会话
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        updateMessage();
        //小陌机器人评价是否完成
        robotEvaluationFinish = false;
        conversationOver = false;

        //是否和机器人发送过消息
        hasSendRobotMsg = false;
        hasSendPersonMsg = false;
        JZflag = true;

        if (ll_bottom_intercept != null) {
            ll_bottom_intercept.setVisibility(View.GONE);
        }

        titleName = getString(R.string.ykfsdk_wait_link);

        getIntentData(intent);

        beginChatSession();//开启回话
    }

    public void getIntentData(Intent intent) {
        //获取技能组id
        if (intent.getStringExtra("PeerId") != null) {
            peerId = intent.getStringExtra("PeerId");
        }
        if (intent.getStringExtra("type") != null) {
            type = intent.getStringExtra("type");
        }
        if (intent.getStringExtra("scheduleId") != null) {
            scheduleId = intent.getStringExtra("scheduleId");
        }
        if (intent.getStringExtra("processId") != null) {
            processId = intent.getStringExtra("processId");
        }
        if (intent.getStringExtra("currentNodeId") != null) {
            currentNodeId = intent.getStringExtra("currentNodeId");
        }
        if (intent.getStringExtra("entranceId") != null) {
            entranceId = intent.getStringExtra("entranceId");
        }
        if (intent.getStringExtra("processType") != null) {
            processType = intent.getStringExtra("processType");
        }

        IMChatManager.getInstance().isFinishWhenReConnect = false;
    }

    /**
     * 分页加载更多
     */
    public void JZMoreMessage() {
        fromToMessage = (ArrayList<FromToMessage>) IMChatManager.getInstance().getMessages(pageSize);
        if (fromToMessage.size() > 0) {
            Collections.reverse(fromToMessage);
            scrollTop(fromToMessage);
        } else {
            mChatList.onRefreshFinished();
            JZflag = true;
        }

    }

    private void scrollTop(List<FromToMessage> list) {
        descFromToMessage.addAll(0, list);
        chatAdapter.setMessageList(descFromToMessage);
        chatAdapter.notifyDataSetChanged();
        int listTop = mChatList.getTop();
        try {
            mChatList.onRefreshFinished();
            JZflag = true;
            if (list.size() > 0) {
                pageSize++;
            }
            mChatList.setSelectionFromTop(list.size(), listTop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrollToBottom() {
        mChatList.post(new Runnable() {
            @Override
            public void run() {
                mChatList.setSelection(mChatList.getBottom());
            }
        });
    }


    /**
     * 延迟调用正在输入接口
     */
    private class DelaySendTask implements Runnable {
        private String mText;
        private boolean canceled = false;

        public DelaySendTask setText(String mText) {
            this.mText = mText;
            return this;
        }

        @Override
        public void run() {
            if (canceled) {
                return;
            }
            sendTypingStatus(mText);
        }


        public DelaySendTask setCanceled(boolean canceled) {
            this.canceled = canceled;
            return this;
        }
    }

    /**
     * 注册按钮监听方法
     */
    public void registerListener() {
        mChatSend.setOnClickListener(this);
        chat_tv_back.setOnClickListener(this);
        mChatSetModeVoice.setOnClickListener(this);
        mChatSetModeKeyboard.setOnClickListener(this);
        mChatList.setOnRefreshListener(this);
        chat_tv_convert.setOnClickListener(this);
        ivDeleteEmoji.setOnClickListener(this);
        rl_bottom.setOnClickListener(this);
        ll_bottom_intercept.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.chat_tv_back) {//断开长连接
            handleLogOutOrBackPressed();
        } else if (id == R.id.chat_tv_convert) {
            if (MoorAntiShakeUtils.getInstance().check()) {
                return;
            }
            TransferAgent transferAgent = new TransferAgent();
            transferAgent.type = "14";
            onEventMainThread(transferAgent);
        } else if (id == R.id.chat_send) {
            String txt = mChatInput.getText().toString();
            //发送的时候校验一次
            if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) ||
                    !IMChatManager.getInstance().getSocketConnect()) {
                Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                LogUtils.aTag("第四个地方break");
                startReStartDialog3();
                return;
            }
            if (IMChatManager.getInstance().isFinishWhenReConnect) {
                startReStartDialog();//并且弹框提示开始新会话
            } else {
                ll_hintView.setVisibility(View.GONE);
                sendTextMsg(txt);
            }
        } else if (id == R.id.chat_set_mode_voice) {
            PermissionXUtil.checkPermission(ChatActivity.this, new OnRequestCallback() {
                @Override
                public void requestSuccess() {
                    if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                            !IMChatManager.getInstance().getSocketConnect()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                        startReStartDialog3();
                        return;
                    }
                    showVoice();
                }
            }, PermissionConstants.RECORD_AUDIO);


        } else if (id == R.id.chat_set_mode_keyboard) {
            mChatInput.requestFocus(100);
            mChatEdittextLayout.setVisibility(View.VISIBLE);
            mRecorderButton.setVisibility(View.GONE);
            voiceAndTextBtnVisibility(serviceShowVoice, true, false);

            if (TextUtils.isEmpty(mChatInput.getText())) {
                mChatMore.setVisibility(View.VISIBLE);
                mChatSend.setVisibility(View.GONE);
            } else {
                mChatMore.setVisibility(View.GONE);
                mChatSend.setVisibility(View.VISIBLE);
            }

        } else if (id == R.id.iv_delete_emoji) {
            int keyCode = KeyEvent.KEYCODE_DEL;
            KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
            KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
            mChatInput.onKeyDown(keyCode, keyEventDown);
            mChatInput.onKeyUp(keyCode, keyEventUp);
        } else if (id == R.id.rl_bottom) {
        } else if (id == R.id.ll_bottom_intercept) {
            if (conversationOver) {
                startReStartDialog();
            } else {
                ll_bottom_intercept.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 点击返回按钮
     * 处理注销和返回键逻辑
     */
    private void handleLogOutOrBackPressed() {
        if (YKFCallHelper.existVideo()) {
            final CommonBottomSheetDialog dialog = CommonBottomSheetDialog.instance(getString(R.string.ykfsdk_ykf_dialog_exist_video)
                    , getString(R.string.ykfsdk_ykf_determine)
                    , getString(R.string.ykfsdk_cancel));
            dialog.setListener(new CommonBottomSheetDialog.OnClickListener() {
                @Override
                public void onClickPositive() {
                    dialog.close(false);
                    YKFCallHelper.leave(false);
                    dealLogOut();
                }

                @Override
                public void onClickNegative() {
                    dialog.close(false);
                }
            });
            dialog.show(getSupportFragmentManager(), "");
        } else {
            dealLogOut();
        }

    }


    /**
     * 准备离开会话页面
     */
    private void dealLogOut() {
        //检查当前是否正在排队，给出提示
        if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Queue_Status) {
            QuitQueueDialog quitQueueDialog = new QuitQueueDialog.Builder().setSubmitListener(new QuitQueueDailogListener() {
                @Override
                public void clickCancle() {

                }

                @Override
                public void clickQuit() {
                    checkInvestigateBack();
                }
            }).build();
            if (!isFinishing()) {
                quitQueueDialog.show(getSupportFragmentManager(), "QuitQueueDialog");
            }
        } else {
            checkInvestigateBack();
        }
    }

    /**
     * 检查满意度等规则，在执行离开页面
     */
    private void checkInvestigateBack() {
        //人工，开启了评价,坐席发过消息、会话存在，访客发过消息，没有评价过,配置了满意度列表,pc没有结束会话,是否弹出评价框
        NotAllowCustomerCloseCsr = MoorSPUtils.getInstance().getBoolean(YKFConstants.NOT_ALLOW_CUSTOMERCLOSECSR, false);

        if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Claim_Status && IMChatManager.getInstance().isInvestigateOn()
                && convesationIsLive && hasSendPersonMsg
                && isZXResply && isInvestigate && hasSet && !conversationOver && !NotAllowCustomerCloseCsr) {
            showInvestigateDialog(true, YKFConstants.INVESTIGATE_TYPE_IN, null, false, false);
        } else {
            leaveChatActivity();
        }
    }


    private void showVoice() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
        }

        mChatEdittextLayout.setVisibility(View.GONE);
        mChatSend.setVisibility(View.GONE);
        mChatMore.setVisibility(View.VISIBLE);
        mRecorderButton.setVisibility(View.VISIBLE);
        mChatEmojiNormal.setVisibility(View.VISIBLE);

        voiceAndTextBtnVisibility(serviceShowVoice, false, true);
    }

    /**
     * 点击分组常见问题的查看更多
     *
     * @param message
     */
    public void handleTab_QusetionMoreClick(String title, ArrayList<String> message) {
        if (message != null) {
            if (message.size() > 0) {
                final BottomTabQuestionDialog bottomTabQuestionDialog =
                        BottomTabQuestionDialog.init(title, message);
                bottomTabQuestionDialog.show(getSupportFragmentManager(), "");
                bottomTabQuestionDialog.setonQuestionClickListener(new BottomTabQuestionDialog.onQuestionClickListener() {
                    @Override
                    public void OnItemClick(String s) {
                        sendXbotTextMsg(s);
                        bottomTabQuestionDialog.close(true);
                    }
                });
            }
        }
    }

    /**
     * 点击快捷按钮
     */
    public void handle_QuickItemClick(MoorFastBtnBean fastBtnBean) {
        if (fastBtnBean != null) {
            if (fastBtnBean.getButton_type() == 2) {
                if (!TextUtils.isEmpty(fastBtnBean.getContent())) {
                    Intent forumIntent = new Intent(this, MoorWebCenter.class);
                    forumIntent.putExtra("OpenUrl", fastBtnBean.getContent());
                    forumIntent.putExtra("titleName", fastBtnBean.getName());
                    startActivity(forumIntent);
                }
            } else {
                if (!TextUtils.isEmpty(fastBtnBean.getContent())) {
                    sendXbotTextMsg(fastBtnBean.getContent());
                }
            }
        }
    }

    /**
     * 点击列表中的立即评价
     *
     * @param message
     */
    public void dealCancelInvestigateClick(FromToMessage message) {
        //需要重新查询数据库，来获取最新值 hasEvaluated
        message = MessageDao.getInstance().getMessageById(message._id);
        if (!isInvestigate && NullUtil.checkNull(message.chatId).equals(chatId)) {
            ToastUtils.showShort(this, getString(R.string.ykfsdk_ykf_has_been_evaluated));
            return;
        }
        if (message.hasEvaluated) {
            ToastUtils.showShort(this, getString(R.string.ykfsdk_ykf_has_been_evaluated));
            return;
        }
        boolean CSRAging = MoorSPUtils.getInstance().getBoolean(YKFConstants.CSRAGING, false);
        if (!hasSet) {
            ToastUtils.showShort(this, getString(R.string.ykfsdk_ykf_dont_evaluated));
            return;
        }
        if (CSRAging && !TextUtils.isEmpty(message.timeStamp)) {
            checkImCsrTimeout(YKFConstants.INVESTIGATE_TYPE_OUT, message, true, message.timeOut, message.timeStamp);
        } else {
            showInvestigateDialog(false, YKFConstants.INVESTIGATE_TYPE_OUT, message, true, false);
        }

    }

    /**
     * 打开评价对话框
     */
    public void openInvestigateDialog(boolean isFromButton, String way, FromToMessage message, boolean fromList) {
        //判断是否是机器人
        if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status) {
            openRobotInvestigateDialog();
        } else {//人工
            boolean CSRAging = MoorSPUtils.getInstance().getBoolean(YKFConstants.CSRAGING, false);
            String timestamp = MoorSPUtils.getInstance().getString(YKFConstants.SERVERTIMESTAMP, "");
            if (INVITATION_INVESTIGATE && isFromButton && CSRAging && !TextUtils.isEmpty(timestamp)) {
                String timeout = MoorSPUtils.getInstance().getString(YKFConstants.TIMEOUT, "");
                checkImCsrTimeout(way, message, fromList, timeout, timestamp);
            } else {
                showInvestigateDialog(false, way, message, fromList, false);
            }
        }

    }

    /**
     * 判断评价是否超时
     */
    private void checkImCsrTimeout(final String way, final FromToMessage message, final boolean fromList, String timeout, String timestamp) {
        if (loadingDialog != null) {
            loadingDialog.show(this.getFragmentManager(), "");
        }
        IMChatManager.getInstance().checkImCsrTimeout(timeout, timestamp, new onResponseListener() {
            @Override
            public void onSuccess() {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                showInvestigateDialog(false, way, message, fromList, false);
            }

            @Override
            public void onFailed() {
                ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_httpfun_error));
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onTimeOut() {
                ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_evaluation_timeout));
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        });
    }


    /**
     * 打开评价框
     * 1.点击注销或者返回键触发
     * 2.主动点击评价按钮
     *
     * @param logOutOrBackPressed true：点击注销或者返回键触发
     * @param way                 in:访客主动评价，out：坐席推送或者是系统（点击注销或者是返回键）评价
     * @param message
     */
    private void showInvestigateDialog(final boolean logOutOrBackPressed, final String way, final FromToMessage message, final boolean fromList, final boolean isXbot) {
        if (!hasSet) {
            ToastUtils.showShort(this, getString(R.string.ykfsdk_ykf_dont_evaluated));
            return;
        }
        if (dialog != null && dialog.getDialog() != null && dialog.getDialog().isShowing()) {
            return;
        }
        final String sessionId;
        if (message == null) {
            sessionId = "";
        } else {
            sessionId = message.chatId;
        }

        String chatType = "";

        if (isXbot) {
            chatType = "xbot";
        } else {
            chatType = "";
        }


        dialog = new InvestigateDialog.Builder()
                .setType(way)
                .setConnectionId(InfoDao.getInstance().getConnectionId())
                .setSessionId(sessionId)
                .setChatType(chatType)
                .setSubmitListener(new SubmitPingjiaListener() {
                    @Override
                    public void OnSubmitSuccess(String content, String finalSatifyThank) {
                        ToastUtils.showShort(ChatActivity.this, finalSatifyThank);
                        FromToMessage cancelMessage = IMMessage.createInvestigateSuccessMessage(content);
                        MessageDao.getInstance().insertSendMsgsToDao(cancelMessage);
                        MessageDao.getInstance().updateHasEvaluatedByChatId(sessionId);
                        if (isXbot) {
                            robotEvaluationFinish = true;
//                            ToastUtils.showLong(ChatActivity.this, getString(R.string.ykfsdk_ykf_robot_evaluation_ok));
                            setChatInviteBtn();
                        }
                        if (logOutOrBackPressed) {
                            //评价成功
                            leaveChatActivity();
                        } else {
                            if (!fromList) {
                                //评价成功需要标记成已评价，按钮隐藏
                                if (!isXbot) {
                                    isInvestigate = false;//已经评价过了
                                }

                            } else {
                                if (!isXbot) {
                                    if (isInvestigate && NullUtil.checkNull(message.chatId).equals(chatId)) {
                                        isInvestigate = false;//已经评价过了
                                    }
                                }
                            }
                            setChatInviteBtn();
                            //界面显示
                            descFromToMessage.add(cancelMessage);
                            chatAdapter.notifyDataSetChanged();
                            scrollToBottom();
                        }


                    }

                    @Override
                    public void OnSubmitCancle() {
                        if (isXbot) {
                            robotEvaluationFinish = false;
//                            ToastUtils.showLong(ChatActivity.this, getString(R.string.ykfsdk_ykf_robot_evaluation_fail));
                        }

                        if (logOutOrBackPressed) {
                            IMChatManager.getInstance().getServerTime(new onResponseListener() {
                                @Override
                                public void onSuccess() {
                                    if (way.equals(YKFConstants.INVESTIGATE_TYPE_IN) && !fromList) {
                                        //是坐席邀请的评价类型时：本地新增一个点击评价的类型，点击时触发正常弹出评价框的逻辑
                                        FromToMessage cancelMessage = IMMessage.createInvestigateCancelMessage(chatId
                                                , MoorSPUtils.getInstance().getString(YKFConstants.TIMEOUT, "")
                                                , MoorSPUtils.getInstance().getString(YKFConstants.SERVERTIMESTAMP, ""));
                                        MessageDao.getInstance().insertSendMsgsToDao(cancelMessage);
                                        leaveChatActivity();
                                    }
                                }

                                @Override
                                public void onFailed() {

                                }

                                @Override
                                public void onTimeOut() {

                                }
                            });

                        } else if (INVITATION_INVESTIGATE) {
                            IMChatManager.getInstance().getServerTime(new onResponseListener() {
                                @Override
                                public void onSuccess() {
                                    if (way.equals(YKFConstants.INVESTIGATE_TYPE_OUT) && !fromList) {
                                        //是坐席邀请的评价类型时：本地新增一个点击评价的类型，点击时触发正常弹出评价框的逻辑
                                        FromToMessage cancelMessage = IMMessage.createInvestigateCancelMessage(chatId
                                                , MoorSPUtils.getInstance().getString(YKFConstants.TIMEOUT, "")
                                                , MoorSPUtils.getInstance().getString(YKFConstants.SERVERTIMESTAMP, ""));
                                        MessageDao.getInstance().insertSendMsgsToDao(cancelMessage);
                                        //界面显示
                                        descFromToMessage.add(cancelMessage);
                                        chatAdapter.notifyDataSetChanged();
                                        scrollToBottom();
                                    }
                                }

                                @Override
                                public void onFailed() {
                                    if (way.equals(YKFConstants.INVESTIGATE_TYPE_OUT) && !fromList) {
                                        //是坐席邀请的评价类型时：本地新增一个点击评价的类型，点击时触发正常弹出评价框的逻辑
                                        FromToMessage cancelMessage = IMMessage.createInvestigateCancelMessage(chatId
                                                , MoorSPUtils.getInstance().getString(YKFConstants.TIMEOUT, "")
                                                , System.currentTimeMillis() + "");
                                        MessageDao.getInstance().insertSendMsgsToDao(cancelMessage);
                                        //界面显示
                                        descFromToMessage.add(cancelMessage);
                                        chatAdapter.notifyDataSetChanged();
                                        scrollToBottom();
                                    }

                                }

                                @Override
                                public void onTimeOut() {

                                }
                            });

                        }


                    }

                    @Override
                    public void OnSubmitFailed() {
                        if (isXbot) {
                            robotEvaluationFinish = false;
                            ToastUtils.showLong(ChatActivity.this, getString(R.string.ykfsdk_ykf_robot_evaluation_fail));
                        }

                        if (logOutOrBackPressed) {
                            leaveChatActivity();
                        } else {
                            if (!isXbot) {
                                isInvestigate = true;
                            }
                        }


                    }
                })
                .build();
        dialog.show(getFragmentManager(), "InvestigateDialog");
    }

    /**
     * 取消评价
     * 1.点击注销/坐席结束会话后若  弹出评价框，点击取消,退出页面；ok
     * 2.主动点击评价按钮/坐席推送的评价，点击取消,请求接口;ok
     */
    private void getInvestigateTime() {
        IMChatManager.getInstance().getServerTime(null);
    }

    /**
     * xbot机器人评价
     */
    private void openRobotInvestigateDialog() {
        //先拉xbot取满意度评价数据
        HttpManager.sdkGetXbotCsrInfo(new HttpResponseListener() {
            @Override
            public void onSuccess(String responseStr) {
                LogUtils.eTag("sdkGetXbotCsrInfo", responseStr);

                try {
                    JSONObject object = new JSONObject(responseStr);
                    if (object != null) {
                        JSONObject data = object.optJSONObject("data");
                        if (data != null) {
                            MoorSPUtils.getInstance().put(YKFConstants.XBOT_SATISFY_COMMENT, data.optString("satisfyComment"), true);
                            MoorSPUtils.getInstance().put(YKFConstants.XBOT_SATISFYTITLE, data.optString("satisfyTitle"), true);
                            MoorSPUtils.getInstance().put(YKFConstants.XBOT_SATISFYTHANK, data.optString("satisfyThank"), true);
                            MoorSPUtils.getInstance().put(YKFConstants.XBOT_INVESTIGATE, data.optJSONArray("options").toString(), true);

                            showInvestigateDialog(false, YKFConstants.INVESTIGATE_TYPE_IN, null, false, true);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MoorDemoConstants.PICK_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    String realPath = PickUtils.getPath(ChatActivity.this, uri);
                    Log.d("发送图片消息了", "图片的本地路径是：" + realPath);
                    createAndSendImgMsg(realPath);
                } else {
                    Log.e(tag, "从相册获取图片失败");
                }
            }
        } else if (requestCode == MoorDemoConstants.PICK_FILE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String path = PickUtils.getPath(ChatActivity.this, uri);
            if (!NullUtil.checkNULL(path)) {
                Toast.makeText(ChatActivity.this, getString(R.string.ykfsdk_ykf_not_support_file), Toast.LENGTH_SHORT).show();
                return;
            }
            File file = new File(path);
            String fileSizeStr = "";
            if (file.exists()) {
                long fileSize = file.length();
                if ((fileSize / 1024 / 1024) > 200.0) {
                    //大于200M不能上传
                    Toast.makeText(ChatActivity.this, getString(R.string.ykfsdk_sendfiletoobig) + "200MB", Toast.LENGTH_SHORT).show();
                } else {
                    fileSizeStr = FileUtils.formatFileLength(fileSize);
                    String fileName = path.substring(path.lastIndexOf("/") + 1);
                    FromToMessage fromToMessage = null;

                    String lowerCase = fileName.toLowerCase();

                    GlobalSet globalSet = GlobalSetDao.getInstance().getGlobalSet();
                    if (globalSet != null) {
                        if (!TextUtils.isEmpty(globalSet.globalUploadBlackList)) {
                            String[] blackList = globalSet.globalUploadBlackList.split(",");
                            for (String s : blackList) {
                                if (lowerCase.endsWith(s)) {
                                    ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_black_upload_type));
                                    return;
                                }
                            }
                        }

                        if (!checkWhiteList(globalSet, lowerCase)) {
                            ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_not_upload_type));
                            return;
                        }
                    }


                    if (MoorUtils.fileIsImage(fileName)) {
                        //发送图片文件
                        fromToMessage = IMMessage.createImageMessage(path);
                    } else if (MoorUtils.fileIsVideo(fileName)) {
                        //发送视频文件
                        fromToMessage = IMMessage.createFileIsVideoMessage(path, fileName, fileSizeStr, getString(R.string.ykfsdk_ykf_has_been_upload_tips));
                    } else {
                        //发送文件
                        fromToMessage = IMMessage.createFileMessage(path, fileName, fileSizeStr, getString(R.string.ykfsdk_ykf_has_been_upload_tips));
                    }

                    ArrayList fromTomsgs = new ArrayList<FromToMessage>();
                    fromTomsgs.add(fromToMessage);
                    descFromToMessage.addAll(fromTomsgs);
                    chatAdapter.notifyDataSetChanged();
                    scrollToBottom();
                    sendMsgToServer(fromToMessage);
                }
            }
        } else if (requestCode == MoorDemoConstants.CAMERA_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //发送图片
                dealCameraCallback();
            } else {
                //删除uri
                MoorTakePicturesHelper.getInstance().deleteUri(ChatActivity.this);
            }
        }

    }


    /**
     * 检查白名单文件类型
     *
     * @param globalSet
     * @param lowerCase
     * @return
     */
    private boolean checkWhiteList(GlobalSet globalSet, String lowerCase) {
        if (globalSet.uploadWhite) {
            if (!TextUtils.isEmpty(globalSet.uploadWhiteList)) {
                String[] whiteList = globalSet.uploadWhiteList.split(",");
                for (String s : whiteList) {
                    if (lowerCase.endsWith(s)) {
                        return true;
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * 拍照返回处理
     */
    private void dealCameraCallback() {
        String realPath = "";
        if (MoorSdkVersionUtil.over29()) {
            // Android 10 使用图片uri加载
            Uri uri = MoorTakePicturesHelper.getInstance().getCameraUri();
            if (uri != null) {
                realPath = MoorFileUtils.getRealPathFromUri(ChatActivity.this, uri);
            }
        } else {
            // 使用图片路径加载
            realPath = MoorTakePicturesHelper.getInstance().getCameraImagePath();
        }
        if (TextUtils.isEmpty(realPath)) {
            return;
        }
        if (!TextUtils.isEmpty(realPath)) {
            createAndSendImgMsg(realPath);
        }
    }

    /**
     * 创建并发送图片消息
     *
     * @param realPath
     */
    private void createAndSendImgMsg(String realPath) {
        //准备发送图片消息
        FromToMessage fromToMessage = IMMessage.createImageMessage(realPath);
        ArrayList fromTomsgs = new ArrayList<FromToMessage>();
        fromTomsgs.add(fromToMessage);
        descFromToMessage.addAll(fromTomsgs);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
        sendMsgToServer(fromToMessage);
    }


    /**
     * 覆盖手机返回键
     */
    @Override
    public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        if (dialog != null && dialog.getDialog() != null && dialog.getDialog().isShowing()) {
            return;
        }
        handleLogOutOrBackPressed();
    }

    @Override
    protected void onDestroy() {

        if (mHashSet.size() > 0) {
            Iterator<String> iterator = mHashSet.iterator();
            while (iterator.hasNext()) {
                String _id = iterator.next();
                IMChatManager.getInstance().updateOrderInfo(_id, "2");
            }
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        unregisterReceiver(msgReceiver);
        unregisterReceiver(keFuStatusReceiver);
        mRecorderButton.cancelListener();
//        cancelTimer();
        IMChat.getInstance().setCancel(true);
        //删除卡片信息
        MessageDao.getInstance().delecteCardMsgs();
        MessageDao.getInstance().delecteNewCardMsgs();
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMChatManager.getInstance().chatPageResume();
        isFront = true;
        if (IMChatManager.getInstance().getApplicationAgain() == null) {
            finish();
        }
        onEventMainThread(new MsgEvent());//有新消息了，并且页面可见。消费消息
        if (WebSocketHandler.getDefault() != null) {
            LogUtils.aTag("chatActivity", "走到OnResume了：" + IMChatManager.getInstance().getSocketConnect());
            if (MoorUtils.isNetWorkConnected(this)) {
                /**
                 * 每次进入聊天页面重新校验tcp是否已经连接，未连接就重新连接一次
                 */
                if (isServiceRunning(this, "com.moor.imkf.SocketService")) {
                    if (!IMChatManager.getInstance().getSocketConnect()) {
                        EventBus.getDefault().post(new TcpBreakEvent());//重连
                    }
                } else {
                    //服务挂了
//                    startReStartDialog3();
                    reStartService();
                }
            } else {
                startReStartDialog3();
            }
        } else {
            startReStartDialog3();
        }


        YKFCallHelper.openActivity();
    }


    @Override
    protected void onPause() {
        super.onPause();
        IMChatManager.getInstance().chatPagePause();
        chatAdapter.onPause();
        isFront = false;
    }

    @Override
    public void toRefresh() {
        if (JZflag) {
            JZflag = false;
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(300);
                        handler.sendEmptyMessage(HANDLER_MSG_MORE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
    }

    /**
     * 新消息接收器,用来通知界面进行更新
     */
    class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handler.sendEmptyMessage(HANDLER_MSG);
        }
    }

    /**
     * 客服状态接收器
     */
    class KeFuStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMChatManager.ROBOT_ACTION.equals(action)) {
                //当前是机器人
                handler.sendEmptyMessage(HANDLER_ROBOT);
            } else if (IMChatManager.ONLINE_ACTION.equals(action)) {
                //当前是客服在线
                handler.sendEmptyMessage(HANDLER_ONLINE);
            } else if (IMChatManager.OFFLINE_ACTION.equals(action)) {
                //当前是客服离线
                handler.sendEmptyMessage(HANDLER_OFFNLINE);
            } else if (IMChatManager.INVESTIGATE_ACTION.equals(action)) {
                //客服发起了评价
                chatId = IMChatManager.getInstance().getMoorChatId();
                handler.sendEmptyMessage(HANDLER_INVESTIGATE);
            } else if (IMChatManager.QUEUENUM_ACTION.equals(action)) {
                //技能组排队数
                if (intent.getStringExtra(IMChatManager.QUEUENUM_ACTION) != null) {
                    String queueNum = intent.getStringExtra(IMChatManager.QUEUENUM_ACTION);
                    Message queueMsg = Message.obtain();
                    queueMsg.what = HANDLER_QUEUENUM;
                    queueMsg.obj = queueNum;
                    handler.sendMessage(queueMsg);
                }
            } else if (IMChatManager.CLIAM_ACTION.equals(action)) {
                //客服领取了会话或者转人工
                handler.sendEmptyMessage(HANDLER_CLIAM);
            } else if (IMChatManager.LEAVEMSG_ACTION.equals(action)) {
                //schedule 跳留言
                schedule_id = intent.getStringExtra(IMChatManager.CONSTANT_ID);
                schedule_topeer = intent.getStringExtra(IMChatManager.CONSTANT_TOPEER);
                handler.sendEmptyMessage(HANDLER_LEAVEMSG);
            } else if (IMChatManager.FINISH_ACTION.equals(action)) {
                //客服关闭了会话
                handler.sendEmptyMessage(HANDLER_FINISH);
            } else if (IMChatManager.USERINFO_ACTION.equals(action)) {
                //客服信息
                String type = intent.getStringExtra(IMChatManager.CONSTANT_TYPE);
                exten = intent.getStringExtra(IMChatManager.CONSTANT_EXTEN);
                userName = intent.getStringExtra(IMChatManager.CONSTANT_USERNAME);
                userIcon = intent.getStringExtra(IMChatManager.CONSTANT_USERICON);

                // 转人工
                if ("claim".equals(type)) {
                    mOtherName.setText(NullUtil.checkNull(userName) + getString(R.string.ykfsdk_seiveceforyou));
                    titleName = NullUtil.checkNull(userName) + getString(R.string.ykfsdk_seiveceforyou);
                }
                // 坐席领取了会话
                if ("activeClaim".equals(type)) {
                    mOtherName.setText(NullUtil.checkNull(userName) + getString(R.string.ykfsdk_seiveceforyou));
                    titleName = NullUtil.checkNull(userName) + getString(R.string.ykfsdk_seiveceforyou);
                }
                if ("redirect".equals(type)) {
                    mOtherName.setText(NullUtil.checkNull(userName) + getString(R.string.ykfsdk_seiveceforyou));
                    titleName = NullUtil.checkNull(userName) + getString(R.string.ykfsdk_seiveceforyou);
                }
                if ("robot".equals(type)) {
                    mOtherName.setText(NullUtil.checkNull(userName) + getString(R.string.ykfsdk_seiveceforyou));
                    titleName = NullUtil.checkNull(userName) + getString(R.string.ykfsdk_seiveceforyou);
                }
            } else if (IMChatManager.VIPASSIGNFAIL_ACTION.equals(action)) {
                //专属座席不在线
                handler.sendEmptyMessage(HANDLER_VIPASSIGNFAIL);
            } else if (IMChatManager.CANCEL_ROBOT_ACCESS_ACTION.equals(action)) {
                //人工干预
                Toast.makeText(ChatActivity.this, R.string.ykfsdk_receivepeopleaction, Toast.LENGTH_SHORT).show();
            } else if (IMChatManager.WITHDRAW_ACTION.equals(action)) {
                //消息撤回
                handler.sendEmptyMessage(HANDLER_MSG);
            } else if (IMChatManager.WRITING_ACTION.equals(action)) {
                //对方正在输入
                handler.sendEmptyMessage(HANDLER_WRITING);
                handler.sendEmptyMessageDelayed(HANDLER_NO_WRITING, 5000);
            } else if (IMChatManager.ROBOT_SWITCH_ACTION.equals(action)) {
                //robotSwitch
                String status = intent.getStringExtra(IMChatManager.CONSTANT_ROBOT_SWITCH);
                String sessionId = intent.getStringExtra(IMChatManager.CONSTANT_SESSIONID);
            } else if (IMChatManager.TCP_ACTION.equals(action)) {
                //tcp状态
                String tcpstatus = intent.getStringExtra(IMChatManager.TCPSTATUS);
//            } else if (IMChatManager.UNASSIGN_ACTION.equals(action)){
//                //开启 访客说话后再接入会话
//                chat_tv_convert.setVisibility(View.GONE);
            } else if (IMChatManager.ZXMSG_ACTION.equals(action)) { //坐席发了消息
                //如果没有回复过，执行接口校验是否回复过
                if (!isZXResply) {
                    checkConverstaion();//这个时候校验是否回复过，刷新UI
                }
            } else if (IMChatManager.ZXMSG_OLD_ACTION.equals(action)) {
                //坐席在会话发过中消息
                isZXResply = true;
                setChatInviteBtn();
            } else if (IMChatManager.VIDEO_INVITED_ACTION.equals(action)) {
                //接收到视频邀请
                String roomId = intent.getStringExtra(IMChatManager.CONSTANT_VIDEO_ROOMNAME);
                String videoType = intent.getStringExtra(IMChatManager.CONSTANT_VIDEO_VIDEO_TYPE);
                boolean video = !TextUtils.isEmpty(videoType) && videoType.equals("video");
                YKFCallInfoBean callInfoBean = new YKFCallInfoBean();
                callInfoBean.setUserName(userName)
                        .setUserIcon(userIcon)
                        .setRoomId(roomId)
                        .setVideo(video)
                        .setPassword(intent.getStringExtra(IMChatManager.CONSTANT_VIDEO_PASSWORD))
                        .setVideoType(videoType)
                        .setExten(exten);
                YKFCallHelper.receivedCall(callInfoBean);
            } else if (IMChatManager.VIDEO_PC_HANGUP_ACTION.equals(action)) {
                if (YKFCallHelper.existVideo()) {
                    //坐席挂断，直接退出
//                    KfVideoManager.getInstance().leave(true);
                    YKFCallHelper.leave(true);
                }
            } else if (IMChatManager.STOP_TIMER.equals(action)) {
//                cancelTimer();
            } else if (IMChatManager.START_TIMER.equals(action)) {
//                IMChatManager.getInstance().resetBreakTimer();
            } else if (IMChatManager.VIDEO_PC_CANCEL_ACTION.equals(action)) {
                YKFCallHelper.setInvitedIntentNull();
            }
        }
    }


    /**
     * 点击加号满意度按钮是否显示控制
     */
    private void setChatInviteBtn() {
        showOrHideInviteButton(false);
        NotAllowCustomerPushCsr = MoorSPUtils.getInstance().getBoolean(YKFConstants.NOT_ALLOWCUSTOMER_PUSH_CSR, false);
        //人工，开启了评价,坐席发过消息、会话存在，访客发过消息，没有评价过,配置了满意度列表
        if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Claim_Status && IMChatManager.getInstance().isInvestigateOn() && convesationIsLive && isZXResply &&
                isInvestigate && hasSet && hasSendPersonMsg && !NotAllowCustomerPushCsr) {
            //展示评价按钮
            showOrHideInviteButton(true);
        }
        //判断是小莫还是xbot
        GlobalSet globalSet = GlobalSetDao.getInstance().getGlobalSet();
        // 如果是机器人，并且还未评价过,并且已经发送过消息
        if (globalSet != null && IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status && !robotEvaluationFinish && hasSendRobotMsg) {
            if ("xbot".equals(globalSet.robotType)) {
                if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status && !robotEvaluationFinish && hasSendRobotMsg && IMChat.getInstance().getBotsatisfaOn()) {
                    showOrHideInviteButton(true);
                }
            } else {
                // 并且小陌机器人开启了评价
                if (IMChat.getInstance().getBotsatisfaOn()) {
                    showOrHideInviteButton(true);
                }
            }
        }

        //如果在排队
        if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Queue_Status) {
            showOrHideInviteButton(false);
        }
    }

    /**
     * 判断是否是有效会话，并且是否评价过
     *
     * @param
     */
    private void checkConverstaion() {
        //人工会话 校验会话是否有效
        HttpManager.getChatSession(new HttpResponseListener() {

            @Override
            public void onSuccess(String responseStr) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseStr);
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data != null) {//会话存在
                        convesationIsLive = true;
                        IMChatManager.getInstance().setMoorChatId(data.getString("_id"));
                        chatId = IMChatManager.getInstance().getMoorChatId();//会话id
                        if (data.has("replyMsgCount")) {
                            //代表坐席回复过
                            isZXResply = data.getInt("replyMsgCount") > 0;
                        } else {
                            isZXResply = false;
                        }
                        //校验是否已经评价过
                        HttpManager.checkIsAppraised(chatId, new HttpResponseListener() {
                            @Override
                            public void onSuccess(String responseStr) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(responseStr);
                                    isInvestigate = jsonObject1.getBoolean("isInvestigate");
                                    setChatInviteBtn();//接口都走完了,处理满意度按钮
                                } catch (JSONException e) {
                                    ToastUtils.showShort(ChatActivity.this, e.toString());
                                }
                            }

                            @Override
                            public void onFailed() {

                            }
                        });
                    } else {
                        //会话不存在
                        convesationIsLive = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed() {
                //判断是否存在会话要是返回失败了，先按照存在该会话,并且可以评论处理。 当我们评论的时候，后台会提示一个评论过了的提示语
//                convesationIsLive = true;
//                isInvestigate = false;
            }
        });
    }

    /**
     * 开启技能组会话
     *
     * @param peerId 技能组Id
     */
    private void beginSession(String peerId) {
        if (IMChatManager.getInstance().getApplicationAgain() == null) {
            return;
        }
        // 这里获取 扩展信息内容，参照文档的传参方式，
        String other_Str = IMChatManager.getInstance().getUserOtherParams();
        IMChatManager.userId = InfoDao.getInstance().getUserId();
        HttpManager.beginNewChatSession(InfoDao.getInstance().getConnectionId()
                , IMChatManager.getInstance().getIsNewVisitor()
                , peerId
                , other_Str
                , getResponseListener());
    }

    /**
     * 开启日程会话
     * *{@link #getResponseListener }
     *
     * @param scheduleId    日程Id
     * @param processId
     * @param currentNodeId
     * @param entranceId
     */
    private void beginScheduleSession(String scheduleId, String processId, String currentNodeId, String entranceId) {
        if (IMChatManager.getInstance().getApplicationAgain() == null) {
            return;
        }
        // 这里获取 扩展信息内容，参照文档的传参方式，
        String other_Str = IMChatManager.getInstance().getUserOtherParams();
        IMChatManager.userId = InfoDao.getInstance().getUserId();
        HttpManager.beginNewScheduleChatSession(InfoDao.getInstance().getConnectionId()
                , IMChatManager.getInstance().getIsNewVisitor()
                , scheduleId
                , processId
                , currentNodeId
                , entranceId
                , other_Str
                , getResponseListener());
    }

    /**
     * 开启技能组会话和日程会话 数据回调
     * * @return HttpResponseListener
     */
    private HttpResponseListener getResponseListener() {
        return new IMChatManager.BeginSessionResponse(new OnSessionBeginListener() {
            @Override
            public void onSuccess(String responseString) {
                LogUtils.aTag("开始会话", responseString);

                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                try {
                    //是否开启转人工按钮
                    boolean showTransferBtn = IMChatManager.getInstance().isShowTransferBtn();
                    chat_tv_convert.setVisibility(IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status && showTransferBtn ? View.VISIBLE : View.GONE);

                    //当前会话id
                    chatId = IMChatManager.getInstance().getMoorChatId();


                    setRobotBottomList(false);//添加底部横向滑动按钮
                    setRobotQuickBtn();//添加机器人快捷回复按钮


                    autoSendNewCard();//自动发送卡片

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_chatbegin_fail) + e);
                    finish();
                }

                //全局配置
                setGlobalConfig();
            }

            @Override
            public void onFailed(String error) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_chatbegin_fail) + error);
                finish();
            }
        });
    }

    /**
     * 技能组坐席不在线跳转留言
     */
    private void showOffLineDialog() {

        if (type.equals("schedule")) {
            return;
        }
        final GlobalSet globalSet = GlobalSetDao.getInstance().getGlobalSet();

        if (null != globalSet) {
            if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status) {
                bar_bottom.setVisibility(View.VISIBLE);
            } else {
                bar_bottom.setVisibility(View.GONE);
            }
            String alertTitle = globalSet.inviteLeavemsgTip;
            if (TextUtils.isEmpty(alertTitle)) {
                alertTitle = NullUtil.checkNull(globalSet.msg);
            }
            if ("1".equals(NullUtil.checkNull(globalSet.isLeaveMsg))) {

                new AlertDialog.Builder(this, R.style.ykfsdk_Dialog_style)
                        .setTitle(R.string.ykfsdk_warm_prompt)
                        .setMessage(alertTitle)
                        .setNegativeButton(R.string.ykfsdk_back, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                leaveChatActivity();
                            }
                        })
                        .setPositiveButton(R.string.ykfsdk_ykf_leave_msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(ChatActivity.this, OfflineMessageActicity.class);
                                intent.putExtra("PeerId", peerId);
                                intent.putExtra("leavemsgTip", NullUtil.checkNull(globalSet.leavemsgTip));
                                intent.putExtra("inviteLeavemsgTip", NullUtil.checkNull(globalSet.inviteLeavemsgTip));
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();

            } else {
                try {
                    new AlertDialog.Builder(this, R.style.ykfsdk_Dialog_style).setTitle(R.string.ykfsdk_warm_prompt)
                            .setMessage(NullUtil.checkNull(globalSet.msg))
                            .setPositiveButton(R.string.ykfsdk_iknow, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    leaveChatActivity();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 提供页面的适配器
     *
     * @return
     */
    public ChatAdapter getChatAdapter() {
        return chatAdapter;
    }

    /**
     * 重新发送消息
     *
     * @param msg
     * @param position
     */
    public void resendMsg(FromToMessage msg, int position) {
        if (IMChatManager.getInstance().isFinishWhenReConnect) {
            startReStartDialog();
        } else {
            if (MoorAntiShakeUtils.getInstance().check()) {
                return;
            }
            msg.sendState = "sending";
            MessageDao.getInstance().updateMsgToDao(msg);
            updateMessage();
            IMChat.getInstance().sendMessage(msg, new ChatListener() {
                @Override
                public void onSuccess(String msg) {
                    updateMessage();
                }

                @Override
                public void onFailed(String msg) {
                    LogUtils.eTag("SendMessage", msg);
                    updateMessage();
                }

                @Override
                public void onProgress(int progress) {
                    updateMessage();
                }
            });
        }
    }


    /**
     * 两种卡片类型点击发送时的调用；
     * {@link ChatListClickListener}
     *
     * @param msg
     * @param type 旧卡片：FromToMessage.MSG_TYPE_CARDINFO
     *             新卡片：FromToMessage.MSG_TYPE_NEW_CARD_INFO
     */
    public void sendCardMsg(FromToMessage msg, String type) {
        FromToMessage fromToMessage = new FromToMessage();
        fromToMessage.userType = "0";
        fromToMessage.message = "";
        fromToMessage.msgType = type;
        fromToMessage.when = System.currentTimeMillis();
        fromToMessage.sessionId = IMChat.getInstance().getSessionId();
        fromToMessage.tonotify = IMChat.getInstance().get_id();
        fromToMessage.type = "User";
        fromToMessage.from = IMChat.getInstance().get_id();
        if (msg.cardInfo != null) {
            fromToMessage.cardInfo = msg.cardInfo;
        }
        if (msg.newCardInfo != null) {
            fromToMessage.newCardInfo = msg.newCardInfo;
        }
        sendSingleMessage(fromToMessage);
    }

    /**
     * 共用方法：发送一条消息
     *
     * @param fromToMessage
     */
    private void sendSingleMessage(FromToMessage fromToMessage) {
        if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                !IMChatManager.getInstance().getSocketConnect()) {
            Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
            LogUtils.aTag("发送消息break");
            startReStartDialog3();
            return;
        }

        if (conversationOver) {
            startReStartDialog();
            return;
        }
        //界面显示
        descFromToMessage.add(fromToMessage);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
        mChatInput.setText("");

        //发送消息
        sendMsgToServer(fromToMessage);
    }

    //重新选择技能组或日程
    private void getIsGoSchedule() {
        if (!isFinishing()) {
            KfStartHelper.getInstance().getIsGoSchedule(this, YKFConstants.FROMCHAT);
        } else {
            ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_chatactivity_isfinish));
        }
    }


    /**
     * 会话已关闭 弹框
     */
    public void startReStartDialog() {
        final CommonBottomSheetDialog dialog = CommonBottomSheetDialog.instance(getString(R.string.ykfsdk_ykf_chatfinish_reopen), getString(R.string.ykfsdk_ykf_chatbegin), getString(R.string.ykfsdk_back));
        dialog.setListener(new CommonBottomSheetDialog.OnClickListener() {
            @Override
            public void onClickPositive() {
                dialog.close(false);
                getIsGoSchedule();
            }

            @Override
            public void onClickNegative() {
                dialog.close(false);
                leaveChatActivity();
            }
        });
        dialog.show(getSupportFragmentManager(), "");
    }


    /**
     * 网络异常弹框
     */
    public void startReStartDialog3() {
        final CommonBottomSheetDialog dialog = CommonBottomSheetDialog.instance(getString(R.string.ykfsdk_ykf_nonetwork_error), getString(R.string.ykfsdk_ykf_chatbegin_reconnect), "");
        dialog.setListener(new CommonBottomSheetDialog.OnClickListener() {
            @Override
            public void onClickPositive() {
                dialog.close(false);
                leaveChatActivity();
            }

            @Override
            public void onClickNegative() {

            }
        });
        if (!isFinishing()) {
            dialog.show(getSupportFragmentManager(), "");
        }
    }

    public ChatListView getChatListView() {
        return mChatList;
    }


    @Override
    public void onRecordFinished(float mTime, String filePath, String pcmFilePath) {
        if (!FileUtils.isExists(filePath)) {
            ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_recording_error));
            return;
        }
        //先在界面上显示出来
        FromToMessage fromToMessage = IMMessage.createAudioMessage(mTime, filePath, "");
        descFromToMessage.add(fromToMessage);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
        sendVoiceMsg("", fromToMessage);
    }

    /**
     * 发送录音消息
     *
     * @param voiceText
     */
    private void sendVoiceMsg(String voiceText, final FromToMessage fromToMessage) {
        fromToMessage.voiceText = voiceText;
        sendMsgToServer(fromToMessage);
    }

    /**
     * 处理收到的订单信息-店铺点击事件
     *
     * @param target 链接
     */
    public void handleOnClickOfLogisticsShop(String target) {
        if (conversationOver) {
            startReStartDialog();
            return;
        }
        if (!TextUtils.isEmpty(target)) {
            Intent forumIntent = new Intent(this, MoorWebCenter.class);
            forumIntent.putExtra("OpenUrl", target);
            forumIntent.putExtra("titleName", "详情");
            startActivity(forumIntent);
        }
    }

    /**
     * 处理收到的订单信息-item点击事件
     *
     * @param
     */
    public void handleOnClickOfLogisticsItem(String _id, String current, OrderInfoBean orderInfoBean) {
        if (conversationOver) {
            startReStartDialog();
            return;
        }
        if (orderInfoBean == null) {
            return;
        }
        mHashSet.add(_id);
        IMChatManager.getInstance().updateOrderInfo(_id, "1");
        if (moreOrderInfoDialog != null) {
            if (moreOrderInfoDialog.isShowing()) {
                moreOrderInfoDialog.dismiss();
            }
        }
      /*  "msg_task":{
            "current":"AI-4@3de59031ac5b4d40b309ba0325accd10",
            "item":{
                "target":"next",
                "params":{"orderNo":"3"}
            }
        }*/

        FromToMessage fromToMessage = new FromToMessage();
        fromToMessage.userType = "0";
        fromToMessage.message = "发送卡片信息";
        fromToMessage.msgType = FromToMessage.MSG_TYPE_LOGISTICS_INFO_LIST;
        fromToMessage.when = System.currentTimeMillis();
        fromToMessage.sessionId = IMChat.getInstance().getSessionId();
        fromToMessage.tonotify = IMChat.getInstance().get_id();
        fromToMessage.type = "User";
        fromToMessage.from = IMChat.getInstance().get_id();
        if (orderInfoBean != null) {
            fromToMessage.newCardInfo = new Gson().toJson(orderInfoBean);
        }
        MsgTaskBean bean = new MsgTaskBean()
                .setCurrent(current)
                .setItem(new MsgTaskItemBean()
                        .setTarget("next")
                        .setParams(new OrderInfoParams()
                                .setOrderNo(orderInfoBean.getParams().getOrderNo())));

        fromToMessage.msgTask = new Gson().toJson(bean);
        sendSingleMessage(fromToMessage);

    }

    /**
     * 处理收到的订单信息-查看更多点击事件
     */
    public void handleOnClickOfLogisticsMore(String current, final String _id) {
        if (loadingDialog != null) {
            loadingDialog.show(this.getFragmentManager(), "");
        }
  /* "msg_task":{
            "current":"AI4@3de59031ac5b4d40b309ba0325accd10",
                    "item":{
                "target":"self",
                        "params":"",
                        "page":"all"
            }
        }*/
        MsgTaskBean bean = new MsgTaskBean()
                .setCurrent(current)
                .setItem(new MsgTaskItemBean()
                        .setTarget("self")
                        .setPage("all"));

        String msgTask = new Gson().toJson(bean);
        HttpManager.getMoreOrderInfo(msgTask, new HttpResponseListener() {
            @Override
            public void onSuccess(String responseStr) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                String succeed = HttpParser.getSucceed(responseStr);
                if ("true".equals(succeed)) {
                    try {
                        JSONObject o = new JSONObject(responseStr);
                        String message = o.getString("msgTask");
                        if (NullUtil.checkNULL(message)) {
                            Type token = new TypeToken<OrderBaseBean>() {
                            }.getType();
                            final OrderBaseBean orderBaseBean = new Gson().fromJson(message, token);
                            if (orderBaseBean.getData() != null) {
                                ArrayList<OrderInfoBean> infoBeanList = orderBaseBean.getData().getShop_list();
                                if (infoBeanList == null) {
                                    infoBeanList = orderBaseBean.getData().getItem_list();
                                }
                                if (infoBeanList == null) {
                                    infoBeanList = new ArrayList<>();
                                }
                                moreOrderInfoDialog = BottomSheetLogisticsInfoDialog.init(infoBeanList, orderBaseBean.getCurrent(), _id);
                                moreOrderInfoDialog.show(getSupportFragmentManager(), "");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_loadmore_fail));
                }
            }

            @Override
            public void onFailed() {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_loadmore_fail));
            }
        });
    }

    /**
     * 处理收到的物流信息-查看完整物流信息点击事件
     */
    public void handleOnClickOfLogisticsProgressMore(FromToMessage message) {

        // TODO: 2019-12-31  查看完整物流信息点击事件
        if (message.msgTask != null && !"".equals(message.msgTask)) {
            Type token = new TypeToken<OrderBaseBean>() {
            }.getType();
            final OrderBaseBean orderBaseBean = new Gson().fromJson(message.msgTask, token);
            if (orderBaseBean.getData() != null) {
                ArrayList<OrderInfoBean> infoBeanList = orderBaseBean.getData().getShop_list();
                if (infoBeanList == null) {
                    infoBeanList = orderBaseBean.getData().getItem_list();
                }
                if (infoBeanList == null) {
                    infoBeanList = new ArrayList<>();
                }
                BottomSheetLogisticsProgressDialog dialog = BottomSheetLogisticsProgressDialog.init(
                        orderBaseBean.getData().getList_title(),
                        orderBaseBean.getData().getList_num(),
                        orderBaseBean.getData().getMessage(),
                        infoBeanList);
                dialog.show(getSupportFragmentManager(), "");
            }
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(QuestionEvent questionEvent) {
//        if (questionDialog != null && questionDialog.isShowing()) {
//            questionDialog.close(false);
//        }
//        updateMessage();
//    }

    /**
     * 判断服务是否还活着
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        boolean isWork = false;
        if (TextUtils.isEmpty(ServiceName)) {
            isWork = false;
            LogUtils.aTag("runService", "服务名字是空的");
        }
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningService = myManager.getRunningServices(Integer.MAX_VALUE);
        if (runningService.size() <= 0) {
//            isWork = false;
            LogUtils.aTag("runService", "服务数是0");
        }
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().equals(ServiceName)) {
                isWork = true;
                LogUtils.aTag("runService", "服务还活着" + isWork);
                break;
            }
        }
        return isWork;
    }


    /**
     * 联想输入
     */
    private class GetLianXiangDataResponeHandler implements HttpResponseListener {

        @Override
        public void onFailed() {
            ll_hintView.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(String responseString) {
            String succeed = HttpParser.getSucceed(responseString);
            if (!TextUtils.isEmpty(mChatInput.getText().toString().trim()) && "true".equals(succeed)) {
                ll_hintView.removeAllViews();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    final JSONArray questions = jsonObject.getJSONArray("questions");
                    final String key = jsonObject.getString("keyword");
                    if (questions.length() > 0) {
                        //如果含有提示文字
                        ll_hintView.setVisibility(View.VISIBLE);
                        for (int j = 0; j < questions.length(); j++) {
                            //首先引入要添加的View
                            View view = View.inflate(ChatActivity.this, R.layout.ykfsdk_item_hint_view, null);
                            TextView textView = view.findViewById(R.id.tv_hintView);

                            if (TextUtils.isEmpty(key)) {
                                textView.setText(questions.getString(j));
                            } else {
                                textView.setText(RegexUtils.matchSearchText(Color.RED, questions.getString(j), key));
                            }


                            final int position = j;
                            textView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        // 直接发出去，然后清空所有内容。并且隐藏

                                        if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                                                !IMChatManager.getInstance().getSocketConnect()) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                                            startReStartDialog3();
                                            return;
                                        }

                                        if (IMChatManager.getInstance().isFinishWhenReConnect) {
                                            startReStartDialog();//并且弹框提示开始新会话

                                        } else {
                                            sendTextMsg(questions.getString(position));
                                            mChatInput.setText("");
                                            ll_hintView.setVisibility(View.GONE);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            ll_hintView.addView(view);
                        }


                    } else {
                        ll_hintView.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                ToastUtils.showShort(responseString);
            } else {
                //请求数据不对，隐藏
                ll_hintView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 语音转文本
     */
    public void getVoiceToText(final FromToMessage fromToMessage) {
        //倒计时三分钟,结束发送超时失败
        mVoiceToTextTimer = new CountDownTimer(1000 * 60 * 3, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                VoiceToTextEvent event = new VoiceToTextEvent();
                event.id = fromToMessage._id;
                event.status_code = VoiceToTextEvent.STATUS_TIMEOUT;
                onEventMainThread(event);
            }
        }.start();
        HttpManager.sendVoiceToText(fromToMessage._id, fromToMessage.message, fromToMessage.when, new HttpResponseListener() {
            @Override
            public void onSuccess(String responseStr) {
                Log.e("语音转文本", responseStr);
                try {
                    JSONObject jsonObject = new JSONObject(responseStr);
                    if (!jsonObject.optBoolean("Succeed")) {
                        stopTimer();
                        //提交语音转文本失败
                        if (fromToMessage.isRobot) {
                            sendVoiceAutoText(fromToMessage, "", false);
                        } else {
                            fromToMessage.isCacheShowVtoT = false;//将这条信息的 正在转换状态重置
                            MessageDao.getInstance().updateMsgToDao(fromToMessage);
                            chatAdapter.notifyDataSetChanged();
                            ToastUtils.showLong(ChatActivity.this, getText(R.string.ykfsdk_voice_to_text_error) + ":" + jsonObject.optString("Message"));
                        }
                    } else {
                        //如果接口直接返回messageId  文本内容，那么直接展示 ，不需要等待TCP
                        if (!TextUtils.isEmpty(jsonObject.optString("messageId"))) {
                            String text = jsonObject.optString("voiceMessage");
                            String id = jsonObject.optString("messageId");
                            VoiceToTextEvent event = new VoiceToTextEvent();
                            event.id = id;
                            event.toText = text;
                            event.status_code = VoiceToTextEvent.STATUS_OK;
                            onEventMainThread(event);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed() {
                stopTimer();
                //提交语音转文本失败
                fromToMessage.isCacheShowVtoT = false;//将这条信息的 正在转换状态重置
                MessageDao.getInstance().updateMsgToDao(fromToMessage);
                chatAdapter.notifyDataSetChanged();
                ToastUtils.showLong(ChatActivity.this, getText(R.string.ykfsdk_voice_to_text_error));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UnAssignEvent unAssignEvent) {
        chat_tv_convert.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ReSendMessage reSendMessage) {
        updateMessage();
    }

    /**
     * TcpMessageHandler 中 收到语音转文本回复，EventBus 回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VoiceToTextEvent voiceToTextEvent) {
        stopTimer();
        if (voiceToTextEvent != null) {
            if (!"VoiceToTextEvent_nullID".equals(voiceToTextEvent.id)) {
                FromToMessage fromToMessage = MessageDao.getInstance().getMessageById(voiceToTextEvent.id);
                if (fromToMessage.isRobot) {
                    //是机器人 执行自动转文本，成功发送text，失败发送语音
                    if (VoiceToTextEvent.STATUS_OK.equals(voiceToTextEvent.status_code)) {
                        //成功
                        sendVoiceAutoText(fromToMessage, voiceToTextEvent.toText, true);
                    } else {
                        //失败
                        sendVoiceAutoText(fromToMessage, "", false);
                    }

                } else {
                    if (VoiceToTextEvent.STATUS_OK.equals(voiceToTextEvent.status_code)) {
                        fromToMessage.voiceToText = voiceToTextEvent.toText;
                        fromToMessage.isShowVtoT = true;
                    } else if (VoiceToTextEvent.STATUS_FAIL.equals(voiceToTextEvent.status_code)) {
                        ToastUtils.showLong(ChatActivity.this, getText(R.string.ykfsdk_voice_to_text_error) + getString(R.string.ykfsdk_ykf_autotext_fail_reclick));
                    } else if (VoiceToTextEvent.STATUS_TIMEOUT.equals(voiceToTextEvent.status_code)) {
                        ToastUtils.showLong(ChatActivity.this, getText(R.string.ykfsdk_voice_to_text_error) + getString(R.string.ykfsdk_ykf_autotext_fail_reclick));
                    } else if (VoiceToTextEvent.STATUS_UNDEFINED.equals(voiceToTextEvent.status_code)) {
                        ToastUtils.showLong(ChatActivity.this, getText(R.string.ykfsdk_voice_to_text_error) + getString(R.string.ykfsdk_ykf_autotext_fail_nocheck));
                    } else if (VoiceToTextEvent.STATUS_TOLONG.equals(voiceToTextEvent.status_code)) {
                        ToastUtils.showLong(ChatActivity.this, getString(R.string.ykfsdk_ykf_autotext_fail_solong));
                    }
                    fromToMessage.isCacheShowVtoT = false;//将这条信息的 正在转换状态重置
                    MessageDao.getInstance().updateMsgToDao(fromToMessage);
                    //更新adapter数据源
                    for (int i = 0; i < descFromToMessage.size(); i++) {
                        if (!TextUtils.isEmpty(descFromToMessage.get(i)._id)) {
                            if (descFromToMessage.get(i)._id.equals(fromToMessage._id)) {
                                descFromToMessage.set(i, fromToMessage);
                            }
                        }
                    }
                }

                chatAdapter.notifyDataSetChanged();

            } else {
                ToastUtils.showShort(ChatActivity.this, getText(R.string.ykfsdk_voice_to_text_error) + ":VoiceToTextEvent_nullID");
            }

        }
    }

    /**
     * 机器人语音自动转文字失败 发送文本//!IMChatManager.getInstance().isManual false是机器人
     */
    private void sendVoiceAutoText(FromToMessage message, String str, boolean success) {
        if (success) {
            message.message = str;
            message.msgType = FromToMessage.MSG_TYPE_TEXT;
            message.isRobot = false;
        } else {
            message.msgType = FromToMessage.MSG_TYPE_AUDIO;
            message.isRobot = false;
        }
        //发送消息
        sendMsgToServer(message);
    }

    /**
     * 发送消息到服务器
     *
     * @param message
     */
    private void sendMsgToServer(final FromToMessage message) {
        IMChat.getInstance().sendMessage(message, new ChatListener() {
            @Override
            public void onSuccess(String msg) {
                //消息发送成功
                updateMessage();
                if (FromToMessage.MSG_TYPE_NEW_CARD_INFO.equals(message.msgType)) {
                    Type token = new TypeToken<NewCardInfo>() {
                    }.getType();
                    final NewCardInfo newCardInfo = new Gson().fromJson(message.newCardInfo, token);
                    if ("true".equals(newCardInfo.getAutoCardSend())) {
                        if (!TextUtils.isEmpty(newCardInfo.getCardID())) {
                            MoorSPUtils.getInstance().put(newCardInfo.getCardID(), true, true);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String msg) {
                //消息发送失败
                LogUtils.eTag("SendMessage", msg);
                updateMessage();
            }

            @Override
            public void onProgress(int progress) {
                updateMessage();
            }
        });
    }

    private void stopTimer() {
        if (mVoiceToTextTimer != null) {
            mVoiceToTextTimer.cancel();
        }
    }

    /**
     * 未读消息变已读
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgunReadToReadEvent event) {
        MessageDao.getInstance().updateUnReadToRead();
        for (int i = 0; i < descFromToMessage.size(); i++) {
            if ("false".equals(descFromToMessage.get(i).dealUserMsg) && "true".equals(descFromToMessage.get(i).sendState)) {
                descFromToMessage.get(i).dealUserMsg = "true";
            }
        }
        chatAdapter.notifyDataSetChanged();
    }

    /**
     * 有新消息了，并且页面可见。消费消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent msgEvent) {
        if (isFront) {
            if (handler != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<String> deals = new ArrayList<>();
                        for (int i = 0; i < descFromToMessage.size(); i++) {
                            FromToMessage t = descFromToMessage.get(i);
                            if ("1".equals(t.userType) && !t.dealMsg) {
                                //添加要消费的消息id
                                deals.add(t._id);
                            }
                        }
                        HttpManager.sdkDealImMsg(deals, new HttpResponseListener() {
                            @Override
                            public void onSuccess(String responseStr) {
                                LogUtils.aTag("消费坐席发送来的消息返回值", responseStr);
                            }

                            @Override
                            public void onFailed() {
                            }
                        });
                    }
                }, 700);
            }

        }

    }

    /**
     * 点击转人工
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TransferAgent agent) {
        //agent.peerid 不为空，传如转人工接口，来自于xbot文本中包含转人工按钮

        if (!IMChatManager.getInstance().getYkfChatStatusEnum().equals(YKFChatStatusEnum.KF_Robot_Status)) {
            ToastUtils.showShort(this, R.string.ykfsdk_no_robot);
            return;
        }


        //转人工服务
        IMChatManager.getInstance().convertManual(agent.peerid, agent.type, new OnConvertManualListener() {
            @Override
            public void onLine() {
//                IMChatManager.getInstance().resetBreakTimer();
                changeRobotVisiable();
                if (!type.equals("schedule")) {
                    //有客服在线,隐藏转人工按钮
                    chat_tv_convert.setVisibility(View.GONE);
                    bar_bottom.setVisibility(View.VISIBLE);
                    mOtherName.setText(R.string.ykfsdk_wait_link);
                    titleName = getString(R.string.ykfsdk_wait_link);
                    Toast.makeText(getApplicationContext(), R.string.ykfsdk_topeoplesucceed, Toast.LENGTH_SHORT).show();
                    rvTagLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void offLine() {
                changeRobotVisiable();
//                cancelTimer();
                //当前没有客服在线
                if (!type.equals("schedule")) {
                    showOffLineDialog();
                    rvTagLabel.setVisibility(View.GONE);
                }
            }
        });

    }


    //xbot表单消息提交
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(XbotFormEvent event) {
        if (event.xbotForm != null) {
            FromToMessage toMessage = IMMessage.createXbotFormMessage(event.xbotForm);
            sendSingleMessage(toMessage);
        }
    }


    //xbot收到表单消息自动弹出
    public void showXbotfrom() {
        FromToMessage message = MessageDao.getInstance().getisFristXbotFormMessages();
        if (message != null) {
            //弹出填写表单 ，数据源来自新转换的对象。
            XbotForm xbotForm_data = new Gson().fromJson(message.xbotForm, XbotForm.class);
            BottomXbotFormDialog xbotFormDialog = BottomXbotFormDialog.init(xbotForm_data.formName, xbotForm_data, message._id);
            xbotFormDialog.show(getSupportFragmentManager(), "");
            MessageDao.getInstance().upFristXbotForm();
        }
    }


    /**
     * Builder模式设置参数
     */
    public static final class Builder {
        private String type;
        private String scheduleId;
        private String processId;
        private String currentNodeId;
        private String processType;
        private String entranceId;
        private String PeerId;
        private CardInfo cardInfo;
        private NewCardInfo newCardInfo;

        public Builder setPeerId(String peerId) {
            PeerId = peerId;
            if (PeerId != null && !"".equals(PeerId)) {
                InfoDao.getInstance().updataPeerID(peerId);
            }

            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setScheduleId(String scheduleId) {
            this.scheduleId = scheduleId;
            return this;
        }

        public Builder setProcessId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder setCurrentNodeId(String currentNodeId) {
            this.currentNodeId = currentNodeId;
            return this;
        }

        public Builder setProcessType(String processType) {
            this.processType = processType;
            return this;
        }

        public Builder setEntranceId(String entranceId) {
            this.entranceId = entranceId;
            return this;
        }

        public Builder setCardInfo(CardInfo cardInfo) {
            this.cardInfo = cardInfo;
            //卡片不为空 则传入数据库
            if (cardInfo != null) {
                FromToMessage cardMsg = new FromToMessage();
                cardMsg.msgType = FromToMessage.MSG_TYPE_CARD;
                cardMsg.cardInfo = JsonBuild.getCardInfo(cardInfo);
                cardMsg.userType = "0";
                cardMsg.when = System.currentTimeMillis();
                LogUtils.aTag("cardinfo==", JsonBuild.getCardInfo(cardInfo));
                MessageDao.getInstance().insertSendMsgsToDao(cardMsg);
            }
            return this;
        }

        public Builder setNewCardInfo(NewCardInfo newCardInfo) {
            this.newCardInfo = newCardInfo;
            //新卡片类型不为空 则传入数据库
            if (newCardInfo != null) {
                FromToMessage cardMsg = new FromToMessage();
                cardMsg.msgType = FromToMessage.MSG_TYPE_NEW_CARD;
//                cardMsg.newCardInfo = JsonBuild.getOrderInfo(newCardInfo);
                cardMsg.newCardInfo = new Gson().toJson(newCardInfo);
                cardMsg.userType = "0";
                cardMsg.when = System.currentTimeMillis();
                LogUtils.aTag("newCardInfo==", new Gson().toJson(newCardInfo));
                MessageDao.getInstance().insertSendMsgsToDao(cardMsg);
            }
            return this;
        }

        public Builder() {

        }

        public Intent build(Context mContex) {
            if (!TextUtils.isEmpty(type)) {
                if (YKFConstants.TYPE_PEER.equals(type)) {
                    //技能组，检查技能组id
                    if (TextUtils.isEmpty(PeerId)) {
                        ToastUtils.showShort(mContex, "ErrorCode" + YKFErrorCode.ErrorCode1007);
                        LogUtils.eTag("ErrorCode1007", "1007:进入会话也没有携带技能组id");
                        IMChatManager.getInstance().quitSDk();
                        return null;
                    }
                } else if (YKFConstants.TYPE_SCHEDULE.equals(type)) {
                    //日程,检查日程需要的字段
                    if (TextUtils.isEmpty(scheduleId) || TextUtils.isEmpty(processId) || TextUtils.isEmpty(currentNodeId)) {
                        ToastUtils.showShort(mContex, "ErrorCode" + YKFErrorCode.ErrorCode1008);
                        LogUtils.eTag("ErrorCode1008", "1008:进入会话没有携带日程所需字段");
                        IMChatManager.getInstance().quitSDk();
                        return null;
                    }
                }
            } else {
                //需要传入技能组或日程
                ToastUtils.showShort(mContex, "ErrorCode" + YKFErrorCode.ErrorCode1009);
                LogUtils.eTag("ErrorCode1009", "1009:进入会话没有携带会话类型");
                IMChatManager.getInstance().quitSDk();
                return null;
            }


            IMChatManager.getInstance().cancelInitTimer();
            Intent intent = new Intent(mContex, ChatActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("scheduleId", scheduleId);
            intent.putExtra("processId", processId);
            intent.putExtra("currentNodeId", currentNodeId);
            intent.putExtra("processType", processType);
            intent.putExtra("entranceId", entranceId);
            intent.putExtra("PeerId", PeerId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContex.startActivity(intent);

            return intent;
        }
    }

    /**
     * 拍照，图库、文件、评价、常见问题按钮功能
     */
    private void dealAddMoreViewClickEvent(PanelView panelView) {
        LinearLayout ll_takepic = panelView.findViewById(R.id.ll_takepic);
        LinearLayout ll_photo = panelView.findViewById(R.id.ll_photo);
        LinearLayout ll_file = panelView.findViewById(R.id.ll_file);
        ll_invite = panelView.findViewById(R.id.ll_invite);
        LinearLayout ll_question = panelView.findViewById(R.id.ll_question);
        LinearLayout ll_video = panelView.findViewById(R.id.ll_video);


        GlobalSet globalSet = GlobalSetDao.getInstance().getGlobalSet();
        //常见问题按钮是否显示-来自服务端配置
        boolean showQuestionButton = globalSet.showIssueButton;
        ll_question.setVisibility(showQuestionButton ? View.VISIBLE : View.GONE);

        //拍照按钮是否显示-来自服务端配置
        boolean showTakePicButton = globalSet.showPhotoButton;
        ll_takepic.setVisibility(showTakePicButton ? View.VISIBLE : View.GONE);

        //图库按钮是否显示-来自服务端配置
        boolean showPhotoButton = globalSet.showGalleryButton;
        ll_photo.setVisibility(showPhotoButton ? View.VISIBLE : View.GONE);

        //附件按钮是否显示-来自服务端配置
        boolean showFileButton = globalSet.showFileButton;
        ll_file.setVisibility(showFileButton ? View.VISIBLE : View.GONE);


        ll_takepic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionXUtil.checkPermission(ChatActivity.this, new OnRequestCallback() {
                    @Override
                    public void requestSuccess() {
                        if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                                !IMChatManager.getInstance().getSocketConnect()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                            startReStartDialog3();
                            return;
                        }
                        MoorTakePicturesHelper.getInstance().openCamera(ChatActivity.this, MoorDemoConstants.CAMERA_ACTIVITY_REQUEST_CODE);
                    }
                }, PermissionConstants.CAMERA);
            }
        });


        ll_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permission = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permission = new String[]{PermissionConstants.IMAGEBY_API33};
                } else {
                    permission = new String[]{PermissionConstants.STORE};
                }
                PermissionXUtil.checkPermission(ChatActivity.this, new OnRequestCallback() {
                    @Override
                    public void requestSuccess() {
                        if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                                !IMChatManager.getInstance().getSocketConnect()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                            startReStartDialog3();
                            return;
                        }
                        openAlbum();
                    }
                }, permission);
            }
        });
        ll_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permission = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permission = new String[]{PermissionConstants.IMAGEBY_API33, PermissionConstants.VIDEOBY_API33};
                } else {
                    permission = new String[]{PermissionConstants.STORE};
                }
                PermissionXUtil.checkPermission(ChatActivity.this, new OnRequestCallback() {
                    @Override
                    public void requestSuccess() {
                        if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                                !IMChatManager.getInstance().getSocketConnect()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                            LogUtils.aTag("chat_morebreak");
                            startReStartDialog3();
                            return;
                        }
                        openFile();
                    }
                }, permission);
            }
        });
        ll_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                        !IMChatManager.getInstance().getSocketConnect()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                    LogUtils.aTag("chat_morebreak");
                    startReStartDialog3();
                    return;
                }
                openInvestigateDialog(true, YKFConstants.INVESTIGATE_TYPE_IN, null, false);
            }
        });
        ll_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                        !IMChatManager.getInstance().getSocketConnect()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                    LogUtils.aTag("chat_morebreak");
                    startReStartDialog3();
                    return;
                }
//                questionDialog = new BottomSheetQuestionDialog(questionList);
//                questionDialog.show(getSupportFragmentManager(), "");
                startActivity(new Intent(ChatActivity.this, CommonQuestionsActivity.class));
            }
        });
        if (ll_invite != null) {
            ll_invite.setVisibility(showInviteButton ? View.VISIBLE : View.GONE);
        }

        if (GlobalSetDao.getInstance().getGlobalSet() != null) {
            boolean mobileVideoChat = "1".equals(NullUtil.checkNull(GlobalSetDao.getInstance().getGlobalSet().mobileVideoChat));
            boolean mobileVideoChatIm = "1".equals(NullUtil.checkNull(GlobalSetDao.getInstance().getGlobalSet().mobileVideoChatIm));
            if (mobileVideoChat && mobileVideoChatIm) {
                try {
                    Class clz = Class.forName(YKFCallHelper.YKFCALLMANAGER_CLASS);
                    ll_video.setVisibility(View.VISIBLE);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    ll_video.setVisibility(View.GONE);
                }
            } else {
                ll_video.setVisibility(View.GONE);
            }
        } else {
            ll_video.setVisibility(View.GONE);
        }
        ll_video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Claim_Status) {
                    PermissionXUtil.checkPermission(ChatActivity.this, new OnRequestCallback() {
                                @Override
                                public void requestSuccess() {
                                    if (!MoorUtils.isNetWorkConnected(IMChatManager.getInstance().getApplicationAgain()) &&
                                            !IMChatManager.getInstance().getSocketConnect()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.ykfsdk_ykf_not_netwokr_error), Toast.LENGTH_SHORT).show();
                                        LogUtils.aTag("chat_morebreak");
                                        startReStartDialog3();
                                        return;
                                    }
                                    openVideo();
                                }
                            }, PermissionConstants.CAMERA
                            , PermissionConstants.RECORD_AUDIO);
                } else {
                    ToastUtils.showShort(ChatActivity.this, getString(R.string.ykfsdk_ykf_starting_video_tips));
                }
            }
        });

    }

    /**
     * 开启视频
     */
    private void openVideo() {
        final BottomSheetVideoOrVoiceDialog videoOrVoiceDialog = new BottomSheetVideoOrVoiceDialog();
        videoOrVoiceDialog.setOnClickListener(new BottomSheetVideoOrVoiceDialog.onClickListener() {
            @Override
            public void onClick(int type) {
                loadingDialog.show(getFragmentManager(), "");
                loadingDialog.setCanceledOnTouchOutside(true);
                YKFCallInfoBean callInfoBean = new YKFCallInfoBean();
                callInfoBean.setUserName(userName)
                        .setUserIcon(userIcon)
                        .setVideo(0 == type)
                        .setExten(exten);

                YKFCallHelper.setOnCancelDialogListener(new OnCancelDialogListener() {
                    @Override
                    public void cancelDialog() {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                    }
                });
                YKFCallHelper.openCall(callInfoBean);
                videoOrVoiceDialog.close(false);
            }
        });
        videoOrVoiceDialog.show(getSupportFragmentManager(), "");
    }

    /**
     * 打开本地相册
     */
    public void openAlbum() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
        intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intentFromGallery
                , PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfos.size() != 0) {
            startActivityForResult(intentFromGallery, MoorDemoConstants.PICK_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            Intent intentFromContent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intentFromContent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intentFromContent, MoorDemoConstants.PICK_IMAGE_ACTIVITY_REQUEST_CODE);
//            ToastUtils.showShort(this, getString(R.string.ykfsdk_ykf_no_imagepick));
        }
    }

    /**
     * 打开文件选择
     */
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, MoorDemoConstants.PICK_FILE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 显示、隐藏评价按钮
     *
     * @param show
     */
    private void showOrHideInviteButton(boolean show) {
        showInviteButton = show;
        if (ll_invite != null) {
            ll_invite.setVisibility(showInviteButton ? View.VISIBLE : View.GONE);
        }

    }


    public boolean isListBottom() {
        return isListBottom;
    }

    private boolean isListViewReachBottomEdge(final AbsListView listView) {
        boolean result = false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result = (listView.getHeight() >= bottomChildView.getBottom());
        }
        return result;
    }

    /**
     * 跳转日程留言
     */
    private void startScheduleOffline() {
        GlobalSet globalSet = GlobalSetDao.getInstance().getGlobalSet();
        Intent intent = new Intent(ChatActivity.this, ScheduleOfflineMessageActivity.class);
        intent.putExtra("LeavemsgNodeId", schedule_id);
        intent.putExtra("ToPeer", schedule_topeer);
        if (globalSet != null) {
            intent.putExtra("inviteLeavemsgTip", NullUtil.checkNull(globalSet.scheduleLeavemsgTip));
        }
        startActivity(intent);
        finish();
    }

    /**
     * 人工状态下，输入文字时调用此接口
     */
    private void sendTypingStatus(String content) {
        if (sdkTypeNoticeFlag) {
            if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Claim_Status) {
                HttpManager.sendTypingStatus(content, new HttpResponseListener() {
                    @Override
                    public void onSuccess(String responseStr) {

                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }
        }
    }

    /**
     * 语音和文字 按钮显示控制
     *
     * @param mServiceShowVoice 来自后台控制的语音按钮开关,false则都隐藏相当于没有切换了
     * @param showVoice         是否显示语音按钮
     * @param showText          是否显示键盘文本按钮
     */
    private void voiceAndTextBtnVisibility(boolean mServiceShowVoice, boolean showVoice, boolean showText) {
        if (!mServiceShowVoice) {
            mChatSetModeVoice.setVisibility(View.GONE);
            mChatSetModeKeyboard.setVisibility(View.GONE);
        } else {
            mChatSetModeVoice.setVisibility(showVoice ? View.VISIBLE : View.GONE);
            mChatSetModeKeyboard.setVisibility(showText ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 当离开页面时要执行退出sdk 操作
     */
    private void leaveChatActivity() {
        IMChatManager.getInstance().quitSDk();
        finish();
    }


    /**
     * 配置切换机器人按钮
     */
    private void setCheckRobotView() {
        GlobalSet globalSet = GlobalSetDao.getInstance().getGlobalSet();

        ArrayList<RobotListBean> arrayList = null;
        if (!TextUtils.isEmpty(globalSet.changeRobotList)) {
            arrayList = new Gson().fromJson(globalSet.changeRobotList, new TypeToken<ArrayList<RobotListBean>>() {
            }.getType());
        }
        final ArrayList<RobotListBean> finalArrayList = arrayList;
        check_robot_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomChangeRobotDialog dialog = new BottomChangeRobotDialog(ChatActivity.this, finalArrayList, new OnClickRobotListener() {
                    @Override
                    public void onClickRobot(RobotListBean robotListBean) {
                        if (robotListBean != null) {
                            changeRobot(robotListBean.switchRobotId);
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
            }
        });
        check_robot_float.setOnTouchListener(new CheckRobotViewTouchListener(rl_container));
    }

    /**
     * 执行切换机器人动作
     *
     * @param switchRobotId 要切换的机器人Id
     */
    public void changeRobot(String switchRobotId) {
        if (!IMChatManager.getInstance().getYkfChatStatusEnum().equals(YKFChatStatusEnum.KF_Robot_Status)) {
            ToastUtils.showShort(this, R.string.ykfsdk_no_robot);
            return;
        }


        HttpManager.sdkTransferRobot(IMChat.getInstance().getRobotId(), switchRobotId, IMChat.getInstance().getRobotType(), new HttpResponseListener() {
            @Override
            public void onSuccess(String responseStr) {
                try {
                    robotEvaluationFinish = false;//机器人评价重置
                    hasSendRobotMsg = false;//机器人状态下说话记录重置

                    JSONObject jsonObject = new JSONObject(responseStr);
                    boolean Succeed = jsonObject.optBoolean("Succeed");
                    if (Succeed) {
                        //机器人有底部底部菜单bottomlist数据
                        if (jsonObject.has("bottomList")) {
                            JSONArray list = jsonObject.getJSONArray("bottomList");
                            IMChatManager.getInstance().setBottomList(list);
                            setRobotBottomList(true);
                        }

                        //更新机器人点赞点踩数据
                        if (jsonObject.has("chatSession")) {
                            ChatSessionBean bean = new Gson().fromJson(jsonObject.getString("chatSession"), ChatSessionBean.class);
                            IMChatManager.getInstance().setChatSession(bean);
                        } else {
                            IMChatManager.getInstance().setChatSession(null);
                        }


                        //当前机器人有横向滑动quickMenu快捷按钮 (跟随消息列表滚动的)
                        if (jsonObject.has("quickMenu")) {
                            JSONArray list = jsonObject.getJSONArray("quickMenu");
                            IMChatManager.getInstance().setQuickMenuList(list);
                            long quickMenuWhen = jsonObject.optLong("quickMenuWhen");
                            IMChatManager.getInstance().setQuickMenuWhen(quickMenuWhen);
                            setRobotQuickBtn();
                        } else {
                            IMChatManager.getInstance().setQuickMenuList(null);
                            IMChatManager.getInstance().setQuickMenuWhen(0);
                            MessageDao.getInstance().deleteQuickMenuBtn();//插入前需要先删除，数据库中只有一条本类型数据


                            Iterator<FromToMessage> iterator = descFromToMessage.iterator();
                            while (iterator.hasNext()) {
                                FromToMessage msg = iterator.next();
                                if (FromToMessage.QUICK_MENU_LIST.equals(msg.msgType)) {
                                    iterator.remove();   //注意这个地方
                                }
                            }
                            chatAdapter.notifyDataSetChanged();
                        }
                    } else {
                        ToastUtils.showShort(ChatActivity.this, R.string.ykfsdk_change_robot_fail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed() {

            }
        });

    }


    /**
     * 显示或隐藏切换机器人按钮
     * 只有在机器人状态下并且开启转接机器人才显示
     */
    private void changeRobotVisiable() {
        GlobalSet globalSet = GlobalSetDao.getInstance().getGlobalSet();
        boolean isShow = false;
        if (globalSet.isOpenChangeRobot) {
            if (YKFChatStatusEnum.KF_Robot_Status.equals(IMChatManager.getInstance().getYkfChatStatusEnum())) {
                isShow = true;
            }
        }

        if (isShow) {
            check_robot_float.setVisibility(View.VISIBLE);
        } else {
            check_robot_float.setVisibility(View.GONE);
        }

    }


    /**
     * 配置xbot底部横向底部菜单滑动按钮
     */
    private synchronized void setRobotBottomList(boolean fresh) {
        if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status) {
            //底部横向滑动按钮
            JSONArray bottomList = IMChatManager.getInstance().getBottomList();
            if (bottomList.length() > 0) {
                if (fresh) {

                } else {
                    if (rvTagLabel.isShown()) {
                        //如果是机器人状态并且已经显示了,那么不需要再处理
                        return;
                    }
                }

                rvTagLabel.setVisibility(View.VISIBLE);
                try {
                    flowBeanList.clear();
                    for (int j = 0; j < bottomList.length(); j++) {
                        JSONObject jb = bottomList.getJSONObject(j);
                        FlowBean flowBean = new FlowBean();
                        flowBean.setButton(jb.getString("button"));
                        flowBean.setText(jb.getString("text"));
                        flowBean.setButton_type(jb.optInt("button_type"));
                        flowBeanList.add(flowBean);
                    }
                    ChatTagLabelsAdapter adapter = new ChatTagLabelsAdapter(flowBeanList);
                    adapter.setOnItemClickListener(LabelsClickListener);
                    rvTagLabel.setAdapter(adapter);
//                    tagLabeAdapter.refreshData(flowBeanList);
                } catch (Exception e) {
                    rvTagLabel.setVisibility(View.GONE);
                }
            } else {
                rvTagLabel.setVisibility(View.GONE);
            }
        } else {
            rvTagLabel.setVisibility(View.GONE);
        }
    }

    ChatTagLabelsAdapter.onItemClickListener LabelsClickListener = new ChatTagLabelsAdapter.onItemClickListener() {
        @Override
        public void OnItemClick(FlowBean flowBean) {
            if (flowBean.getButton_type() == 2) {
                if (!TextUtils.isEmpty(flowBean.getText())) {
                    Intent forumIntent = new Intent(ChatActivity.this, MoorWebCenter.class);
                    forumIntent.putExtra("OpenUrl", flowBean.getText());
                    forumIntent.putExtra("titleName", flowBean.getButton());
                    startActivity(forumIntent);
                }
            } else {
                sendXbotTextMsg(flowBean.getText());
            }

        }
    };

    /**
     * 配置xbot 快捷回复按钮 ,样式跟随消息列表滑动
     */
    private void setRobotQuickBtn() {
        if (IMChatManager.getInstance().getYkfChatStatusEnum() == YKFChatStatusEnum.KF_Robot_Status) {
            //机器人横向滑动快捷按钮（目前仅在页面做处理，sdk内部没有对此部分数据拿取，方便客户可以修改）
            JSONArray quickArray = IMChatManager.getInstance().getQuickMenuList();
            long quickMenuWhen = IMChatManager.getInstance().getQuickMenuWhen();
            if (quickArray != null) {
                FromToMessage quickMsg = IMMessage.createQuickMenuList(quickArray.toString(), quickMenuWhen);
                MessageDao.getInstance().insertSendMsgsToDao(quickMsg);
                updateMessage();
            }
        }
    }


    /**
     * Service 异常断开 重新打开服务
     */
    public void reStartService() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent imserviceIntent = new Intent(ChatActivity.this, SocketService.class);
                SocketService.hasRelogin = true;
                if (IMChatManager.USE_ForegroundService) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(imserviceIntent);
                    } else {
                        startService(imserviceIntent);
                    }
                } else {
                    startService(imserviceIntent);
                }
            }
        }, 500);
    }


    /**
     * 自动发送卡片
     */
    private void autoSendNewCard() {
        List<FromToMessage> messageList = MessageDao.getInstance().queryByTypeMsgs(FromToMessage.MSG_TYPE_NEW_CARD);
        if (messageList != null) {
            if (messageList.size() > 0) {
                FromToMessage cardMsg = messageList.get(0);
                if (FromToMessage.MSG_TYPE_NEW_CARD.equals(cardMsg.msgType)) {
                    Type token = new TypeToken<NewCardInfo>() {
                    }.getType();
                    final NewCardInfo newCardInfo = new Gson().fromJson(cardMsg.newCardInfo, token);
                    if ("true".equals(newCardInfo.getAutoCardSend())) {
                        if (TextUtils.isEmpty(newCardInfo.getCardID())) {
                            //cardId为空 也就直接发,可能会重复发送
                            sendCardMsg(cardMsg, FromToMessage.MSG_TYPE_NEW_CARD_INFO);
                        } else {
                            if (MoorSPUtils.getInstance().getBoolean(newCardInfo.getCardID(), false)) {

                            } else {
                                sendCardMsg(cardMsg, FromToMessage.MSG_TYPE_NEW_CARD_INFO);
                            }
                        }

                    }
                }
            }
        }
    }


    /**
     * 4.9.7 24.6来自后端的访客无响应计时事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(KFBreakTimerEvent event) {

        if (event != null) {
            if ("tips".equals(event.type)) {
                //提示语
                if (handler != null) {
                    handler.sendEmptyMessage(HANDLER_BREAK_TIP);
                }
            } else if ("end".equals(event.type)) {
                //结束
                timerClose(break_end);
            }
        }

    }
}
