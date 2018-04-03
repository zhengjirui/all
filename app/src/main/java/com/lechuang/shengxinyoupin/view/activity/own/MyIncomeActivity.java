package com.lechuang.shengxinyoupin.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.model.DemoTradeCallback;
import com.lechuang.shengxinyoupin.model.bean.OwnIncomeBean;
import com.lechuang.shengxinyoupin.model.bean.OwnIncomeNoticeBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;
import com.lechuang.shengxinyoupin.utils.StringUtil;
import com.lechuang.shengxinyoupin.view.defineView.NumberRollingView;
import com.lechuang.shengxinyoupin.view.defineView.PopChoseAgency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 我的收益
 * <p>
 * 作者：li on 2017/10/6 14:37
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class MyIncomeActivity extends AppCompatActivity implements PopChoseAgency.OnChoseAgency {
    @BindView(R.id.iv_income_back)
    ImageView ivBackIncome;
    //    @BindView(R.id.tv_income_nickname)
//    TextView tvNickName;
    @BindView(R.id.tv_income_forecast_today)
    TextView tvForecastToday;
    @BindView(R.id.tv_income_total_today)
    TextView tvTotalToday;
    @BindView(R.id.tv_income_forecast_month)
    TextView tvForecastMonth;
    @BindView(R.id.tv_income_forecast_lastmonth)
    TextView tvForecastLastMonth;
    //    @BindView(R.id.iv_income_headImg)
//    XCRoundImageView ivHead;
    @BindView(R.id.tl_income_count)
    TabLayout tlCount;
    @BindView(R.id.vp_income_details)
    ViewPager vpDetails;
    @BindView(R.id.agency_income_count)
    LinearLayout mAgenctCount;
    @BindView(R.id.tv_orderDetail_list)
    TextView tvOrderDetail;

    @BindView(R.id.tv_auto_text)
    TextView tvAutoText;


    // 代理:显示  非代理:隐藏
//    @BindView(R.id.income_my_team)
//    LinearLayout mTeam;
//    @BindView(R.id.income_integral_bill)
//    LinearLayout mFriends;

    @BindView(R.id.tv_integral_total)
    NumberRollingView tvIntegralTotal;
    @BindView(R.id.tv_income_integral_enable)
    TextView tvIntegralEnable;
    @BindView(R.id.tv_income_integral_disable)
    TextView tvIntegralDisable;

    private Context mContext = MyIncomeActivity.this;

    private List<IncomeBaseFragment> fragments;
    //标题信息
    public String[] title = new String[]{"今日统计", "昨日统计", "近7日统计", "本月统计", "上月统计"};
    private IncomePaperAdapter mPaperAdapter;
    private SharedPreferences se;

    //打开页面的方法
    private AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
    private Map exParams = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_income);
        ButterKnife.bind(this);
        se = PreferenceManager.getDefaultSharedPreferences(this);

        initView();
    }

    private void initView() {
        if (se.getInt("isAgencyStatus", 0) == 1) {
//            mTeam.setVisibility(View.VISIBLE);
//            mFriends.setVisibility(View.VISIBLE);
//            tvOrderDetail.setText("订单明细");
            mAgenctCount.setVisibility(View.VISIBLE);
        } else {
//            mTeam.setVisibility(View.INVISIBLE);
//            mFriends.setVisibility(View.INVISIBLE);
//            tvOrderDetail.setText("积分账单");
            mAgenctCount.setVisibility(View.GONE);
        }

        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改

        fragments = new ArrayList<>();
        int length = title.length;
        //创建页面
        for (int i = 0; i < length; i++) {
            fragments.add((IncomeBaseFragment) setTitle(new IncomeBaseFragment(), title[i], i + 1));
        }
        //设置适配器
        mPaperAdapter = new IncomePaperAdapter(getSupportFragmentManager());
        vpDetails.setAdapter(mPaperAdapter);
        /*//设置tablout 滑动模式
        tablayoutTopoff.setTabMode(TabLayout.MODE_SCROLLABLE);*/
        //联系tabLayout和viewpager
        tlCount.setupWithViewPager(vpDetails);
        tlCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 设置头目
     */
    private Fragment setTitle(Fragment fragment, String title, int i) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("type", i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // onstart里面执行 提现积分回来后 可刷新积分信息
        initData();
    }

    public void initData() {

//        if (!se.getString("photo", "").equals("")) {
//            Glide.with(MyApplication.getInstance()).load(se.getString("photo", "")).error(R.drawable.pic_morentouxiang).into(ivHead);
//        }
//        tvNickName.setText(se.getString("nickName",""));
        tvAutoText.setVisibility(View.GONE);
        Netword.getInstance().getApi(OwnApi.class)
                .ownIncome(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnIncomeBean>(mContext) {
                    @Override
                    public void successed(OwnIncomeBean result) {
//                        Log.i(" ResultBack_successed", JSON.toJSONString(result));
                        try {
                            tvIntegralTotal.setUseCommaFormat(false);
                            tvIntegralTotal.setFrameNum(50);
                            tvIntegralTotal.setContent(result.record.sumIntegral);
                            tvIntegralEnable.setText(StringUtil.isZhengshu(result.record.withdrawalIntegral));
                            tvIntegralDisable.setText(StringUtil.isZhengshu(result.record.notWithdrawalIntegral));
                            tvForecastToday.setText("¥" + result.record.todayEstimatedIncome);
                            tvTotalToday.setText(result.record.todayVolum);
                            tvForecastMonth.setText(result.record.currentMonthIncome);
                            tvForecastLastMonth.setText(result.record.totalIncome);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Netword.getInstance().getApi(OwnApi.class)
                .ownIncomeNotice()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnIncomeNoticeBean>(mContext) {
                    @Override
                    public void successed(OwnIncomeNoticeBean result) {
                        if (result != null && result.board != null && !TextUtils.isEmpty(result.board.note)) {
                            tvAutoText.setVisibility(View.VISIBLE);
                            tvAutoText.setText(result.board.note);
                            //由于先隐藏后显示，导致tvAutoText不能直接滚动，必须延迟才行
                            new Handler().postDelayed(() -> tvAutoText.requestFocus(), 1000);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //这个接口，暂时只有金都购系列有。即时出错，也不弹提示
                    }
                });

    }

    @OnClick({R.id.iv_income_back, R.id.income_my_team, R.id.income_order_detail,
            R.id.income_integral_bill, R.id.tv_integral_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_income_back:
                finish();
                break;
            case R.id.tv_integral_withdraw:
                // 积分提现
                startActivity(new Intent(mContext, JinfenReflectActivity.class));
                break;
            case R.id.income_my_team:
                if (se.getInt("isAgencyStatus", 0) == 1) {
                    // 我的团队
                    startActivity(new Intent(MyIncomeActivity.this, AbsApplication.getInstance().getMyTeamActivity()));
                } else {
                    // add: 2018/1/9 PopWindow become agency
                    new PopChoseAgency(mContext, MyIncomeActivity.this);
                }
                break;
            case R.id.income_order_detail:
                if (se.getInt("isAgencyStatus", 0) == 1) {
                    // 订单明细
                    startActivity(new Intent(MyIncomeActivity.this, AbsApplication.getInstance().getMyOrderActivity()));
                } else {
                    // add: 2018/1/9 goto taobao order list
                    AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(0, true);
                    AlibcTrade.show(MyIncomeActivity.this, alibcBasePage, alibcShowParams, null, exParams, new DemoTradeCallback());
                }
                break;
            case R.id.income_integral_bill:
                // 积分账单
                Intent intent = new Intent(this, ProfitActivity.class);
                intent.putExtra("flag", "积分账单");
                startActivity(intent);
                break;
        }
    }




    @Override
    public void gotoAgency() {
        // 去成为代理
        startActivity(new Intent(MyIncomeActivity.this, ApplyAgentActivity.class));
    }


    /**
     * 适配器
     */
    private class IncomePaperAdapter extends FragmentPagerAdapter {

        public IncomePaperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getArguments().getString("title");
        }
    }


}
