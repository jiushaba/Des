package com.lb.video.data;

import org.json.JSONObject;

import android.text.TextUtils;

import com.lb.common.util.Log;

public abstract class AData {
	public String id;      
	public String type; 
	public String action;
	protected void parseHeaderFromJSON(JSONObject json) {
		try {
			if (json.has("id")) id = json.getString("id");
			if (json.has("type")) id = json.getString("type");
			if (json.has("action")) id = json.getString("action");
		} catch (Exception e) {
			Log.e("",e.getMessage(), e);
		}
	}
	public JSONObject toJSON() throws Exception{
		JSONObject object = new JSONObject();
		putStringValue(object, "id", id);
		putStringValue(object, "type", type);
		putStringValue(object, "action", action);
		return object;
	}

	/**
	 * 增加一个方法，判断是否为空,统一控制
	 * @throws JSONException 
	 */
	public void putStringValue(JSONObject object,String name ,String value) throws Exception{
		if(TextUtils.isEmpty(value)){
			object.put(name, "");
		}else{
			object.put(name, value);
		}
		
	}
}
