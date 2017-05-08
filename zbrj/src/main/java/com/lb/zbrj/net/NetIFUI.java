/**
 * 网络交互接口
 */
package com.lb.zbrj.net;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import com.lb.common.util.Log;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lz.oncon.widget.InfoProgressDialog;

public class NetIFUI implements  OnCancelListener {		

	private Context context;
	protected NetIF netIF;
	protected int lable;
	protected static final int REGISTER  = 1;
	protected static final int LOGIN =2;
	protected InfoProgressDialog dialog;
	protected boolean showDialog = true;
	public boolean isShowDialog() {
		return showDialog;
	}

	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}
	
	public void setShowDialog(boolean showDialog,int lable) {
		this.showDialog = showDialog;
		this.lable = lable;
	}

	public NetIFUI(Context context,NetInterfaceListener listener) {
		this.context = context;
		this.mNetInterfaceListener = listener;
		dialog = new InfoProgressDialog(context,R.style.NormalProgressDialog);
		dialog.setMessage(context.getString(R.string.loading));		
		dialog.setOnCancelListener(this);
	}
	
	
	
	public void showProgressDialog(final HandleInterface handleInterface){
		try{
			if(handleInterface==null){
				return;
			}
			if(lable==REGISTER){
				dialog.setMessage(context.getResources().getText(R.string.tip_register_loading));
				lable  = 0;
			}else if(lable ==LOGIN){
				dialog.setMessage(context.getResources().getText(R.string.tip_login_loading));
				lable = 0;
			}
			if(showDialog)
				dialog.show();
			new Thread(new Runnable() {
				public void run() {
					NetInterfaceStatusDataStruct status = handleInterface.run();
					if(status!=null){
						if(mNetInterfaceListener!=null){
							mNetInterfaceListener.finish(status);
						}
					}else{
						status = new NetInterfaceStatusDataStruct();
						status.setStatus(Constants.RES_FAIL);
						if(mNetInterfaceListener!=null){
							mNetInterfaceListener.finish(status);
						}
					}
					if(showDialog)
						dialog.dismiss();
				}
			}).start();
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public interface HandleInterface{
		public NetInterfaceStatusDataStruct run();
	}
	
	public void onCancel(DialogInterface dialog) {
		if(netIF!=null){
			netIF.close();
		}
		if(mOnCancelListener!=null){
			mOnCancelListener.onCancel();
		}
	}
	
	public interface NetInterfaceListener{
		public void finish(NetInterfaceStatusDataStruct niStatusData);
	}
	protected NetInterfaceListener mNetInterfaceListener;
	
	public interface OnCancelListener{
		public void onCancel();		
	}
	protected OnCancelListener mOnCancelListener;
	public void setOnCancelListener(OnCancelListener listener){
		this.mOnCancelListener = listener;
	}

	public void setCancelable(boolean flag){
		dialog.setCancelable(flag);
	}
	
	public void setMessage(String msg){
		dialog.setMessage(msg);
	}
}