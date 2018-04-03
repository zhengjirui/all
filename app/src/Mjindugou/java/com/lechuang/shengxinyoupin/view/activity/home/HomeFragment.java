package com.lechuang.shengxinyoupin.view.activity.home;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.mobileim.YWIMKit;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jude.rollviewpager.RollPagerView;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.LeCommon;
import com.lechuang.shengxinyoupin.model.bean.EnvelopeActivityBean;
import com.lechuang.shengxinyoupin.model.bean.GetBean;
import com.lechuang.shengxinyoupin.model.bean.HomeGunDongTextBean;
import com.lechuang.shengxinyoupin.model.bean.HomeKindBean;
import com.lechuang.shengxinyoupin.model.bean.HomeLastProgramBean;
import com.lechuang.shengxinyoupin.model.bean.HomeProgramBean;
import com.lechuang.shengxinyoupin.model.bean.HomeTodayProductBean;
import com.lechuang.shengxinyoupin.model.bean.OwnNewsBean;
import com.lechuang.shengxinyoupin.model.bean.PickRedEnvBean;
import com.lechuang.shengxinyoupin.model.bean.ProductDetailsBean;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.presenter.CommonAdapter;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.NetwordUtil;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.GetApi;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.SigneActivity;
import com.lechuang.shengxinyoupin.view.activity.earn.GetMoneyActivity;
import com.lechuang.shengxinyoupin.view.activity.own.ProfitActivity;
import com.lechuang.shengxinyoupin.view.activity.recyclerViewNew.MyAdapterNew;
import com.lechuang.shengxinyoupin.view.activity.video.PlayActivity;
import com.lechuang.shengxinyoupin.view.adapter.ProductAdapter;
import com.lechuang.shengxinyoupin.view.defineView.AutoTextView;
import com.lechuang.shengxinyoupin.view.defineView.CustomTabLayout;
import com.lechuang.shengxinyoupin.view.defineView.GridItemDecoration;
import com.lechuang.shengxinyoupin.view.defineView.MGridView;
import com.lechuang.shengxinyoupin.view.defineView.ViewHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by YRJ
 * Date 2018/3/15.
 */

public class HomeFragment extends AbsHomeFragment {
    protected Subscription subscription;

    //首页分类
    Unbinder unbinder;
    //自动滚动的textview
    private AutoTextView tvAutoText;
    private ImageView ivProgram1;
    private ImageView ivProgram2;
    private ImageView ivProgram3;
    private ImageView ivProgram4;
    private ImageView lastRollViewPager;
    private MGridView gvKind;
    private View lineHome;

    // header view中的TabLayout
    private TabLayout tabHome;
    // fragment_home_tablebar中的TabLayout
    @BindView(R.id.tablayout_home_top)
    TabLayout tabHomeTop;
    //首页最下商品gridview
    @BindView(R.id.rv_home_table)
    XRecyclerView rvHome;
    @BindView(R.id.iv_top)
    ImageView ivTop;        //回到顶部
    @BindView(R.id.line_home_tab_top)
    View lineHomeTop;


    TextView todayTitle;

    private View v;
    private ArrayList<String> text = null;

    private SharedPreferences sp;
    public boolean isBottom = false;
    private View header;

    RecyclerView todayProduct;
    RecyclerView videoProduct;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case LeCommon.ACTION_LOGIN_SUCCESS:
                case LeCommon.ACTION_LOGIN_OUT:
                    page = 1;
                    rvHome.scrollToPosition(0);
                    ivTop.setVisibility(View.GONE);
                    getData();
                    break;
            }
        }
    };
    private LinearLayoutManager mLayoutManager;
    private long mTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_tablebar, container, false);
        unbinder = ButterKnife.bind(this, v);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_SUCCESS));
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_OUT));
        initView();
        initEvent();
        getData();
        return v;
    }

    //网络获取数据
    public void getData() {
        if (Utils.isNetworkAvailable(getActivity())) {

            getDataKefu();
            //获取首页轮播图数据
            getHomeBannerData();
            //首页分类数据
            getHomeKindData();
            //首页滚动文字数据
            getHomeScrollTextView();
            getTodayProduct();
            //首页4个图片栏目数据
            getHomeProgram();
            getVideoProduct();
            // tab数据
            getTabData();
            // 商品数据(默认'全部')
            mRootId = 99;
            getProductList();

            getHongbao();
        } else {
            //隐藏加载框
            showShortToast(getString(R.string.net_error));
        }

    }

    private void initView() {
        // TODO init XRecyclerView
        header = LayoutInflater.from(getActivity()).inflate(R.layout.header_home_tablebar,
                (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        //轮播图
        todayProduct = (RecyclerView) header.findViewById(R.id.todayProduct);

        todayTitle = (TextView) header.findViewById(R.id.todayTitle);
        mRollViewPager = (RollPagerView) header.findViewById(R.id.rv_banner);
        tabHome = (TabLayout) header.findViewById(R.id.tablayout_home);
        tvAutoText = (AutoTextView) header.findViewById(R.id.tv_auto_text);
        ivProgram1 = (ImageView) header.findViewById(R.id.iv_program1);
        ivProgram2 = (ImageView) header.findViewById(R.id.iv_program2);
        ivProgram3 = (ImageView) header.findViewById(R.id.iv_program3);
        ivProgram4 = (ImageView) header.findViewById(R.id.iv_program4);
        lastRollViewPager = (ImageView) header.findViewById(R.id.lastRollViewPager);
        gvKind = (MGridView) header.findViewById(R.id.gv_kind);
        lineHome = (View) header.findViewById(R.id.line_home_tab);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        todayProduct.setLayoutManager(layoutManager);

        videoProduct = header.findViewById(R.id.videoProduct);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoProduct.setLayoutManager(layoutManager);
        header.findViewById(R.id.check_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), VedioListActivity.class).putExtra("name", "短视频"));
            }
        });
        rvHome.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] loc = new int[2];
                tabHome.getLocationInWindow(loc);

                if (tabHomeTop.getHeight() >= loc[1]) {
                    tabHome.setVisibility(View.INVISIBLE);
                    lineHome.setVisibility(View.INVISIBLE);
                    tabHomeTop.setVisibility(View.VISIBLE);
                    lineHomeTop.setVisibility(View.VISIBLE);
                    ivTop.setVisibility(View.VISIBLE);
                } else {
                    tabHome.setVisibility(View.VISIBLE);
                    lineHome.setVisibility(View.VISIBLE);
                    tabHomeTop.setVisibility(View.INVISIBLE);
                    lineHomeTop.setVisibility(View.INVISIBLE);
                    ivTop.setVisibility(View.GONE);
                }

                if (mProductList.size() - mLayoutManager.findLastVisibleItemPosition() < 5) {
                    if (System.currentTimeMillis() - mTime > 1000 && !isBottom) {
                        page += 1;
                        getProductList();
                    }
                }
            }
        });

        rvHome.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isBottom = false;
                page = 1;
                getData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                getProductList();
//                mAdapter.notifyDataSetChanged();
//                rvHome.refreshComplete();
            }
        });
        View baoyou = header.findViewById(R.id.baoyou);
        baoyou.findViewById(R.id.main_9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), KindDetailActivity.class)
                        .putExtra("type", 2).putExtra("name", "9块9包邮"));
            }
        });
        baoyou.findViewById(R.id.main_19).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), KindDetailActivity.class)
                        .putExtra("type", 10).putExtra("name", "19块9包邮"));
            }
        });
    }

    private void initEvent() {
        //tryAgain = (ImageView) v.findViewById(R.id.tryAgain);
    }

    @Override
    protected void scrollToLastProduct() {
        //滚动到下面的列表
        int[] loc = new int[2];
        tabHome.getLocationInWindow(loc);
        int[] loc2 = new int[2];
        rvHome.getLocationInWindow(loc2);
        rvHome.smoothScrollBy(0, loc[1] - loc2[1]);
    }

    //每日必杀
    private void getTodayProduct() {
        Netword.getInstance().getApi(HomeApi.class)
                .homeTodayProduct(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeTodayProductBean.ProudList>(getActivity()) {
                    @Override
                    public void successed(HomeTodayProductBean.ProudList result) {
                        if (result == null)
                            return;
                        todayTitle.setText(result.programName);
                        todayProduct.setAdapter(new MyAdapterNew(result.adProductList, new MyAdapterNew.RecycleViewListener() {
                            @Override
                            public void itemClik(HomeTodayProductBean.adProductList listBean) {
                                startActivity(new Intent(getActivity(), ProductDetailsActivity.class
                                ).putExtra(Constants.listInfo, JSON.toJSONString(listBean)));
                            }
                        }));
                    }
                });
    }

    /**
     * 获取边看边买
     */
    private void getVideoProduct() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", "1");
        Netword.getInstance().getApi(HomeApi.class)
                .homeVedioProduct(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<ProductListBean>(getActivity()) {
                    @Override
                    public void successed(ProductListBean result) {
                        if (result == null)
                            return;
                        videoProduct.setAdapter(new CommonRecyclerAdapter<ProductListBean.ProductBean>
                                (getActivity(), R.layout.main_vedio_item, result.productList) {
                            @Override
                            public void convert(ViewHolderRecycler holder, ProductListBean.ProductBean bean) {
                                holder.setText(R.id.name, bean.name);
                                holder.setText(R.id.price, "¥" + bean.price);
                                holder.setText(R.id.couponMoney, "券¥" + bean.couponMoney);
                                holder.setText(R.id.nowNumber, "销量：" + bean.nowNumber + "件");
                                holder.displayImage(R.id.img, bean.imgs, R.drawable.loading_square);

                            }

                            @Override
                            public void onClick(ProductListBean.ProductBean bean) {
                                PlayActivity.gotoActivity(getActivity(), v, bean.videoUrl, bean.imgs);
                            }
                        });

                    }
                });
    }



    //获取首页分类数据
    private void getHomeKindData() {
        //首页分类数据
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
                        gvKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                HomeKindBean.ListBean bean = (HomeKindBean.ListBean) parent.getItemAtPosition(position);
                                Intent intent = new Intent(getActivity(), AbsApplication.getInstance().getSearchResultActivity());
                                //传递一个值,搜索结果页面用来判断是从分类还是搜索跳过去的 1:分类 2:搜索界面
                                intent.putExtra("type", 1);
                                //rootName传递过去显示在搜索框上
                                intent.putExtra("rootName", bean.rootName);
//                                intent.putExtra("rootName", "");
                                //rootId传递过去入参
                                intent.putExtra("rootId", bean.rootId + "");
                                startActivity(intent);
                            }
                        });
                    }
                });
    }

    //首页滚动文字数据
    private void getHomeScrollTextView() {
        Netword.getInstance().getApi(HomeApi.class)
                .gunDongText()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeGunDongTextBean>(getActivity()) {
                    @Override
                    public void successed(HomeGunDongTextBean result) {
                        if (result == null) {
                            return;
                        }
                        final List<HomeGunDongTextBean.IndexMsgListBean> list = result.indexMsgList;
                        //滚动TextView
                        text = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            text.add(list.get(i).productName);
                        }
                        //自定义的滚动textview
                        tvAutoText.setTextAuto(text);
                        //设置时间
                        tvAutoText.setGap(3000);
                        tvAutoText.setOnItemClickListener(new AutoTextView.onItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                               /* if (sp.getBoolean("isLogin", false)) {*/
                                startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                                        .putExtra(Constants.listInfo, JSON.toJSONString(list.get(position))));
                               /* } else {
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                }*/
                            }
                        });
                    }
                });
    }

    //首页4个图片栏目数据
    private void getHomeProgram() {
        Netword.getInstance().getApi(HomeApi.class)
                .homeProgramaImg()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeProgramBean>(getActivity()) {
                    @Override
                    public void successed(HomeProgramBean result) {
                        if (result == null) {
                            return;
                        }
                        final List<HomeProgramBean.ListBean> list = result.programaImgList;
                        List<String> imgList = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i) != null)
                                imgList.add(list.get(i).img);
                        }
                        //栏目1
                        if (imgList.get(0) != null)
                            Glide.with(getActivity()).load(imgList.get(0)).placeholder(getResources().getDrawable(R.drawable.ming)).into(ivProgram1);
                        //栏目2
                        if (imgList.get(1) != null)
                            Glide.with(getActivity()).load(imgList.get(1)).placeholder(getResources().getDrawable(R.drawable.lingdian)).into(ivProgram2);
                        //栏目3
                        if (imgList.get(2) != null)
                            Glide.with(getActivity()).load(imgList.get(2)).placeholder(getResources().getDrawable(R.drawable.yi)).into(ivProgram3);

                        //栏目4
                        if (imgList.get(3) != null)
                            Glide.with(getActivity()).load(imgList.get(3)).placeholder(getResources().getDrawable(R.drawable.yi)).into(ivProgram4);
                        ivProgram1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(0).programaId));
                            }
                        });
                        ivProgram2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(1).programaId));
                            }
                        });
                        ivProgram3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(2).programaId));
                            }
                        });
                        ivProgram4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(3).programaId));
                            }
                        });
                    }
                });
    }

    //顶部的条目
//    private ArrayList<ProductListBean.ProductBean> lastProgramList = new ArrayList<>();

    @OnClick({R.id.tv_search, R.id.qiandao, R.id.iv_task, R.id.iv_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_task:
                startActivity(new Intent(getActivity(), GetMoneyActivity.class));
                break;
            case R.id.tv_search:
                startActivity(new Intent(getActivity(), SearchActivity.class).putExtra("whereSearch", 1));
                break;
            //签到
            case R.id.qiandao:
                startActivity(new Intent(getActivity(), SigneActivity.class));
                break;
            //底部跳转到顶部的按钮
            case R.id.iv_top:
                // 回到顶部
                rvHome.scrollToPosition(0);
                ivTop.setVisibility(View.GONE);
                break;
//            case R.id.iv_tryAgain:
//                //刷新数据
//                page = 1;
//                //清空商品集合
//                if (mProductList != null)
//                    mProductList.clear();
//                getData();
//                break;
            default:
                break;
        }
    }


    //用户密码
    private String openImPassword;
    //用户账户
    private String phone;
    //客服账号
    private String customerServiceId;
    public YWIMKit mIMKit;

    //网络获取数据
    private void getDataKefu() {
        Netword.getInstance().getApi(OwnApi.class)
                .isUnread()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnNewsBean>(getActivity()) {
                    @Override
                    public void successed(OwnNewsBean result) {
                        phone = result.appUsers.phone;
                        openImPassword = result.appUsers.openImPassword;
                        customerServiceId = result.appUsers.customerServiceId;
                      /*  if (phone != null && openImPassword != null && customerServiceId != null) {
                            //此实现不一定要放在Application onCreate中
                            //此对象获取到后，保存为全局对象，供APP使用
                            //此对象跟用户相关，如果切换了用户，需要重新获取
                            mIMKit = YWAPI.getIMKitInstance(phone, Constants.APP_KEY);
                            //开始登录
                            IYWLoginService loginService = mIMKit.getLoginService();
                            YWLoginParam loginParam = YWLoginParam.createLoginParam(phone, openImPassword);
                            loginService.login(loginParam, new IWxCallback() {

                                @Override
                                public void onSuccess(Object... arg0) {

                                }

                                @Override
                                public void onProgress(int arg0) {
                                    // TODO Auto-generated method stub
                                }

                                @Override
                                public void onError(int errCode, String description) {
                                    //如果登录失败，errCode为错误码,description是错误的具体描述信息
//                                    Utils.show(mContext, description);
                                }
                            });
                        } else {
                            Utils.show(mContext, getResources().getString(R.string.net_error));
                        }
                        */
                    }
                });
    }

    //分页
    private int page = 1;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        unbinder.unbind();
    }


    // viewpage标题
    private List<GetBean.TopTab> topTabList = new ArrayList<>();

    private void getTabData() {
        if (tabHome != null && tabHome.getTabCount() > 0) {
            topTabList.clear();
            tabHome.removeAllTabs();
            tabHomeTop.removeAllTabs();
        }

        if (Utils.isNetworkAvailable(getActivity())) {

            Netword.getInstance().getApi(GetApi.class)
                    .topTabList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<GetBean>(getActivity()) {
                        @Override
                        public void successed(GetBean result) {
                            if (result == null) return;
                            GetBean.TopTab topTab = new GetBean.TopTab();
                            topTab.rootId = 99;
                            topTab.rootName = "精选";
                            topTabList.add(topTab);
                            List<GetBean.TopTab> tabList = result.tbclassTypeList;
                            if (tabList != null) {
                                topTabList.addAll(tabList);
                            }

                            for (GetBean.TopTab tab : topTabList) {
                                tabHome.addTab(tabHome.newTab().setText(tab.rootName));
                            }

                            for (GetBean.TopTab tab : topTabList) {
                                tabHomeTop.addTab(tabHomeTop.newTab().setText(tab.rootName));
                            }

                            CustomTabLayout.reflex(tabHome);
                            CustomTabLayout.reflex(tabHomeTop);
                            // 为Tab添加响应
                            // 切换Tab时,两个TabLayout都需要保存当前选中位置
                            onSelectTabItem();
                            onSelectTopTabItem();
                        }
                    });
        } else {

        }
    }

    /**
     * TabLayout事件监听
     */
    private void onSelectTabItem() {
        //获取Tab的数量
        int tabCount = tabHome.getTabCount();
        for (int position = 0; position < tabCount; position++) {
            TabLayout.Tab tab = tabHome.getTabAt(position);
            if (tab == null) {
                continue;
            }
            // 这里使用到反射，拿到Tab对象后获取Class
            Class c = tab.getClass();
            try {
                //c.getDeclaredField 获取私有属性。
                //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                Field field = c.getDeclaredField("mView");
                if (field == null) {
                    continue;
                }
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) {
                    continue;
                }
                final int item = position;
                view.setTag(item);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 清空网络请求连接池
                        Netword.getInstance().getOkHttpClient().connectionPool().evictAll();
                        tabHomeTop.setScrollPosition(item, 0, true);
                        // TabLayout child 事件处理
                        mRootId = topTabList.get(item).rootId;
                        page = 1;
                        getProductList();
                        rvHome.scrollToPosition(2);
                    }
                });

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TabLayout事件监听
     */
    private void onSelectTopTabItem() {
        //获取Tab的数量
        int tabCount = tabHomeTop.getTabCount();
        for (int position = 0; position < tabCount; position++) {
            TabLayout.Tab tab = tabHomeTop.getTabAt(position);
            if (tab == null) {
                continue;
            }
            // 这里使用到反射，拿到Tab对象后获取Class
            Class c = tab.getClass();
            try {
                //c.getDeclaredField 获取私有属性。
                //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                Field field = c.getDeclaredField("mView");
                if (field == null) {
                    continue;
                }
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) {
                    continue;
                }
                final int item = position;
                view.setTag(item);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 清空网络请求连接池
                        Netword.getInstance().getOkHttpClient().connectionPool().evictAll();
                        tabHome.setScrollPosition(item, 0, true);
                        // TabLayout child 事件处理
                        mRootId = topTabList.get(item).rootId;
                        page = 1;
                        getProductList();
                        rvHome.scrollToPosition(2);
                    }
                });

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // 设置Adapter
    private CommonRecyclerAdapter mAdapter;
    // 底部商品
    private ArrayList<ProductListBean.ProductBean> mProductList = new ArrayList<>();
    // 当前分类ID
    private int mRootId = 0;

    /**
     * 底部商品数据
     */
    private void getProductList() {
        mTime = System.currentTimeMillis();
        HashMap<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("classTypeId", mRootId + "");
//        if (mRootId != 0) {  //Integer classTypeId   分类id,精选不传
//            map.put("classTypeId", mRootId + "");
//        } else {             //Integer is_official   精选传1,其他不传
//            map.put("type", "");
//        }
        subscription = Netword.getInstance().getApi(HomeApi.class)
                .homeLastProgram(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeLastProgramBean>(getActivity()) {
                    @Override
                    public void successed(HomeLastProgramBean result) {
                        if (result == null) return;
                        if (page == 1) {
                            mProductList.clear();
                        }
                        List<ProductListBean.ProductBean> list = result.productList;
                        if (page > 1 && list.isEmpty()) {
//                            Utils.show(getActivity(), "亲,已经到底了~");
                            rvHome.noMoreLoading();
                            isBottom = true;
                            return;
                        }
                        mProductList.addAll(list);
                        int isAgencyStatus = sp.getInt("isAgencyStatus", 0);
                        if (page == 1) {
                            //轮播图图片数据集合
                            final List<HomeLastProgramBean.programaBean.ListBean> list1 = result.programa.indexBannerList;
                            Glide.with(getActivity()).load(list1.get(0).img).into(lastRollViewPager);
                            lastRollViewPager.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!TextUtils.isEmpty(list1.get(0).link)) {
                                        startActivity(new Intent(getActivity(), EmptyWebActivity.class)
                                                .putExtra("url", list1.get(0).link));
                                    }

                                }
                            });
//                            mAdapter = new CustomHomeAdapter(mProductList);
//                            rvHome.addItemDecoration(new GridSpacingItemDecoration(9, 8, false));
//                            rvHome.setAdapter(mAdapter);
//                            mAdapter.setOnItemClick(HomeTableFragment.this);
                            if (mAdapter == null) {
                                mAdapter = new ProductAdapter.GridAdapter1(getActivity(), mProductList);

                                mLayoutManager = new GridLayoutManager(getContext(), 2);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvHome.addItemDecoration(new GridItemDecoration(
                                        new GridItemDecoration.Builder(getActivity())
                                                .isExistHead(true)
                                                .margin(8, 8)
                                                .horSize(10)
                                                .verSize(10)
                                                .showLastDivider(true)
                                ));

                                rvHome.addHeaderView(header);
                                rvHome.setNestedScrollingEnabled(false);
                                rvHome.setLayoutManager(mLayoutManager);
                                rvHome.setAdapter(mAdapter);
                            }
                        } else {
//                            mAdapter.notifyDataSetChanged();
//                            rvHome.refreshComplete();
                        }
                        mAdapter.notifyDataSetChanged();
                        rvHome.refreshComplete();
                    }

                });
    }


    /**
     * 活动是否已经显示过了<br>
     * 打开一次app，最多只会显示一次
     */
    private boolean isHuodongShowed;

    /**
     * 获取红包信息
     */
    private void getHongbao() {
        Netword.getInstance().getApi(HomeApi.class)
                .envelopeactivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<EnvelopeActivityBean>(getActivity()) {
                               @Override
                               public void successed(EnvelopeActivityBean result) {
                                   if (result == null || result.activityDesc == null) {
                                       return;
                                   }
                                   EnvelopeActivityBean.ActivityDesc ad = result.activityDesc;
                                   if (ad.status == 0 || isHuodongShowed && (ad.status == 1 || ad.status == 3)
                                           || ad.status == 2 && ad.appuserId != null) {
                                       return;
                                   }
                                   if (dialog != null && dialog.isShowing()) {
                                       return;
                                   }
                                   DrawableTypeRequest<String> dtr = Glide.with(getActivity()).load(ad.activeImageUrl);
                                   if (ad.status == 1) {
                                       Uri uri = Uri.parse(ad.activeUrl);
                                       dtr.into(new HuodongTarget(uri));
                                   } else if (ad.status == 2) {
                                       dtr.into(new HuodongTarget());
                                   } else if (ad.status == 3) {
                                       dtr.into(new HuodongTarget(ad.alipayItemId));
                                   }
                               }
                           }
                );
    }
    private Dialog dialog;
    class HuodongTarget extends SimpleTarget<GlideDrawable> {
        @Nullable
        private Uri huodongUri;
        /**
         * 是否是商品链接
         */
        @Nullable
        private String productID;
        private ImageView imageView;
        private TextView hongbao_money;
        private TextView hongbao_explain;

        /**
         * 领红包时，点击页面的单击事件
         */
        private Runnable hongbaoRunnable;

        public HuodongTarget(@Nullable Uri huodongUri) {
            this.huodongUri = huodongUri;
        }

        public HuodongTarget(@Nullable String productID) {
            this.productID = productID;
        }

        public HuodongTarget() {
            super();
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_hongbao);
            hongbao_money = dialog.findViewById(R.id.hongbao_money);
            hongbao_money.setText("");
            hongbao_explain = dialog.findViewById(R.id.hongbao_explain);
            hongbao_explain.setText("");
            imageView = dialog.findViewById(R.id.hongbao_background);
            imageView.setImageDrawable(resource);
            imageView.setOnClickListener(v1 -> {
                //活动
                if (huodongUri != null) {
//                    if (Utils.isTaobaoUri(huodongUri)) {
//                        Utils.print("这里是跳转到淘宝的活动");
//                    } else {
//                        Utils.print("这里是跳转到其他的活动");
//                    }
                    startActivity(new Intent(getActivity(), EmptyWebActivity.class)
                            .putExtra("url", huodongUri.toString()));
                    dialog.onBackPressed();
                    isHuodongShowed = true;
                } else if (productID != null) {
                    showWaitDialog("");
                    //商品链接
                    //先查询商品信息
                    NetwordUtil.queryProductDetails(productID, new ResultBack<ProductDetailsBean>(getActivity()) {
                        @Override
                        public void successed(ProductDetailsBean result) {
                            hideWaitDialog();
                            startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                                    .putExtra(Constants.listInfo, JSON.toJSONString(result.productWithBLOBs)));
                            dialog.onBackPressed();
                        }

                        @Override
                        protected void error(int errorCode, String s) {
                            hideWaitDialog();
                        }
                    });
                    isHuodongShowed = true;
                } else {
                    if (hongbaoRunnable != null) {
                        hongbaoRunnable.run();
                    } else {
                        if (!Utils.isLogined(getActivity())) {
                            return;
                        }
                        //领取红包
                        Netword.getInstance().getApi(HomeApi.class)
                                .pickRedEnv()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new ResultBack<PickRedEnvBean>(getActivity()) {
                                    @Override
                                    public void successed(PickRedEnvBean result) {
                                        if (result != null && result.isSuccess()) {
                                            imageView.setImageResource(R.mipmap.hongbao_suc);
                                            hongbao_money.setText(result.randomRedEnvelope.amountStr);
                                            hongbao_explain.setText(result.randomRedEnvelope.envelopeDescription);
                                            hongbaoRunnable = () -> {
                                                startActivity(new Intent(getActivity(), ProfitActivity.class));
                                                dialog.onBackPressed();
                                            };
                                        } else {
                                            imageView.setImageResource(R.mipmap.hongbao_fail);
                                            hongbaoRunnable = () -> dialog.onBackPressed();
                                        }
                                    }
                                });
                    }
                }
            });
            dialog.findViewById(R.id.close).setOnClickListener(v2 -> dialog.onBackPressed());
            dialog.show();
            Window mWindow = dialog.getWindow();
            if (mWindow != null) {
                WindowManager.LayoutParams wl = mWindow.getAttributes();
                wl.width = -1;
                wl.height = -1;
                mWindow.setAttributes(wl);
                mWindow.setBackgroundDrawable(null);
            }
            hongbaoRunnable = null;
        }
    }
}