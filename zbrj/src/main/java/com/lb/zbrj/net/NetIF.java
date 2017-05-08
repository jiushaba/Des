package com.lb.zbrj.net;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.danmu.data.DanmuContentData;
import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;

import com.lb.common.network.NetworkPostCore;
import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;

/**
 * 获取各接口的返回值（String、JSONObject）
 * 
 * @author Administrator
 * 
 */
public class NetIF {

	private Context mContext;
	protected NetworkStatusCheck netCheck;
	protected NetworkPostCore netPost;
	private int timeOut = 30;
	public NetIF(Context context) {
		this.mContext = context;
		netCheck = new NetworkStatusCheck(mContext);
		netPost = new NetworkPostCore();
	}

	/**
	 * 断开数据连接
	 */
	public void close() {
		netPost.close();
	}

	public String callService(String url, String req) {
		return callService(url, req, timeOut * 1000);
	}

	public String callService(String url, String req, int timeout) {
		try {
			if (!netCheck.checkMobileNetStatus() && !netCheck.checkWifiNetStatus()) {// 无网络连接
				return MyApplication.getInstance().getResources().getString(R.string.network_disconnection);
			}
			// 打印请求
			int maxLogSize = 1000;
			int num = req.length() % maxLogSize == 0 ? req.length() / maxLogSize : (req.length() / maxLogSize + 1);
			for (int i = 0; i < num; i++) {
				int start = i * maxLogSize;
				int end = (i + 1) * maxLogSize;
				end = end > req.length() ? req.length() : end;
				Log.d(Constants.LOG_TAG, "req:" + num + ":" + i + ":" + req.substring(start, end));
			}
			JSONObject jsonReq = new JSONObject(req);
			String idReq = jsonReq.isNull("id") ? "" : jsonReq.getString("id");
			String typeReq = jsonReq.isNull("type") ? "" : jsonReq.getString("type");
			String strRes = netPost.sendPost(url, req.getBytes(Constants.ENCODE), timeout); // 获得返回值
			if (TextUtils.isEmpty(strRes)) { // 判断是否为空
				return null;
			} else {
				// 打印
				maxLogSize = 1000;
				num = strRes.length() % maxLogSize == 0 ? strRes.length() / maxLogSize : (strRes.length() / maxLogSize + 1);
				for (int i = 0; i < num; i++) {
					int start = i * maxLogSize;
					int end = (i + 1) * maxLogSize;
					end = end > strRes.length() ? strRes.length() : end;
					Log.d(Constants.LOG_TAG, "res:" + num + ":" + i + ":" + strRes.substring(start, end));
				}
				if ("m1_contact_server_query".equals(typeReq) || TextUtils.isEmpty(idReq) || TextUtils.isEmpty(typeReq)) {
					return strRes;
				}
				JSONObject jsonRes = new JSONObject(strRes);
				String idRes = jsonRes.getString("id");
				String typeRes = jsonRes.getString("type");
				if (idReq.equalsIgnoreCase(idRes) && typeReq.equalsIgnoreCase(typeRes)) {// 判断报文是成对的报文
					return strRes;
				} else {
					return null;
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			return null;
		}
	}

	protected String callserviceNoCheck(String url,String req){
		if (!netCheck.checkMobileNetStatus() && !netCheck.checkWifiNetStatus()) {// 无网络连接
			return MyApplication.getInstance().getResources().getString(R.string.network_disconnection);
		}
		try {
			return netPost.sendPost(url, req.getBytes(Constants.ENCODE), timeOut * 1000 );
		} catch (UnsupportedEncodingException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			return null;
		}
	}
	/**
	 * 产生6位随机数
	 * 
	 * @return 6位随机数
	 */
	public int getRandomNumber() {
		Random random = new Random();
		int num = random.nextInt(899999) + 100000;
		return num;
	}

	public JSONObject createReqJsonNoAccount(String version, String type) {
		JSONObject jsonReq = new JSONObject();
		try {
			jsonReq.put("version", version);
			jsonReq.put("id", getRandomNumber());
			jsonReq.put("type", type);
			jsonReq.put("action", Constants.ACTION_REQ);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return jsonReq;
	}

	public JSONObject createReqJson(String version, String type) {
		JSONObject jsonReq = new JSONObject();
		try {
			jsonReq.put("version", version);
			jsonReq.put("id", getRandomNumber() + "");
			jsonReq.put("type", type);
			jsonReq.put("action", Constants.ACTION_REQ);
			jsonReq.put("account", AccountData.getInstance().getBindphonenumber());
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return jsonReq;
	}

	public NetInterfaceStatusDataStruct parseResStr(String strRes) {
		NetInterfaceStatusDataStruct nisds = new NetInterfaceStatusDataStruct();
		if (TextUtils.isEmpty(strRes)) {
			nisds.setStatus(Constants.RES_FAIL);
		} else {
			try {
				JSONObject jsonRes = new JSONObject(strRes);
				if (jsonRes == null || !jsonRes.has("status")) {
					nisds.setStatus(Constants.RES_FAIL);
				} else {
					nisds.setStatus(jsonRes.getString("status"));
					nisds.setMessage(jsonRes.has("desc") ? jsonRes.getString("desc") : "");
					nisds.setObj(jsonRes);
				}
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
				nisds.setStatus(Constants.RES_FAIL);
			}
		}
		return nisds;
	}
	
	
}