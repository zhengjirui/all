package com.lechuang.shengxinyoupin.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.Extra;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.OnItemClick;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.bean.TeamBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/6 15:42
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class MyTeamActivity extends AppCompatActivity implements OnItemClick {

    @BindView(R.id.rv_team)
    XRecyclerView rvTeam;
    @BindView(R.id.common_loading_all)
    RelativeLayout commonLoading;

    private View header;
    private TextView tvTeamOne;
    private TextView tvTeamTwo;
    private TextView tvTeamNone;

    private Context mContext = MyTeamActivity.this;
    public int page = 1;
    private List<TeamBean.MineTeamBean.TeamSubBean> items = new ArrayList<>();
    private CommonRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team_recycler);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    public void initView() {
        header = LayoutInflater.from(mContext).inflate(R.layout.header_my_team,
                (ViewGroup) findViewById(android.R.id.content), false);

        tvTeamOne = (TextView) header.findViewById(R.id.tv_team_one);
        tvTeamTwo = (TextView) header.findViewById(R.id.tv_team_two);
        tvTeamNone = (TextView) header.findViewById(R.id.tv_team_none);

        rvTeam.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (null != items) {
                    items.clear();
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

    @OnClick({R.id.iv_team_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_team_back:
                finish();
                break;
        }
    }

    public void getData() {
        if (page == 1) {
            commonLoading.setVisibility(View.VISIBLE);
        }
        Netword.getInstance().getApi(OwnApi.class)
                .mineTeam(page, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TeamBean>(mContext) {
                    @Override
                    public void successed(TeamBean result) {
                        commonLoading.setVisibility(View.GONE);
                        if (result == null) {
                            return;
                        }
                        TeamBean.MineTeamBean mData = result.record;
                        tvTeamOne.setText(mData.members1 + "人");
                        tvTeamTwo.setText(mData.members2 + "人");
                        tvTeamNone.setText(mData.members3 + "人");
                        List<TeamBean.MineTeamBean.TeamSubBean> mList = mData.list;

                        if (page > 1 && mList.isEmpty()) {
                            rvTeam.noMoreLoading();
                            return;
                        }
                        items.addAll(mList);
                        if (1 == page) {
                            if (mAdapter == null) {
                                mAdapter = new CommonRecyclerAdapter(mContext, R.layout.myteam_item, items) {
                                    @Override
                                    public void convert(ViewHolderRecycler viewHolder, Object data) {
                                        try {
                                            final TeamBean.MineTeamBean.TeamSubBean mItem = (TeamBean.MineTeamBean.TeamSubBean) data;
                                            // 昵称
                                            viewHolder.setText(R.id.tv_team_nickname, mItem.nickname);
                                            // 近3个月贡献
                                            viewHolder.setText(R.id.tv_team_contribute, mItem.contribution3);
                                            // 成员数量`
                                            viewHolder.setText(R.id.tv_team_amount, "0".equals(mItem.nextAgentCount) ? "无" : mItem.nextAgentCount);
                                            // 操作  代理:有背景+白色12文字  非代理:无背景+84色值14字体
                                            TextView tvTeamSub = viewHolder.getView(R.id.tv_team_sub);
                                            if ("1".equals(mItem.isAgencyStatus)) {
                                                tvTeamSub.setText("查看成员");
                                                tvTeamSub.setTextSize(12);
                                                tvTeamSub.setTextColor(mContext.getResources().getColor(R.color.white));
                                                tvTeamSub.setBackground(mContext.getResources().getDrawable(R.drawable.ic_team_look));
                                                tvTeamSub.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
//                                                Toast.makeText(mContext, "userId = " + mItem.userId, Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(mContext, MySecondTeamActivity.class);
                                                        intent.putExtra(Extra.USER_ID, mItem.userIdStr);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                tvTeamSub.setText("非代理");
                                                tvTeamSub.setTextSize(14);
                                                tvTeamSub.setTextColor(mContext.getResources().getColor(R.color.c_848484));
                                                tvTeamSub.setBackground(null);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvTeam.addHeaderView(header);
                                rvTeam.setNestedScrollingEnabled(false);
                                rvTeam.setLayoutManager(mLayoutManager);
                                rvTeam.setAdapter(mAdapter);
                                mAdapter.setOnItemClick(MyTeamActivity.this);
                            }
                        } else {

                        }
                        mAdapter.notifyDataSetChanged();
                        rvTeam.refreshComplete();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        commonLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        commonLoading.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void itemClick(View v, int position) {
        // item 点击事件
    }
}
