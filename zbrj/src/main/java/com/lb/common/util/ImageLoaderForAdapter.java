package com.lb.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lb.common.util.FileCore;
import com.lz.oncon.application.MyApplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

public class ImageLoaderForAdapter {

	private MemoryCache memoryCache = new MemoryCache();
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 线程池
	private ExecutorService executorService;

	public ImageLoaderForAdapter() {
		executorService = Executors.newFixedThreadPool(5);
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
	public void displayImage(String url, String saveLocalPath, ImageView imageView, boolean isLoadOnlyFromCache, String filename, int platform) {
		imageViews.put(imageView, url);
		// 先从内存缓存中查找
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null){
			imageView.setImageBitmap(bitmap);
		}else if (!isLoadOnlyFromCache){
			// 若没有的话则开启新线程加载图片
			queuePhoto(url, imageView, filename, saveLocalPath, platform);
		}
	}

	private void queuePhoto(String url, ImageView imageView, String filename, String saveLocalPath,int platform) {
		PhotoToLoad p = new PhotoToLoad(StringUtils.repNull(url), imageView, StringUtils.repNull(filename), StringUtils.repNull(saveLocalPath), platform);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url, String saveLocalPath, String filename, int platform) {
		File f = new File(saveLocalPath);
		// 先从文件缓存中查找是否有
		Bitmap b = null;
		if (f != null && f.exists()){
			b = decodeFile(f);
		}
		if (b != null){
			return b;
		}else{
			if (!TextUtils.isEmpty(url)) {
				// 最后从指定的url中下载图片
				try {
					Bitmap bitmap = null;
					URL imageUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
					conn.setConnectTimeout(30000);
					conn.setReadTimeout(30000);
					conn.setInstanceFollowRedirects(true);
					InputStream is = conn.getInputStream();
					bitmap = decodeFile(f);
					bitmap = BitmapFactory.decodeStream(is);
					BitmapDrawable draw = new BitmapDrawable(MyApplication.getInstance().getResources(), bitmap);
					FileCore.writeSocialToLocal(filename, draw, platform);
					return bitmap;
				} catch (Exception ex) {
					return null;
				}
			}else{
				return null;
			}
		}
	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	public static Bitmap decodeFile(File f) {
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
		public int platform;

		public PhotoToLoad(String u, ImageView i, String fn, String path, int pf) {
			url = u;
			imageView = i;
			filename = fn;
			saveLocalPath = path;
			platform = pf;
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
			Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad.saveLocalPath, photoToLoad.filename, photoToLoad.platform);
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

}