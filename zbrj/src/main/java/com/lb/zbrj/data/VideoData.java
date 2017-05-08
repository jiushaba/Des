package com.lb.zbrj.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONObject;

import com.lb.common.util.Log;

public class VideoData implements Serializable {
	
	private String cacheKey;// 缓存ID
	public String sub_type ; //1：发 动态 ；2：评论；3：点赞；4 ：取消赞；5 回复
	public String post_id ; //操作ID
	public String operator;//
	public String opnick;//
	public String opimageurl;//
	public String optime;
	public String states= "0";//0 未读， 1已读
	public boolean isShowAllComment = false;
	public int expandCommentIdx = -1;
	
	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String videoID          ;//视频id                                                                                                                     
	public String account          ;//视频归属账号                                                                                                               
	public String nick             ;//视频归属人昵称                                                                                                             
	public String imageUrl         ;//视频归属人头像url                                                                                                          
	public String title            ;//视频标题               
	public String label;//视频标签
	public String videoImage;//视频缩略图url
	public int    type             ;//视频所属类型                  
	public int    recommandType    ;//视频推荐类型，0表示普通，不做任何处理，1表示推荐，在视频图片右下角加个“荐”字，2表示点播或直播人数多，在右下角加个“热”字
	public int    watchersNum      ;//观看人数                                                                                                                   
	public int    bulletsNum       ;//弹幕数                                                                                                                     
	public int    upNum            ;//赞数目                                                                                                                     
	public double locationX        ;//视频录制人X坐标                                                                                                            
	public double locationY        ;//视频录制人Y坐标                                                                                                            
	public int    isLive           ;//1表示直播，0表示录播                                                                                                       
	public String playUrl          ;//播放url
	public int isBigVideo          ;//是否需要特殊处理列表项，是1，不是0
	public String viewUrl          ;//当isbigvideo为1时，此项为预览视频url
	public int isPublic;//是否公开，0不是，1是，此属性仅当isLive为1的时候考虑
	public int isComp;//是否比赛视频
	public String compid;//比赛id，仅当isComp为1的时候有效
	public String bulletFile          ;//弹幕文件Url
	public String dateTime          ;//发布日期，格式yyyy-MM-DD hh:mm:Ss
	public String watchTime;//yyyy-MM-dd hh:mm:ss
	public int osType; //操作类型 1 android ,0 ios
	public ArrayList<CommentData> comments = new ArrayList<CommentData>();
	public ArrayList<LikeData> likes = new ArrayList<LikeData>();
	
	public void parseFromJSON(JSONObject json){
		try{
			if(json.has("videoID"))videoID = json.getString("videoID");
			if(json.has("account"))account = json.getString("account");
			if(json.has("nick"))nick = json.getString("nick");
			if(json.has("imageUrl"))imageUrl = json.getString("imageUrl");
			if(json.has("title"))title = json.getString("title");
			if(json.has("label"))label = json.getString("label");
			if(json.has("videoImage"))videoImage = json.getString("videoImage");
			if(json.has("type"))type = json.getInt("type");
			if(json.has("recommandType"))recommandType = json.getInt("recommandType");
			if(json.has("watchersNum"))watchersNum = json.getInt("watchersNum");
			if(json.has("bulletsNum"))bulletsNum = json.getInt("bulletsNum");
			if(json.has("upNum"))upNum = json.getInt("upNum");
			if(json.has("locationX"))locationX = json.getDouble("locationX");
			if(json.has("locationY"))locationY = json.getDouble("locationY");
			if(json.has("isLive"))isLive = json.getInt("isLive");
			if(json.has("playUrl"))playUrl = json.getString("playUrl");
			if(json.has("isBigVideo"))isBigVideo = json.getInt("isBigVideo");
			if(json.has("viewUrl"))viewUrl = json.getString("viewUrl");
			if(json.has("isPublic"))isPublic = json.getInt("isPublic");
			if(json.has("isComp"))isComp = json.getInt("isComp");
			if(json.has("compid"))compid = json.getString("compid");
			if(json.has("bulletFile"))bulletFile = json.getString("bulletFile");
			if(json.has("dateTime"))dateTime = json.getString("dateTime");
			if(json.has("osType")) osType = json.getInt("osType");
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
	}
}