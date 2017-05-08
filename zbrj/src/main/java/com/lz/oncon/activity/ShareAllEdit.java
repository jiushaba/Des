package com.lz.oncon.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.lb.common.util.Constants;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lz.oncon.app.im.util.WeiXinShareUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.SMSTemplateData;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMShareMsg;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.OauthHelper;

public class ShareAllEdit extends BaseActivity {
	private ImageView iv_pic;
	private EditText et_content;
	private ImageView iv_sina;
	private ImageView iv_tencent;
	private FrameLayout fl_sina;
	private FrameLayout fl_tencent;
	private Intent intent;
	private Bundle bundle;
	private int message_type;

	private String text;
	private String image_path;
	private String image_thum;
	private Bitmap bitmap_image;

	private boolean flag_sian = false;
	private boolean flag_tencent = false;

	public static final int REQUEST_AT = 21;
	private String sina_at_name = "";
	private String tencent_at_name = "";

	private SocializeConfig config;
	private DisplayImageOptions options;
	private SMSTemplateData template;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initController();
		initContentView();
		initView();
		setValue();

		config = MyApplication.getInstance().umService.getConfig();
		
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.logo)
		.showImageOnFail(R.drawable.logo).cacheInMemory(true)
		.cacheOnDisc(true).build();
	}

	public void initController() {
		intent = getIntent();
		bundle = intent.getExtras();
		if (bundle != null) {
			message_type = bundle.getInt("MESSAGE_TYPE");
		}
	}

	public void initContentView() {
		setContentView(R.layout.share_all_edit);
	}

	public void initView() {
		iv_pic = (ImageView) findViewById(R.id.share_all_picture);
		et_content = (EditText) findViewById(R.id.share_all_input);
		iv_sina = (ImageView) findViewById(R.id.share_all_sina_iv);
		iv_tencent = (ImageView) findViewById(R.id.share_all_tencent_iv);
		fl_sina = (FrameLayout) findViewById(R.id.share_all_circle_right_sina);
		fl_tencent = (FrameLayout) findViewById(R.id.share_all_circle_right_tencent);
	}

	public void setValue() {
		switch (message_type) {
		case WeiXinShareUtil.TEXT:
			text = bundle.getString("MESSAGE_CONTENT");
			et_content.setText(text);
			break;
		case WeiXinShareUtil.IMAGE:
			image_path = bundle.getString("MESSAGE_IMAGE_PATH");
			image_thum = bundle.getString("MESSAGE_IMAGE_THUM");
			if (image_path != null || !"".equals(image_path)) {
				bitmap_image = BitmapFactory.decodeFile(image_path);
				if (bitmap_image == null) {
					bitmap_image = BitmapFactory.decodeFile(image_thum);
				}
				iv_pic.setImageBitmap(bitmap_image);
				iv_pic.setVisibility(View.VISIBLE);
			}
			break;
		case WeiXinShareUtil.TALK_IMAGE:
			image_path = bundle.getString("MESSAGE_IMAGE_PATH");
			image_thum = bundle.getString("MESSAGE_IMAGE_THUM");
			if (image_path != null || !"".equals(image_path)) {
				bitmap_image = BitmapFactory.decodeFile(image_path);
				if (bitmap_image == null) {
					bitmap_image = BitmapFactory.decodeFile(image_thum);
				}
				iv_pic.setImageBitmap(bitmap_image);
				iv_pic.setVisibility(View.VISIBLE);
			}
			break;
		case WeiXinShareUtil.DYN_EXP:
			break;
		case WeiXinShareUtil.AD:
//			bitmap_image = ((BitmapDrawable) getResources().getDrawable(
//					R.drawable.weibo_advertisement)).getBitmap();
//			iv_pic.setImageResource(R.drawable.weibo_advertisement);
//			iv_pic.setVisibility(View.VISIBLE);
//			text = getResources()
//					.getString(R.string.group_invite_weibo_content);
//			et_content.setText(text);
			getInviteMessage();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_AT:
				Bundle b = data.getExtras();
				if (b != null) {
					sina_at_name = b.getString("at_sina");
					tencent_at_name = b.getString("at_tencent");
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch (arg0.getId()) {
		case R.id.common_title_TV_left: // back
			ShareAllEdit.this.finish();
			break;
		case R.id.common_title_TV_right: // share
			if (flag_sian == false && flag_tencent == false) {
				toastToMessage("没有进行分享操作");
				ShareAllEdit.this.finish();
				return;
			}

			// 分享
			shareToSinaTencent();
			ShareAllEdit.this.finish();

			break;
		case R.id.share_all_icon_iv: // @friends
			if (flag_sian == false && flag_tencent == false) {
				toastToMessage(R.string.share_choose_one_playform);
				return;
			}

			Intent atIntent = new Intent(ShareAllEdit.this,
					ShareTabSocialChooseActivity.class);
			Bundle b = new Bundle();
			b.putBoolean("sina", flag_sian);
			b.putBoolean("tencent", flag_tencent);
			atIntent.putExtras(b);
			startActivityForResult(atIntent, REQUEST_AT);

			break;
		case R.id.share_all_sina_iv: // sina share
			if (fl_sina.getVisibility() == View.VISIBLE) {
				iv_sina.setImageResource(R.drawable.sina_black);
				fl_sina.setVisibility(View.GONE);
				flag_sian = false;
			} else { // oauth sina
				if (OauthHelper.isAuthenticatedAndTokenNotExpired(
						ShareAllEdit.this, SHARE_MEDIA.SINA)) { // 已授权，图片变亮
					iv_sina.setImageResource(R.drawable.sina_light);
					fl_sina.setVisibility(View.VISIBLE);
					flag_sian = true;
				} else { // 未授权，提示是否授权
					oauth(SHARE_MEDIA.SINA);
				}
			}
			break;
		case R.id.share_all_tencent_iv: // tencent share
			if (fl_tencent.getVisibility() == View.VISIBLE) {
				iv_tencent.setImageResource(R.drawable.tencent_black);
				fl_tencent.setVisibility(View.GONE);
				flag_tencent = false;
			} else { // oauth tencent
				if (OauthHelper.isAuthenticatedAndTokenNotExpired(
						ShareAllEdit.this, SHARE_MEDIA.TENCENT)) { // 已授权，图片变亮
					iv_tencent.setImageResource(R.drawable.tencent_light);
					fl_tencent.setVisibility(View.VISIBLE);
					flag_tencent = true;
				} else { // 未授权，提示是否授权
					oauth(SHARE_MEDIA.TENCENT);
				}
			}

			break;

		default:
			break;
		}
	}

	private void getInviteMessage() {
		long lastRequestTime = MyApplication.getInstance().mPreferencesMan.getWeiboInviteTemplateTime();
		if(lastRequestTime > 0 && (System.currentTimeMillis() - lastRequestTime ) < 24*60*60*1000){
			String content = MyApplication.getInstance().mPreferencesMan.getWeiboInviteTemplate();
			if(!TextUtils.isEmpty(content)){
				String splitContent[] = content.split("|||");
				et_content.setText(splitContent[0]);
				if(splitContent.length > 1 && !TextUtils.isEmpty(splitContent[1])){
					iv_pic.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(splitContent[1], iv_pic, options, null);
				}
			}
		}else{
		}
	}

	public void shareToSinaTencent() {
		// 分享的文本内容
		UMShareMsg umsm;

		// byte[] array = null;
		// if (bitmap_image != null) {
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// bitmap_image.compress(Bitmap.CompressFormat.PNG, 100, out);
		// array = out.toByteArray();
		// }

		if (flag_sian) {
			umsm = new UMShareMsg();
			if(null != template){
				umsm.setMediaData(new UMImage(ShareAllEdit.this, template.img));
			}else{
				umsm.setMediaData(new UMImage(ShareAllEdit.this, bitmap_image));
			}
			

			if (sina_at_name == null) {
				sina_at_name = "";
			}

			String share_text = et_content.getText().toString();
			share_text = sina_at_name + share_text;
			int sum = StringUtils.strlen(share_text);
			int slength = 140 - StringUtils
					.strlen(getString(R.string.sharefrom_sina)); // 119
			int a;
			while (sum > slength) {
				a = share_text.length();
				share_text = share_text.substring(0, a - 1);
				sum = StringUtils.strlen(share_text);
			}
			umsm.mText = share_text + getString(R.string.sharefrom_sina);
			MyApplication.getInstance().umService.setShareContent(umsm.mText);
			config.addFollow(SHARE_MEDIA.SINA, Constants.WEIBO_SINA_GOVEMENT_ID);
			MyApplication.getInstance().umService.setConfig(config);
			MyApplication.getInstance().umService.postShare(ShareAllEdit.this,
					SHARE_MEDIA.SINA, new SnsPostListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onComplete(SHARE_MEDIA arg0, int arg1,
								SocializeEntity arg2) {
							if (arg1 == 200) {
								toastToMessage(R.string.share_sina_s);
							} else {
								toastToMessage(getString(R.string.share_sina_f)
										+ arg1);
							}
						}
					});
		}

		if (flag_tencent) {
			umsm = new UMShareMsg();
			if(null != template){
				umsm.setMediaData(new UMImage(ShareAllEdit.this, template.img));
			}else{
				umsm.setMediaData(new UMImage(ShareAllEdit.this, bitmap_image));
			}

			if (tencent_at_name == null) {
				tencent_at_name = "";
			}

			String share_text = et_content.getText().toString();
			share_text = tencent_at_name + share_text;
			int sum = StringUtils.strlen(share_text);
			int tlength = 140 - StringUtils
					.strlen(getString(R.string.sharefrom_tencent));
			int a;
			while (sum > tlength) {
				a = share_text.length();
				share_text = share_text.substring(0, a - 1);
				sum = StringUtils.strlen(share_text);
			}
			umsm.mText = share_text + getString(R.string.sharefrom_tencent);
			MyApplication.getInstance().umService.setShareContent(umsm.mText);
			config.addFollow(SHARE_MEDIA.TENCENT,
					Constants.WEIBO_QQ_GOVEMENT_ID);
			MyApplication.getInstance().umService.setConfig(config);
			MyApplication.getInstance().umService.postShare(ShareAllEdit.this,
					SHARE_MEDIA.TENCENT, new SnsPostListener() {
						@Override
						public void onStart() {
						}

						@Override
						public void onComplete(SHARE_MEDIA arg0, int arg1,
								SocializeEntity arg2) {
							if (arg1 == 200) {
								toastToMessage(R.string.share_tencent_s);
							} else {
								toastToMessage(getString(R.string.share_tencent_f)
										+ arg1);
							}
						}
					});
		}
	}

	// 授权操作
	public void oauth(final SHARE_MEDIA platform) {
		MyApplication.getInstance().umService.doOauthVerify(ShareAllEdit.this,
				platform, new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA arg0) {
					}

					@Override
					public void onError(SocializeException arg0,
							SHARE_MEDIA arg1) {

					}

					@Override
					public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
						if (platform == SHARE_MEDIA.SINA) {
							mHandler.sendEmptyMessage(OAUTH_SUCCESS_SINA);
						}
						if (platform == SHARE_MEDIA.TENCENT) {
							mHandler.sendEmptyMessage(OAUTH_SUCCESS_TENCENT);
						}
					}

					@Override
					public void onCancel(SHARE_MEDIA arg0) {
					}
				});

	}

	private static final int OAUTH_SUCCESS_SINA = 9001; // 新浪授权成功
	private static final int OAUTH_SUCCESS_TENCENT = 9002; // 腾讯授权成功
	private UIHandler mHandler = new UIHandler();

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case OAUTH_SUCCESS_SINA:
				toastToMessage(R.string.oauth_success);
				iv_sina.setImageResource(R.drawable.sina_light);
				fl_sina.setVisibility(View.VISIBLE);
				flag_sian = true;
				break;
			case OAUTH_SUCCESS_TENCENT:
				toastToMessage(R.string.oauth_success);
				iv_tencent.setImageResource(R.drawable.tencent_light);
				fl_tencent.setVisibility(View.VISIBLE);
				flag_tencent = true;
				break;
			default:
				break;
			}
		}
	}

}
