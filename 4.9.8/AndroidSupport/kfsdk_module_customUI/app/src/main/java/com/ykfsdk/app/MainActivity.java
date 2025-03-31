package com.ykfsdk.app;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.m7.imkfsdk.KfStartHelper;
import com.m7.imkfsdk.utils.statusbar.StatusBarUtils;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.lib.utils.sharedpreferences.MoorSPUtils;
import com.moor.imkf.model.entity.CardInfo;
import com.moor.imkf.model.entity.NewCardInfo;
import com.moor.imkf.model.entity.NewCardInfoAttrs;
import com.moor.imkf.model.entity.NewCardInfoTags;
import com.moor.imkf.requesturl.RequestUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int themeType;


    private KfStartHelper helper;
    // 接入id（需后台配置获取）
    private final String accessId = "";
//    用户名
    private final String userName = "";
    //用户id
    private final String userId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        themeType = MoorSPUtils.getInstance().getInt(YKFConstants.SYSTHEME, 0);
        if (themeType == 0) {
            setTheme(com.m7.imkfsdk.R.style.ykfsdk_KFSdkAppTheme);
        } else if (themeType == 1) {
            setTheme(com.m7.imkfsdk.R.style.ykfsdk_KFSdkAppTheme1);
        }
        setContentView(R.layout.kf_activity_main);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.ykfsdk_all_white));

        initKfHelper();

        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);
    }


    private void initKfHelper() {

        //设置sdk 显示语言版本
//        initLanguage();

        /**
         * 1（重要检查）：
         * 请检查在 APPlication onCreate() 中的初始化
         * 检查以下代码是否已添加完成
         * YKFUtils.init(this)
         */


        /**
         * 2:
         * 初始化help
         */
        helper = KfStartHelper.getInstance();


        /**
         * 3：
         * 设置服务环境
         * setRequestUrl() 与 setRequestBasic()，为二选一调用
         * 阿里/腾讯/华为用户只需使用下方的setRequestBasic即可。
         * 私有云用户只需使用setRequestUrl即可。
         */

        /**
         * 3.1:
         * 设置服务环境 阿里/腾讯/华为,可以联系技术支持确认您的服务环境
         * 注意要在helper.initSdkChat()之前设置
         * RequestUrl.ALIYUN_REQUEST;//阿里云环境
         * RequestUrl.TENCENT_REQUEST;//腾讯云环境
         * RequestUrl.HUAWEI_REQUEST;//华为云环境
         */
        RequestUrl.setRequestBasic(RequestUrl.TENCENT_REQUEST);

        /**
         * 3.2:
         * 开放给私有云客户 设置地址的接口
         * 要在helper.initSdkChat()之前设置
         */
//        RequestUrl.setRequestUrl();

        /**
         * 3.2.1:
         * 开放给私有云客户 设置文件服务地址的接口，如果私有云后端配置了文件服务器则需要调用。
         * 要在helper.initSdkChat()之前设置
         * 示例：RequestUrl.setFileUrl( "https://im-fileserver:8000/",new String[]{"im-fileserver:8000"},true)
         */
//        RequestUrl.setFileUrl( "完整的服务地址",new String[]{"只需填写中间域名以及端口部分"},是否为https);


        /**
         * 4:配置图片加载器
         * demo 提供的为Glide加载框架,可以根据自身项目需求配置
         */
        helper.setImageLoader(new GlideImageLoader());

    }

    //更换主题颜色
    private void switchTheme() {
        themeType = themeType == 0 ? 1 : 0;
        MoorSPUtils.getInstance().put(YKFConstants.SYSTHEME, themeType, true);
        recreate();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            //初始化SDK
            initSdk(helper);
        } else if (v.getId() == R.id.setting) {
            //获取未读消息数示例
            getUnReadCount();
            //更改主题
            switchTheme();
        }
    }

    /**
     * 初始化SDK
     */
    private void initSdk(final KfStartHelper helper) {
        /*
          卡片信息实例，若有需要，请参照此方法；
        */
//        handleCardInfo();

        /*
          新卡片类型示例，若有需要，请参照此方法；
         */
        handleNewCardInfo();

        setOtherParams();//添加配置拓展参数，如需使用注意要在initSdkChat之前之前用

        /*
          第三步:设置参数
          初始化sdk方法，必须先调用该方法进行初始化后才能使用IM相关功能
          @param accessId       接入id（需后台配置获取）
          @param userName       用户名 (非空)
          @param userId         用户id (非空)
         */
        IMChatManager.setUSE_ForegroundService(true);
        helper.initSdkChat(accessId, userName, userId);

    }

    /**
     * 获取未读消息数示例
     */
    private void getUnReadCount() {

        /*
         获取未读消息数R
         注意：调用此方法前也需要先配置服务:RequestUrl.setRequestUrl
         @param accessId       接入id（需后台配置获取）
         @param userName       用户名
         @param userId         用户id
         */
        IMChatManager.getInstance().getMsgUnReadCountFromService(
                accessId, userName, userId, new IMChatManager.HttpUnReadListen() {
                    @Override
                    public void getUnRead(int acount) {
                        Toast.makeText(MainActivity.this, getString(R.string.ykfsdk_ykf_unreadmsg) + "  " + acount, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * CardInfo信息示例
     */
    private void handleCardInfo() {
        String s = "https://wap.boosoo.com.cn/bobishop/goodsdetail?id=10160&mid=36819";
//        String s = "https://share1.atoshi.cn/#/productdetail?productId=376&userId=100123350544";
        CardInfo ci = new CardInfo("http://seopic.699pic.com/photo/40023/0579.jpg_wh1200.jpg", "我是一个标题当初读书", "我是name当初读书。", "价格 1000-9999", "https://www.baidu.com");
        String icon = "https://lv-img.szgreenleaf.com/brandImg/%E5%8D%AB%E7%94%9F%E5%B7%BE%E6%97%A5%E7%94%A8-79071590495933511.jpg?Expires=1905855933&OSSAccessKeyId=LTAIfEz63bW0rbAT&Signature=jpcvpoCGcpWlweVaC6iWUYIBvys%3D";
        String title = "美式北欧吊灯家\t居灯卧室客厅书房餐厅灯D1-31008-12头";
        String content = "8头/φ520*H350/96W 天下灯仓包装 黑色";
        String rigth3 = " ¥548.00";
        try {
            ci = new CardInfo(URLEncoder.encode(icon, "utf-8"), URLEncoder.encode(title, "utf-8"),
                    URLEncoder.encode(content, "utf-8"), URLEncoder.encode(rigth3, "utf-8"),
                    URLEncoder.encode(s, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        IMChatManager.getInstance().setCardInfo(ci);
    }

    /**
     * NewCardInfo卡片类型示例,{@link NewCardInfo.Builder()} Builder中默认添加了一些字段，请在此自行定制
     */
    private void handleNewCardInfo() {
        NewCardInfoTags tags1 = new NewCardInfoTags();
        tags1.setLabel("按钮1");
        tags1.setUrl("https://www.baidu.com/?tn=64075107_1_dg");
        tags1.setShowRange("all");//该字段不传在或者为all 访客端座席端都展示,seats只座席端展示，visitor只访客端展示

        NewCardInfoTags tags2 = new NewCardInfoTags();
        tags2.setLabel("按钮2");
        tags2.setUrl("https://www.sogou.com/");
        tags2.setShowRange("all");//该字段不传在或者为all 访客端座席端都展示,seats只座席端展示，visitor只访客端展示

        ArrayList<NewCardInfoTags> tags = new ArrayList<>();
        tags.add(tags1);
        tags.add(tags2);

        NewCardInfo newCardInfo = new NewCardInfo.Builder()
                .setTitle("我是标题")
                .setAttr_one(new NewCardInfoAttrs().setColor("#487903").setContent("x9"))
                .setAttr_two(new NewCardInfoAttrs().setColor("#845433").setContent("未发货"))
                .setOther_title_one("附件信息1")
                .setOther_title_two("附件信息2")
                .setOther_title_three("附件信息3")
                .setSub_title("我是副标题")
                .setPrice("$999")
                .setImg("http://seopic.699pic.com/photo/40023/0579.jpg_wh1200.jpg")
                .setTarget("https://kf.7moor.com/login")
                .setTags(tags)
                //setCardID 自定义一个卡片id，作用是当执行自动发送时，可记录当前卡片是否发送过，这个id的赋值规则客户可以完全自定义
                //如果使用自动发送那么建议就配置cardID，否则会重复发送
                .setCardID("56467981656")
                .setAutoCardSend("false")//是否自动发送 字符串 "false"否  "true"是
                .build();

        IMChatManager.getInstance().setNewCardInfo(newCardInfo);
    }


    /**
     * 语言切换
     */
    private void initLanguage() {
        Locale locale = new Locale("en", "US");
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        //Android 7.0以上的方法
        if (Build.VERSION.SDK_INT >= 24) {
            configuration.setLocale(locale);
            configuration.setLocales(new LocaleList(locale));
            createConfigurationContext(configuration);
            resources.updateConfiguration(configuration, metrics);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Android 4.1 方法
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, metrics);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, metrics);
        }
    }


    /**
     * 添加拓展参数，注意要在 initSdkChat之前之前用
     */
    private void setOtherParams() {
        try {
            JSONObject object = new JSONObject();
            object.put("test05", "test05");


            JSONObject userlabel = new JSONObject();
//            userlabel.put("userlabel1", "userlabel1");

            IMChatManager.getInstance().setUserOtherParams("", object, true, userlabel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
