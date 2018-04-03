package com.lechuang.shengxinyoupin.view.defineView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lechuang.shengxinyoupin.R;

/**
 * @author: LGH
 * @since: 2018/1/10
 * @describe:
 */

public class PopIntegralRemind {

    private PopupWindow popupWindow;
    private final View view;

    public PopIntegralRemind(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.pop_integral_remind, null, false);
        RelativeLayout popAll = (RelativeLayout) view.findViewById(R.id.pop_integral_remind);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_integral_close);

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAsDropDown(view);

        popAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
}
