package com.lz.oncon.activity.friendcircle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 动态 -包括 图片资源、评论资源、点赞资源
 *
 */
public class Source_Dynamic implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id; // 动态id
	private String resId; //资源id
	private String resType; //资源类型
	private String title; //资源标题
	private String userId; //资源归属用户id
	private String nickName; //资源归属用户昵称
	private String source; // 资源发布来源
	private String mobile; // 手机号码
	private String createTime; // 创建时间
	private ArrayList<Source_Photo> list_photo; // 图片集合
	private ArrayList<Source_Comment> list_comment; // 最近评论
	private ArrayList<Source_Up> list_up; // 最近点赞
	private String detail; // 详细（博客内容）
	private String likeNum; // 点赞数
	private String cacheKey;// 缓存ID
	/**
	 * 协议后加
	 */
	private String singer;//歌曲演唱者
	private String commentNum;//动态评论总数
	private String location;//所在位置
	private String link;//分享连接
	private String icon;//分享图标连接
	private String shareContent;//分享内容概要
	private String shareType;//1为图文分享(忽略)
	private String postType;//1为普通图文2为分享图文
	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}
	public String getCacheKey() {
		return cacheKey;
	}
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
	public String getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getResId() {
		return resId;
	}
	public void setResId(String resId) {
		this.resId = resId;
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public ArrayList<Source_Photo> getList_photo() {
		return list_photo;
	}
	public void setList_photo(ArrayList<Source_Photo> list_photo) {
		this.list_photo = list_photo;
	}
	public ArrayList<Source_Comment> getList_comment() {
		return list_comment;
	}
	public void setList_comment(ArrayList<Source_Comment> list_comment) {
		this.list_comment = list_comment;
	}
	public ArrayList<Source_Up> getList_up() {
		return list_up;
	}
	public void setList_up(ArrayList<Source_Up> list_up) {
		this.list_up = list_up;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getLikeNum() {
		return likeNum;
	}
	public void setLikeNum(String likeNum) {
		this.likeNum = likeNum;
	}
	
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(String commentNum) {
		this.commentNum = commentNum;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getShareContent() {
		return shareContent;
	}
	public void setShareContent(String shareContent) {
		this.shareContent = shareContent;
	}
	public String getShareType() {
		return shareType;
	}
	public void setShareType(String shareType) {
		this.shareType = shareType;
	}


	
	
	/**
	 * 保存数据库,朋友圈提醒
	 */
	public String sub_type ; //1：发 动态 ；2：评论；3：点赞；4 ：取消赞；5 回复
	public String post_id ; //操作ID
//	public String post_conrtent;//json  串
	public String operator;//获取头像和名字
	public String optime;
	public String states= "0";//0 未读， 1已读
	
	@Override
	public String toString() {
		return "Source_Dynamic [id=" + id + ", resId=" + resId + ", resType=" + resType + ", title=" + title + ", userId=" + userId + ", nickName=" + nickName + ", source=" + source + ", mobile=" + mobile + ", createTime=" + createTime + ", list_photo=" + list_photo + ", list_comment=" + list_comment + ", list_up=" + list_up + ", detail=" + detail + ", likeNum=" + likeNum + ", cacheKey=" + cacheKey + ", sub_type=" + sub_type + ", post_id=" + post_id + ", operator=" + operator + ", optime=" + optime + ", states=" + states + "]";
	}
	
}
