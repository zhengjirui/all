package com.lechuang.shengxinyoupin.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.wxlib.util.SysUtil;
import com.lechuang.shengxinyoupin.BuildConfig;
import com.lechuang.shengxinyoupin.view.activity.home.SearchResultActivity;
import com.lechuang.shengxinyoupin.view.activity.own.MyOrderActivity;
import com.lechuang.shengxinyoupin.view.activity.own.MyTeamActivity;
import com.umeng.commonsdk.UMConfigure;

import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Administrator on 2017/9/18.
 */
public abstract class AbsApplication extends Application {
    private static AbsApplication myApplaction = null;
    //单例模式中获取唯一的MyApplication实例
    private List<Activity> activityList = new LinkedList<Activity>();

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/26  11:22
     * @describe 初始化百川
     */

    @Override
    public void onCreate() {
        super.onCreate();
        myApplaction = this;
        UMConfigure.init(this, null, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
                //初始化成功，设置相关的全局配置参数
                // 是否使用支付宝
                AlibcTradeSDK.setShouldUseAlipay(true);
                // 设置是否使用同步淘客打点
                AlibcTradeSDK.setSyncForTaoke(true);
                // 是否走强制H5的逻辑，为true时全部页面均为H5打开
                AlibcTradeSDK.setForceH5(true);

            }

            @Override
            public void onFailure(int code, String msg) {
                //初始化失败，可以根据code和msg判断失败原因，详情参见错误说明
            }
        });


        //极光推送
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush

        //必须首先执行这部分代码, 如果在":TCMSSevice"进程中，无需进行云旺（OpenIM）和app业务的初始化，以节省内存;
        SysUtil.setApplication(this);
        if (SysUtil.isTCMSServiceProcess(this)) {
            return;
        }
        //第一个参数是Application Context
        //这里的APP_KEY即应用创建时申请的APP_KEY，同时初始化必须是在主进程中
        if (SysUtil.isMainProcess()) {
            YWAPI.init(myApplaction, Constants.APP_KEY);
        }

        if (BuildConfig.DEBUG) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    Log.e("进入了Activity", activity + "");
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }
    }

    /**
     * 返回当前Application实力
     */
    public static <T extends AbsApplication> T getInstance() {
        return (T) myApplaction;
    }

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 跳转搜索结果的页面
     */
    public Class<?> getSearchResultActivity() {
        return SearchResultActivity.class;
    }

    /**
     * 我的团队页面
     */
    public Class<?> getMyTeamActivity() {
        return MyTeamActivity.class;
    }

    /**
     * 我的收益页面
     */
    public Class<?> getMyOrderActivity() {
        return MyOrderActivity.class;
    }
}
