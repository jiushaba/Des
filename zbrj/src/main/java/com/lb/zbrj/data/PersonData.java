package com.lb.zbrj.data;

import java.io.Serializable;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class PersonData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8574700550397288325L;
	public String account;
	public String nickname = ""   ;//昵称        
	public String image = ""      ;//头像url  
	public String sign = ""      ;//个人签名    
	public String label = ""      ;//标签        
	public int    fansNum    ;//粉丝数目    
	public int oldFansNum;//上次粉丝数目
	public int    score      ;//积分数      
	public int     focusNum  ;//关注人数
	public int     oldFocusNum  ;//关注人数 
	public String    location = "";//所在地区
	public int videoNums     ;//直播视频数目
	public int sex = 0;//性别，0男，1女
	public String birthday;//出生日期，格式yyyy-MM-DD hh:mm:ss
	public long timestamp;//同步其他信息时间
	public long fanstimestamp;//同步粉丝数目时间
	public String memoName;//备注姓名
	
	public void parseFromJSON(JSONObject json){
		try{
			if(json == null)return;
			if(json.has("account"))account = json.getString("account");
			if(json.has("nickName"))nickname = json.getString("nickName");
			if(json.has("nick"))nickname = json.getString("nick");
			if(json.has("image"))image = json.getString("image");
			if(json.has("imageurl"))image = json.getString("imageurl");
			if(json.has("sign"))sign = json.getString("sign");
			if(json.has("label"))label = json.getString("label");
			if(json.has("fansNum"))fansNum = json.getInt("fansNum");
			if(json.has("score"))score = json.getInt("score");
			if(json.has("focusNum"))focusNum = json.getInt("focusNum");
			if(json.has("location"))location = json.getString("location");
			if(json.has("videoNums"))videoNums = json.getInt("videoNums");
			if(json.has("sex"))sex = json.getInt("sex");
			if(json.has("birthDay"))birthday = json.getString("birthDay");
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}
	
	public String save(JSONObject obj){
		try{
			obj.put("nickName", nickname);
			obj.put("label", label);
			obj.put("location", location);
			obj.put("sign", sign);
			obj.put("sex", sex);
			obj.put("birthDay", birthday);

			return obj.toString();
		}catch(Exception e){
			return "";
		}
	}
}
