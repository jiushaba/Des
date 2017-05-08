package com.lz.oncon.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.lb.common.util.ImageUtil;
import com.lb.common.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lb.common.util.Constants;
import com.lb.common.util.corpimage.CropImage;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.app.im.util.SystemCamera;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog;
import com.lz.oncon.widget.TitleView;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog.OnChoiceClickListener;

public class RegisterActivity3 extends BaseActivity {

	private BaseController mController;
	private PersonController mPersonController;
	private TitleView title;
	private EditText nicknameET;
	private ImageView headIV;
	private AlertDialog nickNameFailDialog, cancelDialog;
	private CameraGalleryWithClearChoiceDialog headPicDialog;
	private String mobile;
	private Bitmap b, tempb;
	private PersonData person;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initController();
		initContentView();
		initViews();
		setValues();
		setListeners();
	}
	
	public void initContentView() {
		this.setContentView(R.layout.register3);
	}

	public void initController() {
		mController = new AccountController(this);
		mPersonController = new PersonController();
	}

	public void initViews() {
		title = (TitleView) findViewById(R.id.title);
		title.getRightView().setTextColor(Color.GRAY);
		nicknameET = (EditText)this.findViewById(R.id.nickname_ET);
		headIV = (ImageView) this.findViewById(R.id.head_IV);
		
		nickNameFailDialog = new AlertDialog.Builder(this)
		.setMessage(R.string.modify_nickname_fail_memo)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		}).create();
		
		headPicDialog = new CameraGalleryWithClearChoiceDialog(this);
		headPicDialog.addOnChoiceClickListener(new OnChoiceClickListener() {
			@Override
			public void onClicked(int which) {
				if (2 == which) {
					if (b != null && !b.isRecycled())
						b.recycle();
					headIV.setImageResource(R.drawable.avatar_img_loading);
					((AccountController) mController).uploadPhoto(b);
				}
			}
		});
		
		cancelDialog = new AlertDialog.Builder(this)
		.setMessage(R.string.last_reg_cancel_memo)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				RegisterActivity3.this.finish();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		}).create();
	}

	public void setListeners() {
		nicknameET.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				String nickname = nicknameET.getText().toString();
				if(TextUtils.isEmpty(nickname)){
					title.getRightView().setTextColor(Color.GRAY);
					title.getRightView().setClickable(false);
					title.getRightView().setEnabled(false);
				}else{
					title.getRightView().setTextColor(Color.BLACK);
					title.getRightView().setClickable(true);
					title.getRightView().setEnabled(true);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}

	public void setValues() {
		mobile = AccountData.getInstance().getBindphonenumber();
		person = mPersonController.findPerson(mobile);
		person.account = mobile;
	}
	
	@Override
	public void onClick(View v){
		super.onClick(v);
		switch(v.getId()){
			case R.id.common_title_TV_left:
				cancel();
				break;
			case R.id.common_title_TV_right:
				String nickname = nicknameET.getText().toString().trim();
				if(TextUtils.isEmpty(nickname)){
				}else{
					updNickName();
				}
				break;
			case R.id.nickname_ET:
				break;
			case R.id.head_IV:
				headPicDialog.showAgain();
				break;
			default:
				break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (data != null) {
				switch (requestCode) {
				case Constants.CAMERA_RESULT_CODE:
					if (tempb != null && !tempb.isRecycled())
						tempb.recycle();
					String filePath = SystemCamera.getCaptureFilePath();
					tempb = ImageUtil.loadBitmap(filePath, true);
					SystemCamera.getCropHeadImageIntent(this, tempb);
					SystemCamera.captureFilePath = null;
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
					readPhotoInfo(data);
					((AccountController) mController).uploadPhoto(b);
					break;
				default:
					break;
				}
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
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} catch (Error e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

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
			headIV.setImageBitmap(b);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			toastToMessage(R.string.read_photo_fail);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (b != null && !b.isRecycled())
			b.recycle();
		if (tempb != null && !tempb.isRecycled())
			tempb.recycle();
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
	
	private void updNickName(){
		String nickname = nicknameET.getText().toString().trim();
		person.nickname = nickname;
		new NetIFUI_ZBRJ(RegisterActivity3.this, new NetInterfaceListener(){
			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				if(Constants.RES_SUCCESS.equals(niStatusData.getStatus())){
					mPersonController.insert(person);
					go2MainActivity();
				}else{
					mUIHandler.sendEmptyMessage(UPD_NICKNAME_FAIL);
				}
			}
		}).m1_update_personalInfo(person);
	}
	
	private static final int UPD_NICKNAME_FAIL = 1;
	
	private UIHandler mUIHandler = new UIHandler(this);
	
	private static class UIHandler extends Handler {
		WeakReference<RegisterActivity3> mActivity;
		UIHandler(RegisterActivity3 activity) {
			mActivity = new WeakReference<RegisterActivity3>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			RegisterActivity3 theActivity = mActivity.get();
			switch(msg.what){
			case UPD_NICKNAME_FAIL://1
				try{
					if(!theActivity.nickNameFailDialog.isShowing()){
						theActivity.nickNameFailDialog.show();
					}
				}catch(Exception e){
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
				break;
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void cancel(){
		if(!cancelDialog.isShowing()){
			cancelDialog.show();
		}
	}
}