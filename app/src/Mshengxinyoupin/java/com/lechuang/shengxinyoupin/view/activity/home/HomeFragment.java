package com.lechuang.shengxinyoupin.view.activity.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.conversation.EServiceContact;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.jude.rollviewpager.RollPagerView;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.OnItemClick;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.LeCommon;
import com.lechuang.shengxinyoupin.model.bean.GetBean;
import com.lechuang.shengxinyoupin.model.bean.HomeGunDongTextBean;
import com.lechuang.shengxinyoupin.model.bean.HomeKindItemBean;
import com.lechuang.shengxinyoupin.model.bean.HomeLastProgramBean;
import com.lechuang.shengxinyoupin.model.bean.HomeProgramBean;
import com.lechuang.shengxinyoupin.model.bean.HomeTodayProductBean;
import com.lechuang.shengxinyoupin.model.bean.OwnNewsBean;
import com.lechuang.shengxinyoupin.presenter.CommonAdapter;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.GetApi;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.earn.GetMoneyActivity;
import com.lechuang.shengxinyoupin.view.activity.own.TaskCenterActivity;
import com.lechuang.shengxinyoupin.view.activity.recyclerViewNew.MyAdapterNew;
import com.lechuang.shengxinyoupin.view.activity.ui.LoginActivity;
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

import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;

/**
 * Created by YRJ
 * Date 2018/3/15.
 */

public class HomeFragment extends AbsHomeFragment implements OnItemClick {
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


    TextView tv_title;

    private View v;
    private ArrayList<String> text = null;
    private SharedPreferences sp;
    public boolean isBottom = false;
    private View header;

    List<HomeTodayProductBean.productList> todaykill = new ArrayList();
    RecyclerView todayProduct;
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
            getTodayProduct();
            //首页滚动文字数据
            getHomeScrollTextView();
            //首页4个图片栏目数据
            getHomeProgram();
            // tab数据
            getTabData();
            // 商品数据(默认'全部')
            mRootId = 0;
            getProductList();

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
        tv_title = (TextView) header.findViewById(R.id.tv_title);
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
                        tv_title.setText(result.programName);
                        todaykill = result.productList;
                        todayProduct.setAdapter(new MyAdapterNew(result.adProductList, new MyAdapterNew.RecycleViewListener() {
                            @Override
                            public void itemClik(HomeTodayProductBean.adProductList listBean) {
                                startActivity(new Intent(getActivity(), ProductDetailsActivity.class
                                ).putExtra(Constants.listInfo, JSON.toJSONString(listBean)));
                            }
                        }));
//
                    }

                });
    }    //获取最底部图片

    private int[] homeKindImg = {R.mipmap.juhuasuan, R.mipmap.meirixinpin, R.mipmap.renqiremai,
            R.mipmap.pinpaiguan, R.mipmap.fenxiangzhuan};
    private String[] homeKindName = {"聚划算", "今日新品", "人气热卖", "品牌馆", "分享赚"};
    private List<HomeKindItemBean> homeKindList = new ArrayList<>();

    //获取首页分类数据
    private void getHomeKindData() {
        if (homeKindList != null) {
            homeKindList.clear();
        }
        for (int i = 0; i < homeKindImg.length; i++) {
            HomeKindItemBean bean = new HomeKindItemBean();
            bean.img = homeKindImg[i];
            bean.name = homeKindName[i];
            homeKindList.add(bean);
        }
        gvKind.setAdapter(new CommonAdapter<HomeKindItemBean>(getActivity(), homeKindList, R.layout.home_kinds_item) {
            @Override
            public void setData(ViewHolder viewHolder, Object item) {
                HomeKindItemBean bean = (HomeKindItemBean) item;
                //分类名称
                viewHolder.setText(R.id.tv_kinds_name, bean.name);
                //分类图片
                viewHolder.setImageResource(R.id.iv_kinds_img, bean.img);
            }
        });
        // TODO: 2017/10/2  分类item
        gvKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:  //聚划算
                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                .putExtra("type", 12).putExtra("name", "聚划算"));
                        break;
                    case 1:  //今日新品
                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                .putExtra("type", 9).putExtra("name", "今日新品"));
                        break;
                    case 2:  //人气热卖
                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                .putExtra("type", 8).putExtra("name", "人气热卖"));
                        break;
                    case 3:  //品牌馆
                        startActivity(new Intent(getActivity(), KindDetailActivity.class)
                                .putExtra("type", 4).putExtra("name", "品牌馆"));
                        break;
                    case 4:  //分享赚
                        startActivity(new Intent(getActivity(), GetMoneyActivity.class));
                        break;
                    default:
                        break;
                }
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
//    private ArrayList<HomeLastProgramBean.ListBean> lastProgramList = new ArrayList<>();

    @OnClick({R.id.tv_search, R.id.iv_news, R.id.iv_task, R.id.iv_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_task:
                if (sp.getBoolean("isLogin", false) == true) {
//                    startActivity(new Intent(getActivity(), SigneActivity.class));
                    startActivity(new Intent(getActivity(), TaskCenterActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.tv_search:
                startActivity(new Intent(getActivity(), SearchActivity.class).putExtra("whereSearch", 1));
                break;
            //签到
            case R.id.iv_news:
                if (sp.getBoolean("isLogin", false) == true) {
                    if (Utils.isNetworkAvailable(getActivity())) {

                        if (phone != null && openImPassword != null && customerServiceId != null) {
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
                                    //userid是客服帐号，第一个参数是客服帐号，第二个是组ID，如果没有，传0
                                    EServiceContact contact = new EServiceContact(customerServiceId, 0);
                                    //如果需要发给指定的客服帐号，不需要Server进行分流(默认Server会分流)，请调用EServiceContact对象
                                    //的setNeedByPass方法，参数为false。
                                    contact.setNeedByPass(false);
                                    Intent intent = mIMKit.getChattingActivityIntent(contact);
                                    startActivity(intent);
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


                    } else {
                        Utils.show(mContext, getResources().getString(R.string.net_error));
                    }
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
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
                .subscribe(new ResultBack<OwnNewsBean>(mContext) {
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
                            topTab.rootId = 0;
                            topTab.rootName = "全部";
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
    private ArrayList<HomeLastProgramBean.ProductBean> mProductList = new ArrayList<>();
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
                        if (page == 1 && mProductList != null) {
                            mProductList.clear();
                        }
                        List<HomeLastProgramBean.ProductBean> list = result.productList;
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
                                mAdapter = new CommonRecyclerAdapter<HomeLastProgramBean.ProductBean>(getActivity(), R.layout.item_last_program1, mProductList) {
                                    @Override
                                    public void convert(ViewHolderRecycler holder, HomeLastProgramBean.ProductBean bean) {
                                        try {
                                            //商品图
//                                            Glide.with(mContext).load(bean.imgs).into((ImageView) holder.getView(R.id.iv_img));
                                            holder.displayImage(R.id.iv_img, bean.imgs, R.drawable.loading_square);
                                            //动态调整滑动时的内存占用
                                            Glide.get(mContext).setMemoryCategory(MemoryCategory.LOW);
                                            // 原价
                                            TextView tvZhuan = holder.getView(R.id.tv_get);
                                            ImageView shoptype = holder.getView(R.id.shoptype);
                                            TextView tv_xiaoliang = holder.getView(R.id.tv_xiaoliang);
                                            TextView spannelTextView = holder.getView(R.id.spannelTextView);
                                            spannelTextView.setText(bean.name);


                                            if (bean.shopType.equals("1")) {
                                                shoptype.setImageDrawable(getResources().getDrawable(R.drawable.zhuan_taobao));

                                            } else {
                                                shoptype.setImageDrawable(getResources().getDrawable(R.drawable.zhuan_tmall));

                                            }
                                            tv_xiaoliang.setText("销量：" + bean.nowNumber + "件");
                                            if (bean.zhuanMoney != null) {
                                                tvZhuan.setVisibility(View.VISIBLE);
                                                tvZhuan.setText(bean.zhuanMoney);
                                            } else {
                                                tvZhuan.setVisibility(View.GONE);
                                            }
                                            // 领券减
                                            TextView tvCoupon = holder.getView(R.id.tv_quanMoney);
                                            if (bean.couponMoney != null) {
                                                tvCoupon.setVisibility(View.VISIBLE);
                                                tvCoupon.setText(bean.couponMoney);
                                            } else {
                                                tvCoupon.setVisibility(View.GONE);
                                            }
                                            TextView tvOldPrice = holder.getView(R.id.tv_oldprice);
                                            tvOldPrice.setText("¥" + bean.price + "");
                                            // 中划线
                                            tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                                            // 券后价
                                            holder.setText(R.id.tv_nowprice, "¥" + bean.preferentialPrice);


                                            //商品名称
//                                            holder.setSpannelTextView(R.id.spannelTextView, bean.name, Integer.parseInt(bean.shopType));
                                            //赚
//            if (isAgencyStatus == 1) {
//                holder.tvGet.setVisibility(View.VISIBLE);
//            } else {
//                holder.tvGet.setVisibility(View.GONE);
//            }
                                            holder.setText(R.id.tv_get, bean.zhuanMoney);
                                            //销量
//                                            holder.setText(R.id.tv_xiaoliang, "已抢" + bean.nowNumber + "件");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };


                                mLayoutManager = new LinearLayoutManager(getContext());
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
                                mAdapter.setOnItemClick(HomeFragment.this);
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

    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                .putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
    }

}
