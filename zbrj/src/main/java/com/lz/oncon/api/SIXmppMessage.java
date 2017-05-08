package com.lz.oncon.api;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.lz.oncon.api.core.im.data.IMDataDB;

public class SIXmppMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FILE_TEMP_DIC = IMDataDB.FILE_TEMP_DIC;
	
	/**
	 * 消息类型
	 * @author Administrator
	 *
	 */
	public enum ContentType{
		TYPE_TEXT,//文本消息
		TYPE_IMAGE,//图片消息
		TYPE_AUDIO,//录音消息
		TYPE_NEWS,//新闻消息
		TYPE_LOC,//坐标位置消息
		TYPE_DYN_EXP,//动态表情消息
		TYPE_TALK_PIC,//会说话的图片
		TYPE_SNAP_PIC,//闪图
		TYPE_INTERCOM,//语音对讲
		TYPE_APP_MSG,//应用消息
		TYPE_FILE,//文件消息
		TYPE_SYSTEM,//系统消息
		TYPE_GROUP_SYS_NOTI,//圈子系统通知
		TYPE_PUBLICACCOUNT_SYS_NOTI,//公众账号关注/取消关注
		TYPE_MUSIC,//音乐消息
		TYPE_APP_NOTI,//应用提醒
		TYPE_HTML_TEXT,//HTML文本
		TYPE_HTML_TEXT_2,//新的HTML文本(手机看帖功能使用)
		TYPE_PUBLICACCOUNT_NAMECARD,//服务号名片
		TYPE_IMAGE_TEXT,//图文分享消息
		TYPE_FRIENDCIRCLE_NOTI,//朋友圈提醒
		TYPE_UNKNOWN,//未知类型
		TYPE_HTML_TEXT_GENERAL,//通用HTML文本
		TYPE_VIDEO_CONF,//视频会议
		TYPE_LINK_MSG,//链接消息
		TYPE_CUSTOM_PROTOCOL,//自定义协议
	}
	
	public enum SourceType{
		/**
		 * 发送的消息
		 */
		SEND_MESSAGE,
		/**
		 * 接收的消息
		 */		
		RECEIVE_MESSAGE,
		/**
		 * 系统消息
		 */
		SYSTEM_MESSAGE,
	}
	
	/**
	 * 发送状态,接收的消息不关心发送状态
	 * @author Administrator
	 *
	 */
	public enum SendStatus{
		/**
		 * 已发送
		 */
		STATUS_SENT,
		/**
		 * 未发送
		 */
		STATUS_DRAFT,
		/**
		 * 已送达
		 */
		STATUS_ARRIVED,
		/**
		 * 发送失败
		 */
		STATUS_ERROR,
		/**
		 * 已读
		 */
		STATUS_READED,
		/**
		 * 无状态
		 */
		STATUS_NULL,
	}
	
	/**
	 * 消息发送来源的设备或者版本
	 * @author Administrator
	 *
	 */
	public enum Device{
		/**
		 * iphone或者通用的ios设备
		 */
		DEVICE_IPHONE,
		/**
		 * android
		 */
		DEVICE_ANDROID,
		/**
		 * windows pc
		 */
		DEVICE_WINDOWS,
		/**
		 * IPOD TOUCH
		 */
		DEVICE_IPOD_TOUCH,
		/**
		 * IPAD
		 */
		DEVICE_IPAD,
		/**
		 * MAC
		 */
		DEVICE_MAC,
		/**
		 * unknown
		 */
		DEVICE_UNKNOWN,
	}
	
	public enum LinkMsgType{
		FRIEND,//好友
		CIRCLE,//人脉圈
	}
	
	private String from;
	private String to;
	private ContentType contentType;
	private SourceType sourceType;
	private long time;
	private String textContent;
	
	private String imageFileId;
	private String imageName;
	private int imageWidth;
	private int imageHeight;
	private String imagePath;
	private String imageURL;
	private long imageFileSize;
	
	private String thumbnailFileId;
	private String thumbnailPath;
	private String thumbnailURL;
	
	private String audioFileId;
	private String audioName;
	private String audioPath;
	private String audioURL;
	private int audioTimeLength;
	private long audioFileSize;
	
	private SendStatus status = SendStatus.STATUS_DRAFT;
	private SendStatus oldStatus = SendStatus.STATUS_DRAFT;
	private Device device;
	private String id;
	public String tId;
	private String nickname;
	private int snapTime;
	private String onconActive = "1";
	private String newMsgFlag = "0";
	private String onconArrived = "0";
	public String read_ids;
	public String noread_count;
	
	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	
	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}
	
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getAudioURL() {
		return audioURL;
	}

	public void setAudioURL(String audioURL) {
		this.audioURL = audioURL;
	}
	
	public int getAudioTimeLength() {
		return audioTimeLength;
	}

	public void setAudioTimeLength(int audioTimeLength) {
		this.audioTimeLength = audioTimeLength;
	}
	
	public ArrayList<String> getReadlist() {
		ArrayList<String> readlist = new ArrayList<String>();
		try{
			if(!TextUtils.isEmpty(read_ids)){
				JSONObject obj = new JSONObject(read_ids);
				if(obj.has("readlist") && !obj.isNull("readlist") && obj.getJSONArray("readlist").length() > 0){
					JSONArray readids = obj.getJSONArray("readlist");
					for(int i=0;i<readids.length();i++){
						readlist.add(readids.getString(i));
					}
				}
			}
		}catch(Exception e){
		}
		return readlist;
	}
	
	public ArrayList<String> getNoreadlist() {
		ArrayList<String> noreadlist = new ArrayList<String>();
		try{
			if(!TextUtils.isEmpty(read_ids)){
				JSONObject obj = new JSONObject(read_ids);
				if(obj.has("noreadlist") && !obj.isNull("noreadlist") && obj.getJSONArray("noreadlist").length() > 0){
					JSONArray readids = obj.getJSONArray("noreadlist");
					for(int i=0;i<readids.length();i++){
						noreadlist.add(readids.getString(i));
					}
				}
			}
		}catch(Exception e){
		}
		return noreadlist;
	}
	
	public String getOnconArrived() {
		return onconArrived;
	}
	public void setOnconArrived(String onconArrived) {
		this.onconArrived = onconArrived;
	}
	public String getNewMsgFlag() {
		return newMsgFlag;
	}
	public void setNewMsgFlag(String newMsgFlag) {
		this.newMsgFlag = newMsgFlag;
	}
	public String getOnconActive() {
		return onconActive;
	}
	public void setOnconActive(String onconActive) {
		this.onconActive = onconActive;
	}
	public int getSnapTime() {
		return snapTime;
	}
	public void setSnapTime(int snapTime) {
		this.snapTime = snapTime;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public ContentType getContentType() {
		return contentType;
	}
	public void setContentType(ContentType type) {
		this.contentType = type;
	}
	public SourceType getSourceType() {
		return this.sourceType;
	}
	public void setSourceType(SourceType type) {
		this.sourceType= type;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getAudioName() {
		return audioName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}	
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}	
	public String getAudioPath() {
		return audioPath;
	}
	public void setAudioName(String audioName) {
		this.audioName = audioName;
	}
	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}
	public String getImageFileId() {
		return imageFileId;
	}
	public void setImageFileId(String imageFileId) {
		this.imageFileId = imageFileId;
	}
	public String getAudioFileId() {
		return audioFileId;
	}
	public void setAudioFileId(String audioFileId) {
		this.audioFileId = audioFileId;
	}
	public String getThumbnailFileId() {
		return thumbnailFileId;
	}
	public void setThumbnailFileId(String thumbnailFileId) {
		this.thumbnailFileId = thumbnailFileId;
	}
	public long getImageFileSize() {
		return imageFileSize;
	}
	public void setImageFileSize(long imageFileSize) {
		this.imageFileSize = imageFileSize;
	}
	public long getAudioFileSize() {
		return audioFileSize;
	}
	public void setAudioFileSize(long audioFileSize) {
		this.audioFileSize = audioFileSize;
	}
	public SendStatus getStatus() {
		return status;
	}
	public void setStatus(SendStatus status) {
		this.status = status;
	}
	public SendStatus getOldStatus() {
		return oldStatus;
	}
	public void setOldStatus(SendStatus oldStatus) {
		this.oldStatus = oldStatus;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	
	public enum DownloadType{
		TYPE_IMAGE,
		TYPE_THUMBNAIL,
		TYPE_AUDIO
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof SIXmppMessage){
			SIXmppMessage com = (SIXmppMessage)o;
			return this.getId().equals(com.getId());
		}
		return super.equals(o);
	}
	@Override
	public String toString() {
		return "SIXmppMessage [from=" + from + ", to=" + to + ", contentType="
				+ contentType + ", time=" + time + ", textContent="
				+ textContent + ", status=" + status + ", device=" + device
				+ ", id=" + id + "]";
	}
}
