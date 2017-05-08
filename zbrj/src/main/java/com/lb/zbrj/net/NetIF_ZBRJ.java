package com.lb.zbrj.net;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.danmu.data.DanmuContentData;
import com.lb.common.util.Constants;
import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;
import com.lb.video.data.CommResData;
import com.lb.video.data.DanmuQueryData;
import com.lb.video.data.DanmuQueryResData;
import com.lb.video.data.DanmuSendData;
import com.lb.video.data.VideoStartRecorderData;
import com.lb.zbrj.data.ChannelData;
import com.lb.zbrj.data.CommentData;
import com.lb.zbrj.data.CompData;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.LikeData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.ReplyData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.data.VideoHlsData;
import com.lb.zbrj.view.VideoListView;
import com.lz.oncon.activity.friendcircle.Source_DynamicList;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.sun.crypto.provider.RSACipher;

public class NetIF_ZBRJ extends NetIF {
	// http
	public static String address = "http://122.112.13.176:8090/livenotes/livenotes/service/";// 生产服务器
	public static String address_file = "http://122.112.13.176:8090/sources";//生产服务器
	public static String address_head = "http://122.112.13.176:8090/sources/images/head";//
	public static String address_live = "rtmp://122.112.13.176:1935/live/";
	public static String address_share ="http://www.appxuan.cn:8090/html5/index.html";
	private static String VERSION = "1.0";

	public NetIF_ZBRJ(Context context) {
		super(context);
	}

	/**
	 * 1.1 获取验证码模块
	 */
	public NetInterfaceStatusDataStruct m1_get_verify(String account) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_verify");
			req.put("account", account);
			//FIXME 测试用
			String res = "";
			res = callService(address + "verify", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.2 注册
	 */
	public NetInterfaceStatusDataStruct m1_reg(String account, String password, String verify) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_reg");
			req.put("account", account);
//			req.put("password", MD5.bytes2hex(MD5.md5(password.getBytes(Constants.ENCODE))));
			req.put("password", password);//FIXME MD5
			req.put("verify", verify);
			//FIXME 测试用
			String res = "";
			res = callService(address + "regist", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.3 登录接口
	 */
	public NetInterfaceStatusDataStruct m1_login(String account, String password, String uuid) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_login");
			req.put("account", account);
//			req.put("password", MD5.bytes2hex(MD5.md5(password.getBytes(Constants.ENCODE))));
			req.put("password", password);//FIXME MD5
			req.put("uuid", uuid);
			// 测试
			String res = "";//"{'status':'0','username':'"+account+"','mobile':'"+account+"','nickname':'"+account+"'}";
			res = callService(address + "login", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.4 获取视频列表
	 * isupdate 0表示获取更多视频，1表示更新视频列表。 客户端仅在视频列表页面下拉刷新的时候，设置该值为1.当isUpdate为1的时候，服务器返回大于startTime的
	 * startTime 时间点

	 */
	public NetInterfaceStatusDataStruct m1_get_videoList(int videoType , int isUpdate , String startTime ,int queryType, int actType , int count){
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_videoList");
			req.put("videoType", videoType);
			req.put("isUpdate", isUpdate);
			req.put("startTime", startTime);
			req.put("count", count);
			if(queryType != -1){
				req.put("queryType", queryType);
			}
			if(queryType == VideoListView.QUERYTYPE_FRIEND){
				req.put("account", AccountData.getInstance().getBindphonenumber());
			}else if(queryType == VideoListView.QUERYTYPE_NEARBY){
				req.put("locationX", MyApplication.getInstance().mPreferencesMan.getLatitude());
				req.put("locationY", MyApplication.getInstance().mPreferencesMan.getLongitude());
			}
			req.put("actType", actType);
			
			String res = "";
			res = callService(address + "video/getList", req.toString());
			result = parseResStr(res);
			// 增加测试数据
			//getTestVideo(result);
			List<Object> list = new ArrayList<Object>();
			if (Constants.RES_SUCCESS.equals(result.getStatus())) {
				JSONObject jsonObject = (JSONObject) result.getObj();
				if (jsonObject.has("videoList")) {
					JSONArray videoList = ((JSONObject) result.getObj()).getJSONArray("videoList");
					int length = videoList.length();
					for (int i = 0; i < length; i++) {
						JSONObject obj = videoList.getJSONObject(i);
						VideoData video = new VideoData();
						video.parseFromJSON(obj);
						list.add(video);
						
					}
				}
				
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	/*public NetInterfaceStatusDataStruct m1_get_videoList(int videoType, int queryType, int actType, int start, int count) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_videoList");
			req.put("videoType", videoType);
			req.put("start", start);
			req.put("count", count);
			if(queryType != -1){
				req.put("queryType", queryType);
			}
			req.put("actType", actType);
			
			//FIXME 测试
			String res = "";
			res = callService(address + "video/getList", req.toString());
			result = parseResStr(res);
			
//			getTestVideo(result);
			
			ArrayList<VideoData> list = new ArrayList<VideoData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("videoList")){
				JSONArray videoList = ((JSONObject)result.getObj()).getJSONArray("videoList");
				int length = videoList.length();
				for(int i=0;i<length;i++){
					JSONObject obj = videoList.getJSONObject(i);
					VideoData video = new VideoData();
					video.parseFromJSON(obj);
					list.add(video);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}*/
	
	/**
	 * 1.5 坐标上传接口
	 */
	public NetInterfaceStatusDataStruct m1_upload_location(double locationX, double locationY) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_upload_location");
			req.put("locationX", locationX);
			req.put("locationY", locationY);
			
			String res = callService(address + "upload/location", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.6 关闭/开启距离查看接口
	 */
	public NetInterfaceStatusDataStruct m1_ops_location(int isShow) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_ops_location");
			req.put("isShow", isShow);
			
			String res = callService(address + "control/location", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.7 获取个人信息接口
	 */
	public NetInterfaceStatusDataStruct m1_get_personalInfo(String account) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_personalInfo");
			req.put("account", account);
			
			//FIXME 测试数据
			String res = "";
			res = callService(address + "account/info/fetch", req.toString());
			result = parseResStr(res);
			
//			getPersonInfo(result, account);
			
			PersonData data = new PersonData();
			data.account = account;
			data.parseFromJSON((JSONObject)result.getObj());
			result.setObj(data);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.8 更改个人信息接口
	 */
	public NetInterfaceStatusDataStruct m1_update_personalInfo(PersonData person) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_update_personalInfo");
			req.put("nickName", person.nickname);
			req.put("location", person.location);
			req.put("sex", person.sex);
			req.put("birthDay", person.birthday);
			req.put("desc", person.sign);
			
			String res = callService(address + "account/info/update", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.8 更改个人信息接口
	 */
	public NetInterfaceStatusDataStruct m1_update_personalInfoLabel(String label) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_update_personalInfo");
			req.put("label", label);
			
			String res = callService(address + "account/info/update", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.8 更改个人信息接口
	 */
	public NetInterfaceStatusDataStruct m1_update_personalInfoSign(String sign) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_update_personalInfo");
			req.put("desc", sign);
			
			String res = callService(address + "account/info/update", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.9 上传头像
	 */
	public NetInterfaceStatusDataStruct m1_upload_image(String image) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_upload_image");
			req.put("image", image);
			String res = callService(address + "account/img/upload", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.10 获取粉丝列表
	 */
	public NetInterfaceStatusDataStruct m1_get_fans(String account, int start, int count) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_get_fans");
			req.put("account", account);
			req.put("start", start);
			req.put("count", count);
			
			String res = callService(address + "funs/getList", req.toString());
			result = parseResStr(res);
			ArrayList<FansData> list = new ArrayList<FansData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("fansList")){
				JSONArray fansList = ((JSONObject)result.getObj()).getJSONArray("fansList");
				int length = fansList.length();
				for(int i=0;i<length;i++){
					JSONObject obj = fansList.getJSONObject(i);
					FansData fans = new FansData();
					fans.parseFromJSON(obj);
					list.add(fans);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.11 获取关注列表
	 */
	public NetInterfaceStatusDataStruct m1_get_focus(String account, int start, int count) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_get_focus");
			req.put("account", account);
			req.put("start", start);
			req.put("count", count);
			
			String res = callService(address + "focus/getList", req.toString());
			result = parseResStr(res);
			ArrayList<FansData> list = new ArrayList<FansData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("focusList")){
				JSONArray fansList = ((JSONObject)result.getObj()).getJSONArray("focusList");
				int length = fansList.length();
				for(int i=0;i<length;i++){
					JSONObject obj = fansList.getJSONObject(i);
					FansData fans = new FansData();
					fans.parseFromJSON(obj);
					list.add(fans);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}	
	
	/**
	 * 1.12 添加关注
	 */
	public NetInterfaceStatusDataStruct m1_add_focus(String focusAccount, int isSpecial) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_add_focus");
			req.put("focusAccount", focusAccount);
			req.put("isSpecial", isSpecial);
			
			String res = callService(address + "focus/add", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.13 取消关注
	 * @param cancelType 0,取消关注，1取消特别关注
	 */
	public NetInterfaceStatusDataStruct m1_cancel_focus(String focusAccount,int cancelType) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_cancel_focus");
			req.put("focusAccount", focusAccount);
			req.put("cancelType", cancelType);
			String res = callService(address + "focus/cancel", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.14 上传观看记录
	 * acttype = 1  使用m1_upload_viewInfo_live 方法
	 * @see VideoNet
	 */
	public NetInterfaceStatusDataStruct m1_upload_viewInfo(String actType, VideoData videoData) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_upload_viewInfo");
			if(videoData == null){
				req.put("videoID", "");
			}else{
				req.put("videoID", videoData.videoID);
			}
			req.put("actType", actType);
			String res = callService(address + "video/record/upload", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	public CommResData m1_upload_viewInfo_live(VideoStartRecorderData data){
		CommResData result = new CommResData();
		try{
			if(data != null){
				String res = callService(address + "video/record/upload", data.toJSONString());
				if(TextUtils.isEmpty(res)){
					result.status = Constants.RES_FAIL;
				}else{
					JSONObject jsonRes = new JSONObject(res);
					if(jsonRes == null || !jsonRes.has("status")){
						result.status = Constants.RES_FAIL;
					}else{
						result.parseFromJSON(jsonRes);
					}
				}
			}
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.15 获取已直播视频列表（我的足迹）
	 */
	/*public NetInterfaceStatusDataStruct m1_get_myvideoList(String account, long dateTime , int opType) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_myvideoList");
			req.put("account", account);
			req.put("opType", opType);
			req.put("dateTime", dateTime);
			//FIXME 测试
			String res = "";
			res = callService(address + "track/getList", req.toString());
			result = parseResStr(res);
			
//			getTestVideo(result);
			
			ArrayList<VideoData> list = new ArrayList<VideoData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("videoList")){
				JSONArray videoList = ((JSONObject)result.getObj()).getJSONArray("videoList");
				int length = videoList.length();
				for(int i=0;i<length;i++){
					JSONObject obj = videoList.getJSONObject(i);
					VideoData video = new VideoData();
					video.parseFromJSON(obj);
					if(obj.has("commentList")
							&& obj.optJSONArray("commentList") != null){
						JSONArray commentList = obj.getJSONArray("commentList");
						int length2 = commentList.length();
						for(int j=0;j<length2;j++){
							JSONObject obj2 = commentList.getJSONObject(j);
							CommentData comment = new CommentData();
							comment.parseFromJSON(obj2);
							video.comments.add(comment);
						}
					}
					if(obj.has("likeList")
							&& obj.optJSONArray("likeList") != null){
						JSONArray likelist = obj.getJSONArray("likeList");
						int length2 = likelist.length();
						for(int j=0;j<length2;j++){
							JSONObject obj2 = likelist.getJSONObject(j);
							LikeData like = new LikeData();
							like.parseFromJSON(obj2);
							video.likes.add(like);
						}
					}
					list.add(video);
				}
			}
			Source_DynamicList source_dynamicList = new Source_DynamicList();
			source_dynamicList.setSourceDynamicList(list);
			result.setObj(source_dynamicList);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}*/
	
	public NetInterfaceStatusDataStruct m1_get_myvideoList(String account, String starttime, int count, int opType) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_myvideoList");
			req.put("account", account);
			req.put("startTime", starttime);
			req.put("count", count);
			req.put("opType", opType);
			//FIXME 测试
			String res = "";
			res = callService(address + "track/getList", req.toString());
			result = parseResStr(res);
			
//			getTestVideo(result);
			
			ArrayList<VideoData> list = new ArrayList<VideoData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("videoList")){
				JSONArray videoList = ((JSONObject)result.getObj()).getJSONArray("videoList");
				int length = videoList.length();
				for(int i=0;i<length;i++){
					JSONObject obj = videoList.getJSONObject(i);
					VideoData video = new VideoData();
					video.parseFromJSON(obj);
					if(obj.has("commentList")
							&& obj.optJSONArray("commentList") != null){
						JSONArray commentList = obj.getJSONArray("commentList");
						int length2 = commentList.length();
						for(int j=0;j<length2;j++){
							JSONObject obj2 = commentList.getJSONObject(j);
							CommentData comment = new CommentData();
							comment.parseFromJSON(obj2);
							video.comments.add(comment);
						}
					}
					if(obj.has("likeList")
							&& obj.optJSONArray("likeList") != null){
						JSONArray likelist = obj.getJSONArray("likeList");
						int length2 = likelist.length();
						for(int j=0;j<length2;j++){
							JSONObject obj2 = likelist.getJSONObject(j);
							LikeData like = new LikeData();
							like.parseFromJSON(obj2);
							video.likes.add(like);
						}
					}
					list.add(video);
				}
			}
			Source_DynamicList source_dynamicList = new Source_DynamicList();
			source_dynamicList.setSourceDynamicList(list);
			result.setObj(source_dynamicList);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.17 阻止某人看我的足迹
	 */
	public NetInterfaceStatusDataStruct m1_forbid_view(String forbidAccount) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_forbid_view");
			req.put("forbidAccount", forbidAccount);
			
			String res = callService(address + "track/forbid", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.18 解除阻止观看足迹
	 */
	public NetInterfaceStatusDataStruct m1_agree_view(String agreeAccount) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_agree_view");
			req.put("agreeAccount", agreeAccount);
			
			String res = callService(address + "track/agree", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.19 查看是否被指定账户阻止观看足迹
	 */
	public NetInterfaceStatusDataStruct m1_isForbid_view(String requestAccount) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_isForbid_view");
			req.put("requestAccount", requestAccount);
			//FIXME 测试
			String res = "";
			res = callService(address + "track/check", req.toString());
			result = parseResStr(res);
			
//			result.setStatus(Constants.RES_SUCCESS);
//			JSONObject obj = new JSONObject();
//			obj.put("isForbid", 0);
//			result.setObj(obj);
			
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				result.setObj(((JSONObject)result.getObj()).getInt("isForbid"));
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.20 添加黑名单
	 */
	public NetInterfaceStatusDataStruct m1_add_blacklist(String blackAccount) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_add_blacklist");
			req.put("blackAccount", blackAccount);
			
			String res = callService(address + "blackList/add", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.21 取消黑名单
	 */
	public NetInterfaceStatusDataStruct m1_cancel_blacklist(String blackAccount) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_cancel_blacklist");
			req.put("blackAccount", blackAccount);
			
			String res = callService(address + "blackList/cancel", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.22 获取黑名单列表
	 */
	public NetInterfaceStatusDataStruct m1_get_blacklist() {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_get_blacklist");
			
			String res = callService(address + "blackList/get", req.toString());
			result = parseResStr(res);
			ArrayList<String> list = new ArrayList<String>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("blackList")){
				JSONArray blacklist = ((JSONObject)result.getObj()).getJSONArray("blackList");
				int length = blacklist.length();
				for(int i=0;i<length;i++){
					JSONObject obj = blacklist.getJSONObject(i);
					list.add(obj.getString("blackAccount"));
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}	
	
	/**
	 * 1.23 举报接口
	 */
	public NetInterfaceStatusDataStruct m1_report(String reportAccount, String content) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_report");
			req.put("reportAccount", reportAccount);
			req.put("content", content);
			
			String res = callService(address + "expose/add", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.24 发表评论
	 */
	public NetInterfaceStatusDataStruct m1_comment(CommentData comment) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_comment");
			req.put("commentAccount", comment.commentToAccount);
			req.put("comentVideoID", comment.comentVideoID);
			req.put("commentId", comment.commentToCommentID);
			req.put("content", comment.content);
			
			String res = callService(address + "comment/add", req.toString());
			result = parseResStr(res);
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				JSONObject resJsonObject = (JSONObject) result.getObj();
				if(resJsonObject.has("commentID")){
					comment.commentID = resJsonObject.getString("commentID");
				}
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.25 删除评论接口
	 */
	public NetInterfaceStatusDataStruct m1_del_comment(String comentVideoID, String comentid) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_del_comment");
			req.put("comentVideoID", comentVideoID);
			req.put("commentID", comentid);
			
			String res = callService(address + "comment/delete", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.26 查询评论接口
	 */
	public NetInterfaceStatusDataStruct m1_get_comment(String comentVideoID) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_get_comment");
			req.put("comentVideoID", comentVideoID);
			
			String res = callService(address + "comments/get", req.toString());
			result = parseResStr(res);
			ArrayList<CommentData> list = new ArrayList<CommentData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("commentList")){
				JSONArray commentList = ((JSONObject)result.getObj()).getJSONArray("commentList");
				int length = commentList.length();
				for(int i=0;i<length;i++){
					JSONObject obj = commentList.getJSONObject(i);
					CommentData fans = new CommentData();
					fans.parseFromJSON(obj);
					list.add(fans);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.27 获取个人坐标
	 */
	public NetInterfaceStatusDataStruct m1_get_location() {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_get_location");
			
			String res = callService(address + "location/get", req.toString());
			result = parseResStr(res);
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				double[] location = new double[2];
				location[0] = ((JSONObject)result.getObj()).getDouble("locationX");
				location[1] = ((JSONObject)result.getObj()).getDouble("locationY");
				result.setObj(location);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.28 点赞接口
	 */
	public NetInterfaceStatusDataStruct m1_give_like(String videoID) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_give_like");
			req.put("videoID", videoID);
			
			String res = callService(address + "video/praise", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.29 查找/筛选接口
	 */
	public NetInterfaceStatusDataStruct m1_filters(String filterValue, int opType, int start, int count) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_filters");
			req.put("filterValue", filterValue);
			req.put("start", start);
			req.put("count", count);
			
			//FIXME 测试
			String res = "";
			res = callService(address + "video/filter", req.toString());
			result = parseResStr(res);
			
//			getTestVideo(result);
			
			ArrayList<Object> list = new ArrayList<Object>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				if(((JSONObject)result.getObj()).has("videoList")){
					JSONArray videoList = ((JSONObject)result.getObj()).getJSONArray("videoList");
					int length = videoList.length();
					for(int i=0;i<length;i++){
						JSONObject obj = videoList.getJSONObject(i);
						VideoData video = new VideoData();
						video.parseFromJSON(obj);
						list.add(video);
					}
				}
				if(((JSONObject)result.getObj()).has("userList")){
					JSONArray videoList = ((JSONObject)result.getObj()).getJSONArray("userList");
					int length = videoList.length();
					for(int i=0;i<length;i++){
						JSONObject obj = videoList.getJSONObject(i);
						PersonData data = new PersonData();
						data.parseFromJSON(obj);
						list.add(data);
					}
				}
				
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.30 查询视频观众数目等信息接口
	 */
	public NetInterfaceStatusDataStruct m1_query_viewer(String videoID) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_query_viewer");
			req.put("videoID", videoID);
			String res = callService(address + "video/numbers", req.toString());
			result = parseResStr(res);
			ArrayList<VideoData> list = new ArrayList<VideoData>();
			String bulletsNum = "", upNum = "", watchersNum = "",friendNum="";
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				JSONObject obj = (JSONObject)result.getObj();
				/*弹幕数*/
				if(obj.has("bulletsNum")){
					try{
						bulletsNum = obj.getString("bulletsNum");
					}catch(Exception e){
						Log.e(e.getMessage(), e);
					}
				}
				/*赞数目*/
				if(obj.has("upNum")){
					try{
						upNum = obj.getString("upNum");
					}catch(Exception e){
						Log.e(e.getMessage(), e);
					}
				}
				/*观看人数*/
				if(obj.has("watchersNum")){
					try{
						watchersNum = obj.getString("watchersNum");
					}catch(Exception e){
						Log.e(e.getMessage(), e);
					}
				}
				/*好友观看数*/
				if(obj.has("friendNum")){
					try{
						friendNum = obj.getString("friendNum");
					}catch(Exception e){
						Log.e(e.getMessage(), e);
					}
				}
				Bundle map = new Bundle();
				map.putString("bulletsNum", bulletsNum);
				map.putString("upNum", upNum);
				map.putString("watchersNum", watchersNum);
				map.putString("friendNum", friendNum);
				result.setObj(map);
			}
			
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 1.31 发送直播实时弹幕
	 */
	public CommResData danmu_send(DanmuSendData data){
		CommResData result = new CommResData();
		try{
			if(data != null){
				String res = callService(address+"barrage/send" , data.toJSONString());
				if(TextUtils.isEmpty(res)){
					result.status = Constants.RES_FAIL;
				}else{
					JSONObject jsonRes = new JSONObject(res);
					if(jsonRes == null || !jsonRes.has("status")){
						result.status = Constants.RES_FAIL;
					}else{
						result.parseFromJSON(jsonRes);
					}
				}
			}
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 1.32 查询实时弹幕
	 * @param data
	 * @return
	 */
	public DanmuQueryResData m1_get_bullet(DanmuQueryData data){
		DanmuQueryResData result = new DanmuQueryResData();
		try{
			if(data != null){
				String res = callService(address+"barrage/get" , data.toJSONString());
				if(TextUtils.isEmpty(res)){
					result.status = Constants.RES_FAIL;
				}else{
					JSONObject jsonRes = new JSONObject(res);
					if(jsonRes == null || !jsonRes.has("status")){
						result.status = Constants.RES_FAIL;
					}else{
						result.parseFromJSON(jsonRes);
					}
				}
			}
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.37	 禁止弹幕
	 * @return
	 */
	public NetInterfaceStatusDataStruct m1_forbid_bullet(String videoID,String isOpen){
		NetInterfaceStatusDataStruct result = new NetInterfaceStatusDataStruct();
		
		try{
			JSONObject req = createReqJson(VERSION, "m1_forbid_bullet");
			req.put("videoID", videoID);
			req.put("isOpen", isOpen);
			String res = "";
			res = callService(address+"barrage/forbid", req.toString());
			result = parseResStr(res);
		}catch(Exception e){
			Log.e(e.getMessage(), e);
			result.setStatus(Constants.RES_FAIL);
		}
		return result;
	}
	/**
	 * 1.40	邀请随机观看
	 * 过滤掉自己的帐号
	 */
	public NetInterfaceStatusDataStruct m1_invite_random(String videoTitle){
		NetInterfaceStatusDataStruct result = null;
		String myaccount = AccountData.getInstance().getBindphonenumber();
		try{
			JSONObject req = createReqJson(VERSION, "m1_invite_random");
			//FIXME 文档里有，报文没有这个也可以使用，暂时屏蔽掉
			String res = "";
			res = callService(address+"video/visit", req.toString());
			result = parseResStr(res);
			List<String> list = new ArrayList<String>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("compList")){
				JSONArray userlist = ((JSONObject)result.getObj()).getJSONArray("userlist");
				int length = userlist.length();
				for(int i=0;i<length;i++){
					JSONObject obj = userlist.getJSONObject(i);
					if(obj.has("account")){
						String account = obj.getString("account");
						if(account.equals(myaccount)){
							continue;
						}else{
							list.add(account);
						}
					}
				}
			}
		}catch(Exception e){
			Log.e(e.getMessage(), e);
			result.setStatus(Constants.RES_FAIL);
		}
		return result;
	}
	/**
	 * 1.43 查询比赛视频
	 */
	public NetInterfaceStatusDataStruct m1_get_compVideoList() {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_compVideoList");
			
			//FIXME 测试
			String res = "";
			res = callService(address + "competition/get", req.toString());
			result = parseResStr(res);
			
//			getComp(result);
			
			ArrayList<CompData> list = new ArrayList<CompData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("compList")){
				JSONArray userlist = ((JSONObject)result.getObj()).getJSONArray("compList");
				int length = userlist.length();
				for(int i=0;i<length;i++){
					JSONObject obj = userlist.getJSONObject(i);
					CompData comp = new CompData();
					comp.parseFromJSON(obj);
					list.add(comp);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.44 查询指定类型比赛更多视频信息
	 */
	public NetInterfaceStatusDataStruct m1_get_CompDetailvideoList(String compId, int start, int count, String queryValue) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_CompDetailvideoList");
			req.put("compId", compId);
			req.put("start", start);
			req.put("count", count);
			req.put("queryValue", queryValue);
			
			//FIXME 测试
			String res = "";
			res = callService(address + "competition/getcompbyid", req.toString());
			result = parseResStr(res);
			
//			getTestVideo(result);
			
			ArrayList<VideoData> list = new ArrayList<VideoData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("videoList")){
				JSONArray userlist = ((JSONObject)result.getObj()).getJSONArray("videoList");
				int length = userlist.length();
				for(int i=0;i<length;i++){
					JSONObject obj = userlist.getJSONObject(i);
					VideoData video = new VideoData();
					video.parseFromJSON(obj);
					list.add(video);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.45 查询指定类型比赛排行榜
	 */
	public NetInterfaceStatusDataStruct m1_get_CompTopLine(String compId, int start, int count) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_CompTopLine");
			req.put("compId", compId);
			req.put("start", start);
			req.put("count", count);
			
			//FIXME 测试
			String res = "";
			res = callService(address + "competition/gettopline", req.toString());
			result = parseResStr(res);
			
//			getTestVideo(result);
			
			ArrayList<VideoData> list = new ArrayList<VideoData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("videoList")){
				JSONArray userlist = ((JSONObject)result.getObj()).getJSONArray("videoList");
				int length = userlist.length();
				for(int i=0;i<length;i++){
					JSONObject obj = userlist.getJSONObject(i);
					VideoData video = new VideoData();
					video.parseFromJSON(obj);
					list.add(video);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.46 获取频道列表，全量（客户端每天获取一次即可）
	 */
	public NetInterfaceStatusDataStruct m1_get_channels() {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_get_channels");
			//FIXME 测试
			String res = "";
			res = callService(address + "channel/getlist", req.toString());
			result = parseResStr(res);
//			result.setStatus(Constants.RES_FAIL);
			
			ArrayList<ChannelData> list = new ArrayList<ChannelData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("channelList")){
				JSONArray userlist = ((JSONObject)result.getObj()).getJSONArray("channelList");
				int length = userlist.length();
				for(int i=0;i<length;i++){
					JSONObject obj = userlist.getJSONObject(i);
					ChannelData channel = new ChannelData();
					channel.parseFromJSON(obj);
					list.add(channel);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.47 添加/修复备注姓名
	 */
	public NetInterfaceStatusDataStruct m1_add_nick(String optAccount, String name) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_add_nick");
			req.put("optAccount", optAccount);
			req.put("name", name);
			
			String res = callService(address + "user/updatenickinfo", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.48 获取备注
	 */
	public NetInterfaceStatusDataStruct m1_get_nick(String optAccount) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_get_nick");
			req.put("optAccount", optAccount);
			
			String res = callService(address + "user/getnickinfo", req.toString());
			result = parseResStr(res);
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& result.getObj() != null
					&& ((JSONObject)(result.getObj())).has("name")){
				result.setObj(((JSONObject)(result.getObj())).getString("name"));
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	private void getTestVideo(NetInterfaceStatusDataStruct result) throws JSONException{
		result.setStatus(Constants.RES_SUCCESS);
		JSONObject resobj = new JSONObject();
		JSONArray resarray = new JSONArray();
		for(int i=0;i<10;i++){
			JSONObject obj = new JSONObject();
			obj.put("videoID", ""+i);
			obj.put("account", ""+i);
			obj.put("nick", "白玫瑰"+i);
			obj.put("imageUrl", "http://g.hiphotos.baidu.com/image/pic/item/1f178a82b9014a90a08cd810aa773912b21beee9.jpg");
			obj.put("title", "街舞跳的真心棒"+i);
			obj.put("label", "");
			obj.put("videoImage", "http://h.hiphotos.baidu.com/image/pic/item/8718367adab44aedf1dea5eab11c8701a18bfb28.jpg");
			obj.put("type", 0);
			obj.put("recommandType", 0);
			obj.put("watchersNum", 1434+i);
			obj.put("bulletsNum", 141+i);
			obj.put("upNum", 12+i);
			obj.put("locationX", 116.329086+i);
			obj.put("locationY", 39.970861+i);
			obj.put("isLive", i%2 == 0 ? 1 : 0);
			obj.put("playUrl", "");
			obj.put("isBigVideo", i%5 == 0 ? 1 : 0);
			obj.put("viewurl", "");
			obj.put("bulletFile", "");
			obj.put("dateTime", "2015-02-03 12:13:13");
			obj.put("isPublic", 0);
			obj.put("isComp", 0);
			obj.put("compid", "1");
			obj.put("commentList", getTestCommentList());
			obj.put("likelist", getTestLikeList());
			resarray.put(obj);
		}
		resobj.put("videoList", resarray);
		result.setObj(resobj);
	}
	
	private JSONArray getTestCommentList() throws JSONException{
		JSONArray array = new JSONArray();
		for(int i=0;i<10;i++){
			JSONObject obj = new JSONObject();
			obj.put("commentID", "" + i);
			obj.put("commentAccount", "" + i);
			obj.put("nick", "白玫瑰"+i);
			obj.put("imageurl", "http://g.hiphotos.baidu.com/image/pic/item/1f178a82b9014a90a08cd810aa773912b21beee9.jpg");
			obj.put("content", "评论" + i);
			array.put(obj);
		}
		return array;
	}
	
	private JSONArray getTestLikeList() throws JSONException{
		JSONArray array = new JSONArray();
		for(int i=0;i<10;i++){
			JSONObject obj = new JSONObject();
			obj.put("likeAccount", "" + i);
			obj.put("nick", "白玫瑰"+i);
			obj.put("imageurl", "http://g.hiphotos.baidu.com/image/pic/item/1f178a82b9014a90a08cd810aa773912b21beee9.jpg");
			array.put(obj);
		}
		return array;
	}
	
	private void getPersonInfo(NetInterfaceStatusDataStruct result, String mobile) throws JSONException{
		result.setStatus(Constants.RES_SUCCESS);
		JSONObject resobj = new JSONObject();
		resobj.put("nickname", mobile);
		resobj.put("image", "http://g.hiphotos.baidu.com/image/pic/item/1f178a82b9014a90a08cd810aa773912b21beee9.jpg");
		resobj.put("sign", "一个承诺会让一个人悲伤，一个承诺会让一个人失落，不要轻易许诺，许下的诺言就是欠下的债");
		resobj.put("label", "90后");
		resobj.put("fansNum", 28);
		resobj.put("score", 60);
		resobj.put("focusNum", 110);
		resobj.put("location", "浙江省 杭州市");
		resobj.put("videoNums", 10);
		resobj.put("sex", 0);
		resobj.put("birthday", "1991-01-01");
		result.setObj(resobj);
	}
	
	private void getComp(NetInterfaceStatusDataStruct result) throws JSONException{
		result.setStatus(Constants.RES_SUCCESS);
		JSONObject resobj = new JSONObject();
		JSONArray comparray = new JSONArray();
		for(int j=0;j<4;j++){
			JSONObject comp = new JSONObject();
			comp.put("compName", "COS比赛" + j);//比赛名称
			comp.put("compType", "二次元" + j);//比赛所属频道
			comp.put("compNum", 32000);//比赛参加人数
			comp.put("compid", "二次元" + j);//比赛类型id
			comp.put("starttime", "2015-03-05");//比赛起始时间
			comp.put("endTime", "2016-03-05");//比赛结束时间
			comp.put("rule", "");//比赛规则
			JSONArray resarray = new JSONArray();
			for(int i=0;i<4;i++){
				JSONObject obj = new JSONObject();
				obj.put("vidoeID", ""+i);
				obj.put("account", ""+i);
				obj.put("nick", "白玫瑰"+i);
				obj.put("imageUrl", "http://g.hiphotos.baidu.com/image/pic/item/1f178a82b9014a90a08cd810aa773912b21beee9.jpg");
				obj.put("title", "街舞跳的真心棒"+i);
				obj.put("videoImage", "http://h.hiphotos.baidu.com/image/pic/item/8718367adab44aedf1dea5eab11c8701a18bfb28.jpg");
				obj.put("type", 0);
				obj.put("recommandType", 0);
				obj.put("watchersNum", 1434+i);
				obj.put("bulletsNum", 141+i);
				obj.put("upNum", 12+i);
				obj.put("locationX", 116.329086+i);
				obj.put("locationY", 39.970861+i);
				obj.put("isLive", i%2 == 0 ? 1 : 0);
				obj.put("playUrl", "");
				obj.put("isBigVideo", i%5 == 0 ? 1 : 0);
				obj.put("viewurl", "");
				obj.put("bulletFile", "");
				obj.put("dateTime", "2015-02-03 12:13:13");
				obj.put("isPublic", 0);
				obj.put("isComp", 0);
				obj.put("compid", "1");
				resarray.put(obj);
			}
			comp.put("videoList", resarray);
			comparray.put(comp);
		}
		resobj.put("compList", comparray);
		result.setObj(resobj);
	}
	
	/**
	 * 1.55 直播过程心跳包接口
	 */
	public NetInterfaceStatusDataStruct m1_send_ping(String videoID){
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_send_ping");
			req.put("videoID", videoID);
			String res = "";
			res = callService(address + "ping", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 1.56 取消赞接口
	 */
	public NetInterfaceStatusDataStruct m1_cancel_like(String videoID){
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_cancel_like");
			req.put("videoID", videoID);
			String res = "";
			res = callService(address + "video/cancelpraise", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 1.57  查询视频信息接口
	 * @param videoID
	 * @return
	 */
	public NetInterfaceStatusDataStruct m1_get_videoInfo(String videoID){
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_get_videoinfo");
			req.put("videoID", videoID);
			String res = "";
			res = callService(address + "video/getInfo", req.toString());
			result = parseResStr(res);
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& result.getObj() != null
					&& ((JSONObject)(result.getObj())).has("videoInfo")){
				JSONObject  data = ((JSONObject)(result.getObj())).getJSONObject("videoInfo");
				VideoData videoData = new VideoData();
				videoData.parseFromJSON(data);
				result.setObj(videoData);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			result.setStatus(Constants.RES_FAIL);
		}
		return result;
	}
	/**
	 * 1.58 是否保存录像接口
	 */
	public NetInterfaceStatusDataStruct m1_cancel_save(String videoID) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_cancel_save");
			req.put("videoID", videoID);
			String res = "";
			res = callService(address + "video/cancelsave", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 查询弹幕文件
	 * @param bulletFile
	 * @return
	 */
	public List<DanmuContentData> downloadDanmuFile(String bulletFile){
		List<DanmuContentData> result = new java.util.ArrayList<DanmuContentData>();
		String url =bulletFile;
		String res = callserviceNoCheck(url, "");
		if(StringUtils.isNull(res))
			return result;
		try{
			String[] contentStrings = res.split("\n");
			Log.i("downloadDanmuFile", res);
			if(contentStrings.length>0){
				int length = contentStrings.length;
				for(int i=0 ; i< length ; i++){
					String s = contentStrings[i];
					if("".equals(s.trim()))
						continue;
					if(s.endsWith(",")){
						s = s.substring(0, s.length()-1);
					}
					JSONObject ob = new JSONObject(s);
					DanmuContentData contentData = new DanmuContentData();
					contentData.parseFromJSON(ob);
					result.add(contentData);
				}
			}
			return result;
		}catch(Exception e){
			Log.e(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 1.59 删除视频
	 */
	public NetInterfaceStatusDataStruct m1_delete_video(String videoID) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_delete_video");
			req.put("videoID", videoID);
			String res = "";
			res = callService(address + "video/count", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.60 获取所有朋友圈回复消息
	 */
	public NetInterfaceStatusDataStruct m1_get_allreply() {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_get_allreply");
			
			String res = callService(address + "comments/becommented", req.toString());
			result = parseResStr(res);
			ArrayList<ReplyData> list = new ArrayList<ReplyData>();
			if(Constants.RES_SUCCESS.equals(result.getStatus())
					&& ((JSONObject)result.getObj()).has("replylist")){
				JSONArray fansList = ((JSONObject)result.getObj()).getJSONArray("replylist");
				int length = fansList.length();
				for(int i=0;i<length;i++){
					JSONObject obj = fansList.getJSONObject(i);
					ReplyData data = new ReplyData();
					data.parseFromJSON(obj);
					list.add(data);
				}
			}
			result.setObj(list);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.61 获取发布的视频数据
	 */
	public NetInterfaceStatusDataStruct m1_count_video(String account) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJson(VERSION, "m1_count_video");
			req.put("account", account);
			String res = "";
			res = callService(address + "video/count", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 1.62  找回密码接口
	 */
	public NetInterfaceStatusDataStruct m1_modify_pwd(String account, String password, String verify) {
		NetInterfaceStatusDataStruct result = null;
		try {
			JSONObject req = createReqJsonNoAccount(VERSION, "m1_modify_pwd");
			req.put("account", account);
//			req.put("password", MD5.bytes2hex(MD5.md5(password.getBytes(Constants.ENCODE))));
			req.put("password", password);//FIXME MD5
			req.put("verify", verify);
			//FIXME 测试用
			String res = "";
			res = callService(address + "resetpwd", req.toString());
			result = parseResStr(res);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 1.64根据视频ID获取HLS播放地址接口
	 */
	public NetInterfaceStatusDataStruct m1_get_videoHls(String videoId){
		NetInterfaceStatusDataStruct result = null;
		try{
			JSONObject req = createReqJson(VERSION, "m1_get_videoinfo");
			req.put("videoID" , videoId);
			String reString = callService(address+"video/gethls", req.toString());
			result = parseResStr(reString);
			JSONObject jsonRes = (JSONObject) result.getObj();
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				VideoHlsData hlsData = new VideoHlsData();
				hlsData.parseFromJSON(jsonRes);
				result.setObj(hlsData);
			}
		} catch(Exception e){
			Log.e(e.getMessage(), e);
		}
		return result;
	}
}