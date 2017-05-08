package com.lb.common.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.lb.common.util.Log;

import com.lb.common.util.Constants;

/**
 *
 */
public class HttpsPostCoreJava {

	private HttpsURLConnection conn;
	private OutputStream postOutputStream;
	private InputStream inputStream;
	private ByteArrayOutputStream baw;
	
	/**
	 * 向指定URL发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param requestConnect
	 *            post的数据body
	 * @return String 返回的网络数据
	 */
	
	public String sendPost(String url, byte[] requestContent, int timeout,boolean is_lz){
		StringBuffer sb = new StringBuffer(); 
		 try{
			 SSLContext sc = SSLContext.getInstance("TLS"); 
             sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
             HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory()); 
             HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
             conn = (HttpsURLConnection)new URL(url).openConnection();
             conn.setConnectTimeout(timeout);
             conn.setReadTimeout(timeout);
             conn.setDoOutput(true);
             conn.setDoInput(true);
             conn.setRequestProperty("Content-Length", "" + requestContent.length);
             if(!is_lz){
            	 conn.setRequestProperty("content-type",Constants.HTTP_CONTENT_TYPE_TEXT);
             }
             conn.connect();
             postOutputStream = conn.getOutputStream();
             postOutputStream.write(requestContent);
             postOutputStream.flush();
             inputStream = conn.getInputStream();
             byte[] b = new byte[1024];
             int length = 0;
             baw = new ByteArrayOutputStream();
             while ((length = inputStream.read(b)) > 0) {
            	 baw.write(b, 0, length);
             }
             sb.append(baw.toString());
		 }	catch(Exception e){ 
             Log.e(Constants.LOG_TAG, e.getMessage(), e);
		 } finally{
			 close();
		 }
		 return sb.toString();
	}

	public void close() {
		try {
			if(postOutputStream != null){
				postOutputStream.close();
			}
			if(baw != null){
				baw.close();
			}
			if(inputStream != null){
				inputStream.close();
			}
			if(conn != null){
				conn.disconnect();
			}
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	private class MyHostnameVerifier implements HostnameVerifier{
        @Override
        public boolean verify(String hostname, SSLSession session) {
        	return true;
         }
	}
	
	private class MyTrustManager implements X509TrustManager{
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
        @Override
        public X509Certificate[] getAcceptedIssuers() {
        	return null;
        }        
	}  
}
