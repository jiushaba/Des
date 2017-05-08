package com.lz.oncon.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.lb.common.util.Log;

import com.lb.common.network.HttpDownload;
import com.lb.common.util.Constants;
import com.lb.common.util.DateUtil;
import com.lb.common.util.DeviceUtils;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.util.Base64;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.AccountDataStruct;
import com.lz.oncon.data.db.AccountHelper;
import com.lz.oncon.widget.InfoProgressDialog;

public class AccountController extends BaseController implements OnCancelListener {

	private AccountHelper dbHelper;

	private SyncListener syncListener;

	public InfoProgressDialog dialog;
	boolean bindWeiboResult = false;

	private Bitmap headBM;
	private Context mContext;

	@SuppressLint("SimpleDateFormat")
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public AccountController(Context context) {
		super(context);
		mContext = context;
	}

	public List<AccountDataStruct> findAll() {
		return dbHelper.findAll();
	}

	public List<AccountDataStruct> findRecentLogin() {
		return dbHelper.findRecentLogin();
	}

	public void addAccount(AccountData accountData) {
		dbHelper.addAccount(accountData);
	}

	public void modifyLastLogin(String lastLoginTime, String password, String nationalNumber, String username) {
		dbHelper.modifyLastLogin(lastLoginTime, password, nationalNumber, username);
	}

	public void modifyLastLoginAcc(String lastLoginTime, String password, String username) {
		dbHelper.modifyLastLoginAcc(lastLoginTime, password, username);
	}

	/**
	 * 登录
	 */
	public void login(final AccountData acc, boolean showDialog, int timeout) {
		NetIFUI_ZBRJ niUI = new NetIFUI_ZBRJ(this.mContext, new com.lb.zbrj.net.NetIFUI.NetInterfaceListener() {

			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				try {
					if (getSyncListener() != null) {
						if (Constants.RES_SUCCESS.equals(niStatusData.getStatus())) {
							afterLogin(niStatusData, acc);
							//同步个人信息
							PersonController.synInfo();
							//FIXME 暂停启动独立同步背景图线程
//							new Thread(new SyncBgRunnable()).start();
						} else if (Constants.RES_NET_ERROR.equals(niStatusData.getStatus())) {// 离线登录
							List<AccountDataStruct> accList = dbHelper.findRecentLogin();
							boolean exists = false;
							if (accList != null && accList.size() > 0) {
								for (AccountDataStruct accTemp : accList) {
									if (acc.getUsername().equalsIgnoreCase(accTemp.getUsername())
											&& acc.getPassword().equalsIgnoreCase(accTemp.getPassword())) {
										exists = true;
										break;
									}
								}
								if (exists) {
									acc.setLastLoginTime(DateUtil.getDateTimeString());
									dbHelper.modifyLastLogin(acc.getLastLoginTime(), acc.getPassword(), acc.getNationalNumber(), acc.getUsername());
									accList = dbHelper.findAll();
									if (accList != null && accList.size() > 0) {
										for (AccountDataStruct accTemp : accList) {
											if (acc.getUsername().equalsIgnoreCase(accTemp.getUsername())
													|| acc.getUsername().equalsIgnoreCase(accTemp.getBindphonenumber())
													|| (acc.getNationalNumber() + acc.getUsername()).equalsIgnoreCase(accTemp.getBindphonenumber())) {
												accTemp.setLastLoginTime(acc.getLastLoginTime());
												accTemp.setPassword(acc.getPassword());
												accTemp.setNationalNumber(acc.getNationalNumber());
												AccountData.getInstance().copy(accTemp);
												dbHelper.modifyLastLoginAcc(acc.getLastLoginTime(), acc.getPassword(), acc.getUsername());
												break;
											}
										}
									}
									niStatusData.setStatus(Constants.RES_NET_ERROR);
									//同步个人信息
									PersonController.synInfo();
									//FIXME 暂停启动独立同步背景图线程
//									new Thread(new SyncBgRunnable()).start();
								} else {
									niStatusData.setStatus(Constants.RES_FAIL);
								}
							} else {
								niStatusData.setStatus(Constants.RES_FAIL);
							}
						} else {
							AccountData.getInstance().clearCurrAcc();
						}
						getSyncListener().onLogined(niStatusData.getStatus(), niStatusData.getMessage(), acc);
					}
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
			}
		});
		niUI.setShowDialog(showDialog);
		String username = acc.getUsername();
		Pattern p = Pattern.compile("^[0-9]+$");
		Matcher m = p.matcher(username);
		if (m.matches()) {
			if (IMConstants.COUNTRY_CODE_CHINA.equals(acc.getNationalNumber())) {
				// username = acc.getNationalNumber() + username;
			} else {
				username = acc.getNationalNumber() + username;
			}

		}
		niUI.m1_login(username, acc.getPassword(), timeout);
	}

	public String getWeiboId(String type) {
		if (IMConstants.WEIBO_TYPE_SINA.equals(type)) {
			return MyApplication.getInstance().mPreferencesMan.getSinaWeiboId();
		} else if (IMConstants.TYPE_QQ.equals(type)) {
			return MyApplication.getInstance().mPreferencesMan.getQQId();
		} else if (IMConstants.WEIBO_TYPE_TECENT.equals(type)) {
			return MyApplication.getInstance().mPreferencesMan.getTencentWeiboId();
		}
		return null;
	}

	/**
	 * 记录登录日志
	 * 
	 * @param acc
	 */
	public void recordLogin(AccountData acc) {
		// 判断是否需要记录最新登录
		List<AccountDataStruct> accList = dbHelper.findRecentLogin();
		boolean existsLogin = false;
		if (accList != null && accList.size() > 0) {
			for (AccountDataStruct accTemp : accList) {
				if (acc.getUsername().equalsIgnoreCase(accTemp.getUsername())) {
					existsLogin = true;
					accTemp.setLastLoginTime(acc.getLastLoginTime());
					accTemp.setPassword(acc.getPassword());
					accTemp.setNationalNumber(acc.getNationalNumber());
					acc.copy(accTemp);
					break;
				}
			}
		}
		if (!existsLogin) {
			dbHelper.addLogin(acc);
		} else {
			dbHelper.modifyLastLogin(acc.getLastLoginTime(), acc.getPassword(), acc.getNationalNumber(), acc.getUsername());
		}
	}

	/**
	 * 登录成功后的处理
	 * 
	 * @param niStatusData
	 * @param acc
	 */
	private void afterLogin(NetInterfaceStatusDataStruct niStatusData, AccountData acc) {
		JSONObject result = (JSONObject) niStatusData.getObj();
		acc.setLastLoginTime(DateUtil.getDateTimeString());
		this.recordLogin(acc);
		// 更新sessionId
		try {
			if (result.isNull("sessionId")) {
				AccountData.getInstance().setSessionId("");
			} else {
				AccountData.getInstance().setSessionId(((JSONObject) niStatusData.getObj()).getString("sessionId"));
			}
		} catch (JSONException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		List<AccountDataStruct> accList = dbHelper.findAll();
		boolean existsAcc = false;
		if (accList != null && accList.size() > 0) {
			for (AccountDataStruct accTemp : accList) {
				if (acc.getUsername().equalsIgnoreCase(accTemp.getUsername())) {
					existsAcc = true;
					accTemp.setLastLoginTime(acc.getLastLoginTime());
					accTemp.setPassword(acc.getPassword());
					accTemp.setNationalNumber(acc.getNationalNumber());
					acc.copy(accTemp);
					break;
				}
			}
		}
		if (!existsAcc) {
			dbHelper.addAccount(acc);
		} else {
			dbHelper.modifyLastLoginAcc(acc.getLastLoginTime(), acc.getPassword(), acc.getUsername());
		}
		AccountData.getInstance().copy(acc);
	}

	String path = "";

	/**
	 * 找回密码
	 */
	public void findpwd(String username, String checkNum) {
		if (StringUtils.length(username) <= 0) {
			toastToMessage(this.mContext.getString(R.string.please_enter) + this.mContext.getString(R.string.mobile_or_email_binded));
			return;
		}

		new NetIFUI_ZBRJ(this.mContext, new NetInterfaceListener() {

			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				if (Constants.RES_SUCCESS.equals(niStatusData.getStatus())) {
					Message message = new Message();
					message.what = MESSAGE_FINDPWD_SUCCESS;
					mUIHandler.sendMessage(message);
				} else {
					Message message = new Message();
					message.what = MESSAGE_FINDPWD_FAIL;
					message.obj = niStatusData.getStatus();
					mUIHandler.sendMessage(message);
				}
			}

		})
		//FIXME 找回密码
//		.findPassword(username, checkNum)
		;
	}

	/**
	 * 更改密码
	 */
	public void updPassword(String oldpwd, final String newpwd, String confirmpwd) {
		if (StringUtils.length(oldpwd) <= 0) {
			toastToMessage(this.mContext.getString(R.string.please_enter) + this.mContext.getString(R.string.oldpwd));
			return;
		}
		if (StringUtils.length(newpwd) < 6) {
			toastToMessage(this.mContext.getString(R.string.newpwd) + this.mContext.getString(R.string.str_length_not_less_than, 6));
			return;
		}
		if (StringUtils.length(newpwd) > 20) {
			toastToMessage(this.mContext.getString(R.string.newpwd) + this.mContext.getString(R.string.str_length_not_more_than, 20));
			return;
		}
		// Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
		// Matcher m = p.matcher(newpwd);
		// if (!m.matches()) {
		// toastToMessage(this.mContext.getString(R.string.newpwd) +
		// this.mContext.getString(R.string.str_format_error));
		// return;
		// }
		if (!newpwd.equals(confirmpwd)) {
			toastToMessage(R.string.newpwd_confirmpwd_noequal);
			return;
		}

		new NetIFUI_ZBRJ(this.mContext, new NetInterfaceListener() {

			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				if (Constants.RES_SUCCESS.equals(niStatusData.getStatus())) {
					dbHelper.updatePassword(newpwd, AccountData.getInstance().getUsername(),
							StringUtils.repNull(AccountData.getInstance().getBindphonenumber()));
					AccountData.getInstance().setPassword(newpwd);
					MyApplication.getInstance().mPreferencesMan.setFirstReg(false);
					Message message = new Message();
					message.what = MESSAGE_UPDPWD_SUCCESS;
					mUIHandler.sendMessage(message);
				} else {
					Message message = new Message();
					message.what = MESSAGE_UPDPWD_FAIL;
					mUIHandler.sendMessage(message);
				}
			}

		})
		//FIXME 修改密码
//		.updPassword(AccountData.getInstance().getUsername(), oldpwd, newpwd, AccountData.getInstance().getSessionId())
		;
	}

	/**
	 * 同步背景图
	 */
	public void syncBg() {
		// 删除非当前背景图的图片
		String currentBgFileName = MyApplication.getInstance().mPreferencesMan.getBgFileName();
		File bgDir = this.getLocalBgDir();
		File[] bgFiles = bgDir.listFiles();
		if (!TextUtils.isEmpty(currentBgFileName) && bgFiles != null && bgFiles.length >= 2) {
			for (File bgFileTemp : bgFiles) {
				if (!currentBgFileName.equalsIgnoreCase(bgFileTemp.getName())) {
					bgFileTemp.delete();
				}
			}
		}
		// 调用"更新背景图(m1_contact_bg)"
		String url = "";//new NetInterface(this.mContext).backGround_Get(StringUtils.repNull(AccountData.getInstance().getBindphonenumber()));
		if (!TextUtils.isEmpty(url)) {
			File newBgFile = this.getLocalBgFile(url);
			// 本地是否存在对应图片
			if (newBgFile.exists()) {
				return;
			}
			// 下载图片,更新当前背景图为该图
			boolean result = HttpDownload.downLoad(url, newBgFile.getAbsolutePath(), 0, new BasicNameValuePair("", ""));
			if (result) {
				MyApplication.getInstance().mPreferencesMan.setBgFileName(newBgFile.getName());
			}
		}
	}

	/**
	 * 上传头像
	 */
	public void uploadPhoto(Bitmap bm) {
		String bContent = "";
		headBM = bm;
		if (bm != null && !bm.isRecycled()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			bContent = new String(Base64.encode(b));
		}
		new NetIFUI_ZBRJ(this.mContext, new NetInterfaceListener() {

			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				if (Constants.RES_SUCCESS.equals(niStatusData.getStatus())) {
					Message message = new Message();
					message.what = MESSAGE_UPLOADPHOTO_SUCCESS;
					mUIHandler.sendMessage(message);
				} else {
					Message message = new Message();
					message.what = MESSAGE_UPLOADPHOTO_FAIL;
					mUIHandler.sendMessage(message);
				}
			}

		}).m1_upload_image(bContent);
	}

	/**
	 * 获取背景图存放路径
	 * 
	 * @return
	 */
	public File getLocalBgDir() {
		String localPath = this.mContext.getFilesDir().getAbsolutePath() + File.separator + "pic" + File.separator;
		File file = new File(localPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取背景图
	 * 
	 * @param url
	 * @return
	 */
	private File getLocalBgFile(String url) {
		String localPath = this.mContext.getFilesDir().getAbsolutePath() + File.separator + "pic" + File.separator
				+ url.substring(url.lastIndexOf("/") + 1);
		File file = new File(localPath);
		return file;
	}

	public interface SyncListener {
		public abstract void onLogined(String status, String message, AccountData acc);

		public abstract void onCheckContactsed(String status, String sendnum, String filesize, String filedir);
	}

	public SyncListener getSyncListener() {
		return syncListener;
	}

	public void setSyncListener(SyncListener syncListener) {
		this.syncListener = syncListener;
	}

	@Override
	public void initDatabase() {
		dbHelper = new AccountHelper();
	}

	@Override
	public void onDestroy() {

	}

	public static String getLang() {
		boolean l = DeviceUtils.isZh(MyApplication.getInstance());
		if (l) {
			return "zh";
		} else {
			return "en";
		}
	}
	
	private static final int MESSAGE_FINDPWD_SUCCESS = 7;// 找回密码成功
	private static final int MESSAGE_FINDPWD_FAIL = 8;// 找回密码失败
	private static final int MESSAGE_UPDPWD_SUCCESS = 9;// 修改密码成功
	private static final int MESSAGE_UPDPWD_FAIL = 10;// 修改密码失败
	private static final int MESSAGE_UPLOADPHOTO_SUCCESS = 13;// 上传头像成功
	private static final int MESSAGE_UPLOADPHOTO_FAIL = 14;// 上传头像失败

	@SuppressLint("HandlerLeak")
	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent intent = null;
			switch (msg.what) {
			case MESSAGE_FINDPWD_SUCCESS:
				toastToMessage(R.string.findpwd_success);
				intent = AppUtil.getLoginActIntent(mContext);
				mContext.startActivity(intent);
				break;
			case MESSAGE_FINDPWD_FAIL:
				if ("2".equals(msg.obj)) {
					toastToMessage(R.string.mobile_unbinded);
				} else {
					toastToMessage(mContext.getString(R.string.findpwd) + mContext.getString(R.string.fail));
				}
				break;
			case MESSAGE_UPDPWD_SUCCESS:
				toastToMessage(mContext.getString(R.string.updpwd) + mContext.getString(R.string.success));
				((Activity) mContext).finish();
				break;
			case MESSAGE_UPDPWD_FAIL:
				toastToMessage(mContext.getString(R.string.updpwd) + mContext.getString(R.string.fail));
				break;
			case MESSAGE_UPLOADPHOTO_SUCCESS:
				toastToMessage(mContext.getString(R.string.bindheadpic) + mContext.getString(R.string.success));
				PersonController.synPersonInfo(AccountData.getInstance().getBindphonenumber(), true);
				break;
			case MESSAGE_UPLOADPHOTO_FAIL:
				toastToMessage(mContext.getString(R.string.bindheadpic) + mContext.getString(R.string.fail));
				try {
					new AlertDialog.Builder(mContext).setTitle(mContext.getString(R.string.bindheadpic) + mContext.getString(R.string.fail))
							.setMessage(R.string.reupload_or_not).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									arg0.dismiss();
									uploadPhoto(headBM);
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									arg0.dismiss();
								}
							}).show();
				} catch (Exception e) {
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCancel(DialogInterface dialog) {
	}

	class SyncBgRunnable implements Runnable {
		public void run() {
			// 同步背景图
			syncBg();
		}
	}
}