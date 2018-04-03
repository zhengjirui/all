package com.lechuang.shengxinyoupin.model.bean;

import java.util.List;

/**
 * 作者：li on 2017/9/29 16:25
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class TipoffDetail {

    /**
     * productList : [{"alipayCouponId":"7cfcfef983124b4fae3e82613f36c843","alipayItemId":"558639560877","commission":0,"couponMoney":50,"id":"1vfi6","img":"http://img3.tbcdn.cn/tfscom/i2/3306149389/TB1VnrKd0fJ8KJjy0FeXXXKEXXa_!!0-item_pic.jpg","imgs":"http://img3.tbcdn.cn/tfscom/i2/3306149389/TB1VnrKd0fJ8KJjy0FeXXXKEXXa_!!0-item_pic.jpg_400x400.jpg","name":"2017秋冬新款女装修身百搭打底衫韩版时尚毛衣针织衫女均码","nowNumber":6,"preferentialPrice":"19.90","price":69.9,"productName":"2017秋冬新款女装修身百搭打底衫韩版时尚毛衣针织衫女均码","productUrl":"https://detail.tmall.com/item.htm?id=558639560877","shareIntegral":"0","shopType":"2","smallImages":["http://img3.tbcdn.cn/tfscom/i4/3306149389/TB23afLd_nI8KJjSszbXXb4KFXa_!!3306149389.jpg","http://img3.tbcdn.cn/tfscom/i1/3306149389/TB1InVKaqmgSKJjSsphXXcy1VXa_!!0-item_pic.jpg","http://img1.tbcdn.cn/tfscom/i4/3306149389/TB2yHn.hiAKL1JjSZFoXXagCFXa_!!3306149389.jpg","http://img4.tbcdn.cn/tfscom/i1/3306149389/TB2WjfJd0fJ8KJjy0FeXXXKEXXa_!!3306149389.jpg"],"zhuanMoney":"0.0"}]
     * status : 1
     * tipOff : {"appraiseCount":2,"createTime":1511233908000,"createTimeStr":"2017-11-21 11:11:48","id":"26o1j","img":"qn|taoyouji2|07b159370add82e60a6c5da3cdacc727","img1":[],"nickName":"123456789","pageViews":1,"photo":"http://img.taoyouji666.com/48a4be43fb7b81d8d105c2c3125f4084?imageView2/2/w/480/q/90","praiseCount":0,"productIds":"62","textBoxContent":"<p>秋冬必备针织毛衣，时尚打底，韩版百搭，柔软舒适，修身显瘦，不显臃肿，简约版型！<\/p><p><br><\/p>","title":"123456789"}
     */

    public int status;
    public TipOffBean tipOff;
    public List<ProductListBean> productList;

    public static class TipOffBean {
        /**
         * appraiseCount : 2
         * createTime : 1511233908000
         * createTimeStr : 2017-11-21 11:11:48
         * id : 26o1j
         * img : qn|taoyouji2|07b159370add82e60a6c5da3cdacc727
         * img1 : []
         * nickName : 123456789
         * pageViews : 1
         * photo : http://img.taoyouji666.com/48a4be43fb7b81d8d105c2c3125f4084?imageView2/2/w/480/q/90
         * praiseCount : 0
         * productIds : 62
         * textBoxContent : <p>秋冬必备针织毛衣，时尚打底，韩版百搭，柔软舒适，修身显瘦，不显臃肿，简约版型！</p><p><br></p>
         * title : 123456789
         */

        public int appraiseCount;
        public long createTime;
        public String createTimeStr;
        public String id;
        public String img;
        public String nickName;
        public int pageViews;
        public String photo;
        public int praiseCount;
        public String productIds;
        public String textBoxContent;
        public String title;
        public List<?> img1;
    }

    public static class ProductListBean {
        /**
         * alipayCouponId : 7cfcfef983124b4fae3e82613f36c843
         * alipayItemId : 558639560877
         * commission : 0.0
         * couponMoney : 50.0
         * id : 1vfi6
         * img : http://img3.tbcdn.cn/tfscom/i2/3306149389/TB1VnrKd0fJ8KJjy0FeXXXKEXXa_!!0-item_pic.jpg
         * imgs : http://img3.tbcdn.cn/tfscom/i2/3306149389/TB1VnrKd0fJ8KJjy0FeXXXKEXXa_!!0-item_pic.jpg_400x400.jpg
         * name : 2017秋冬新款女装修身百搭打底衫韩版时尚毛衣针织衫女均码
         * nowNumber : 6
         * preferentialPrice : 19.90
         * price : 69.9
         * productName : 2017秋冬新款女装修身百搭打底衫韩版时尚毛衣针织衫女均码
         * productUrl : https://detail.tmall.com/item.htm?id=558639560877
         * shareIntegral : 0
         * shopType : 2
         * smallImages : ["http://img3.tbcdn.cn/tfscom/i4/3306149389/TB23afLd_nI8KJjSszbXXb4KFXa_!!3306149389.jpg","http://img3.tbcdn.cn/tfscom/i1/3306149389/TB1InVKaqmgSKJjSsphXXcy1VXa_!!0-item_pic.jpg","http://img1.tbcdn.cn/tfscom/i4/3306149389/TB2yHn.hiAKL1JjSZFoXXagCFXa_!!3306149389.jpg","http://img4.tbcdn.cn/tfscom/i1/3306149389/TB2WjfJd0fJ8KJjy0FeXXXKEXXa_!!3306149389.jpg"]
         * zhuanMoney : 0.0
         */

        public String alipayCouponId;
        public String alipayItemId;
        public double commission;
        public String couponMoney;
        public String id;
        public String img;
        public String imgs;
        public String name;
        public int nowNumber;
        public String preferentialPrice;
        public String price;
        public String productName;
        public String productUrl;
        public String shareIntegral;
        public String shopType;
        public String zhuanMoney;
        public List<String> smallImages;
        //分享文案
        public String shareText;
    }
}
