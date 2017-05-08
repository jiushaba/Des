package com.lz.oncon.api;

/**
 * 连接状态监听器接口
 * @author Administrator
 *
 */
public interface SIXmppConnectionListener{
	
	public static final int SUCCESS = 0;
	public static final int FAILED = 1;
	public static final int CONNECTTING = 2;
	
	/**
	 * 登录成功的回调,SUCCESS,FAILED,CONNECTTING
	 */
	public void loginStatusChanged(int status);
	
}	