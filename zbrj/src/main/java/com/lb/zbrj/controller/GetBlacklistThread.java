package com.lb.zbrj.controller;

import java.util.ArrayList;
import java.util.Calendar;

import com.lb.common.util.Constants;
import com.lb.zbrj.data.db.BlackHelper;
import com.lb.zbrj.listener.BlackListener;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class GetBlacklistThread extends Thread {

	@SuppressWarnings("unchecked")
	public void run(){
		try{
			long syntime = MyApplication.getInstance().mPreferencesMan.getSynBlackTime();
			if(syntime == 0 || Calendar.getInstance().getTimeInMillis() - syntime >= 24 * 60 * 60 * 1000){
				NetIF_ZBRJ net = new NetIF_ZBRJ(MyApplication.getInstance());
				NetInterfaceStatusDataStruct result = net.m1_get_blacklist();
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					ArrayList<String> blacklist = (ArrayList<String>)result.getObj();
					BlackHelper helper = new BlackHelper(AccountData.getInstance().getUsername());
					helper.delAll();
					if(blacklist != null){
						helper.insert(blacklist);
					}
					ArrayList<BlackListener> listeners = new ArrayList<BlackListener>();
					listeners.addAll(MyApplication.getInstance().getListeners(Constants.LISTENER_BLACK));
					for(BlackListener listener:listeners){
						if(listener != null)listener.syn(blacklist);
					}
					MyApplication.getInstance().mPreferencesMan.setSynBlackTime(Calendar.getInstance().getTimeInMillis());
				}
			}
		}catch(Exception e){
			
		}
	}
}