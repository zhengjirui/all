package com.lechuang.shengxinyoupin.view.activity.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsActivity;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 专属的搜索结果页面
 * 搜索结果页面
 */
public class ExclusiveSearchResultActivity extends AbsActivity implements View.OnClickListener {

    //搜索结果展示的方式  0:按销量展示  1.按好评展示 2.按价格展示  3.按新品展示
    private int showStyle = 0;
    private ImageView iv_price;
    //展示在搜索框上的搜索内容
    private TextView rootName;
    //入参的搜索内容
    private String productName;
    /**
     * 入参的排序方式
     * isVolume 1代表按销量排序从高到底
     * isAppraise 1好评从高到底
     * isPrice  1价格从低到高排序
     * isPrice  2价格从高到低排序
     * isNew    1新品商品冲最近的往后排序
     */
    private String style = "isVolume=1";
    //入参 页数
    private int page = 1;
    //
    //上个界面传递过来的值,用来判断是从分类还是搜索跳过来的 1:分类 2:搜索界面
    private int productstyle = 1;
    //可以刷新的gridview
    private XRecyclerView gv_search;
    private CommonRecyclerAdapter mAdapter;
    //无网络状态
    private LinearLayout ll_notNet;
    private RelativeLayout commonLoading;
    // 没有搜索到商品
    private LinearLayout nothingAll;
    private TextView tvRemind;
    //拼接完的参数
    private String allParameter;
    //刷新重试按钮
    private ImageView iv_tryAgain;
    // 没有商品展示默认图片
//    private RelativeLayout nothingData;
    //商品头图片 淘宝或天猫
    private int headImg;
    //参数map
    private HashMap<String, String> allParamMap;
    /**
     * 是否是人工筛选。false搜全网
     */
    private TextView wiperSwitch;

    //保存用户登录信息的sp
    private SharedPreferences se;
    private ImageView ivTop;
    private LinearLayoutManager mLayoutManager;
    public boolean isBottom = false;
    private long mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_search_result);
        initView();
        initData();

    }

    protected void initView() {
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this);
        //商品集合
        productList = new ArrayList<>();
        allParamMap = new HashMap<>();
        findViewById(R.id.iv_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_black_title)).setText("搜索");
        rootName=findViewById(R.id.tv_search);
        commonLoading = (RelativeLayout) findViewById(R.id.common_loading_all);
        ll_notNet = (LinearLayout) findViewById(R.id.ll_noNet);
        //刷新重试
        iv_tryAgain = (ImageView) findViewById(R.id.iv_tryAgain);
        iv_tryAgain.setOnClickListener(this);
        ivTop = (ImageView) findViewById(R.id.iv_top);
        ivTop.setOnClickListener(this);
//        nothingData = (RelativeLayout) findViewById(R.id.common_nothing_data);
//        nothingData.setOnClickListener(this);

        nothingAll = (LinearLayout) findViewById(R.id.search_result_nothing);
        tvRemind = (TextView) findViewById(R.id.tv_remind_nothing);

        findViewById(R.id.ll_type).setVisibility(View.GONE);
        iv_price = (ImageView) findViewById(R.id.iv_price);
        gv_search = (XRecyclerView) findViewById(R.id.gv_search);
        wiperSwitch = findViewById(R.id.wiperSwitch);
        wiperSwitch.setText("搜全网");
        wiperSwitch.setSelected(false);
        //设置监听
        wiperSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPeopleChange = !v.isSelected();
                v.setSelected(isPeopleChange);
                if(isPeopleChange){
                    wiperSwitch.setText("找券");
                }else{
                    wiperSwitch.setText("搜全网");
                }
                productList.clear();
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                page = 1;
                getData();
            }
        });
    }

    protected void initData() {
        productstyle = getIntent().getIntExtra("type", 1);
        if (productstyle == 1) {//1:分类
            productName = "&classTypeId=" + getIntent().getStringExtra("rootId");
        } else {  //2 搜索
            productName = "&name=" + getIntent().getStringExtra("rootId");
        }
        rootName.setText(getIntent().getStringExtra("rootName"));
        rootName.setOnClickListener(this);
        findViewById(R.id.ll_sale).setOnClickListener(this);
        findViewById(R.id.ll_like).setOnClickListener(this);
        findViewById(R.id.ll_price).setOnClickListener(this);
        findViewById(R.id.ll_new).setOnClickListener(this);

        gv_search.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isBottom = false;
                page = 1;
                if (null != productList) {
                    productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
                getData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                getData();
            }
        });

        gv_search.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastVisibleItemPosition() > 15) {
                    ivTop.setVisibility(View.VISIBLE);
                } else {
                    ivTop.setVisibility(View.GONE);
                }

                if (productList.size() - mLayoutManager.findLastVisibleItemPosition() < 5) {
                    if (System.currentTimeMillis() - mTime > 1000 && !isBottom) {
                        page += 1;
                        getData();
                    }
                }
            }
        });

        getData();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (null != productList) {
//            productList.clear();
//        }
//        getData();
//    }


    private List<ProductListBean.ProductBean> productList;

    //网络请求获取数据
    private void getData() {
        mTime = System.currentTimeMillis();
        if (page == 1) {
            commonLoading.setVisibility(View.VISIBLE);
        }
        if (allParamMap != null) {
            allParamMap.clear();
        }
        if (Utils.isNetworkAvailable(this)) {
            ll_notNet.setVisibility(View.GONE);
            //区分搜索还是分类跳转
            productstyle = getIntent().getIntExtra("type", 1);
            //拼接之后的参数
            //allParameter = "?page=" + page + productName + "&" + style;
            allParamMap.put("page", page + "");
            if (productstyle == 1) {
                //分类
                allParamMap.put("classTypeId", getIntent().getStringExtra("rootId"));
            } else {
                //搜索
                allParamMap.put("name", getIntent().getStringExtra("rootId"));
            }
            //是否人工筛选
            allParamMap.put("flag", wiperSwitch.isSelected() ? 1 + "" : 0 + "");

            if (showStyle == 0) {
                //按销量
                allParamMap.put("isVolume", 1 + "");
            } else if (showStyle == 1) {
                //按好评排序
                allParamMap.put("isAppraise", 1 + "");
            } else if (showStyle == 2) {
                //按价格排序
                /**
                 * isPrice 1 价格从低到高排序
                 * isPrice 2 价格从高到低排序
                 */
                if (isHighToDown) {
                    //价格从高到低
                    allParamMap.put("isPrice", 2 + "");
                } else {
                    allParamMap.put("isPrice", 1 + "");
                }
            } else if (showStyle == 3) {
                //按新品排序
                allParamMap.put("isNew", 1 + "");
            }
            Netword.getInstance().getApi(HomeApi.class)
                    .searchResult(allParamMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<ProductListBean>(ExclusiveSearchResultActivity.this) {
                        @Override
                        public void successed(ProductListBean result) {
                            commonLoading.setVisibility(View.GONE);
                            if (result == null) {
                                nothingAll.setVisibility(View.VISIBLE);
                                return;
                            }

                            List<ProductListBean.ProductBean> list = result.productList;
                            if (page == 1 && (list.toString().equals("[]") || list.isEmpty())) {
                                nothingAll.setVisibility(View.VISIBLE);
                                return;
                            }
                            nothingAll.setVisibility(View.GONE);
//                            nothingData.setVisibility(View.GONE);
                            if (page > 1 && list.isEmpty()) {
//                                showShortToast("亲!已经到底了");
                                gv_search.noMoreLoading();
                                isBottom = true;
                                return;
                            }
                            productList.addAll(list);
                            //只有page=1 的时候设置适配器 下拉刷新直接调用notifyDataSetChanged()
                            if (1 == page) {
                                if (mAdapter == null) {
                                    mAdapter = new ProductAdapter.ListAdapter3(ExclusiveSearchResultActivity.this, productList) {
                                        @Override
                                        public void onClick(ProductListBean.ProductBean bean) {
                                            startActivity(new Intent(mContext, ProductDetailsActivity.class)
                                                    .putExtra(Constants.listInfo, JSON.toJSONString(bean)));
                                        }
                                    };

                                    mLayoutManager = new LinearLayoutManager(ExclusiveSearchResultActivity.this, LinearLayoutManager.VERTICAL, false);
                                    mLayoutManager.setSmoothScrollbarEnabled(true);

                                    gv_search.setNestedScrollingEnabled(false);
                                    gv_search.setLayoutManager(mLayoutManager);
                                    gv_search.setAdapter(mAdapter);
                                }
                            } else {

                            }

                            mAdapter.notifyDataSetChanged();
                            gv_search.refreshComplete();
                        }

                        @Override
                        public void onCompleted() {
                            commonLoading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            commonLoading.setVisibility(View.GONE);
                        }
                    });
        } else {
            commonLoading.setVisibility(View.GONE);
            ll_notNet.setVisibility(View.VISIBLE);
        }

    }


    //价格从高到底
    private boolean isHighToDown = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_search: //搜索
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                break;
            case R.id.ll_sale: //按销量排序
                //style = "isVolume=1";
                showStyle = 0;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            case R.id.ll_like://按好评排序
                //style = "isAppraise=1";
                showStyle = 1;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            case R.id.ll_price: //按价格排序

                /**
                 * isPrice 1 价格从低到高排序
                 * isPrice 2 价格从高到低排序
                 */
                showStyle = 2;
                selectShowStyle(showStyle);
                if (isHighToDown) {
                    //价格从高到低
                    //style = "isPrice=2";
                    iv_price.setImageResource(R.drawable.shousuohou_jiage_shang);
                    isHighToDown = !isHighToDown;
                    page = 1;
                    if (productList != null)
                        productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    getData();

                } else {
                    // style = "isPrice=1";
                    iv_price.setImageResource(R.drawable.sousuohou_jiage_xia);
                    isHighToDown = !isHighToDown;
                    page = 1;
                    if (productList != null)
                        productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    getData();
                }
                break;
            case R.id.ll_new:  //按新品排序
                //style = "isNew=1";
                showStyle = 3;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            case R.id.common_nothing_data:
                page = 1;
                if (null != productList)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            // 返回顶部
            case R.id.iv_top:
                gv_search.scrollToPosition(0);
                break;

            default:
                break;
        }
    }

    //选择展示的方式
    private void selectShowStyle(int showStyle) {

        if (showStyle == 0) {
            changeStyle(showStyle);
        } else if (showStyle == 1) {
            changeStyle(showStyle);
        } else if (showStyle == 2) {
            changeStyle(showStyle);
        } else if (showStyle == 3) {
            changeStyle(showStyle);
        }
    }

    private void changeStyle(int showStyle) {

        View[] v = {findViewById(R.id.tv_sale), findViewById(R.id.tv_like),
                findViewById(R.id.tv_price), findViewById(R.id.tv_new)};
        View[] v1 = {findViewById(R.id.v_sale), findViewById(R.id.v_like)
                , findViewById(R.id.v_price), findViewById(R.id.v_new)};
        for (int i = 0; i < v.length; i++) {
            ((TextView) v[i]).setTextColor(getResources().getColor(R.color.black));
        }
        for (int i = 0; i < v1.length; i++) {
            v1[i].setVisibility(View.GONE);
        }

        ((TextView) v[showStyle]).setTextColor(getResources().getColor(R.color.main));
        v1[showStyle].setVisibility(View.VISIBLE);
    }
}
