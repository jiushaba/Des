package com.lb.zbrj.controller;

import java.util.ArrayList;
import java.util.Calendar;

import com.lb.common.util.Constants;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.db.FansHelper;
import com.lb.zbrj.listener.FansListener;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class GetFanslistThread extends Thread {

	@SuppressWarnings("unchecked")
	public void run(){
		try{
			FansHelper helper = new FansHelper(AccountData.getInstance().getUsername());
			long syntime = MyApplication.getInstance().mPreferencesMan.getSynFansTime();
			if(syntime == 0 || Calendar.getInstance().getTimeInMillis() - syntime >= 24 * 60 * 60 * 1000){
				NetIF_ZBRJ net = new NetIF_ZBRJ(MyApplication.getInstance());
				NetInterfaceStatusDataStruct result = net.m1_get_fans(AccountData.getInstance().getBindphonenumber(), -1, 0);
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					ArrayList<FansData> fanslist = (ArrayList<FansData>)result.getObj();
					helper.delAll();
					if(fanslist != null){
						helper.insert(fanslist);
					}
					ArrayList<FansListener> listeners = new ArrayList<FansListener>();
					listeners.addAll(MyApplication.getInstance().getListeners(Constants.LISTENER_FANS));
					for(FansListener listener:listeners){
						if(listener != null)listener.syn(fanslist);
					}
					MyApplication.getInstance().mPreferencesMan.setSynFansTime(Calendar.getInstance().getTimeInMillis());
				}
			}
		}catch(Exception e){
			
		}
	}
}