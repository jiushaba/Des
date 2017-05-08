package com.lb.zbrj.data;

import java.io.Serializable;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class VideoHlsData implements Serializable{
	private static final long serialVersionUID = 1L;
	public String status;
	public String hslUrl;
	public String isLive;
	public String desc;

	public void parseFromJSON(JSONObject json) {
		try {
			if (json.has("status")) {
				status = json.getString("status");
			}
			if (json.has("hslUrl")) {
				hslUrl = json.getString("hslUrl");
			}
			if (json.has("isLive")) {
				hslUrl = json.getString("isLive");
			}
			if (json.has("desc")) {
				desc = json.getString("desc");
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}

	}
}
