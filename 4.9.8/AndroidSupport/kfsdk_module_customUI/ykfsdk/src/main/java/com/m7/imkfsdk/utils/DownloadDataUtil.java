package com.m7.imkfsdk.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.moor.imkf.utils.LogUtils;
import com.moor.imkf.utils.MoorKFfileUtils;

import java.io.File;

public class DownloadDataUtil {


    private final DownloadManager manager;
    private long downloadId;


    Context mcontext;

    private final boolean checkout = true;

    BroadcastReceiver broadcastReceiver;


    public DownloadDataUtil(Context mcontext) {
        this.mcontext = mcontext;
        manager = (DownloadManager) mcontext.getSystemService(Context.DOWNLOAD_SERVICE);
    }


    public void downurl(String downurl, String fileName) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        query.setFilterByStatus(DownloadManager.STATUS_RUNNING);//正在下载
        Cursor c = manager.query(query);
        if (c.moveToNext()) {
            //正在下载中，不重新下载
        } else {
            DownloadManager.Request down = new DownloadManager.Request(Uri.parse(downurl));
            //设置允许使用的网络类型，这里是移动网络和wifi都可以
            down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            //显示在下载界面，即下载后的文件在系统下载管理里显示
            down.setVisibleInDownloadsUi(true);
            //设置下载标题
            down.setTitle(fileName);
            //显示Notification
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);


            //将下载请求放入队列,返回值为downloadId
            downloadId = manager.enqueue(down);

            listener(downloadId);

        }
    }


    // 这个是能够监听到的
    private void listener(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long mID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (mID == Id) {
                    LogUtils.d("UpDataUtil-->", " 下载完成!");
                    // getPath(ID);
                    context.unregisterReceiver(broadcastReceiver);
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mcontext.registerReceiver(broadcastReceiver, intentFilter,Context.RECEIVER_NOT_EXPORTED);
        }else{
            mcontext.registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    public void getPath(long downloadId) {
        //通过downloadId去查询下载的文件名
        LogUtils.d("DownloadReceiver--->", downloadId + "");
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor myDownload = manager.query(query);
        if (myDownload.moveToFirst()) {
            String fileName = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                int columnIndex = myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                LogUtils.d("DownloadReceiver--->", columnIndex + "");
                String string = myDownload.getString(columnIndex);
                LogUtils.d("DownloadReceiver--->", string + "");
                //这个是国外大神发现的骚操作
                //https://stackoverflow.com/questions/38839688/downloadmanager-column-local-filename-deprecated
                String replace = string.replace("file://", "");
                LogUtils.d("DownloadReceiver--->", replace + "");
                fileName = replace;
                Uri uri = MoorKFfileUtils.fileToUri(new File(replace));
                String path = PickUtils.getPath(mcontext, uri);
                LogUtils.d("this-->", "this path is-->" + path);
            } else {
                //在7.0的系统这里会报错！！！
                int fileNameIdx = myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                fileName = myDownload.getString(fileNameIdx);
                // Log.e("--",fileName);
            }
            if (fileName == null) {
                LogUtils.d("DownloadReceiver--->", "this is error");
                return;
            }
        }
    }

}
