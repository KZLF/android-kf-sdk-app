package com.m7.imkfsdk.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.m7.imkfsdk.R;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.lib.utils.sharedpreferences.MoorSPUtils;

public class KFBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int themeType = MoorSPUtils.getInstance().getInt(YKFConstants.SYSTHEME, 0);
        if (themeType == 0) {
            setTheme(R.style.ykfsdk_KFSdkAppTheme);
        } else if (themeType == 1) {
            setTheme(R.style.ykfsdk_KFSdkAppTheme1);
        }
    }
}
