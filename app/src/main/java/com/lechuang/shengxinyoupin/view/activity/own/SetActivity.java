package com.lechuang.shengxinyoupin.view.activity.own;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ali.auth.third.login.callback.LogoutCallback;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.Extra;
import com.lechuang.shengxinyoupin.model.LeCommon;
import com.lechuang.shengxinyoupin.model.LocalSession;
import com.lechuang.shengxinyoupin.presenter.ToastManager;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.CommenApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.ui.ChangePwdActivity;
import com.lechuang.shengxinyoupin.view.defineView.DialogAlertView;
import com.lechuang.shengxinyoupin.view.defineView.XCRoundImageView;

import java.io.File;
import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author zhf 2017/08/14
 *         【设置】
 */
public class SetActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_set_head)
    XCRoundImageView ivSetHead;
    @BindView(R.id.tv_set_name)
    TextView tvSetName;
    @BindView(R.id.tv_set_no)
    TextView tvSetNo;
    @BindView(R.id.line_user)
    LinearLayout lineUser;
    @BindView(R.id.line_modify_pwd)
    LinearLayout lineModifyPwd;
    @BindView(R.id.line_feedback)
    LinearLayout lineFeedback;
    @BindView(R.id.line_update)
    LinearLayout lineUpdate;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.tv_clear_cache)
    TextView tvClearCache;
    @BindView(R.id.tv_cache)
    TextView tvCache;
    @BindView(R.id.line_clearCache)
    LinearLayout lineClearCache;

    private Context mContext = SetActivity.this;
    private LocalSession mSession;
    //保存用户登录信息的sp
    private SharedPreferences se;
    private boolean isShowCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        mSession = LocalSession.get(mContext);
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/25  15:44
     * @describe 初始化用户信息
     */
    @Override
    protected void onStart() {
        super.onStart();
        //昵称 没有昵称时展示手机号
        tvSetName.setText(se.getString("nickName", se.getString("phone", "----")));
        //手机号
        tvSetNo.setText("账号:" + se.getString("phone", "----"));
        //头像
        if (!se.getString("photo", "").equals("")) {
            Glide.with(AbsApplication.getInstance()).load(se.getString("photo", "")).into(ivSetHead);
        }
        isShowCache = getIntent().getBooleanExtra("isShowCache", false);
        if (isShowCache) {
            lineClearCache.setVisibility(View.VISIBLE);
//            tvCache.setText(getCacheSize(this));
        } else
            lineClearCache.setVisibility(View.GONE);
    }


    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/25  15:44
     * @describe 标题栏设置
     */
    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("设置");
    }


    @OnClick({R.id.iv_back, R.id.line_user, R.id.line_modify_pwd, R.id.line_feedback, R.id.line_update, R.id.tv_exit, R.id.line_helpCenter, R.id.line_clearCache})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.line_user://用户详情
                startActivity(new Intent(this, UserCenterActivity.class));
                break;
            case R.id.line_modify_pwd://密码修改
                //type 判断是找回密码还是修改密码    1  找回    2 修改
                startActivity(new Intent(this, ChangePwdActivity.class));
                break;
            case R.id.line_feedback: //意见补充
                startActivity(new Intent(this, FeedBackActivity.class));
                break;
            case R.id.line_update:  //检查版本更新
                startActivity(new Intent(this, VersionUpdateActivity.class));
                break;
            case R.id.tv_exit:
                loginOut();
                break;
            case R.id.line_helpCenter:
                startActivity(new Intent(this, HelpCenterActivity.class));
                break;
            case R.id.line_clearCache:
                tipDialog(this, "你确定要删除缓存，这些数据一旦删除，无法恢复");
                break;
        }
    }

    /**
     * 包含取消，确定的diolog框，用户可以自己设置内容
     */
    private void tipDialog(Context context, String str) {
        final DialogAlertView dialog = new DialogAlertView(context, R.style.CustomDialog);
        dialog.setView(R.layout.dialog_unlogin);
        dialog.show();
        ((TextView) dialog.findViewById(R.id.txt_notice)).setText(str);
        dialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView tvSure = dialog.findViewById(R.id.txt_exit);
        tvSure.setText("确定");
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                clearDiskCache(context);
//                tvCache.setText("0kb");
                Utils.D("删除成功");
            }
        });
        dialog.show();
    }

    private void loginOut() {
        Netword.getInstance().getApi(CommenApi.class)
                .logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(mContext) {
                    @Override
                    public void successed(String result) {
//                        se.edit().clear().apply();
//                        //登出  清空数据
//                        mSession.loginOut();
                        exitTaobao(result);
                        finish();
                    }
                });

    }

    //退出淘宝
    private void exitTaobao(final String result) {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();
        if (alibcLogin.isLogin()) {
            alibcLogin.logout(this, new LogoutCallback() {
                @Override
                public void onSuccess() {
                    sendBroadcast(new Intent(LeCommon.ACTION_LOGIN_OUT));
                    mSession.loginOut();
                    se.edit().clear().apply();
                    Utils.show(mContext, result);
                }

                @Override
                public void onFailure(int i, String s) {
                    ToastManager.getInstance().showShortToast(s);
                }
            });
        } else {
            sendBroadcast(new Intent(LeCommon.ACTION_LOGIN_OUT));
            mSession.loginOut();
            se.edit().clear().apply();
            // 切换到 MainActivity-->HomeFragment
            setResult(Extra.CODE_MAIN_BACK);
        }
    }

    /**
     * 清除图片磁盘缓存
     */
    public static void clearDiskCache(Context context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Glide.get(context).clearDiskCache();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }

    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    public static String getCacheSize(Context context) {
        try {
            return getFormatSize(getFolderSize(new File(context.getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化单位
     *
     * @param size size
     * @return size
     */
    private static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        if (file == null) return size;
        File[] fileList = file.listFiles();
        for (File aFileList : fileList) {
            if (aFileList.isDirectory()) {
                size = size + getFolderSize(aFileList);
            } else {
                size = size + aFileList.length();
            }
        }
        return size;
    }

}
