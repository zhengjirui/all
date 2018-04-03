package com.lechuang.shengxinyoupin.base;

import com.lechuang.shengxinyoupin.view.activity.own.MyOrderActivity2;
import com.lechuang.shengxinyoupin.view.activity.own.MyTeamActivity2;

public class MyApplication extends AbsApplication {
    /**
     * 我的团体页面
     */
    public Class<?> getMyTeamActivity() {
        return MyTeamActivity2.class;
    }

    /**
     * 我的收益页面
     */
    public Class<?> getMyOrderActivity() {
        return MyOrderActivity2.class;
    }
}
