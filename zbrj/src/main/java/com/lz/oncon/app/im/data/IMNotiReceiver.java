package com.lz.oncon.app.im.data;

import com.lz.oncon.api.SIXmppAccout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class IMNotiReceiver extends BroadcastReceiver {
	
	public static final String ONCON_IM_HEARTBEAT = "ONCON_IM_HEARTBEAT";
	
	Context mContext;
	
	public void onReceive(Context context, Intent intent) {  
		mContext = context;
		String action = intent.getAction();
        
        if(ONCON_IM_HEARTBEAT.equals(action)){
        	SIXmppAccout account = ImCore.getInstance().getAccout();
        	if(TextUtils.isEmpty(account.getUsername())){
        		ImCore.getInstance().setAccout();
        	}
        	IMNotification.getInstance();
        	ImData.getInstance();
        	IMMessageWriteData.getInstance();
//        	ImCore.getInstance().getConnection().startHeartBeat(account);
        }
    }
	
}