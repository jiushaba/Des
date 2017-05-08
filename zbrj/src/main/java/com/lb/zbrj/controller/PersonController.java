package com.lb.zbrj.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.lb.common.util.Log;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.activity.PersonActivity;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.db.BlackHelper;
import com.lb.zbrj.data.db.FansHelper;
import com.lb.zbrj.data.db.FocusHelper;
import com.lb.zbrj.data.db.PersonHelper;
import com.lb.zbrj.listener.FocusListener;
import com.lz.oncon.api.core.im.data.IMDataDB;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.util.IMUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

public class PersonController {

	private static HashMap<String, PersonData> persons;
	private static int CAPACITY = 100;
	
	private FansHelper mFansHelper;
	private FocusHelper mFocusHelper;
	private PersonHelper mPersonHelper;
	private BlackHelper mBlackHelper;
	
	private void initDBHelper(){
		if(mFansHelper == null)mFansHelper = new FansHelper(AccountData.getInstance().getUsername());
		if(mFocusHelper == null)mFocusHelper = new FocusHelper(AccountData.getInstance().getUsername());	
	}
	
	public ArrayList<FansData> getFriends(String search_word){
		initDBHelper();
		ArrayList<FansData> friends = new ArrayList<FansData>();
		if(TextUtils.isEmpty(search_word)){
			friends.addAll(mFocusHelper.findAll());//关注即为好友
		}else{
			friends.addAll(mFocusHelper.findAll(search_word));//关注即为好友
		}
		Collections.sort(friends, new Comparator<FansData>() {

			@Override
			public int compare(FansData lhs, FansData rhs) {
				return lhs.index.compareTo(rhs.index);
			}
			
		});
		return friends;
	}
	
	public boolean isFriend(String account){
		initDBHelper();
		if(AccountData.getInstance().getBindphonenumber().equals(account)){
			return true;
		}
		return mFocusHelper.isFriend(account);
	}
	
	public static void go2Detail(Context ctx, String mobile){
		try{
			Intent intent = new Intent(ctx, PersonActivity.class);
			Bundle b = new Bundle();
			b.putString("mobile", mobile);
			intent.putExtras(b);
			ctx.startActivity(intent);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean isFocused(String mobile){
		if(mFocusHelper == null)mFocusHelper = new FocusHelper(AccountData.getInstance().getUsername());
		if(mFocusHelper.find(mobile) != null){
			return true;
		}
		return false;
	}
	
	public void addFocus(FansData fans){
		if(mFocusHelper == null)mFocusHelper = new FocusHelper(AccountData.getInstance().getUsername());
		mFocusHelper.insert(fans);
		synPersonInfo(AccountData.getInstance().getBindphonenumber(), true);
		synPersonInfo(fans.account, true);
		ImData.getInstance().moveSThread2Thread(fans.account);
		ArrayList<FocusListener> listeners = new ArrayList<FocusListener>();
		listeners.addAll(MyApplication.getInstance().getListeners(Constants.LISTENER_FOCUS));
		for(FocusListener listener:listeners){
			if(listener != null)listener.syn(mFocusHelper.findAll());
		}
		//发送IM消息
		ImCore.getInstance().getCustomProtocolDealerManager().createDealer(fans.account).focus_notify(fans, 0);
	}
	
	public void cancelFocus(String mobile){
		if(mFocusHelper == null)mFocusHelper = new FocusHelper(AccountData.getInstance().getUsername());
		FansData fans = mFocusHelper.find(mobile);
		mFocusHelper.del(mobile);
		synPersonInfo(AccountData.getInstance().getBindphonenumber(), true);
		synPersonInfo(fans.account, true);
		ImData.getInstance().moveThread2SThread(mobile);
		ArrayList<FocusListener> listeners = new ArrayList<FocusListener>();
		listeners.addAll(MyApplication.getInstance().getListeners(Constants.LISTENER_FOCUS));
		for(FocusListener listener:listeners){
			if(listener != null)listener.syn(mFocusHelper.findAll());
		}
		ImCore.getInstance().getCustomProtocolDealerManager().createDealer(mobile).focus_notify(fans, 1);
	}
	
	public boolean isBlack(String mobile){
		if(mBlackHelper == null)mBlackHelper = new BlackHelper(AccountData.getInstance().getUsername());
		return mBlackHelper.isBlack(mobile);
	}
	
	public void addBlack(String mobile){
		if(mBlackHelper == null)mBlackHelper = new BlackHelper(AccountData.getInstance().getUsername());
		mBlackHelper.insert(mobile);
	}
	
	public void cancelBlack(String mobile){
		if(mBlackHelper == null)mBlackHelper = new BlackHelper(AccountData.getInstance().getUsername());
		mBlackHelper.del(mobile);
	}
	
	public void insert(PersonData person){
		if(mPersonHelper == null)mPersonHelper = new PersonHelper(AccountData.getInstance().getUsername());
		mPersonHelper.insert(person);
	}
	
	public static void synPersonInfo(String mobile, boolean force){
		MyApplication.getInstance().personThreadPool.submit(new GetPersonInfoThread(mobile, force));
	}
	
	public static void synInfo(){
		MyApplication.getInstance().threadPool.submit(new GetBlacklistThread());
		MyApplication.getInstance().threadPool.submit(new GetChannellistThread());
		//FIXME 好友从关注中来,暂不判断粉丝
//		MyApplication.getInstance().threadPool.submit(new GetFanslistThread());
		MyApplication.getInstance().threadPool.submit(new GetFocuslistThread());
		MyApplication.getInstance().threadPool.submit(new GetPersonInfoThread());
	}
	
	public PersonData findPerson(String mobile){
		PersonData person;
		if(mPersonHelper == null)mPersonHelper = new PersonHelper(AccountData.getInstance().getUsername());
		try{
			//优先查找缓存
			if(persons == null){
				persons = new HashMap<String, PersonData>();
			}
			if(persons.containsKey(mobile)){
				return persons.get(mobile);
			}
			person = mPersonHelper.find(mobile);
			//查找昵称
			if(person != null){
				addPerson(mobile, person);
				return person;
			}else{
				synPersonInfo(mobile, false);
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		person = new PersonData();
		person.account = mobile;
		person.nickname = mobile;
		addPerson(mobile, person);
		return person;
	}
	
	public String findNameByMobile(String mobile){
		String name = IMUtil.removeCCode(mobile);
		try{
			//feedback
			if (Constants.FEEDBACK_AND_ADVICE.equals(mobile)) {
				name = MyApplication.getInstance().getString(R.string.feedback);
				return name;
			}
			//901
			if(Constants.NO_901.equals(mobile)){
				name = MyApplication.getInstance().getString(R.string.haibao_name);
				return name;
			}
			//900
			if(Constants.NO_900.equals(mobile)){
				name = MyApplication.getInstance().getString(R.string.xuanbotuandui_name);
				return name;
			}
			PersonData person = findPerson(mobile);
			if(!TextUtils.isEmpty(person.memoName)){
				return person.memoName;
			}else if(!TextUtils.isEmpty(person.nickname)){
				return person.nickname;
			}else{
				return person.account;
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return name;
	}

	private void addPerson(String mobile, PersonData person){
		if(persons == null){
			persons = new HashMap<String, PersonData>();
		}
		if(persons.size() >= CAPACITY){
			persons.remove(0);
		}
		persons.put(mobile, person);
	}
	
	public void updPerson(String mobile, PersonData person){
		if(persons == null){
			persons = new HashMap<String, PersonData>();
		}
		if(TextUtils.isEmpty(person.nickname)){
			person.nickname = person.account;
		}
		if(persons.containsKey(mobile)){
			persons.put(mobile, person);
		}else{
			addPerson(mobile, person);
		}
	}
	
	public void delSynFocus(ArrayList<FansData> focuslist){
		try{
			if(mFocusHelper == null)mFocusHelper = new FocusHelper(AccountData.getInstance().getUsername());	
			mFocusHelper.delAll();
			if(focuslist != null){
				mFocusHelper.insert(focuslist);
			}
			for(FansData fans:focuslist){
				if("1".equals(fans.isFocused)){//好友
					IMThreadData thread = ImData.getInstance().getSDatas().get(fans.account);
					if(thread != null){
						ImData.getInstance().getSIndexs().remove(fans.account);
						ImData.getInstance().getSDatas().remove(fans.account);
						ImData.getInstance().addThreadData(fans.account, thread);
					}
				}else{//非好友
					IMThreadData thread = ImData.getInstance().getDatas().get(fans.account);
					if(thread != null){
						ImData.getInstance().getIndexs().remove(fans.account);
						ImData.getInstance().getDatas().remove(fans.account);
						ImData.getInstance().addThreadData(fans.account, thread);
					}
				}
			}
			ArrayList<FocusListener> listeners = new ArrayList<FocusListener>();
			listeners.addAll(MyApplication.getInstance().getListeners(Constants.LISTENER_FOCUS));
			for(FocusListener listener:listeners){
				if(listener != null)listener.syn(focuslist);
			}
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}
}