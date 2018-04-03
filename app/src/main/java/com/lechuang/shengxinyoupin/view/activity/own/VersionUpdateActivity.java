package com.lechuang.shengxinyoupin.view.activity.own;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.drawable.FilletBitmapDrawable;
import com.lechuang.shengxinyoupin.model.bean.OwnCheckVersionBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.QUrl;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.CommenApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.dialog.VersionUpdateDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/10 14:52
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class VersionUpdateActivity extends AppCompatActivity {
    private TextView tv_version_img;
    private Context mContext = VersionUpdateActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update);
        ButterKnife.bind(this);
        Utils.verifyStoragePermissions(this);
        initVeiw();
        getData();
    }

    private void initVeiw() {
        tv_version_img = (TextView) findViewById(R.id.tv_version_img);
        ((TextView) findViewById(R.id.tv_version)).setText(getVersion());
        ((TextView) findViewById(R.id.tv_title)).setText("版本说明");

       Bitmap logoBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.logo);

        ((ImageView)findViewById(R.id.logo)).setImageDrawable(new FilletBitmapDrawable(logoBitmap,0.1f));

//        Drawable drawable = getResources().getDrawable(R.drawable.logo_fillet);
//        drawable.setBounds(0, 0, 200, 200);
//        tv_version_img.setCompoundDrawables(drawable, null, null, null);
//        tv_version_img.setCompoundDrawables(null, drawable, null, null);
    }

    // 获取当前版本的版本号
    public String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    public void getData() {
        Netword.getInstance().getApi(CommenApi.class)
                .updataVersion("1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnCheckVersionBean>(mContext) {
                    @Override
                    public void successed(OwnCheckVersionBean result) {
                        ((TextView) findViewById(R.id.tv_versiondesc)).setText(result.app.versionDescribe);
                        if (!Utils.getAppVersionName(mContext).equals(result.maxApp.versionNumber)) {//版本号
                            /*UpdateVersion version = new UpdateVersion(mContext);
                            version.setDescribe(result.maxApp.versionDescribe);//版本描述
                            if (result.maxApp.downloadUrl != null)
                                version.setUrl(QUrl.url + result.maxApp.downloadUrl);//下载地址
                            version.showUpdateDialog();*/
                            VersionUpdateDialog version = new VersionUpdateDialog(mContext, result.maxApp.versionDescribe);
                            //下载地址
                            if (result.maxApp.downloadUrl != null)
                                version.setUrl(QUrl.url + result.maxApp.downloadUrl);
                            version.show();
                        }
                    }
                });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

}
