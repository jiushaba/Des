package com.lb.common.util;

import java.io.File;
import android.os.Environment;

public class SocialPicPath {
	public static final int SINA_PIC_PATH = 10;
	public static final int TENCENT_PIC_PATH = 11;
	public static String path;
	public static File f;

	public static String getSaveFilePath(int platform) {
		if (hasSDCard()) {
			if (platform == SINA_PIC_PATH) {
				path = getRootFilePath() + Constants.LOG_TAG + File.separator
						+ "pic" + File.separator + "social" + File.separator
						+ "sina" + File.separator;
			} else if (platform == TENCENT_PIC_PATH) {
				path = getRootFilePath() + Constants.LOG_TAG + File.separator
						+ "pic" + File.separator + "social" + File.separator
						+ "tencent" + File.separator;
			} else {
				path = getRootFilePath() + Constants.LOG_TAG + File.separator
						+ "pic" + File.separator + "social" + File.separator;
			}
		} else {
			if (platform == SINA_PIC_PATH) {
				path = getRootFilePath() + Constants.LOG_TAG + File.separator
						+ "pic" + File.separator + "social" + File.separator
						+ "sina" + File.separator;
			} else if (platform == TENCENT_PIC_PATH) {
				path = getRootFilePath() + Constants.LOG_TAG + File.separator
						+ "pic" + File.separator + "social" + File.separator
						+ "tencent" + File.separator;
			} else {
				path = getRootFilePath() + Constants.LOG_TAG + File.separator
						+ "pic" + File.separator + "social" + File.separator;
			}
		}
		return mkDir(path);
	}
	
	public static String mkDir(String path){
		f = new File(path);
		if(!f.exists()){
			f.mkdir();
		}
		return path;
	}

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					+ File.separator;// filePath: /sdcard/
		} else {
			return Environment.getDataDirectory().getAbsolutePath()
					+ File.separator + "data" + File.separator; // filePath: /data/data/
		}
	}

}
