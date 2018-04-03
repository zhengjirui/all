package com.lechuang.shengxinyoupin.model.bean;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * 一个比较通用的商品属性
 */
public class ProductListBean {
    public List<ProductBean> productList;
    /**
     * 广告图，不一定有值
     */
    @Nullable
    public List<ImageBean> indexBannerList;

    public static class ProductBean {
        public String id;
        //图片
        public String imgs;
        //商品标题
        public String name;
        //价格
        public String price;
        //券后价
        public String preferentialPrice;
        //商品id,传给H5页面的参数
        public String alipayItemId;
        /* //转链接之后的链接
         public String tbPrivilegeUrl;*/
        public String alipayCouponId;
        // 1 淘宝 2天猫
        public String shopType;
        public String shareIntegral;
        // 赚金额
        public String zhuanMoney;
        // 销量
        public int nowNumber;
        // 券金额
        public String couponMoney;
        //分享图片列表
        public List<String> smallImages;
        //分享文案
        public String shareText;

        public String videoUrl;
    }

    public static class ImageBean {
        public String id;
        //图片
        public String img;
        //链接
        public String link;
        public int type;
        public int programaId;
    }
}
