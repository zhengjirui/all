package com.lechuang.shengxinyoupin.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.BaseActivity;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.OnItemClick;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.bean.HomeProgramDetailBean;
import com.lechuang.shengxinyoupin.presenter.adapter.BannerAdapter;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.defineView.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/3
 * @E-mail 1422947831@qq.com
 * @desc 栏目详情
 */
public class ProgramDetailActivity extends BaseActivity implements OnItemClick {

    private Context mContext = ProgramDetailActivity.this;
    //轮播图
    private RollPagerView rollPagerView;
    private XRecyclerView rvProduct;
    //没有网络状态
    private LinearLayout ll_noNet;
    //刷新重试按钮
    private ImageView iv_tryAgain;
    //分页页数
    private int page = 1;
    //栏目id
    private int programaId;

    private View header;
    private CommonRecyclerAdapter mAdapter;
    //商品集合
    private List<HomeProgramDetailBean.ProductListBean> mProductList;
    //轮播图链接
    private List<String> linkList;
    //图片集合
    private List<String> imgList;
    private ImageView ivTop;
    private GridLayoutManager mLayoutManager;
    private long mTime;
    public boolean isBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductList = new ArrayList<>();
        linkList = new ArrayList<>();
        imgList = new ArrayList<>();
        programaId = getIntent().getIntExtra("programaId", 0);
        getData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_program_detail_recycler;
    }

    @Override
    protected void initTitle() {
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        // headerView 轮播图
        header = LayoutInflater.from(mContext).inflate(R.layout.header_program_detail,
                (ViewGroup) findViewById(android.R.id.content),false);
        rollPagerView = (RollPagerView) header.findViewById(R.id.rollPagerView);

        //没有网络时的默认图片
        ll_noNet = (LinearLayout) findViewById(R.id.ll_noNet);
        //刷新重试
        iv_tryAgain = (ImageView) findViewById(R.id.iv_tryAgain);
        iv_tryAgain.setOnClickListener(this);
        rvProduct = (XRecyclerView) findViewById(R.id.gv_product);
        ivTop = (ImageView) findViewById(R.id.iv_top);
        ivTop.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        rvProduct.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isBottom = false;
                page = 1;
                getData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                getData();
            }
        });

        rvProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastVisibleItemPosition() > 15) {
                    ivTop.setVisibility(View.VISIBLE);
                } else {
                    ivTop.setVisibility(View.GONE);
                }

                if (mProductList.size() - mLayoutManager.findLastVisibleItemPosition() < 5) {
                    if (System.currentTimeMillis() - mTime > 1000 && !isBottom) {
                        page += 1;
                        getData();
                    }
                }
            }
        });
    }

    private void getData() {
        mTime = System.currentTimeMillis();
        if (Utils.isNetworkAvailable(mContext)) {
            //网络畅通 隐藏无网络状态
            ll_noNet.setVisibility(View.GONE);

            if (programaId == 1) {
                //栏目1详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program1(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            } else if (programaId == 2) {
                //栏目2详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program2(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            } else if (programaId == 3) {
                //栏目3详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program3(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            } else if (programaId == 4) {
                //栏目4详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program4(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            } else if (programaId == 5) {
                //栏目5详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program5(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            }
        } else {
            //网络不通 展示无网络状态
            ll_noNet.setVisibility(View.VISIBLE);
        }


    }

    //商品头图片 淘宝或天猫
    private int headImg;

    private void productData(HomeProgramDetailBean result) {
        if (result == null)
            return;
        ll_noNet.setVisibility(View.GONE);

        if (page == 1) {
            imgList.clear();
            linkList.clear();
            mProductList.clear();
        }
        //商品集合
        List<HomeProgramDetailBean.ProductListBean> list = result.productList;
        mProductList.addAll(list);

        if (page > 1 && list.isEmpty()) {
            //数据没有了
//            Utils.show(mContext, "亲!已经到底了");
            rvProduct.noMoreLoading();
            isBottom = true;
            return;
        }
        if (page == 1) {
            //标题
            ((TextView) findViewById(R.id.tv_title)).setText(result.indexBannerList.get(0).pname);
            //轮播图
            List<HomeProgramDetailBean.IndexBannerListBean> list1 = result.indexBannerList;
            for (int i = 0; i < list1.size(); i++) {
                //只取图片和链接
                imgList.add(list1.get(i).img);
                linkList.add(list1.get(i).link);
            }
            //设置播放时间间隔
            rollPagerView.setPlayDelay(3000);
            //设置透明度
            rollPagerView.setAnimationDurtion(500);
            //设置适配器
            rollPagerView.setAdapter(new BannerAdapter(mContext, imgList));
            //自定义指示器图片
            //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
            //设置圆点指示器颜色
            rollPagerView.setHintView(new ColorPointHintView(mContext, Color.YELLOW, Color.WHITE));
            rollPagerView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    startActivity(new Intent(mContext, EmptyWebActivity.class)
                            .putExtra("url", linkList.get(position)));
                }
            });

            if (mAdapter == null) {
                mAdapter = new CommonRecyclerAdapter(mContext, R.layout.item_program_product, mProductList) {
                    @Override
                    public void convert(ViewHolderRecycler viewHolder, Object data) {
                        try {
                            HomeProgramDetailBean.ProductListBean bean = (HomeProgramDetailBean.ProductListBean) data;
                            viewHolder.displayImage(R.id.iv_img, bean.imgs,R.drawable.loading_square);
                            // 原价
                            TextView tvOldPrice = viewHolder.getView(R.id.tv_oldprice);
                            tvOldPrice.setText("¥" + bean.price);
                            tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                            viewHolder.setText(R.id.tv_xiaoliang, "销量:" + bean.nowNumber + "件");
                            viewHolder.setText(R.id.tv_nowprice, "券后价 ¥" + bean.preferentialPrice);
                            viewHolder.setSpannelTextViewGrid(R.id.tv_name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                mLayoutManager = new GridLayoutManager(mContext, 2);
                mLayoutManager.setSmoothScrollbarEnabled(true);

                rvProduct.addItemDecoration(new GridItemDecoration(
                        new GridItemDecoration.Builder(mContext)
                                .isExistHead(true)
                                .margin(8,8)
                                .horSize(10)
                                .verSize(10)
                                .showLastDivider(true)
                ));

                rvProduct.addHeaderView(header);
                rvProduct.setNestedScrollingEnabled(false);
                rvProduct.setLayoutManager(mLayoutManager);
                rvProduct.setAdapter(mAdapter);
                mAdapter.setOnItemClick(ProgramDetailActivity.this);
            }
        } else {

        }
        mAdapter.notifyDataSetChanged();
        rvProduct.refreshComplete();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_tryAgain://刷新重试
                page = 1;

                getData();
                break;
            // 回到顶部
            case R.id.iv_top:
                rvProduct.scrollToPosition(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(mContext, ProductDetailsActivity.class)
                .putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
    }
}
