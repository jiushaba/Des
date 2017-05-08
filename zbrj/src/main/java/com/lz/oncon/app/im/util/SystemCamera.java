package com.lz.oncon.app.im.util;


import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.lb.common.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.lb.common.util.Constants;
import com.lb.common.util.ImageThumbUtil;
import com.lb.common.util.corpimage.CropImage;
import com.lb.common.util.corpimage.CropUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.widget.InfoToast;

public class SystemCamera{

	public static final int TAKE_PICTURE = 1;
	public static final int SHOW_PICTURE = 2; 
	public static final int FILTER_PICTURE = 3; 
	
	public static final int DEFAULT_PICTURE_QUALITY = 100;
	
	public static String captureFilePath = null;
	public static Uri uri;
	public static void takePicture(Activity a,int type){
		try{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			captureFilePath = Environment.getExternalStorageDirectory() + "/" + Constants.PACKAGENAME + "/oncon/photos/"+"camera_"+System.currentTimeMillis()+".jpg";
			File out = new File(captureFilePath);
			File outParent = out.getParentFile();
			if(!outParent.exists()){
				outParent.mkdirs();
			}
			uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
			a.startActivityForResult(intent, type);
		}catch(Exception e){
			InfoToast.makeText(a
					, a.getString(R.string.camera) + a.getString(R.string.fail)
					, Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
		}
	}
	public static String getCaptureFilePath(){
		return captureFilePath;
	}
	
	
	public static void setCaptureFilePath(String captureFilePath) {
		SystemCamera.captureFilePath = captureFilePath;
	}
	public static Uri getCaptureUri(){
		return uri;
	}
	
	/**
	 * show images in the android device media store
	 */
	public static void showPictures(Activity a,int type){
		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
        String IMAGE_UNSPECIFIED = "image/*";
        innerIntent.setType(IMAGE_UNSPECIFIED); //查看类型 String IMAGE_UNSPECIFIED = "image/*"; 详细的类型在 com.google.android.mms.ContentType 中
        Intent wrapperIntent = Intent.createChooser(innerIntent, null);
        a.startActivityForResult(wrapperIntent, type);
	}
	
	public static String getPicturePath(String srcPath){
		return Environment.getExternalStorageDirectory() + "/" + Constants.PACKAGENAME + "/oncon/photos/"+"picture_"+System.currentTimeMillis() + srcPath.substring(srcPath.lastIndexOf("."));
	}
	
	/*
	 * 调用系统剪切图片工具
	 * @param uri
	 * @param data
	 */
	public static void getCropImageIntent(Activity a,Bitmap data){
		try{
			//将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
			File f = CropUtil.makeTempFile(data, a.getFilesDir().getAbsolutePath() + File.separator, "TEMPIMG.png");
			cropImageFilePath = f.getAbsolutePath();
			//调用CropImage类对图片进行剪切
			Intent intent = new Intent(a, CropImage.class);
			Bundle extras = new Bundle();
			extras.putString("circleCrop", "true");
			extras.putInt("aspectX", 1);
			extras.putInt("aspectY", 1);
			extras.putInt("outputX", 100);  
			extras.putInt("outputY", 100);  
			intent.setDataAndType(Uri.fromFile(f), "image/*");
			intent.putExtras(extras);
			a.startActivityForResult(intent, com.lb.common.util.Constants.CORP_PHOTO_CODE);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	/*
	 * 调用系统剪切图片工具
	 * @param uri
	 * @param data
	 */
	public static void getCropHeadImageIntent(Activity a,Bitmap data){
		try{
			//将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
			File f = CropUtil.makeTempFile(data, a.getFilesDir().getAbsolutePath() + File.separator, "TEMPIMG.png");
			cropImageFilePath = f.getAbsolutePath();
			//调用CropImage类对图片进行剪切
			Intent intent = new Intent(a, CropImage.class);
			Bundle extras = new Bundle();
			extras.putString("circleCrop", "true");
			extras.putInt("aspectX", 1);
			extras.putInt("aspectY", 1);
			extras.putInt("outputX", 100);  
			extras.putInt("outputY", 100);  
			intent.setDataAndType(Uri.fromFile(f), "image/*");
			intent.putExtras(extras);
			a.startActivityForResult(intent, com.lb.common.util.Constants.CORP_PHOTO_CODE);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * 调用系统剪切图片工具
	 * @param a activity
	 * @param data bitmap
	 * @param xr 宽高比的X值
	 * @param yr 宽高比的Y值
	 * @param x 截取后图片的宽
	 * @param y 截取后图片的高
	 */
	public static void getCropImageIntentForFriendcircle(Activity a,Bitmap data, int xr, int yr, int x, int y){
		try{
			//将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
			File f = CropUtil.makeTempFile(data, a.getFilesDir().getAbsolutePath() + File.separator, "TEMPIMG.png");
			cropImageFilePath = f.getAbsolutePath();
			//调用CropImage类对图片进行剪切
			Intent intent = new Intent(a, CropImage.class);
			Bundle extras = new Bundle();
			extras.putString("circleCrop", "true");
			extras.putInt("aspectX", xr);
			extras.putInt("aspectY", yr);
			extras.putInt("outputX", x);  
			extras.putInt("outputY", y);  
			intent.setDataAndType(Uri.fromFile(f), "image/*");
			intent.putExtras(extras);
			a.startActivityForResult(intent, com.lb.common.util.Constants.CORP_PHOTO_CODE);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	private static String cropImageFilePath = null;
	public static String getCropImageFilePath(){
		return cropImageFilePath;
	}
	
	/**
	 * 从文件中读取图片
	 * @param a
	 * @param file_path
	 */
	public static void getCropImageIntent(Activity a,String file_path,int outputX, int outputY){
		try{
			Bitmap data = ImageThumbUtil.getInstance().loadImageFromFile(file_path);
			//将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
			File f = CropUtil.makeTempFile(data, Environment.getExternalStorageDirectory() + "/" + Constants.PACKAGENAME + "/oncon/photos/", "cropimage_"+System.currentTimeMillis()+".jpg");
			cropImageFilePath = f.getAbsolutePath();
			//调用CropImage类对图片进行剪切
			Intent intent = new Intent(a, CropImage.class);
			Bundle extras = new Bundle();
			extras.putString("circleCrop", "true");
			extras.putInt("aspectX", 1);
			extras.putInt("aspectY", 1);
			extras.putInt("outputX", outputX);  
			extras.putInt("outputY", outputY);  
			intent.setDataAndType(Uri.fromFile(f), "image/*");
			intent.putExtras(extras);
			a.startActivityForResult(intent, com.lb.common.util.Constants.CORP_PHOTO_CODE);
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	public static Bitmap rotatePic(Bitmap bmp){
		Bitmap tempb = null;
		try{
			Matrix m = new Matrix();
			m.setRotate(90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
			tempb = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
		}catch(Exception e){}
		return tempb;
	}
}
