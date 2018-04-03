package com.lechuang.shengxinyoupin.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.home.ProductDetailsActivity;

import java.util.List;

/**
 * Created by YRJ
 * Date 2018/3/16.
 */

public interface ProductAdapter {
    class GridAdapter1 extends CommonRecyclerAdapter<ProductListBean.ProductBean> {
        /**
         * 是否有视频播放的图片
         */
        private boolean hasVedio;

        public GridAdapter1(Context context, List<ProductListBean.ProductBean> datas) {
            this(context, datas, false);
        }

        public GridAdapter1(Context context, List<ProductListBean.ProductBean> datas, boolean hasVedio) {
            super(context, R.layout.product_item_grid_1, datas);
            this.hasVedio = hasVedio;
        }

        @Override
        public void convert(ViewHolderRecycler holder, ProductListBean.ProductBean bean) {
            TextView tvOldPrice = holder.getView(R.id.tv_oldprice);
            tvOldPrice.setText("¥" + bean.price);
            //销量
            holder.setText(R.id.tv_xiaoliang, "月销" + bean.nowNumber);
            //优惠券
            if (Utils.isZero(bean.couponMoney)) {
                tvOldPrice.getPaint().setFlags(0);
            } else {
                tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            }
            holder.setText(R.id.coupon_money, "券 ￥" + bean.couponMoney);
            //券后价
            holder.setText(R.id.tv_nowprice, "￥" + bean.preferentialPrice);

            ImageView iv_img=holder.getView(R.id.iv_img);
            //图片
            holder.displayImage(iv_img, bean.imgs, R.drawable.loading_square);

            //商品名字
            holder.setTextViewImageSpan(R.id.name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
            holder.getView(R.id.video_play_icon).setVisibility(hasVedio ? View.VISIBLE : View.GONE);
            if (hasVedio) {
                iv_img.setTag(R.integer.tag_id_1,bean);
                if (!iv_img.hasOnClickListeners()) {
                    iv_img.setOnClickListener(v -> {
                        ProductListBean.ProductBean b = (ProductListBean.ProductBean) v.getTag(R.integer.tag_id_1);
                        onClickVideo(b);
                    });
                }
            }
        }

        @Override
        public void onClick(ProductListBean.ProductBean bean) {
            mContext.startActivity(new Intent(mContext, ProductDetailsActivity.class)
                    .putExtra(Constants.listInfo, JSON.toJSONString(bean)));
        }

        /**
         * 点击了视频
         */
        protected void onClickVideo(ProductListBean.ProductBean bean) {
        }
    }

    class ListAdapter1 extends CommonRecyclerAdapter<ProductListBean.ProductBean> {
        protected ListAdapter1(Context context, List<ProductListBean.ProductBean> datas) {
            super(context, R.layout.product_item_list_1, datas);
        }

        @Override
        public void convert(ViewHolderRecycler holder, ProductListBean.ProductBean bean) {
            //商品图
            holder.displayImage(R.id.iv_img, bean.imgs, R.drawable.loading_square);
//        //动态调整滑动时的内存占用
            holder.setTextViewImageSpan(R.id.name, bean.name, "1".equals(bean.shopType) ? 1 : 0);
            //销量
            holder.setText(R.id.tv_xiaoliang, "已抢" + bean.nowNumber + "件");
            LinearLayout tt = holder.getView(R.id.tt);
            TextView tvOldprice = holder.getView(R.id.tv_oldprice);
            Glide.get(mContext).setMemoryCategory(MemoryCategory.LOW);
            //领券减金额
            //商品名字
            if (Utils.isZero(bean.couponMoney)) {
                tt.setVisibility(View.GONE);
                tvOldprice.getPaint().setFlags(0);
            } else {
                tt.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_nowprice, "¥" + bean.preferentialPrice);
                holder.setText(R.id.tv_quanMoney, "领券减" + bean.couponMoney);
                tvOldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
            //原价
            holder.setText(R.id.tv_oldprice, bean.price + "");

            //中划线
            //券后价
            holder.setText(R.id.zhuanMoney, bean.zhuanMoney);
        }
    }
    class ListAdapter2 extends CommonRecyclerAdapter<ProductListBean.ProductBean> {
        protected ListAdapter2(Context context, List<ProductListBean.ProductBean> datas) {
            super(context, R.layout.product_item_list_2, datas);
        }

        @Override
        public void convert(ViewHolderRecycler holder, ProductListBean.ProductBean bean) {
            TextView tvOldPrice = holder.getView(R.id.tv_oldprice);
            tvOldPrice.setText("¥" + bean.price);
            //销量
            holder.setText(R.id.tv_xiaoliang, "已疯抢" + bean.nowNumber + "件");
            //优惠券
            if (Utils.isZero(bean.couponMoney)) {
                tvOldPrice.getPaint().setFlags(0);
            } else {
                tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            }
            holder.setText(R.id.coupon_money, "优惠券:" + bean.couponMoney + "元");
            //券后价
            holder.setText(R.id.tv_nowprice, "￥" + bean.preferentialPrice);
            //图片
            holder.displayImage(R.id.iv_img, bean.imgs, R.drawable.loading_square);

            //商品名字
            holder.setTextViewImageSpan(R.id.name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
        }
    }
    class ListAdapter3 extends CommonRecyclerAdapter<ProductListBean.ProductBean> {
        protected ListAdapter3(Context context, List<ProductListBean.ProductBean> datas) {
            super(context, R.layout.product_item_list_3, datas);
        }

        @Override
        public void convert(ViewHolderRecycler holder, ProductListBean.ProductBean bean) {
            TextView tvOldPrice = holder.getView(R.id.tv_oldprice);
            tvOldPrice.setText("¥" + bean.price);
            //销量
            holder.setText(R.id.tv_xiaoliang, "销量：" + bean.nowNumber);
            //优惠券
            if (Utils.isZero(bean.couponMoney)) {
                tvOldPrice.getPaint().setFlags(0);
            } else {
                tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            }
            holder.setText(R.id.coupon_money, "领券减￥" + bean.couponMoney);
            //券后价
            holder.setText(R.id.tv_nowprice, "￥" + bean.preferentialPrice);
            //图片
            holder.displayImage(R.id.iv_img, bean.imgs, R.drawable.loading_square);

            //商品名字
            holder.setTextViewImageSpan(R.id.name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
            //赚
            holder.setText(R.id.zhuan, bean.zhuanMoney);
        }
    }
}
