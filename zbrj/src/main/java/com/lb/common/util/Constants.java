package com.lb.common.util;

import android.os.Environment;

public class Constants {
	
	public static final String APPID = "wxfa4423edf8c60c48"; // 微信appid
	public static final String APPSECRET = "b1653fc1fa91de51801292f6ed88c602";//微信key
	public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001; // 微信支持朋友圈最低版本号
	
	public static final String QQ_APPID = "1104729917"; // qq appid
	public static final String QQ_APPKEY = "mf6IFwgqLUf7440S"; // qq app key
	//日志TAG
	public static final String LOG_TAG = "com.xuanbo.xuan";
	public static String PACKAGENAME = "com.xuanbo.xuan";
	
	public static String AESPassword = "lur8apa4zu484pvj"; //AES解密秘钥
	//RSA加密公钥
	public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC30/EAXW7zg/dexXwxQs27nsx19YgjJgtQKtzU5dkBvHjPJn6ISltjfkFLpA/1Zk3/FDhJyI+J+tpZr4H2aeyTjIPcv1PVBMT7hFre7MihIu2vnJPqTvJU6qt40menki9K/+kdwEoS5iEo96NLbWDWKb+my0+bcG8+xRl7kYleSwIDAQAB";
		
	public static int search_in_out = 0;
	public static int im_search_in_out = 0;
	
	public static final String FEEDBACK_AND_ADVICE = "gz_e6dfe079df62786d";  //咨询与建议公众号.. 替代800
	public static final String NO_901 = "901";
	public static final String NO_900 = "900";
	
	public static final String LOCING = "0";//定位中
	public static final String LOCED = "1";//定位结束
	
	
	// 编码方式
	public static final String ENCODE = "UTF-8";
	
	public static final String DEFAULT_NATIONAL_CODE = "0086";
	
	public static final String DIRECT_PER_VR_DEPT = "-999";
	
	// 设备类型
	public static final String DEVICE_TYPE = "phone";
	
	public static final String DEVICE_OS_TYPE = "android";
	
	public static final String RES_NET_ERROR = "-1";
	public static final String RES_NET_SOCKET_EXCEPTION = "-2";
	public static final String RES_SUCCESS = "0";
	public static final String RES_FAIL = "1";
	public static final String RES_NO_ORG = "5";
	public static final String RES_BINDED = "2";
	public static final String RES_UNKNOWN = "unknown";
	
	public static final int CAMERA_RESULT_CODE = 1001;
	public static final int GALLERY_RESULT_CODE = 1002;
	public static final int CORP_PHOTO_CODE = 2002;    //剪切图片CODE
	public static final int REQ_AREA = 10000;
	
	public static final int ActivitytoMorePicActivity = 200100;
	
	public static final int PicAlbum2PicChoose = 900100;
	public static final int PicChoose2PicPreview = 900200;
	
	//标签分隔符
	public static final String LABEL_SPLIT = "\n";
	
	// 请求、响应动作
	public static final String ACTION_REQ = "request"; // 请求动作	
	public static final String ACTION_RES = "response"; // 响应动作

	
	public static final String WEIBO_SINA_GOVEMENT_ID = "1243434295"; // 直播日记在新浪的官方微博id
	public static final String WEIBO_QQ_GOVEMENT_ID = "on-con"; // 直播日记在腾讯的官方微博id
	
	public static final String HTTP_CONTENT_TYPE_TEXT = "text/html;charset=UTF-8"; // 
	public static final String HTTP_CONTENT_TYPE_DEFAULT = "application/x-www-form-urlencoded"; // 
		
	public static final String KW_USERNAME = "username";
	public static final String KW_PASSWORD = "password";
	
	public static final int REQUEST_CODE_MORE_APP_DETAIL = 10001;
	public static final int REQUEST_CODE_IM_MULTI_IMGS = 10002;
	public static final int REQUEST_CODE_PERSON_SET_MEMO = 20001;
	public static final int REQUEST_CODE_PERSON_SET_SIGN = 20002;
	public static final int REQUEST_CODE_PERSON_SET_INFO = 20003;
	public static final String ENTERPRISE_DEMO_CODE = "9999";
	public static final String lz_post_id= "yx_e8d9b0a1a9379d68";
	
	
	// 四个底部button对应的Activity
	public static enum ActivityState {
		EnterAddressBook, MessageCenter, AppCentre, More;
	}
	
	// Intent传递MemberData序列化的key
	public static final String SER_KEY = "memberToDetail";
	
	public static final String LISTENER_CONTACT_PHOTO_UPLOAD = "LISTENER_CONTACT_PHOTO_UPLOAD";
	public static final String LISTENER_PUBLIC_ACCOUNT_SYN = "LISTENER_PUBLIC_ACCOUNT_SYN";
	public static final String LISTENER_APP_NOTI = "LISTENER_APP_NOTI";
	public static final String LISTENER_FC_NOTI = "LISTENER_FC_NOTI";//朋友圈监听
	public static final String LISTENER_BLACK = "LISTENER_BLACK";//黑名单
	public static final String LISTENER_CHANNEL = "LISTENER_CHANNEL";//频道
	public static final String LISTENER_FANS = "LISTENER_FANS";//粉丝
	public static final String LISTENER_FOCUS = "LISTENER_FOCUS";//关注
	public static final String LISTENER_SYN_PERSONINFO = "LISTENER_SYN_PERSONINFO";//获取个人信息
	
	public static final String LISTENER_UPDATE_LISTVIEW_AFTER_SYNC_MSG = "LISTENER_UPDATE_LISTVIEW_AFTER_SYNC_MSG"; // 同步最近通讯录后更新UI监听的KEY

	public static final String PAGE_SIZE = "10";//分页获取数据时每页数量
	public static final int PAGE_SIZE_INT = 10;//分页获取数据时每页数量
	
	public static final String THUMB_BIG = Environment.getExternalStorageDirectory()
			+ "/" + Constants.PACKAGENAME + "/pic/thumb0/";
	public static final String THUMB_SMALL = Environment.getExternalStorageDirectory()
			+ "/" + Constants.PACKAGENAME + "/pic/thumb1/";
}
