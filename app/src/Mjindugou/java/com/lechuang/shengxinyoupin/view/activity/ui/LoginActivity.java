package com.lechuang.shengxinyoupin.view.activity.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ali.auth.third.core.model.Session;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.callback.AlibcLoginCallback;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsActivity;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.base.Extra;
import com.lechuang.shengxinyoupin.model.LeCommon;
import com.lechuang.shengxinyoupin.model.LocalSession;
import com.lechuang.shengxinyoupin.model.bean.DataBean;
import com.lechuang.shengxinyoupin.model.bean.TaobaoLoginBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.QUrl;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.CommenApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.home.EmptyWebActivity;
import com.lechuang.shengxinyoupin.view.activity.own.BoundPhoneActivity;
import com.lechuang.shengxinyoupin.view.dialog.FlippingLoadingDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/9/27 14:39
 * 邮箱：961567115@qq.com
 * 修改备注:登录界面
 */
public class LoginActivity extends AbsActivity implements View.OnClickListener {
    /**
     * 淘宝登陆的按钮
     */
    private View login_taobao;
    /**
     * 手机号
     */
    private EditText login_phone_et;
    /**
     * 验证码
     */
    private View login_verification;
    /**
     * 发送验证码
     */
    private TextView login_verification_send;
    /**
     * 验证码输入框
     */
    private EditText login_verification_et;
    /**
     * 密码
     */
    private EditText login_password_et;
    /**
     * 眼睛
     */
    private View login_password_yanjing;
    /**
     * 登入按钮
     */
    private View login_login;
    /**
     * 注册按钮
     */
    private View login_register;
    /**
     * 忘记密码按钮
     */
    private View login_forget;
    /**
     * 登入的额外信息：用户协议和忘记密码
     */
    private View login_login_ewai;

    public FlippingLoadingDialog mLoadingDialog;
    public LocalSession mSession;
    public SharedPreferences.Editor se;

    /**
     * 当前的模式<br>
     * 1登陆；2注册；3忘记密码
     */
    private int modeType;
    private Handler handler = new Handler();
    private CountDownRunnable countDownRunnable;

    private ImageView closeIV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");

//        findViewById(R.id.iv_back).setOnClickListener(this);

        login_login = findViewById(R.id.login_login);
        login_register = findViewById(R.id.login_register);
        login_forget = findViewById(R.id.login_forget);

        login_login.setOnClickListener(this);
        login_register.setOnClickListener(this);
        login_forget.setOnClickListener(this);

        login_taobao = findViewById(R.id.login_taobao);
        login_taobao.setOnClickListener(this);

        login_phone_et = findViewById(R.id.login_phone_et);

        login_verification = findViewById(R.id.login_verification);
        login_verification_et = login_verification.findViewById(R.id.login_verification_et);
        login_verification_send = login_verification.findViewById(R.id.login_verification_send);
        login_verification_send.setOnClickListener(this);
        countDownRunnable = new CountDownRunnable(login_verification_send, login_verification_send);


        login_password_et = findViewById(R.id.login_password_et);
        login_password_yanjing = findViewById(R.id.login_password_yanjing);
        login_password_yanjing.setOnClickListener(this);

        login_login_ewai = findViewById(R.id.login_login_ewai);
        login_login_ewai.findViewById(R.id.goto_forget).setOnClickListener(this);
        TextView xieyi = login_login_ewai.findViewById(R.id.xieyi);
        xieyi.setText("您已同意" + getString(R.string.app_agreement));
        xieyi.setOnClickListener(this);
        login_login_ewai.findViewById(R.id.goto_forget).setOnClickListener(this);
        mSession = LocalSession.get(this);
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this).edit();

        closeIV = findViewById(R.id.close);
        closeIV.setOnClickListener(this);
        changeModeType(1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_login://登入
                if (view.isSelected()) {
                    loginRegisterFind();
                } else {
                    changeModeType(1);
                }
                break;
            case R.id.login_register://注册
                if (view.isSelected()) {
                    loginRegisterFind();
                } else {
                    changeModeType(2);
                }
                break;
            case R.id.login_forget://找回密码
                loginRegisterFind();
                break;
            case R.id.goto_forget://切换到找回密码
                changeModeType(3);
                break;
            case R.id.login_taobao://淘宝登录
                taobaoLogin();
                break;
            case R.id.login_verification_send:
                String userPhone = login_phone_et.getText().toString();
                //输入框为空 提示用户
                if (userPhone.length() == 0) {
                    Utils.show(this, "请输入手机号!");
                    return;
                }
                //输入的不是正确的手机号
                if (!Utils.isTelNumber(userPhone)) {
                    Utils.show(this, "请输入正确的手机号!");
                    return;
                }
                if (Utils.isNetworkAvailable(this)) {
                    if (modeType == 2) {
                        //注册获取验证码
                        sendCode(userPhone);
                    } else if (modeType == 3) {
                        //找回密码获取验证码
                        findPwdsendCode(userPhone);
                    }
                } else {
                    Utils.show(this, "亲！您的网络开小差了哦");
                }
                break;
            case R.id.xieyi:
                startActivity(new Intent(LoginActivity.this, EmptyWebActivity.class)
                        .putExtra(Extra.LOAD_URL, QUrl.userAgreement).putExtra(Extra.TITLE, "用户协议"));
                break;
            case R.id.login_password_yanjing:
                setPasswordShow();
                break;
            case R.id.close:
                onBackPressed();
                break;
        }
    }

    /**
     * 切换模式
     *
     * @param modeType 1登陆；2注册；3忘记密码
     */
    private void changeModeType(int modeType) {
        if (this.modeType != modeType) {
            this.modeType = modeType;
            switch (modeType) {
                case 1://登入
                    login_login.setSelected(true);
                    login_register.setSelected(false);
                    login_forget.setVisibility(View.GONE);

                    login_verification.setVisibility(View.GONE);
                    login_taobao.setVisibility(View.VISIBLE);
                    login_login_ewai.setVisibility(View.VISIBLE);

                    closeIV.setImageResource(R.mipmap.icon_chacha);
                    break;
                case 2://注册
                    login_login.setSelected(false);
                    login_register.setSelected(true);
                    login_forget.setVisibility(View.GONE);

                    login_verification.setVisibility(View.VISIBLE);
                    login_taobao.setVisibility(View.GONE);
                    login_login_ewai.setVisibility(View.GONE);

                    closeIV.setImageResource(R.mipmap.icon_chacha);
                    break;
                case 3://忘记密码
                    login_login.setSelected(false);
                    login_register.setSelected(false);
                    login_forget.setVisibility(View.VISIBLE);
                    login_forget.setSelected(true);

                    login_verification.setVisibility(View.VISIBLE);
                    login_taobao.setVisibility(View.GONE);
                    login_login_ewai.setVisibility(View.GONE);
                    closeIV.setImageResource(R.drawable.huiyuan_fanhui);
                    break;
            }
        }
    }

    private void setPasswordShow() {
        int selection;
        if (getCurrentFocus() == login_password_et) {
            selection = login_password_et.getSelectionEnd();
        } else {
            selection = -1;
        }
        if (login_password_yanjing.isSelected()) {
            login_password_yanjing.setSelected(false);
            login_password_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            login_password_yanjing.setSelected(true);
            login_password_et.setTransformationMethod(null);
        }
        if (selection > -1) {
            login_password_et.setSelection(selection);
        }
    }

    @Override
    public void onBackPressed() {
        if (modeType == 3) {
            changeModeType(1);
            return;
        }
        super.onBackPressed();
    }

    /**
     * 执行登录或注册或找回密码
     */
    private void loginRegisterFind() {
        String userPhone = login_phone_et.getText().toString();
        //输入框为空 提示用户
        if (userPhone.length() == 0) {
            Utils.show(this, "请输入手机号!");
            return;
        }
        //输入的不是正确的手机号
        if (!Utils.isTelNumber(userPhone)) {
            Utils.show(this, "请输入正确的手机号!");
            return;
        }
        String userPwd = login_password_et.getText().toString().trim();
        //没有输入密码
        if (userPwd.length() == 0) {
            Utils.show(this, "请输入密码");
            return;
        }
        //md5加密
        if (modeType == 1) {
            String pwd = Utils.getMD5(userPwd);
            //登录
            normalLogin(userPhone, pwd);
            return;
        }
        String verification = login_verification_et.getText().toString().trim();
        if (verification.length() == 0) {
            Utils.show(this, "请输入验证码");
            return;
        }

        if (userPwd.length() < 6 || userPwd.length() > 20) {
            Utils.show(this, "密码长度6～20位");
            return;
        }
        if (userPwd.contains(" ")) {
            Utils.show(this, "密码不能包含空格");
            return;
        }
        showLoadingDialog("请稍后");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", userPhone);
        map.put("password", Utils.getMD5(userPwd));
        map.put("verifiCode", verification);
        if (modeType == 2) {
            //注册
            register(map);
        } else if (modeType == 3) {
            //找回密码
            findPwd(map);
        }
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  17:51
     * @describe 正常登录
     */
    private void normalLogin(String number, String pwd) {
        // TODO: 2017/10/5
        Netword.getInstance().getApi(CommenApi.class)
                .login(number, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<DataBean>(this) {
                    @Override
                    public void successed(DataBean data) {
                        DataBean.UserBean result = data.user;
                        String photo = result.photo;
                        //用户信息
                        //登录状态设为true
                        mSession.setLogin(true);
                        se.putBoolean(LeCommon.KEY_HAS_LOGIN, true);
                        //绑定的支付宝号
                        if (result.alipayNumber != null) {
                            mSession.setAlipayNumber(result.alipayNumber);
                            se.putString("alipayNumber", result.alipayNumber);
                        }
                        //用户id
                        if (result.id != null) {
                            mSession.setId(result.id);
                            se.putString("id", result.id);
                        }
                        //是否是代理
                        if (result.isAgencyStatus != 0) {
                            mSession.setIsAgencyStatus(result.isAgencyStatus);
                            se.putInt(LeCommon.KEY_AGENCY_STATUS, result.isAgencyStatus);
                        }
                        //昵称
                        if (result.nickName != null) {
                            mSession.setName(result.nickName);
                            se.putString("nickName", result.nickName);
                        }
                        //用户手机号
                        if (result.phone != null) {
                            mSession.setPhoneNumber(result.phone);
                            se.putString("phone", result.phone);
                        }
                        //头像
                        if (result.photo != null) {
                            mSession.setImge(result.photo);
                            se.putString("photo", result.photo);
                        }
                        //safeToken
                        if (result.safeToken != null) {
                            mSession.setSafeToken(result.safeToken);
                            se.putString("safeToken", result.safeToken);
                        }
                        //淘宝号
                        if (result.taobaoNumber != null) {
                            mSession.setAccountNumber(result.taobaoNumber);
                            se.putString("taobaoNumber", result.taobaoNumber);
                        }
                               /* se.putString("userId", userId);
                                se.putString("pwd", pwd)*/
                        ;
                        se.commit();
                        //登陆成功
                        Utils.show(LoginActivity.this, "登陆成功");
                        sendBroadcast(new Intent(LeCommon.ACTION_LOGIN_SUCCESS));
                        finish();
                    }
                });
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  18:43
     * @describe 注册
     */
    private void register(HashMap<String, String> map) {
        Netword.getInstance().getApi(CommenApi.class)
                .register(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(LoginActivity.this, result);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", login_phone_et.getText().toString());
                        se.commit();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        dismissLoadingDialog();
                        finish();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  15:44
     * @describe 淘宝的登录
     */
    private void taobaoLogin() {
        showLoadingDialog("");
        final AlibcLogin alibcLogin = AlibcLogin.getInstance();
        alibcLogin.showLogin(this, new AlibcLoginCallback() {

            @Override
            public void onFailure(int i, String s) {
                dismissLoadingDialog();

            }

            @Override
            public void onSuccess() {
                Session taobao = alibcLogin.getSession();
                mSession.setLogin(true);
                //获取淘宝头像
                mSession.setImge(taobao.avatarUrl);
                //淘宝昵称
                mSession.setName(taobao.nick);
                mSession.setAccountNumber(taobao.nick);
                se.putBoolean(LeCommon.KEY_HAS_LOGIN, true);
                //se.putString("photo",taobao.avatarUrl);
                threeLogin(mSession.getAccountNumber());
                Utils.show(LoginActivity.this, "登陆成功!");
                MobclickAgent.onProfileSignIn("web", taobao.nick);
                dismissLoadingDialog();
            }
        });
        dismissLoadingDialog();


    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/5  19:24
     * @describe 绑定手机号
     */
    public void threeLogin(String params) {
        Netword.getInstance().getApi(CommenApi.class)
                .threeLogin(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TaobaoLoginBean>(this) {
                    @Override
                    protected void error300(int errorCode, String s) {
                        if (errorCode == 300) {    //第一次登录淘宝账号,要绑定手机号
                            // TODO: 2017/10/5 绑定手机号
                            Utils.show(LoginActivity.this, s);
                            startActivity(new Intent(LoginActivity.this, BoundPhoneActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void successed(TaobaoLoginBean result) {  //代表之前绑定过手机号码
                        //用户信息
                        //登录状态设为true
                        mSession.setLogin(true);
                        se.putBoolean("isLogin", true);
                        if (result == null)
                            return;
                        //绑定的支付宝号
                        if (result.alipayNumber != null) {
                            mSession.setAlipayNumber(result.alipayNumber);
                            se.putString("alipayNumber", result.alipayNumber);
                        }
                        //用户id
                        if (result.id != null) {
                            mSession.setId(result.id);
                            se.putString("id", result.id);
                            MobclickAgent.onProfileSignIn(result.id);

                        }
                        //是否是代理
                        if (result.isAgencyStatus != 0) {
                            mSession.setIsAgencyStatus(result.isAgencyStatus);
                            se.putInt("isAgencyStatus", result.isAgencyStatus);
                        }
                        //昵称
                        if (result.nickName != null) {
                            mSession.setName(result.nickName);
                            se.putString("nickName", result.nickName);
                        }
                        //用户手机号
                        if (result.phone != null) {
                            mSession.setPhoneNumber(result.phone);
                            se.putString("phone", result.phone);
                        }
                        //头像
                        if (result.photo != null) {
                            mSession.setImge(result.photo);
                            se.putString("photo", result.photo);
                        }
                        //safeToken
                        if (result.safeToken != null) {
                            mSession.setSafeToken(result.safeToken);
                            se.putString("safeToken", result.safeToken);
                        }
                        //淘宝号
                        if (result.taobaoNumber != null) {
                            mSession.setAccountNumber(result.taobaoNumber);
                            se.putString("taobaoNumber", result.taobaoNumber);
                        }
                        se.commit();
                        finish();
                    }
                });

    }

    /**
     * 获取验证码
     */
    private void sendCode(String phoneNumber) {
        Netword.getInstance().getApi(CommenApi.class)
                .threeBind(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    protected void error300(int errorCode, String s) {
                        Utils.show(LoginActivity.this, s);
                    }

                    @Override
                    public void successed(String result) {
                        Utils.show(LoginActivity.this, result);
                        countDownRunnable.startUp();
                    }
                });
    }

    private class CountDownRunnable implements Runnable {
        private WeakReference<View> sendButton;
        private WeakReference<TextView> showTime;
        private int time;

        private CountDownRunnable(View sendButton, TextView showTime) {
            this.sendButton = new WeakReference<>(sendButton);
            this.showTime = new WeakReference<>(showTime);
        }

        /**
         * 启动
         */
        public void startUp() {
            time = Constants.TIME;
            handler.removeCallbacks(this);
            handler.post(this);
        }

        @Override
        public void run() {
            View sb = sendButton.get();
            TextView st = showTime.get();
            if (st != null && sb != null) {
                if (time <= 0) {
                    sb.setEnabled(true);
                    st.setText("重新获取");
                    handler.removeCallbacks(this);
                    return;
                }
                sb.setEnabled(false);
                st.setText(time-- + "s重新获取");
                handler.postDelayed(this, 1000);
            } else {
                handler.removeCallbacks(this);
            }
        }
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
                        Utils.show(LoginActivity.this, result);
                        countDownRunnable.startUp();
                    }
                });
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
                        Utils.show(LoginActivity.this, result);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", login_phone_et.getText().toString());
                        se.commit();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        finish();
                    }

                });
    }

    // 显示加载框
    public void showLoadingDialog(String text) {
        if (text != null) {
            mLoadingDialog.setText(text);
        }

        mLoadingDialog.show();
    }

    // 关闭加载框
    public void dismissLoadingDialog() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void finish() {
        super.finish();
        handler.removeCallbacks(countDownRunnable);
    }
}
