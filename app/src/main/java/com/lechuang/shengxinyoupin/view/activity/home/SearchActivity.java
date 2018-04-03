package com.lechuang.shengxinyoupin.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.base.BaseActivity;
import com.lechuang.shengxinyoupin.base.Constants;
import com.lechuang.shengxinyoupin.utils.SearchHistoryBean;
import com.lechuang.shengxinyoupin.utils.SearchHistoryUtils;
import com.lechuang.shengxinyoupin.utils.Utils;
import com.lechuang.shengxinyoupin.view.defineView.ClearEditText;
import com.lechuang.shengxinyoupin.view.defineView.FlowLayout;

import java.util.ArrayList;

/**
 * @author yrj
 * @date 2017/9/29
 * @E-mail 1422947831@qq.com
 * @desc 搜索界面
 */
public class SearchActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private ClearEditText etProduct;  //搜索框
    private TextView tvSearch;   //搜索
    private ImageView iv_shanchu;  //删除历史
    //保存搜索历史的sp
    private SharedPreferences sp;
    private Context mContext = SearchActivity.this;
    //搜索历史
    private SearchHistoryUtils historyUtils;
    private ArrayList<SearchHistoryBean> list;
    private Intent intent;
    private int whereSearch;
    private FlowLayout flowLayout;
    private String search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        //最大数量10条
        historyUtils = new SearchHistoryUtils(mContext, 20, sp);
        initEvent();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        flowLayout = (FlowLayout) findViewById(R.id.flow_layout);
        etProduct = (ClearEditText) findViewById(R.id.et_product);
        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.iv_shanchu).setOnClickListener(this);
        etProduct.setOnEditorActionListener(SearchActivity.this);
    }

    @Override
    protected void initData() {

    }


    private void initEvent() {
        intent = new Intent(mContext, AbsApplication.getInstance().getSearchResultActivity());
        whereSearch = getIntent().getIntExtra("whereSearch", 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        list = historyUtils.sortHistory();
        if (list != null && list.size() > 0) {
            // 循环添加TextView到容器
            for (int i = 0; i < list.size(); i++) {
                final TextView view = new TextView(this);
                view.setText(list.get(i).history);
                view.setMaxLines(1);
                view.setEllipsize(TextUtils.TruncateAt.END);
                view.setTextColor(getResources().getColor(R.color.rgb_646464));
                view.setPadding(5, 10, 5, 10);
                view.setGravity(Gravity.CENTER);
                view.setTextSize(14);

                // 设置点击事件
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search(view.getText().toString());
                    }
                });

                // 设置彩色背景
                /*GradientDrawable normalDrawable = new GradientDrawable();
                normalDrawable.setShape(GradientDrawable.RECTANGLE);
                normalDrawable.setColor(getResources().getColor(R.color.rgb_E6E6E6));*/
                Drawable normalDrawable = getResources().getDrawable(R.drawable.bg_search_biaoqian);

                // 设置按下的灰色背景

                Drawable pressedDrawable = getResources().getDrawable(R.drawable.bg_search_biaoqian);

                // 背景选择器
                StateListDrawable stateDrawable = new StateListDrawable();
                stateDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
                stateDrawable.addState(new int[]{}, normalDrawable);

                // 设置背景选择器到TextView上
                view.setBackground(stateDrawable);

                flowLayout.addView(view);
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        flowLayout.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:   //搜索
                //搜索内容
                search = etProduct.getText().toString().trim();
                if (search == null || search.isEmpty()) {
                    Utils.show(mContext, "搜索内容不能为空");
                    return;
                }
                //判断是否包含Emoji表情
                if (Utils.containsEmoji(search)) {
                    Utils.show(mContext, mContext.getResources().getString(R.string.no_emoji));
                    return;
                }
                historyUtils.save(search);
                search(search);
                break;
            case R.id.iv_shanchu:  //清除历史
                //清除sp里面所有数据
                historyUtils.clear();
                //清除list数据
                list.clear();
                flowLayout.removeAllViews();
                break;
            default:
                break;
        }
    }

    /**
     * 开始搜索
     *
     * @param search
     */
    private void search(String search) {
        if (whereSearch == 0) {
            intent = new Intent();
            intent.putExtra("content", etProduct.getText().toString().trim());
            this.setResult(1, intent);
        } else {
            //传递一个值,搜索结果页面用来判断是从分类还是搜索跳过去的 1:分类 2:搜索界面
            intent.putExtra("type", 2);
            //rootName传递过去显示在搜索框上
            intent.putExtra("rootName", search);
            //rootId传递过去入参
            intent.putExtra("rootId", search);
            startActivity(intent);

        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //以下方法防止两次发送请求
        if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_UP:
                    //搜索内容
                    search = etProduct.getText().toString().trim();
                    if (search == null || search.isEmpty()) {
                        Utils.show(mContext, "搜索内容不能为空");
                        return true;
                    }
                    //判断是否包含Emoji表情
                    if (Utils.containsEmoji(search)) {
                        Utils.show(mContext, mContext.getResources().getString(R.string.no_emoji));
                        return true;
                    }
                    historyUtils.save(search);
                    search(search);
                    return true;
                default:
                    return true;
            }
        }

        return false;
    }
    /**
     * 沉浸式状态栏
     */
   /* private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }*/
}
