package com.lb.zbrj.controller;

import java.util.ArrayList;
import java.util.Calendar;

import com.lb.common.util.Constants;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.db.PersonHelper;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class GetPersonInfoThread extends Thread {

	String mobile;
	boolean force;
	
	public GetPersonInfoThread(){
		this.mobile = AccountData.getInstance().getBindphonenumber();
		this.force = false;
	}
	
	public GetPersonInfoThread(String mobile){
		this.mobile = mobile;
		this.force = false;
	}
	
	public GetPersonInfoThread(String mobile, boolean force){
		this.mobile = mobile;
		this.force = force;
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		try{
			PersonHelper helper = new PersonHelper(AccountData.getInstance().getUsername());
			PersonData person = helper.find(mobile);
			long syntime = person == null ? 0 : person.timestamp;
			if(force || syntime == 0 || Calendar.getInstance().getTimeInMillis() - syntime >= 24 * 60 * 60 * 1000){
				NetIF_ZBRJ net = new NetIF_ZBRJ(MyApplication.getInstance());
				NetInterfaceStatusDataStruct result = net.m1_get_personalInfo(mobile);
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					PersonData p = (PersonData)result.getObj();
					p.timestamp = Calendar.getInstance().getTimeInMillis();
					p.fanstimestamp = p.timestamp;
					p.oldFansNum = person == null ? p.fansNum : person.fansNum;
					p.oldFocusNum = person == null ? p.focusNum : person.focusNum;
					if(!AccountData.getInstance().getBindphonenumber().equals(mobile)){//本人不需要备注姓名
						result = net.m1_get_nick(mobile);
						if(Constants.RES_SUCCESS.equals(result.getStatus())
								&& result.getObj() != null
								&& result.getObj() instanceof String){
							p.memoName = (String)result.getObj();
						}
					}
					helper.insert(p);
					ArrayList<SynPersonInfoListener> listeners = new ArrayList<SynPersonInfoListener>();
					listeners.addAll(MyApplication.getInstance().getListeners(Constants.LISTENER_SYN_PERSONINFO));
					for(SynPersonInfoListener listener:listeners){
						if(listener != null)listener.syn(p);
					}
					new PersonController().updPerson(mobile, p);
				}
			}
		}catch(Exception e){
			
		}
	}
}