/**
 * 临时记录下之前在聊天框里输入的内容，但是没有发送，或者取消的
 */
package com.lz.oncon.app.im.data;

import java.util.HashMap;

public class IMMessageWriteData {
	
	private HashMap<String, String> mMessages;
	
	private static IMMessageWriteData instance;
	public static IMMessageWriteData getInstance(){
		if(instance == null){
			instance = new IMMessageWriteData();
		}
		return instance;
	}
	private IMMessageWriteData() {
		mMessages = new HashMap<String, String>();
	}	
	 
	public void putMessage(String onconid,String message){
		mMessages.put(onconid, message);
	}
	public void removeMessage(String onconid){
		mMessages.remove(onconid);
	}
	public String getMessage(String onconid){
		String message = mMessages.get(onconid);
		if(message==null){
			return "";
		}
		return message;
	}
	public void clearMessags(){
		mMessages.clear();
	}
}
