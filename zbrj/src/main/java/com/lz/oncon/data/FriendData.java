package com.lz.oncon.data;

import java.io.Serializable;

import android.graphics.Bitmap;

public class FriendData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5369785574311629274L;
	private String contactid;
	private String contactName;
	private String mobile;
	private Bitmap headpic;
	private String index;
	public String email;
	public boolean checked = true;
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public Bitmap getHeadpic() {
		return headpic;
	}
	public void setHeadpic(Bitmap headpic) {
		this.headpic = headpic;
	}
	public String getContactid() {
		return contactid;
	}
	public void setContactid(String contactid) {
		this.contactid = contactid;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof FriendData){
			FriendData fd = (FriendData)o;
			if(fd.getMobile().equals(mobile)){
				return true;
			}else{
				return false;
			}
		}
		return super.equals(o);
	}
	
	
}
