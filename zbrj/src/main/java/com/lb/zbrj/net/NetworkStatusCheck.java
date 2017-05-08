package com.lb.zbrj.net;

import com.lz.oncon.application.MyApplication;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
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
	
	public boolean checkNetWorkAvliable(){
		boolean success = false; 
		if(connManager == null)
			connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connManager != null){
			NetworkInfo[] nis = connManager.getAllNetworkInfo();
			for(NetworkInfo ni:nis){
				if(ni.getState() == State.CONNECTED){
					return true;
				}
			}
		} 
		return success;
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
	
	public static String getNetType(){
		String netType = "";
		ConnectivityManager connectMgr = (ConnectivityManager)MyApplication.getInstance()
		        .getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if(info != null){
			if(ConnectivityManager.TYPE_WIFI == info.getType()){
				netType = "WIFI";
			}else if(ConnectivityManager.TYPE_MOBILE == info.getType()){
				if(TelephonyManager.NETWORK_TYPE_CDMA == info.getSubtype()
						|| TelephonyManager.NETWORK_TYPE_GPRS == info.getSubtype()
						|| TelephonyManager.NETWORK_TYPE_EDGE == info.getSubtype()){
					netType = "2G";
				} else if(TelephonyManager.NETWORK_TYPE_LTE == info.getSubtype()){
					netType = "4G";
				} else{
					netType = "3G";
				}
			}
		}
		return netType;
	}
	
	public static boolean isWifiOpen(){
		WifiManager wifiManager = (WifiManager) MyApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}
}
