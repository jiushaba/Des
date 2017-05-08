/**
*
*  ProjectName: android50UI
*  ClassName:NormalProgressDialog
*  Description: 
*  Author: Steven
*  Date:2013-12-6 上午9:44:03
*
*/
package com.lz.oncon.widget;


import com.xuanbo.xuan.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InfoProgressDialog extends Dialog {

	/**
	 * @param context
	 */
	RelativeLayout root;
	Context ctx;
	TextView title;
	public InfoProgressDialog(Context context) {
		this(context,R.style.NormalProgressDialog);
	}
	
	public InfoProgressDialog(Context context, int theme) {
		super(context, theme);
		ctx = context;
		init();
	}
	
	public void init(){
		root = (RelativeLayout)LayoutInflater.from(ctx).inflate(R.layout.normalprogressdialog, null);
		title = (TextView) root.findViewById(R.id.pdtitle);
		setContentView(root);
		Display display = this.getWindow().getWindowManager().getDefaultDisplay();
		this.getWindow().setLayout((int)(display.getWidth()*0.5), WindowManager.LayoutParams.WRAP_CONTENT);
//		this.getWindow().setLayout((int)(display.getWidth()*0.5), (int)((display.getHeight())*0.3));
	} 
	
	public void setMessage(String msg){
		title.setText(msg);
	}
	
	public void setMessage(CharSequence msg){
		title.setText(msg.toString());
	}
	
	public void setMessage(int rid){
		title.setText(rid);
	}	
}