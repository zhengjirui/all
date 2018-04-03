package com.lechuang.shengxinyoupin.view.activity.soufanli;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsActivity;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.model.bean.SoufanliProgremBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.utils.SearchHistoryBean;
import com.lechuang.shengxinyoupin.utils.SearchHistoryUtils;
import com.lechuang.shengxinyoupin.utils.SharedPreferencesUtils;
import com.lechuang.shengxinyoupin.view.activity.home.KindDetailActivity;
import com.lechuang.shengxinyoupin.view.activity.home.ProductDetailsActivity;
import com.lechuang.shengxinyoupin.view.activity.home.SearchActivity;
import com.lechuang.shengxinyoupin.view.adapter.ProductAdapter;
import com.lechuang.shengxinyoupin.view.defineView.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class ExclusiveSouFanLiActivity extends AbsActivity implements View.OnClickListener {

    Unbinder unbinder;
    private View header;
    private ImageView ivProgram1;
    private ImageView ivProgram2;
    private ImageView ivProgram3;
    private ImageView ivProgram4;
    private LinearLayout ll_like;

    @BindView(R.id.rv_soufanli)
    XRecyclerView rvSoufanli;
    @BindView(R.id.iv_top)
    ImageView ivTop;

    private View buttonSouquan;
    private View buttonSouquanwang;
    private View viewSouquan;
    private View viewSouquanwang;


    private LinearLayoutManager mLayoutManager;
    private FlowLayout flowLayout;
    private SearchHistoryUtils historyUtils;
    ArrayList<SearchHistoryBean> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soufanli_exclusive);
        unbinder = ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        header = LayoutInflater.from(this).inflate(R.layout.activity_soufanli_exclusive_head,
                (ViewGroup) findViewById(android.R.id.content), false);
        //轮播图
        ivProgram1 = (ImageView) header.findViewById(R.id.iv_program1);
        ivProgram2 = (ImageView) header.findViewById(R.id.iv_program2);
        ivProgram3 = (ImageView) header.findViewById(R.id.iv_program3);
        ivProgram4 = (ImageView) header.findViewById(R.id.iv_program4);
        ll_like = (LinearLayout) header.findViewById(R.id.ll_like);

        ivTop.setOnClickListener(this);
        buttonSouquan = findViewById(R.id.souquan);
        buttonSouquanwang = findViewById(R.id.souquanwang);
        buttonSouquan.setOnClickListener(this);
        buttonSouquanwang.setOnClickListener(this);

        viewSouquanwang = findViewById(R.id.list);
        viewSouquan = findViewById(R.id.souquan_shuoming);

        findViewById(R.id.et_content).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        buttonSouquan.callOnClick();


        rvSoufanli.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastVisibleItemPosition() > 15) {
                    ivTop.setVisibility(View.VISIBLE);
                } else {
                    ivTop.setVisibility(View.GONE);
                }
            }
        });

        rvSoufanli.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (mProductList != null)
                    mProductList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                page = 1;
                initData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                initRecycle();
            }
        });
        flowLayout = header.findViewById(R.id.flow_layout);
        header.findViewById(R.id.iv_shanchu).setOnClickListener(this);
    }

    private void initData() {
        if (SharedPreferencesUtils.isLogined()) {
            ll_like.setVisibility(View.VISIBLE);
        } else {
            ll_like.setVisibility(View.GONE);
        }
        initProgrem();
        initRecycle();
        historyUtils = new SearchHistoryUtils(this, 20, getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE));
    }

    private void initProgrem() {
        Netword.getInstance().getApi(HomeApi.class)
                .soufanliProgramaImg()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<SoufanliProgremBean>(this) {
                    @Override
                    public void successed(SoufanliProgremBean result) {
                        if (result == null) {
                            return;
                        }
                        final List<SoufanliProgremBean.SearchImgListBean> list = result.searchImgList;
                        List<String> imgList = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i) != null)
                                imgList.add(list.get(i).img);
                        }
                        //栏目1
                        if (imgList.get(0) != null)
                            Glide.with(ExclusiveSouFanLiActivity.this).load(imgList.get(0)).placeholder(R.drawable.chaojisou).into(ivProgram1);
                        //栏目2
                        if (imgList.get(1) != null)
                            Glide.with(ExclusiveSouFanLiActivity.this).load(imgList.get(1)).placeholder(R.drawable.chaojisou).into(ivProgram2);
                        //栏目3
                        if (imgList.get(2) != null)
                            Glide.with(ExclusiveSouFanLiActivity.this).load(imgList.get(2)).placeholder(R.drawable.chaojisou).into(ivProgram3);
                        //栏目4
                        if (imgList.get(3) != null)
                            Glide.with(ExclusiveSouFanLiActivity.this).load(imgList.get(3)).placeholder(R.drawable.chaojisou).into(ivProgram4);
                        ivProgram1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ExclusiveSouFanLiActivity.this, KindDetailActivity.class)
                                        .putExtra("type", list.get(0).programType).putExtra("name", list.get(0).name));
                            }
                        });
                        ivProgram2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ExclusiveSouFanLiActivity.this, KindDetailActivity.class)
                                        .putExtra("type", list.get(1).programType).putExtra("name", list.get(1).name));
                            }
                        });
                        ivProgram3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ExclusiveSouFanLiActivity.this, KindDetailActivity.class)
                                        .putExtra("type", list.get(2).programType).putExtra("name", list.get(2).name));
                            }
                        });
                        ivProgram4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ExclusiveSouFanLiActivity.this, KindDetailActivity.class)
                                        .putExtra("type", list.get(3).programType).putExtra("name", list.get(3).name));
                            }
                        });
                    }
                });
    }

    int page = 1;
    //底部数据展示
    private CommonRecyclerAdapter mAdapter;
    private List<ProductListBean.ProductBean> mProductList = new ArrayList<>();

    private void initRecycle() {
        if (page == 1) {
//            commonLoading.setVisibility(View.VISIBLE);
        }
        Netword.getInstance().getApi(HomeApi.class)
                .soufanliProduct(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<ProductListBean>(ExclusiveSouFanLiActivity.this) {
                    @Override
                    public void successed(ProductListBean result) {
                        if (result == null) {
                            ll_like.setVisibility(View.GONE);
                            return;
                        }
                        List<ProductListBean.ProductBean> list = result.productList;

                        if (list.size() == 0) {
                            ll_like.setVisibility(View.GONE);
                        }

                        if (page == 1 && mProductList != null) {
                            mProductList.clear();
                        }
                        mProductList.addAll(list);
                        if (page > 1 && list.isEmpty()) {
                            rvSoufanli.noMoreLoading();
                            return;
                        }

                        if (page == 1) {
                            if (mAdapter == null) {
                                mAdapter = new ProductAdapter.ListAdapter1(ExclusiveSouFanLiActivity.this, mProductList) {
                                    @Override
                                    public void onClick(ProductListBean.ProductBean bean) {
                                        startActivity(new Intent(ExclusiveSouFanLiActivity.this, ProductDetailsActivity.class)
                                                .putExtra(Constants.listInfo, JSON.toJSONString(bean)));
                                    }
                                };

                                mLayoutManager = new LinearLayoutManager(ExclusiveSouFanLiActivity.this, LinearLayoutManager.VERTICAL, false);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvSoufanli.addHeaderView(header);
                                rvSoufanli.setNestedScrollingEnabled(false);
                                rvSoufanli.setLayoutManager(mLayoutManager);
                                rvSoufanli.setAdapter(mAdapter);
                            }
                        } else {

                        }
                        mAdapter.notifyDataSetChanged();
                        rvSoufanli.refreshComplete();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        list = historyUtils.sortHistory();
        if (list != null && list.size() > 0) {
            // 循环添加TextView到容器
            for (int i = 0; i < list.size(); i++) {
                final TextView view = new TextView(this);
                view.setText(list.get(i).history);
                view.setMaxLines(1);
                view.setEllipsize(TextUtils.TruncateAt.END);
                view.setTextColor(getResources().getColor(R.color.rgb_646464));
                view.setPadding(5, 10, 5, 10);
                view.setGravity(Gravity.CENTER);
                view.setTextSize(14);

                // 设置点击事件
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search(((TextView) v).getText().toString());
                    }
                });

                // 设置彩色背景
                /*GradientDrawable normalDrawable = new GradientDrawable();
                normalDrawable.setShape(GradientDrawable.RECTANGLE);
                normalDrawable.setColor(getResources().getColor(R.color.rgb_E6E6E6));*/
                Drawable normalDrawable = getResources().getDrawable(R.drawable.bg_search_biaoqian);

                // 设置按下的灰色背景

                Drawable pressedDrawable = getResources().getDrawable(R.drawable.bg_search_biaoqian);

                // 背景选择器
                StateListDrawable stateDrawable = new StateListDrawable();
                stateDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
                stateDrawable.addState(new int[]{}, normalDrawable);

                // 设置背景选择器到TextView上
                view.setBackground(stateDrawable);

                flowLayout.addView(view);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        flowLayout.removeAllViews();
    }

    /**
     * 开始搜索
     *
     * @param search
     */
    private void search(String search) {
        Intent intent = new Intent(this, AbsApplication.getInstance().getSearchResultActivity());
        //传递一个值,搜索结果页面用来判断是从分类还是搜索跳过去的 1:分类 2:搜索界面
        intent.putExtra("type", 2);
        //rootName传递过去显示在搜索框上
        intent.putExtra("rootName", search);
        //rootId传递过去入参
        intent.putExtra("rootId", search);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top:
                rvSoufanli.scrollToPosition(0);
                break;
            case R.id.et_content:
                //搜索内容
                startActivity(new Intent(ExclusiveSouFanLiActivity.this, SearchActivity.class));
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.souquan:
                if (!buttonSouquan.isSelected()) {
                    buttonSouquan.setSelected(true);
                    buttonSouquanwang.setSelected(false);
                    viewSouquanwang.setVisibility(View.VISIBLE);
                    viewSouquan.setVisibility(View.GONE);
                }
                break;
            case R.id.souquanwang:
                if (!buttonSouquanwang.isSelected()) {
                    buttonSouquanwang.setSelected(true);
                    buttonSouquan.setSelected(false);
                    viewSouquan.setVisibility(View.VISIBLE);
                    viewSouquanwang.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_shanchu:  //清除历史
                //清除sp里面所有数据
                historyUtils.clear();
                //清除list数据
                list.clear();
                flowLayout.removeAllViews();
                break;
        }
    }
}