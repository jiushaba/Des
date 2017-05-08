package com.lz.oncon.activity.friendcircle;

import java.io.Serializable;

/**
 * 图片资源
 * @author yao
 *
 */
public class Source_Photo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String mid; // 中图
	public String src; // 原图
	public String small; // 小图
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getSmall() {
		return small;
	}
	public void setSmall(String small) {
		this.small = small;
	}
	
}
