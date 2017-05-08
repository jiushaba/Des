package com.lz.oncon.api;


public class SIXmppAccout {
	private String username;
	private String password;
	private String resource;
	public boolean auth = false;
	
	public SIXmppAccout() {
	}
	
	public SIXmppAccout(String username,String password,String resource){
		setAccoutInfo(username, password, resource);
	}
	
	/**
	 * 设置用户名，密码，资源
	 * @param username
	 * @param password
	 * @param resource
	 */
	public void setAccoutInfo(String username,String password,String resource){
		this.username = username;
		this.password = password;
		this.resource = resource;
	}
	
	/**
	 * 获取用户名
	 * @return
	 */
	public String getUsername(){
		return username;
	}
	
	/**
	 * 获取密码
	 * @return
	 */
	public String getPassword(){
		return password;
	}
	
	/**
	 * 获取资源
	 * @return
	 */
	public String getResouce(){
		return resource;
	}
	
	/**
	 * 设置服务器地址
	 * @param serviceName
	 */
//	public void setServiceName(String serviceName){
//		this.serviceName = serviceName;
//	}
	
	/**
	 * 获取服务器地址
	 * @return
	 */
//	public String getServiceName(){
//		return serviceName;
//	}
	
}
