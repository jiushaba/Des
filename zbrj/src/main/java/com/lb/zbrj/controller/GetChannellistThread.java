package com.lb.zbrj.controller;

import java.util.ArrayList;
import java.util.Calendar;

import com.lb.common.util.Constants;
import com.lb.zbrj.data.ChannelData;
import com.lb.zbrj.data.db.ChannelHelper;
import com.lb.zbrj.listener.ChannelListener;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class GetChannellistThread extends Thread {

	@SuppressWarnings("unchecked")
	public void run(){
		try{
			long syntime = MyApplication.getInstance().mPreferencesMan.getSynChannelTime();
			if(syntime == 0 || Calendar.getInstance().getTimeInMillis() - syntime >= 24 * 60 * 60 * 1000){
				NetIF_ZBRJ net = new NetIF_ZBRJ(MyApplication.getInstance());
				NetInterfaceStatusDataStruct result = net.m1_get_channels();
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					ArrayList<ChannelData> channels = (ArrayList<ChannelData>)result.getObj();
					ChannelHelper helper = new ChannelHelper(AccountData.getInstance().getUsername());
					if(channels == null || channels.size() == 0){
						helper.delAll();
					}else{
						for(ChannelData channel:channels){
							//FIXME 暂时过滤0，服务器有问题0这个id 不更新
							if(channel.id == 0)
								continue;
							switch(channel.optType){
							case 0://新增
							case 1://修改
								helper.insert(channel);
								break;
							case 2://删除
								helper.del(channel);
								break;
							}
						}
					}
					ArrayList<ChannelListener> listeners = new ArrayList<ChannelListener>();
					listeners.addAll(MyApplication.getInstance().getListeners(Constants.LISTENER_CHANNEL));
					for(ChannelListener listener:listeners){
						if(listener != null)listener.syn(channels);
					}
					MyApplication.getInstance().mPreferencesMan.setSynChannelTime(Calendar.getInstance().getTimeInMillis());
				}
			}
		}catch(Exception e){
			
		}
	}
}