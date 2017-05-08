package com.lz.oncon.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 接收提示后操作
 * @author luoqun
 *
 */
public class OnNotiReceiver extends BroadcastReceiver {
	
	public static final String ONCON_NOTI_DIALOG = "ONCON_NOTI_DIALOG";
	public static final String ONCON_FRIEND_CHANGED = "ONCON_FRIEND_CHANGED";
	public static final String ONCON_MYAPP_CHANGEED = "ONCON_MYAPP_CHANGEED";
	public static final String ONCON_IM_RECVNEWMSG = "ONCON_IM_RECVNEWMSG";
	public static final String ONCON_DOWNLOADING_FILE = "ONCON_DOWNLOADING_FILE";
	public static final String ONCON_DOWNLOADED_FILE = "ONCON_DOWNLOADED_FILE";
	public static final String ONCON_MYENTER_CHANGEED = "ONCON_MYENTER_CHANGEED";
	public static final String ONCON_MYATTENTION_CHANGEED = "ONCON_MYATTENTION_CHANGEED";
	public static final String ONCON_ADDSYSCONTACT_FAIL = "ONCON_ADDSYSCONTACT_FAIL";
	public static final String ONCON_MYCOMMANY_CHANGEED = "ONCON_MYCOMMANY_CHANGEED";
	
	Context mContext;
	
	public void onReceive(Context context, Intent intent) {  
		mContext = context;
		String action = intent.getAction();
        
        if(ONCON_NOTI_DIALOG.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_NOTI_DIALOG) != null && mNotiListeners.get(ONCON_NOTI_DIALOG).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_NOTI_DIALOG)){
        			mNotiListener.finishNoti(action);
        		}
    		}
        }else if(ONCON_FRIEND_CHANGED.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_FRIEND_CHANGED) != null && mNotiListeners.get(ONCON_FRIEND_CHANGED).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_FRIEND_CHANGED)){
        			mNotiListener.finishNoti(action);
        		}
    		}
        }else if(ONCON_MYAPP_CHANGEED.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_MYAPP_CHANGEED) != null && mNotiListeners.get(ONCON_MYAPP_CHANGEED).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_MYAPP_CHANGEED)){
        			mNotiListener.finishNoti(action);
        		}
    		}
        }else if(ONCON_IM_RECVNEWMSG.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_IM_RECVNEWMSG) != null && mNotiListeners.get(ONCON_IM_RECVNEWMSG).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_IM_RECVNEWMSG)){
        			mNotiListener.finishNoti(action);
        		}
    		}
        }else if(ONCON_DOWNLOADING_FILE.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_DOWNLOADING_FILE) != null && mNotiListeners.get(ONCON_DOWNLOADING_FILE).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_DOWNLOADING_FILE)){
        			String filePath = intent.getStringExtra("filePath");
        			int fileSize = intent.getIntExtra("fileSize", 0);
        			long downLoadSize = intent.getLongExtra("downLoadSize", 0);
        			mNotiListener.finishNoti(action + "|" + filePath + "|" + fileSize + "|" + downLoadSize);
        		}
    		}
        }else if(ONCON_DOWNLOADED_FILE.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_DOWNLOADED_FILE) != null && mNotiListeners.get(ONCON_DOWNLOADED_FILE).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_DOWNLOADED_FILE)){
        			String filePath = intent.getStringExtra("filePath");
        			mNotiListener.finishNoti(action + "|" + filePath);
        		}
    		}
        }else if(ONCON_MYENTER_CHANGEED.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_MYENTER_CHANGEED) != null && mNotiListeners.get(ONCON_MYENTER_CHANGEED).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_MYENTER_CHANGEED)){
        			mNotiListener.finishNoti(action);
        		}
    		}
        }else if(ONCON_MYATTENTION_CHANGEED.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_MYATTENTION_CHANGEED) != null && mNotiListeners.get(ONCON_MYATTENTION_CHANGEED).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_MYATTENTION_CHANGEED)){
        			mNotiListener.finishNoti(action);
        		}
    		}
        }else if(ONCON_ADDSYSCONTACT_FAIL.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_ADDSYSCONTACT_FAIL) != null && mNotiListeners.get(ONCON_ADDSYSCONTACT_FAIL).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_ADDSYSCONTACT_FAIL)){
        			mNotiListener.finishNoti(action);
        		}
    		}
        }else if(ONCON_MYCOMMANY_CHANGEED.equals(action)){
        	if(mNotiListeners != null && mNotiListeners.get(ONCON_MYCOMMANY_CHANGEED) != null && mNotiListeners.get(ONCON_MYCOMMANY_CHANGEED).size() > 0){
        		for(NotiListener mNotiListener: mNotiListeners.get(ONCON_MYCOMMANY_CHANGEED)){
        			mNotiListener.finishNoti(action);
        		}
    		}
        }
    }
	
	private Map<String, List<NotiListener>> mNotiListeners;
	
	public void addNotiListener(String action, NotiListener notiListener){
		if(mNotiListeners == null){
			mNotiListeners = new HashMap<String, List<NotiListener>>();
		}
		if(mNotiListeners.get(action) == null){
			mNotiListeners.put(action, new ArrayList<NotiListener>());
		}
		mNotiListeners.get(action).add(notiListener);
	}

	public interface NotiListener{
		public void finishNoti(String action);
	}
}