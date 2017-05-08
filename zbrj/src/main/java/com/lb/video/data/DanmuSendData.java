package com.lb.video.data;

import org.json.JSONObject;

import com.danmu.data.DanmuContentData;
import com.lb.common.util.Log;

public class DanmuSendData extends AData{
	public String videoID;
	public String version="1.0";
	public DanmuContentData content = new DanmuContentData();
	public DanmuSendData(){
		type = "m1_send_bullet";
		action = "request";
	}
	public String toJSONString() {
		JSONObject object = null;
		try{
			object = toJSON();
			putStringValue(object, "videoID", videoID);
			putStringValue(object, "version", version);
			JSONObject contentObject = content.toJSONObject();
			object.put("content", contentObject.toString());
			return object.toString();
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return "{}";
	}
}
