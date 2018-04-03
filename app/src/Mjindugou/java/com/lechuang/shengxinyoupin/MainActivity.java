package com.lechuang.shengxinyoupin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.lechuang.shengxinyoupin.view.activity.home.BaokuanFragment;
import com.lechuang.shengxinyoupin.view.activity.home.HomeFragment;
import com.lechuang.shengxinyoupin.view.activity.live.LiveFragment;
import com.lechuang.shengxinyoupin.view.activity.own.OwnFragment;
import com.lechuang.shengxinyoupin.view.activity.xiaobianshuo.XiaobianFragment;

public class MainActivity extends AbsMainActivity {
    @Override
    protected int getContextID() {
        return R.layout.activity_main;
    }

    @Override
    protected BottomItem newBottomItem(int index, View item,FragmentManager fm) {
        Fragment fragment;
        boolean isNeedLogin;
        switch (index) {
            case 0:
                fragment = fm.findFragmentByTag(HomeFragment.class.getName());
                if (fragment == null) {
                    fragment = new HomeFragment();
                }
                isNeedLogin = false;
                break;
            case 1:
                fragment = fm.findFragmentByTag(BaokuanFragment.class.getName());
                if (fragment == null) {
                    fragment = new BaokuanFragment();
                }
                isNeedLogin = false;
                break;
            case 2:
                fragment = fm.findFragmentByTag(LiveFragment.class.getName());
                if (fragment == null) {
                    fragment = new LiveFragment();
                }
                isNeedLogin = false;
                break;
            case 3:
                fragment = fm.findFragmentByTag(XiaobianFragment.class.getName());
                if (fragment == null) {
                    fragment = new XiaobianFragment();
                }
                isNeedLogin = false;
                break;
            case 4:
                fragment = fm.findFragmentByTag(OwnFragment.class.getName());
                if (fragment == null) {
                    fragment = new OwnFragment();
                }
                isNeedLogin = true;
                break;
            default:
                return null;
        }
        return new BottomItem(isNeedLogin, item, fragment);
    }
}
