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

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.OnItemClick;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.bean.OrderBean;
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

public class MyOrderActivity2 extends AppCompatActivity implements OnItemClick {

    @BindView(R.id.line_order_all)
    View lineAll;
    @BindView(R.id.line_order_valid)
    View lineValid;
    @BindView(R.id.line_order_invalid)
    View lineInvalid;

    @BindView(R.id.show_order_valid)
    LinearLayout showValidOrder;
    @BindView(R.id.tv_order_pay)
    TextView tvPay;
    @BindView(R.id.tv_order_send)
    TextView tvSend;
    @BindView(R.id.tv_order_end)
    TextView tvEnd;
    @BindView(R.id.common_nothing_data)
    RelativeLayout nothingData;
    @BindView(R.id.common_loading_all)
    RelativeLayout commonLoading;

    @BindView(R.id.lv_order)
    XRecyclerView rvOrder;
    @BindView(R.id.tv_oder_one)
    TextView tvOderOne;
    @BindView(R.id.tv_one_type)
    TextView tvOneType;
    @BindView(R.id.tv_order_two)
    TextView tvTwoOrder;
    @BindView(R.id.tv_two_type)
    TextView tvTwoType;
    @BindView(R.id.tv_oder_tree)
    TextView tvOderTree;
    @BindView(R.id.tv_three_type)
    TextView tvThreeType;
    @BindView(R.id.tab_order_all)
    RelativeLayout tabOrderAll;
    @BindView(R.id.tv_order)
    TextView tvOrder;

    private Context mContext = MyOrderActivity2.this;
    private boolean isFinish = false;
    public int page = 1;
    public int type = 0; // (不传返回所有订单)type  1 所有有效 2 已付款 3 已收货 4 已结算 5 已失效
    public int flag = 1;
    private List<OrderBean.OrderDetail> items = new ArrayList<>();
    private CommonRecyclerAdapter mAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order2);
        ButterKnife.bind(this);

        initView();
        getData();
        tvOneType.setSelected(true);
        tvOderOne.setSelected(true);

    }

    private void initView() {

        rvOrder = (XRecyclerView) findViewById(R.id.lv_order);

        nothingData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != items && !items.isEmpty())
                    items.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                page = 1;
                getData();
            }
        });

        rvOrder.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (null != items && !items.isEmpty())
                    items.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
            }

            @Override
            public void onLoadMore() {
                page += 1;
                getData();
            }
        });
    }

    @OnClick({R.id.iv_order_back, R.id.tv_order_pay, R.id.tv_order_send, R.id.tv_order_end,
            R.id.tab_order_all, R.id.tab_order_valid, R.id.tab_order_invalid, R.id.tv_one_type,
            R.id.tv_two_type, R.id.tv_three_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_order_back:
                isFinish = true;
                finish();
                break;
            //  所有订单
            case R.id.tab_order_all:
                type = 0;
                lineAll.setVisibility(View.VISIBLE);
                lineValid.setVisibility(View.INVISIBLE);
                lineInvalid.setVisibility(View.INVISIBLE);
                showValidOrder.setVisibility(View.GONE);
                break;
            //  有效订单
            case R.id.tab_order_valid:
                type = 1;
                lineAll.setVisibility(View.INVISIBLE);
                lineValid.setVisibility(View.VISIBLE);
                lineInvalid.setVisibility(View.INVISIBLE);
                // 显示已付款/已收货/已结算
                showValidOrder.setVisibility(View.VISIBLE);
                tvPay.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvSend.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvEnd.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvPay.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvSend.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvEnd.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                break;
            //  无效订单
            case R.id.tab_order_invalid:
                type = 5;
                lineAll.setVisibility(View.INVISIBLE);
                lineValid.setVisibility(View.INVISIBLE);
                lineInvalid.setVisibility(View.VISIBLE);
                showValidOrder.setVisibility(View.GONE);
                break;
            case R.id.tv_order_pay:
                //  已付款
                type = 2;
                tvPay.setTextColor(mContext.getResources().getColor(R.color.white));
                tvSend.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvEnd.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvPay.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_pay));
                tvSend.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvEnd.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                break;
            case R.id.tv_order_send:
                //  已收货
                type = 3;
                tvPay.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvSend.setTextColor(mContext.getResources().getColor(R.color.white));
                tvEnd.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvPay.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvSend.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_send));
                tvEnd.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                break;
            case R.id.tv_order_end:
                //  已结算
                type = 4;
                tvPay.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvSend.setTextColor(mContext.getResources().getColor(R.color.c_444444));
                tvEnd.setTextColor(mContext.getResources().getColor(R.color.white));
                tvPay.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvSend.setBackground(mContext.getResources().getDrawable(R.drawable.btn_order_type));
                tvEnd.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_end));
                break;
            case R.id.tv_one_type:
                flag = 1;
                initTextStatus();
                tvOneType.setSelected(true);
                tvOderOne.setSelected(true);
                break;
            case R.id.tv_two_type:
                flag = 2;
                initTextStatus();
                tvTwoType.setSelected(true);
                tvTwoOrder.setSelected(true);
                break;
            case R.id.tv_three_type:
                flag = 3;
                initTextStatus();
                tvThreeType.setSelected(true);
                tvOderTree.setSelected(true);
                break;
        }
        if (!isFinish) {
            page = 1;
            if (null != items && !items.isEmpty())
                items.clear();
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
            getData();
        }
    }

    private void initTextStatus() {
        tvOderOne.setSelected(false);
        tvTwoOrder.setSelected(false);
        tvOderTree.setSelected(false);
        tvThreeType.setSelected(false);
        tvTwoType.setSelected(false);
        tvOneType.setSelected(false);
        getData();
    }

    private void getData() {
        if (1 == page) {
            commonLoading.setVisibility(View.VISIBLE);
        }
        Netword.getInstance().getApi(OwnApi.class)
                .orderDetails_shen(type, page, flag)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OrderBean>(mContext) {
                    @Override
                    public void successed(OrderBean result) {
                        commonLoading.setVisibility(View.GONE);
                        if (result == null) {
                            nothingData.setVisibility(View.VISIBLE);
                            return;
                        }
                        tvOrder.setText(result.getSumIncome());
                        tvOderOne.setText(result.getOwnIncome());
                        tvTwoOrder.setText(result.getAgencyIncome());
                        tvOderTree.setText(result.getNextAgencyIncome());

                        List<OrderBean.OrderDetail> mList = result.orderList;
                        if (page == 1 && (mList.toString().equals("[]") || mList.size() <= 0)) {
                            if (null != items) {
                                items.clear();
                                nothingData.setVisibility(View.VISIBLE);
//                                rvOrder.setVisibility(View.INVISIBLE);
                            }
                            return;
                        }
                        nothingData.setVisibility(View.GONE);

                        if (items.size() > 0 && mList.toString().equals("[]")) {
                            Toast.makeText(mContext, "亲!已经到底了", Toast.LENGTH_SHORT).show();
                            mAdapter.notifyDataSetChanged();
                            rvOrder.refreshComplete();
                            return;
                        }
                        int size = mList.size();
                        for (int i = 0; i < size; i++) {
                            items.add(mList.get(i));
                        }

//                        if (1 == page) {

                        if (mAdapter == null) {
                            mAdapter = new CommonRecyclerAdapter(mContext, R.layout.item_order_details2, items) {
                                @Override
                                public void convert(ViewHolderRecycler viewHolder, Object data) {
                                    try {
                                        final OrderBean.OrderDetail mItem = (OrderBean.OrderDetail) data;
                                        // 商品图片
//                                            viewHolder.displayImage(R.id.iv_orderDetail_img, mItem.img);
//                                            // 店铺类型(1 淘宝 2天猫)
//                                            ((SpannelTextView) viewHolder.getView(R.id.stv_orderDetail_title)).setShopType(mItem.shopType == null ? 1 : Integer.parseInt(mItem.shopType));
//                                            ((SpannelTextView) viewHolder.getView(R.id.stv_orderDetail_title)).setDrawText(mItem.goodsMsg);
//                                            // 店铺名称：
//                                            viewHolder.setText(R.id.tv_orderDetail_shop, "店铺名称：" + mItem.shopName);
//                                            // 订单状态
                                        TextView tvOrderState = viewHolder.getView(R.id.tv_orderDetail_state);
                                        tvOrderState.setText(mItem.orderStatus);
                                        viewHolder.setText(R.id.tv_order_number, "订单号：" + mItem.orderNum);
                                        viewHolder.setText(R.id.tv_source, mItem.sourceText);
                                        viewHolder.setText(R.id.tv_payClearingIncome, mItem.payClearingIncome);
                                        if ("已付款".equals(mItem.orderStatus)) {
                                            // 已付款红色背景
                                            tvOrderState.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_pay));
                                        } else if ("已收货".equals(mItem.orderStatus)) {
                                            // 已付款蓝色背景
                                            tvOrderState.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_send));
                                        } else if ("已结算".equals(mItem.orderStatus)) {
                                            // 已付款绿色是背景
                                            tvOrderState.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_end));
                                        } else if ("已失效".equals(mItem.orderStatus)) {
                                            // 已付款灰色背景
                                            tvOrderState.setBackground(mContext.getResources().getDrawable(R.drawable.btn_orderstate_invalid));
                                        }
                                        // 付款金额
//                                            viewHolder.setText(R.id.tv_orderDetail_pay, "¥" + mItem.payClearingMoney);
                                        // 预估收入
//                                            viewHolder.setText(R.id.tv_orderDetail_forecast, /*"¥" +*/ mItem.payClearingIncome);
                                        // 提成
//                                            viewHolder.setText(R.id.tv_orderDetail_deduct, mItem.commisiom);
                                        // 来源
//                                            viewHolder.setText(R.id.tv_orderDetail_source, mItem.source);
                                        // 订单创建时间
                                        viewHolder.setText(R.id.tv_create_time, "下单时间：" + mItem.createTime /*+ "  创建"*/);
//                                            TextView tvClearTime = viewHolder.getView(R.id.tv_clear_time);
//                                            if ("已结算".equals(mItem.orderStatus)) {
//                                                // 订单结算时间
//                                                tvClearTime.setVisibility(View.VISIBLE);
//                                                tvClearTime.setText(mItem.clearingTime + "  结算");
//                                            } else {
//                                                tvClearTime.setVisibility(View.GONE);
//                                            }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                            mLayoutManager.setSmoothScrollbarEnabled(true);

                            rvOrder.setNestedScrollingEnabled(false);
                            rvOrder.setLayoutManager(mLayoutManager);
                            rvOrder.setAdapter(mAdapter);
                            mAdapter.setOnItemClick(MyOrderActivity2.this);
//                            }
                        } else {
                            mAdapter.notifyDataSetChanged();
                            rvOrder.refreshComplete();
                        }
                        mAdapter.notifyDataSetChanged();
                        rvOrder.refreshComplete();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        commonLoading.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void itemClick(View v, int position) {
        // 暂时不做处理
    }
}
