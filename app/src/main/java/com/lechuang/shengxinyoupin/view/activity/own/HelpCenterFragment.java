package com.lechuang.shengxinyoupin.view.activity.own;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.BaseFragment;
import com.lechuang.shengxinyoupin.model.bean.KefuInfoBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.CommenApi;
import com.lechuang.shengxinyoupin.view.defineView.ProgressWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xianren on 2018/1/12.
 */

public class HelpCenterFragment extends BaseFragment {
    @BindView(R.id.wv_progress)
    ProgressWebView wvContent;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    View inflate;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.bangzhu, container, false);
        ButterKnife.bind(inflate);
        initView();
        getData();
        return inflate;
    }

    protected void initView() {
        ((TextView) inflate.findViewById(R.id.tv_title)).setText("使用教程");
        inflate.findViewById(R.id.iv_back).setVisibility(View.GONE);

    }

    public void getData() {
        Netword.getInstance().getApi(CommenApi.class)
                .getHelpInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<KefuInfoBean>(getActivity()) {
                    @Override
                    public void successed(KefuInfoBean result) {
                        String helpInfo = result.HelpInfo.helpInfo;
                        webData(helpInfo);
                    }
                });
    }


    private void webData(String helpInfo) {
        //记载网页
        wvContent = (ProgressWebView) inflate.findViewById(R.id.wv_progress);
        WebSettings webSettings = wvContent.getSettings();
        // 让WebView能够执行javaScript

        webSettings.setSupportZoom(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(false);//support zoom
        //自适应屏幕
        webSettings.setUseWideViewPort(false);// 这个很关键
        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setTextSize(WebSettings.TextSize.LARGEST);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // Use the API 11+ calls to disable the controls
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
        } else {
            // Use the reflection magic to make it work on earlier APIs
        }
        //加载HTML字符串进行显示
        wvContent.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
        wvContent.loadData(helpInfo, "text/html; charset=UTF-8", null);
        // 设置WebView的客户端
        wvContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;// 返回false
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        getActivity().finish();
    }
}