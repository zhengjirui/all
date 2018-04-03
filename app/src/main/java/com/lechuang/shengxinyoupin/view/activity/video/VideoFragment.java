package com.lechuang.shengxinyoupin.view.activity.video;

import android.view.View;

import com.lechuang.shengxinyoupin.base.BaseFragment;
import com.lechuang.shengxinyoupin.mine.adapter.OnItemClick;

/**
 * @author yrj
 * @date 2017/10/3
 * @E-mail 1422947831@qq.com
 * @desc 栏目详情
 */
public class VideoFragment extends BaseFragment implements View.OnClickListener, OnItemClick {
    @Override
    public void onClick(View v) {

    }

    @Override
    public void itemClick(View v, int position) {

    }
//
//    private XRecyclerView rvVideo;
//    private CommonRecyclerAdapter mAdapter;
//    private RelativeLayout commonLoading;
//    //没有网络状态
//    private LinearLayout ll_noNet;
//    //刷新重试按钮
//    private ImageView iv_tryAgain;
//    //分页页数
//    private int page = 1;
//    private View view;
//    private ImageView ivTop;
//    private GridLayoutManager mLayoutManager;
//    private List<VideoDataBean.ProductListBean>  mVideoList = new ArrayList<>();
//
//    private long mTime;
//    private boolean isBottom = false;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_video, container, false);
//
//        initView();
//        getData();
//        return view;
//    }
//
//
//    private void initView() {
//        rvVideo = (XRecyclerView) view.findViewById(R.id.rv_video);
//        //没有网络时的默认图片
//        ll_noNet = (LinearLayout) view.findViewById(R.id.ll_noNet);
//        commonLoading = (RelativeLayout) view.findViewById(R.id.common_loading_all);
//        //刷新重试
//        iv_tryAgain = (ImageView) view.findViewById(R.id.iv_tryAgain);
//        iv_tryAgain.setOnClickListener(this);
//        ivTop = (ImageView) view.findViewById(R.id.iv_top);
//        ivTop.setOnClickListener(this);
//
//        rvVideo.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                isBottom = false;
//                page = 1;
//                if (mVideoList != null)
//                    mVideoList.clear();
//                if (mAdapter != null)
//                    mAdapter.notifyDataSetChanged();
//                getData();
//
//            }
//
//            @Override
//            public void onLoadMore() {
//                page += 1;
//                getData();
//            }
//        });
//
//        rvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (mLayoutManager.findLastVisibleItemPosition() > 15) {
//                    ivTop.setVisibility(View.VISIBLE);
//                } else {
//                    ivTop.setVisibility(View.GONE);
//                }
//
//                if (mVideoList.size() - mLayoutManager.findLastVisibleItemPosition() < 5) {
//                    if (System.currentTimeMillis() - mTime > 1000 && !isBottom) {
//                        page += 1;
//                        getData();
//                    }
//                }
//            }
//        });
//        if (page == 1) {
//            if (mAdapter == null) {
//                mAdapter = new CommonRecyclerAdapter(getActivity(), R.layout.item_video_grid, mVideoList) {
//                    @Override
//                    public void convert(ViewHolderRecycler holder, Object data) {
//                        try {
//                            final VideoDataBean.ProductListBean bean = (VideoDataBean.ProductListBean) data;
//                            ImageView ivImg = holder.getView(R.id.iv_img);
//                            ImageView ivPlay = holder.getView(R.id.iv_play);
//                            //商品图
//                            Glide.with(mContext).load(bean.imgs).into(ivImg);
//                            //动态调整滑动时的内存占用
//                            Glide.get(mContext).setMemoryCategory(MemoryCategory.LOW);
//
//                            // 领券减
//                            TextView tvCoupon = holder.getView(R.id.tv_quanMoney);
//                            TextView tvBuy = holder.getView(R.id.tv_youhuiquan);
//                            if (bean.couponMoney != null) {
//                                if (Integer.parseInt(bean.couponMoney) > 0) {
//                                    tvCoupon.setVisibility(View.VISIBLE);
//                                    tvCoupon.setText("券¥" + bean.couponMoney);
//                                    tvBuy.setText("立即领取");
//                                } else {
//                                    tvCoupon.setVisibility(View.GONE);
//                                    tvBuy.setText("立即购买");
//                                }
//                            } else {
//                                tvCoupon.setVisibility(View.GONE);
//                                tvBuy.setText("立即购买");
//                            }
//                            TextView tvOldPrice = holder.getView(R.id.tv_oldprice);
//                            tvOldPrice.setText(bean.price + "");
//                            // 中划线
//                            tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
//                            // 券后价
//                            holder.setText(R.id.tv_nowprice, "¥" + bean.preferentialPrice);
//                            //商品名称
//                            holder.setSpannelTextView(R.id.spannelTextView, bean.name, Integer.parseInt(bean.shopType));
//                            //销量
//                            holder.setText(R.id.tv_xiaoliang, "月销" + bean.nowNumber);
//
//                            ivImg.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//
//                                    Intent intent = new Intent(getActivity(), PlayActivity.class);
//                                    intent.putExtra(PlayActivity.TRANSITION, true);
//                                    intent.putExtra(Extra.VIDEO_PATH, bean.videoUrl);
//                                    intent.putExtra(Extra.VIDEO_IMG, bean.imgs);
//                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                                        Pair pair = new Pair<>(view, PlayActivity.IMG_TRANSITION);
//                                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                                getActivity(), pair);
//                                        ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
//                                    } else {
//                                        getActivity().startActivity(intent);
//                                        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
//                                    }
//                                }
//                            });
//                            ivPlay.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(getActivity(), PlayActivity.class);
//                                    intent.putExtra(PlayActivity.TRANSITION, true);
//                                    intent.putExtra(Extra.VIDEO_PATH, bean.videoUrl);
//                                    intent.putExtra(Extra.VIDEO_IMG, bean.imgs);
//                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                                        Pair pair = new Pair<>(view, PlayActivity.IMG_TRANSITION);
//                                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                                getActivity(), pair);
//                                        ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
//                                    } else {
//                                        getActivity().startActivity(intent);
//                                        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
//                                    }
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//
//                mLayoutManager = new GridLayoutManager(getActivity(), 2);
//                mLayoutManager.setSmoothScrollbarEnabled(true);
//
//                rvVideo.addItemDecoration(new GridItemDecoration(
//                        new GridItemDecoration.Builder(getActivity())
//                                .margin(6, 6)
//                                .horSize(16)
//                                .verSize(16)
//                                .showLastDivider(true)
//                ));
//
//                rvVideo.setNestedScrollingEnabled(false);
//                rvVideo.setLayoutManager(mLayoutManager);
//                rvVideo.setAdapter(mAdapter);
//                mAdapter.setOnItemClick(VideoFragment.this);
//            }
//        }
//    }
//
//
//    private void getData() {
//        mTime = System.currentTimeMillis();
//        if (page == 1) {
//            commonLoading.setVisibility(View.VISIBLE);
//        }
//        Netword.getInstance().getApi(HomeApi.class)
//                .videoProduct(page)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new ResultBack<VideoDataBean>(getActivity()) {
//                    @Override
//                    public void successed(VideoDataBean result) {
//                        commonLoading.setVisibility(View.GONE);
//                        productData(result);
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        super.onCompleted();
//                        commonLoading.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        commonLoading.setVisibility(View.GONE);
//                    }
//                });
//
//    }
//
//    private void productData(VideoDataBean result) {
//        if (result == null)
//            return;
//        ll_noNet.setVisibility(View.GONE);
//
//        //商品集合
//        List<VideoDataBean.ProductListBean> list = result.productList;
//
//        if (page > 1 && list.isEmpty()) {
//            //数据没有了
////            Utils.show(mContext, "亲!已经到底了");
//            rvVideo.noMoreLoading();
//            isBottom = true;
//            return;
//        }
//
//        mVideoList.addAll(list);
//        mAdapter.notifyDataSetChanged();
//        rvVideo.refreshComplete();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_tryAgain://刷新重试
//                page = 1;
//                if (mVideoList != null)
//                    mVideoList.clear();
//                if (mAdapter != null)
//                    mAdapter.notifyDataSetChanged();
//                getData();
//                break;
//            // 回到顶部
//            case R.id.iv_top:
//                rvVideo.scrollToPosition(0);
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void itemClick(View v, int position) {
//        startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
//                .putExtra(Constants.listInfo, JSON.toJSONString(mVideoList.get(position))));
//    }
}
