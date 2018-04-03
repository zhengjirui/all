package com.lechuang.shengxinyoupin.view.activity.home;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.BaseFragment;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.model.bean.HomeBannerBean;
import com.lechuang.shengxinyoupin.model.bean.ProductDetailsBean;
import com.lechuang.shengxinyoupin.presenter.adapter.BannerAdapter;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.NetwordUtil;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by YRJ
 * Date 2018/3/15.
 */

public abstract class AbsHomeFragment extends BaseFragment {
    protected class HomeKindItem {
        protected int typeID;
        //图片
        protected int img;
        //淘宝类目名
        protected String name;
        @Nullable
        protected Class<?> tar;
        @Nullable
        protected Runnable runnable;
        protected boolean isNeedLogin;

        protected HomeKindItem(int typeID, String name, int img) {
            this.typeID = typeID;
            this.img = img;
            this.name = name;
        }

        protected HomeKindItem(@Nullable Class<?> tar, String name, boolean isNeedLogin, int img) {
            this.tar = tar;
            this.isNeedLogin = isNeedLogin;
            this.img = img;
            this.name = name;
        }

        protected HomeKindItem(@Nullable Runnable runnable, String name, boolean isNeedLogin, int img) {
            this.runnable = runnable;
            this.isNeedLogin = isNeedLogin;
            this.img = img;
            this.name = name;
        }

        protected void click() {
            if (isNeedLogin && !Utils.isLogined(getActivity())) {
                return;
            }
            if (runnable != null) {
                runnable.run();
            } else if (tar != null) {
                startActivity(new Intent(getActivity(), tar));
            } else {
                startActivity(new Intent(getActivity(), KindDetailActivity.class)
                        .putExtra("type", typeID).putExtra("name", name));
            }
        }
    }

    //轮播图
    protected RollPagerView mRollViewPager;
    private List<HomeBannerBean.IndexBannerList> bannerList;

    /**
     * 获取首页轮播图数据<br>
     * 需要先赋值mRollViewPager
     */
    protected void getHomeBannerData() {
        //首页轮播图数据
        Netword.getInstance().getApi(HomeApi.class)
                .homeBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeBannerBean>(getActivity()) {

                    @Override
                    public void successed(HomeBannerBean result) {
                        if (result == null) {
                            return;
                        }
                        bannerList = result.indexBannerList0;
                        List<String> imgList = new ArrayList<>();
                        for (int i = 0; i < bannerList.size(); i++) {
                            imgList.add(bannerList.get(i).img);
                        }
                        //设置播放时间间隔
                        mRollViewPager.setPlayDelay(3000);
                        //设置透明度
                        mRollViewPager.setAnimationDurtion(500);
                        //设置适配器
                        mRollViewPager.setAdapter(new BannerAdapter(getActivity(), imgList));
                        //设置指示器（顺序依次）
                        //自定义指示器图片
                        //设置圆点指示器颜色
                        //设置文字指示器
                        //隐藏指示器
                        //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
                        mRollViewPager.setHintView(new ColorPointHintView(getActivity(), getResources().getColor(R.color.main), Color.WHITE));
                        //mRollViewPager.setHintView(new TextHintView(this));
                        //mRollViewPager.setHintView(null);
                        mRollViewPager.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                //获取到点击条目
                                int programaId = bannerList.get(position).programaId;
                                if (programaId >= 1 && programaId < getProgramaCount()) {
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", programaId));
                                } else if(programaId == getProgramaCount()){
                                    scrollToLastProduct();
                                }else {
                                    String link = bannerList.get(position).link;
                                    if (!TextUtils.isEmpty(link)) {
                                        String productID = Utils.getProductIdForUri(link);
                                        if (productID == null) {
                                            startActivity(new Intent(getActivity(), EmptyWebActivity.class)
                                                    .putExtra("url", link));
                                        } else {
                                            showWaitDialog("");
                                            //商品链接
                                            //先查询商品信息
                                            NetwordUtil.queryProductDetails(productID, new ResultBack<ProductDetailsBean>(getActivity()) {
                                                @Override
                                                public void successed(ProductDetailsBean result) {
                                                    hideWaitDialog();
                                                    startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                                                            .putExtra(Constants.listInfo, JSON.toJSONString(result.productWithBLOBs)));
                                                }

                                                @Override
                                                protected void error(int errorCode, String s) {
                                                    hideWaitDialog();
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
    }

    /**
     * 返回栏目数量，一般是5个，最后一个是滚动到底部的商品列表
     */
    protected int getProgramaCount(){
        return 5;
    }

    /**
     * 滚动到底部商品列表
     */
    protected void scrollToLastProduct() {

    }
}
