package com.lb.common.network;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.NameValuePair;

import com.lb.common.util.Log;

import com.lb.common.util.Constants;
import com.lb.common.util.FileCore;
import com.lb.common.util.ReadStorageUtil;

public class HttpDownload {
	//public static String dataFilePath = null;

	/**
	 * 1.首先与服务器建立连接，并获得返回数据流
	 * 2.创建文件
	 * 3.读流，并将流写入文件
	 * 3.开始下载文件，同时需要传入已经下载的文件的大小
	 */
	public static boolean downLoad(String urlParams, String filePath, long range,NameValuePair mValuePair) {
		InputStream responseData = HttpCoreApache.sendPostInputStream(urlParams, range, mValuePair);
		if (range == 0) {
			long content_length = HttpCoreApache.getContentLength();
			long externalStorage = ReadStorageUtil.readSDcard();
			try {
				FileCore.isHaveStorage(content_length, externalStorage);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
		
		boolean isWrite=false;
		try {
			isWrite = FileCore.writeInputStream(responseData, filePath);
		}catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return isWrite;
	}	
}
