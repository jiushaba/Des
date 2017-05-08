package com.lz.oncon.api.core.im.data;

import java.security.MessageDigest;

import com.lb.common.util.Log;


public class MD5 {

	
	private static MessageDigest md5 = null;

	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public MD5(byte[] keyBytes) {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}


	/**
	 * MD5摘要
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] md5(byte[] src) {
		synchronized (md5) {
			return md5.digest(src);
		}
	}

	/**
	 * 字节数组转换为十六进制
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytes2hex(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i]).toUpperCase();
			sb.append(hex.length() == 1 ? ("0" + hex) : (hex.length() > 2 ? hex
					.substring(6, 8) : hex));
			sb.append("");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		String tmp_md5str = "123456";
		byte[] bytes = MD5.md5( tmp_md5str.getBytes() );
       System.out.println( "---------md5="+MD5.bytes2hex( bytes ) );
	}

}
