package com.lz.oncon.api;

import java.util.HashMap;

public class CustomProtocolDealerManager {
	
	private HashMap<String, CustomProtocolDealer> mDealers; 
	protected CustomProtocolDealerManager() {
		mDealers = new HashMap<String, CustomProtocolDealer>();
	}
	
	/**
	 * 通过一个账号名添加一个对话线程，如果已存在则直接返回
	 * @param username
	 * @return
	 */
	public CustomProtocolDealer createDealer(String username){
		if(mDealers.containsKey(username)){
			return mDealers.get(username);
		}else{
			CustomProtocolDealer chat = new CustomProtocolDealer(username);
			mDealers.put(username, chat);
			return chat;
		}
	}
	
	/**
	 * 获得队列中所有的对话线程
	 * @return
	 */
	public HashMap<String, CustomProtocolDealer> getDealers(){
		return mDealers;
	}		
}