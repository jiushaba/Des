/**
*
*  ProjectName: oncon
*  ClassName:IMUtil
*  Description: 
*  Author: Steven
*  Date:2012-12-10 上午11:19:23
*
*/
package com.lz.oncon.app.im.util;

import java.io.File;

import com.lb.common.util.Constants;

import android.os.Environment;

public class IMConstants {
	
	public static final int SORTNO_DEFAULT = 0;
	public static final int RESPONSE_FINISH = 1;
	public static final String SEX_MALE = "1";
	public static final String SEX_SECRET = "-1";
	public static final String SEX_FEMALE = "0";
	public static final String SUFFIX_SYNC_PC  = "_sync_personalcontact";
	
	public static final String PATH_NEWS_PICTURE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.LOG_TAG
			+File.separator+"pic"+File.separator+"news"+File.separator;
			
	public static final String PATH_PERSONALCONTACT_DB = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.LOG_TAG
					+File.separator+"databases";
			
	public static final String PATH_FACE_PICTURE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.LOG_TAG
					+File.separator+"pic"+File.separator+"face"+File.separator;
	public static final String NEW_VERSIONAPK_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.LOG_TAG
					+File.separator+"newversion"+File.separator;
	public static final String PATH_APPAD_PICTURE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.LOG_TAG
			+File.separator+"pic"+File.separator;
	static {
		while(! Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		File dbFolder = new File(PATH_PERSONALCONTACT_DB);
		// 目录不存在则自动创建目录
		if (!dbFolder.exists()){
			dbFolder.mkdirs();
		}
	}

	public static final int PAGE_SIZE = 10;
	public static final int CHOOSER_ADDMEMBER = 3003;
	public static final int CHOOSER_SUBMEMBER = 3004;
	public static final int CHOOSER_CLOSEDIALOG = 3005;
	public static final int COLOSE_DIALOG = 3006;
	public static final int COLOSE_DIALOG_BYFAILED = 3007;
	public static final int ADAPTER_FRESH = 3008;
	public static final int SEARCH_CHOOSER_ADDMEMBER = 3009;
	public static final int SEARCH_CHOOSER_SUBMEMBER = 3010;
	public static final int CLOSE_PROGRESSBAR = 3011;
	public static final int NO_PERMISSION = 3012;
	public static final int LEAVE_GROUP = 3013;
	public static final int LOAD_FROM_SERVER = 3014;
	public static final int lOAD_FROM_SDCARD = 3015;
	public static final int NEWS_LOADURL_ERROR = 3016;
	public static final int NEWS_LOADURL_FINISHNORMAL = 3017;
	public static final int REQUEST_COUNTRY_CODE = 3018;
	public static final int REQUEST_COUNTRY_CODE_OK = 3019;
	public static final int REQUEST_COUNTRY_CODE_CANCEL = 3020;
	public static final int COUNTRY_CODE_INIT = 3021;
	public static final int COUNTRY_CODE_SEARCH = 3022;
	public static final int NOTIFY_UPDATE = 3023;
	public static final int lOAD_FROM_APK = 3024;
	public static final int SEND_INVITATION = 3025;
	
	public static final int SHOW_DIALOG = 3026;
	
	public static final int SHOW_ALL_BUTTON = 3027;
	public static final int SHOW_CANCAL_BUTTON = 3028;
	
	public static final int DISMISSDIALOG = 9999;
	
	public static final String SAVE_TO_CONTACT = "1";
	public static final String UNSAVE_TO_CONTACT = "0";
	public static final String COUNTRY_CODE_CHINA = "0086";
	public static final String COUNTRY_CODE_SCHINA = "+86";
	
	
	public static  String globalThirdPartyType;
	public static final String WEIBO_TYPE_SINA = "0";
	public static final String WEIBO_TYPE_TECENT = "1";
	public static final String TYPE_QQ = "5";
	public static final String WEIBO_TYPE_PHONE = "2";
	public static final String WEIBO_ADVERTISEMENT_TITLE_SINA = "#直播日记#，指尖上的快乐生活和工作。立即体验>> http://www.on-con.com (@直播日记_掌中云)";
	public static final String WEIBO_ADVERTISEMENT_TITLE_QQ = "#直播日记#，指尖上的快乐生活和工作。立即体验>> http://www.on-con.com (@直播日记)";
	public static final String WEIBO_DEVICE_ANDROID = "Android";
	public static final String WEIBO_DEVICE_TYPE_IOS = "ios";
	public static final String NEWS_ATTR_ID = "id";
	public static final String NEWS_ATTR_TYPE = "type";
	public static final String NEWS_ATTR_NAME = "name";
	public static final String NEWS_ATTR_BRIEF = "brief";
	public static final String NEWS_ATTR_URL = "url";
	public static final String NEWS_ATTR_IMAGE = "image";
	public static final String NEWS_ATTR_EXTRACOUNT = "count";
	public static final String NEWS_ATTR_NEWSLIST = "newslist";
	public static final String KEY_NEWS_URL = "key_news_url";
	public static final String KEY_NEWS_TITLE = "key_news_title";
	public static final String KEY_BACK_ABOUTACTIVITY = "AboutActivity";
	public static final String KEY_COUNTRY_CODE = "key_country_code";
	public static final String KEY_COUNTRY_CODE2 = "key_country_code2";
	public static final String KEY_CONTACTINFO_NAME = "key_contactinfo_name";
	public static final String MSG_MODE_REG = "key_mode_regist";
	public static final String MSG_MODE_BIND = "key_mode_bind";
	
	

}

