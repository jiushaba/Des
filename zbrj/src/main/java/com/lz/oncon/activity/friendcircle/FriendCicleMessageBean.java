package com.lz.oncon.activity.friendcircle;
/**
 * 消息提醒实体Bean 
 * @author Administrator
 *
 */
public class FriendCicleMessageBean {
	
//	public String toMobile = "";
//	public String subtype="";//subtype：操作类型，1：发 动态 ；2：评论；3：点赞；4 ：取消赞；5 回复
//	public String post_id = "";//post_id：帖子id 
//	public String post_content ="";//post_content: 帖子内容（base64位编码），当 subtype为1时，post_content为空，其余情况不能为空
//	public String operator = "";//operator：操作者，即评论者或点赞者或取消点赞者
//	public String optime ="";//optime：操作时间，自1970年起 的毫秒数
	
	public String operator_mobile;//获取头像和名字
	public String operator_content;//只是文本
	public String operator_action = "0";//点赞 1为点赞 
	public String operator_time = "";//时间 
	public String post_original_text;//帖子原文
	public String post_original_image_url;//图片URL
	

}
