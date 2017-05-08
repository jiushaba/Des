/**
 * apache的http接口封装
 */
package com.lb.common.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.lb.common.util.Log;

import com.lb.common.util.Constants;

public class HttpCoreApache {

	private static HttpClient client;
	private static long content_length = 0l;
	public static long length = 0L;

	private static HttpClient getHttpClient() {
		if (null == client) {
			HttpParams pramas = new BasicHttpParams();
			ConnManagerParams.setTimeout(pramas, 30000);
			HttpConnectionParams.setConnectionTimeout(pramas, 30000);
			HttpConnectionParams.setSoTimeout(pramas, 20000);
			client = new DefaultHttpClient(pramas);
		}
		return client;
	}

	/**
	 * 
	 * @param urlString
	 * @param range
	 * @return
	 */
	public static InputStream sendGetInputStream(String urlString, long range) {
		String urlName = urlString;
		InputStream resultStream = null;
		if (urlName != null) {
			HttpClient client = getHttpClient();
			// 建立GET请求的连接
			HttpGet httpGetRequest = new HttpGet(urlName);
			// 设置断点续传包头
			if (range < 0) {
			} else {
				httpGetRequest.addHeader("Range", "bytes=" + range + "-");
			}
			try {
				// 向指定的URL发送请求，并获得返回数据
				HttpResponse responseStr = client.execute(httpGetRequest);
				int statusCode = responseStr.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_PARTIAL_CONTENT
						&& statusCode != HttpStatus.SC_OK) {
					// throw new RuntimeException("请求失败");
					httpGetRequest.abort();
					return null;
				}
				// 如果正确的返回 ，获取content_length的值
				if (range == 0) {
					Header[] header = responseStr.getAllHeaders();
					for (int i = 0; i < header.length; i++) {
						if (header[i].getName().equals("Content-Length")) {
							content_length = Long.parseLong(header[i]
									.getValue());
							setContentLength(content_length);
						}
					}
				}
				// 获取网络数据流
				HttpEntity httpEntity = responseStr.getEntity();
				resultStream = httpEntity.getContent();
				if (resultStream != null) {
					return resultStream;
				}
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * 向指定的URL发送get请求
	 * 
	 * @param urlString
	 *            发送请求的URL
	 * @return 返回byte数组
	 */
	public static byte[] sendGet(String urlString, long range) {
		String urlName = urlString;
		BufferedInputStream bufferedInput = null;
		ByteArrayOutputStream out = null;

		if (urlName != null) {
			HttpClient client = getHttpClient();
			// 建立GET请求的连接
			HttpGet httpGetRequest = new HttpGet(urlName);
			// 设置断点续传包头
			if (range < 0) {
			} else if (range == 0) {
				httpGetRequest.addHeader("Range", "bytes=" + range + "-");
			} else {
				httpGetRequest.addHeader("Range", "bytes=" + (range - 1) + "-");
			}
			try {
				// 向指定的URL发送请求，并获得返回数据
				HttpResponse responseStr = client.execute(httpGetRequest);
				if (responseStr.getStatusLine().getStatusCode() != HttpStatus.SC_PARTIAL_CONTENT) {
					throw new RuntimeException("请求失败");
					// httpGetRequest.abort();
				}
				// 如果正确的返回
				if (range == 0) {
					Header[] header = responseStr.getAllHeaders();
					for (int i = 0; i < header.length; i++) {
						if (header[i].getName().equals("Content-Length")) {
							content_length = Long.parseLong(header[i]
									.getValue());
						}
					}
				}
				HttpEntity httpEntity = responseStr.getEntity();
				// 获取网络数据流
				InputStream resultStream = httpEntity.getContent();
				// 将输入流转换成缓冲流
				// bufferedInput = new BufferedInputStream(resultStream, 1024);
				// bufferedInput = new BufferedInputStream(resultStream);
				if (resultStream != null) {
					out = new ByteArrayOutputStream(1024);
					byte[] temp = new byte[1024];
					int size = 0;
					while ((size = resultStream.read(temp)) != -1) {
						out.write(temp, 0, size);
						out.flush();
					}
					resultStream.close();
					return temp;
				}
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
			// 用finally块来关闭输入流
			finally {
				try {
					if (bufferedInput != null) {
						bufferedInput.close();
					}
				} catch (IOException ex) {
					Log.e(Constants.LOG_TAG, ex.getMessage(), ex);
				}
			}
			if (out != null) {
				return out.toByteArray();
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * 向指定的URL发送post请求
	 * 
	 * @param urlString
	 *            发送请求的URL
	 * @param valuePair
	 *            post的数据body 是一个NameValuePair的格式(键值对的格式)
	 * @return 服务器返回InputStream数据
	 */
	public static InputStream sendPostInputStream(String urlString, long range,
			NameValuePair valuePair) {
		try {
			String urlName = urlString;
			InputStream resultStream = null;
			if (urlName != null && valuePair != null) {
				HttpClient client = getHttpClient();
				// 建立连接，并发送post请求
				HttpPost httpPostRequest = new HttpPost(urlName);
				// 设置断点续传包头
				if (range < 0) {
				} else {
					httpPostRequest.addHeader("Range", "bytes=" + range + "-");
				}
				List<NameValuePair> entityParams = new ArrayList<NameValuePair>();
				entityParams.add(valuePair);
				// 设置Post数据body
				httpPostRequest.addHeader("Charset", "UTF-8");
				httpPostRequest.setEntity(new UrlEncodedFormEntity(
						entityParams, HTTP.UTF_8));
				// 获取返回数据
				HttpResponse responseStr = client.execute(httpPostRequest);
				// 如果不成功，连接取消
				int statusCode = responseStr.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_PARTIAL_CONTENT
						&& statusCode != HttpStatus.SC_OK) {
					// throw new RuntimeException("请求失败");
					httpPostRequest.abort();
					return null;
				}
				if (statusCode == HttpStatus.SC_NOT_FOUND) {
				}
				if (range == 0) {
					Header[] header = responseStr.getAllHeaders();
					for (int i = 0; i < header.length; i++) {
						if (header[i].getName().equals("Content-Length")) {
							content_length = Long.parseLong(header[i]
									.getValue());
							setContentLength(content_length);
						}
					}
				}
				// 如果成功，获得返回数据流
				HttpEntity httpEntity = responseStr.getEntity();
				resultStream = httpEntity.getContent();
				if (resultStream != null) {
					return resultStream;
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 向指定的URL发送post请求
	 * 
	 * @param urlString
	 *            发送请求的URL
	 * @param valuePair
	 *            post的数据body 是一个NameValuePair的格式(键值对的格式)
	 * @return 服务器返回byte数组数据
	 */
	public static byte[] sendPost(String urlString, Long range,
			NameValuePair valuePair) {
		String urlName = urlString;
		BufferedInputStream bufferedInput = null;
		ByteArrayOutputStream out = null;
		if (urlName != null && valuePair != null) {
			HttpClient client = getHttpClient();
			// 建立连接，并发送post请求
			HttpPost httpPostRequest = new HttpPost(urlName);
			// 设置断点续传包头
			if (range < 0) {
			} else {
				httpPostRequest.addHeader("Range", "bytes=" + range + "-");
			}
			List<NameValuePair> entityParams = new ArrayList<NameValuePair>();
			entityParams.add(valuePair);
			try {
				// 设置Post数据body
				httpPostRequest
						.setEntity(new UrlEncodedFormEntity(entityParams));
				// 获取返回数据
				HttpResponse responseStr = client.execute(httpPostRequest);
				// 如果不成功，连接取消
				int statusCode = responseStr.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_PARTIAL_CONTENT
						&& statusCode != HttpStatus.SC_OK) {
					// throw new RuntimeException("请求失败");
					httpPostRequest.abort();
					return null;
				}
				if (range == 0) {
					Header[] header = responseStr.getAllHeaders();
					for (int i = 0; i < header.length; i++) {
						if (header[i].getName().equals("Content-Length")) {
							content_length = Long.parseLong(header[i]
									.getValue());
							setContentLength(content_length);
						}
					}
				}
				// 如果成功，获得返回数据流
				HttpEntity httpEntity = responseStr.getEntity();
				InputStream resultStream = httpEntity.getContent();
				// 将输入流转换成缓冲流
				if (resultStream != null) {
					bufferedInput = new BufferedInputStream(resultStream);
					out = new ByteArrayOutputStream(1024);
					byte[] temp = new byte[1024];
					int size = 0;
					while ((size = bufferedInput.read(temp)) != -1) {
						out.write(temp, 0, size);
					}
					resultStream.close();
					return temp;
				}
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
			// 用finally块来关闭输入流
			finally {
				try {
					if (bufferedInput != null) {
						bufferedInput.close();
					}
				} catch (IOException ex) {
					Log.e(Constants.LOG_TAG, ex.getMessage(), ex);
				}
			}
			if (out != null) {
				return out.toByteArray();
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * 获取服务器响应中的content_length的值
	 * 
	 * @return
	 */
	public static long getContentLength() {
		return length;
	}

	/**
	 * 保存content_length的值
	 * 
	 * @param length
	 */
	private static void setContentLength(Long content_length) {
		length = content_length;
	}
}
