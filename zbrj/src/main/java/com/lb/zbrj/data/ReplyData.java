package com.lb.zbrj.data;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class ReplyData {
	public String account;
	public String nick;
	public String imageurl;
	public String msg;
	public String commentid;
	public String videoid;
	
	public void parseFromJSON(JSONObject json){
		try{
			if(json == null)return;
			if(json.has("account"))account = json.getString("account");
			if(json.has("nick"))nick = json.getString("nick");
			if(json.has("imageurl"))imageurl = json.getString("imageurl");
			if(json.has("msg"))msg = json.getString("msg");
			if(json.has("commentid"))commentid = json.getString("commentid");
			if(json.has("videoid"))videoid = json.getString("videoid");
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}

}
