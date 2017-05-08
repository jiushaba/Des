package com.lb.common.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lz.oncon.application.MyApplication;

public class ImageThumbUtil {
	private static ImageThumbUtil instance;

	private ImageThumbUtil() {
		instance = this;
	}

	public static ImageThumbUtil getInstance() {
		if (instance == null) {
			instance = new ImageThumbUtil();
		}
		return instance;
	}

	@TargetApi(8)
	public Bitmap thumbImage(String desPath, String outPath, int width,
			int height) throws IOException {
		Bitmap tempBitmap = loadImageFromFile(desPath);
		tempBitmap = ThumbnailUtils.extractThumbnail(tempBitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		writeImageToLocal(outPath, tempBitmap);
		return tempBitmap;
	}

	public Bitmap loadImageFromFile(String desPath) throws IOException {
		Bitmap tempBitmap = null;
		File file = new File(desPath);
		InputStream imageStream = new FileInputStream(file);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getInstance().getResources(), imageStream);
		tempBitmap = bitmapDrawable.getBitmap();
		imageStream.close();
		imageStream = null;
		bitmapDrawable = null;
		return tempBitmap;
	}

	public void writeImageToLocal(String outPath, Bitmap bitmap)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		FileCore.writeBytes(baos.toByteArray(), outPath, false);
	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		return ((BitmapDrawable)drawable).getBitmap();
	}
	
	 public byte[] compressImage(Bitmap image) {
	    	byte[] b = null;
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        try{
	        	image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	            int options = 90;
	            while (baos.toByteArray().length / 1024 > 40) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
	            	if(options > 10){
	            		options -= 10;
	            	}else if(options > 1){
	            		options -= 1;
	            	}else{
	            		break;
	            	}
	                baos.reset();//重置baos即清空baos
	                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
	            }
	            b = baos.toByteArray();
	        }catch(Exception e){
	        	e.printStackTrace();
	        }finally{
	        	try{
	        		if(baos != null)baos.close();
	        	}catch(Exception e){}
	        }
	        return b;  
	    } 
}
