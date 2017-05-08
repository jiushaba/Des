package com.lb.zbrj.data;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class FansData {
	public String account  ;//账号                      
	public String nick     ;//昵称                      
	public String imageurl ;//头像url                   
	public int    isFocused;//你是否关注了对方，1是，0否
	public int isSpecial;//0否,1是
	
	public String index;
	
	public void parseFromJSON(JSONObject json){
		try{
			if(json == null)return;
			if(json.has("account"))account = json.getString("account");
			if(json.has("nick"))nick = json.getString("nick");
			if(json.has("imageUrl"))imageurl = json.getString("imageUrl");
			if(json.has("isFocused"))isFocused = json.getInt("isFocused");
			if(json.has("isSpecial"))isSpecial = json.getInt("isSpecial");
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}
}
