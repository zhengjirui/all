package com.lechuang.shengxinyoupin.presenter.net;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.model.bean.ProductDetailsBean;
import com.lechuang.shengxinyoupin.model.bean.ResultBean;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;

import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date 2018/3/22.
 */
public class NetwordUtil {
    /**
     * 查询商品信息
     */
    public static void queryProductDetails(String productID, ResultBack<ProductDetailsBean> resultBack) {
        HomeApi homeApi = Netword.getInstance().getApi(HomeApi.class);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AbsApplication.getInstance());
        //用户id
        String userId = sp.getString("id", "");
        Observable<ResultBean<ProductDetailsBean>> observable;
        if (userId.length() == 0) {
            observable = homeApi.queryProductDetails(productID);
        } else {
            observable = homeApi.queryProductDetails(productID, userId);
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBack);
    }

    /**
     * 查询商品信息
     */
    public static void queryProductDetails(HashMap<String, String> paramMap, ResultBack<ProductDetailsBean> resultBack) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AbsApplication.getInstance());
        //用户id
        String userId = sp.getString("id", "");
        if (userId.length() != 0) {
            paramMap.put("userId", userId);
        }
        Netword.getInstance().getApi(HomeApi.class)
                .queryProductDetails(paramMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBack);
    }
}
