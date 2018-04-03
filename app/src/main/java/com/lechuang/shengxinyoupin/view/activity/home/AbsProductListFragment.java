package com.lechuang.shengxinyoupin.view.activity.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.BaseFragment;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.defineView.GridItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 商品列表<br>
 * 参数 isActivity，类型boolean，默认值false
 */
public abstract class AbsProductListFragment extends BaseFragment {

    //商品列表
    private XRecyclerView rvProduct;
    //没有网络状态
    private View ll_noNet;
    //分页页数
    private int page = 1;
    //参数map
    private HashMap<String, String> allParamMap = new HashMap<>();
    private CommonRecyclerAdapter mAdapter;
    //商品集合
    private List<ProductListBean.ProductBean> mProductList;
    //    //轮播图链接
//    private List<String> linkList;
//    //图片集合
//    private List<String> imgList;
    private LinearLayoutManager mLayoutManager;
    private View ivTop;
    private long mTime;
    public boolean isBottom = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_program_detail_recycler, container, false);
        Bundle bundle = getArguments();
        if (bundle == null) {
            Utils.show("请传参数name和type");
            return v;
        }
        initParamMap(allParamMap, bundle);

        View titleView = v.findViewById(R.id.title_bar_program_detail);
        titleView.findViewById(R.id.iv_back).setVisibility(View.GONE);
        View back2 = titleView.findViewById(R.id.iv_back2);
        if (bundle.getBoolean("isActivity", false)) {
            back2.setVisibility(View.VISIBLE);
            back2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        } else {
            back2.setVisibility(View.GONE);
        }

        //标题
        ((TextView) titleView.findViewById(R.id.tv_title)).setText(bundle.getString("name"));

        //没有网络时的默认图片
        ll_noNet = v.findViewById(R.id.ll_noNet);
        //刷新重试
        v.findViewById(R.id.iv_tryAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(true);
            }
        });
        rvProduct = v.findViewById(R.id.gv_product);
        ivTop = v.findViewById(R.id.iv_top);
        ivTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvProduct.scrollToPosition(0);
            }
        });
        mProductList = new ArrayList<>();
        mAdapter = initAdapter(mProductList);

        mLayoutManager = initLinearLayoutManager();
        mLayoutManager.setSmoothScrollbarEnabled(true);

        rvProduct.addItemDecoration(new GridItemDecoration(
                new GridItemDecoration.Builder(getActivity())
                        .margin(4, 4)
                        .horSize(16)
                        .verSize(16)
                        .showLastDivider(true)
        ));
        View headerView = initHeaderView();
        if (headerView != null) {
            rvProduct.addHeaderView(headerView);
        }
        rvProduct.setNestedScrollingEnabled(false);
        rvProduct.setLayoutManager(mLayoutManager);
        rvProduct.setAdapter(mAdapter);
        initData();

        getData(true);
        return v;
    }

    protected void initData() {
        rvProduct.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getData(true);

            }

            @Override
            public void onLoadMore() {
                getData(false);
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
                        getData(false);
                    }
                }
            }
        });
    }

    protected void getData(boolean isRefresh) {
        mTime = System.currentTimeMillis();
        if (Utils.isNetworkAvailable(getActivity())) {
            //网络畅通 隐藏无网络状态
            ll_noNet.setVisibility(View.GONE);
            if (isRefresh) {
                isBottom = false;
                page = 1;
            } else {
                page++;
            }
            allParamMap.put("page", page + "");

            putParamBefore(allParamMap);

            requestNetword(allParamMap, new ResultBack<ProductListBean>(getActivity()) {
                @Override
                public void successed(ProductListBean result) {
                    kindData(result);
                }
            });
        } else {
            //网络不通 展示无网络状态
            ll_noNet.setVisibility(View.VISIBLE);
            if (isRefresh) {
//                if (imgList != null)
//                    imgList.clear();
//                if (linkList != null)
//                    linkList.clear();
                if (mProductList != null) {
                    mProductList.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 在请求之前
     *
     * @param allParamMap
     */
    protected void putParamBefore(HashMap<String, String> allParamMap) {
    }

    private void kindData(ProductListBean result) {
        if (result == null)
            return;
        ll_noNet.setVisibility(View.GONE);
        if (page == 1) {
            mProductList.clear();
            kindDataAtFirstPage(result);
        }
        //商品集合
        List<ProductListBean.ProductBean> list = result.productList;
        if (page > 1 && list.isEmpty()) {
            //数据没有了
//            Utils.show(mContext, "亲!已经到底了");
            rvProduct.noMoreLoading();
            isBottom = true;
            return;
        }
        mProductList.addAll(list);
        mAdapter.notifyDataSetChanged();
        rvProduct.refreshComplete();
    }

    /**
     * 如果是第一页，再额外处理
     */
    protected void kindDataAtFirstPage(ProductListBean result){

    }
    /**
     * 初始化列表的HeaderView
     */
    @Nullable
    protected View initHeaderView() {
        return null;
    }

    /**
     * 初始化参数请求的参数。这里不需要放page
     */
    protected abstract void initParamMap(HashMap<String, String> paramMap, Bundle bundle);

    /**
     * 执行网络请求
     */
    protected abstract void requestNetword(HashMap<String, String> paramMap, ResultBack<ProductListBean> resultBack);

    @NonNull
    protected abstract CommonRecyclerAdapter<ProductListBean.ProductBean> initAdapter(List<ProductListBean.ProductBean> data);

    @NonNull
    protected abstract LinearLayoutManager initLinearLayoutManager();
}
