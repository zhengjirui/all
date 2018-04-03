package com.lechuang.shengxinyoupin.view.activity.soufanli;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.lechuang.shengxinyoupin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：li on 2017/11/13 15:26
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class SoufanliInfo extends AppCompatActivity {
    @BindView(R.id.iv_InfoBcak)
    ImageView ivInfoBcak;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soufanli);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_InfoBcak)
    public void onViewClicked() {
        finish();
    }
}
