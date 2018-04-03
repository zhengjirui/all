package com.lechuang.shengxinyoupin.view.activity.soufanli;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.BaseFragment;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.OnItemClick;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.LeCommon;
import com.lechuang.shengxinyoupin.model.bean.CountBean;
import com.lechuang.shengxinyoupin.model.bean.HomeKindBean;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.model.bean.SoufanliProgremBean;
import com.lechuang.shengxinyoupin.presenter.CommonAdapter;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.home.KindDetailActivity;
import com.lechuang.shengxinyoupin.view.activity.home.ProductDetailsActivity;
import com.lechuang.shengxinyoupin.view.activity.home.SearchActivity;
import com.lechuang.shengxinyoupin.view.activity.home.SearchResultActivity;
import com.lechuang.shengxinyoupin.view.defineView.MGridView;
import com.lechuang.shengxinyoupin.view.defineView.NumberRollingView;
import com.lechuang.shengxinyoupin.view.defineView.ViewHolder;

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
public class SouFanLiFragmentNew extends BaseFragment implements OnItemClick, View.OnClickListener {

    Unbinder unbinder;
    private View header;
    private ImageView ivProgram1;
    private ImageView ivProgram2;
    private ImageView ivProgram3;
    private ImageView ivProgram4;
    private MGridView gvKind;
    private LinearLayout llProgram;
    private LinearLayout ll_like;

    @BindView(R.id.rv_soufanli)
    XRecyclerView rvSoufanli;
    @BindView(R.id.iv_top)
    ImageView ivTop;
    @BindView(R.id.et_content)
    TextView etContent;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.countNumber)
    NumberRollingView countNumber;
    @BindView(R.id.searchInfo)
    TextView searchInfo;

    @BindView(R.id.common_loading_all)
    RelativeLayout commonLoading;
    private int whereSearch;


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case LeCommon.ACTION_LOGIN_SUCCESS:
                case LeCommon.ACTION_LOGIN_OUT:
                    page = 1;
                    initData();
                    break;
            }
        }
    };
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_suofanli, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_SUCCESS));
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_OUT));

        intent = new Intent(getActivity(), SearchResultActivity.class);
        whereSearch = getActivity().getIntent().getIntExtra("whereSearch", 1);
        initView();
        initData();
        return inflate;
    }

    private void initView() {
        header = LayoutInflater.from(getActivity()).inflate(R.layout.header_soufanli,
                (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        //轮播图
        ivProgram1 = (ImageView) header.findViewById(R.id.iv_program1);
        ivProgram2 = (ImageView) header.findViewById(R.id.iv_program2);
        ivProgram3 = (ImageView) header.findViewById(R.id.iv_program3);
        ivProgram4 = (ImageView) header.findViewById(R.id.iv_program4);
        gvKind = (MGridView) header.findViewById(R.id.gv_kind);
        llProgram = (LinearLayout) header.findViewById(R.id.ll_program);
        ll_like = (LinearLayout) header.findViewById(R.id.ll_like);


        ivTop.setOnClickListener(this);
        search.setOnClickListener(this);
        etContent.setOnClickListener(this);
        searchInfo.setOnClickListener(this);

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

    }

    private Intent intent;

    private void initData() {
        if (sp.getBoolean("isLogin", false) == true) {
            ll_like.setVisibility(View.VISIBLE);
        } else {
            ll_like.setVisibility(View.GONE);
        }
        initCont();
        initKin();
        initProgrem();
        initRecycle();
    }


    private void initCont() {
        Netword.getInstance().getApi(HomeApi.class)
                .countSum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<CountBean>(getActivity()) {
                    @Override
                    public void successed(CountBean result) {
                        // update: 2018/1/12  添加动效
                        countNumber.setUseCommaFormat(false);
                        countNumber.setFrameNum(50);
                        countNumber.setContent(result.couponCount);
                    }
                });
    }

    private void initProgrem() {
        Netword.getInstance().getApi(HomeApi.class)
                .soufanliProgramaImg()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<SoufanliProgremBean>(getActivity()) {
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
                            Glide.with(getActivity()).load(imgList.get(0)).placeholder(R.drawable.chaojisou).into(ivProgram1);
                        //栏目2
                        if (imgList.get(1) != null)
                            Glide.with(getActivity()).load(imgList.get(1)).placeholder(R.drawable.chaojisou).into(ivProgram2);
                        //栏目3
                        if (imgList.get(2) != null)
                            Glide.with(getActivity()).load(imgList.get(2)).placeholder(R.drawable.chaojisou).into(ivProgram3);
                        //栏目4
                        if (imgList.get(3) != null)
                            Glide.with(getActivity()).load(imgList.get(3)).placeholder(R.drawable.chaojisou).into(ivProgram4);
                        ivProgram1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                        .putExtra("type", list.get(0).programType).putExtra("name", list.get(0).name));
                            }
                        });
                        ivProgram2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                        .putExtra("type", list.get(1).programType).putExtra("name", list.get(1).name));
                            }
                        });
                        ivProgram3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                        .putExtra("type", list.get(2).programType).putExtra("name", list.get(2).name));
                            }
                        });
                        ivProgram4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                        .putExtra("type", list.get(3).programType).putExtra("name", list.get(3).name));
                            }
                        });
                    }
                });
    }

    private void initKin() {
        Netword.getInstance().getApi(HomeApi.class)
                .homeClassify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeKindBean>(getActivity()) {
                    @Override
                    public void successed(HomeKindBean result) {
                        if (result == null) {
                            return;
                        }
                        List<HomeKindBean.ListBean> list = result.tbclassTypeList;
                        //取前10类
                        if (list.size() > 10) {
                            list = list.subList(0, 10);
                        }
                        gvKind.setAdapter(new CommonAdapter<HomeKindBean.ListBean>(getActivity(), list, R.layout.home_kinds_item) {
                            @Override
                            public void setData(ViewHolder viewHolder, Object item) {
                                HomeKindBean.ListBean bean = (HomeKindBean.ListBean) item;
                                //分类名称
                                viewHolder.setText(R.id.tv_kinds_name, bean.rootName);
                                //分类图片
                                viewHolder.displayImage(R.id.iv_kinds_img, bean.img);
                            }
                        });
                        final List<HomeKindBean.ListBean> list1 = result.tbclassTypeList;
                        // TODO: 2017/10/2  分类item
                        gvKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getActivity(), AbsApplication.getInstance().getSearchResultActivity());
                                //传递一个值,搜索结果页面用来判断是从分类还是搜索跳过去的 1:分类 2:搜索界面
                                intent.putExtra("type", 1);
                                //rootName传递过去显示在搜索框上
                                intent.putExtra("rootName", list1.get(position).rootName);
                                //rootId传递过去入参
                                intent.putExtra("rootId", list1.get(position).rootId + "");
                                startActivity(intent);
                            }
                        });
                    }
                });
    }

    int page = 1;
    SharedPreferences sp;
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
                .subscribe(new ResultBack<ProductListBean>(getActivity()) {
                    @Override
                    public void successed(ProductListBean result) {
                        commonLoading.setVisibility(View.GONE);
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
                        int isAgencyStatus = sp.getInt("isAgencyStatus", 0);

                        if (page == 1) {
                            if (mAdapter == null) {
                                mAdapter = new CommonRecyclerAdapter(getActivity(), R.layout.item_last_programnew, mProductList) {
                                    @Override
                                    public void convert(ViewHolderRecycler holder, Object data) {
                                        try {
                                            ProductListBean.ProductBean bean = (ProductListBean.ProductBean) data;
                                            //商品图
                                            holder.displayImage(R.id.iv_img, bean.imgs, R.drawable.loading_square);
//        //动态调整滑动时的内存占用
                                            Glide.get(mContext).setMemoryCategory(MemoryCategory.LOW);
                                            //领券减金额
                                            holder.setText(R.id.tv_quanMoney, "领券减" + bean.couponMoney);
                                            //原价
                                            holder.setText(R.id.tv_oldprice, bean.price + "");
                                            TextView tvOldprice = holder.getView(R.id.tv_oldprice);
                                            //中划线
                                            //券后价
                                            holder.setText(R.id.tv_nowprice, "¥" + bean.preferentialPrice);
                                            //赚
                                            TextView tvGet = holder.getView(R.id.tv_get);
                                            LinearLayout tt = holder.getView(R.id.tt);
                                            holder.setText(R.id.tv_get, bean.zhuanMoney);
                                            if (bean.zhuanMoney == null || bean.zhuanMoney.matches("0\\.?0*")) {
                                                tvGet.setVisibility(View.GONE);

                                            }
                                            if (Utils.isZero(bean.couponMoney)) {
                                                tt.setVisibility(View.GONE);
                                            } else {
                                                tvOldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                                tt.setVisibility(View.VISIBLE);
                                                tvGet.setVisibility(View.VISIBLE);
                                            }

                                            //商品名称
                                            holder.setSpannelTextView(R.id.spannelTextView, bean.name, Integer.valueOf(bean.shopType));
                                            //销量
                                            holder.setText(R.id.tv_xiaoliang, "已抢" + bean.nowNumber + "件");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                ;

                                mLayoutManager = new

                                        LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvSoufanli.addHeaderView(header);
                                rvSoufanli.setNestedScrollingEnabled(false);
                                rvSoufanli.setLayoutManager(mLayoutManager);
                                rvSoufanli.setAdapter(mAdapter);
                                mAdapter.setOnItemClick(SouFanLiFragmentNew.this);

                            }
                        } else {

                        }
                        mAdapter.notifyDataSetChanged();
                        rvSoufanli.refreshComplete();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        commonLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        commonLoading.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        unbinder.unbind();
    }

    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                .putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top:
                rvSoufanli.scrollToPosition(0);
                break;
            case R.id.searchInfo:
                startActivity(new Intent(getActivity(), SoufanliInfo.class));
                break;
            case R.id.search:
            case R.id.et_content:
                //搜索内容
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
        }
    }
}