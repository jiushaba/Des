package com.lz.oncon.application;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.videolan.vlc.VLCApplication;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.lb.common.util.CrashHandler;
import com.lb.common.util.DeviceUtils;
import com.lb.common.util.LogUtil;
import com.lb.zbrj.controller.LocThread;
import com.lb.zbrj.service.LocService;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.preferences.PreferencesMan;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class MyApplication extends Application {
	private static MyApplication instance;
	public PreferencesMan mPreferencesMan;
	public ActivityManager mActivityManager;
	public UMSocialService umService;
	LogUtil logUtil;
	
	public LocationClient mLocationClient;//百度定位，主线程实例化
	@SuppressWarnings("rawtypes")
	private HashMap<String, ArrayList> listeners;
	public boolean isRegist;
	public boolean isTopActivity;
	public static String songId = "";
	public Date beTopTime = Calendar.getInstance().getTime();
	private Timer locTimer;
	private TimerTask locTimerTask;
	public ExecutorService threadPool;
	public ExecutorService personThreadPool;
	public ExecutorService asyncTaskPool;
	public ExecutorService imTaskPool;
	public static MyApplication getInstance() {
		return instance;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		ImCore.getInstance();
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
		mLocationClient = new LocationClient(this);
		mPreferencesMan = new PreferencesMan(this);
		mActivityManager = ActivityManager.getScreenManager();
		umService = UMServiceFactory.getUMSocialService("android", RequestType.SOCIAL);

		listeners = new HashMap<String, ArrayList>();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.discCacheFileCount(200)//Set max cache file count in SD card
		.tasksProcessingOrder(QueueProcessingType.LIFO)
//		.enableLogging() // Not necessary in common  FIXME 关闭加载图片日志
		.build();

		//Initialize ImageLoader with configuration
		ImageLoader.getInstance().init(config);
		
		//注册crashHandler 
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext());  
        
        logUtil = new LogUtil(this);
        
        DeviceUtils.mkNoMediaFile(this);
        
        //定时定位
        locTimer = new Timer() ;
        locTimerTask = new TimerTask(){
            public void run(){
            	new LocThread().start();
            }
        };
        locTimer.schedule(locTimerTask, 0, 2*60*60*1000) ;
        
        threadPool = Executors.newFixedThreadPool(10);
        personThreadPool = Executors.newFixedThreadPool(10);
        asyncTaskPool = Executors.newFixedThreadPool(10);
        imTaskPool = Executors.newFixedThreadPool(10);
        VLCApplication.setInstance(this);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addListener(String key, Object listener){
		ArrayList alisteners = listeners.get(key);
		if(alisteners == null){
			alisteners = new ArrayList();
			listeners.put(key, alisteners);
		}
		alisteners.add(listener);
	}
	
	@SuppressWarnings("rawtypes")
	public void removeListener(String key, Object listener){
		ArrayList alisteners = listeners.get(key);
		if(alisteners == null){
			alisteners = new ArrayList();
			listeners.put(key, alisteners);
			return;
		}
		alisteners.remove(listener);
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList getListeners(String key) {
		ArrayList alisteners = listeners.get(key);
		if(alisteners == null){
			alisteners = new ArrayList();
			listeners.put(key, alisteners);
		}
		return alisteners;
	}
}