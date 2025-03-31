package com.ykfsdk.app;

import android.app.Application;

import com.moor.imkf.utils.YKFUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;

/**
 * <pre>
 *     @desc   : 项目 Application
 *     @version: 1.0
 * </pre>
 */
public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 1（重要）：
         * 请检查在 APPlication onCreate() 中的初始化
         * 初始化YKFUtils ：YKFUtils.init(this)
         *
         * 注意:如果使用三方crash监控等插件,请在初始化三方crash监控之前init本sdk,像下面demo这样的顺序.
         */
        YKFUtils.init(this);


        //bugly(demo 演示用,可移除)
        CrashReport.initCrashReport(getApplicationContext(), "33d895df1e", true);

        //友盟(demo 演示用,可移除)
        UMConfigure.init(this, "64104af1ba6a5259c41da93e", "Demo", UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(true);


    }
}
