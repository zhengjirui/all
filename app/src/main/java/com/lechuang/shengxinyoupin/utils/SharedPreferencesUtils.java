package com.lechuang.shengxinyoupin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lechuang.shengxinyoupin.base.AbsApplication;

/**
 * Created by YRJ
 * Date 2018/3/15.
 */

public class SharedPreferencesUtils {
    private static SharedPreferences defaultSP;

    private static SharedPreferences lastingSP;
    /**
     * 默认的sp，注销登录后会被清空
     */
    private static SharedPreferences getDefaultSharedPreferences() {
        if (defaultSP == null) {
            defaultSP = PreferenceManager.getDefaultSharedPreferences(AbsApplication.getInstance());
        }
        return defaultSP;
    }
    /**
     * 不会被清空的sp
     */
    private static SharedPreferences getSharedPreferencesLasting() {
        if (lastingSP == null) {
            lastingSP = AbsApplication.getInstance().getSharedPreferences("lasting", Context.MODE_PRIVATE);
        }
        return lastingSP;
    }
    /**
     * 是否已经登录
     */
    public static boolean isLogined() {
        return getDefaultSharedPreferences().getBoolean("isLogin", false);
    }

    /**
     * 返回上次保存在剪贴板里面的文字
     */
    public static String getClipBoardText(){
        return getSharedPreferencesLasting().getString("clipBoardText", "");
    }

    /**
     * 保存上次保存在剪贴板里面的文字
     */
    public static void setClipBoardText(String clipBoardText) {
        getSharedPreferencesLasting().edit().putString("clipBoardText", clipBoardText).apply();
    }
}
