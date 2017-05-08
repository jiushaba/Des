package com.lz.oncon.activity.friendcircle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.app.Application;

public class FriendCircleCacheUtil {
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public static boolean saveObject(Serializable ser, String file, Application app) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = app.openFileOutput(file, Application.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Serializable readObject(String file, Application app) {
		if (!isExistDataCache(file, app))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = app.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = app.getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	/**
	 * 删除缓存文件
	 * @param cachefile
	 * @param app
	 * @return
	 */
	public static boolean removeDataCache(String cachefile, Application app) {
		boolean isSuc = false;
		boolean isSave = isExistDataCache(cachefile, app);
		if(isSave){
			File data = app.getFileStreamPath(cachefile);
			isSuc = data.delete();
		}
		return isSuc;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private static boolean isExistDataCache(String cachefile, Application app) {
		boolean exist = false;
		File data = app.getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	public static boolean isReadDataCache(String cachefile, Application app) {
		return readObject(cachefile, app) != null;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile, Application app) {
		boolean failure = false;
		try {
			File data = app.getFileStreamPath(cachefile);
			if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME) {
				data.delete();
				failure = true;
			} else if (!data.exists()) {
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		return failure;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache(Application app) {
		// 清除数据缓存
		clearCacheFolder(app.getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(app.getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(app), System.currentTimeMillis());
		}
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
}
