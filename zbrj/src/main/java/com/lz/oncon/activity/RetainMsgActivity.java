package com.lz.oncon.activity;

import java.io.File;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lb.common.util.Constants;
import com.lb.common.util.DateUtil;
import com.lb.common.util.FileCore;
import com.xuanbo.xuan.R;
import com.lz.oncon.app.im.data.IMNotification;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.ui.IMMessageListActivity;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.BottomPopupWindow;

public class RetainMsgActivity extends BaseActivity {

	private String checkTime, chioceItem;
	private BottomPopupWindow monthClearMenu, dayClearMenu, clearMsgRecordMenu;
	private ImageView three_month_img, forever_img, seven_day_img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentView();
		initView();
		setvalue();
	}

	public void initContentView() {
		setContentView(R.layout.activity_retain_message);
	}

	private void initView() {
		three_month_img = (ImageView) findViewById(R.id.three_month_img);
		forever_img = (ImageView) findViewById(R.id.forever_img);
		seven_day_img = (ImageView) findViewById(R.id.seven_day_img);

		monthClearMenu = new BottomPopupWindow(this);
		monthClearMenu.setTitle(R.string.delete_three_month);
		monthClearMenu.addButton(R.string.confirm, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				monthClearMenu.dismiss();
				showProgressDialog(R.string.wait, false);
				  new ClearMsgRecordAsyncTask().execute("1");
			}
		}, false);

		dayClearMenu = new BottomPopupWindow(this);
		dayClearMenu.setTitle(R.string.delete_seven_day);
		dayClearMenu.addButton(R.string.confirm, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dayClearMenu.dismiss();
				showProgressDialog(R.string.wait, false);
				new ClearMsgRecordAsyncTask().execute("3");
			}
		}, false);
		
		clearMsgRecordMenu = new BottomPopupWindow(this);
		clearMsgRecordMenu.setTitle(R.string.will_clear_msg_record);
		clearMsgRecordMenu.addButton(R.string.confirm, new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				clearMsgRecordMenu.dismiss();
				showProgressDialog(R.string.clear_msg_record_ing, false);
				new ClearMsgRecordAsyncTask().execute("");
			}
		}, false);
	}

	private void setvalue() {
		chioceItem = AccountData.getInstance().getChioceItem();
		three_month_img.setVisibility(View.GONE);
		forever_img.setVisibility(View.GONE);
		seven_day_img.setVisibility(View.GONE);

		if (!TextUtils.isEmpty(chioceItem)) {
			if ("1".equals(chioceItem)) {
				three_month_img.setVisibility(View.VISIBLE);
			} else if ("2".equals(chioceItem)) {
				forever_img.setVisibility(View.VISIBLE);
			} else if ("3".equals(chioceItem)) {
				seven_day_img.setVisibility(View.VISIBLE);
			}
		} else {
			three_month_img.setVisibility(View.VISIBLE);
		}

	}
	
	class ClearMsgRecordAsyncTask extends AsyncTask<String, Integer, String> {
		String type = "";
		@Override
		protected String doInBackground(String... parameter) {
			type = parameter[0];
			try{
				if ("1".equals(type)) {
					ImData.getInstance().deleteAllThreadsMessageByTime(DateUtil.get3MonthAgoMillis());
				}else if("3".equals(type)) {
					ImData.getInstance().deleteAllThreadsMessageByTime(DateUtil.get7DayAgoMillis());
				}else {
					ImData.getInstance().deleteThreadDataAll();
				}
				IMNotification.getInstance().clear();
				MyApplication.getInstance().mActivityManager.popActivity(IMMessageListActivity.class);
				
				String onconpath = "";
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
					onconpath = getFilesDir().getAbsolutePath() + File.separator + "oncon" + File.separator;
		        }else{  
		        	onconpath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator  
                            +   getPackageName() + File.separator + "oncon" + File.separator;
		        }
				if ("1".equals(type)) {
					File f = new File(Constants.THUMB_BIG);//多图发送大图
					if(f.exists())FileCore.RecursionDeleteFileByTime(f,Long.valueOf(DateUtil.get3MonthAgoMillis()));
					f = new File(Constants.THUMB_SMALL);//多图发送小图
					if(f.exists())FileCore.RecursionDeleteFileByTime(f,Long.valueOf(DateUtil.get3MonthAgoMillis()));
					f = new File(onconpath);//聊天相关语音和图片
					if(f.exists())FileCore.RecursionDeleteFileByTime(f,Long.valueOf(DateUtil.get3MonthAgoMillis()));
				}else if ("3".equals(type)) {
					File f = new File(Constants.THUMB_BIG);//多图发送大图
					if(f.exists())FileCore.RecursionDeleteFileByTime(f,Long.valueOf(DateUtil.get7DayAgoMillis()));
					f = new File(Constants.THUMB_SMALL);//多图发送小图
					if(f.exists())FileCore.RecursionDeleteFileByTime(f,Long.valueOf(DateUtil.get7DayAgoMillis()));
					f = new File(onconpath);//聊天相关语音和图片
					if(f.exists())FileCore.RecursionDeleteFileByTime(f,Long.valueOf(DateUtil.get7DayAgoMillis()));
				}else {
					File f = new File(Constants.THUMB_BIG);//多图发送大图
					if(f.exists())FileCore.RecursionDeleteFile(f);
					f = new File(Constants.THUMB_SMALL);//多图发送小图
					if(f.exists())FileCore.RecursionDeleteFile(f);
					f = new File(onconpath);//聊天相关语音和图片
					if(f.exists())FileCore.RecursionDeleteFile(f);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					hideProgressDialog();
					RetainMsgActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							try {
								RetainMsgActivity.this.toastToMessage(R.string.delete_success);
								if ("1".equals(type)) {
									three_month_img.setVisibility(View.VISIBLE);
									forever_img.setVisibility(View.GONE);
									seven_day_img.setVisibility(View.GONE);
									AccountData.getInstance().setChioceItem("1");
								}else if ("3".equals(type)) {
									three_month_img.setVisibility(View.GONE);
									forever_img.setVisibility(View.GONE);
									seven_day_img.setVisibility(View.VISIBLE);
									AccountData.getInstance().setChioceItem("3");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return "";
		}
		@Override
		protected void onProgressUpdate(Integer... progress) {
		}
		@Override
		protected void onPostExecute(String result) {
		}
	}
	
	

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		case R.id.retain_three_month:
			if(monthClearMenu != null && !monthClearMenu.isShowing())monthClearMenu.showAtLocation(findViewById(R.id.topLayout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.retain_forever:
			AccountData.getInstance().setChioceItem("2");
			three_month_img.setVisibility(View.GONE);
			forever_img.setVisibility(View.VISIBLE);
			seven_day_img.setVisibility(View.GONE);
			break;
		case R.id.retain_seven_day:
			if(dayClearMenu != null && !dayClearMenu.isShowing())dayClearMenu.showAtLocation(findViewById(R.id.topLayout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.clearMsgRecordRL:
			if(clearMsgRecordMenu != null && !clearMsgRecordMenu.isShowing())clearMsgRecordMenu.showAtLocation(findViewById(R.id.topLayout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		default:
			break;
		}
	}

}
