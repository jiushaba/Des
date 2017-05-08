package com.lb.video.data;

import org.json.JSONObject;

import com.lb.common.util.Log;
import com.lz.oncon.data.AccountData;

/**
 * 视频开始观看处理类
 * @author zhanglijun
 *
 */
public class VideoStartLookData extends AData {
	public String account = AccountData.getInstance().getBindphonenumber();
	public String version ="1.0";
	public String videoID ;
	/*表示行为，1表示正在直播，2表示正在观看*/
	public int actType=2;

	public VideoStartLookData(){
		type = "m1_upload_viewInfo";
		action="request";
	}
	public String toJSONString() {
		JSONObject object = toJSONObject();
		return object == null ?"{}":object.toString();
	}
	public JSONObject toJSONObject(){
		JSONObject object = null;
		try{
			object = super.toJSON();
			object.put("account", account);
			putStringValue(object, "videoID", videoID);
			putStringValue(object, "version", version);
			object.put("actType", actType);//FIXME 添加了actType
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return object;
	}
}
