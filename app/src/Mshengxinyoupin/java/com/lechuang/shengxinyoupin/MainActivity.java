package com.lechuang.shengxinyoupin;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.view.activity.home.HomeFragment;
import com.lechuang.shengxinyoupin.view.activity.live.LiveFragment;
import com.lechuang.shengxinyoupin.view.activity.own.HelpCenterFragment;
import com.lechuang.shengxinyoupin.view.activity.own.OwnFragment;
import com.lechuang.shengxinyoupin.view.activity.soufanli.SouFanLiFragmentNew;
import com.lechuang.shengxinyoupin.view.activity.tipoff.BaoLiaoFragment;

public class MainActivity extends AbsMainActivity {
    @Override
    protected int getContextID() {
        return R.layout.activity_main;
    }

    @Override
    protected BottomItem newBottomItem(int index, View item, FragmentManager fm) {
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
                fragment = fm.findFragmentByTag(SouFanLiFragmentNew.class.getName());
                if (fragment == null) {
                    fragment = new SouFanLiFragmentNew();
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
                TextView tv = (TextView) item;
                if (getResources().getBoolean(R.bool.isHelpInMain)) {
                    tv.setText("使用教程");
                    Drawable d = getResources().getDrawable(R.drawable.main_btn_get);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    tv.setCompoundDrawables(null, d, null, null);
                    fragment = fm.findFragmentByTag(HelpCenterFragment.class.getName());
                    if (fragment == null) {
                        fragment = new HelpCenterFragment();
                    }
                } else {
                    tv.setText("爆料");
                    Drawable d = getResources().getDrawable(R.drawable.main_btn_baoliao);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    tv.setCompoundDrawables(null, d, null, null);
                    fragment = fm.findFragmentByTag(BaoLiaoFragment.class.getName());
                    if (fragment == null) {
                        fragment = new BaoLiaoFragment();
                    }
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
