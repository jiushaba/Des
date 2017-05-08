package com.lz.oncon.activity.friendcircle;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.lb.zbrj.controller.PersonController;

public class HeadImageListener implements OnClickListener {
	private String name;
	private Context mc;
	private String mobile;

	public HeadImageListener(Context c, String name, String mobile) {
		this.mc = c;
		this.name = name;
		this.mobile = mobile;
	}

	@Override
	public void onClick(View v) {
		PersonController.go2Detail(mc, mobile);
	}

}
