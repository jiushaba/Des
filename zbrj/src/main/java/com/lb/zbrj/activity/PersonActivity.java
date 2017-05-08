package com.lb.zbrj.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import com.lb.common.util.Constants;
import com.lb.common.util.DateUtil;
import com.lb.common.util.ImageUtil;
import com.lb.common.util.Log;
import com.lb.common.util.corpimage.CropImage;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.controller.GetPersonInfoThread;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.HeadBigActivity;
import com.lz.oncon.activity.RegisterAllActivity;
import com.lz.oncon.activity.SetPasswordActivity;
import com.lz.oncon.activity.fc.selectimage.Fc_PicConstants;
import com.lz.oncon.activity.friendcircle.FriendCircleActivity;
import com.lz.oncon.api.CustomProtocolListener;
import com.lz.oncon.api.SIXmppP2PInfo;
import com.lz.oncon.app.im.contact.ContactMsgCenterActivity;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.ui.IMMessageHistorySearchActivity;
import com.lz.oncon.app.im.ui.IMMessageListActivity;
import com.lz.oncon.app.im.util.SystemCamera;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog;
import com.lz.oncon.widget.HeadImageView;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog.OnChoiceClickListener;

public class PersonActivity extends BaseActivity implements SynPersonInfoListener, CustomProtocolListener {
	private HeadImageView headIv;
	private TextView nicknamev;
	private TextView fansV, focusV, locationV, sexV, oldV, underHeadV, videoV, signV;
	private PersonController mPersonController;
	private String mobile = "";
	private PersonData person;
	private SIXmppP2PInfo mP2PInfo;
	private AlertDialog aChangeAccDialog, aExitAppDialog;
	private Builder choiceExitDialog;
	private String[] quitStr;
	private BaseController mController;
	private Bitmap b, tempb;
	private String videoCount="0";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView(R.layout.activity_person);
		mPersonController = new PersonController();
		mController = new AccountController(this);
		initViews();
		setValues();
		MyApplication.getInstance().addListener(Constants.LISTENER_SYN_PERSONINFO, this);
		ImCore.getInstance().getConnection().addCustomProtocolListener(this);
		getBaseInfoFromServer();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().removeListener(Constants.LISTENER_SYN_PERSONINFO, this);
		ImCore.getInstance().getConnection().removeCustomProtocolListener(this);
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if(requestCode == Constants.REQUEST_CODE_PERSON_SET_SIGN
				&& resultCode == RESULT_OK){
			person = mPersonController.findPerson(mobile);
			setPersonInfo();
		}else if(requestCode == Constants.REQUEST_CODE_PERSON_SET_MEMO
				&& resultCode == RESULT_OK){
			nicknamev.setText(mPersonController.findNameByMobile(person.account));
		}else if(requestCode == Constants.REQUEST_CODE_PERSON_SET_INFO){
			MyApplication.getInstance().personThreadPool.submit(new GetPersonInfoThread(mobile, true));
		}else{
			try {
				
					switch (requestCode) {
					case Constants.CAMERA_RESULT_CODE:
						if (data != null) {
							if (tempb != null && !tempb.isRecycled())
								tempb.recycle();
							String filePath = CameraGalleryWithClearChoiceDialog.getFilePath();
							tempb = ImageUtil.loadBitmap(filePath, true);
							SystemCamera.getCropHeadImageIntent(this, tempb);
							SystemCamera.captureFilePath = null;
						} else {
							if (!CropImage.flag) {
								if (tempb != null && !tempb.isRecycled())
									tempb.recycle();
								String filePath = CameraGalleryWithClearChoiceDialog.getFilePath();
								tempb = ImageUtil.loadBitmap(filePath, true);
								SystemCamera.getCropHeadImageIntent(this, tempb);
							} else {
								CropImage.flag = false;
							}
						}
						break;
					case Constants.GALLERY_RESULT_CODE:
						String photopathString = "";
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
							Uri selectedImageUri = data.getData();  
							photopathString = ImageUtil.getPath(this, selectedImageUri);
						}else {
							if (tempb != null && !tempb.isRecycled())
								tempb.recycle();
							Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
							cursor.moveToFirst();
							photopathString = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
							if (cursor != null) {
								cursor.close();
							}
						}
						tempb = ImageUtil.loadBitmap(photopathString, true);
						SystemCamera.getCropHeadImageIntent(this, tempb);
						break;
					case Constants.CORP_PHOTO_CODE:
						if(resultCode == RESULT_OK){
							readPhotoInfo(data);
							((AccountController) mController).uploadPhoto(b);
						}else{
							CropImage.flag = false;
						}
						break;
					default:
						break;
					}
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			} catch (Error e){
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
	}

	public void initViews() {
		headIv = (HeadImageView) findViewById(R.id.mng_selfinfo_IV_headpic);
		nicknamev = (TextView) findViewById(R.id.nickname_v);
		fansV = (TextView) findViewById(R.id.fans_num);
		focusV = (TextView) findViewById(R.id.focus_num);
		locationV = (TextView) findViewById(R.id.location);
		sexV = (TextView) findViewById(R.id.sex_v);
		oldV = (TextView) findViewById(R.id.old);
		underHeadV = (TextView) findViewById(R.id.btn_under_head);
		videoV = (TextView) findViewById(R.id.video_num);
		signV = (TextView) findViewById(R.id.sign);
		
		aChangeAccDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.changeaccount)
		.setMessage(R.string.dialog_change_account_message)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				Message message = new Message();
				message.what = CHANGE_ACCOUNT;
				mUIHandler.sendMessage(message);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		}).create();
	
		aExitAppDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.exit).setMessage(R.string.dialog_message)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				Message message = new Message();
				message.what = EXIT_APP;
				mUIHandler.sendMessage(message);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		}).create();
	
		choiceExitDialog = new Builder(this);
	}

	public void setValues() {
		quitStr = new String[] {getString(R.string.changeaccount), getString(R.string.exit)};
		
		mobile = getIntent().hasExtra("mobile") ? getIntent().getStringExtra("mobile") : AccountData.getInstance().getBindphonenumber();
		person = mPersonController.findPerson(mobile);
		mP2PInfo = ImData.getInstance().p2p_query(mobile);
		setPersonInfo();
		if(mobile.equals(AccountData.getInstance().getBindphonenumber())){
		}else{
			findViewById(R.id.exitLL).setVisibility(View.GONE);
			findViewById(R.id.findPwdLL).setVisibility(View.GONE);
			findViewById(R.id.edit).setVisibility(View.GONE);
			findViewById(R.id.edit_label).setVisibility(View.GONE);
			findViewById(R.id.menubar).setVisibility(View.VISIBLE);
			findViewById(R.id.find_msg_recordLL).setVisibility(View.VISIBLE);
			if(mPersonController.isFocused(mobile)){//FIXME 特别关注如何判断,接口只有设置关注有是否特别,取消和查询都没有
//				findViewById(R.id.add_special_friend).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.btn_under_head)).setText(R.string.cancel_focus);
			}else{
//				findViewById(R.id.add_special_friend).setVisibility(View.GONE);
				((TextView)findViewById(R.id.btn_under_head)).setText(R.string.set_focus);
			}
//			findViewById(R.id.footprintLL).setVisibility(View.GONE);//需要实时查询来显示
//			findViewById(R.id.dont_let_ta_view_my_footprintLL).setVisibility(View.GONE);//需要实时查询来显示
//			AppUtil.execAsyncTask(new QryFootPrintAsyncTask(this));
//			findViewById(R.id.msg_no_notiL).setVisibility(View.VISIBLE);
			findViewById(R.id.find_msg_recordLL).setVisibility(View.VISIBLE);
			findViewById(R.id.exitLL).setVisibility(View.GONE);
			((ImageView)findViewById(R.id.msg_not_noti)).setImageResource("1".equals(mP2PInfo.getPush()) ? R.drawable.btn_person_mute: R.drawable.btn_person_unmute);
			setBlackView();
		}
		AppUtil.execAsyncTask(new QryVideoCountAsyncTask(this));
	}
	
	/**
	 * 获取基本信息
	 */
	public void getBaseInfoFromServer() {
		PersonController.synPersonInfo(mobile, true);
	}
	
	private void setBlackView(){
		findViewById(R.id.add_blacklistLL).setVisibility(View.VISIBLE);
		if(mPersonController.isBlack(mobile)){
			((TextView)findViewById(R.id.add_blacklist)).setText(R.string.cancel_blacklist);
		}else{
			((TextView)findViewById(R.id.add_blacklist)).setText(R.string.add_blacklist);
		}
	}
	
	private void setPersonInfo(){
		nicknamev.setText(mPersonController.findNameByMobile(person.account));
		fansV.setText(person == null ? "" : person.fansNum + "");
		focusV.setText(person == null ? "" : person.focusNum + "");
		locationV.setText(person == null ? "" : person.location);
		sexV.setText(person == null ? "" : person.sex == 1 ? getString(R.string.female_sign) : getString(R.string.male_sign));
		if(person == null || TextUtils.isEmpty(person.birthday)){
			oldV.setText("");
		}else{
			int sui = DateUtil.getAgeByBirthday(person.birthday);
			if(sui == 0){
				oldV.setText("");
			}else{
				oldV.setText(DateUtil.getAgeByBirthday(person.birthday) + "岁");
			}
		}
		headIv.setPerson(mobile, person.image);
		signV.setText(person == null ? "" : person.sign);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent;
		switch (v.getId()) {
		case R.id.mng_selfinfo_IV_headpic:
			intent = new Intent(this, HeadBigActivity.class);
			intent.putExtra("data", person);
			startActivity(intent);
			this.overridePendingTransition(R.anim.slide_in_left_top, R.anim.slide_out_left);
			break;
		case R.id.common_title_TV_left:
			finish();
			break;
		case R.id.edit:
			if(AccountData.getInstance().getBindphonenumber().equals(mobile)){
				intent = new Intent(this, ConsummationInfoActivity.class);
				startActivityForResult(intent, Constants.REQUEST_CODE_PERSON_SET_INFO);
//				startActivity(intent);
			}
			break;
		case R.id.fans:
			if(AccountData.getInstance().getBindphonenumber().equals(mobile)){
				intent = new Intent(this, FansListActivity.class);
				intent.putExtra("mobile", mobile);
				startActivity(intent);
			}
			break;
		case R.id.focus:
			if(AccountData.getInstance().getBindphonenumber().equals(mobile)){
				intent = new Intent(this, FocusListActivity.class);
				intent.putExtra("mobile", mobile);
				startActivity(intent);
			}
			break;
		case R.id.edit_label://签名点击事件
			if(AccountData.getInstance().getBindphonenumber().equals(mobile)){
				intent = new Intent(this, PersonSetSignActivity.class);
				intent.putExtra("person", person);
				this.startActivityForResult(intent, Constants.REQUEST_CODE_PERSON_SET_SIGN);
			}
			break;
		case R.id.footprintLL:
			Fc_PicConstants.fc_selected_Pic_List.clear();
			Fc_PicConstants.selectlist.clear();
			intent = new Intent(MyApplication.getInstance(), FriendCircleActivity.class);
			intent.putExtra("videoCount", videoCount);
			intent.putExtra("mobile", mobile);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.find_msg_recordLL://查找消息点击事件
			intent = new Intent(this, IMMessageHistorySearchActivity.class);
			intent.putExtra("mobile", mobile);
			startActivity(intent);
			break;
		case R.id.exitLL:
			choiceExitDialog.setItems(quitStr, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (0 == which) {
						if(MyApplication.getInstance().mPreferencesMan.isFirstReg()){
							 Intent intent = new Intent(PersonActivity.this, SetPasswordActivity.class);
							startActivity(intent);
						}else{
							if (!aChangeAccDialog.isShowing()) {
								aChangeAccDialog.show();
							}
						}
					} else {
						if(MyApplication.getInstance().mPreferencesMan.isFirstReg()){
							 Intent intent = new Intent(PersonActivity.this, SetPasswordActivity.class);
							startActivity(intent);
						}else{
							if (!aExitAppDialog.isShowing()) {
								aExitAppDialog.show();
							}
						}
					}
				}
			});
			choiceExitDialog.show();
			break;
		case R.id.btn_under_head:
			if(AccountData.getInstance().getBindphonenumber().equals(mobile)){
				CameraGalleryWithClearChoiceDialog dialog = new CameraGalleryWithClearChoiceDialog(PersonActivity.this);
				dialog.addOnChoiceClickListener(new OnChoiceClickListener() {

					@Override
					public void onClicked(int which) {
						if (2 == which) {
							if (b != null && !b.isRecycled())
								b.recycle();
							headIv.setImageResource(R.drawable.avatar_img_loading);
							((AccountController) mController).uploadPhoto(b);
						}
					}

				});
				dialog.showAgain();
			}else{
				AppUtil.execAsyncTask(new SetFocusAsyncTask(this));
			}
			break;
		case R.id.chat:
			intent = new Intent(this, IMMessageListActivity.class);
			intent.putExtra("data", mobile);
			startActivity(intent);
			break;
		case R.id.recommand_to_friend:
			intent = new Intent(this, ContactMsgCenterActivity.class);
			intent.putExtra(ContactMsgCenterActivity.LAUNCH_MODE, ContactMsgCenterActivity.LAUNCH_MODE_SEND_RECOMMAND);
			intent.putExtra("mobile", mobile);
			startActivity(intent);
			break;
//		case R.id.dont_let_ta_view_my_footprint:
//			AppUtil.execAsyncTask(new SetFootPrintAsyncTask(this));
//			break;
		case R.id.msg_not_noti://免打扰点击事件
			if("1".equals(mP2PInfo.getPush())){
				boolean flag = ImData.getInstance().setP2PAttributes(mobile, "push", "0");
				if(flag){
					((ImageView)findViewById(R.id.msg_not_noti)).setImageResource(R.drawable.btn_person_unmute);
					mP2PInfo.setPush("0");
				}else{
					
				}
				
			}else{
				boolean flag = ImData.getInstance().setP2PAttributes(mobile, "push", "1");
				if(flag){
					((ImageView)findViewById(R.id.msg_not_noti)).setImageResource(R.drawable.btn_person_mute);
					mP2PInfo.setPush("1");
				}else{
					
				}
			}
			break;
		case R.id.add_blacklistLL:
			AppUtil.execAsyncTask(new SetBlackAsyncTask(this));
			break;
		case R.id.findPwdLL:
			intent = new Intent(this, RegisterAllActivity.class);
			intent.putExtra("isRegister", false);
			startActivity(intent);
//		case R.id.memo_labelLL://备注点击事件
//			intent = new Intent(this, PersonSetMemoActivity.class);
//			intent.putExtra("person", person);
//			this.startActivityForResult(intent, Constants.REQUEST_CODE_PERSON_SET_MEMO);
//			break;
//		case R.id.set_special_focusLL://FIXME 设为特别关注
//			break;
		}
	}

	@Override
	public void syn(PersonData person) {
		//更新个人信息
		if(person != null && mobile.equals(person.account)){
			this.person = person;
			mUIHandler.sendEmptyMessage(SET_PERSON_INFO);
		}
	}
	
	class QryFootPrintAsyncTask extends BaseNetAsyncTask{

		public QryFootPrintAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			return super.ni.m1_isForbid_view(mobile);
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				if(1 == (Integer)result.getObj()){
//					findViewById(R.id.dont_let_ta_view_my_footprintLL).setVisibility(View.VISIBLE);
//					((TextView)findViewById(R.id.dont_let_ta_view_my_footprint)).setText(R.string.let_ta_view_my_footprint);
				}else{
//					findViewById(R.id.dont_let_ta_view_my_footprintLL).setVisibility(View.VISIBLE);
					findViewById(R.id.footprintLL).setVisibility(View.VISIBLE);
//					((TextView)findViewById(R.id.dont_let_ta_view_my_footprint)).setText(R.string.dont_let_ta_view_my_footprint);
				}
			}else{
				toastToMessage(getString(R.string.qry_view_my_footprint) + getString(R.string.fail));
			}
		}
		
	}
	
//	class SetFootPrintAsyncTask extends BaseNetAsyncTask{
//
//		public SetFootPrintAsyncTask(Context context) {
//			super(context);
//		}

//		@Override
//		public NetInterfaceStatusDataStruct doNet() {
//			if(((TextView)findViewById(R.id.dont_let_ta_view_my_footprint)).getText().equals(getString(R.string.let_ta_view_my_footprint))){
//				return super.ni.m1_agree_view(mobile);
//			}else{
//				return super.ni.m1_forbid_view(mobile);
//			}
//		}

//		@Override
//		public void afterNet(NetInterfaceStatusDataStruct result) {
//			if(((TextView)findViewById(R.id.dont_let_ta_view_my_footprint)).getText().equals(getString(R.string.let_ta_view_my_footprint))){
//				if(Constants.RES_SUCCESS.equals(result.getStatus())){
//					toastToMessage(getString(R.string.let_ta_view_my_footprint) + getString(R.string.success));
//					((TextView)findViewById(R.id.dont_let_ta_view_my_footprint)).setText(R.string.dont_let_ta_view_my_footprint);
//				}else{
//					toastToMessage(getString(R.string.let_ta_view_my_footprint) + getString(R.string.fail));
//				}
//			}else{
//				if(Constants.RES_SUCCESS.equals(result.getStatus())){
//					toastToMessage(getString(R.string.dont_let_ta_view_my_footprint) + getString(R.string.success));
//					((TextView)findViewById(R.id.dont_let_ta_view_my_footprint)).setText(R.string.let_ta_view_my_footprint);
//				}else{
//					toastToMessage(getString(R.string.dont_let_ta_view_my_footprint) + getString(R.string.fail));
//				}
//			}
//		}
//		
//	}
	
	class SetFocusAsyncTask extends BaseNetAsyncTask{

		public SetFocusAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			if(((TextView)findViewById(R.id.btn_under_head)).getText().equals(getString(R.string.set_focus))){
				return super.ni.m1_add_focus(mobile, 0);
			}else{
				return super.ni.m1_cancel_focus(mobile, 0);
			}
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(((TextView)findViewById(R.id.btn_under_head)).getText().equals(getString(R.string.set_focus))){
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					FansData fans = new FansData();
					fans.account = person.account;
					fans.nick = person.nickname;
					fans.imageurl = person.image;
					mPersonController.addFocus(fans);
					toastToMessage(getString(R.string.set_focus) + getString(R.string.success));
					underHeadV.setText(R.string.cancel_focus);
					underHeadV.setCompoundDrawables(null, null, null, null);
				}else{
					toastToMessage(getString(R.string.set_focus) + getString(R.string.fail));
				}
			}else{
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					mPersonController.cancelFocus(mobile);
					toastToMessage(getString(R.string.cancel_focus) + getString(R.string.success));
					underHeadV.setText(R.string.set_focus);
					underHeadV.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_person_focus), null, null, null);
				}else{
					toastToMessage(getString(R.string.cancel_focus) + getString(R.string.fail));
				}
			}
		}
	}
	
	class SetBlackAsyncTask extends BaseNetAsyncTask{

		public SetBlackAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			if(mPersonController.isBlack(mobile)){
				return super.ni.m1_cancel_blacklist(mobile);
			}else{
				return super.ni.m1_add_blacklist(mobile);
			}
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(mPersonController.isBlack(mobile)){
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					mPersonController.cancelBlack(mobile);
					toastToMessage(getString(R.string.cancel_blacklist) + getString(R.string.success));
					setBlackView();
				}else{
					toastToMessage(getString(R.string.cancel_blacklist) + getString(R.string.fail));
				}
			}else{
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					mPersonController.addBlack(mobile);
					toastToMessage(getString(R.string.add_blacklist) + getString(R.string.success));
					setBlackView();
				}else{
					toastToMessage(getString(R.string.add_blacklist) + getString(R.string.fail));
				}
			}
		}
	}
	
	class QryVideoCountAsyncTask extends BaseNetAsyncTask{

		public QryVideoCountAsyncTask(Context context) {
			super(context);
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			return super.ni.m1_count_video(mobile);
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(Constants.RES_SUCCESS.equals(result.getStatus())){
				JSONObject obj = (JSONObject)result.getObj();
				if(obj != null && obj.has("videocount") && !obj.isNull("videocount")){
					try {
						videoV.setText(obj.getString("videocount"));
						videoCount = obj.getString("videocount");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				toastToMessage(getString(R.string.qry_video_count) + getString(R.string.fail));
			}
		}
		
	}
	
	private static final int SET_PERSON_INFO = 1;
	private static final int CHANGE_ACCOUNT = 2;
	private static final int EXIT_APP = 3;
	UIHandler mUIHandler = new UIHandler(this);

	private static class UIHandler extends Handler {
		WeakReference<PersonActivity> mActivity;

		UIHandler(PersonActivity activity) {
			mActivity = new WeakReference<PersonActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final PersonActivity theActivity = mActivity.get();
			switch (msg.what) {
			case SET_PERSON_INFO:// 1
				theActivity.setPersonInfo();
				break;
			case CHANGE_ACCOUNT:
				try {
					theActivity.showProgressDialog(R.string.exiting, false);
					new Thread(new Runnable() {
						public void run() {
							try {
								AppUtil.stopAllServices();
								MyApplication.getInstance().mPreferencesMan.setPutPCLasttime("0");
								AppUtil.exitIM();
								AppUtil.cancelNotis();
								AccountData.getInstance().clearCurrAcc();
								AccountData.getInstance().clearLastAcc();
								ImCore.clearImCore();
								
								// 如果切换用户，那么微博账户重新授权。
								theActivity.removeWeiboAuthors();
							} catch (Exception e) {
								Log.e(Constants.LOG_TAG, e.getMessage(), e);
							} finally {
								theActivity.hideProgressDialog();
								Intent intent = AppUtil.getLoginActIntent(theActivity);
								theActivity.startActivity(intent);
							}
						}
					}).start();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
				break;
			case EXIT_APP:
				try {
					theActivity.showProgressDialog(R.string.exiting, false);
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
								theActivity.hideProgressDialog();
								Intent intent = new Intent();
								intent.setAction(BaseActivity.MAIN_ACTIVITY_EXIT_ACTION);
								theActivity.sendBroadcast(intent);
							}
						}
					}).start();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	/*
	 * 获取修剪后的图片资源
	 * 
	 * @param data
	 */
	public void readPhotoInfo(Intent data) {
		try {
			if (tempb != null && !tempb.isRecycled())
				tempb.recycle();
			Uri uri = data.getData();
			if (uri == null) {
				Bundle bundle = data.getExtras();
				tempb = (Bitmap) bundle.get("data");
			} else {
				ContentResolver cr = getContentResolver();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				tempb = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
			}
			if (b != null && !b.isRecycled()) {
				b.recycle();
			}
			b = compressImage(tempb);
			headIv.setImageBitmap(b);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			toastToMessage(R.string.read_photo_fail);
		}
	}

	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 20) { // 循环判断如果压缩后图片是否大于20kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	@Override
	public void request_join_live(String account, String nick, String videoID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void response_join_live(String account, String nick, String videoID,
			String videoTitle, String accept) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void private_bullet(String account, String msg, String videoID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kick_off_video(String account, String nick, String videoID,
			String videoTitle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mute_video(String account, String nick, String videoID,
			String videoTitle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forbid_bullet(String videoID,String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void friend_status(String account, String type, String videoID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invite_video(String account, String nick, String videoID,
			String videoTitle, String playurl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entrust_invite_video(String videoID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void comment_notify(String commenVideoID, String commentid,
			String account, String nick, String imageurl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focus_notify(int optType, int isSpecial, String account,
			String nick, String imageurl) {
		if(AccountData.getInstance().getBindphonenumber().equals(mobile)){
			person = mPersonController.findPerson(mobile);
			fansV.setText(person == null ? "" : person.fansNum + "");
		}
	}
}