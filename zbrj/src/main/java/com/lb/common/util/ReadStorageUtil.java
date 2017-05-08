package com.lb.common.util;

import android.os.Environment;
import android.os.StatFs;

public class ReadStorageUtil {
	
	/**
	 * 获取文件系统剩余空间大小
	 * @return 当前文件系统的大小
	 */
	public static long readFileSystem() {
		long blockSize = 0l;
		long blockCount = 0l;
		long allsize = 0l;
		String systemPath = Environment.getRootDirectory().getPath();
        StatFs statFs = new StatFs(systemPath);
        blockSize = statFs.getBlockSize();
        blockCount = statFs.getBlockCount();
        allsize = blockSize*blockCount;
//        Log.e(Constants.LOG_TAG, "path=" + systemPath + ",size=" + blockSize + ",blockcount=" + blockCount + ",allsize=" + allsize + "B");
        return allsize;
	}
	
	/**
	 * 获得sdcard的剩余空间大小
	 * @return 当前剩余的sdcard大小
	 */
	public static long readSDcard() {
    	long blockSize = 0l;
		long blockCount = 0l;
		long allsize = 0l;
		String sdCardStatu = Environment.getExternalStorageState();
	        if (Environment.MEDIA_MOUNTED.equals(sdCardStatu)) { //sdcard存在时
	        	String sdCardPath = Environment.getExternalStorageDirectory().getPath();
	        	StatFs statFs = new StatFs(sdCardPath);
	        	blockSize = statFs.getBlockSize();
	            blockCount = statFs.getBlockCount();
	            allsize = blockSize*blockCount;
	            return allsize;
	        } else {
	        	return -1;
	        }
	 }
	
}
