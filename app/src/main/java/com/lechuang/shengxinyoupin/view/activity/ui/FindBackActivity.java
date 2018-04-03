package com.lechuang.shengxinyoupin.view.activity.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.BaseActivity;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.CommenApi;
import com.lechuang.shengxinyoupin.utils.PhotoUtil;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.defineView.ClearEditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.shengxinyoupin.utils.PhotoUtil.t;

/**
 * 作者：li on 2017/10/6 08:58
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class FindBackActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_phoneNumber)
    ClearEditText etPhoneNumber;
    //    @BindView(R.id.tv_search)
//    ImageView tvSearch;
    @BindView(R.id.et_good)
    ClearEditText etGood;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.et_mima)
    ClearEditText etMima;
    @BindView(R.id.btn_complete)
    TextView btnComplete;
    //    @BindView(R.id.iv_yanjing)
//    ImageView ivYanjing;
    //type 判断是找回密码还是修改密码    1  找回    2 修改
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_find_back);
        ButterKnife.bind(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_find_back;
    }

    @Override
    protected void initTitle() {
        type = getIntent().getIntExtra("type", 2);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_code, R.id.btn_complete, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_code:
                if (!Utils.isTelNumber(etPhoneNumber.getText().toString())) {
                    Utils.show(this, "请输入正确的手机号");
                    return;
                }
                if (Utils.isNetworkAvailable(this)) {
                    String s = etPhoneNumber.getText().toString();
                   /* if (type == 1) {
                        //找回密码获取验证码
                        findPwdsendCode(s);
                    } else {
                        //修改密码获取验证码
                        changePwdSendCode(s);
                    }*/
                    if (getIntent().getIntExtra("type", 1) == 1) {
                        findPwdsendCode(s);
                    } else {
                        changePwdSendCode(s);
                    }
                    tvCode.setEnabled(false);
                } else {
                    Utils.show(this, "亲！您的网络开小差了哦");
                }
                break;
            case R.id.btn_complete:
                if (!Utils.isTelNumber(etPhoneNumber.getText().toString())) {
                    Utils.show(this, "请输入正确的手机号");
                   return;
                }
                if(Utils.isEmpty(etGood)){
                    Utils.show(this,"验证码不能为空!");
                    return;
                }
                if(Utils.isEmpty(etMima)){
                    Utils.show(this,"密码不能为空!");
                    return;
                }
                HashMap map = new HashMap();
                map.put("phone", etPhoneNumber.getText().toString());
                map.put("password", Utils.getMD5(etMima.getText().toString()));
                map.put("verifiCode", etGood.getText().toString());
                if (getIntent().getIntExtra("type", 1) == 1) {
                    //找回密码
                    findPwd(map);
                } else {
                    //修改密码
                    updatePwd(map);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 找回密码
     *
     * @param map
     */
    private void findPwd(HashMap map) {
        Netword.getInstance().getApi(CommenApi.class)
                .findPwd(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(FindBackActivity.this, result);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", etPhoneNumber.getText().toString());
                        se.commit();
                        startActivity(new Intent(FindBackActivity.this, LoginActivity.class));
                        finish();
                    }

                });
    }

    /**
     * 修改密码
     *
     * @param map
     */
    private void updatePwd(HashMap map) {
        Netword.getInstance().getApi(CommenApi.class)
                .changePassword(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(FindBackActivity.this, result);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", etPhoneNumber.getText().toString());
                        se.commit();
                        startActivity(new Intent(FindBackActivity.this, LoginActivity.class));
                        finish();
                    }

                });
    }

    /**
     * 找回密码获取验证码
     */
    private void findPwdsendCode(String phoneNumber) {
        Netword.getInstance().getApi(CommenApi.class)
                .findCode(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(FindBackActivity.this, result);
                        PhotoUtil.getCode(tvCode);
                        PhotoUtil.handler.post(t);

                    }
                });
    }

    /**
     * 修改密码获取验证码
     */
    private void changePwdSendCode(String phoneNumber) {
        Netword.getInstance().getApi(CommenApi.class)
                .updatePwdCode(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(FindBackActivity.this, result);
                        PhotoUtil.getCode(tvCode);
                        PhotoUtil.handler.post(t);

                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoUtil.closeCode();
    }
}
