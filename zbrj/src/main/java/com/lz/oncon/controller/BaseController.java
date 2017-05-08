package com.lz.oncon.controller;

import android.content.Context;
import android.widget.Toast;


public abstract class BaseController {
	
	protected Context mContext;

	public BaseController(Context mContext) {
		super();
		this.mContext = mContext;
		initDatabase();
	}
	
	public abstract void initDatabase();

	public abstract void onDestroy();
	
	public void toastToMessage(int resId){
		Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
	}
	
	public void toastToMessage(String s){
		Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
	}
}
