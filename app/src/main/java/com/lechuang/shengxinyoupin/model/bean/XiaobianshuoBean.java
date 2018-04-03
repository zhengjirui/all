package com.lechuang.shengxinyoupin.model.bean;

import java.util.List;

/**
 * Created by YRJ
 * Date 2018/3/20.
 */

public class XiaobianshuoBean {
    public HeadBean blnImg;
    public List<ProductBean> todayList;
    public List<ProductBean> yesterdayList;
//    "alipayItemId":"563049810028",
//            　　　　　　　　"classNameId":1,
//            　　　　　　　　"commission":"1.3",
//            　　　　　　　　"couponPrice":"25",
//            　　　　　　　　"couponUrl":"https://detail.tmall.com/item.htm?id=563049810028",
//            　　　　　　　　"createTime":1521431384000,
//            　　　　　　　　"createTimeStr":"2018-03-19 11:49:44",
//            　　　　　　　　"id":"b8qp",
//            　　　　　　　　"img":"qn|taoyouji2|8561630B4376ECAAA2B0DECD940B4313",
//            　　　　　　　　"imgList":[
//            　　　　　　　　　　{
//　　　　　　　　　　　　"appraiseCount":0,
//　　　　　　　　　　　　"height":150,
//　　　　　　　　　　　　"img1":[
//
//　　　　　　　　　　　　],
//　　　　　　　　　　　　"imgUrl":"http://img.taoyouji666.com/8561630B4376ECAAA2B0DECD940B4313?imageView2/2/w/480/q/90",
//　　　　　　　　　　　　"praiseCount":0,
//　　　　　　　　　　　　"width":150
//　　　　　　　　　　}
//　　　　　　　　],
//        　　　　　　　　"imgs":"qn|taoyouji2|8561630B4376ECAAA2B0DECD940B4313",
//        　　　　　　　　"name":"孔乙己",
//        　　　　　　　　"note":"内容",
//        　　　　　　　　"number":9,
//        　　　　　　　　"price":"99",
//        　　　　　　　　"productId":"1",
//        　　　　　　　　"productName":"商品标题",
//        　　　　　　　　"productText":"商品文案",
//        　　　　　　　　"productUrl":"https://detail.tmall.com/item.htm?id=563049810028",
//        　　　　　　　　"shareText":"商品标题
//        [在售价]：99.00
//            [券后价]：74.00
//            [下单链接] {选择分享渠道后生成淘口令}
//    打开[手机淘宝]即可查看",
//            　　　　　　　　"zhuanMoney":"0.19"

    //　"blnImg":{
//　　　　　　"id":"1",
//　　　　　　"img":"qn|taoyouji2|96501fa1bf9c265c1815d74a944a3a1e",
//　　　　　　"imgUrl":"http://img.taoyouji666.com/96501fa1bf9c265c1815d74a944a3a1e?imageView2/2/w/480/q/90",
//　　　　　　"type":"0",
//　　　　　　"url":"qn|taoyouji2|8561630B4376ECAAA2B0DECD940B4313"
//　　　　},
    public static class ProductBean {
        public String alipayItemId;
        public String id;
        /**
         * 发布人名字
         */
        public String name;
        /**
         * 发布人头像
         */
        public String img;
        /**
         * 发布内容
         */
        public String note;
        public String createTimeStr;
        /**
         * 商品名称
         */
        public String productName;
        /**
         * 商品文案
         */
        public String productText;

        public String couponPrice;
        public String price;

        public String productUrl;

        public List<ProductImageBean> imgList;
        /**
         * 点赞数量
         */
        public int number;
        public Integer thumb;
    }

    public static class HeadBean {
        public String imgUrl;
    }

    public static class ProductImageBean {
        public String imgUrl;
    }
}
