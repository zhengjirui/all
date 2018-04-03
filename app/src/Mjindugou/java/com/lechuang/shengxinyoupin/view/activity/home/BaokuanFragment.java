package com.lechuang.shengxinyoupin.view.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.view.adapter.ProductAdapter;

import java.util.List;

/**
 * Created by YRJ
 * Date 2018/3/16.
 */

public class BaokuanFragment extends AbsKindDetailFragmeng {
    ImageView headerView;
    public BaokuanFragment() {
        Bundle b = new Bundle();
        b.putString("name", "爆款疯抢");
        b.putString("type", "8");
        setArguments(b);
    }

    @Nullable
    @Override
    protected View initHeaderView() {
        headerView = new ImageView(getActivity());
        headerView.setScaleType(ImageView.ScaleType.FIT_XY);
//        headerView.setImageDrawable(new ColorDrawable(0xFFFF00FF));
        int width = getActivity().getResources().getDisplayMetrics().widthPixels;
        headerView.setLayoutParams(new ViewGroup.LayoutParams(width, (int) (width /3)));
        return headerView;
    }

    @NonNull
    @Override
    protected CommonRecyclerAdapter<ProductListBean.ProductBean> initAdapter(List<ProductListBean.ProductBean> data) {
        return new ProductAdapter.ListAdapter2(getActivity(), data) {
            @Override
            public void onClick(ProductListBean.ProductBean bean) {
                startActivity(new Intent(mContext, ProductDetailsActivity.class)
                        .putExtra(Constants.listInfo, JSON.toJSONString(bean)));
            }
        };
    }

    @NonNull
    @Override
    protected LinearLayoutManager initLinearLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected void kindDataAtFirstPage(ProductListBean result) {
        if (result.indexBannerList == null || result.indexBannerList.isEmpty()) {
            headerView.setVisibility(View.GONE);
        } else {
            headerView.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(result.indexBannerList.get(0).img).placeholder(R.drawable.rvbaner).into(headerView);
        }
    }
}
