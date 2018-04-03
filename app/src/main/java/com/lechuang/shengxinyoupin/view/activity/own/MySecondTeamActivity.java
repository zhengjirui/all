package com.lechuang.shengxinyoupin.view.activity.own;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.Extra;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.OnItemClick;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.bean.TeamNextBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;
import com.lechuang.shengxinyoupin.view.defineView.XCRoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MySecondTeamActivity extends AppCompatActivity implements OnItemClick {

    @BindView(R.id.iv_headImg)
    XCRoundImageView ivHead;
    @BindView(R.id.tv_team_nickname)
    TextView tvNickName;
    @BindView(R.id.tv_team_contribute)
    TextView tvContribute;
    @BindView(R.id.rv_second_team)
    XRecyclerView rvTeam;
    @BindView(R.id.common_nothing_data)
    RelativeLayout nothingData;

    public int page = 1;
    private String mUserId;
    private boolean isItemBg = true;
    private Context mContext = MySecondTeamActivity.this;
    private CommonRecyclerAdapter mAdapter;
    private List<TeamNextBean.TeamNext.TeamMember> items = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_second_team);
        ButterKnife.bind(this);

        initView();
        getData();
    }

    private void initView() {
        mUserId = getIntent().getStringExtra(Extra.USER_ID);

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

    @OnClick({R.id.iv_team_back, R.id.common_nothing_data})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.iv_team_back:
                finish();
                break;
            case R.id.common_nothing_data:
                page = 1;
                if (null != items) {
                    items.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
                getData();
                break;
        }
    }

    private void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .nextTeam(mUserId, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TeamNextBean>(mContext) {
                    @Override
                    public void successed(TeamNextBean result) {
                        if (result == null) {
                            nothingData.setVisibility(View.VISIBLE);
                            return;
                        }
                        TeamNextBean.TeamNext mData = result.record;
                        tvContribute.setText(mData.contributionSum);
                        tvNickName.setText(mData.nickname);
                        Glide.with(mContext).load(mData.photo).error(R.drawable.pic_morentouxiang).into(ivHead);
                        List<TeamNextBean.TeamNext.TeamMember> mList = mData.list;
                        if (page == 1 && (mList.toString().equals("[]") || mList.size() <= 0)) {
                            nothingData.setVisibility(View.VISIBLE);
                            return;
                        }
                        nothingData.setVisibility(View.GONE);
                        if (items.size() > 0 && mList.toString().equals("[]")) {
                            Toast.makeText(mContext, "亲!已经到底了", Toast.LENGTH_SHORT).show();
                            rvTeam.refreshComplete();
                            return;
                        }
                        int size = mList.size();
                        for (int i = 0; i < size; i++) {
                            items.add(mList.get(i));
                        }

                        if (1 == page) {

                            if (mAdapter == null) {
                                mAdapter = new CommonRecyclerAdapter(mContext, R.layout.item_next_team, items) {
                                    @Override
                                    public void convert(ViewHolderRecycler viewHolder, Object data) {
                                        try {
                                            final TeamNextBean.TeamNext.TeamMember mItem = (TeamNextBean.TeamNext.TeamMember) data;
                                            LinearLayout itemInfo = viewHolder.getView(R.id.item_second_info);
                                            if (isItemBg) {
                                                itemInfo.setBackgroundColor(getResources().getColor(R.color.c_FAFAFA));
                                                isItemBg = false;
                                            } else {
                                                itemInfo.setBackgroundColor(getResources().getColor(R.color.white));
                                                isItemBg = true;
                                            }
                                            // 头像
                                            XCRoundImageView ivHead = viewHolder.getView(R.id.iv_second_head);
                                            Glide.with(mContext).load(mItem.photo).error(getResources().getDrawable(R.drawable.pic_morentouxiang)).into(ivHead);
                                            // 昵称
                                            viewHolder.setText(R.id.tv_team_nickname, mItem.nickname);
                                            // 加入时间
                                            viewHolder.setText(R.id.tv_team_join_time, mItem.joinTime);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvTeam.setNestedScrollingEnabled(false);
                                rvTeam.setLayoutManager(mLayoutManager);
                                rvTeam.setAdapter(mAdapter);
                                mAdapter.setOnItemClick(MySecondTeamActivity.this);
                            }
                        } else {

                        }
                        mAdapter.notifyDataSetChanged();
                        rvTeam.refreshComplete();
                    }
                });
    }

    @Override
    public void itemClick(View v, int position) {
        // 暂无点击事件
    }
}
