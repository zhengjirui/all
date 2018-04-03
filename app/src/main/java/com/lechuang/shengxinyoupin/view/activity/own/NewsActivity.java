package com.lechuang.shengxinyoupin.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.BaseActivity;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.OnItemClick;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.model.bean.OwnNewsListBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc 消息列表页面
 */
public class NewsActivity extends BaseActivity implements OnItemClick {
    private Context mContext = NewsActivity.this;
    private XRecyclerView rvNews;
    private int page = 1;
    //实体类
    //private News news;
    //消息数据
    private List<OwnNewsListBean.ListBean> newsList;
    private CommonRecyclerAdapter mAdapter;
    private RelativeLayout nothingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消息数据集合
        newsList = new ArrayList<>();
        getData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_news_recycler;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.tv_title)).setText("消息");
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
        nothingData = (RelativeLayout) findViewById(R.id.common_nothing_data);
        rvNews = (XRecyclerView) findViewById(R.id.rv_news);
        nothingData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                if (null != newsList) {
                    newsList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
                getData();
            }
        });
    }

    @Override
    protected void initData() {
        rvNews.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (null != newsList) {
                    newsList.clear();
                    if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                }
                getData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                getData();
            }
        });
    }

    //联网获取数据
    private void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .allNws(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnNewsListBean>(mContext) {
                    @Override
                    public void successed(OwnNewsListBean result) {
                        if (result == null) {
                            nothingData.setVisibility(View.VISIBLE);
//                            rvNews.refreshComplete();
                            return;
                        }
                        List<OwnNewsListBean.ListBean> list = result.list;
                        if (page == 1 && (list.toString().equals("[]") || list.size() <= 0)) {
                            nothingData.setVisibility(View.VISIBLE);
//                            if (mAdapter != null)
//                                mAdapter.notifyDataSetChanged();
//                            rvNews.refreshComplete();
                            return;
                        }
                        if (page > 1 && list.toString().equals("[]")) {
                            showShortToast("亲!已经到底了");
                            mAdapter.notifyDataSetChanged();
                            rvNews.refreshComplete();
                            return;
                        }
                        nothingData.setVisibility(View.GONE);
                        newsList.addAll(list);
                        if (page == 1) {
                            if (mAdapter == null) {
                                mAdapter = new CommonRecyclerAdapter(mContext, R.layout.item_news, newsList) {
                                    @Override
                                    public void convert(ViewHolderRecycler viewHolder, Object data) {
                                        try {
                                            OwnNewsListBean.ListBean bean = (OwnNewsListBean.ListBean) data;
                                            viewHolder.setText(R.id.tv_time, bean.createTimeStr);
                                            viewHolder.setText(R.id.tv_content, bean.content);
                                            viewHolder.setText(R.id.tv_title, "消息通知");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvNews.setNestedScrollingEnabled(false);
                                rvNews.setLayoutManager(mLayoutManager);
                                rvNews.setAdapter(mAdapter);
                                mAdapter.setOnItemClick(NewsActivity.this);
                            }
                        } else {

                        }
                        mAdapter.notifyDataSetChanged();
                        rvNews.refreshComplete();

                    }
                });
    }

    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(mContext, NewsDetailsActivity.class)
                .putExtra("time", newsList.get(position).createTimeStr)
                .putExtra("content", newsList.get(position).content));
    }
}
