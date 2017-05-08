package com.lb.video.data;

import org.json.JSONObject;

import com.lb.common.util.Log;
import com.lz.oncon.data.AccountData;

public class VideoStartRecorderData extends VideoStartLookData {
	/*视频标题*/
	public String videoTitle;
	/*视频标签*/
	public String label	;
	/*视频缩略图url*/
	public String videoImage;
	/*视频所属类型*/
	public int videoType =0;
	/*视频录制人X坐标*/
	public double locationX	;
	/*视频录制人Y坐标*/
	public double locationY;
	/*播放url*/
	public String playUrl;
	/*是否公开，1是，0否*/
	public int isPublic;
	/*是否参与比赛，1是，0否*/
	public int isComp;
	/*比赛id，仅当isComp为1的时候上传*/
	public String compid;
	public int osType=1;
	public VideoStartRecorderData(){
		type = "m1_upload_viewInfo";
		action="request";
		actType = 1;
	}
	public String toJSONString() {
		JSONObject object = toJSONObject();
		return object == null ?"{}":object.toString();
	}
	public JSONObject toJSONObject(){
		JSONObject object = null;
		try{
			object = super.toJSONObject();
			putStringValue(object, "videoTitle", videoTitle);
			putStringValue(object, "label", label);
			putStringValue(object, "videoImage", videoImage);
			object.put("videoType", videoType);
			object.put("locationX", locationX);
			object.put("locationY", locationY);
			putStringValue(object, "playUrl", playUrl);
			object.put("isPublic", isPublic);
			object.put("isComp", isComp);
			object.put("osType", osType);
			putStringValue(object, "compid", compid);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return object;
	}
	
}
