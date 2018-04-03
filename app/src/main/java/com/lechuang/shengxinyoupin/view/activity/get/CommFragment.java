package com.lechuang.shengxinyoupin.view.activity.get;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ajguan.library.EasyRefreshLayout;
import com.alibaba.fastjson.JSON;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.base.LazyBaseFragment;
import com.lechuang.shengxinyoupin.model.LeCommon;
import com.lechuang.shengxinyoupin.model.bean.GetBean;
import com.lechuang.shengxinyoupin.view.activity.home.ProductDetailsActivity;
import com.lechuang.shengxinyoupin.view.activity.own.ApplyAgentActivity;
import com.lechuang.shengxinyoupin.view.activity.recyclerView.GetInfoAdapter;
import com.lechuang.shengxinyoupin.view.activity.ui.LoginActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/27.
 */

public abstract class CommFragment extends LazyBaseFragment implements OnKeyShareCallback {
    private View v;
    protected RecyclerView mRecyclerView;
    protected EasyRefreshLayout mRefreshLayout;
    //    protected ImageView mTopBannerView;
//    protected boolean mViewCreated = false;
    protected boolean mFirstLoadData = true;
    //    protected boolean mRefreshSuccess = true;//刷新成功的标记
    protected int page = 1;
    protected static final int PAGE_COUNT = 10;//每页显示的item数目
    protected int rootId;//当前页面的分类id
    protected List<GetBean.ListInfo> productList = new ArrayList<>();
    protected GetInfoAdapter infoAdapter;

    public CommFragment() {

    }

    @Override
    public void initView(View root) {
//        mTopBannerView = new ImageView(getActivity());
//        mTopBannerView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        mTopBannerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().scaledDensity*120+0.5f)));

        mRefreshLayout = (EasyRefreshLayout) root.findViewById(R.id.easyRefreshLayout);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        infoAdapter = new GetInfoAdapter(getActivity());
        mRecyclerView.setAdapter(infoAdapter);
        infoAdapter.setOnKeyShareCallback(this);
        infoAdapter.setNewData(productList);
//        infoAdapter.removeHeaderView(mTopBannerView);
//        infoAdapter.addHeaderView(mTopBannerView);
    }


    @Override
    public void show(GetBean.ListInfo listInfo, int position) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isLogin = sp.getBoolean(LeCommon.KEY_HAS_LOGIN, false);
        if (!isLogin) {
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), 0, null);
        } else {
            int isAgency = sp.getInt(LeCommon.KEY_AGENCY_STATUS, 0);
            if (isAgency == 0) {
                startActivity(new Intent(getActivity(), ApplyAgentActivity.class));
            } else {
                startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                        .putExtra(Constants.listInfo, JSON.toJSONString(listInfo)));
            }
        }
    }

}
