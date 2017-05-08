package com.lz.oncon.api;

import java.util.HashMap;

public class SIXmppChatManager {
	
	private HashMap<String,SIXmppChat> mChats; 
	protected SIXmppChatManager() {
		mChats = new HashMap<String, SIXmppChat>();
	}
	
	/**
	 * 通过一个账号名添加一个对话线程，如果已存在则直接返回
	 * @param username
	 * @return
	 */
	public SIXmppChat createChat(String username){
		if(mChats.containsKey(username)){
			return mChats.get(username);
		}else{
			SIXmppChat chat = new SIXmppChat(username);
			mChats.put(username, chat);
			return chat;
		}
	}
	
	/**
	 * 获得队列中所有的对话线程
	 * @return
	 */
	public HashMap<String, SIXmppChat> getChats(){
		return mChats;
	}		

}
