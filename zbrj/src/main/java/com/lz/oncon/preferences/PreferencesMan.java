package com.lz.oncon.preferences;

import com.lz.oncon.data.AccountData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * 配置文件操作管理类
 */
public class PreferencesMan {
	// 定义配置文件名
	public final static String PREFERENCES_NAME = "zbrj.pref";
	// 定义配置文件版本号
	public final static int PREFERENCES_VERSION = 1;
	// 定义配置文件帮助类
	private PrefHelper prefHelper;
	private SharedPreferences sp = null;

	public PreferencesMan(Context context) {
		prefHelper = new PrefHelper(context);
		sp = prefHelper.getSharedPreferences();
	}
	
	/**
	 * 获取上次同步我的关注时间  for myyule
	 * @return
	 */
	public String getAttentionTime_forMyyule(String phone) {
		return sp.getString("AttentionTime_" + phone,"");
	}
	/**
	 * 设置同步我的关注时间  for myyule
	 * @param 
	 */
	public void setAttentionTime_forMyyule(String phone,String time) {
		Editor editor = sp.edit();
		editor.putString("AttentionTime_" + phone, time);
		editor.commit();
	}
	
	/**
	 * 获取上次同步我的关注时间
	 * @return
	 */
	public String getSynMyAttentionTime(String phone) {
		return sp.getString("SynMyAttentionTime_" + phone,"");
	}
	/**
	 * 设置同步我的关注时间
	 * @param 
	 */
	public void setSynMyAttentionTime(String phone,String time) {
		Editor editor = sp.edit();
		editor.putString("SynMyAttentionTime_" + phone, time);
		editor.commit();
	}
	
	/**
	 * 获取上次查询注册用户的时间
	 * @return
	 */
	public String getRegIMUserTime() {
		return sp.getString("time","");
	}
	/**
	 * 设置查询注册用户的时间
	 * @param 
	 */
	public void setRegIMUserTime(String time) {
		Editor editor = sp.edit();
		editor.putString("time", time);
		editor.commit();
	}
	
	/**
	 * 获得自动同步个人通讯录设置
	 * @return
	 */
	public boolean isSyncPC(String syncKey) {
		return sp.getBoolean(syncKey,false);
	}
	/**
	 * 设置打开或关闭自动同步个人通讯录
	 * @param avilable
	 */
	public void setSyncPC(String syncKey,boolean avilable) {
		Editor editor = sp.edit();
		editor.putBoolean(syncKey, avilable);
		editor.commit();
	}
	/**
	 * 获得同一次上传个人通讯录服务器返回时间
	 * @return
	 */
	public String getOncePCNowtime() {
		return sp.getString("sync_personalcontact_oncenowtime","0");
	}
	/**
	 * 设置同一次上传个人通讯录服务器返回时间
	 * @param lastTime
	 */
	public void setOncePCNowtime(String nowtime) {
		Editor editor = sp.edit();
		editor.putString("sync_personalcontact_oncenowtime", nowtime);
		editor.commit();
	}

	/**
	 * 获得最后一次同步个人通讯录时间
	 * @return
	 */
	public String getPutPCLasttime() {
		return sp.getString("sync_personalcontact_lasttime","0");
	}
	/**
	 * 设置最后一次同步个人通讯录时间
	 * @param lastTime
	 */
	public void setPutPCLasttime(String lastTime) {
		Editor editor = sp.edit();
		editor.putString("sync_personalcontact_lasttime", lastTime);
		editor.commit();
	}
	
	/**
	 * 获得第三方账户id，比如腾讯，新浪等等
	 * @return
	 */
	public String getSinaWeiboId() {
		return sp.getString("weibo_sina_id",null);
	}
	/**
	 * 设置第三方账户id，比如腾讯，新浪等等
	 * @param id
	 */
	public void setSinaWeiboId(String id) {
		Editor editor = sp.edit();
		editor.putString("weibo_sina_id", id);
		editor.commit();
	}
	
	/**
	 * 获得第三方账户id，比如腾讯，新浪等等
	 * @return
	 */
	public String getTencentWeiboId() {
		return sp.getString("weibo_tecent_id",null);
	}
	/**
	 * 设置第三方账户id，比如腾讯，新浪等等
	 * @param id
	 */
	public void setTencentWeiboId(String id) {
		Editor editor = sp.edit();
		editor.putString("weibo_tecent_id", id);
		editor.commit();
	}
	
	/**
	 * 设置QQ账户id
	 * @param id
	 */
	public void setQQId(String id) {
		Editor editor = sp.edit();
		editor.putString("qq_id", id);
		editor.commit();
	}
	
	/**
	 * 获得QQ账户id，
	 * @return
	 */
	public String getQQId() {
		return sp.getString("qq_id",null);
	}
	
	/**
	 * 获取当前背景图
	 * @return
	 */
	public String getBgFileName(){
		return sp.getString("bgFileName", "");
	}
	/**
	 * 设置当前背景图
	 * @param bgFileName
	 */
	public void setBgFileName(String bgFileName) {
		Editor editor = sp.edit();
		editor.putString("bgFileName", bgFileName);	
		editor.commit();
	}

	private class PrefHelper extends PerferencesHelper{

		public PrefHelper(Context context, String name, int version) {
			super(context, name, version);
		}
		
		public PrefHelper(Context context){
			this(context, PREFERENCES_NAME, PREFERENCES_VERSION);
		}
		//初始化
		public void onCreate(SharedPreferences sp) {
			Editor editor = sp.edit();
			editor.putBoolean("installation", true).commit();
		}
		//升级
		public void onUpgrade(SharedPreferences sp, int oldVersion,
				int newVersion) {
			onCreate(sp);
		}
		
	}
	
	/**
	 * 获取上次at过的新浪好友
	 * @return
	 */
	public String getSinaAted() {
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":sina_ated", "");
	}
	/**
	 * 设置sina at好友
	 * @param enter_code
	 */
	public void setSinaAted(String enter_code) {
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() +":sina_ated", enter_code);	
		editor.commit();
	}
	
	/**
	 * 获取上次at过的腾讯好友show name
	 * @return
	 */
	public String getTencentAted() {
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":tencent_ated", "");
	}
	/**
	 * 设置tencent at好友show name
	 * @param enter_code
	 */
	public void setTencentAted(String enter_code) {
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":tencent_ated", enter_code);	
		editor.commit();
	}
	
	/**
	 * 获取上次at过的腾讯好友 real name
	 * @return
	 */
	public String getTencentRealAted() {
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":tencent_real_ated", "");
	}
	/**
	 * 设置tencent at好友 real name
	 * @param enter_code
	 */
	public void setTencentRealAted(String enter_code) {
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":tencent_real_ated", enter_code);	
		editor.commit();
	}
	
	/**
	 * 获得刷新APP时间
	 */
	public String getFreshAppTime() {
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":freshAppTime", "");
	}
	/**
	 * 设置刷新APP时间
	 */
	public void setFreshAppTime(String freshAppTime) {
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":freshAppTime", freshAppTime);	
		editor.commit();
	}
	
	/**
	 * 获得刷新服务账号时间
	 */
	public String getFreshPublicAccountTime() {
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":freshPublicAccountTime", "");
	}
	/**
	 * 设置刷新服务账号时间
	 */
	public void setFreshPublicAccountTime(String freshPublicAccountTime) {
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":freshPublicAccountTime", freshPublicAccountTime);	
		editor.commit();
	}
	
	/**
	 * 获得是否初次注册
	 */
	public boolean isFirstReg() {
		return sp.getBoolean(AccountData.getInstance().getBindphonenumber() + ":isFirstReg", false);
	}
	/**
	 * 设置是否初次注册
	 */
	public void setFirstReg(boolean isFirstReg) {
		Editor editor = sp.edit();
		editor.putBoolean(AccountData.getInstance().getBindphonenumber() + ":isFirstReg", isFirstReg);	
		editor.commit();
	}
	
	/**
	 * 获得短信邀请模板
	 */
	public String getSMSInviteTemplate() {
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":smsInviteTemplate", "");
	}
	/**
	 * 设置短信邀请模板
	 */
	public void setSMSInviteTemplate(String smsInviteTemplate) {
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":smsInviteTemplate", smsInviteTemplate);	
		editor.commit();
	}
	
	/**
	 * 获得保存短信邀请模板时间
	 */
	public Long getSMSInviteTemplateTime() {
		return sp.getLong(AccountData.getInstance().getBindphonenumber() + ":smsInviteTemplateTime", 0L);
	}
	/**
	 * 设置保存短信邀请模板时间
	 */
	public void setSMSInviteTemplateTime(Long smsInviteTemplateTime) {
		Editor editor = sp.edit();
		editor.putLong(AccountData.getInstance().getBindphonenumber() + ":smsInviteTemplateTime", smsInviteTemplateTime);	
		editor.commit();
	}
	
	/**
	 * 获得邮件邀请模板
	 */
	public String getMailInviteTemplate() {
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":mailInviteTemplate", "");
	}
	/**
	 * 设置邮件邀请模板
	 */
	public void setMailInviteTemplate(String mailInviteTemplate) {
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":mailInviteTemplate", mailInviteTemplate);	
		editor.commit();
	}
	
	/**
	 * 获得保存邮件邀请模板时间
	 */
	public Long getMailInviteTemplateTime() {
		return sp.getLong(AccountData.getInstance().getBindphonenumber() + ":MailInviteTemplateTime", 0L);
	}
	/**
	 * 设置保存邮件邀请模板时间
	 */
	public void setMailInviteTemplateTime(Long mailInviteTemplateTime) {
		Editor editor = sp.edit();
		editor.putLong(AccountData.getInstance().getBindphonenumber() + ":mailInviteTemplateTime", mailInviteTemplateTime);	
		editor.commit();
	}
	
	/**
	 * 获得微博邀请模板
	 */
	public String getWeiboInviteTemplate() {
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":WeiboInviteTemplate", "");
	}
	/**
	 * 设置微博邀请模板
	 */
	public void setWeiboInviteTemplate(String weiboInviteTemplate) {
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":weiboInviteTemplate", weiboInviteTemplate);	
		editor.commit();
	}
	
	/**
	 * 获得保存邮件邀请模板时间
	 */
	public Long getWeiboInviteTemplateTime() {
		return sp.getLong(AccountData.getInstance().getBindphonenumber() + ":weiboInviteTemplateTime", 0L);
	}
	/**
	 * 设置保存邮件邀请模板时间
	 */
	public void setWeiboInviteTemplateTime(Long weiboInviteTemplateTime) {
		Editor editor = sp.edit();
		editor.putLong(AccountData.getInstance().getBindphonenumber() + ":weiboInviteTemplateTime", weiboInviteTemplateTime);	
		editor.commit();
	}
	

	public boolean isOpenDisturbModel() {
		return sp.getBoolean("isOpenDisturbModel",false);//默认关
	}

	public void setOpenDisturbModel(boolean isOpenDisturbModel) {
		Editor editor = sp.edit();
		editor.putBoolean("isOpenDisturbModel", isOpenDisturbModel);
		editor.commit();
	}

	public String getDisturbStartTime() {
		return sp.getString("disturbStartTime","");
	}

	public void setDisturbStartTime(String disturbStartTime) {
		Editor editor = sp.edit();
		editor.putString("disturbStartTime", disturbStartTime);
		editor.commit();
	}

	public String getDisturbEndTime() {
		return sp.getString("disturbEndTime","");
	}

	public void setDisturbEndTime(String disturbEndTime) {
		Editor editor = sp.edit();
		editor.putString("disturbEndTime", disturbEndTime);
		editor.commit();
	}

	public boolean isOpenHandLockModel() {
		return sp.getBoolean("isOpenHandLockModel",false);//默认关
	}

	public void setOpenHandLockModel(boolean isOpenHandLockModel) {
		Editor editor = sp.edit();
		editor.putBoolean("isOpenHandLockModel", isOpenHandLockModel);
		editor.commit();
	}

	public int getHandLockType() {
		return sp.getInt("handLockType",0);// 0自动锁定 ;1手动锁定
	}

	public void setHandLockType(int handLockType) {
		Editor editor = sp.edit();
		editor.putInt("handLockType", handLockType);
		editor.commit();
	}
	
	//经度
	public void setLongitude(String longitude){
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":longitude", longitude);	
		editor.commit();
	}
		
	public String getLongitude(){
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":longitude", "");
	}
		
	//纬度
	public void setLatitude(String latitude){
		Editor editor = sp.edit();
		editor.putString(AccountData.getInstance().getBindphonenumber() + ":latitude", latitude);	
		editor.commit();
	}
	
	public String getLatitude(){
		return sp.getString(AccountData.getInstance().getBindphonenumber() + ":latitude", "");
	}
	
	//同步频道时间
	public void setSynChannelTime(long time){
		Editor editor = sp.edit();
		editor.putLong(AccountData.getInstance().getBindphonenumber() + ":synchanneltime", time);	
		editor.commit();
	}
						
	public long getSynChannelTime(){
		return sp.getLong(AccountData.getInstance().getBindphonenumber() + ":synchanneltime", 0);
	}
		
	//同步黑名单时间
	public void setSynBlackTime(long time){
		Editor editor = sp.edit();
		editor.putLong(AccountData.getInstance().getBindphonenumber() + ":synblacktime", time);	
		editor.commit();
	}
					
	public long getSynBlackTime(){
		return sp.getLong(AccountData.getInstance().getBindphonenumber() + ":synblacktime", 0);
	}
	
	//同步粉丝时间
	public void setSynFansTime(long time){
		Editor editor = sp.edit();
		editor.putLong(AccountData.getInstance().getBindphonenumber() + ":synfanstime", time);	
		editor.commit();
	}
						
	public long getSynFansTime(){
		return sp.getLong(AccountData.getInstance().getBindphonenumber() + ":synfanstime", 0);
	}
		
	//同步关注时间
	public void setSynFocusTime(long time){
		Editor editor = sp.edit();
		editor.putLong(AccountData.getInstance().getBindphonenumber() + ":synfocustime", time);	
		editor.commit();
	}
						
	public long getSynFocusTime(){
		return sp.getLong(AccountData.getInstance().getBindphonenumber() + ":synfocustime", 0);
	}
}