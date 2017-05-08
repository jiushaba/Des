package com.lb.zbrj.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.lb.common.util.DateUtil;
import com.lb.common.util.ImageUtil;
import com.lb.common.util.Log;
import com.lb.common.util.corpimage.CropImage;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.listener.SynPersonInfoListener;
import com.lb.zbrj.net.NetIFUI.NetInterfaceListener;
import com.lb.zbrj.net.NetIFUI_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.AboutActivity;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.activity.BlackListActivity;
import com.lz.oncon.activity.HeadBigActivity;
import com.lz.oncon.activity.SettingAreaActivity;
import com.lz.oncon.activity.ShowTagActivity;
import com.lz.oncon.app.im.util.SystemCamera;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.controller.AccountController;
import com.lz.oncon.controller.BaseController;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog;
import com.lz.oncon.widget.HeadImageView;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog.OnChoiceClickListener;

public class ConsummationInfoActivity extends BaseActivity implements SynPersonInfoListener {

	private Bitmap b;
	private Bitmap tempb;
	private HeadImageView headIv;
	private BaseController mController;
//	private Builder choiceSexDialog;
	private DatePickerDialog birthdayDialog;
	private RadioGroup sexRG;
	private RadioButton maleRB, femaleRB;
	private TextView area_value, birthday_value, personal_label_value, score_value, birthday_star;
//	private TextView sex_value;
	private EditText name_ET;
//	private String[] sexStr;
	public static final String SEX_MALE = "1";
	public static final String SEX_FEMALE = "2";
	public String default_text_must_input;
	public String default_text_must_check;
	public String default_text_input;
	private PersonController mPersonController;
	private PersonData person;
    private Calendar birthdayCal;
    private int newSex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consummation_info);
		initView();
		initController();
		setValue();
		MyApplication.getInstance().addListener(Constants.LISTENER_SYN_PERSONINFO, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getLabel();
	}

	private void initController() {
		mController = new AccountController(this);
		mPersonController = new PersonController();
	}

	private void setValue() {
		person = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
		default_text_input = ConsummationInfoActivity.this.getResources().getString(R.string.null_info);
		default_text_must_check = ConsummationInfoActivity.this.getResources().getString(R.string.must_choise);
		default_text_must_input = ConsummationInfoActivity.this.getResources().getString(R.string.must_input);
		headIv.setPerson(person.account, person.image);
		getLabel();
		if (!TextUtils.isEmpty(person.nickname)) {
			name_ET.setText(person.nickname);
		}
		newSex = person.sex;
		if(person.sex == 0){
			maleRB.setChecked(true);
		}else{
			femaleRB.setChecked(true);
		}
//		sex_value.setText(sexStr[person.sex]);
		if (!TextUtils.isEmpty(person.location)) {
			area_value.setText(person.location);
		}
		if (!TextUtils.isEmpty(person.birthday)) {
			birthday_value.setText(person.birthday);
			Date date = DateUtil.getDate(person.birthday);
			if(date != null){
				birthdayCal.setTime(date);
				birthday_star.setText(DateUtil.getAstro(birthdayCal.get(Calendar.MONTH) + 1, birthdayCal.get(Calendar.DATE)));
			}
		}
		score_value.setText(person.score + "");
	}

	private void initView() {
		headIv = (HeadImageView) findViewById(R.id.mng_selfinfo_IV_headpic);
		name_ET = (EditText) findViewById(R.id.name_ET);
//		sex_value = (TextView) findViewById(R.id.sex_value);
		area_value = (TextView) findViewById(R.id.area_value);
		birthday_value = (TextView) findViewById(R.id.birthday_value);
		birthday_star = (TextView) findViewById(R.id.birthday_star);
		score_value = (TextView) findViewById(R.id.score_value);
		personal_label_value = (TextView) findViewById(R.id.personal_label_value);
//		sexStr = getResources().getStringArray(R.array.Sex);
//		choiceSexDialog = new Builder(this);
//		choiceSexDialog.setItems(sexStr, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (0 == which) {
//					sex_value.setText(sexStr[0]);
//					person.sex = 0;
//				} else {
//					sex_value.setText(sexStr[1]);
//					person.sex = 1;
//				}
//			}
//		});
		sexRG = (RadioGroup) findViewById(R.id.sex_value);
		maleRB = (RadioButton) findViewById(R.id.sexMale);
		femaleRB = (RadioButton) findViewById(R.id.sexFemale);
		sexRG.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group,int checkedId) {
				if(maleRB.getId()==checkedId){
					newSex = 0;
				}else{
					newSex = 1;
				}
			}
		});
		birthdayCal = Calendar.getInstance();
		birthdayDialog = new DatePickerDialog(ConsummationInfoActivity.this, Datelistener
        		, birthdayCal.get(Calendar.YEAR), birthdayCal.get(Calendar.MONTH), birthdayCal.get(Calendar.DATE));
	}

	private void getLabel() {
		// 取个人标签
		if (!TextUtils.isEmpty(person.label)) {
			String[] tags = person.label.split(Constants.LABEL_SPLIT);
			if (tags != null && tags.length > 1) {
				personal_label_value.setText(getResources().getString(R.string.tag_synopsis, tags[0], tags.length));
			} else if (tags != null && tags.length == 1) {
				personal_label_value.setText(tags[0]);
			} else {
				personal_label_value.setText(getResources().getString(R.string.null_info));
			}
		} else {
			personal_label_value.setText(getResources().getString(R.string.null_info));
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			switch (requestCode) {
			case Constants.CAMERA_RESULT_CODE:
				if (data != null) {
					if (tempb != null && !tempb.isRecycled())
						tempb.recycle();
					String filePath = SystemCamera.getCaptureFilePath();
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
				readPhotoInfo(data);
				((AccountController) mController).uploadPhoto(b);
				break;
			case Constants.REQ_AREA:
				if(resultCode == Activity.RESULT_OK){
					area_value.setText(data.getStringExtra("areaInfo"));
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

	private UIHandler mUIHandler = new UIHandler(ConsummationInfoActivity.this);

	private static final int GET_BASE_INFO = 0;
	private static final int SET_BASE_INFO = 2;

	static class UIHandler extends Handler {
		WeakReference<ConsummationInfoActivity> mActivity;

		UIHandler(ConsummationInfoActivity activity) {
			mActivity = new WeakReference<ConsummationInfoActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final ConsummationInfoActivity theActivity = mActivity.get();
			NetInterfaceStatusDataStruct niStatusData = (NetInterfaceStatusDataStruct) msg.obj;
			switch (msg.what) {
			case GET_BASE_INFO:
				theActivity.setValue();
				break;
			case SET_BASE_INFO:
				if (niStatusData != null) {
					if ("0".equals(niStatusData.getStatus())) {
						theActivity.toastToMessage(theActivity.getString(R.string.success));
					}else {
						theActivity.toastToMessage(theActivity.getString(R.string.fail));
					}
				}else {
					theActivity.toastToMessage(theActivity.getString(R.string.fail));
				}
				theActivity.finish();
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent;
		switch (v.getId()) {
		case R.id.info_head:
			CameraGalleryWithClearChoiceDialog dialog = new CameraGalleryWithClearChoiceDialog(ConsummationInfoActivity.this);
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
			break;
		case R.id.mng_selfinfo_IV_headpic:
			intent = new Intent(ConsummationInfoActivity.this, HeadBigActivity.class);
			intent.putExtra("data", person);
			startActivity(intent);
			this.overridePendingTransition(R.anim.slide_in_left_top, R.anim.slide_out_left);
			break;
		case R.id.info_sex:
//			choiceSexDialog.show();
			break;
		case R.id.info_birthday:
			birthdayDialog.updateDate(birthdayCal.get(Calendar.YEAR), birthdayCal.get(Calendar.MONTH), birthdayCal.get(Calendar.DATE));
			birthdayDialog.show();//显示DatePickerDialog组件
			break;
		case R.id.info_personal_label:
			intent = new Intent(ConsummationInfoActivity.this, ShowTagActivity.class);
			startActivity(intent);
			break;
		case R.id.info_area:
			intent = new Intent(ConsummationInfoActivity.this, SettingAreaActivity.class);
			startActivityForResult(intent, Constants.REQ_AREA);
			break;
		case R.id.common_title_TV_left:
			save();
			break;
		case R.id.blacklistLL:
			intent = new Intent(this, BlackListActivity.class);
			startActivity(intent);
			break;
		case R.id.info_userinfo_privacy:
			intent = new Intent(this, PrivateConfigActivity.class);
			startActivity(intent);
			break;
		case R.id.aboutLL:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().removeListener(Constants.LISTENER_SYN_PERSONINFO, this);
		if (b != null && !b.isRecycled())
			b.recycle();
		if (tempb != null && !tempb.isRecycled())
			tempb.recycle();
	}
	
	/**
	 * 基本信息设置
	 */
	public void setBaseInfoToServer() {
		new NetIFUI_ZBRJ(this, new NetInterfaceListener() {
			@Override
			public void finish(NetInterfaceStatusDataStruct niStatusData) {
				mUIHandler.obtainMessage(SET_BASE_INFO, niStatusData).sendToTarget();
			}
		}).m1_update_personalInfo(person);
	}

	private DatePickerDialog.OnDateSetListener Datelistener=new DatePickerDialog.OnDateSetListener(){
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear,int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
        	birthdayCal.set(Calendar.YEAR, myyear);
        	birthdayCal.set(Calendar.MONTH, monthOfYear);
        	birthdayCal.set(Calendar.DATE, dayOfMonth);
        	//在TextView上显示日期
        	birthday_value.setText(DateUtil.getDateString(birthdayCal.getTime(), "yyyy-MM-dd"));
        	birthday_star.setText(DateUtil.getAstro(monthOfYear + 1, dayOfMonth));
        }
    };

	@Override
	public void syn(PersonData person) {
		if(person != null && AccountData.getInstance().getBindphonenumber().equals(person.account)){
			this.person = person;
			mUIHandler.sendEmptyMessage(GET_BASE_INFO);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			save();
			return true;
		}
		return false;
	}
	
	private void save(){
		String name = name_ET.getText().toString().trim();
		String area = area_value.getText().toString();
		String birthday = birthday_value.getText().toString();
		if (TextUtils.isEmpty(name) || default_text_must_input.equals(name)) {
			toastToMessage(R.string.must_input_info_not_null);
			return;
		}
//		if (TextUtils.isEmpty(area) || default_text_must_input.equals(area)) {
//			toastToMessage(R.string.must_input_info_not_null);
//			return;
//		}
//		if (TextUtils.isEmpty(birthday) || default_text_must_input.equals(birthday)) {
//			toastToMessage(R.string.must_input_info_not_null);
//			return;
//		}
		if(!name.equals(person.nickname) || newSex != person.sex || !area.equals(person.location) || !birthday.equals(person.birthday)){
			person.nickname = name_ET.getText().toString().trim();
			person.sex = newSex;
			person.location = area_value.getText().toString();
			person.birthday = birthday_value.getText().toString();
			setBaseInfoToServer();
		}else{
			setResult(Constants.REQUEST_CODE_PERSON_SET_INFO);
			finish();
		}
	}
}