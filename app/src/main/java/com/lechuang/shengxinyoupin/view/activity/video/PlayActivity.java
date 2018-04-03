package com.lechuang.shengxinyoupin.view.activity.video;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.utils.Utils;

/**
 * 单独的视频播放页面，跳转请调用PlayActivity.gotoActivity()
 */
public class PlayActivity extends AppCompatActivity {
    public static void gotoActivity(Activity activity, View view, String videoUrl, String imgs) {
        gotoActivity(activity, videoUrl);
    }

    public static void gotoActivity(Activity activity, String videoUrl) {
        try {
            Uri uri = Uri.parse(videoUrl);
            Intent intent = new Intent(activity, PlayActivity.class);
            intent.putExtra("video_uri", uri);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.show("视频链接地址错误");
        }
    }

    VideoView videoView;
    private int currentPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        videoView = findViewById(R.id.video_player);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(getIntent().getParcelableExtra("video_uri"));
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("currentPosition", 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //写在这个方法里面是因为onStop无法获取videoView.getCurrentPosition()
        currentPosition = videoView.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentPosition != 0) {
            videoView.seekTo(currentPosition);
            currentPosition = 0;
        }
        videoView.start();
        videoView.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //如果按home健，videoView.getCurrentPosition()会返回0，因此需要在onPause()中设置currentPosition
        if (outState != null) {
            if (currentPosition == 0) {
                currentPosition = videoView.getCurrentPosition();
            }
            outState.putInt("currentPosition", currentPosition);
        }
    }
}
