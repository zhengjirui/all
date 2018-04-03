package com.lechuang.shengxinyoupin.view.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.view.activity.video.PlayActivity;
import com.lechuang.shengxinyoupin.view.adapter.ProductAdapter;

import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 短视频列表
 */
public class VedioListFragment extends AbsProductListFragment {
    @Override
    protected void initParamMap(HashMap<String, String> paramMap, Bundle bundle) {

    }

    @Override
    protected void requestNetword(HashMap<String, String> paramMap, ResultBack<ProductListBean> resultBack) {
        Netword.getInstance().getApi(HomeApi.class)
                .homeVedioProduct(paramMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBack);
    }

    @NonNull
    @Override
    protected CommonRecyclerAdapter<ProductListBean.ProductBean> initAdapter(List<ProductListBean.ProductBean> data) {
        return new ProductAdapter.GridAdapter1(getActivity(), data, true) {
            @Override
            protected void onClickVideo(ProductListBean.ProductBean bean) {
                PlayActivity.gotoActivity(getActivity(), getView(), bean.videoUrl, bean.imgs);
            }
        };
    }

    @NonNull
    @Override
    protected LinearLayoutManager initLinearLayoutManager() {
        return new GridLayoutManager(getActivity(), 2);
    }
}
