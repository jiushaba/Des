package com.lb.common.network;

public class NetworkPostCore {
	NewHttpPostCoreJava newHttp;
	NewHttpsPostCoreJava newHttps;
	public String sendPost(String url, byte[] requestContent, int timeout){
		if(url.startsWith("https:")){
			newHttps = new NewHttpsPostCoreJava();
			return newHttps.sendPost(url, requestContent, timeout);
		}else{
			newHttp = new NewHttpPostCoreJava();
			return newHttp.sendPost(url, requestContent, timeout);
		}
	}
	public void close(){
		if(newHttp != null){
			newHttp.close();
		}
		if(newHttps != null){
			newHttps.close();
		}
	}
}
