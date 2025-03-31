package com.m7.imkfsdk.chat.chatrow;

/**
 * Created by longwei on 2016/3/9.
 * 聊天条目的类型
 */
public enum ChatRowType {

    /*
     * 接收的文本消息类型
     */
    TEXT_ROW_RECEIVED("C200R", 1),

    /*
     * 发送的文本消息类型
     */
    TEXT_ROW_TRANSMIT("C200T", 2),
    /*
     * 接收的图片消息类型
     */
    IMAGE_ROW_RECEIVED("C300R", 3),

    /*
     * 发送的图片消息类型
     */
    IMAGE_ROW_TRANSMIT("C300T", 4),
    /*
     * 接收的语音消息类型
     */
    VOICE_ROW_RECEIVED("C400R", 5),

    /*
     * 发送的语音消息类型
     */
    VOICE_ROW_TRANSMIT("C400T", 6),


    FILE_ROW_RECEIVED("C600R", 8),

    FILE_ROW_TRANSMIT("C600T", 9),

    IFRAME_ROW_RECEIVED("C700R", 10),

    BREAK_TIP_ROW_RECEIVED("C800R", 11),

    /*
     * 知识库富文本
     */
    RICHTEXT_ROW_RECEIVED("C1300R", 13),

    RICHTEXT_ROW_TRANSMIT("C1400T", 14),
    /*
     * 卡片展示
     */
    CARDINFO_ROW_TRANSMIT("C1500T", 15),
    /*
     * 视频展示
     */
    VIDEO_ROW_TRANSMIT("C1600T", 16),
    VIDEO_ROW_RECEIVED("C1600R", 17),

    /*
     * 收到的物流信息列表/收到的物流进度信息列表
     */
    LOGISTICS_INFORMATION_ROW_RECEIVED("C1700R", 18),
    /*
     * 发送的物流信息类型
     */
    LOGISTICS_INFORMATION_ROW_TRANSMIT("C1700T", 19),

    /*
     * 点击发送给坐席的卡片信息类型
     */
    ORDER_INFO_ROW_TRANSMIT("C2000T", 20),
    /*
     * 发出的卡片信息类型
     */
    SEND_ORDER_INFO_ROW_TRANSMIT("C2100T", 21),

    /*
     * 收到的卡片信息类型
     */
    RECEIVED_ORDER_INFO_ROW_RECEIVED("C2100R", 22),
    /*
     * 取消评价类型
     */
    INVESTIGATE_CANCEL_RECEIVED("C2200R", 23),
    /*
     * 评价成功类型
     */

    INVESTIGATE_SUCCESS_TRANSMIT("C2300T", 24),

    /*
     * 分组常见问题
     */

    TAB_QUESTION_RECEIVED("C2400R", 25),

    /*
     * Xbot收到的表单消息
     */

    XBOT_FORM_DATA_RECEIVED("C2500R", 26),
    /*
     * Xbot填写好发出表单消息
     */

    XBOT_FORM_DATA_SUBMIT("C2600T", 27),

    /*
     *  Xbot 快捷菜单按钮 横向滑动
     */
    QUICK_MENU_LIST("C2700R", 28),

    /**
     * 文件是视频类型
     */
    SEND_FILE_IS_VIDEO("C2800T", Integer.valueOf(29)),
    RECEIVED_FILE_IS_VIDEO("C2800R", Integer.valueOf(30)),

    /*
     * 未知消息类型
     */

    TAB_QUESTION_UNKNOWN("C9900R", 100);


    private final Integer mId;
    private final Object mDefaultValue;

    ChatRowType(Object defaultValue, Integer id) {
        this.mId = id;
        this.mDefaultValue = defaultValue;
    }

    /**
     * Method that returns the unique identifier of the setting.
     *
     * @return the mId
     */
    public Integer getId() {
        return this.mId;
    }

    /**
     * Method that returns the default value of the setting.
     *
     * @return Object The default value of the setting
     */
    public Object getDefaultValue() {
        return this.mDefaultValue;
    }

    public static ChatRowType fromValue(String value) {
        ChatRowType[] values = values();
        int cc = values.length;
        for (int i = 0; i < cc; i++) {
            if (values[i].mDefaultValue.equals(value)) {
                return values[i];
            }
        }
        return null;
    }


}
