package com.lb.common.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.widget.BottomGridPopupWindow;
import com.lz.oncon.widget.ImageTextSharePopupWindow;
import com.lz.oncon.widget.InfoToast;
import com.lz.oncon.widget.ImageTextSharePopupWindow.SEND_TYPE;
import com.lz.oncon.widget.ShareGridAdapter;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.ShareType;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SimpleShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xuanbo.xuan.R;

public class ShareMenuUtil {
	private static ShareMenuUtil instance;
	
	private String titleStr, pub_accountStr, authorStr, imageUrlStr, briefStr,
	msgIdStr,url;
	private BottomGridPopupWindow bottomPopupWindow;
	private ImageTextSharePopupWindow sharePopupWindow;
	private IWXAPI api;
	private Activity activity;
	private View parentView;
	
	private boolean isShowReport = true;
	
	public static ShareMenuUtil getInstance(Activity activity){
		if(instance == null){
			instance = new ShareMenuUtil(activity);
		}
		return instance;
	}
	
	private ShareMenuUtil(Activity activity){
		this.activity = activity;
	}
	
	
	public void setShowReport(boolean isShowReport) {
		this.isShowReport = isShowReport;
	}


	/**
	 * handler处理消息机制
	 */
	private static final int ILLEGAL_REPORT = 1;
	@SuppressLint("HandlerLeak")
	protected Handler handler = new Handler() {
		public void handleMessage(Message message) {
			NetInterfaceStatusDataStruct niStatusData;
			switch (message.what) {			
			case ILLEGAL_REPORT:
				niStatusData = (NetInterfaceStatusDataStruct) message.obj;
				if (niStatusData != null) {
					String msg = "";
					if (TextUtils.isEmpty(niStatusData.getStatus())) {
						msg = activity.getString(R.string.report_this_content)
								+ activity.getString(R.string.fail);
					} else if (niStatusData.getStatus().equals("0")) {
						msg = activity.getString(R.string.report_this_content)
								+ activity.getString(R.string.success);
					} else {
						if (TextUtils.isEmpty(niStatusData.getMessage())) {
							msg = activity.getString(R.string.report_this_content)
									+ activity.getString(R.string.fail);
						} else {
							msg = niStatusData.getMessage();
						}
					}
					Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
	public void initBottomPopupWindow(View parent,String msgIdStr,String titleStr,String briefStr,String pub_accountStr,
			String authorStr,String url,String imageUrlStr,boolean isSend,int messageType) {
		parentView = parent;
		this.titleStr = titleStr;
		this.imageUrlStr = imageUrlStr;
		this.briefStr = briefStr;
		this.url = url;
		
		
		ShareUtil.registerPlatform(activity,activity.getApplicationContext());
		api = WXAPIFactory.createWXAPI(activity, Constants.APPID);
		sharePopupWindow = new ImageTextSharePopupWindow(activity);
		bottomPopupWindow = new BottomGridPopupWindow(activity);
		sharePopupWindow.setData(msgIdStr, titleStr, briefStr, pub_accountStr,
				authorStr, url, imageUrlStr, isSend,messageType);
		
		String[] nameArray = activity.getResources().getStringArray(
				R.array.share_item_name);

		TypedArray ar = activity.getResources()
				.obtainTypedArray(R.array.share_item_icon);
		int len = ar.length();
		int[] iconarray = new int[len];
		for (int i = 0; i < len; i++)
			iconarray[i] = ar.getResourceId(i, 0);
		ar.recycle();

		final List<String> nameList = new ArrayList<String>();
		List<Integer> iconList = new ArrayList<Integer>();
		boolean isWXInstalled = api.isWXAppInstalled();
		int length = isShowReport?nameArray.length:nameArray.length - 1;
		for (int i = 0; i < length; i++) {
			if (!isWXInstalled
					&& (activity.getString(R.string.share_weixin).equals(nameArray[i]) || activity.getString(
							R.string.share_wxcircle).equals(nameArray[i]))) {
				continue;
			}

			nameList.add(nameArray[i]);
			iconList.add(iconarray[i]);
		}

		ShareGridAdapter adapter = new ShareGridAdapter(activity, nameList,
				iconList);
		bottomPopupWindow.getGridView().setAdapter(adapter);
		bottomPopupWindow.getGridView().setOnItemClickListener(
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						bottomPopupWindow.dismiss();
						String platformName = nameList.get(arg2);
						if(activity.getString(R.string.cancel).equals(platformName)){
							return;
						}else if(activity.getString(R.string.share_copy_link).endsWith(platformName)){
							copyLink2Clipboard();
							return;
						}
						shareContent(platformName);
					}

					
				});	
		
	}
	@SuppressLint("NewApi")
	private void copyLink2Clipboard() {
		ClipboardManager  clipboard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
		Uri copyUri = Uri.parse(url);
        ClipData clipUri = ClipData.newUri(activity.getContentResolver(),"URI",copyUri);
        clipboard.setPrimaryClip(clipUri);
        InfoToast.makeText(activity
				, activity.getString(R.string.share_copy_link)+activity.getString(R.string.success)
				, Gravity.CENTER, 0, 0, Toast.LENGTH_SHORT).show();
	}
	public void showShareMenu(){
		if (bottomPopupWindow != null && !bottomPopupWindow.isShowing()) {			
			bottomPopupWindow.showAtLocation(parentView,
					Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
		}
		
	}
	public void dismiss(){
		if (bottomPopupWindow != null && bottomPopupWindow.isShowing()) {			
			bottomPopupWindow.dismiss();
		}
	}
	private void shareContent(String platformName) {
		SocializeConstants.SHOW_ERROR_CODE = true;
		SocializeConstants.DEBUG_MODE= true;
		SHARE_MEDIA shareMedia = null;
		SimpleShareContent shareContent = null;
		if (platformName.equalsIgnoreCase(activity.getString(R.string.share_weixin))) {
			shareMedia = SHARE_MEDIA.WEIXIN;
			shareContent = new WeiXinShareContent();
			((BaseShareContent) shareContent).setTitle(titleStr);
			shareContent.setShareImage(new UMImage(activity,
					imageUrlStr));
			shareContent.setShareContent(briefStr);
			((BaseShareContent) shareContent).setTargetUrl(url);			
		} else if (platformName
				.equalsIgnoreCase(activity.getString(R.string.share_wxcircle))) {
			shareMedia = SHARE_MEDIA.WEIXIN_CIRCLE;
			shareContent = new CircleShareContent();
			((BaseShareContent) shareContent).setTitle(titleStr);
			shareContent.setShareContent(briefStr);
			shareContent.setShareImage(new UMImage(activity,
					imageUrlStr));
			((BaseShareContent) shareContent).setTargetUrl(url);			
		}  else if (platformName
				.equalsIgnoreCase(activity.getString(R.string.share_sina))) {
			shareMedia = SHARE_MEDIA.SINA;
			shareContent = new SinaShareContent();
			/*((BaseShareContent) shareContent).setTitle(titleStr + url);
			shareContent.setShareImage(new UMImage(activity,
					imageUrlStr));
			shareContent.setShareContent(briefStr + " " + url +" ");
			((BaseShareContent) shareContent).setTargetUrl(url);*/
			((BaseShareContent) shareContent).setTitle(titleStr);
			shareContent.setShareContent(titleStr+url);
			dataDircterShare(SHARE_MEDIA.SINA ,shareContent);
			return;
			//showShareDialog(SEND_TYPE.SHARE_THIRD_PARTY,shareMedia,shareContent);
			//return;
		} else if (platformName
				.equalsIgnoreCase(activity.getString(R.string.share_tencent))) {
			shareMedia = SHARE_MEDIA.TENCENT;

			shareContent = new TencentWbShareContent();
			((BaseShareContent) shareContent).setTitle(titleStr + url);
			shareContent.setShareImage(new UMImage(activity,
					imageUrlStr));
			shareContent.setShareContent(briefStr + " " + url +" ");
			((BaseShareContent) shareContent).setTargetUrl(url);
			//showShareDialog(SEND_TYPE.SHARE_THIRD_PARTY,shareMedia,shareContent);
			//return;
		} else if (platformName
				.equalsIgnoreCase(activity.getString(R.string.share_email))) {
			shareMedia = SHARE_MEDIA.EMAIL;

			shareContent = new MailShareContent();
			((MailShareContent) shareContent).setTitle(titleStr);
			shareContent.setShareContent(briefStr + " " + url);
//			UMSocialService mUMSocialService = UMServiceFactory.getUMSocialService("android",
//					RequestType.SOCIAL);
//			mUMSocialService.getConfig().setMailSubject(titleStr);			
		} else if (platformName.equalsIgnoreCase(activity.getString(R.string.share_sms))) {
			shareMedia = SHARE_MEDIA.SMS;
			shareContent = new SmsShareContent();
			shareContent.setShareContent(briefStr + " " + url);						
		} else if (platformName
				.equalsIgnoreCase(activity.getString(R.string.share_facebook))) {
			shareMedia = SHARE_MEDIA.FACEBOOK;
		} else if (platformName
				.equalsIgnoreCase(activity.getString(R.string.share_twitter))) {
			shareMedia = SHARE_MEDIA.TWITTER;
		}else if(platformName
				.equalsIgnoreCase(activity.getString(R.string.share_zbrj))){
			showShareDialog(SEND_TYPE.SEND_FRIEND,null,null);
			return;
		}else if(platformName
				.equalsIgnoreCase(activity.getString(R.string.share_zbrj_circle))){
			showShareDialog(SEND_TYPE.SEND_FRIEND_CIRCLE,null,null);
			return;
		}else if(platformName
				.equalsIgnoreCase(activity.getString(R.string.share_zbrj_circle))){
			
		}else if(platformName
				.equalsIgnoreCase(activity.getString(R.string.share_qq))){
			shareMedia = SHARE_MEDIA.QQ;
			QQShareContent qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(briefStr);
			qqShareContent.setTitle(titleStr);
			qqShareContent.setShareMedia(new UMImage(activity,imageUrlStr));
			qqShareContent.setTargetUrl(url);
		     shareContent = qqShareContent;
			/*QQShareContent qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(url);
			dataDircterShare(SHARE_MEDIA.QQ , qqShareContent); 
			return;*/
		}else if(platformName
				.equalsIgnoreCase(activity.getString(R.string.share_qzone))){
			/*shareMedia = SHARE_MEDIA.QZONE;
			QZoneShareContent qzone = new QZoneShareContent();
			qzone.setShareContent(briefStr);
			qzone.setTargetUrl(url);
			qzone.setTitle(titleStr);
			qzone.setShareMedia(new UMImage(activity,imageUrlStr));
			shareContent = qzone;*/
		}		
		ShareUtil.shareTo(activity,shareMedia, shareContent);
	}
	private void dataDircterShare(final SHARE_MEDIA shareMedia ,final SimpleShareContent content) {
		final UMSocialService mController = UMServiceFactory.getUMSocialService(
				"com.umeng.share");	
		if(OauthHelper.isAuthenticated(activity,shareMedia) == false){
			mController.doOauthVerify(activity, shareMedia, new UMAuthListener() {
			    @Override
			    public void onStart(SHARE_MEDIA platform) {
			        Toast.makeText(activity, "授权开始", Toast.LENGTH_SHORT).show();
			    }
			    @Override
			    public void onError(SocializeException e, SHARE_MEDIA platform) {
			        Toast.makeText(activity, "授权错误", Toast.LENGTH_SHORT).show();
			    }
			    @Override
			    public void onComplete(Bundle value, SHARE_MEDIA platform) {
			    	mController.setShareMedia(content);
					//直接分享
					mController.directShare(activity, shareMedia,
					            new SnsPostListener() {
					            @Override
					            public void onStart() {
					                //Toast.makeText(activity, "分享开始",Toast.LENGTH_SHORT).show();
					            }
								@Override
								public void onComplete(SHARE_MEDIA platform,
										int eCode, SocializeEntity entity) {
									if(eCode == StatusCode.ST_CODE_SUCCESSED){
					                    Toast.makeText(activity, "分享成功",Toast.LENGTH_SHORT).show();
					                }else{
					                    Toast.makeText(activity, "分享失败"+eCode,Toast.LENGTH_SHORT).show();
					                }
								}
					    });
			    	
			       // Toast.makeText(activity, "授权完成", Toast.LENGTH_SHORT).show();
			        //获取相关授权信息或者跳转到自定义的分享编辑页面
			        //String uid = value.getString("uid");
			    }
			    @Override
			    public void onCancel(SHARE_MEDIA platform) {
			        Toast.makeText(activity, "授权取消", Toast.LENGTH_SHORT).show();
			    }
			} );
		}else{
			mController.setShareMedia(content);
			//直接分享
			mController.directShare(activity, shareMedia,
			            new SnsPostListener() {
			            @Override
			            public void onStart() {
			                //Toast.makeText(activity, "分享开始",Toast.LENGTH_SHORT).show();
			            }
						@Override
						public void onComplete(SHARE_MEDIA platform,
								int eCode, SocializeEntity entity) {
							if(eCode == StatusCode.ST_CODE_SUCCESSED){
			                    Toast.makeText(activity, "分享成功",Toast.LENGTH_SHORT).show();
			                }else{
			                    Toast.makeText(activity, "分享失败"+eCode,Toast.LENGTH_SHORT).show();
			                }
						}
			    });
		}
		
		
	}

	public static void clear(){
		instance = null;
	}
	private void showShareDialog(SEND_TYPE sendType,SHARE_MEDIA shareMedia,SimpleShareContent shareContent){
		sharePopupWindow.setType(sendType);
		sharePopupWindow.setShareMedia(shareMedia);
		sharePopupWindow.setShareContent(shareContent);
		sharePopupWindow.showAtLocation(parentView,
				Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
	}
}