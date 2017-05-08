package com.lz.oncon.api.core.im.data;
/**
 * 用于 同步最近7天聊天记录   数据组装
 * time形如：
 * 		setter:yyyy-MM-dd HH:mm:ss    getter: return 毫秒
 * body形如：
 * 		m1_extend_msg@@@lz-oncon@@@v1.0|||type=2|||coor=xxx|||long=xxx|||lat=xxx|||loc=xxx|||subtype=1
 * from、to形如：
 * 		im登录帐号（手机号）
 * messageId形如：默认
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageForTxtFile {
	
	public String time;
	public String messageId;
	public String from;
	public String to;
	public String body;
	public long getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
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
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	

}
