package com.danmu.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.danmu.comm.Log;

public class DanmuContentData  {
	/*
	Account 发布者账号
	Nick 发布者昵称
	Distance:与视频直播人的距离（进入视频的时候已经计算好）
	Fontsize:大小
	Time:视频的时间节点，格式mm:ss

	*/
	public String id;
	public String account;
	public String nick;
	public String msg;
	public String distance;
	public String fontsize;
	public String time;
	public String speed;
	public String location;
	public JSONObject toJSONObject() throws Exception{
		JSONObject object = new JSONObject();
		object.put("id", id);
		putStringValue(object, "account", account);
		putStringValue(object, "nick", nick);
		putStringValue(object, "msg", msg);
		putStringValue(object, "fontsize", fontsize);
		putStringValue(object, "time", time);
		object.put("distance", distance);
		object.put("speed", speed);
		object.put("location", location);
		return object;
	}
	
	public void parseFromJSON(JSONObject json){
		try {
			if (json.has("account")) account = json.getString("account");
			if (json.has("nick")) nick = json.getString("nick");
			if (json.has("msg")) msg = json.getString("msg");
			if (json.has("fontsize")) fontsize = json.getString("fontsize");
			if (json.has("time")) time = json.getString("time");
			if (json.has("distance")) distance = json.getString("distance");
			if(json.has("speed")) speed = json.getString("speed");
			if(json.has("location")) location = json.getString("location");
		} catch (JSONException e) {
			Log.e(e.getMessage(), e);
		}
	}
	public long getTimemillisecond(){
		try{
			String[] s = time.split(":");
			if(s.length==2){
				return Integer.parseInt(s[0])*60000+Integer.parseInt(s[1])*1000;
			}
		}catch(Exception e){
			Log.d(e.getMessage(), time+" 不是有效格式");
		}
		return 0;
	}
	public void putStringValue(JSONObject object,String name ,String value) throws Exception{
		if(TextUtils.isEmpty(value)){
			object.put(name, "");
		}else{
			object.put(name, value);
		}
		
	}
	
}
