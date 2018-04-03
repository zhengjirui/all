package com.lechuang.shengxinyoupin.view.activity.home;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.BaseActivity;

/**
 * Created by YRJ
 * Date 2018/3/16.
 */

public class VedioListActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction();
        VedioListFragment f = new VedioListFragment();
        Bundle bundle = getIntent().getExtras();
        bundle.putBoolean("isActivity", true);
        f.setArguments(bundle);
        ft.add(R.id.ll_context, f);
        ft.commit();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

    }
}
