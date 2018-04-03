package com.lechuang.shengxinyoupin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lechuang.shengxinyoupin.BuildConfig;


/**
 * 用来存储主页头部数据(除底部商品外)
 *
 * @author lgh
 * @version 1.3.2
 * @since 2016-01-25
 */
public class SharePreHome {
	public static final String 			CACHE_HOME 		= "home_new_data";
	public static final String 			HOME_BANNER_DATA= "home_banner_data";	// 首页轮播图数据
	public static final String 			HOME_KIND_DATA 	= "home_kind_data";		// 首页类目数据（女装 ...）
	public static final String 			HOME_AUTO_DATA 	= "home_auto_data";		// 首页滚动文字数据
	public static final String 			HOME_COLUMN_DATA= "home_column_data";	// 首页栏目数据
	public static final String 			IS_FIRST_OPEN 	= "firstOpen";			// 是否首次打开应用
	private SharedPreferences.Editor 	edit 			= null;
	private static SharePreHome 		instance;
	private SharedPreferences 			sp 				= null;
	private Context 					context;

	public SharePreHome(Context context) {
		this(context,CACHE_HOME + "_" + BuildConfig.VERSION_CODE);
	}

	/**
	 * Create SharedPreferences by filename
	 *
	 * @param context
	 * @param filename
	 */
	private SharePreHome(Context context, String filename) {
		this(context, context.getSharedPreferences(filename, Context.MODE_PRIVATE));
	}

	/**
	 * Create SharedPreferences by SharedPreferences
	 *
	 * @param context
	 * @param sp
	 */
	private SharePreHome(Context context, SharedPreferences sp) {
		this.context = context;
		this.sp = sp;
		edit = sp.edit();
	}

	public static SharePreHome getHomeInstance(Context context) {
		if (instance == null) {
			instance = new SharePreHome(context);
		}
		return instance;
	}

	// ====================/Set/====================

	// Boolean
	public SharePreHome setValue(String key, boolean value) {
		edit.putBoolean(key, value);
		edit.commit();
		return SharePreHome.this;
	}

	public SharePreHome setValue(int resKey, boolean value) {
		setValue(this.context.getString(resKey), value);
		return SharePreHome.this;
	}

	// Float
	public SharePreHome setValue(String key, float value) {
		edit.putFloat(key, value).commit();
		return SharePreHome.this;
	}

	public SharePreHome setValue(int resKey, float value) {
		setValue(this.context.getString(resKey), value);
		return SharePreHome.this;
	}

	// Integer
	public SharePreHome setValue(String key, int value) {
		edit.putInt(key, value).commit();
		return SharePreHome.this;
	}

	public SharePreHome setValue(int resKey, int value) {
		setValue(this.context.getString(resKey), value);
		return SharePreHome.this;
	}

	// Long
	public SharePreHome setValue(String key, long value) {
		edit.putLong(key, value).commit();
		return SharePreHome.this;
	}

	public SharePreHome setValue(int resKey, long value) {
		setValue(this.context.getString(resKey), value);
		return SharePreHome.this;
	}

	// String
	public SharePreHome setValue(String key, String value) {
		edit.putString(key, value).commit();
		return SharePreHome.this;
	}

	public SharePreHome setValue(int resKey, String value) {
		setValue(this.context.getString(resKey), value);
		return SharePreHome.this;
	}

	// ====================/Get/====================

	// Boolean
	public boolean getValue(String key, boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}

	public boolean getValue(int resKey, boolean defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// Float
	public float getValue(String key, float defaultValue) {
		return sp.getFloat(key, defaultValue);
	}

	public float getValue(int resKey, float defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// Integer
	public int getValue(String key, int defaultValue) {
		return sp.getInt(key, defaultValue);
	}

	public int getValue(int resKey, int defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// Long
	public long getValue(String key, long defaultValue) {
		return sp.getLong(key, defaultValue);
	}

	public long getValue(int resKey, long defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// String
	public String getValue(String key, String defaultValue) {
		return sp.getString(key, defaultValue);
	}

	public String getValue(int resKey, String defaultValue) {
		return getValue(this.context.getString(resKey), defaultValue);
	}

	// Delete
	public void remove(String key) {
		edit.remove(key).commit();
	}

	public void clear() {
		sp.edit().clear().commit();
	}


}
