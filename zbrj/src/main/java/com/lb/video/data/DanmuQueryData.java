package com.lb.video.data;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class DanmuQueryData extends AData {
	public String videoID;
	public String version="1.0";
	public String datetime = "0";
	public DanmuQueryData(){
		action = "request";
		type = "m1_get_bullet";
	}
	public String toJSONString() {
		JSONObject object = null;
		try{
			object = toJSON();
			putStringValue(object, "videoID", videoID);
			putStringValue(object, "version", version);
			putStringValue(object, "datetime", datetime);
			return object.toString();
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return "{}";
	}
}

