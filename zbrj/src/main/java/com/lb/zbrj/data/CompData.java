package com.lb.zbrj.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lb.common.util.Log;

public class CompData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String compName  ;//比赛名称    
	public String compType  ;//比赛所属频道
	public int    compNum   ;//比赛参加人数
	public String compid    ;//比赛类型id  
	public String starttime ;//比赛起始时间
	public String endTime   ;//比赛结束时间
	public String rule      ;//比赛规则    
	public int sort = -1;
	public ArrayList<VideoData> videos = new ArrayList<VideoData>();
	
	public void parseFromJSON(JSONObject json){
		try{
			if(json.has("compName"))compName = json.getString("compName");
			if(json.has("compType"))compType = json.getString("compType");
			if(json.has("compNum"))compNum = json.getInt("compNum");
			if(json.has("compid"))compid = json.getString("compid");
			if(json.has("starttime"))starttime = json.getString("starttime");
			if(json.has("endTime"))endTime = json.getString("endTime");
			if(json.has("rule"))rule = json.getString("rule");
			if(json.has("videoList")){
				JSONArray videoList = json.getJSONArray("videoList");
				if(videoList.length() > 0){
					int length = videoList.length();
					for(int i=0;i<length;i++){
						VideoData video = new VideoData();
						video.parseFromJSON(videoList.getJSONObject(i));
						videos.add(video);
					}
				}
			}
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}
}
