
package com.lb.video.activity;
/*****************************************************************************
 * VideoPlayerActivity.java
 *****************************************************************************
 * Copyright 漏 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.videolan.libvlc.EventHandler;
import org.videolan.vlc.util.WeakHandler;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.danmu.data.DanmuContentData;
import com.danmu.widget.DanmuSurfaceView;
import com.lb.common.util.Constants;
import com.lb.common.util.DisUtil;
import com.lb.common.util.ImageLoader;
import com.lb.common.util.Log;
import com.lb.common.util.ShareMenuUtil;
import com.lb.common.util.StringUtils;
import com.lb.video.adapter.DanmuListAdapter;
import com.lb.video.data.CommResData;
import com.lb.video.data.DanmuSendData;
import com.lb.video.data.PopupData;
import com.lb.video.im.ProtocalMessageWhat;
import com.lb.video.im.VideoPlayerProtocolListener;
import com.lb.video.job.DanmuFileLoadRunnable;
import com.lb.video.job.DanmuQueryRunnable;
import com.lb.video.job.SendIMLoopRunnable;
import com.lb.video.job.VideoNumberQueryRunnable;
import com.lb.video.view.DanmuOptionLayout;
import com.lb.video.widget.DanmuListView;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.PersonData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.data.db.CollectVideoHelper;
import com.lb.zbrj.data.db.WatchHistoryHelper;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.api.CustomProtocolDealerManager;
import com.lz.oncon.api.core.im.core.OnconIMCore;
import com.lz.oncon.app.im.contact.ContactMsgCenterActivity;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.umeng.socialize.common.SocializeConstants;
import com.xuanbo.xuan.R;

public class VideoPlayerActivity extends AVideoPlayerActivity {
	private static final int TIMEOUTSECOND = 30;
	private static final int MYEVENT_SHOWTASH = 1;
	public static final int MYEVENT_DANMULOAD_SUCCESS=2;
	public static final int MYEVENT_DANMULOAD_FAIL=3;
	private static final int EventVidoNumberChange = 4;
	private static final int EventHideSumMenu = 5;
	private static final int MYEVENT_SHOWTOAST = 6;
	//是否能够发送弹幕信息
	//
	private boolean forbidDanmuFlag = false;
	private boolean timeoutCheck;
	private EditText danmuInput;
	private TextView danmuswitchButton;
	private TextView danmusendButton; 
	private static DanmuQueryRunnable danmuQueryRunnable;
	private static Thread danmuQueryThread;
	public boolean danmuthreadStarted = false;
	private boolean canPlay = false;
	private Thread checkTimeOutThread;
	//end add
	private View video_danmu_list_layout;
	private DanmuListView danmu_list_view;
    private DanmuListAdapter danmuListAdapter;
    private View pop_confirm_window_hide;
    /*private List<PopupData> popupDatas =  new ArrayList<PopupData>();
    private Thread popupThread = null;*/
    //private PopupWindow popwinWindow;
    
	private TextView titleTextView;
	private ImageView personHead;
	private TextView personNick;
	private View titleControlLayout;
	private View blowControlLayout;
	private TextView btnLike;
	private TextView btnfocus;
	private TextView btnCollection;
	private View title_report_layout;
	private View video_sound_layout ;
	private View video_danmu_layout ;
	private View video_danmu_send_layout ;
	private VideoPlayerProtocolListener imlistener;
	private TextView video_stat_watchersNum,video_stat_bulletsNum,video_stat_upNum,video_stat_distance;
	private VideoNumberQueryRunnable videoNumberQueryRunnable;
	private VideoData videoData;
	private static DanmuSurfaceView danmuSurfaceView;
	private TextView actionTextView;
	private View attentionMoreLayout;
	protected NetIF_ZBRJ netIF_ZBRJ;
	private DanmuOptionLayout danmuOptionLayout;
	private int distanceInt;
	private String nick = null;
	private int i = 0;
	PersonController personController = null;
	private CollectVideoHelper collectVideoHelper;
	private WatchHistoryHelper watchHistoryHelper;
	/*直播直接延时3秒显示*/
	private int delayTime = 5000;
    private boolean delay = false;
    private int oldVolume = 0;
    /*延时是否处理完毕*/
    private boolean delayDone = false;
    private View player_blocked;
    private PopupWindow popwinWindow;
    private List<View> subMenuList = new ArrayList<View>();
    private List<View> btnsubMenuList = new ArrayList<View>();
    private ShareMenuUtil shareMenuUtil;
    private boolean resumePlay = false;
    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
    	 videoData = (VideoData) getIntent().getExtras().getSerializable("videoData");
         mLocation = getIntent().getExtras().getString("itemLocation");
         super.onCreate(savedInstanceState);
    }
    @Override
    protected void onResume() {
    	super.onResume();
    }
    @Override
    protected void init(){
    	 super.init();
    	 initNetIfs();
    	 initDBHelper();
    	 initdistance();
    	 initDanmuList();
         initCustomLayout();
         initDanmusend();
         initPlayVisible();
         initLiveDelay();
         initnick();
         initShare();
         
    }

    private boolean danmustartFlag = false;
	private void startDanmuSurface() {
		if(danmustartFlag)
			return;
		danmustartFlag = true;
		danmuSurfaceView.setStartTime(videoData.dateTime);
		danmuSurfaceView.start();
	}
	private void initShare() {
		SocializeConstants.DEBUG_MODE = true;
		SocializeConstants.SHOW_ERROR_CODE = true;
		shareMenuUtil = ShareMenuUtil.getInstance(this);
		String nick = getNickName(AccountData.getInstance().getBindphonenumber());
		String url = netIF_ZBRJ.address_share+"?name="+URLEncoder.encode(nick)+"&videoid="+videoData.videoID;
		shareMenuUtil.initBottomPopupWindow(mOverlayProgress, getResources().getString(R.string.share),videoData.title, nick, "briefStr", nick, url,videoData.videoImage, true, ContactMsgCenterActivity.LAUNCH_MODE_IMAGETEXTMSG);
	}
	private void initnick() {
		 TextView nick = ((TextView)findViewById(R.id.video_person_nick));
		 if(nick.getText().equals(videoData.account)){
			 nick.setText(videoData.nick);
		 }
	}
	private void initLiveDelay() {
		player_blocked = findViewById(R.id.player_blocked);
		if(videoData.isLive == 1){
			delayDone = false;
			setAudioVolume(0,false);
			
		}else{
			player_blocked.setVisibility(View.GONE);
			delayDone = true;
			delayTime = 0;
		}
	}
	private void initDBHelper() {
		collectVideoHelper = new CollectVideoHelper(AccountData.getInstance().getUsername());
		watchHistoryHelper =  new WatchHistoryHelper(AccountData.getInstance().getBindphonenumber());
		personController = new PersonController();;
	}
	private void initPlayVisible() {
		if(videoData.isLive == 1){
			mPlayPause.setVisibility(View.INVISIBLE);
		}else{
			mPlayPause.setVisibility(View.VISIBLE);
		}
	}
	private void initdistance() {
		distanceInt = DisUtil.distanceInt(videoData.locationX + "", videoData.locationY + "");
	}
	private void initCustomLayout() {
		
		danmuSurfaceView = (DanmuSurfaceView) findViewById(R.id.player_danmaku);
		
    	titleControlLayout =findViewById(R.id.video_title_control_layout);
    	blowControlLayout = findViewById(R.id.video_player_control_layout);
    	//View statlayout = findViewById(R.id.video_stat_layout);
    	actionTextView = (TextView) findViewById(R.id.video_control_display_action);
    	personHead = (ImageView) findViewById(R.id.video_person_head);
		personNick = (TextView)findViewById(R.id.video_person_nick);
		setTilePersonData();
    	setActionText();
    	setReportVisible(titleControlLayout);
    	
    	//设置底部按钮功能
    	//点赞关注，举报事件设置
    	btnLike = (TextView)blowControlLayout.findViewById(R.id.player_btn_like);
    	btnLike.setOnClickListener(netCommonOnClickListener);
    	
    	btnfocus = (TextView) blowControlLayout.findViewById(R.id.player_btn_focus);
    	setfocus();
    	
    	btnCollection = (TextView) blowControlLayout.findViewById(R.id.player_btn_collection);
    	setCollect();
    	
    	
    	pop_confirm_window_hide = findViewById(R.id.pop_confirm_window_hide);
    	
    	//测试按钮
		findViewById(R.id.player_btn_test).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DanmuContentData content = new DanmuContentData();
				content.msg="测试弹幕"+ i++;
				content.nick = "测试";
				if(danmuListAdapter != null){
					danmuListAdapter.addData(content);
					danmuListAdapter.notifyDataSetChanged();
				}
				danmuSurfaceView.addDanmakuDisplayByNow(content);
				showInfo(i);
			}
		});
		
    	//举报按钮控制
    	titleControlLayout.findViewById(R.id.video_btn_report_politics).setOnClickListener(netCommonOnClickListener);
    	titleControlLayout.findViewById(R.id.video_btn_report_porn).setOnClickListener(netCommonOnClickListener);
    	titleControlLayout.findViewById(R.id.video_btn_reporting_advert).setOnClickListener(netCommonOnClickListener);
    	titleControlLayout.findViewById(R.id.video_btn_report_other).setOnClickListener(netCommonOnClickListener);
    	
    	titleControlLayout.findViewById(R.id.video_control_share).setOnClickListener(btnMenuClickListener);
    	//menu 弹出layout
    	title_report_layout = titleControlLayout.findViewById(R.id.video_control_reporting_layout);
    	video_sound_layout = findViewById(R.id.video_sound_layout);
    	video_danmu_layout = findViewById(R.id.video_danmu_layout);
    	video_danmu_send_layout = findViewById(R.id.video_danmu_send_layout);
    	//图形按钮，控制弹出的各种控制
    	View btn_report = titleControlLayout.findViewById(R.id.video_control_reporting);
    	btn_report.setVisibility(View.VISIBLE);
    	btn_report.setOnClickListener(btnMenuClickListener);
    	attentionMoreLayout = findViewById(R.id.player_attention_more_layout);
    	findViewById(R.id.video_btn_control_back).setOnClickListener(btnClickListener);
    	findViewById(R.id.player_btn_attention_more).setOnClickListener(btnMenuClickListener);
    	findViewById(R.id.video_btn_sound).setOnClickListener(btnMenuClickListener);
    	findViewById(R.id.video_btn_danmu_option).setOnClickListener(btnMenuClickListener);
    	findViewById(R.id.video_btn_danmu_send).setOnClickListener(btnMenuClickListener);
 
    	
    	titleTextView = (TextView) findViewById(R.id.video_control_title);
    	
    	//关注菜单控制
    	findViewById(R.id.player_danmu_setting).setOnClickListener(btnMenuClickListener);
    	danmuswitchButton =  (TextView) findViewById(R.id.player_danmu_switch);
		initDanmuswitch();
		
		findViewById(R.id.player_danmu_see).setOnClickListener(btnMenuClickListener);
		
    	//显示观看者，赞，弹幕部分信息
		video_stat_watchersNum = (TextView) findViewById(R.id.video_stat_watchersNum_num);
		video_stat_bulletsNum = (TextView) findViewById(R.id.video_stat_bulletsNum);
		video_stat_upNum = (TextView) findViewById(R.id.video_stat_upNum);
		video_stat_distance = (TextView)findViewById(R.id.video_stat_distance);
		
    	if(videoData != null){
    		setVideoStatNum(videoData.watchersNum+"", videoData.bulletsNum+"", videoData.upNum+"");
    		titleTextView.setText(videoData.title);
    	}
    	
    	// 声音控制
    	SeekBar seekSound = (SeekBar)findViewById(R.id.video_btn_sound_change);
    	seekSound.setMax(mAudioMax);
    	int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    	oldVolume = currentVolume;
    	seekSound.setProgress(currentVolume);
    	seekSound.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onProgressChanged(SeekBar bar, int value, boolean fromuser) {
				setAudioVolume(value,true);
				
			}
		});
    	
    	//处理弹幕设置按钮
    	danmuOptionLayout = (DanmuOptionLayout) findViewById(R.id.danmu_option);
    	danmuOptionLayout.setDanmuSurfaceView(danmuSurfaceView);
    	subMenuList.add(title_report_layout);
    	subMenuList.add(video_sound_layout);
    	subMenuList.add(video_danmu_layout);
    	subMenuList.add(attentionMoreLayout);
    	subMenuList.add(video_danmu_send_layout);
    	subMenuList.add(danmuOptionLayout);
    	subMenuList.add(video_danmu_list_layout);
    	
    	btnsubMenuList.add(btn_report);
    	btnsubMenuList.add(findViewById(R.id.video_btn_sound));
    	btnsubMenuList.add(findViewById(R.id.video_btn_danmu_option));
    	btnsubMenuList.add(findViewById(R.id.video_btn_danmu_send));
    	btnsubMenuList.add(findViewById(R.id.player_btn_attention_more));
	}
	@Override
	protected boolean hidenSubMenu(){
		boolean result = false;
		for(View view :subMenuList){
			if(view.getVisibility() == View.VISIBLE){
				view.setVisibility(View.INVISIBLE);
				result = true;
			}
		}
		if(result){
			for(View view : btnsubMenuList){
				view.setSelected(false);
			}
		}
		if(true){
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.hideSoftInputFromWindow(danmuInput.getWindowToken(), 0);
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			} 
		}
		return result;
	}
	
	@Override
	protected long getTime() {
		long time =  super.getTime() - delayTime;
		if(time<0){
			delayDeal(false);
			return 0;
		}
		delayDeal(true);
		return time;
	}
	private void delayDeal(boolean isdone){
		if(delayDone)
			return;
		delayDone = isdone;
		if(isdone){
			setAudioVolume(oldVolume,false);
			player_blocked.setVisibility(View.GONE);
		}else{
			setAudioVolume(0,false);
		}
		
	}
	private void setCollect() {
		try {
			if(collectVideoHelper.findOne(videoData.videoID) == null){
				btnCollection.setOnClickListener(btnClickListener);
			}else{
				btnCollection.setText(R.string.player_btn_collected);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			btnCollection.setOnClickListener(btnClickListener);
		}
	}
	private void setfocus() {
		try {
			if(personController.isFocused(videoData.account)){
				btnfocus.setText(R.string.player_btn_focused);
			}else{
				btnfocus.setOnClickListener(netCommonOnClickListener);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			btnfocus.setOnClickListener(netCommonOnClickListener);
		}
	}
	
	private void initDanmuList() {
    	video_danmu_list_layout = findViewById(R.id.video_danmu_list_layout);
    	danmu_list_view = (DanmuListView) video_danmu_list_layout.findViewById(R.id.danmu_list_view);
    	View view = video_danmu_list_layout.findViewById(R.id.danmu_list_view_empty);
    	danmu_list_view.setEmptyView(view);
    	
    	danmuListAdapter = new DanmuListAdapter(this,null);
    	danmu_list_view.setAdapter(danmuListAdapter);
    	View hideDanmuView = video_danmu_list_layout.findViewById(R.id.video_btn_hide_danmu_list);
    	danmuListAdapter.notifyDataSetChanged();
    	hideDanmuView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				video_danmu_list_layout.startAnimation(AnimationUtils.loadAnimation(VideoPlayerActivity.this, android.R.anim.fade_out));
				video_danmu_list_layout.setVisibility(View.GONE);
			}
		});
	}
	
    /*上传观看记录 ,是否失败不再提示用户*/
    public void reportLookRecoder() {
    	if(videoData == null)
    		return;
		try{
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					 netIF_ZBRJ.m1_upload_viewInfo("2", videoData);
				}
			});
			thread.start();
		}catch(Exception e){
			Log.e(TAG, "上报观看记录失败" ,e);
		}
	}
    
    public void reportLookEnd(){
    	try{
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					 netIF_ZBRJ.m1_upload_viewInfo("0", videoData);
				}
			});
			thread.start();
		}catch(Exception e){
			Log.e(TAG, "上报观看记录失败" ,e);
		}
    }


	private void setReportVisible(View TitleControlLayout) {
		View report = TitleControlLayout.findViewById(R.id.video_control_reporting);
    	report.setVisibility(View.VISIBLE);
	}


	private void setActionText() {
		String text = "";
		if(videoData != null){
			if(videoData.isComp == 1){
				text = getString(R.string.competition);
			}else if(videoData.isLive == 1){
				text = getString(R.string.live);
			}else{
				text = getString(R.string.playback);
			}
		}
		actionTextView.setText(text);
	}


	@Override
    protected void onPause() {
        super.onPause();
        
		
    }

   
    @Override
    protected void onDestroy() {
        super.onDestroy();
		try {
			if(danmuQueryRunnable != null){
				try {
					danmuQueryRunnable.stop();
				} catch (Exception e) {
				}
	        }
			if(danmuQueryThread != null)
				danmuQueryThread = null;
			stopCheckTimeOutThread();
			shareMenuUtil.clear();
			danmuSurfaceView.stop();
			danmuSurfaceView.release();
			removeImListener();
			stopVideoNumberRunnable();
			hidePopupwindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void startPrePlay(){
    	if(videoData != null){
    		//
    		/*if(videoData.isLive == 1){*/
    			addImListener();
    		/*}*/
    		if(videoData.isLive == 1 && videoData.isPublic == 0 && canPlay == false){
    			try {
					CustomProtocolDealerManager cdm = ImCore.getInstance().getCustomProtocolDealerManager();
					cdm.createDealer(videoData.account).request_join_live(videoData.videoID);
					mHandler.sendEmptyMessage(ProtocalMessageWhat.request_join_live);
				} catch (Exception e) {
					Log.e(e.getMessage(), e);
					showInfo("发送请求失败！");
					mHandler.sendEmptyMessageDelayed(AUDIO_SERVICE_CONNECTION_FAILED, 3000);
				}
    		}else{
    			canPlay = true;
        		mHandler.sendEmptyMessage(ProtocalMessageWhat.acceptPlay);
        		reportLookRecoder();
        		if(danmuSurfaceView.isPaused()){
        			danmuSurfaceView.resume();
        		}
                
    		}
    		startDanmuSurface();
    	}
    }

    private void addImListener() {
		if(imlistener == null)
			imlistener = new VideoPlayerProtocolListener(mHandler,videoData.videoID);
		OnconIMCore.getInstance().addCustomProtocolListener(imlistener);
	}
   private void removeImListener(){
	   if(imlistener != null){
		   OnconIMCore.getInstance().removeCustomProtocolListener(imlistener);
		   imlistener = null;
	   }
   }


    public  static void start(Context context ,VideoData videoData){
    	if(videoData == null)
    		videoData = new VideoData();
    	// 测试，使用本地视频
    	//videoData.playUrl="file:///storage/sdcard1/wahaha.flv";
    	//videoData.videoID="0215f553-7a1e-44fd-bfc8-d0aa94f9b67d";
    	//videoData.isLive = 1;
    	//videoData.isPublic = 0;
		Intent intent = new Intent(context, VideoPlayerActivity.class);
        //intent.setAction(VideoPlayerActivity.PLAY_FROM_VIDEOGRID);
        intent.putExtra("itemLocation", videoData.playUrl);
        intent.putExtra("itemTitle", videoData.title);
        intent.putExtra("dontParse", false);
        intent.putExtra("fromStart", true);
        intent.putExtra("itemPosition", -1);
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoData", videoData);
        intent.putExtras(bundle);
      /*  if (dontParse)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);*/

        context.startActivity(intent);
	}
   


    protected void hideOverlay(boolean fromUser){
    	super.hideOverlay(fromUser);
    	/*titleControlLayout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    	blowControlLayout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    	statlayout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    	titleControlLayout.setVisibility(View.INVISIBLE);
    	blowControlLayout.setVisibility(View.INVISIBLE);
    	title_report_layout.setVisibility(View.GONE);
    	 statlayout.setVisibility(View.INVISIBLE);*/
    }
   
    /**
     * Handle resize of the surface and the overlay
     */
   @Override  
    protected void showOverlayTimeout(int timeout) {
    	super.showOverlayTimeout(timeout);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        /*titleControlLayout.setVisibility(View.VISIBLE);
        blowControlLayout.setVisibility(View.VISIBLE);
        statlayout.setVisibility(View.VISIBLE);*/
    }




	private void startCheckTimeOutThread() {
    	timeoutCheck = true;
    	checkTimeOutThread = new Thread(new Runnable() {
			int i=0;
			public void run() {
				while (i < TIMEOUTSECOND && timeoutCheck) {
					i++;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(i>=TIMEOUTSECOND && timeoutCheck){
					Message msg = new Message();
					//msg.getData().getInt("event")
					Bundle data = new Bundle();
					data.putInt("event", EventHandler.MediaPlayerEncounteredError);
					msg.setData(data);
					mEventHandler.sendMessage(msg);
				}
			}
		});
    	checkTimeOutThread.start();
		
	}
    private void stopCheckTimeOutThread(){
    	if(checkTimeOutThread != null){
    		try{
    			timeoutCheck = false;
    			checkTimeOutThread = null;
    		}catch(Exception e){
    			Log.e("停止检查超时失败", e);
    		}
    	}
    }



    
    
    //add new method
    private void initDanmusend() {
		danmuInput = (EditText) findViewById(R.id.player_danmu_input);
		danmusendButton = (TextView) findViewById(R.id.player_danmu_send);
	/*	danmuInput.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int arg1, KeyEvent arg2) {
				danmu_send();
				return false;
			}
		});*/
		try{
			
			if(videoData != null){
				danmuSurfaceView.setStartTime(videoData.dateTime);
				if(videoData.isLive == 1){
					initDanmuThread();
				}else if(videoData.isLive == 0){
					initDanmuFileThread();
				}
			}
			danmusendButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					danmu_send();
				}
			});
		}catch(Exception e){
			Log.e(TAG, e);
		}
		
	}
	private void forbidDanmu(boolean forbid) {
		forbidDanmuFlag = forbid;
	}
	private void setVideoStatNum(String watchersNum , String bullettNum ,String upNum){
		video_stat_watchersNum.setText(watchersNum);
		video_stat_bulletsNum.setText(bullettNum);
		video_stat_upNum.setText(upNum);
		String distance = DisUtil.distance(videoData.locationX + "", videoData.locationY + "");
		video_stat_distance.setText(distance);

	}

	private void initDanmuFileThread() {
		DanmuFileLoadRunnable runnable = new DanmuFileLoadRunnable(this ,mEventHandler , danmuSurfaceView,videoData.bulletFile,danmuListAdapter);
		danmuQueryThread = new Thread(runnable);
		
	}


	private void initDanmuThread() {
		danmuQueryRunnable = new DanmuQueryRunnable(netIF_ZBRJ, danmuSurfaceView, videoData.videoID  ,danmuListAdapter);
		danmuQueryThread = new Thread(danmuQueryRunnable);
		danmuthreadStarted = false;
	}
	
	private boolean isLive(){
		if(videoData == null || videoData.isLive != 1){
			return false;
		}else{
			return true;
		}
	}
	
	 public void startDanmuThread() {
		if(danmuthreadStarted)
			return;
		if(danmuQueryThread == null)
			return;
		danmuthreadStarted= true;
		danmuQueryThread.start();
	 }

	private void initNetIfs() {
		netIF_ZBRJ =new NetIF_ZBRJ(this);
	}
	private void initDanmuswitch() {
		danmuswitchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String onString = getResources().getString(R.string.player_danmu_swich_on);
				String offString = getResources().getString(R.string.player_danmu_swich_off);
				if(danmuswitchButton.getText().equals(onString)){
					//danmuSurfaceView.start();
					danmuSurfaceView.show();
					danmuswitchButton.setText(offString);
				}else{
					//danmuSurfaceView.stop();
					danmuSurfaceView.hide();
					danmuswitchButton.setText(onString);
				}
			}
		});
		
	}
	
	private void startVideoNumberRunnable(){
		if(videoNumberQueryRunnable != null)
			videoNumberQueryRunnable.stop();
		videoNumberQueryRunnable = new VideoNumberQueryRunnable(this, false, videoData.videoID, mEventHandler, EventVidoNumberChange);
		Thread thread = new Thread(videoNumberQueryRunnable);
		thread.start();
			
	}
	private void stopVideoNumberRunnable(){
		if(videoNumberQueryRunnable != null)
			videoNumberQueryRunnable.stop();
	}
	
	 private String convertTimeToString(long playerTime){
			if(playerTime<=0)
				return "00:00";
			else{
				long min = (playerTime/60000);
				long sec = (playerTime%60000)/1000;
				StringBuilder sb = new StringBuilder();
				if(min<10){
					sb.append(0);
				}
				sb.append(min).append(":");
				if(sec<10){
					sb.append(0);
				}
				sb.append(sec);
				return sb.toString();
			}
			
		}
	 //设置播放title 的用户头像和昵称
	 public void setTilePersonData() {
	 		
			PersonData personData = personController.findPerson(videoData.account);
			if(personData != null){
				personNick.setText(personData.nickname);
				String img = personData.image;
				if (StringUtils.isNull(img)) {
					personHead.setVisibility(View.INVISIBLE);
				} else {
					try {
						ImageLoader.displayHeadImage(img, personHead);
						personHead.setVisibility(View.VISIBLE);
					} catch (Exception e) {
						Log.e(Constants.LOG_TAG, e);
						personHead.setVisibility(View.INVISIBLE);
					}
				}
			}
		}
	 
	private void addPopupData(PopupData data) {
		popupwindow(data);
	}
	
	private void entrust_invite_video() {
		PopupData data = new PopupData();
		data.msg = getResources().getString(R.string.entrust_invite_video_confirm);
		data.title="";
		data.callback = new PopupData.Callback() {
			@Override
			public void callback() {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try{
							sendIMStatus(SendIMLoopRunnable.invite_video);
							threadToastMessage("邀请成功") ;
						}catch(Exception e){
							com.lb.common.util.Log.e(e.getMessage(), e);
							threadToastMessage("邀请失败") ;
						}
					}
				});
				thread.start();
			}
		};
		addPopupData(data);
	}
	
	private void alertExit(){
		PopupData data = new PopupData();
		data.msg = getResources().getString(R.string.video_exist);
		data.title="        ";
		data.callback = new PopupData.Callback() {
			@Override
			public void callback() {
				myfinish();
			}
		};
		addPopupData(data);
	}
	@Override
	protected void myfinish(){
		sendIMStatus(SendIMLoopRunnable.donothing);
		try {
			mSeekbar.setOnSeekBarChangeListener(null);
	        /* Stop the earliest possible to avoid vout error */
	        if (isFinishing())
	            stopPlayback();
			danmuSurfaceView.stop();
			danmuSurfaceView.release();
			removeImListener();
			stopVideoNumberRunnable();
			reportLookEnd();
			hidePopupwindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		oldfinish();
	}
	private final Handler mEventHandler = new MyEventHandler(this);

	private class MyEventHandler extends
			WeakHandler<VideoPlayerActivity> {
		public MyEventHandler(VideoPlayerActivity owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoPlayerActivity activity = getOwner();
			if(activity.isFinishing())
				return;
			// Do not handle events if we are leaving the VideoPlayerActivity
			switch (msg.what) {
				case MYEVENT_SHOWTASH:
					{
					String s = msg.getData().getString("msg");
					if (s == null || "".equals(s.trim()))
						return;
					activity.showInfo(s);
					}
					break;
				case MYEVENT_DANMULOAD_SUCCESS:
					activity.showInfo( R.string.player_danmu_download_success);
					initDanmuThread();
					startDanmuThread();
					break;
				case MYEVENT_DANMULOAD_FAIL:
					activity.showInfo( R.string.player_danmu_download_fail);
					initDanmuThread();
					startDanmuThread();
					break;
				case EventVidoNumberChange:
					Bundle data = msg.getData();
					setVideoStatNum(data.getString("watchersNum")+"("+data.getString("friendNum")+")", data.getString("bulletsNum"), data.getString("upNum"));
					break;
				case EventHideSumMenu: //隐藏所有子菜单
					hidenSubMenu();
					break;
				case MYEVENT_SHOWTOAST:
					{
					String s = msg.getData().getString("msg");
					if (s == null || "".equals(s.trim()))
						return;
					toastToMessage(s);
					}
					break;
			}
		
		}
	}
	private OnClickListener btnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int id = view.getId();
			if(id == R.id.video_btn_control_back){
				alertExit();
			}else if(id == R.id.player_btn_collection){
				try{
					VideoData vv = collectVideoHelper.findOne(videoData.videoID);
					if(vv == null){
						collectVideoHelper.add(videoData);
					}
					showInfo(getString(R.string.player_btn_collect)+getString(R.string.success));
					btnCollection.setText(R.string.player_btn_collected);
					btnCollection.setOnClickListener(null);
				}catch(Exception e){
					showInfo(getString(R.string.player_btn_collect)+getString(R.string.fail));
				}
			}
		}
	};
	private OnClickListener btnMenuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			int id = view.getId();
			View operateView = null;
			switch (id) {
			case R.id.video_control_reporting:
				operateView = title_report_layout;
				break;
			case  R.id.player_btn_attention_more:
				operateView = attentionMoreLayout;
				break;
			case R.id.video_btn_sound:
				operateView = video_sound_layout;
				break;
			case R.id.video_btn_danmu_option:
				operateView = video_danmu_layout;
				break;
			case R.id.video_btn_danmu_send:
				operateView = video_danmu_send_layout;
				break;
			case R.id.player_danmu_setting:
				operateView = danmuOptionLayout;
				break;
			case R.id.player_danmu_see:
				operateView = video_danmu_list_layout;
				break;
			case R.id.video_control_share:
				//pause();
				shareMenuUtil.showShareMenu();
				resumePlay = true;
				break;
			default:
				showInfo("开发中");
				return;
			}
			if(operateView != null){
				if(operateView.getVisibility() != View.VISIBLE){
					hidenSubMenu();
					view.setSelected(true);
					operateView.setVisibility(View.VISIBLE);
				}else{
					view.setSelected(false);
					operateView.startAnimation(AnimationUtils.loadAnimation(VideoPlayerActivity.this, android.R.anim.fade_out));
					operateView.setVisibility(View.INVISIBLE);
				}
			}
		}
	};
	private OnClickListener netCommonOnClickListener = new OnClickListener() {	
		@Override
		public void onClick(final View view) {
			int action = 0;
			String content = "";
			String actionName="";
			CallBack callBack  = null;
			switch (view.getId()) {
			case R.id.player_btn_focus:
				action = NetAsyncTask.action_add_focus;
				actionName = getResources().getString(R.string.player_btn_focus);
				callBack = new CallBack() {
					@Override
					public void doAfterNet() {
						btnfocus.setText(R.string.player_btn_focused);
						btnfocus.setOnClickListener(null);
					}
				};
				break;
			case R.id.player_btn_like:
				String like = getResources().getString(R.string.player_btn_like);
				String cancellike = getResources().getString(R.string.player_btn_cancel_like);
				if(btnLike.getText().equals(like)){
					action = NetAsyncTask.action_like;
					actionName = like;
				}else{
					action = NetAsyncTask.action_cancel_like;
					actionName = cancellike;
				}
				final int intaction = action;
				callBack = new CallBack() {
					@Override
					public void doAfterNet() {
						if(intaction == NetAsyncTask.action_like){
							btnLike.setText(R.string.player_btn_cancel_like);
						}else{
							btnLike.setText(R.string.player_btn_like);
						}
					}
				};
				break;
			case R.id.video_btn_report_politics:
			case R.id.video_btn_report_porn:
			case R.id.video_btn_reporting_advert:
			case R.id.video_btn_report_other:
				PopupData data = new PopupData();
				data.msg = getResources().getString(R.string.view_reporting_confirm);
				data.callback = new PopupData.Callback() {
					
					@Override
					public void callback() {
						NetAsyncTask netAsyncTask = new NetAsyncTask(VideoPlayerActivity.this,videoData);
						netAsyncTask.action =  NetAsyncTask.action_report;;
						netAsyncTask.content = ((TextView)view).getText().toString();
						netAsyncTask.actionName = getResources().getString(R.string.video_control_reporting);
						AppUtil.execAsyncTask(netAsyncTask);
						
					}
				};
				addPopupData(data);
				break;
			default:
				break;
			}
			if(action >0){
				NetAsyncTask netAsyncTask = new NetAsyncTask(VideoPlayerActivity.this,videoData);
				netAsyncTask.action = action;
				netAsyncTask.content = content;
				netAsyncTask.actionName = actionName;
				netAsyncTask.callBack = callBack;
				AppUtil.execAsyncTask(netAsyncTask);
			}
		}
	};
	
	
	class NetAsyncTask extends BaseNetAsyncTask{
		Context context;
		public static final int action_like = 1;
		public static final int action_add_focus = 2;
		public static final int action_remove_focus = 3;
		public static final int action_report = 4;
		public static final int action_cancel_like = 5;
		public int action = 0;
		public String content;
		private String actionName;
		private VideoData videoData;
		public CallBack callBack = null;
		public NetAsyncTask(Context context,VideoData videoData) {
			super(context);
			this.context = context;
			this.videoData = videoData;
		}

		@Override
		public NetInterfaceStatusDataStruct doNet() {
			if(action == action_like){
				return super.ni.m1_give_like(videoData.videoID);
			}
			if(action == action_cancel_like){
				return super.ni.m1_cancel_like(videoData.videoID);
			}
			if(action == action_remove_focus)
				return super.ni.m1_cancel_focus(videoData.account,0);
			if(action == action_report)	{
				return super.ni.m1_report(videoData.account, content);
			}
			if(action == action_add_focus){
				PersonController mPersonController = new PersonController();
				NetInterfaceStatusDataStruct result = null;
				result = ni.m1_get_personalInfo(videoData.account);
				if(!Constants.RES_SUCCESS.equals(result.getStatus())){
					return result;
				}
				PersonData person = (PersonData)result.getObj();
				result =  super.ni.m1_add_focus(videoData.account, 0);
				if(!Constants.RES_SUCCESS.equals(result.getStatus())){
					return result;
				}
				FansData fans = new FansData();
				fans.account = person.account;
				fans.nick = person.nickname;
				fans.imageurl = person.image;
				mPersonController.addFocus(fans);
				return result;
			}
			NetInterfaceStatusDataStruct result = new NetInterfaceStatusDataStruct();
			result.setMessage("正在开发中");
			return result;
		}

		@Override
		protected void onPreExecute() {
			//不做任何事情
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if (Constants.RES_SUCCESS.equals(result.getStatus())) {
				showInfo(actionName+getResources().getString(R.string.success));
				if(callBack != null)
					callBack.doAfterNet();
			} else {
				if (TextUtils.isEmpty(result.getMessage())) {
					showInfo(R.string.servernoresponse);
				} else {
					showInfo(result.getMessage());
				}
			}
			
		}
		
	 }
	 interface CallBack{
		public void doAfterNet();
	}
	 
	@Override
	protected Context getActitity() {
		return VideoPlayerActivity.this;
	}




	@Override
	protected String getVideoTitle() {
		if(videoData != null)
			return videoData.title;
		return "";
	}




	@Override
	protected String getVideoLocation() {
		if(videoData != null)
			return videoData.playUrl;
		return "";
	}

	private void popupwindow(final PopupData data){
		View v = pop_confirm_window_hide;
		hidePopupwindow();
		View popupView = LayoutInflater.from(this).inflate(R.layout.pop_confirm_window, null);
		TextView confrim_title = (TextView) popupView.findViewById(R.id.confrim_title);
		TextView confirm_text = (TextView) popupView.findViewById(R.id.confirm_text);
		confirm_text.setText(data.msg);
		if(data.title != null)
			confrim_title.setText(data.title);
		View btn_yes = popupView.findViewById(R.id.btn_yes);
		View btn_no	= popupView.findViewById(R.id.btn_no);
		
		popwinWindow = new PopupWindow(popupView,  v.getWidth(), v.getHeight());
		OnClickListener closeClickListener = new OnClickListener() {
			public void onClick(View arg0) {
				popwinWindow.dismiss();
			}
		};
		OnClickListener okClickListener = new OnClickListener() {
			public void onClick(View arg0) {
				if(data.callback != null)
					data.callback.callback();
				hidePopupwindow();
			}
		};
		btn_yes.setOnClickListener(okClickListener);
		btn_no.setOnClickListener(closeClickListener);
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		popwinWindow.showAtLocation(v, Gravity.NO_GRAVITY ,location[0], location[1]);
		popwinWindow.update();
	}
	private void hidePopupwindow(){
		if(popwinWindow != null && popwinWindow.isShowing()){
			popwinWindow.dismiss();
			popwinWindow = null;
		}
	}
	@Override
	protected void childVLCHandler(Message msg) {
		switch (msg.getData().getInt("event")) {
        case EventHandler.MediaParsedChanged:
              break;
        case EventHandler.MediaPlayerPlaying:
            stopCheckTimeOutThread();
            
            startDanmuThread();
            break;
        case EventHandler.MediaPlayerPaused:
        	//danmuSurfaceView.pause();
            Log.i(TAG, "MediaPlayerPaused");
            break;
        case EventHandler.MediaPlayerStopped:
            Log.i(TAG, "MediaPlayerStopped");
            //danmuSurfaceView.stop();
            break;
        case EventHandler.MediaPlayerEndReached:
        	reportLookEnd();
        	
            break;
		}
		
	}
	protected void endReached(){
		super.endReached();
		String type = "";
		if(videoData.isLive == 1){
			type = getResources().getString(R.string.live);
		}else{
			type = getResources().getString(R.string.playback);
		}
		sendIMStatus(SendIMLoopRunnable.donothing);
		 mAlertDialog = new AlertDialog.Builder(getActitity())
	        .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int id) {
	                myfinish();
	            }
	        })
	        .setTitle(R.string.memo)
	        .setMessage(getResources().getString(R.string.video_end ,type))
	        .create();
	        mAlertDialog.show();
	}
	@Override
	protected void childPlayHandler(Message msg) {
		switch (msg.what) {
		case FADE_OUT:
			break;
		case SHOW_PROGRESS:
			break;
		case SURFACE_LAYOUT:
			break;
		case FADE_OUT_INFO:
			break;
		case AUDIO_SERVICE_CONNECTION_SUCCESS:
			startCheckTimeOutThread();
			startPrePlay();
			break;
		case AUDIO_SERVICE_CONNECTION_FAILED:
			break;
		case RESET_BACK_LOCK:
			break;
		case ProtocalMessageWhat.request_join_live:
			showInfo("等待播主确认加入直播");
			break;
		case ProtocalMessageWhat.acceptPlay:
			if (videoData.isLive == 1)
				startVideoNumberRunnable();
			int type = 0;
			if(videoData.isLive== 1){
				type =SendIMLoopRunnable.start_watch_live;
			}else{
				type = SendIMLoopRunnable.start_watch_playback;
			}
			sendIMStatus(type);
			watchHistoryHelper.add(videoData);
			break;
		case ProtocalMessageWhat.refusePlay:
			showInfo(msg.getData().getString("message"));
			msg = mEventHandler.obtainMessage(AUDIO_SERVICE_CONNECTION_FAILED);
			mEventHandler.sendMessageDelayed(msg, 3000);
			break;
		case ProtocalMessageWhat.kick_off_video:
			canPlay = false;
			showInfo(msg.getData().getString("message"));
			stopPlayback();
			stopVideoNumberRunnable();
			msg = mEventHandler.obtainMessage(AUDIO_SERVICE_CONNECTION_FAILED);
			mEventHandler.sendMessageDelayed(msg, 3000);
			break;
		case ProtocalMessageWhat.mute_video:
		case ProtocalMessageWhat.forbid_bullet:
		{
			String ftype = msg.getData().getString("type");
			if("1".equals(ftype)){
				showInfo("播主开启弹幕", 5000);
				forbidDanmu(false);
			}else{
				showInfo("播主禁止弹幕", 5000);
				forbidDanmu(true);
			}
			
		}
			break;
		case ProtocalMessageWhat.entrust_invite_video: //委托邀请 ，，播主委托你邀请你的好友收看此直播
			entrust_invite_video();
		//case ProtocalMessageWhat.invite_video:
			break;
		
		}
	}
	
	public String getNickName(String account){
		String nick =  personController.findNameByMobile(account);
		if(StringUtils.isNull(nick))
			return account;
		else {
			return nick;
		}
	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	alertExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	public void sendIMStatus(final int type){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					NetInterfaceStatusDataStruct data = netIF_ZBRJ.m1_get_fans(AccountData.getInstance().getBindphonenumber(), -1, 100);
					ArrayList<FansData> list = null;
					if(!Constants.RES_SUCCESS.equals(data.getStatus())){
						return;
					}
					list = (ArrayList<FansData>) data.getObj();
					if(list.size() == 0){
						return ;
					}
					List<String> listaccount = new java.util.ArrayList<String>();
					for(FansData fan : list){
						listaccount.add(fan.account);
						//FIXME 委托邀请只取10个粉丝
						if(type == ProtocalMessageWhat.entrust_invite_video && listaccount.size()>=10){
							break;
						}
					}
					list = null;
					data = null;
					MyApplication.getInstance().imTaskPool.submit(new SendIMLoopRunnable(type, videoData, listaccount));
				}catch(Exception e){
					com.danmu.comm.Log.e(e.getMessage(), e);
				}
			}
		}).start();
	}
	private void danmu_send() {
		final String s = danmuInput.getText().toString();
		if(forbidDanmuFlag == true){
			showInfo( "播主禁止发送弹幕");
			return;
		}
		if(s == null || s.trim().equals("")){
			showInfo("不能发送空内容");
			return;
		}
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				DanmuSendData data = new DanmuSendData();
				data.videoID = videoData.videoID;
				data.content.id = netIF_ZBRJ.getRandomNumber()+"";
				data.content.account = AccountData.getInstance().getBindphonenumber();
				if(nick == null)
					nick = getNickName(data.content.account);
				data.content.nick = nick;
				data.content.fontsize = danmuSurfaceView.fontSizeLevel+"";
				data.content.location = danmuSurfaceView.locationLevel+"";
				data.content.speed = danmuSurfaceView.speedLevel+"";
				data.content.distance= DisUtil.number2String(distanceInt);
				Calendar cal = Calendar.getInstance();
				long time = 0;
				if(videoData.isLive == 1){
					time = cal.getTimeInMillis() - danmuSurfaceView.getStartTime();
				}else{
					time = getTime();
				}
				data.content.time = convertTimeToString(time);
				data.content.msg = s;
				data.id = netIF_ZBRJ.getRandomNumber()+"";
				CommResData result  = netIF_ZBRJ.danmu_send(data);
				Log.i("danmu_send", result.status+"------"+result.desc);
				if(!Constants.RES_SUCCESS.equals(result.status)){
					Message msg = new Message();
					msg.what = MYEVENT_SHOWTASH;
					Bundle bu = new Bundle();
					bu.putString("msg", StringUtils.isNull(result.desc)?"发送弹幕失败":result.desc);
					msg.setData(bu);
					mEventHandler.sendMessage(msg);
				}else{
					mEventHandler.sendEmptyMessage(EventHideSumMenu);
				}
				
			}
		});
		thread.start();
		danmuInput.setText("");
	}
	 protected int getURLErrorText(){
		 if(videoData != null && videoData.isLive ==1){
			 return R.string.encountered_error_message_live;
		 }else{
			 return R.string.encountered_error_message;
		 }
	 }
	 protected boolean needResume() {
		if(videoData != null && videoData.isLive ==1)
			return false;
		return true;
	}
	 private void threadToastMessage(String s){
		 Message msg = new Message();
			msg.what = MYEVENT_SHOWTOAST;
			Bundle bu = new Bundle();
			bu.putString("msg", s);
			msg.setData(bu);
			mEventHandler.sendMessage(msg);
	 }
}
