package com.lz.oncon.app.im.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class ExternalStorageUtil {
	private static boolean isReadOnlyShow = false;
	private static boolean isNotMountShow = false;
	
	public static void show(Context context){
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			isReadOnlyShow = false;
			isNotMountShow = false;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) && !isReadOnlyShow) {
	    	Toast.makeText(context, "存储卡只读", Toast.LENGTH_SHORT).show();
	    	isReadOnlyShow = true;
	    }else if(!isNotMountShow){
	    	Toast.makeText(context, "没有存储卡", Toast.LENGTH_SHORT).show();
	    	isNotMountShow = true;
	    }
	}
	
	public static synchronized boolean copyDB2Pad(File dbFile){
		if( dbFile != null && dbFile.exists() ){
			String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			StringBuffer buffer = new StringBuffer();
			buffer.append(sdcardPath);
			buffer.append(File.separator);
			buffer.append("im_backup.db");
			String path = buffer.toString();
			try{
				InputStream is = new FileInputStream(dbFile);
				FileOutputStream fos = new FileOutputStream(path);
				byte[] tempBuf = new byte[8 * 1024];
				int length = 0;
				while ((length = is.read(tempBuf)) > 0) {
					fos.write(tempBuf, 0, length);
				}
				fos.flush();
				is.close();
				fos.close();
				return true ;
			}catch (Exception e) {
			}
		}
		
		return false ;
	}
}
