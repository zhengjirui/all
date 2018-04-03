package com.lechuang.shengxinyoupin.view.activity.home;

import android.os.Bundle;

import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 必须传参数，type，类型int<br>
 * isActivity，类型boolean，默认false
 */
public abstract class AbsKindDetailFragmeng extends AbsProductListFragment {
    @Override
    protected void initParamMap(HashMap<String, String> paramMap, Bundle bundle) {
        //两种类型都可以
        String type = bundle.getString("type", null);
        if (type != null) {
            paramMap.put("type", type);
        } else {
            paramMap.put("type", bundle.getInt("type", 1) + "");
        }
    }

    @Override
    protected void requestNetword(HashMap<String, String> paramMap, ResultBack<ProductListBean> resultBack) {
        Netword.getInstance().getApi(HomeApi.class)
                .searchResult(paramMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBack);
    }
}
