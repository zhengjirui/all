package com.lechuang.shengxinyoupin.view.activity.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.model.bean.ProductListBean;
import com.lechuang.shengxinyoupin.view.adapter.ProductAdapter;

import java.util.HashMap;
import java.util.List;

public class KindDetailFragmeng extends AbsKindDetailFragmeng {
    private View button9;
    private View button19;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);

        //如果是9.9包邮活19.9
        int type = getActivity().getIntent().getIntExtra("type", 0);
        if (type == 2 || type == 10) {
            //顶部盖上一个标题
            View title = inflater.inflate(R.layout.title_99, container, false);
            title.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            button9 = title.findViewById(R.id.baoyou_9);
            button19 = title.findViewById(R.id.baoyou_19);
            vg.addView(title);
            button9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!button9.isSelected()) {
                        button9.setSelected(true);
                        button19.setSelected(false);
                        getData(true);
                    }
                }
            });
            button19.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!button19.isSelected()) {
                        button19.setSelected(true);
                        button9.setSelected(false);
                        getData(true);
                    }
                }
            });
            button9.setSelected(type == 2);
            button19.setSelected(type == 10);
        }
        return vg;
    }

    @NonNull
    @Override
    protected CommonRecyclerAdapter<ProductListBean.ProductBean> initAdapter(List<ProductListBean.ProductBean> data) {
        return new ProductAdapter.GridAdapter1(getActivity(), data);
    }

    @Override
    protected void putParamBefore(HashMap<String, String> allParamMap) {
        if (button9 != null && button19 != null) {
            if (button9.isSelected()) {
                allParamMap.put("type", "2");
            } else if (button19.isSelected()) {
                allParamMap.put("type", "10");
            }
        }
    }

    @NonNull
    @Override
    protected LinearLayoutManager initLinearLayoutManager() {
        return new GridLayoutManager(getActivity(), 2);
    }
}
