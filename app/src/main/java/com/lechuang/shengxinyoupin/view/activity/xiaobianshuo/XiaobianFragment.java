package com.lechuang.shengxinyoupin.view.activity.xiaobianshuo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.BaseFragment;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.shengxinyoupin.mine.adapter.ViewHolderRecycler;
import com.lechuang.shengxinyoupin.mine.view.XRecyclerView;
import com.lechuang.shengxinyoupin.model.bean.AllProductBean;
import com.lechuang.shengxinyoupin.model.bean.DianzanBean;
import com.lechuang.shengxinyoupin.model.bean.ProductDetailsBean;
import com.lechuang.shengxinyoupin.model.bean.XiaobianshuoBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.HomeApi;
import com.lechuang.shengxinyoupin.presenter.net.NetwordUtil;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.home.ProductDetailsActivity;
import com.lechuang.shengxinyoupin.view.activity.home.ProductShareActivity;
import com.lechuang.shengxinyoupin.view.defineView.OriginalImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by SMJ
 * Date 2018/3/20.
 */
public class XiaobianFragment extends BaseFragment {
    //商品列表
    private XRecyclerView rvProduct;
    //没有网络状态
    private View ll_noNet;
    //分页页数
    private int page = 1;
    private LinearLayoutManager mLayoutManager;
    private View ivTop;
    //商品集合
    private List<XiaobianshuoBean.ProductBean> mProductList = new ArrayList<>();
    private CommonRecyclerAdapter mAdapter;
    private ImageView headImage;

    private OriginalImageView originalImageView;
    /**
     * 是否显示了昨天的数据
     */
    private boolean isShowYestady;
    /**
     * 今天的数据的数量
     */
    private int todayCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_xiaobianshuo, container, false);
        originalImageView = v.findViewById(R.id.original_image);
        originalImageView.setVisibility(View.GONE);
        originalImageView.setOnClickListener(v1 -> v1.setVisibility(View.GONE));
        //没有网络时的默认图片
        ll_noNet = v.findViewById(R.id.ll_noNet);
        //刷新重试
        v.findViewById(R.id.iv_tryAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(true);
            }
        });
        rvProduct = v.findViewById(R.id.gv_product);
        ivTop = v.findViewById(R.id.iv_top);
        ivTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvProduct.scrollToPosition(0);
            }
        });

        rvProduct.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getData(true);
            }

            @Override
            public void onLoadMore() {
//                getData(false);
            }
        });
        rvProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastVisibleItemPosition() > 8) {
                    ivTop.setVisibility(View.VISIBLE);
                } else {
                    ivTop.setVisibility(View.GONE);
                }
            }
        });
        mAdapter = new CommonRecyclerAdapter<XiaobianshuoBean.ProductBean>(getActivity(), R.layout.fragment_xiaobianshuo_item, mProductList) {
            @Override
            public int getItemViewType(int position) {
                if (isShowYestady) {
                    return 0;
                }
                if (position == todayCount) {
                    return 1;
                }
                return 0;
            }

            @Override
            public int getItemCount() {
                return isShowYestady ? super.getItemCount() : todayCount + 1;
            }

            @Override
            public ViewHolderRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == 0) {
                    return super.onCreateViewHolder(parent, viewType);
                } else {
                    return ViewHolderRecycler.get(getActivity(), parent, R.layout.fragment_xiaobianshuo_footer);
                }
            }

            @Override
            public void onBindViewHolder(ViewHolderRecycler holder, int position) {
                if (getItemViewType(position) == 0) {
                    super.onBindViewHolder(holder, position);
                } else {
                    Utils.print("列表中，设置了查看昨天数据的按钮");
                    holder.getView(R.id.check_yestday).setOnClickListener(checkYestdayListener);
                }
            }

            @Override
            public void convert(ViewHolderRecycler holder, XiaobianshuoBean.ProductBean bean) {
                if (TextUtils.isEmpty(bean.img)) {
                    holder.setImageResource(R.id.user_portrait, R.drawable.pic_morentouxiang);
                } else {
                    holder.displayImage(R.id.user_portrait, bean.img, R.drawable.pic_morentouxiang);
                }
                holder.setText(R.id.user_name, bean.name);
                holder.setText(R.id.create_time, bean.createTimeStr);
                holder.setText(R.id.product_wenan, bean.productText);
                holder.setText(R.id.product_title, bean.productName);

                holder.setText(R.id.now_price, "￥" + bean.price);

                if (Utils.isZero(bean.couponPrice)) {
                    holder.setText(R.id.couponMoney, "");
                } else {
                    holder.setText(R.id.couponMoney, bean.couponPrice + "元");
                }
                int[] imageres = {R.id.image1, R.id.image2, R.id.image3, R.id.image4,
                        R.id.image5, R.id.image6, R.id.image7, R.id.image8, R.id.image9};
                ConstraintLayout.LayoutParams cl = (ConstraintLayout.LayoutParams) holder.getView(R.id.kuang).getLayoutParams();
                int imageCount = bean.imgList == null ? 0 : bean.imgList.size();
                ImageView image_only = holder.getView(R.id.image_only);
                if (imageCount > 1) {
                    cl.topToBottom = R.id.image7;
                    image_only.setVisibility(View.GONE);
                    for (int i = 0; i < imageres.length; i++) {
                        int res = imageres[i];
                        ImageView iv = holder.getView(res);
                        if (i < imageCount) {
                            iv.setVisibility(View.VISIBLE);
                            if (!iv.hasOnClickListeners()) {
                                iv.setOnClickListener(onClickImage);
                            }
                            iv.setTag(R.integer.tag_id_1, bean.imgList.get(i).imgUrl);
                            holder.displayImage(iv, bean.imgList.get(i).imgUrl, R.drawable.loading_square);
                        } else if (i % 3 == 0) {
                            iv.setVisibility(View.GONE);
                        } else {
                            iv.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {

                    for (int res : imageres) {
                        holder.getView(res).setVisibility(View.GONE);
                    }
                    if (imageCount == 1) {
                        cl.topToBottom = R.id.image_only;
                        image_only.setVisibility(View.VISIBLE);
                        if (!image_only.hasOnClickListeners()) {
                            image_only.setOnClickListener(onClickImage);
                        }
                        image_only.setTag(R.integer.tag_id_1, bean.imgList.get(0).imgUrl);
//                        holder.displayImage(image_only, bean.imgList.get(0).imgUrl, R.drawable.loading_square);
                        Glide.with(AbsApplication.getInstance()).load(bean.imgList.get(0).imgUrl).asBitmap().placeholder(R.drawable.loading_square)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        ConstraintLayout.LayoutParams cl = (ConstraintLayout.LayoutParams) image_only.getLayoutParams();
                                        cl.dimensionRatio = resource.getWidth() + ":" + resource.getHeight();
                                        image_only.requestLayout();
                                        image_only.setImageBitmap(resource);
                                    }
                                });

                    } else {
                        cl.topToBottom = R.id.product_wenan;
                        image_only.setVisibility(View.GONE);
                        //没有图片，一般不会进来
                    }
                }

                View lingquan = holder.getView(R.id.lingquan);
                lingquan.setTag(bean);
                lingquan.setOnClickListener(lingquanListener);

                View fenxiang = holder.getView(R.id.fenxiang);
                fenxiang.setTag(bean);
                fenxiang.setOnClickListener(fenxiangListener);

                View dianzan = holder.getView(R.id.dianzan);
                dianzan.setSelected(bean.thumb != null);
                dianzan.setTag(bean);
                dianzan.setOnClickListener(dianzanListener);

                holder.setText(R.id.dianzan_number, bean.number + "");
            }


            View.OnClickListener onClickImage = v1 -> {
                ImageView iv = (ImageView) v1;
                String uri = (String) iv.getTag(R.integer.tag_id_1);
                int index = uri.lastIndexOf("?");
                if (index != -1) {
                    uri = uri.substring(0, index);
                }
                Glide.with(AbsApplication.getInstance()).load(uri).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        originalImageView.setBitmap(resource);
                    }
                });

            };
            View.OnClickListener checkYestdayListener=v1 -> {
                isShowYestady = true;
                mAdapter.notifyDataSetChanged();
            };
        };

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setSmoothScrollbarEnabled(true);

//        rvProduct.addItemDecoration(new GridItemDecoration(
//                new GridItemDecoration.Builder(getActivity())
//                        .margin(4, 4)
//                        .horSize(16)
//                        .verSize(16)
//                        .showLastDivider(true)
//        ));

        View head = inflater.inflate(R.layout.fragment_xiaobianshuo_head, container, false);
        headImage = head.findViewById(R.id.head_image);
        rvProduct.setNestedScrollingEnabled(false);
        rvProduct.setLayoutManager(mLayoutManager);
        rvProduct.addHeaderView(head);
        rvProduct.setAdapter(mAdapter);

        getData(true);
        return v;
    }

    View.OnClickListener lingquanListener = v -> {
        XiaobianshuoBean.ProductBean bean = (XiaobianshuoBean.ProductBean) v.getTag();
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("productId", bean.alipayItemId);
        paramMap.put("id", bean.id);
        paramMap.put("type", "4");
        NetwordUtil.queryProductDetails(paramMap, new ResultBack<ProductDetailsBean>(getActivity()) {
            @Override
            public void successed(ProductDetailsBean result) {
                startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                        .putExtra(Constants.listInfo, JSON.toJSONString(result.productWithBLOBs)));
            }
        });
    };
    View.OnClickListener fenxiangListener = v -> {
        XiaobianshuoBean.ProductBean bean = (XiaobianshuoBean.ProductBean) v.getTag();
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("productId", bean.alipayItemId);
        paramMap.put("id", bean.id);
        paramMap.put("type", "4");
        NetwordUtil.queryProductDetails(paramMap, new ResultBack<ProductDetailsBean>(getActivity()) {
            @Override
            public void successed(ProductDetailsBean result) {
                AllProductBean.ListInfo b = JSON.parseObject(JSON.toJSONString(result.productWithBLOBs), AllProductBean.ListInfo.class);
                startActivity(new Intent(getActivity(), ProductShareActivity.class).putExtra(Constants.listInfo, b));
            }
        });
    };
    /**
     * 点赞操作
     */
    View.OnClickListener dianzanListener = v -> {
        final XiaobianshuoBean.ProductBean bean = (XiaobianshuoBean.ProductBean) v.getTag();
        int type;
        if (v.isSelected()) {
            bean.thumb = null;
            v.setSelected(false);
            type = 0;
        } else {
            bean.thumb = 0;
            v.setSelected(true);
            type = 1;
        }
        Netword.getInstance().getApi(HomeApi.class)
                .xiaobianshuoDianzan(bean.id, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<DianzanBean>(getActivity()) {
                    @Override
                    public void successed(DianzanBean result) {
                        TextView dianzan_number = ((View) v.getParent()).findViewById(R.id.dianzan_number);
                        bean.number = result.number;
                        dianzan_number.setText(result.number + "");
                    }
                });
    };

    protected void getData(boolean isRefresh) {
//        mTime = System.currentTimeMillis();
        if (Utils.isNetworkAvailable(getActivity())) {
            //网络畅通 隐藏无网络状态
            ll_noNet.setVisibility(View.GONE);
            if (isRefresh) {
                page = 1;
            } else {
                page++;
            }
            Netword.getInstance().getApi(HomeApi.class)
                    .xiaobianshuo(page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<XiaobianshuoBean>(getActivity()) {
                        @Override
                        public void successed(XiaobianshuoBean result) {
                            setData(result);
                        }

                        @Override
                        protected void error(int errorCode, String s) {
                            if (page > 1) {
                                page--;
                            }
                            rvProduct.refreshComplete();
                        }
                    });
        } else {
            //网络不通 展示无网络状态
            ll_noNet.setVisibility(View.VISIBLE);
            if (isRefresh) {
                mProductList.clear();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setData(XiaobianshuoBean result) {
        if (result == null) {
            return;
        }
        isShowYestady = false;
        ll_noNet.setVisibility(View.GONE);
        Glide.with(AbsApplication.getInstance()).load(result.blnImg.imgUrl).placeholder(R.drawable.rvbaner).into(headImage);
        //商品集合
        List<XiaobianshuoBean.ProductBean> list = result.todayList;
        todayCount = list.size();
        if (page > 1 && list.isEmpty()) {
            //数据没有了
//            Utils.show(mContext, "亲!已经到底了");
            rvProduct.noMoreLoading();
            return;
        }
        if (page == 1) {
            mProductList.clear();
        }
        mProductList.addAll(list);
        mProductList.addAll(result.yesterdayList);
        mAdapter.notifyDataSetChanged();
        rvProduct.refreshComplete();
    }
}
