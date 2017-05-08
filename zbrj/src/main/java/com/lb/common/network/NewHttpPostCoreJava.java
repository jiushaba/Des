package com.lb.common.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lb.common.util.Log;

import com.lb.common.util.Constants;

public class NewHttpPostCoreJava {

	private ByteArrayOutputStream baw = null;
	private InputStream inputStream = null;
	private OutputStream postOutputStream = null;
	private HttpURLConnection connection;
	/**
	 * 向指定URL发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param requestConnect
	 *            post的数据body
	 * @return String 返回的网络数据
	 */
	
	public String sendPost(String url, byte[] requestContent, int timeout) {

		String result = "";
		if(timeout <= 0){
			timeout = 30 * 1000;
		}
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			postOutputStream = connection.getOutputStream();
			postOutputStream.write(requestContent);
			postOutputStream.flush(); // 把流缓冲区中的当前全部内容强制输出

			inputStream = connection.getInputStream();

			byte[] b = new byte[1024];
			int length = 0;
			baw = new ByteArrayOutputStream();
			while ((length = inputStream.read(b)) > 0) {
				baw.write(b, 0, length);
			}
			result += baw.toString();
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} finally {
			close();
		}
		return result;
	}

	public void close() {
		try {
			if(connection != null){
				connection.disconnect();
			}
			if (baw != null) {
				baw.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (postOutputStream != null) {
				postOutputStream.close();
			}
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
}
