package com.lb.zbrj.data;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class ChannelData {
	
	public static int ADDED = 1;
	public static int NOT_ADDED = 0;

	public int id;
	public String name;
	public int optType;//操作类型，0，新增，1修改，2删除。客户端根据类型做对应修改
	public int seq;
	public int isAdd;//1-加 0-未加
	
	public void parseFromJSON(JSONObject json){
		try{
			if(json.has("id"))id = json.getInt("id");
			if(json.has("name"))name = json.getString("name");
			if(json.has("optType"))optType = json.getInt("optType");
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}
}
