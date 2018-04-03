package com.lechuang.shengxinyoupin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lechuang.shengxinyoupin.base.AbsActivity;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.model.LocalSession;
import com.lechuang.shengxinyoupin.model.bean.GetHostUrlBean;
import com.lechuang.shengxinyoupin.model.bean.OwnCheckVersionBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.QUrl;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.CommenApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.ui.LoginActivity;
import com.lechuang.shengxinyoupin.view.dialog.VersionUpdateDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 主界面
 */
public abstract class AbsMainActivity extends AbsActivity {
//    @BindView(R.id.ll_contest)
//    LinearLayout llContest;
//    @BindView(R.id.main_home)
//    TextView mainHome;
//    @BindView(R.id.main_tipoff)
//    TextView mainTipoff;
//    @BindView(R.id.main_thesun)
//    TextView mainThesun;
//    @BindView(R.id.main_own)
//    TextView mainOwn;
//    @BindView(R.id.content)
//    LinearLayout content;
//    @BindView(R.id.main_live)
//    TextView mainLive;
    //fragment集合
//    private List<Fragment> fragments;
//    //textview集合
//    private List<TextView> views;

    private ArrayList<BottomItem> bottomItems;
//    //指定显示的界面
//    private int oldIndex = 0;

    @Nullable
    private BottomItem oldBottomItem;

    //    private HomeTabBarFragment homeFragment;
//    private LiveFragment liveFragment;
//    private Fragment getFragment;
//    private SouFanLiFragmentNew souFanLiFragment;
//    private OwnFragment ownFragment;
    private SharedPreferences sp;
    private LocalSession mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContextID());
        ButterKnife.bind(this);
        Utils.verifyStoragePermissions(this);
        mSession = LocalSession.get(AbsMainActivity.this);
        //读取用户信息
        sp = PreferenceManager.getDefaultSharedPreferences(AbsMainActivity.this);
//        initView();
        initBottomItems();
        setUserInfo();
//        new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                initFragments();
//                return false;
//            }
//        }).sendEmptyMessageDelayed(0, 500);
        getData();
        deleteImgs();
    }

    @LayoutRes
    protected abstract int getContextID();

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        setUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 检查更新  获取分享商品域名
     */
    public void getData() {

        Netword.getInstance().getApi(CommenApi.class)
                .getShareProductUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<GetHostUrlBean>(this) {
                    @Override
                    public void successed(GetHostUrlBean result) {
                        Utils.E(result.show.appHost);
                        sp.edit().putString(Constants.getShareProductHost, result.show.appHost).apply();
                    }
                });

        Netword.getInstance().getApi(CommenApi.class)
                .updataVersion("1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnCheckVersionBean>(this) {
                    @Override
                    public void successed(OwnCheckVersionBean result) {
                        // ((TextView) findViewById(R.id.tv_versiondesc)).setText(result.app.versionDescribe);
                        if (!Utils.getAppVersionName(AbsMainActivity.this).equals(result.maxApp.versionNumber)) {//版本号
                           /* UpdateVersion version = new UpdateVersion(mContext);
                            version.setDescribe(result.maxApp.versionDescribe);//版本描述
                            if (result.maxApp.downloadUrl != null)
                                version.setUrl(QUrl.url + result.maxApp.downloadUrl);//下载地址
                            version.showUpdateDialog();*/
                            VersionUpdateDialog version = new VersionUpdateDialog(AbsMainActivity.this, result.maxApp.versionDescribe);
                            //下载地址
                            if (result.maxApp.downloadUrl != null)
                                version.setUrl(QUrl.url + result.maxApp.downloadUrl);
                            version.show();
                        }
                    }
                });
    }



   /* @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getIntExtra("start", 0) == 1) {
            showCurrentFragment(0);
        }
    }*/

    /**
     * 初始化底部的按钮
     */
    private void initBottomItems() {
        bottomItems = new ArrayList<>();
        ViewGroup content = findViewById(R.id.content);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomItem bottomItem = (BottomItem) v.getTag();
                if (bottomItem.isNeedLogin && !sp.getBoolean("isLogin", false)) {
                    startActivity(new Intent(AbsMainActivity.this, LoginActivity.class));
                } else {
                    if (bottomItem != oldBottomItem) {
                        FragmentTransaction ft = getSupportFragmentManager()
                                .beginTransaction();
                        if (oldBottomItem != null) {
                            oldBottomItem.item.setSelected(false);
                            ft.hide(oldBottomItem.fragment);
                        }
                        bottomItem.item.setSelected(true);
                        if (!bottomItem.fragment.isAdded()) {
                            ft.add(R.id.ll_contest, bottomItem.fragment, bottomItem.fragment.getClass().getName());
                        } else {
                            ft.show(bottomItem.fragment);
                        }
                        ft.commit();
                        oldBottomItem = bottomItem;
                    }
                }
            }
        };
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            BottomItem bi = newBottomItem(i, v, fm);
            if (bi == null) {
                continue;
            }
            if (bi.fragment.isAdded()) {
                fm.beginTransaction().hide(bi.fragment).commit();
            }
            v.setTag(bi);
            v.setOnClickListener(onClickListener);
        }
        //执行第一个的单击事件
        backHomeFragment();
    }

    protected abstract BottomItem newBottomItem(int index, View item, FragmentManager fm);

    /**
     * 回到首页
     */
    public void backHomeFragment() {
        ViewGroup content = findViewById(R.id.content);
        content.getChildAt(0).callOnClick();
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/21  17:12
     * @describe 初始化应用操作
     */
//    private void initView() {
//
//
//        views = new ArrayList<>();
//        views.clear();
//        views.add(mainHome);
//        views.add(mainTipoff);
//        views.add(mainLive);
//        views.add(mainThesun);
//        views.add(mainOwn);
//        views.get(0).setSelected(true);
//    }

    //@OnClick({R.id.main_live, R.id.main_home, R.id.main_tipoff, R.id.main_thesun, R.id.main_own})
//    @OnClick({R.id.main_home, R.id.main_tipoff, R.id.main_live, R.id.main_thesun, R.id.main_own})
//    public void onViewClicked(View view) {
//        int current = oldIndex;
//        switch (view.getId()) {
//            case R.id.main_live:
//                current = 2;
//                break;
//            case R.id.main_home:
//                current = 0;
//                break;
//            case R.id.main_tipoff:
//                current = 1;
//                break;
//            case R.id.main_thesun:
//                current = 3;
//                break;
//            case R.id.main_own:
//                if (!sp.getBoolean("isLogin", false)) {
//                    startActivity(new Intent(this, LoginActivity.class));
//                } else {
//                    current = 4;
//                }
//                break;
//        }
//        showCurrentFragment(current);
//    }

//    /**
//     * 初始化用到的Fragment
//     */
//    private void initFragments() {
//        homeFragment = new HomeTabBarFragment();
//        liveFragment = new LiveFragment();
//        if (BuildConfig.isHelpInMain) {
//            getFragment = new HelpCenterFragment();
//            mainTipoff.setText("使用教程");
//            Drawable d = getResources().getDrawable(R.drawable.main_btn_get);
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            mainTipoff.setCompoundDrawables(null, d, null, null);
//        } else {
//            getFragment = new BaoLiaoFragment();
//            mainTipoff.setText("爆料");
//            Drawable d = getResources().getDrawable(R.drawable.main_btn_baoliao);
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            mainTipoff.setCompoundDrawables(null, d, null, null);
//        }
//        souFanLiFragment = new SouFanLiFragmentNew();
//        ownFragment = new OwnFragment();
//        fragments = new ArrayList<>();
//        fragments.clear();
//        fragments.add(homeFragment);
//        fragments.add(getFragment);
//        fragments.add(souFanLiFragment);
//        fragments.add(liveFragment);
//        fragments.add(ownFragment);
////        默认加载前两个Fragment，其中第一个展示，第二个隐藏
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.ll_contest, homeFragment)
////                .add(R.id.ll_contest, tipOffFragment)
////                .hide(tipOffFragment)
//                .show(homeFragment)
//                .commit();
////        showCurrentFragment(0);
//
//    }

    //设置用户信息
    private void setUserInfo() {
        mSession.setId(sp.getString("id", ""));
        mSession.setImge(sp.getString("photo", ""));
        mSession.setName(sp.getString("nickName", ""));
        mSession.setPhoneNumber(sp.getString("phone", ""));
        mSession.setAccountNumber(sp.getString("taobaoNumber", ""));
        mSession.setAlipayNumber(sp.getString("alipayNumber", ""));
        mSession.setIsAgencyStatus(sp.getInt("isAgencyStatus", 0));
        mSession.setSafeToken(sp.getString("safeToken", ""));
        mSession.setLogin(sp.getBoolean("isLogin", false));

    }

//    /**
//     * 展示当前选中的Fragment
//     *
//     * @param currentIndex
//     */
//    public void showCurrentFragment(int currentIndex) {
//        if (currentIndex != oldIndex) {
//            views.get(oldIndex).setSelected(false);
//            views.get(currentIndex).setSelected(true);
//            FragmentTransaction ft = getSupportFragmentManager()
//                    .beginTransaction();
//            ft.hide(fragments.get(oldIndex));
//            if (!fragments.get(currentIndex).isAdded()) {
//                ft.add(R.id.ll_contest, fragments.get(currentIndex));
//            }
//            ft.show(fragments.get(currentIndex)).commit();
//            oldIndex = currentIndex;
//        }
//    }

    /**
     * 双击返回
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(AbsMainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {

                @Override
                public void run() {

                    isExit = false;
                }
            }, 2000);
        } else {
            AbsApplication.getInstance().exit();
            Process.killProcess(Process.myPid());
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }


    /**
     * 删除手机内存中的图片(分享商品 和分享app)
     */
    private void deleteImgs() {
        File dirs = new File(getExternalCacheDir() + "");
        Utils.deleteAllFiles(dirs);
    }

    protected class BottomItem {
        private boolean isNeedLogin;
        private View item;
        private Fragment fragment;

        public BottomItem(boolean isNeedLogin, View item, Fragment fragment) {
            this.isNeedLogin = isNeedLogin;
            this.item = item;
            this.fragment = fragment;
        }
    }
}
