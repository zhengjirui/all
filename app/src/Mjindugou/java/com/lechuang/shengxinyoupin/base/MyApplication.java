package com.lechuang.shengxinyoupin.base;

import com.lechuang.shengxinyoupin.view.activity.home.ExclusiveSearchResultActivity;

public class MyApplication extends AbsApplication {
    @Override
    public Class<?> getSearchResultActivity() {
        return ExclusiveSearchResultActivity.class;
    }
}
