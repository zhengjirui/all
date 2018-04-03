package com.lechuang.shengxinyoupin.view.activity.video;

import android.support.v7.app.AppCompatActivity;

/*
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
*/

public class VideoPlayActivity extends AppCompatActivity {

   /* private VideoView mVideoView;
    private MediaController mMediaController;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        path = getIntent().getStringExtra(Extra.VIDEO_PATH);
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mVideoView.setVideoPath(this.path);//设置播放地址
        mMediaController = new MediaController(this);//实例化控制器
        mMediaController.show(5000);//控制器显示5s后自动隐藏
        mVideoView.setMediaController(mMediaController);//绑定控制器
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
        mVideoView.requestFocus();//取得焦点
    }
*/
}
