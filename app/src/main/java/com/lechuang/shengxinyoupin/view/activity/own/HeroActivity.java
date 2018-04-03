package com.lechuang.shengxinyoupin.view.activity.own;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.model.bean.HeroBean;
import com.lechuang.shengxinyoupin.presenter.net.Netword;
import com.lechuang.shengxinyoupin.presenter.net.ResultBack;
import com.lechuang.shengxinyoupin.presenter.net.netApi.OwnApi;
import com.lechuang.shengxinyoupin.view.defineView.XCRoundImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/24 10:21
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class HeroActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv_team)
    ListView lvTeam;
    @BindView(R.id.common_nothing_data)
    RelativeLayout nothingData;

    public List<HeroBean.ListBean> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_heroes);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void initView() {
        tvTitle.setText("英雄榜");
    }

    @OnClick({R.id.iv_back, R.id.common_nothing_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.common_nothing_data:
                getData();
                break;
        }

    }

    public void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .heroAgentInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HeroBean>(this) {
                    @Override
                    public void successed(HeroBean result) {
                        list = result.list;
                        if (null != list && list.toString().equals("[]")) {
                            nothingData.setVisibility(View.VISIBLE);
                        } else {
                            nothingData.setVisibility(View.GONE);
                        }
                        lvTeam.setAdapter(new Myadapter(list));
                    }
                });

    }
    public class  Myadapter extends BaseAdapter{
        List<HeroBean.ListBean> list;
        public  Myadapter(List list){
            this.list=list;
        }

        @Override
        public int getCount() {
            return list==null?0:list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder myViewHolder;
            if(convertView==null){
                myViewHolder=new MyViewHolder();
                convertView=View.inflate(HeroActivity.this,R.layout.hero_item,null);
                myViewHolder.iv= (XCRoundImageView) convertView.findViewById(R.id.iv_img);
                myViewHolder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
                myViewHolder.iv_design= (ImageView) convertView.findViewById(R.id.iv_design);
                myViewHolder.tv_money= (TextView) convertView.findViewById(R.id.tv_money);
                myViewHolder.tv_time= (TextView) convertView.findViewById(R.id.tv_design);
                convertView.setTag(myViewHolder);
            }else {
                myViewHolder= (MyViewHolder) convertView.getTag();
            }
            HeroBean.ListBean listBean = list.get(position);
            if (listBean.photo != null && !listBean.photo.equals("")) {
                Glide.with(HeroActivity.this).load(listBean.photo).error(R.drawable.pic_morentouxiang).into(myViewHolder.iv);  //头像
            }
            if(position==0){
                myViewHolder.iv_design.setVisibility(View.VISIBLE);
                myViewHolder.iv_design.setImageResource(R.drawable.jin);
                myViewHolder.tv_time.setVisibility(View.GONE);
            }else if(position==1) {
                myViewHolder.iv_design.setVisibility(View.VISIBLE);
                myViewHolder.iv_design.setImageResource(R.drawable.yin);
                myViewHolder.tv_time.setVisibility(View.GONE);
            }else if(position==2) {
                myViewHolder.iv_design.setVisibility(View.VISIBLE);
                myViewHolder.iv_design.setImageResource(R.drawable.tong);
                myViewHolder.tv_time.setVisibility(View.GONE);
            }else {
                myViewHolder.iv_design.setVisibility(View.GONE);
                myViewHolder.tv_time.setVisibility(View.VISIBLE);
                myViewHolder.tv_time.setText(position+1+"");
            }
            myViewHolder.tv_money.setText(listBean.sumIntegralStr+"元");
            myViewHolder.tv_name.setText(listBean.nickName);
            return convertView;
        }
    }

    private class MyViewHolder {
        public XCRoundImageView iv;
        public TextView tv_name;
        public ImageView iv_design;
        public TextView tv_money;
        public TextView tv_time;
    }
}
