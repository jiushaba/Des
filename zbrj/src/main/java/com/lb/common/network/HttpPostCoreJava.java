package com.lb.common.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.lb.common.util.Log;

import com.lb.common.util.Constants;

/**
 *
 */
public class HttpPostCoreJava {

	private ByteArrayOutputStream baw = null;
	private InputStream inputStream = null;
	private OutputStream postOutputStream = null;
	private HttpURLConnection connection;
	
	/** 
	* Description: 上传企业信息
	* @param url 
	* @param value 请求报文
	* @return     
	* String 
	*/
	public String sendPostEnterpirse(String url,String value){     
		String responseStr = null;
		try {
		HttpPost request = new HttpPost(url);
		// 绑定到请求 Entry
		StringEntity se = new StringEntity(value,"UTF-8"); 
		request.setEntity(se);
		HttpClient client = new DefaultHttpClient();
        // 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
        // 读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
		// 发送请求
		HttpResponse httpResponse = client.execute(request);
		// 得到应答的字符串，这也是一个 JSON 格式保存的数据
		responseStr = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseStr;
	}
	
	/** 
	* Description: 从本地向服务器上传通讯录
	* @param url 上传地址
	* @param value 报文内容
	* @return     
	* String 
	*/
	public String syncPost(String url,String value) 
	 {     
		String responseStr = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> parms = new ArrayList<NameValuePair>();
		parms.add(new BasicNameValuePair("info", value));
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(parms, "UTF-8");
			post.setEntity(entity);
			ResponseHandler<String> handler = new ResponseHandler<String>() {
			    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			        HttpEntity entity = response.getEntity();
			        if (entity != null) {
			            return EntityUtils.toString(entity);
			        } else {
			            return null;
			        }
			    }
			};
			responseStr = client.execute(post, handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseStr;
	}


	
	/**
	 * 向指定URL发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param requestConnect
	 *            post的数据body
	 * @return String 返回的网络数据
	 */
	
	public String sendPost(String url, byte[] requestContent, int timeout,boolean is_lz) {

		String result = "";
		if(timeout <= 0){
			timeout = 30 * 1000;
		}
		try {
			String urlName = url;
			URL realUrl = new URL(urlName);
			// 1.设置超时
			// 设置连接主机的超时时间,(单位毫秒)
			// System.setProperty("sun.net.client.defaultConnectTimeout",
			// "30000");
			// //设置从主机读取数据的超时时间,(单位毫秒)
			// System.setProperty("sun.net.client.defaultReadTimeout", "30000");

			// 2.初始化连接
			// 打开和URL之间的连接
			connection = (HttpURLConnection) realUrl.openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			// post方法需要设置doinput和doOutput为true
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// 设置http header
			// connection.setRequestProperty("Accept", "text/xml");
			connection.setRequestProperty("Content-Length", "" + requestContent.length);
			if(!is_lz){
				connection.setRequestProperty("content-type",Constants.HTTP_CONTENT_TYPE_TEXT);
			}

			// 3.建立连接，发送输出流
			// 建立输出流，发送post数据
			postOutputStream = connection.getOutputStream();
			
			postOutputStream.write(requestContent);
			
			postOutputStream.flush(); // 把流缓冲区中的当前全部内容强制输出
			// 4.得到服务端回复
			// 得到http的响应，如果responseCode=HttpConnection.HTTP_OK，即请求成功
			// int responseCode = connection.getResponseCode();
			// String responseMessage = connection.getResponseMessage();
			// 得到网络数据流
			inputStream = connection.getInputStream();

			// 5.对获取的数据流进行处理
			byte[] b = new byte[1024];
			int length = 0;
			baw = new ByteArrayOutputStream();
			while ((length = inputStream.read(b)) > 0) {
				baw.write(b, 0, length);
			}
			result += baw.toString();
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		// 6.使用finally块来关闭输入流
		finally {
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
