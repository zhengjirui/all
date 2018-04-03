package com.lechuang.shengxinyoupin.view.activity.tipoff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.model.LocalSession;
import com.lechuang.shengxinyoupin.model.bean.TipoffDetail;
import com.lechuang.shengxinyoupin.model.bean.TipoffListBean;
import com.lechuang.shengxinyoupin.presenter.CommonAdapter;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.TipoffShowApi;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.activity.home.ProductDetailsActivity;
import com.lechuang.shengxinyoupin.view.defineView.MListView;
import com.lechuang.shengxinyoupin.view.defineView.ViewHolder;
import com.lechuang.shengxinyoupin.view.defineView.XCRoundImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/9/22 15:17
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class StoryDetailActivity extends AppCompatActivity {
    @BindView(R.id.iv_author_head)
    XCRoundImageView ivAuthorHead;
    @BindView(R.id.tv_author_name)
    TextView tvAuthorName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_see_number)
    TextView tvSeeNumber;
    @BindView(R.id.iv_dianzan)
    ImageView ivDianzan;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.tv_number_dianzan)
    TextView tvNumberDianzan;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.seeAll)
    TextView seeAll;
    @BindView(R.id.lv_comment)
    MListView lvComment;
    @BindView(R.id.tv_user_head)
    XCRoundImageView tvUserHead;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.iv_product)
    ImageView iv_product;
    @BindView(R.id.mScrollView)
    PullToRefreshScrollView mScrollView;
    @BindView(R.id.tv_title_story)
    TextView tvTitleStory;
    @BindView(R.id.wv_content)
    WebView wvContent;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerlayout;   //侧滑菜单
    @BindView(R.id.ll_chouti)
    LinearLayout llChouti;       //侧滑布局
    @BindView(R.id.lv_chouti)
    MListView lvChouti;          //侧滑的listview


    private boolean flag = true;
    private LocalSession mSession;
    private TipoffDetail.TipOffBean tipOff;
    //private List<TipoffDetail.ProductListBean> product;
    private Context mContext = this;
    private String id;
    private List<TipoffListBean.AppraiseListBean> appraiseList;
    private TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout_story_detail);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("精彩爆料");
        ButterKnife.bind(this);
        mSession = LocalSession.get(this);
        mScrollView.setOnRefreshListener(refresh);
        initView();
        getData();

    }

    private void initView() {
        //去除侧滑菜单出来时的阴影
        //drawerlayout.setScrimColor(Color.TRANSPARENT);
        //侧滑点击监听
        drawerlayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //设置ture点击侧滑菜单内容区域不会响应
                drawerView.setClickable(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        iv_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.openDrawer(llChouti);
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.closeDrawer(llChouti);
            }
        });
    }

    @OnClick({R.id.iv_dianzan, R.id.seeAll, R.id.tv_send, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /*case R.id.ll_good: //商品详情

            case R.id.iv_cart: //去购买
            case R.id.tv_gobuy://去购买
                startActivity(new Intent(this, ProductDetailsActivity.class)
                        .putExtra("t", 2)
                        .putExtra(Constants.listInfo, JSON.toJSONString(product)));
                break;*/
            case R.id.iv_dianzan://点赞// ;
                changeVeiw();
                tipHelp(id, 1);
                break;
            case R.id.seeAll://查看全部
                //传入一个id入参,whichComment作用判断哪个界面的评论详情 1:爆料 2:晒单
                startActivity(new Intent(mContext, TipOffCommentDetailsActivity.class).putExtra("tipOffId", tipOff.id).putExtra("whichComment", 1));
                break;
            case R.id.tv_send://发送评论
                if (Utils.isEmpty(etContent)) {
                    Utils.show(this, "请输入内容");
                    return;
                }
                //不能包含emoji表情
                if (Utils.containsEmoji(etContent.getText().toString())) {
                    Utils.show(this, this.getResources().getString(R.string.no_emoji));
                    return;
                }
                //添加评论
                submit();
                break;
            case R.id.iv_back://返回
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * 发表评论
     */
    private void submit() {

        String id = getIntent().getStringExtra("id");
        String content = etContent.getText().toString().trim();
        Netword.getInstance().getApi(TipoffShowApi.class)
                .sendContent(id, 1, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(StoryDetailActivity.this, result);
                        etContent.setText("");
                        getCommentDate();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCommentDate();
    }

    /**
     * 改变显示状态
     *
     * @describe
     */
    private void changeVeiw() {

        if (ivDianzan.isSelected()) {
            ivDianzan.setSelected(false);
            tipOff.praiseCount = tipOff.praiseCount - 1;
            tvNumberDianzan.setText(tipOff.praiseCount + "次赞");
        } else {
            ivDianzan.setSelected(true);
            tipOff.praiseCount = tipOff.praiseCount + 1;
            tvNumberDianzan.setText(tipOff.praiseCount + "次赞");
        }
    }


    //访问网络
    public void getData() {
        id = getIntent().getStringExtra("id");
        Netword.getInstance().getApi(TipoffShowApi.class)
                .getTipoffDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TipoffDetail>(this) {
                    @Override
                    public void successed(TipoffDetail data) {
                        tipOff = data.tipOff;
                        mScrollView.setMode(PullToRefreshBase.Mode.BOTH);
                        if (data.status == 0) {//表示一点赞
                            flag = false;
                            ivDianzan.setSelected(true);
                        } else {//没点赞
                            flag = true;
                            ivDianzan.setSelected(false);
                        }
                        //有头像设置头像
                        String photo = data.tipOff.photo;
                        if (photo != null && !photo.equals("")) {
                            Glide.with(AbsApplication.getInstance()).load(photo).into((ivAuthorHead));//作者头像
                        }
                        webData();
                        if (mSession.getImge() != null && !mSession.getImge().equals("")) {
                            Glide.with(StoryDetailActivity.this).load(mSession.getImge()).into(tvUserHead);//用户头像
                        }
                        tvTime.setText(tipOff.createTimeStr);//时间
                        tvNumberDianzan.setText(tipOff.praiseCount + "次赞");//点赞
                        tvTitleStory.setText(tipOff.title);//文章标题
                        tvAuthorName.setText(tipOff.nickName); //作者名
                        tvSeeNumber.setText(tipOff.pageViews + "");//浏览量
                        mScrollView.onRefreshComplete();
                        getCommentDate();
                        //侧滑栏数据
                        getDrawerData(data.productList);
                    }
                });

    }

    //侧滑栏数据
    private void getDrawerData(final List<TipoffDetail.ProductListBean> product) {
        if (product != null) {

            lvChouti.setAdapter(new CommonAdapter<TipoffDetail.ProductListBean>(mContext, product, R.layout.item_story_detail_product) {
                @Override
                public void setData(ViewHolder viewHolder, Object item) {

                    viewHolder.displayImage(R.id.iv_img, ((TipoffDetail.ProductListBean) item).imgs);
                    viewHolder.setText(R.id.tv_title, ((TipoffDetail.ProductListBean) item).name);
                    viewHolder.setText(R.id.tv_price, "¥" + ((TipoffDetail.ProductListBean) item).preferentialPrice);
                    if (((TipoffDetail.ProductListBean) item).couponMoney != null) {
                        viewHolder.getView(R.id.ll_quan).setVisibility(View.VISIBLE);
                        viewHolder.setText(R.id.couponMoney, ((TipoffDetail.ProductListBean) item).couponMoney);
                        viewHolder.setText(R.id.tv_soujia, "券后价");
                    } else {
                        viewHolder.getView(R.id.ll_quan).setVisibility(View.GONE);
                        viewHolder.setText(R.id.tv_soujia, "售价");
                    }
                }
            });

            lvChouti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    startActivity(new Intent(mContext, ProductDetailsActivity.class)
                            .putExtra("t", 2)
                            .putExtra(Constants.listInfo, JSON.toJSONString(product.get(position))));

                }
            });
        }
    }

    /**
     * 中间段加载数据
     */
    private void webData() {
        //记载网页
        WebSettings webSettings = wvContent.getSettings();
        // 让WebView能够执行javaScript


        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(true);//support zoom
        //自适应屏幕
        webSettings.setTextSize(WebSettings.TextSize.LARGEST);
        webSettings.setUseWideViewPort(true);// 这个很关键
        webSettings.setLoadWithOverviewMode(true);

        //加载HTML字符串进行显示
        wvContent.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
        wvContent.loadData(tipOff.textBoxContent, "text/html; charset=UTF-8", null);
        // 设置WebView的客户端
        wvContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;// 返回false
            }
        });
    }

    /**
     * 获取评论状态
     */
    public void getCommentDate() {
        String id = getIntent().getStringExtra("id");
        Netword.getInstance().getApi(TipoffShowApi.class)
                .getTipoffList(id, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<TipoffListBean>(this) {
                    @Override
                    public void successed(TipoffListBean result) {
                        appraiseList = result.appraiseList;


                        lvComment.setAdapter(new CommonAdapter<TipoffListBean.AppraiseListBean>(mContext, result.appraiseList, R.layout.tipoff_comment_item) {
                            @Override
                            public void setData(ViewHolder viewHolder, Object item) {
                                final TipoffListBean.AppraiseListBean tipAppraise = (TipoffListBean.AppraiseListBean) item;
                                if (tipAppraise.photo != null && !tipAppraise.photo.equals("")) {
                                    viewHolder.displayImage(R.id.iv_img, tipAppraise.photo);
                                }
                                viewHolder.setText(R.id.tv_nickname, tipAppraise.nickName);
                                viewHolder.setText(R.id.tv_time, tipAppraise.createTimeStr);
                                viewHolder.setText(R.id.tv_details, tipAppraise.content);
                            }
                        });
                    }
                });
    }

    //评论点赞
    private void tipHelp(String tipId, int index) {
        Netword.getInstance().getApi(TipoffShowApi.class)
                .tipPraise(tipId, index)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(mContext, result);
                    }
                });
    }

    private PullToRefreshBase.OnRefreshListener2 refresh = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            String label = DateUtils.formatDateTime(
                    getApplicationContext(),
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            // 显示最后更新的时间
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            // 模拟加载任务
            getData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            // 显示最后更新的时间
            String label = DateUtils.formatDateTime(
                    getApplicationContext(),
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            Utils.show(mContext, "亲,已经到底了!");
            mScrollView.onRefreshComplete();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断侧滑栏是否开启  如果开启关闭
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerlayout.isDrawerOpen(llChouti)) {
                drawerlayout.closeDrawer(llChouti);
            } else {
                finish();
            }
        }
        return true;
    }
}
