/**
*
*  ProjectName: oncon
*  ClassName:AsyncImageLoader
*  Description: 
*  Author: Steven
*  Date:2012-9-6 下午6:03:50
*
*/
package com.lz.oncon.app.im.ui.news;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.lb.common.util.Log;
import android.widget.ImageView;

import com.lb.common.util.Constants;
import com.lb.common.util.ImageUtil;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.application.MyApplication;

public class AsyncImageLoader {
	private HashMap<String, SoftReference<Drawable>> imageCache;
	private int THREAD_NUM = 4;
	private static AsyncImageLoader instance;
	
	public static AsyncImageLoader getInstance(){
		if(instance == null){
			instance = new AsyncImageLoader();
		}
		return instance;
	}
    	
	private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);
	// static int screenWidth,screenHeight;
	private AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
		// this.screenHeight = screenHeight;
		//  this.screenWidth = screenWidth;
	}
	
	/**
	 * 异步加载图片并保持在本地
	 * @param fileName
	 * @param picLocalPath 本地绝对路径地址
	 * @param picRemoteUrl 远程绝对路径地址
	 * @param head
	 */
	public void loadDrawableToSave(String fileName,final String picLocalPath,String picRemoteUrl,final ImageView head,boolean isFromServer){
		final File imageFile = new File(picLocalPath);
		AsyncImageLoader asyncImageLoader = AsyncImageLoader.getInstance();
		if (imageFile.exists()&&!isFromServer) {
			asyncImageLoader.loadDrawable(picLocalPath, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					head.setImageDrawable(imageDrawable);
				}
			}, IMConstants.lOAD_FROM_SDCARD);
		} else {
			asyncImageLoader.loadDrawable(picRemoteUrl, new ImageCallback() {
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					head.setImageDrawable(imageDrawable);
					if (imageFile.exists()){
						imageFile.delete();
					}
					ImageUtil.savePicToLocal(picLocalPath, imageDrawable);
				}
			}, IMConstants.LOAD_FROM_SERVER);
		}
	}
      
	public synchronized Drawable loadDrawable(final String imageUrl, final ImageCallback imageCallback,final int type) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
				return drawable;
			}
		}
		executorService.submit(new Runnable(){
        	 
			@Override
			public void run() {
				if(type == IMConstants.LOAD_FROM_SERVER){
					Drawable drawable = loadImageFromUrl(imageUrl);
					if(drawable != null){
						imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
						Message message = handler.obtainMessage(0, drawable);
						handler.sendMessage(message);
					}
				}else if(type == IMConstants.lOAD_FROM_SDCARD){
					Drawable drawable = loadImageFromSDCard(imageUrl);
					if(drawable != null){
						imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
						Message message = handler.obtainMessage(0, drawable);
						handler.sendMessage(message);
					}
				}else if(type == IMConstants.lOAD_FROM_APK){
					Drawable drawable = getApkIcon(imageUrl);
					if(drawable != null){
						imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
						Message message = handler.obtainMessage(0, drawable);
						handler.sendMessage(message);
					}
				}
			}});
		return null;
	}
	
	public synchronized Drawable loadDrawable(final String imageUrl, final String imagePath, final ImageCallback imageCallback) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imagePath);
			}
		};
		if (imageCache.containsKey(imagePath)) {
			SoftReference<Drawable> softReference = imageCache.get(imagePath);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
				return drawable;
			}
		}
		executorService.submit(new Runnable(){
        	 
			@Override
			public void run() {
				boolean needServer = false;
				if(!TextUtils.isEmpty(imagePath)){
					File f = new File(imagePath);
					if(f.exists()){//从本地读取
						Drawable drawable = loadImageFromSDCard(imagePath);
						if(drawable != null){
							imageCache.put(imagePath, new SoftReference<Drawable>(drawable));
							Message message = handler.obtainMessage(0, drawable);
							handler.sendMessage(message);
						}
					}else{//本地无图片则显示默认图片
						needServer = true;
						Message message = handler.obtainMessage(0, null);
						handler.sendMessage(message);
					}
				}else if(!TextUtils.isEmpty(imageUrl)){
					needServer = true;
				}else{
					Message message = handler.obtainMessage(0, null);
					handler.sendMessage(message);
				}
				if(needServer){
					boolean result = saveImageFromUrl(imageUrl, imagePath);
					if(result){
						Drawable drawable = loadImageFromSDCard(imagePath);
						if(drawable != null){
							imageCache.put(imagePath, new SoftReference<Drawable>(drawable));
							Message message = handler.obtainMessage(0, drawable);
							handler.sendMessage(message);
						}
					}else{
						Message message = handler.obtainMessage(0, null);
						handler.sendMessage(message);
					}
				}
			}});
		return null;
	}
    	
	/**
	 * 加载网络图片
	 */
	private static  Drawable loadImageFromUrl(String url) {
		URL m;
		Drawable drawable = null;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
			drawable = Drawable.createFromStream(i, "src");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
    
    public static InputStream loadImageInputStreamFromUrl(String url){
    	URL m;
    	InputStream i = null;
    	try {
    		m = new URL(url);
    		i = (InputStream) m.getContent();
    	} catch (MalformedURLException e1) {
    		e1.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return i;
    }
    
    private static boolean saveImageFromUrl(String url, String path){
    	InputStream is = null;
		FileOutputStream fos = null;
		try{
			is = loadImageInputStreamFromUrl(url);
			if (is != null) {
				File file = new File(path);
				File tempFile = new File(path + ".tmp");
				File parentFile = tempFile.getParentFile();
				if(!parentFile.exists()){
					parentFile.mkdirs();
				}
				fos = new FileOutputStream(tempFile);
				int size = 0;
				byte[] buffer = new byte[1024];
				while((size=is.read(buffer)) != -1){
					fos.write(buffer, 0, size);
					fos.flush();
				}
				return tempFile.renameTo(file);
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}finally{
			try{
				if(is != null){
					is.close();
				}
			}catch(Exception e){}
			try{
				if(fos != null){
					fos.close();
				}
			}catch(Exception e){}
		}
    	return false;
    }
    
    public static Drawable getApkIcon(String Path){
		//未安装的程序通过apk文件获取icon
		String apkPath = Path; //   apk  文件所在的路径
		String PATH_PackageParser = "android.content.pm.PackageParser";
		String PATH_AssetManager = "android.content.res.AssetManager";
		try {
			Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
			Class<?>[] typeArgs = { String.class };
			Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs = { apkPath };
			Object pkgParser = pkgParserCt.newInstance(valueArgs);
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();
			typeArgs = new Class<?>[] { File.class, String.class, DisplayMetrics.class, int.class };
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);
			valueArgs = new Object[] { new File(apkPath), apkPath, metrics, 0 };
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField("applicationInfo");
			ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);
			Object assetMag = assetMagCls.newInstance();
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
			Resources res = MyApplication.getInstance().getResources();
			typeArgs = new Class[3];
			typeArgs[0] = assetMag.getClass();
			typeArgs[1] = res.getDisplayMetrics().getClass();
			typeArgs[2] = res.getConfiguration().getClass();
			Constructor<Resources> resCt = Resources.class.getConstructor(typeArgs);
			valueArgs = new Object[3];
			valueArgs[0] = assetMag;
			valueArgs[1] = res.getDisplayMetrics();
			valueArgs[2] = res.getConfiguration();
			res = (Resources) resCt.newInstance(valueArgs);
			if (info != null) {
				if (info.icon != 0) {
					Drawable  icon=res.getDrawable(info.icon);
					return  icon;
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    
    /**
	 * 加载内存卡图片
	 */
    public static Drawable loadImageFromSDCard(String path) {
    	Options options=new Options();
    	Drawable drawable = null;
    	options.inSampleSize=3;
//	    Bitmap source=BitmapFactory.decodeFile(path, options);
    	Bitmap source=BitmapFactory.decodeFile(path);
    	drawable = new BitmapDrawable(MyApplication.getInstance().getResources(), source);
    	//  Drawable drawable=new BitmapDrawable(source);
    	return drawable;
    }
    
    public HashMap<String, SoftReference<Drawable>> getImageCache() {
		return imageCache;
	}

	public void setImageCache(HashMap<String, SoftReference<Drawable>> imageCache) {
		this.imageCache = imageCache;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}
}