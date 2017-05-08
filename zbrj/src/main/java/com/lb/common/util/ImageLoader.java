package com.lb.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lz.oncon.application.MyApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xuanbo.xuan.R;

public class ImageLoader {
	
	public static DisplayImageOptions headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
    		.showStubImage(R.drawable.avatar_img_loading)
			.showImageOnFail(R.drawable.avatar_img_loading)
			.showImageForEmptyUri(R.drawable.avatar_img_loading)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.delayBeforeLoading(100)
			.build();
	
	public static DisplayImageOptions picOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
    		.showStubImage(R.drawable.defaultpic)
    		.showImageForEmptyUri(R.drawable.defaultpic)
    		.showImageOnFail(R.drawable.defaultpic)
    		.bitmapConfig(Bitmap.Config.RGB_565)
    		.resetViewBeforeLoading(false)
    		.delayBeforeLoading(200)
    		.build();

	private MemoryCache memoryCache = new MemoryCache();
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 线程池

	public ImageLoader() {
		executorService = Executors.newFixedThreadPool(5);
	}
	
	
	private int THREAD_NUM = 1;
	private static ImageLoader instance;
	private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
	
	public static ImageLoader getInstance(){
		if(instance == null){
			instance = new ImageLoader();
		}
		return instance;
	}

	// 加载图片
	/**
	 * 
	 * @param url  图片url
	 * @param saveLocalPath 本地保存路径
	 * @param imageView ImageView控件
	 * @param isLoadOnlyFromCache 标记listview滑动状态
	 * @param filename 图片文件名
	 */
	public void displayImage(String url, String saveLocalPath, ImageView imageView, boolean isLoadOnlyFromCache, String filename) {
		imageViews.put(imageView, url);
		// 先从内存缓存中查找
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else if (!isLoadOnlyFromCache){
			// 若没有的话则开启新线程加载图片
			queuePhoto(url, imageView, filename, saveLocalPath);
		}
	}
	
	public static Bitmap loadRoundBitmapFromFile(String path){
		if(path == null){
			return null;
		}
		
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if(bitmap!=null){
			return bitmap;
		}
		return null;
	}

	private void queuePhoto(String url, ImageView imageView, String filename, String saveLocalPath) {
		PhotoToLoad p = new PhotoToLoad(url, imageView, filename, saveLocalPath);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url, String saveLocalPath, String filename) {
		File f = new File(saveLocalPath);
		// 先从文件缓存中查找是否有
		Bitmap b = null;
		if (f != null && f.exists()){
			b = decodeFile(f);
		}
		if (b != null){
			return b;
		}else{
			// 最后从指定的url中下载图片
			try {
				Bitmap bitmap = null;
				URL imageUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) imageUrl
						.openConnection();
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);
				conn.setInstanceFollowRedirects(true);
				InputStream is = conn.getInputStream();
//				OutputStream os = new FileOutputStream(f);
//				copyStream(is, os);
//				os.close();
				bitmap = decodeFile(f);
				bitmap = BitmapFactory.decodeStream(is);
				BitmapDrawable draw = new BitmapDrawable(MyApplication.getInstance().getResources(), bitmap);
				FileCore.writeFaceToLocal(filename, draw);
				return bitmap;
			} catch (Exception ex) {
				return null;
			}
		}
	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, options);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 100;
			int width_tmp = options.outWidth;
			int height_tmp = options.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options options2 = new BitmapFactory.Options();
			options2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, options2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public String filename;
		public String saveLocalPath;

		public PhotoToLoad(String u, ImageView i, String fn, String path) {
			url = u;
			imageView = i;
			filename = fn;
			saveLocalPath = path;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad.saveLocalPath, photoToLoad.filename);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 更新的操作放在UI线程中
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
	
		}
	}

	// 清理内存缓存
	public void clearCache() {
		memoryCache.clear();
	}

	// 保存文件
	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
	
	public static void displayHeadImage(String url, ImageView iv){
		if(TextUtils.isEmpty(url)){
			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("", iv, headOptions);
		}else{
			String urllow = url.toLowerCase();
			if(urllow.startsWith("file:") || urllow.startsWith("http:") || urllow.startsWith("https:")){
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url, iv, headOptions);
			}else{
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(NetIF_ZBRJ.address_head + url, iv, headOptions);
			}
		}
	}
	
	public static void displayHeadImage(String url, ImageView iv, ImageLoadingListener listener){
		if(TextUtils.isEmpty(url)){
			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("", iv, headOptions, listener);
		}else{
			String urllow = url.toLowerCase();
			if(urllow.startsWith("file:") || urllow.startsWith("http:") || urllow.startsWith("https:")){
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url, iv, headOptions, listener);
			}else{
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(NetIF_ZBRJ.address_head + url, iv, headOptions, listener);
			}
		}
	}
	
	public static void displayPicImage(String url, ImageView iv){
		try {
			if(TextUtils.isEmpty(url)){
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("", iv, picOptions);
			}else {
				String urllow = url.toLowerCase();
				if(urllow.startsWith("file:") || urllow.startsWith("http:") || urllow.startsWith("https:")){
					com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url, iv, picOptions);
				}else{
					com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(NetIF_ZBRJ.address_file + url, iv, picOptions);
				}
			}
		} catch (Exception e) {
		
		}
	}
	
	public static void displayPicImage(String url, ImageView iv, ImageLoadingListener listener){
		if(TextUtils.isEmpty(url)){
			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("", iv, picOptions, listener);
		}else{
			String urllow = url.toLowerCase();
			if(urllow.startsWith("file:") || urllow.startsWith("http:") || urllow.startsWith("https:")){
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url, iv, picOptions, listener);
			}else{
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(NetIF_ZBRJ.address_file + url, iv, picOptions
						, listener);
			}
		}
	}
}