package com.lz.oncon.api.core.im.network;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
/**
 * @检查网络状态
 * @author Administrator
 *
 */
public class NetworkStatusCheck {

	private Context mContext;
	private ConnectivityManager connManager;

	public NetworkStatusCheck(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	public boolean checkMobileNetStatus(){
		boolean success = false; 
		if(connManager == null)
			connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager != null){
			NetworkInfo ni = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if(ni != null){
				State state = ni.getState();  //获取移动网络状态
				if (State.CONNECTED == state) {  
					success = true;   
				}
			}
		} 
		return success;
	}
	
	public boolean checkWifiNetStatus(){
		boolean success = false; 
		if(connManager == null)
			connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager != null){
			NetworkInfo ni = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if(ni != null){
				State state = ni.getState();  //获取WIFI状态
				if (State.CONNECTED == state) {  
					success = true;   
				} 
			}
		}
		return success;
	}
	
	public String checkApnStatus(){
		String apntype="";
		Cursor cr = mContext.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), null, null, null, null);
		while (cr != null && cr.moveToNext()) {
			apntype = cr .getString(cr.getColumnIndex("apn"));
		}
		if (cr != null) {
			cr.close();
		}
		return apntype;
	}
	
	public void checkNetworkStatus(NetStatusChangeListener netStatusChangeListener){
		netStatusChangeListener.finish(checkMobileNetStatus(), checkWifiNetStatus(), checkApnStatus());
	}
	
	public interface NetStatusChangeListener{
		public void finish(boolean isMobileConn, boolean isWiFiConn, String apnName);
	}
	//检查是否有任何网络连接
	public static boolean isNetworkConnected(Context context) {  
	     if (context != null) {  
	         ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                 .getSystemService(Context.CONNECTIVITY_SERVICE);  
	         NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	         if (mNetworkInfo != null) {  
	             return mNetworkInfo.isAvailable();  
	         }  
	     }  
	     return false;  
	 }
}
