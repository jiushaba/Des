package com.lz.oncon.data;

import com.lz.oncon.application.MyApplication;

import android.content.SharedPreferences;

public class SettingInfoData {
	//FIXME 初始均为不提示
	private boolean isFirstLoad = false;
	private boolean isFirstLoadFC = false;
	private boolean isFirstLoadContact = false;
	private boolean isFirstLoadMessage = false;
	
	private SharedPreferences sp = null;
	private SharedPreferences.Editor editor = null;
	private String IS_FIRST_LOAD = "isFirstLoad";
	private String IS_FIRST_LOAD_FC = "is_first_load_fc";
	private String IS_FIRST_LOAD_CONTACT = "is_first_load_contact";
	private String IS_FIRST_LOAD_MESSAGE = "is_first_load_message";
	
	private static SettingInfoData instance = null;
	public static SettingInfoData getInstance(){
		if(instance == null){
			instance = new SettingInfoData();
		}
		return instance;
	}
	private SettingInfoData() {
		sp = MyApplication.getInstance().getSharedPreferences("settings", 0);
		editor = sp.edit();
		isFirstLoad = sp.getBoolean(IS_FIRST_LOAD, true);
		isFirstLoadFC = sp.getBoolean(IS_FIRST_LOAD_FC, true);
		isFirstLoadContact = sp.getBoolean(IS_FIRST_LOAD_CONTACT, true);
		isFirstLoadMessage = sp.getBoolean(IS_FIRST_LOAD_MESSAGE, true);
	}
	
	public boolean isFirstLoad(){
		return isFirstLoad;
	}
	public boolean setIsFirstLoad(boolean isFirstLoad){
		this.isFirstLoad = isFirstLoad;
		editor.putBoolean(IS_FIRST_LOAD, isFirstLoad);
		return editor.commit();
	}
	
	public boolean isFirstLoadFC(){
		return isFirstLoadFC;
	}
	public boolean setIsFirstLoadFC(boolean isFirstLoadFC){
		this.isFirstLoadFC = isFirstLoadFC;
		editor.putBoolean(IS_FIRST_LOAD_FC, isFirstLoadFC);
		return editor.commit();
	}
	
	public boolean isFirstLoadContact(){
		return isFirstLoadContact;
	}
	public boolean setIsFirstLoadContact(boolean isFirstLoadContact){
		this.isFirstLoadContact = isFirstLoadContact;
		editor.putBoolean(IS_FIRST_LOAD_CONTACT, isFirstLoadContact);
		return editor.commit();
	}
	
	public boolean isFirstLoadMessage(){
		return isFirstLoadMessage;
	}
	public boolean setIsFirstLoadMessage(boolean isFirstLoadMessage){
		this.isFirstLoadMessage = isFirstLoadMessage;
		editor.putBoolean(IS_FIRST_LOAD_MESSAGE, isFirstLoadMessage);
		return editor.commit();
	}
}
