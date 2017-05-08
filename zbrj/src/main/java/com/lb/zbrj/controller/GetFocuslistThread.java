package com.lb.zbrj.controller;

import java.util.ArrayList;
import java.util.Calendar;

import com.lb.common.util.Constants;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class GetFocuslistThread extends Thread {

	@SuppressWarnings("unchecked")
	public void run(){
		try{
			long syntime = MyApplication.getInstance().mPreferencesMan.getSynFocusTime();
			if(syntime == 0 || Calendar.getInstance().getTimeInMillis() - syntime >= 24 * 60 * 60 * 1000){
				NetIF_ZBRJ net = new NetIF_ZBRJ(MyApplication.getInstance());
				NetInterfaceStatusDataStruct result = net.m1_get_focus(AccountData.getInstance().getBindphonenumber(), -1, 0);
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					new PersonController().delSynFocus((ArrayList<FansData>)result.getObj());
				}
				MyApplication.getInstance().mPreferencesMan.setSynFocusTime(Calendar.getInstance().getTimeInMillis());
			}
			
		}catch(Exception e){
			
		}
	}
}