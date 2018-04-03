package com.lechuang.shengxinyoupin.base;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lechuang.shengxinyoupin.R;
import com.lechuang.shengxinyoupin.utils.SharedPreferencesUtils;
import com.lechuang.shengxinyoupin.view.activity.home.SearchResultActivity;
import com.lechuang.shengxinyoupin.view.defineView.DialogAlertView;

/**
 * Activity的父类，一个比较简单是Activity
 * Date 2018/3/19.
 */
public abstract class AbsActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        checkClip();
    }

    private void checkClip() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm == null) {
            return;
        }
        // 获取剪贴板的剪贴数据集
        ClipData clipData = cm.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            // 从数据集中获取（粘贴）第一条文本数据
            CharSequence text = clipData.getItemAt(0).getText();
            if (text != null && text.length() > 0) {
                String s = text.toString();
                if (s.equals(SharedPreferencesUtils.getClipBoardText())) {
                    return;
                }
                showSouTip(s);
                SharedPreferencesUtils.setClipBoardText(s);
            }
        }
    }

    //提示搜索内容
    private void showSouTip(final String text) {
        final DialogAlertView dialog = new DialogAlertView(this, R.style.CustomDialog);
        dialog.setView(R.layout.dialog_unlogin);
        dialog.show();
        ((TextView) dialog.findViewById(R.id.txt_notice)).setText("是否要搜索\"" + text + "\"");
        ((TextView) (dialog.findViewById(R.id.txt_exit))).setText("确定");
        dialog.findViewById(R.id.txt_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(text, 1);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 开始搜索
     *
     * @param search
     */
    private void search(String search, int whereSearch) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        if (whereSearch == 0) {
            intent.putExtra("content", search);
            setResult(1, intent);
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
}
