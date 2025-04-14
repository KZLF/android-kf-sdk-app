package com.m7.imkfsdk.chat;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.m7.imkfsdk.R;
import com.m7.imkfsdk.utils.FileUtils;
import com.m7.imkfsdk.utils.RegexUtils;
import com.m7.imkfsdk.utils.statusbar.StatusBarUtils;
import com.moor.imkf.YKFConstants;
import com.moor.imkf.lib.constants.MoorPathConstants;
import com.moor.imkf.lib.http.donwload.IMoorOnDownloadListener;
import com.moor.imkf.lib.http.donwload.MoorDownLoadUtils;


//视频播放页面
public class YKFVideoActivity extends KFBaseActivity {

    private Context mContext;
    private VideoView ykf_videoview;
    private ImageView iv_closevideo;
    private TextView save_file_path;
    private String video_url;
    private String video_name;
    private RelativeLayout rl_video_progress;
    private LinearLayout ll_video_path;
    private String dirStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.ykfsdk_ykf_videoactivity);
        StatusBarUtils.setTransparent(this);
        ykf_videoview = findViewById(R.id.ykf_videoview);
        iv_closevideo = findViewById(R.id.iv_closevideo);
        rl_video_progress = findViewById(R.id.rl_video_progress);
        save_file_path = findViewById(R.id.save_file_path);
        ll_video_path = findViewById(R.id.ll_video_path);

        video_url = getIntent().getStringExtra(YKFConstants.YKFVIDEOPATHURI);
        video_name = getIntent().getStringExtra(YKFConstants.YKFVIDEOFILENAME);

        if (!RegexUtils.checkURL(video_url)) {
            initVideo(video_url);
            ll_video_path.setVisibility(View.GONE);
        } else {
            ll_video_path.setVisibility(View.VISIBLE);
            String absoluteFilePath = MoorPathConstants.getStoragePath("Moor_Download_File") + video_name;
            save_file_path.setText("文件管理/内部存储(我的手机)" + absoluteFilePath.substring(absoluteFilePath.indexOf("/Android/")));
            if (FileUtils.isExists(absoluteFilePath)) {
                initVideo(absoluteFilePath);
            } else {
                MoorDownLoadUtils.loadFile(video_url, video_name, new IMoorOnDownloadListener() {
                    @Override
                    public void onDownloadStart() {

                    }

                    @Override
                    public void onDownloadSuccess(final String filePath) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                save_file_path.setText(filePath);
                                initVideo(filePath);
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {

                    }
                });
            }
        }

        iv_closevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initVideo(String url) {
        Uri uri = Uri.parse(url);

        ykf_videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        //视频显示出第一帧隐藏 progress
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            rl_video_progress.setVisibility(View.GONE);
                        }

                        return true;
                    }
                });
            }

        });


        //播放完成回调
        ykf_videoview.setOnCompletionListener(new MyPlayerOnCompletionListener());

        //设置视频路径
        ykf_videoview.setVideoURI(uri);
        //设置视频控制器
        MediaController mediaController = new MediaController(this);
        ykf_videoview.setMediaController(mediaController);
        mediaController.setMediaPlayer(ykf_videoview);


        //开始播放视频
        ykf_videoview.start();
        ykf_videoview.requestFocus();

    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            ykf_videoview.start();

        }
    }

    @Override
    protected void onDestroy() {
        if (ykf_videoview.isPlaying()) {
            ykf_videoview.stopPlayback();
        }
        super.onDestroy();
    }

}
