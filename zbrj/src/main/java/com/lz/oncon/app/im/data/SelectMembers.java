package com.lz.oncon.app.im.data;

import java.util.HashMap;

public class SelectMembers {
	
	private String mobile;//发送的手机号码
	private String name;//编辑框文字信息
	private HashMap<String, String> namesAndMobiles;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<String, String> getNamesAndMobiles() {
		return namesAndMobiles;
	}
	public void setNamesAndMobiles(HashMap<String, String> namesAndMobiles) {
		this.namesAndMobiles = namesAndMobiles;
	}
	

}
