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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.conversation.EServiceContact;
import com.bumptech.glide.Glide;
import com.lechuang.shengxinyoupin.AbsMainActivity;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.base.Extra;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.model.DemoTradeCallback;
import com.lechuang.shengxinyoupin.model.LeCommon;
import com.lechuang.shengxinyoupin.model.LocalSession;
import com.lechuang.shengxinyoupin.model.bean.OwnNewsBean;
import com.lechuang.shengxinyoupin.model.bean.OwnUserInfoBean;
import com.lechuang.shengxinyoupin.model.bean.UpdataInfoBean;
import com.lechuang.shengxinyoupin.presenter.CommonAdapter;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.CommenApi;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.SigneActivity;
import com.lechuang.shengxinyoupin.view.activity.sun.SunActivity;
import com.lechuang.shengxinyoupin.view.activity.tipoff.TipOffActivity;
import com.lechuang.shengxinyoupin.view.activity.ui.LoginActivity;
import com.lechuang.shengxinyoupin.view.defineView.MGridView;
import com.lechuang.shengxinyoupin.view.defineView.NumberRollingView;
import com.lechuang.shengxinyoupin.view.defineView.PopIntegralRemind;
import com.lechuang.shengxinyoupin.view.defineView.ViewHolder;
import com.lechuang.shengxinyoupin.view.defineView.XCRoundImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class OwnFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.gv_state)
    MGridView gvState;
    @BindView(R.id.iv_set)
    ImageView ivSet;
    @BindView(R.id.iv_news)
    ImageView ivNews;
    @BindView(R.id.iv_headImg)
    XCRoundImageView iv_headImg;
    @BindView(R.id.tv_login_or_register)
    TextView tvLoginOrRegister;
    @BindView(R.id.tv_my_income)
    NumberRollingView tv_my_income;
    @BindView(R.id.ll_tipoff)
    TextView ll_tipoff;
    @BindView(R.id.ll_shaidan)
    TextView ll_shaidan;
    @BindView(R.id.tv_all_order)
    TextView tv_all_order;

    @BindView(R.id.ll_get_fanli)
    LinearLayout hehuoren;
    @BindView(R.id.ll_go_share)
    LinearLayout ll_goShare;
    @BindView(R.id.tv_item_number)
    TextView tv_item_num;

    @BindView(R.id.tv_nickname)
    TextView tv_nickname;
    @BindView(R.id.tv_signin)
    TextView tvSignin;

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
    private boolean isSign = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_own, container, false);
        unbinder = ButterKnife.bind(this, v);

        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_APPLY_AGENT_SUCCESS));
        initView();
        return v;
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/22  20:01
     * @describe 初始化下边的淘宝订单信息
     */
    private void initView() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", images[i]);
            map.put("name", str[i]);
            list.add(map);
        }
        gvState.setAdapter(new CommonAdapter<Map<String, Object>>(getActivity(), list, R.layout.home_kinds_item_new) {
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
        gvState.setOnItemClickListener(this);
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
        if (se.getBoolean(LeCommon.KEY_HAS_LOGIN, false))
            getData();
        //是否登录
        mSession.setLogin(se.getBoolean("isLogin", false));
        //id
        mSession.setId(se.getString("id", ""));
        if (se.getBoolean("isLogin", false)) {

//            tvVip.setVisibility(View.VISIBLE);
            if (!se.getString("photo", "").equals("")) {
                Glide.with(AbsApplication.getInstance()).load(se.getString("photo", "")).error(R.drawable.pic_morentouxiang).into(iv_headImg);
            }
            //没有昵称时展示手机号
            //没有昵称时展示手机号
//            if (!nick.equals(se.getString("phone", "----"))) {
//                tvPhone.setVisibility(View.GONE);
//                tvPhone.setText("手机号码: " + se.getString("phone", "----"));
//            } else {
//                tvPhone.setVisibility(View.GONE);
//            }
//            tvLoginOrRegister.setText(nick.length() > 9 ? nick.substring(0, 7) + "..." : nick);
            if (!se.getString("nickName", "").equals("")) {
//                有昵称
                String str = se.getString("phone", se.getString("phone", "----"));
                String strBefore = str.substring(0, 3);
                String strEnd = str.substring(7, 11);
                str = strBefore + "****" + strEnd;
                tvLoginOrRegister.setText("手机号:" + str);
                String ni = se.getString("nickName", "");
                tv_nickname.setText(ni);

            } else {
//                没有昵称
                String nick = se.getString("phone", se.getString("phone", "----"));
                String strBefore = nick.substring(0, 3);
                String strEnd = nick.substring(7, 11);
                String lastStr = strBefore + "****" + strEnd;
                tv_nickname.setText(lastStr);
                tvLoginOrRegister.setText("手机号:" + lastStr);

            }
            tvLoginOrRegister.setEnabled(false);
        } else {
            tvLoginOrRegister.setText("登录/注册");
            iv_headImg.setImageResource(R.drawable.pic_morentouxiang);
//            tvVip.findViewById(R.id.tv_vip).setVisibility(View.GONE);//vip
            tvLoginOrRegister.setEnabled(true);
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
                        tv_item_num.setText(result.agencyNum + "");
                        if (result.signedStatus == 0) {
                            tvSignin.setText("立即签到");
                            isSign = false;
                        } else {
                            tvSignin.setText("已签到");
                            isSign = true;
                        }

                        se.edit().putInt("isAgencyStatus", result.isAgencyStatus).apply();
                        if (result.isAgencyStatus == 1) {
                            hehuoren.setVisibility(View.GONE);
                            ll_goShare.setVisibility(View.VISIBLE);
                        } else {
                            hehuoren.setVisibility(View.VISIBLE);
                            ll_goShare.setVisibility(View.GONE);
                        }
//                        }
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

    @OnClick({R.id.iv_set, R.id.iv_news, R.id.tv_login_or_register, R.id.ll_myincom, R.id.ll_task,
            R.id.ll_huiyuan, R.id.ll_sign, R.id.ll_share_money, R.id.tv_all_order, R.id.tv_tuiguang, R.id.tv_item_number
            , R.id.ll_jifen, R.id.ll_shaidan, R.id.ll_get_fanli, R.id.ll_tipoff, R.id.ll_gouwuche, R.id.iv_shuoming})
    public void onViewClicked(View view) {
        if (mSession.isLogin()) {
            switch (view.getId()) {
                case R.id.ll_tipoff:   //爆料
                    startActivity(new Intent(getActivity(), TipOffActivity.class));
                    break;
                case R.id.iv_shuoming:   //s说明
                    new PopIntegralRemind(getActivity());
                    break;
                case R.id.ll_share_money:  //分享
                    startActivity(new Intent(getActivity(), ShareMoneyActivity.class));
                    break;

                case R.id.tv_all_order:   //订单
                    orderType = 0;
                    showTaoBaoOrderType();
                    break;

              /*  case R.id.ll_myteam:   //团队人数
                    startActivity(new Intent(getActivity(), MyTeamActivity.class));
                    break;
                case R.id.ll_hasjifeng:   //已获得积分
                    startActivity(new Intent(getActivity(), MyTeamActivity.class));
                    break;*/
                case R.id.ll_shaidan:   //晒单
                    startActivity(new Intent(getActivity(), SunActivity.class));
                    break;
                case R.id.iv_set://设置
                    Intent intent = new Intent(getActivity(), SetActivity.class).putExtra("isShowCache", true);
                    startActivityForResult(intent, Extra.CODE_MAIN_BACK);
//                    startActivity(new Intent(getActivity(), SetActivity.class));
                    break;
                case R.id.iv_news://消息
                    startActivity(new Intent(getActivity(), NewsCenterActivity.class));
                    break;
                case R.id.ll_huiyuan://vip
                    if (Utils.isNetworkAvailable(getActivity())) {

                        if (phone != null && openImPassword != null && customerServiceId != null) {
                            Log.e("在线客服的问题", "这里会进入在线客服");
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
                            Log.e("在线客服的问题", "不是网络问题，" + phone + "，" + openImPassword + "，" + customerServiceId);
                        }
                    } else {
                        Utils.show(mContext, getResources().getString(R.string.net_error));
                        Log.e("在线客服的问题", "真的是网络的问题");
                    }
                    break;
                case R.id.tv_login_or_register://注册。登录
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                case R.id.ll_myincom://账户明细
                    startActivity(new Intent(getActivity(), MyIncomeActivity.class));
                    break;
                case R.id.ll_task://任务中心
                    startActivity(new Intent(getActivity(), TaskCenterActivity.class));
                    break;
                case R.id.ll_sign://签到
                    if (isSign) {
                        Utils.D("您今日已经签到过，请明天再来");
                        return;
                    }
                    startActivity(new Intent(getActivity(), SigneActivity.class));
                    break;
                case R.id.ll_get_fanli: // 合伙人中心
                    if (se.getInt("isAgencyStatus", 0) == 1) {
                        startActivity(new Intent(getActivity(), AbsApplication.getInstance().getMyTeamActivity()));
                    } else {
                        startActivity(new Intent(getActivity(), ApplyAgentActivity.class));
                    }
                    break;
                case R.id.tv_tuiguang:   //开始推广
                    startActivity(new Intent(getActivity(), ShareMoneyActivity.class));
                    break;
                case R.id.tv_item_number:   //团队人数
                    startActivity(new Intent(getActivity(), AbsApplication.getInstance().getMyTeamActivity()));
                    break;
                case R.id.ll_jifen://积分提现
                    startActivity(new Intent(getActivity(), JinfenReflectActivity.class));
                    break;
                case R.id.ll_gouwuche:
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

    private String str[] = {"购物车", "待付款", "待发货", "待收货", "全部订单"};
    private int images[] = {R.drawable.wode_gouwuche, R.drawable.wode_daifukuan, R.drawable.wode_daifahuo, R.drawable.wode_daishouhuo,
            R.drawable.wode_shouhou};

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mSession.isLogin()) {
            switch (images[position]) {
                case R.drawable.wode_gouwuche://购物车
                    AlibcBasePage alibcBasePage = new AlibcMyCartsPage();
                    AlibcTaokeParams taokeParams = new AlibcTaokeParams(Constants.PID, "", "");
                    AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, taokeParams, exParams, new DemoTradeCallback());
                    break;
                case R.drawable.wode_daifukuan://代付款
                    orderType = 1;
                    showTaoBaoOrderType();
                    break;
                case R.drawable.wode_daifahuo://待发货
                    orderType = 2;
                    showTaoBaoOrderType();
                    break;
                case R.drawable.wode_daishouhuo://待收货
                    orderType = 3;
                    showTaoBaoOrderType();
                    break;
                case R.drawable.wode_shouhou://全部订单
                    orderType = 0;
                    showTaoBaoOrderType();

                    break;
            }
        } else {

            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
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