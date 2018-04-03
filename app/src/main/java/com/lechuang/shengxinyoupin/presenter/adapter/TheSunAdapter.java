package com.lechuang.shengxinyoupin.presenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.base.AbsApplication;
import com.lechuang.shengxinyoupin.model.LocalSession;
import com.lechuang.shengxinyoupin.model.bean.SunShowBean;
import com.lechuang.shengxinyoupin.presenter.CommonAdapter;
import com.lechuang.shengxinyoupin.view.activity.SunBigPicActivity;
import com.lechuang.shengxinyoupin.view.defineView.MGridView;
import com.lechuang.shengxinyoupin.view.defineView.RatingBar;
import com.lechuang.shengxinyoupin.view.defineView.XCRoundImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by yrj on 2017/8/31.
 * 晒单Adapter
 */


public class TheSunAdapter extends BaseAdapter {

    protected List<SunShowBean.ListBean> theSunList;
    protected ArrayList<String> imgList;
    protected Context context;
    private Map<String, ArrayList<String>> map;

    private LocalSession mSession;

    public TheSunAdapter(List<SunShowBean.ListBean> mDatas, Context context, Map<String, ArrayList<String>> map) {
        this.theSunList = mDatas;
        this.context = context;
        this.map = map;
        mSession = LocalSession.get(context);
    }


    @Override
    public int getCount() {
        return theSunList == null ? 0 : theSunList.size();
    }

    @Override
    public Object getItem(int position) {
        return theSunList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.thesun_mlv_item, null);
            //发布图片gridView
            holder.mgv_img = (MGridView) convertView.findViewById(R.id.mgv_img);
            //头像
            holder.iv_headImg = (XCRoundImageView) convertView.findViewById(R.id.iv_headImg);
            //星级
            holder.ratingbarId = (RatingBar) convertView.findViewById(R.id.ratingbarId);
            //发布人
            holder.tv_issuer = (TextView) convertView.findViewById(R.id.tv_issuer);
            //点赞数量
            holder.tv_likeNum = (TextView) convertView.findViewById(R.id.tv_likeNum);
            //评论数量
            holder.tv_commentNum = (TextView) convertView.findViewById(R.id.tv_commentNum);
            //发布内容
            holder.tv_details = (TextView) convertView.findViewById(R.id.tv_details);
            //发布时间
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
           //点赞按钮
            holder.cb_praise = (ImageView) convertView.findViewById(R.id.cb_praise);
            /* //评论按钮
            holder.line_sun_comment = (LinearLayout) convertView.findViewById(R.id.line_sun_comment);
            //分享
            holder.line_sun_share = (LinearLayout) convertView.findViewById(R.id.ll_share);*/

            //记录点击的gridview处于父ListView的position
            holder.mgv_img.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ratingbarId.setClickable(false);
        //取List对应下标的数据
        final SunShowBean.ListBean sun = theSunList.get(position);
        //图片JSONArray
        List<SunShowBean.ListBean.Img1Bean> array= sun.img1;
        //获取点击的gridview处于父ListView的position
        //final int p = (int) holder.mgv_img.getTag();
        final int p1 = position;
        //存储图片数据的集合
        imgList = new ArrayList<>();
        //解析出图片地址添加到集合
        for (int i = 0; i < array.size(); i++) {
            SunShowBean.ListBean.Img1Bean img1Bean = array.get(i);
            imgList.add(img1Bean.imgUrl);
        }
        //map集合存储每一个图片集合  key = "theSunImgList" + position
        map.put("theSunImgList" + position, imgList);
        //头像
        // ImageLoader.getInstance().displayImage(sun.photo, holder.iv_headImg);
        Glide.with(AbsApplication.getInstance()).load(sun.photo).into(holder.iv_headImg);
        //发布人
        holder.tv_issuer.setText(sun.nickName);
        //发布时间
        holder.tv_time.setText(sun.createTimeStr);
        //星级
        holder.ratingbarId.setStar(sun.starLevel);
        //评论内容
        holder.tv_details.setText(sun.content);
        //评论数量
        holder.tv_commentNum.setText(sun.appraiseCount + "");
        //点赞数量
        holder.tv_likeNum.setText(sun.praiseCount + "");
        //晒单界面暂不点赞
        //嵌套点赞
//        if (sun.status == 1) {
//            holder.cb_praise.setSelected(false);
//        } else {
//            holder.cb_praise.setSelected(true);
//        }
//        //holder.cb_praise.setChecked(true);.
//        holder.cb_praise.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.cb_praise.setClickable(false);
//                if(sun.status==0){
//                    sun.status=1;
//                    sun.praiseCount= sun.praiseCount -1;
//                    holder.tv_likeNum.setText(sun.praiseCount + "");
//                 holder.cb_praise.setSelected(false);
//
//                }else {
//                    sun.status=0;
//                    sun.praiseCount= sun.praiseCount + 1;
//                    holder.tv_likeNum.setText(sun.praiseCount+ "");
//                    holder.cb_praise.setSelected(true);
//                }
//                tipHelp(sun.id,2);
//                holder.cb_praise.setClickable(true);
//            }
//        });

        //gridView设置适配器
        holder.mgv_img.setAdapter(new CommonAdapter<String>(context, imgList, R.layout.thesun_gvitem) {
            @Override
            public void setData(com.lechuang.shengxinyoupin.view.defineView.ViewHolder viewHolder, Object item) {
                viewHolder.displayImage(R.id.iv_gv_img, (String) item);
            }
        });
        //item点击事件
        holder.mgv_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(context, SunBigPicActivity.class);
                        intent.putExtra("current", position);
                        intent.putStringArrayListExtra("list", map.get("theSunImgList" + p1));
                        context.startActivity(intent);
            }
        });

        return convertView;
    }

    //点赞
//    private void tipHelp(String tipId, int index) {
//        Netword.getInstance().getApi(TipoffShowApi.class)
//                .tipPraise(tipId, index)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new ResultBack<String>(context) {
//                    @Override
//                    public void successed(String result) {
//                        Utils.show(context,result);
//                    }
//                });
//    }


    private class ViewHolder {
        //头像
        private XCRoundImageView iv_headImg;
        //星级
        private RatingBar ratingbarId;
        //发布人,昵称
        private TextView tv_issuer;
        //发布时间
        private TextView tv_time;
        //发布内容
        private TextView tv_details;
        //发布图片GridView
        private MGridView mgv_img;
        //点赞数量
        private TextView tv_likeNum;
        //评论数量
        private TextView tv_commentNum;
        //点赞按钮
        private ImageView cb_praise;
        //评论
        private LinearLayout line_sun_comment;
        //分享
        private LinearLayout line_sun_share;
    }


}