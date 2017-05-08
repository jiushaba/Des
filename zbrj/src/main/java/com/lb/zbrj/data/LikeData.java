package com.lb.zbrj.data;

import java.io.Serializable;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class LikeData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String likeAccount;//点赞人账号
	public String nick       ;//点赞人昵称
	public String imageurl   ;//点赞人头像
	
	public void parseFromJSON(JSONObject json){
		try{
			if(json.has("likeAccount"))likeAccount = json.getString("likeAccount");
			if(json.has("nick"))nick = json.getString("nick");
			if(json.has("imageUrl"))imageurl = json.getString("imageUrl");
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}
}
