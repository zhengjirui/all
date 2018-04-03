package com.lechuang.shengxinyoupin.view.activity.own;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.alibaba.mobileim.YWIMKit;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.shengxinyoupin.AbsMainActivity;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.base.Extra;
import com.lechuang.shengxinyoupin.model.DemoTradeCallback;
import com.lechuang.shengxinyoupin.model.LeCommon;
import com.lechuang.shengxinyoupin.model.LocalSession;
import com.lechuang.shengxinyoupin.model.bean.OwnNewsBean;
import com.lechuang.shengxinyoupin.model.bean.OwnUserInfoBean;
import com.lechuang.shengxinyoupin.model.bean.UpdataInfoBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.CommenApi;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.SigneActivity;
import com.lechuang.shengxinyoupin.view.activity.soufanli.ExclusiveSouFanLiActivity;
import com.lechuang.shengxinyoupin.view.activity.sun.SunActivity;
import com.lechuang.shengxinyoupin.view.activity.ui.LoginActivity;
import com.lechuang.shengxinyoupin.view.defineView.NumberRollingView;
import com.lechuang.shengxinyoupin.view.defineView.PopIntegralRemind;
import com.lechuang.shengxinyoupin.view.defineView.XCRoundImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;


/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class OwnFragment extends Fragment   {

    @BindView(R.id.iv_headImg)
    XCRoundImageView iv_headImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_phone)
    TextView userPhone;

    @BindView(R.id.tv_my_income)
    NumberRollingView tv_my_income;

    @BindView(R.id.qiandao)
    TextView qiandao;
    /**
     * 还没有成为合伙人
     */
    @BindView(R.id.agency_no)
    View agency_no;
    /**
     * 已经是合伙人了
     */
    @BindView(R.id.agency_yes)
    View agency_yes;
    /**
     * 合伙人的人数
     */
    @BindView(R.id.renshu)
    TextView renshu;
    private LocalSession mSession;
    //打开页面的方法
    private AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
    private Map exParams = new HashMap<>();

    //保存用户登录信息的sp
    private SharedPreferences se;

    private int signedStatus;
    private Unbinder unbinder;
    private LocalSession session;
    private String url;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_own, container, false);
        unbinder = ButterKnife.bind(this, v);

        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_APPLY_AGENT_SUCCESS));
        initView();
        PullToRefreshScrollView pullview = v.findViewById(R.id.pullview);
        pullview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getData();
                refreshView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
            }
        });
        return v;
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/22  20:01
     * @describe 初始化下边的淘宝订单信息
     */
    private void initView() {
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSession = LocalSession.get(getActivity());
        /*List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", images[i]);
            map.put("name", str[i]);
            list.add(map);
        }
        gvState.setAdapter(new CommonAdapter<Map<String, Object>>(getActivity(), list, R.layout.home_kinds_item) {
            @Override
            public void setData(ViewHolder viewHolder, Object item) {
                viewHolder.getView(R.id.iv_kinds_img).setVisibility(View.GONE);
                viewHolder.getView(R.id.iv_own).setVisibility(View.VISIBLE);
                HashMap<String, Object> map = (HashMap<String, Object>) item;
                String name = (String) map.get("name");
                int img = (int) map.get("image");
                viewHolder.setText(R.id.tv_kinds_name, name);
                viewHolder.setImageResource(R.id.iv_own, img);
            }
        });
        gvState.setOnItemClickListener(this);*/
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
    }


    //代理申请成功广播通知
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LeCommon.ACTION_APPLY_AGENT_SUCCESS.equals(action)) {
                //申请代理成功
               /* llGetFanli.setVisibility(View.GONE);
                llGoShare.setVisibility(View.VISIBLE);*/
                getData();
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        if (se.getBoolean(LeCommon.KEY_HAS_LOGIN, false)){
            getData();
        }
        //是否登录
        mSession.setLogin(se.getBoolean("isLogin", false));
        //id
        mSession.setId(se.getString("id", ""));
        if (se.getBoolean("isLogin", false)) {
            if (!se.getString("photo", "").equals("")) {
                Glide.with(AbsApplication.getInstance()).load(se.getString("photo", "")).error(R.drawable.pic_morentouxiang).into(iv_headImg);
            }
            userName.setText(se.getString("nickName", ""));
            userPhone.setText(Utils.hidePhone(se.getString("phone", "")));
        } else {
            iv_headImg.setImageResource(R.drawable.pic_morentouxiang);
            tv_my_income.setContent("0");
        }
    }

    private void getData() {
        getDataKefu();
        Netword.getInstance().getApi(OwnApi.class)
                .userInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnUserInfoBean>(getActivity()) {
                    @Override
                    public void successed(OwnUserInfoBean result) {
                        int signStatus = result.signedStatus;
                        if (signStatus == 1) {
                            qiandao.setText("已签到");
                        } else {
                            qiandao.setText("立即签到");
                        }
                        /*if (result.vipGradeName != null && !result.vipGradeName.equals("")) {
                            tvVip.setText(result.vipGradeName);
                        } else {
                            tvVip.setText("了解会员");
                        }*/
                        //我的收益
                        tv_my_income.setUseCommaFormat(false);
                        tv_my_income.setFrameNum(50);
                        tv_my_income.setContent(result.sumIntegral);
                        signedStatus = result.signedStatus;
                        se.edit().putInt("isAgencyStatus", result.isAgencyStatus).apply();
                        if (result.isAgencyStatus == 1) {
                            renshu.setText(result.agencyNum + "");
                            agency_no.setVisibility(View.GONE);
                            agency_yes.setVisibility(View.VISIBLE);
                        }else{
                            agency_no.setVisibility(View.VISIBLE);
                            agency_yes.setVisibility(View.GONE);
                        }
                    }
                });
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
                       /* if (phone != null && openImPassword != null && customerServiceId != null) {
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
                        }else{
                            Utils.show(mContext, getResources().getString(R.string.net_error));
                        }*/
                    }
                });
    }

    @OnClick({R.id.iv_set, R.id.iv_news, R.id.iv_shuoming, R.id.qiandao, R.id.ll_share_money,
            R.id.agency_no, R.id.tuiguang, R.id.tuandui,
            R.id.ll_myincom, R.id.ll_tixian, R.id.ll_chaojisou,
            R.id.ll_renwu, R.id.ll_dingdan, R.id.ll_gouwuche,
            R.id.ll_shaidan, R.id.ll_kefu, R.id.ll_help})
    public void onViewClicked(View view) {
        if (mSession.isLogin()) {
            switch (view.getId()) {
                case R.id.iv_set://设置
                    Intent intent = new Intent(getActivity(), SetActivity.class);
                    startActivityForResult(intent, Extra.CODE_MAIN_BACK);
//                    startActivity(new Intent(getActivity(), SetActivity.class));
                    break;
                case R.id.iv_news://消息
                    startActivity(new Intent(getActivity(), NewsCenterActivity.class));
                    break;
                case R.id.iv_shuoming:   //s说明
                    new PopIntegralRemind(getActivity());
                    break;
                case R.id.qiandao://签到
                    startActivity(new Intent(getActivity(), SigneActivity.class));
                    break;
                case R.id.ll_share_money:  //分享
                    startActivity(new Intent(getActivity(), ShareMoneyActivity.class));
                    break;
                case R.id.agency_no: // 合伙人中心
                    startActivity(new Intent(getActivity(), ApplyAgentActivity.class));
                    break;
                case R.id.tuiguang: // 推广
                    startActivity(new Intent(getActivity(), ShareMoneyActivity.class));
                    break;
                case R.id.tuandui: // 团队
                    startActivity(new Intent(getActivity(), MyTeamActivity.class));
                    break;
                case R.id.ll_myincom://收益
                    startActivity(new Intent(getActivity(), MyIncomeActivity.class));
                    break;
                case R.id.ll_tixian://积分提现
                    startActivity(new Intent(getActivity(), JinfenReflectActivity.class));
                    break;
                case R.id.ll_chaojisou:
                    startActivity(new Intent(getActivity(), ExclusiveSouFanLiActivity.class));
                    break;
                case R.id.ll_renwu://任务中心
                    startActivity(new Intent(getActivity(), TaskCenterActivity.class));
                    break;
                case R.id.ll_dingdan:   //订单
                    orderType = 0;
                    showTaoBaoOrderType();
                    break;
                case R.id.ll_gouwuche:
                    AlibcBasePage alibcBasePage = new AlibcMyCartsPage();
                    AlibcTaokeParams taokeParams = new AlibcTaokeParams(Constants.PID, "", "");
                    AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, taokeParams, exParams, new DemoTradeCallback());
                    break;
                case R.id.ll_shaidan:   //晒单
                    startActivity(new Intent(getActivity(), SunActivity.class));
                    break;
                case R.id.ll_kefu://vip
                    startActivity(new Intent(getActivity(), HelpCenterActivity.class));
//                    if (Utils.isNetworkAvailable(getActivity())) {
//
//                        if (phone != null && openImPassword != null && customerServiceId != null) {
//                            Log.e("在线客服的问题", "这里会进入在线客服");
//                            //此实现不一定要放在Application onCreate中
//                            //此对象获取到后，保存为全局对象，供APP使用
//                            //此对象跟用户相关，如果切换了用户，需要重新获取
//                            mIMKit = YWAPI.getIMKitInstance(phone, Constants.APP_KEY);
//                            //开始登录
//                            IYWLoginService loginService = mIMKit.getLoginService();
//                            YWLoginParam loginParam = YWLoginParam.createLoginParam(phone, openImPassword);
//                            loginService.login(loginParam, new IWxCallback() {
//
//                                @Override
//                                public void onSuccess(Object... arg0) {
//                                    //userid是客服帐号，第一个参数是客服帐号，第二个是组ID，如果没有，传0
//                                    EServiceContact contact = new EServiceContact(customerServiceId, 0);
//                                    //如果需要发给指定的客服帐号，不需要Server进行分流(默认Server会分流)，请调用EServiceContact对象
//                                    //的setNeedByPass方法，参数为false。
//                                    contact.setNeedByPass(false);
//                                    Intent intent = mIMKit.getChattingActivityIntent(contact);
//                                    startActivity(intent);
//                                }
//
//                                @Override
//                                public void onProgress(int arg0) {
//                                    // TODO Auto-generated method stub
//                                }
//
//                                @Override
//                                public void onError(int errCode, String description) {
//                                    //如果登录失败，errCode为错误码,description是错误的具体描述信息
////                                    Utils.show(mContext, description);
//                                }
//                            });
//                        } else {
//                            Utils.show(mContext, getResources().getString(R.string.net_error));
//                            Log.e("在线客服的问题", "不是网络问题，" + phone + "，" + openImPassword + "，" + customerServiceId);
//                        }
//                    } else {
//                        Utils.show(mContext, getResources().getString(R.string.net_error));
//                        Log.e("在线客服的问题", "真的是网络的问题");
//                    }
                    break;
                case R.id.ll_help:
                    startActivity(new Intent(getActivity(), HelpCenterActivity.class));
                    break;

            }
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    int orderType = 0;


    /**
     * 淘宝更改
     *
     * @param nick
     */
    private void updateInfoTaobao(final String nick) {
        Map<String, String> map = new HashMap<>();
        map.put("taobaoNumber", nick);
        Netword.getInstance().getApi(CommenApi.class)
                .updataInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<UpdataInfoBean>(getActivity()) {
                    @Override
                    public void successed(UpdataInfoBean result) {
                        se.edit().putString("taobaoNumber", nick).commit();
                    }
                });

    }



    //淘宝信息  订单 售后 ....
    private void showTaoBaoOrderType() {
        AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(orderType, true);
        AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, null, exParams, new DemoTradeCallback());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlibcTradeSDK.destory();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
           /* if (data.getIntExtra("sign", 0) == 1) {
                tv_sign.setText("已签到");
            } else {
                tv_sign.setText("签到");
            }*/
        } else if (requestCode == Extra.CODE_MAIN_BACK && resultCode == Extra.CODE_MAIN_BACK) {
            // 切换到 HomeFragment
            AbsMainActivity activity = (AbsMainActivity) getActivity();
            activity.backHomeFragment();
        }
    }

}