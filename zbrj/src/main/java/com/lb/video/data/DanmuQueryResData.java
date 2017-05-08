package com.lb.video.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.danmu.data.DanmuContentData;
import com.lb.common.util.Log;


public class DanmuQueryResData extends AData {
	public String status;
	public String desc;
	public String dateTime;
	public ArrayList<DanmuContentData> bulletlist = new ArrayList<DanmuContentData>();
	
	public void parseFromJSON(JSONObject json){
		this.parseHeaderFromJSON(json);
		try {
			if (json.has("status")) status = json.getString("status");
			if (json.has("desc")) desc = json.getString("desc");
			if (json.has("dateTime")) dateTime = json.getString("dateTime"); 
			if(json.has("bulletList")){
				JSONArray array = json.getJSONArray("bulletList");
				if(array != null && array.length()>0){
					int length = array.length();
					for(int i=0 ; i<length ; i++){
						DanmuContentData content = new DanmuContentData();
						String s = array.getJSONObject(i).getString("content");
						JSONObject subj = new JSONObject(s);
						content.parseFromJSON(subj);
						bulletlist.add(content);
					}
				}
			}
		} catch (JSONException e) {
			Log.e(e.getMessage(), e);
		}
	}
}