package com.lz.oncon.activity.friendcircle;

import java.io.Serializable;

/**
 * 评论资源
 * @author yao
 *
 */
public class Source_Comment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 998759472704313799L;
	private String id; //Mongo id
	private String type; //评论类型0为评论1为回复
	private String content; //评论内容
	private String source; //评论来源
	private String userId; //评论用户id
	private String mobile; //评论者手机号码
	private String commentTopicEntity; //资源实体
	private String createTime; //评论时间
	private String resId; //资源id
	private String resUserId; //资源所属用户id
	private String topicId; //所属资源评论id
	private String toUserId; //回复引用的评论者id
	private String resType; //评论资源类型 地盘：song/video/album/photo/blog/songAlbum 部落：clubPhoto,clubSubject
	private String parentId; //回复引用的评论id
	private String dnamicType; //动态类型:friendsCircle
	private String feedId; // 动态id
	private String toMobile; //回复引用评论者手机号码
	
	
	private String module; //评论所属模块null为地盘、club为部落
	private String nickName; //评论用户昵称
	private String belongId; //所属模块具体类的id 地盘：null 部落：部落id
	private String commentAvatar; //评论者用户头像
	private String toNickName; // 回复引用评论者昵称
	private String belongTitle; //所属模块具体类名称 地盘：null 部落：部落名称
	
	
	public String getToMobile() {
		return toMobile;
	}
	public void setToMobile(String toMobile) {
		this.toMobile = toMobile;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCommentTopicEntity() {
		return commentTopicEntity;
	}
	public void setCommentTopicEntity(String commentTopicEntity) {
		this.commentTopicEntity = commentTopicEntity;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getResId() {
		return resId;
	}
	public void setResId(String resId) {
		this.resId = resId;
	}
	public String getResUserId() {
		return resUserId;
	}
	public void setResUserId(String resUserId) {
		this.resUserId = resUserId;
	}
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public String getToUserId() {
		return toUserId;
	}
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getDnamicType() {
		return dnamicType;
	}
	public void setDnamicType(String dnamicType) {
		this.dnamicType = dnamicType;
	}
	public String getFeedId() {
		return feedId;
	}
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
	
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getBelongId() {
		return belongId;
	}
	public void setBelongId(String belongId) {
		this.belongId = belongId;
	}
	public String getCommentAvatar() {
		return commentAvatar;
	}
	public void setCommentAvatar(String commentAvatar) {
		this.commentAvatar = commentAvatar;
	}
	public String getToNickName() {
		return toNickName;
	}
	public void setToNickName(String toNickName) {
		this.toNickName = toNickName;
	}
	public String getBelongTitle() {
		return belongTitle;
	}
	public void setBelongTitle(String belongTitle) {
		this.belongTitle = belongTitle;
	}
}
