package com.lechuang.shengxinyoupin.view.activity.own;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.BaseActivity;
import com.lechuang.shengxinyoupin.model.bean.OwnJiFenInfoBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;
import com.lechuang.shengxinyoupin.utils.StringUtil;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.defineView.ClearEditText;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc 积分提现
 */
public class JinfenReflectActivity extends BaseActivity {
    private ClearEditText et_money;
    private TextView tv_jifen;  //可提现积分
    private TextView tv_money;  //可提现金额
    private TextView tv_rule;   //提现规则  1元 = ?积分
    private TextView tv_tixian_min; //最小提现金额
    private TextView tv_ali;        //支付宝账号
    private Button btn_tixian;      //提现按钮
    private Context mContext = JinfenReflectActivity.this;
    //可提现金额
    private double withDrawMoney;
    //最小提现金额
    private int minWithDrawMoney;
    //保存用户信息的sp
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_jinfen_reflect;
    }

    @Override
    protected void initTitle() {
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.tixian_title);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //读取用户信息
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ((TextView) findViewById(R.id.tv_ali)).setText("支付宝账号  (" + sp.getString("alipayNumber", "未绑定") + ")");
        et_money = (ClearEditText) findViewById(R.id.et_money);
        findViewById(R.id.btn_tx).setOnClickListener(this);
        findViewById(R.id.ll_go_alipay).setOnClickListener(this);
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);

    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onStart() {
        super.onStart();
        jifenInfo();
    }

    //积分信息
    private void jifenInfo() {
        if (Utils.isNetworkAvailable(mContext)) {
            Netword.getInstance().getApi(OwnApi.class)
                    .jifenInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<OwnJiFenInfoBean>(mContext) {
                        @Override
                        public void successed(OwnJiFenInfoBean result) {
                            if (result == null)
                                return;
                            String number = sp.getString("alipayNumber", "");
                            if (TextUtils.isEmpty(number)) {
                                number = "未绑定";
                            }
                            ((TextView) findViewById(R.id.tv_ali)).setText("支付宝账号  (" + number + ")");
                            String s = getString(R.string.tixian_company);
                            //积分提现规则 1元 = ?积分
                            ((TextView) findViewById(R.id.tv_rule)).setText("1元=" + result.integralRate + s);
                            //用户可提现积分
                            ((TextView) findViewById(R.id.tv_jifen)).setText(StringUtil.isZhengshu(result.withdrawIntegral) + s);
                            //用户可提现金额
                            withDrawMoney = result.withdrawPrice;
                            ((TextView) findViewById(R.id.tv_money)).setText(result.withdrawPrice + "元");
                            //最小提现金额
                            minWithDrawMoney = result.withdrawMinPrice;
                            ((TextView) findViewById(R.id.tv_tixian_min)).setText("满" + result.withdrawMinPrice + "元可提现");
                            ((TextView) findViewById(R.id.tv_reflect_remind)).setText(result.cashDeclaration);
                        }
                    });
        } else {
            showShortToast(getString(R.string.net_error));
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_tx: //提现
                //是否有网络
                if (Utils.isNetworkAvailable(mContext)) {
                    //是否绑定支付宝账号
                    if (sp.getString("alipayNumber", "").equals("")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setTitle("提示");
                        dialog.setMessage("您还未绑定支付宝,前去绑定?");
                        dialog.setNegativeButton("考虑一下", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setPositiveButton("前去绑定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(mContext, CheckIdentityActivity.class));
                                finish();
                                return;
                            }
                        });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                        return;
                        //showShortToast("未绑定支付宝账号,请先绑定支付宝账号~");
                    }
                    String money = et_money.getText().toString().trim();
                    if (Utils.isEmpty(et_money)) {
                        showShortToast("提现金额不能为空");
                        return;
                    }
                    //输入的金额大于用户可提现的金额
                    if (Double.parseDouble(money) > withDrawMoney) {
                        showShortToast("提现金额超过可提现的总金额");
                        return;
                    }
                    //提现的金额小于最小的提现金额
                    if (Double.parseDouble(money) < minWithDrawMoney) {
                        showShortToast("提现金额小于最小可提现金额");
                        return;
                    }
                    //提现的金额小于最小的提现金额
                    if (Double.parseDouble(money) <= 0) {
                        showShortToast("提现金额不能小于0");
                        return;
                    }
                    //提现
                    withDraw(Double.parseDouble(money));
                } else {
                    showShortToast(getString(R.string.net_error1));
                }

                break;
            case R.id.ll_go_alipay:
                if (sp.getString("alipayNumber", "未绑定").equals("未绑定"))
                    startActivity(new Intent(mContext, CheckIdentityActivity.class));
                else {
                    startActivity(new Intent(mContext, ChangeBoundAlipayActivity.class).putExtra("pay", sp.getString("alipayNumber", "")));
                }

                break;
            default:
                break;
        }
    }

    //提现
    private void withDraw(double money) {
        Netword.getInstance().getApi(OwnApi.class)
                .jifenTx(money)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(mContext) {
                    @Override
                    public void successed(String result) {
                        showShortToast(result);
                        finish();
                    }
                });
    }
}
