package com.lz.oncon.app.im.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lb.common.util.DateTimePickDialogUtil;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppP2PInfo;
import com.lz.oncon.api.SIXmppMessage.ContentType;
import com.lz.oncon.api.core.im.data.IMDataDB;
import com.lz.oncon.app.im.data.IMThreadData.Type;
import com.lz.oncon.app.im.data.ImData.OnDataChangeListener;
import com.lz.oncon.app.im.ui.IMMessageListActivity;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.receiver.OnNotiReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;



public class IMNotification {

	private NotificationManager nm = null;
	
	private static IMNotification instance = null;
	private static long preTime;
	private static long currentTime;
	SIXmppMessage firstMessage;
	private PersonController mPersonController;
	public static IMNotification getInstance(){
		if(instance==null){
			instance = new IMNotification();
		}
		return instance;
	}
	private IMNotification() {		
		nm = (NotificationManager) MyApplication.getInstance().getSystemService(
				Context.NOTIFICATION_SERVICE);
		mOnconidFilters = new ArrayList<String>();
		mNewMessageHashMap = Collections.synchronizedMap(new HashMap<String, Integer>());
		mNewMessageWhileFilterHashMap = Collections.synchronizedMap(new HashMap<String, Integer>());
		mPersonController = new PersonController();
	}
	
	private synchronized void showNotification(String roster,SIXmppMessage message){
		if(roster==null || message==null){
			return;
		}
		for(String onconid:mOnconidFilters){
			if(onconid.equals(roster)){
				return;
			}
		}
		if(nm!=null){
			String text = IMUtil.getMessageBrief(message, mPersonController);
			String st = "[" + MyApplication.getInstance().getString(R.string.dyn_exp) + "]";
		    if(message.getContentType() == ContentType.TYPE_TEXT)text = text.replaceAll("\\[.{2,3}\\]", st);
	        Intent i = null;
	        SIXmppP2PInfo p2pInfo = null;
			i = new Intent(MyApplication.getInstance(),IMMessageListActivity.class);
//			Map<String, IMThreadData> datas = ImData.getInstance().getDatas();
//			if(datas!=null){
				i.putExtra("data", roster);
//			}
			
			PendingIntent contentIntent = PendingIntent.getActivity(MyApplication.getInstance(), R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
			String nickname = roster;
			IMThreadData threadData = null;
			if(mPersonController.isFriend(roster)){
				threadData = ImData.getInstance().getDatas().get(roster);
			}else{
				threadData = ImData.getInstance().getSDatas().get(roster);
			}
			if(threadData!=null){
				if(threadData.getType() == Type.P2P){
					nickname = threadData.getNickName();
					if(TextUtils.isEmpty(threadData.getNickName()) || threadData.getId().equals(threadData.getNickName())){
						nickname = mPersonController.findNameByMobile(threadData.getId());
					}
					p2pInfo = ImData.getInstance().p2p_query(roster);
				}
			}
			preTime = currentTime;
			currentTime = System.currentTimeMillis();
			Notification n = new Notification();
			n.icon = R.drawable.logo;
			n.tickerText = text;
			n.when = System.currentTimeMillis();
			String messagecome = nickname+" "+"来消息啦";
			if(currentTime - preTime > 5 * 1000){
				if(p2pInfo != null && "1".equals(p2pInfo.getTone()) && "1".equals(message.getOnconActive())){
					if(!(MyApplication.getInstance().mPreferencesMan.isOpenDisturbModel()&&DateTimePickDialogUtil.isLimitSound())){
						n.defaults=Notification.DEFAULT_SOUND;
					}
				}
			}
			n.setLatestEventInfo(MyApplication.getInstance(), messagecome, text, contentIntent);
			n.audioStreamType= android.media.AudioManager.ADJUST_LOWER;
			if(p2pInfo != null && "1".equals(p2pInfo.getPush())){
				nm.notify(R.string.app_name, n);
			}
		}
	}
	
	public void cancelNotification(){
		if(nm!=null){
			nm.cancel(R.string.app_name);
		}
	}

	public void clear(){
		cancelNotification();
		if(mNewMessageHashMap!=null){
			mNewMessageHashMap.clear();
		}
		if(mNewMessageWhileFilterHashMap != null){
			mNewMessageWhileFilterHashMap.clear();
		}
		clearOnconidFilter();
	}
	
	private Map<String, Integer> mNewMessageHashMap;
	private Map<String, Integer> mNewMessageWhileFilterHashMap;

	public void addNewMessageNotifaction(String roster, SIXmppMessage message) {
		for (String onconid : mOnconidFilters) {
			if (onconid.equals(roster)) {
				ImCore.getInstance().getConnection().sendReadMessage(roster, message.getId());
				if(mNewMessageWhileFilterHashMap.containsKey(roster)){
					mNewMessageWhileFilterHashMap.put(roster, mNewMessageWhileFilterHashMap.get(roster) + 1);
				}else{
					mNewMessageWhileFilterHashMap.put(roster, 0);
				}
				return;
			}
		}
		//FIXME 计算提醒值
		if(mNewMessageHashMap!=null){
			if(mNewMessageHashMap.containsKey(roster)){
				int count = mNewMessageHashMap.get(roster) + 1;
				mNewMessageHashMap.put(roster, count);
			}else{
				mNewMessageHashMap.put(roster, 1);
			}
		}
		showNotification(roster, message);
	}
	
	public void updNewMsgCount(String onconid, int count){
		if(mNewMessageHashMap!=null){
			mNewMessageHashMap.put(onconid, count);
			IMNotification.getInstance().cancelNotification();
		}
	}
	
	public void removeNewMessageNotication(String roster){
		if(mNewMessageHashMap!=null && mNewMessageHashMap.containsKey(roster)){
			if(mNewMessageHashMap.get(roster) > 0)reportOpAction(roster);
			mNewMessageHashMap.remove(roster);
			//广播通知刷新tab的未读消息
			Intent intent = new Intent(OnNotiReceiver.ONCON_IM_RECVNEWMSG);
			MyApplication.getInstance().sendBroadcast(intent);
			try{
				if(ImData.getInstance().getOnDataChangeListener() != null && ImData.getInstance().getOnDataChangeListener().size() > 0){
					for(OnDataChangeListener onDataChangeListener: ImData.getInstance().getOnDataChangeListener()){
						onDataChangeListener.onDataChanged(roster);
					}
				}
			}catch(Exception e){}
		}
	}
	
	private void reportOpAction(final String roster){
		new Thread(){
			public void run(){
				try{
					IMDataDB.getInstance().updateRecvMsgView(roster);
				}catch(Exception e){}
			}
		}.start();
	}
	
	public boolean isNewMessageNotication(String roster){
		if(mNewMessageHashMap!=null && mNewMessageHashMap.containsKey(roster)){
			return true;
		}
		return false; 
	}
	public int getNewMessageNoticationCount(String roster){
		if(mNewMessageHashMap!=null){
			if(mNewMessageHashMap.containsKey(roster)){
				return mNewMessageHashMap.get(roster);
			}
		}
		return 0;
	}
	
	public int getAllNewMessageNoticationCount(){
		int count = 0;
		if(mNewMessageHashMap!=null){
			try{
				List<String> tempList = new ArrayList<String>();
				tempList.addAll(mNewMessageHashMap.keySet());
				for(int i=0;i<tempList.size();i++){
					String tempKey = tempList.get(i);
					Integer tempValue = mNewMessageHashMap.get(tempKey);
					if(tempValue!=null){
						count += tempValue;
					}
				}
			}catch(Exception e){}
		}
		return count;
	}
	
	
	//屏蔽某些好友的通知，一般用在正在聊天中的好友，没有必要再通知
	private ArrayList<String> mOnconidFilters;
	public void addOnconidFilter(String onconid){
		mOnconidFilters.add(onconid);
		mNewMessageWhileFilterHashMap.put(onconid, 0);
	}
	public void removeOnconidFilter(String onconid){
		mOnconidFilters.remove(onconid);
		if(mNewMessageWhileFilterHashMap.containsKey(onconid) && mNewMessageWhileFilterHashMap.get(onconid) > 0)reportOpAction(onconid);
		mNewMessageWhileFilterHashMap.remove(onconid);
	}
	public void clearOnconidFilter(){
		mOnconidFilters.clear();
	}
	public void setOnlyOnconFilter(String onconid){
		clearOnconidFilter();
		addOnconidFilter(onconid);
	}
	
	//系统级别的通知
	public void addSystemNotification(String message){
		if(message==null){
			return;
		}
		if(nm!=null){			
			Notification n = new Notification();
			n.icon = R.drawable.logo;
			n.tickerText = message;
			n.when = System.currentTimeMillis();
			if(!(MyApplication.getInstance().mPreferencesMan.isOpenDisturbModel()&&DateTimePickDialogUtil.isLimitSound())){
				n.defaults=Notification.DEFAULT_SOUND;
			}
			n.audioStreamType= android.media.AudioManager.ADJUST_LOWER;
	        n.flags = Notification.FLAG_AUTO_CANCEL;
			PendingIntent contentIntent = PendingIntent.getActivity(MyApplication.getInstance(), R.string.app_name, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
			n.setLatestEventInfo(MyApplication.getInstance(), "系统通知", message, contentIntent);
			nm.notify(R.string.app_name, n);			
		}
	}
}