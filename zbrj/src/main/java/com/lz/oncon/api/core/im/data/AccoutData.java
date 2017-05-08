package com.lz.oncon.api.core.im.data;

public class AccoutData {
	private static AccoutData instance = null;
	public static AccoutData getInstance(){
		if(instance == null){
			instance = new AccoutData();
		}
		return instance;
	}
	private AccoutData() {
//		domain = "im.si-tech.com.cn";//测试
		domain = "im.on-con.com";//生产
//		conferenceDomain = "conference.im.si-tech.com.cn";//测试
		conferenceDomain = "conference.im.on-con.com";//生产
	}
	public String domain;
	public String conferenceDomain;
}