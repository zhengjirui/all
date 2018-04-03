package com.lechuang.shengxinyoupin.view.activity.home;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;

import java.util.List;

/**
 * Created by YRJ
 * Date 2018/3/16.
 */

public class KindDetailFragmeng extends AbsKindDetailFragmeng {
    @NonNull
    @Override
    protected CommonRecyclerAdapter<ProductListBean.ProductBean> initAdapter(List<ProductListBean.ProductBean> data) {
        return new CommonRecyclerAdapter<ProductListBean.ProductBean>(getActivity(), R.layout.item_program_product, data) {
            @Override
            public void convert(ViewHolderRecycler viewHolder, ProductListBean.ProductBean bean) {
                try {
                    viewHolder.setText(R.id.tv_xiaoliang, "销量: " + bean.nowNumber + "件");
                    viewHolder.displayImage(R.id.iv_img, bean.imgs, R.drawable.loading_square);
                    //原件
                    TextView tvOldPrice = viewHolder.getView(R.id.tv_oldprice);
                    tvOldPrice.setText("¥" + bean.price);
                    tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    viewHolder.setSpannelTextViewGrid(R.id.tv_name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
                    viewHolder.setText(R.id.tv_nowprice, "券后价 ¥" + bean.preferentialPrice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

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
        return new GridLayoutManager(getActivity(), 2);
    }
}
