package com.lz.oncon.listener;

public interface AppNotiListener {
	public void recvAppNoti(String app_id, String content);
	public void delAppNoti(String app_id);
}
