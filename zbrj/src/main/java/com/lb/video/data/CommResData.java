package com.lb.video.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.lb.common.util.Log;

import android.R.integer;
/**
 * 服务器返回信息
 * @author zhanglijun
 *
 */
public class CommResData extends AData {
	/*状态值，0成功，1失败*/
	public String status;
	public String desc;
	public void parseFromJSON(JSONObject json){
		this.parseHeaderFromJSON(json);
		try {
			if (json.has("status")) status = json.getString("status");
			if (json.has("desc")) desc = json.getString("desc");
		} catch (JSONException e) {
			Log.e(e.getMessage(), e);
		}
	}
}
