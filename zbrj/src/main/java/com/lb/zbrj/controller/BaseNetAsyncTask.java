package com.lb.zbrj.controller;

import com.xuanbo.xuan.R;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.BaseActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public abstract class BaseNetAsyncTask extends AsyncTask<Object, Integer, NetInterfaceStatusDataStruct> {
	
	Context context;
	public NetIF_ZBRJ ni;
	
	public BaseNetAsyncTask(Context context){
		this.context = context;
		ni = new NetIF_ZBRJ(context);
	}
	
	@Override
	protected NetInterfaceStatusDataStruct doInBackground(Object... arg0) {
		return doNet();
	}

	@Override  
    protected void onPostExecute(NetInterfaceStatusDataStruct result) {
		if(context instanceof BaseActivity){
			((BaseActivity)context).hideProgressDialog();
		}
		afterNet(result);
    }
	
	@Override  
    protected void onPreExecute() {
		if(context instanceof BaseActivity){
			((BaseActivity)context).progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
				@Override
				public void onCancel(DialogInterface arg0) {
					BaseNetAsyncTask.this.cancel(true);
				}
			});
			((BaseActivity)context).showProgressDialog(R.string.wait, true);
		}
    }
	
	public abstract NetInterfaceStatusDataStruct doNet();
	
	public abstract void afterNet(NetInterfaceStatusDataStruct result);
}