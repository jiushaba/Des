package com.lz.oncon.application;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import com.lb.common.util.Log;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lb.zbrj.service.LocService;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.LoginActivity;
import com.lz.oncon.activity.TabMainActivity;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.db.DatabaseMan;

public class AppUtil {
	/**
	 * 停止全部服务
	 */
	public static void stopAllServices() {
		MyApplication.getInstance().stopService(new Intent(MyApplication.getInstance(), LocService.class));
	}

	/**
	 * 退出IM
	 */
	public static void exitIM() {
		if (ImCore.isInstanciated())
			ImCore.getInstance().logout();
	}

	/**
	 * 关闭库
	 */
	public static void closeDB() {
		DatabaseMan dbMan = DatabaseMan.getInstance();
		if (dbMan != null)
			dbMan.close();
	}

	/**
	 * 关闭提醒
	 */
	public static void cancelNotis() {
		
	}

	/**
	 * 停止应用
	 */
	public static void killApp() {
		try {
			if (MyApplication.getInstance().mLocationClient != null && MyApplication.getInstance().mLocationClient.isStarted()) {
				MyApplication.getInstance().mLocationClient.stop();
			}
		} catch (Exception e) {
		}
		ActivityManager activityManager = (ActivityManager) MyApplication.getInstance().getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.restartPackage(MyApplication.getInstance().getApplicationContext().getPackageName());
		Process.killProcess(Process.myPid());
	}

	public static void loginIM() {
		try {
			if (!TextUtils.isEmpty(AccountData.getInstance().getBindphonenumber())) {
//				if (ImCore.isInstanciated()) {
//					Log.e(Constants.LOG_TAG, "loginIM------------------");
//					ImCore.getInstance().logout();
//				}
				ImCore.getInstance().setAccout();
				if (ImData.isInstanciated()) {
					Log.e(Constants.LOG_TAG, "loginIM   ImData   clear------------------");
					ImData.getInstance().clear();
				}
				ImData.getInstance();
				new Thread(){
					public void run(){
						ImCore.getInstance().login();
					}
				}.start();
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public static Intent getMainActIntent(Context ctx) {
		return new Intent(ctx, TabMainActivity.class);// 直播日记
	}

	public static Intent getLoginActIntent(Context ctx) {
		return new Intent(ctx, LoginActivity.class);// 直播日记
	}

	public static String getLoginActivityName() {
		return LoginActivity.class.getName();// 直播日记
	}

	public static void toBackground(Context ctx) {
		String packageName = "com.android.launcher";
		if (isApkAvailable(packageName)) {
			try {
				Intent i = new Intent();
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(packageName, "com.android.launcher2.Launcher"); // Laucher2
				i.setComponent(cn);
				ctx.startActivity(i);
			} catch (Exception e) {
				try {
					Intent i = new Intent();
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ComponentName cn = new ComponentName(packageName, "com.android.launcher.Launcher"); // Laucher
					i.setComponent(cn);
					ctx.startActivity(i);
				} catch (Exception e1) {
					Intent i = new Intent(Intent.ACTION_MAIN);
					i.addCategory(Intent.CATEGORY_HOME);
					ctx.startActivity(i);
				}
			}
		} else {
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			ctx.startActivity(i);
		}
	}

	public static boolean isApkAvailable(String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = MyApplication.getInstance().getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	public static void exitApp(final Context ctx, final ProgressDialog exitAppDialog) {
		exitAppDialog.setMessage(ctx.getString(R.string.exiting));
		exitAppDialog.setCancelable(false);
		try {
			if (exitAppDialog != null && !exitAppDialog.isShowing())
				exitAppDialog.show();
			else
				return;
			new Thread(new Runnable() {
				public void run() {
					try {
						AppUtil.stopAllServices();
						AccountData.getInstance().clearCurrAcc();
						AppUtil.exitIM();
						AppUtil.cancelNotis();
						AppUtil.closeDB();
					} catch (Exception e) {
						Log.e(Constants.LOG_TAG, e.getMessage(), e);
					} finally {
						if (exitAppDialog != null) {
							exitAppDialog.dismiss();
						}
						Intent intent = new Intent();
						intent.setAction(BaseActivity.MAIN_ACTIVITY_EXIT_ACTION);
						ctx.sendBroadcast(intent);
					}
				}
			}).start();
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public static void changeApp2Login(final Context ctx, final ProgressDialog changeAccDialog) {
		new Thread(new Runnable() {
			public void run() {
				try {
					AppUtil.stopAllServices();
					MyApplication.getInstance().mPreferencesMan.setPutPCLasttime("0");
					AppUtil.exitIM();
					AppUtil.cancelNotis();
					AccountData.getInstance().clearCurrAcc();
					AccountData.getInstance().clearLastAcc();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				} finally {
					if (changeAccDialog != null) {
						changeAccDialog.dismiss();
						Intent intent = new Intent(ctx, com.lz.oncon.activity.LoginActivity.class);
						ctx.startActivity(intent);
					}
				}
			}
		}).start();
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void execAsyncTask(AsyncTask at){
		if(Build.VERSION.SDK_INT < 11){
			at.execute();
		}else{
			at.executeOnExecutor(MyApplication.getInstance().asyncTaskPool);
		}
	}
}