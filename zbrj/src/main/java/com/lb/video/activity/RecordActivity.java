package com.lb.video.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.videolan.vlc.util.Strings;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.util.WeakHandler;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danmu.widget.DanmuSurfaceView;
import com.lb.common.util.BaiduLocation;
import com.lb.common.util.BaiduLocation.BaiduLocationLister;
import com.lb.common.util.Constants;
import com.lb.common.util.DateUtil;
import com.lb.common.util.ImageUtil;
import com.lb.common.util.Log;
import com.lb.common.util.ShareMenuUtil;
import com.lb.common.util.StringUtils;
import com.lb.common.util.corpimage.CropImage;
import com.lb.video.adapter.DanmuListAdapter;
import com.lb.video.adapter.data.AdapterData;
import com.lb.video.comm.TouchEvent;
import com.lb.video.data.CommResData;
import com.lb.video.data.ShareData;
import com.lb.video.data.VideoStartRecorderData;
import com.lb.video.im.VideoRecorderProtocolListener;
import com.lb.video.job.DanmuQueryRunnable;
import com.lb.video.job.SendIMLoopRunnable;
import com.lb.video.job.VideoLivePingRunnable;
import com.lb.video.job.VideoNumberQueryRunnable;
import com.lb.video.view.DanmuOptionLayout;
import com.lb.video.widget.DanmuListView;
import com.lb.video.widget.WiperSwitch;
import com.lb.video.widget.WiperSwitch.OnChangedListener;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.ChannelData;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.data.db.ChannelHelper;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lb.zbrj.util.Base64;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.CustomProtocolDealerManager;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.contact.ContactMsgCenterActivity;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.util.SystemCamera;
import com.lz.oncon.application.AppUtil;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog;
import com.lz.oncon.widget.CameraGalleryWithClearChoiceDialog.OnChoiceClickListener;
import com.xuanbo.xuan.R;


public class RecordActivity extends BaseActivity {
    private final static String CLASS_LABEL = "RecordActivity";
    private final static String LOG_TAG = CLASS_LABEL;
    private static int MAX_RECORDING_TIME=6*60*1000;
    private final static int EventNoPremiss = -1;
    private final static int EventFinish = -2;
    private final static int EventSaveLiveOk = 1;
	protected static final int EventSaveLiveFail = 0;
	private  final int EventVidoNumberChange = 2;
    public static final int EventRequestJoinLive= 10;
    public static final int EventRequestJoinLiveEmpty=11;
    public  final int EventLivePingError = 12;
    public static final int EventLiveNetError = 13;
    public static final int EventUpdateStatTime = 14;
    public static final int EventReloadVideoOk = 15;
    public final int EventToastMsg = 20;
    private final int taskAction_cancel = -100;
    private PowerManager.WakeLock mWakeLock;
    

    long startTime = 0;
    long recordingTime = 0;
    //更新状态栏时间
    long updateStatTime = 0;
    boolean recording = false;
    boolean canRecord = true;
    private volatile FFmpegFrameRecorder recorder;

    private boolean isPreviewOn = true;

    private int sampleAudioRateInHz = 16000;
    private int imageWidth = 320;
    private int imageHeight = 240;
    private int previewWidth = imageWidth;
    private int previewHeight = imageHeight;
    private int frameRate = 30;
    List<String> fanAccountList = null;
    /* audio data getting thread */
    private AudioRecord audioRecord;
    private AudioRecordRunnable audioRecordRunnable;
    private DanmuQueryRunnable danmuQueryRunnable;
    private VideoNumberQueryRunnable videoNumberQueryRunnable;
    private VideoLivePingRunnable videoLivePingRunnable;
    private NetIF_ZBRJ netIF_ZBRJ ;
    private Thread audioThread;
    volatile boolean runAudioThread = true;
    private boolean micOn = true;
    /* video data getting thread */
    private Camera cameraDevice;
    private CameraView cameraView;

    private IplImage yuvIplimage = null;
    private int screenWidth, screenHeight;

    /** 代码修改过，只能使用0了*/
    final int RECORD_LENGTH = 0;
    IplImage[] images;
    long[] timestamps;
    ShortBuffer[] samples;
    int imagesIndex, samplesIndex;
    private int cameraPosition=-1;
    PersonController personController = new PersonController();
    private DanmuSurfaceView danmuView;
    public ShareData shareData = new ShareData();
    private View videoStartLayout;
	private View videoSlecttypeLayout;
	private View videoLiveInputLayout;
	
	private View videoControlLayout;
	private View videoStatLayout;
	private View videoTitleLayout;
	private View videoBtnsLayout;
    private TextView displayActionTextView ;
    private EditText videoTitleView ;
    private EditText videoRemarkView;

    private EditText videoChannelName;
    private EditText videoChannelId;
    private Bitmap videoBitmap;
	private Bitmap tempb;
    private ImageView videoImageView;
    private TextView video_stat_watchersNum,video_stat_bulletsNum,video_stat_upNum;
    private TextView video_stat_time;
    
	private String compId = null;
	private TextView video_live_ispublic_txt;
	private WiperSwitch ispublicSwitch;
	private boolean ispublic = true;
	private ListView video_request_listview;
	
	private LinearLayout video_danmu_option_layout;
	private LinearLayout video_invite_option_layout;
	private TextView video_btn_close_danmu;
	private ImageView video_btn_control_misc;
	private DanmuListView danmu_list_view;
	private DanmuListAdapter danmuListAdapter;
	private View video_danmu_list_layout;
	private Handler videoHandler = new VideoHandler(this);
	private VideoRecorderProtocolListener imlistener;
	private TextView video_control_title;
	private DanmuOptionLayout danmuoptionLayout;
	boolean invite_video_done = false;
	boolean entrust_invite_video = false;
	private View pop_confirm_window_hide;
	private boolean popupUpFlag = false;
	private PopupWindow popwinWindow;
	private TextView btn_forbidden_danmu;
	private ShareMenuUtil shareMenuUtil;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_video_recorder);
        try {
			compId = getIntent().getExtras().getString("compid");
		} catch (Exception e) {
		}
        initNetIf();
        initShareData();
        initTopLayout();
        initStartInputLayout();
        initDanmu();
        initControlLayout();
        initDanmuList();
        addImListener();
        // startVideoNumberRunnable
       // startVideoNumberRunnable();
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void reloadVideoDataImageUrl() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				NetInterfaceStatusDataStruct result =  netIF_ZBRJ.m1_get_videoInfo(shareData.videoId);
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					VideoData videoData = (VideoData) result.getObj();
					shareData.videoImage = videoData.videoImage;
					videoHandler.sendEmptyMessage(EventReloadVideoOk);
				}
			}
		}).start();
	}

	private void initNetIf() {
		 netIF_ZBRJ = new NetIF_ZBRJ(this);
	}


	private void initTopLayout() {
		topLayout = (RelativeLayout)findViewById(R.id.videoViewlayout);
	}


	private void initDanmuList() {
    	video_danmu_list_layout = findViewById(R.id.video_danmu_list_layout);
    	TextView emptyTextView = (TextView) findViewById(R.id.danmu_list_view_empty);
    	
    	danmu_list_view = (DanmuListView) video_danmu_list_layout.findViewById(R.id.danmu_list_view);
    	danmu_list_view.setEmptyView(emptyTextView);
    	danmuListAdapter = new DanmuListAdapter(this,shareData);
    	danmu_list_view.setAdapter(danmuListAdapter);
    	View hideDanmuView = video_danmu_list_layout.findViewById(R.id.video_btn_hide_danmu_list);
    	danmuListAdapter.notifyDataSetChanged();
    	hideDanmuView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				video_danmu_list_layout.startAnimation(AnimationUtils.loadAnimation(RecordActivity.this, android.R.anim.fade_out));
				video_danmu_list_layout.setVisibility(View.GONE);
			}
		});
	}


	@Override
    protected void onResume() {
        super.onResume();
		try {
			 boolean check = addCameraLayout();
		        if(check == false){
		        	videoHandler.sendEmptyMessage(EventNoPremiss);
		        }
			if (mWakeLock == null) {
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
						CLASS_LABEL);
				mWakeLock.acquire();
			}
		} catch (Exception e) {
			com.lb.common.util.Log.i("wakeLock 权限没有",e.getMessage());
		}
		if (danmuView != null && danmuView.isPrepared() && danmuView.isPaused()) {
			danmuView.resume();
		}
    }

    private void initControlLayout() {
    	videoControlLayout = findViewById(R.id.video_control_layout);
    	videoStatLayout = findViewById(R.id.video_stat_layout);
    	videoTitleLayout = findViewById(R.id.video_title_layout);
    	videoBtnsLayout = findViewById(R.id.video_control_btns_layout);
    	video_control_title = (TextView) videoTitleLayout.findViewById(R.id.video_control_title);
    	//设置菜单显示还是隐藏的事件
    	videoControlLayout.setOnTouchListener(touchEvent);
    	/*返回按钮设置*/
    	View backView = findViewById(R.id.video_btn_control_back);
   		backView.setOnClickListener(finishClickListener);
   		
   		/*切换摄像头*/
    	View changeCameraView = findViewById(R.id.video_btn_control_cameara);
		changeCameraView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					showProgressDialog(R.string.video_working_waiting, true);
					cameraView.changeCamera();
				} catch (Exception e) {
					com.lb.common.util.Log.e(LOG_TAG, e);
				}finally{
					hideProgressDialog();
				}
			}
		});
    	
    	/*闪关灯设置*/
    	View lightView = findViewById(R.id.video_btn_control_light);
		lightView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				changeLight(true);
			}
		});
		video_btn_control_misc = (ImageView) findViewById(R.id.video_btn_control_misc);
		video_btn_control_misc.setOnClickListener(controlClickListener);
    	/*停止录制按钮*/
    	/*View stopView = findViewById(R.id.video_btn_control_finish);
		stopView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				stopRecording();
				stopVideoNumberRunnable();
			}
		});*/
		video_danmu_option_layout = (LinearLayout) findViewById(R.id.video_danmu_option_layout);
		video_invite_option_layout = (LinearLayout) findViewById(R.id.video_invite_option_layout);
		View danmuoption = findViewById(R.id.video_btn_control_danmu);
		danmuoption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(shareData.isLive)
					displayOptionLayout(R.id.video_danmu_option_layout);
			}
		});
		
		View inviteOption = findViewById(R.id.video_btn_control_invite);
		inviteOption.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(shareData.isLive)
					displayOptionLayout(R.id.video_invite_option_layout);
			}
		});
		danmuoptionLayout = (DanmuOptionLayout) findViewById(R.id.danmu_menu_layout);
		if(danmuoptionLayout != null)
			danmuoptionLayout.setDanmuSurfaceView(danmuView);
		video_danmu_option_layout.findViewById(R.id.video_btn_list_danmu).setOnClickListener(controlClickListener);
		video_btn_close_danmu = (TextView) video_danmu_option_layout.findViewById(R.id.video_btn_close_danmu);
		video_btn_close_danmu.setOnClickListener(controlClickListener);
		btn_forbidden_danmu = (TextView) video_danmu_option_layout.findViewById(R.id.video_btn_forbidden_danmu);
		btn_forbidden_danmu.setOnClickListener(controlClickListener);
		video_danmu_option_layout.findViewById(R.id.video_btn_option_danmu).setOnClickListener(controlClickListener);
		//邀请
		video_invite_option_layout.findViewById(R.id.video_btn_invite_random).setOnClickListener(controlClickListener);
		video_invite_option_layout.findViewById(R.id.video_btn_invite_other).setOnClickListener(controlClickListener);
		// 测试菜单，屏蔽掉
		video_invite_option_layout.findViewById(R.id.video_btn_test).setOnClickListener(controlClickListener);
		
		//显示观看者，赞，弹幕部分信息
		videoStatLayout.findViewById(R.id.video_stat_distance).setVisibility(View.INVISIBLE);
		videoStatLayout.findViewById(R.id.video_stat_distance_icon).setVisibility(View.INVISIBLE);
		
		video_stat_watchersNum = (TextView) videoStatLayout.findViewById(R.id.video_stat_watchersNum_num);
		video_stat_bulletsNum = (TextView) videoStatLayout.findViewById(R.id.video_stat_bulletsNum);
		video_stat_upNum = (TextView) videoStatLayout.findViewById(R.id.video_stat_upNum);
		video_stat_time = (TextView) findViewById(R.id.video_stat_time);
		
		pop_confirm_window_hide = findViewById(R.id.pop_confirm_window_hide);
		findViewById(R.id.video_control_share).setOnClickListener(controlClickListener);
    }

  
	protected void displayOptionLayout(int videoDanmuOptionLayout) {
		if(video_danmu_option_layout.getId() ==videoDanmuOptionLayout && video_danmu_option_layout.getVisibility() == View.INVISIBLE){
			video_danmu_option_layout.setVisibility(View.VISIBLE);
		}else{
			video_danmu_option_layout.setVisibility(View.INVISIBLE);
		}
		if(video_invite_option_layout.getId() == videoDanmuOptionLayout && video_invite_option_layout.getVisibility() == View.INVISIBLE){
			video_invite_option_layout.setVisibility(View.VISIBLE);
		}else{
			video_invite_option_layout.setVisibility(View.INVISIBLE);
		}
	}

	
	

	protected void changeLight(boolean change) {
		try {
			if(cameraDevice == null)
				return;
			Parameters parameters = cameraDevice.getParameters();
			if(parameters == null)
				return;
			//List<String> flashModes = parameters.getSupportedFlashModes();
			String flashMode = parameters.getFlashMode();
			if(flashMode == null){
				return;
			}
			
			if(change){
				if(flashMode.equals(Parameters.FLASH_MODE_TORCH)){
					flashMode = Parameters.FLASH_MODE_OFF;
				}else{
					flashMode = Parameters.FLASH_MODE_TORCH;
				}
			}else{
				flashMode = Parameters.FLASH_MODE_OFF;
			}

		   parameters.setFlashMode(flashMode);
		   cameraDevice.setParameters(parameters);
			
		} catch (Exception e) {
			com.lb.common.util.Log.e(LOG_TAG, e);
		}
		
	}


	private void initShareData() {
    	shareData.account = AccountData.getInstance().getBindphonenumber();
    	shareData.nick = getNickName(shareData.account);
	}
	public String getNickName(String account){
		String nick =  personController.findNameByMobile(account);
		if(StringUtils.isNull(nick))
			return account;
		else {
			return nick;
		}
	}

	private void initStartInputLayout() {
    	
    	
		View back = findViewById(R.id.video_btn_back);
		if(back != null){	
			back.setOnClickListener(finishClickListener);
		}
		videoStartLayout = findViewById(R.id.video_recorder_start_layout);
	    videoSlecttypeLayout = findViewById(R.id.video_selecttype_layout);
	    videoLiveInputLayout = findViewById(R.id.video_live_input_layout);
	    
	    //input 信息设置
	    videoTitleView = (EditText) videoLiveInputLayout.findViewById(R.id.video_live_title);
	    videoTitleView.setText(generatorTitle());
	    videoRemarkView = (EditText) videoLiveInputLayout.findViewById(R.id.video_live_remark);
	    videoChannelName = (EditText) findViewById(R.id.video_live_channel);
		videoChannelId = (EditText) findViewById(R.id.video_live_channel_id);
		videoImageView = (ImageView) findViewById(R.id.video_live_image);
		videoImageView.setOnClickListener(controlClickListener);
		videoChannelName.setOnClickListener(controlClickListener);
		findViewById(R.id.video_live_channel_triangle).setOnClickListener(controlClickListener);
	    /*选择直播按钮*/
		View liveStart = findViewById(R.id.video_btn_live);
		liveStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				videoSlecttypeLayout.setVisibility(View.GONE);
				videoLiveInputLayout.setVisibility(View.VISIBLE);
				setdisplayAction(R.string.video_action_live_pre);
			}
		});
		
		
		View fileStart = findViewById(R.id.video_btn_file);
		fileStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showProgressDialog(R.string.video_saveing_file, true);
				String fileName = createLocalFile();
				if(StringUtils.isNull(fileName)){
					toastermsg("创建本地文件失败");
					return;
				}
				shareData.videoTitle=fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.length());
				hideVideoStartLayout();
				showVideoControlLayout();
				setdisplayAction(R.string.video_action_filing);
				shareData.isLive= false;
				shareData.playUrl=fileName;
				startRecording();
				//mshowDialog(R.string.video_nodevelop);
			}
		});
		
		
		View liveSave= findViewById(R.id.video_btn_live_start);
		liveSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkAndStartLive();
			}
		});
		video_live_ispublic_txt = (TextView) findViewById(R.id.video_live_ispublic_txt);
		ispublicSwitch = (WiperSwitch) findViewById(R.id.video_live_ispublic);
		ispublicSwitch.setChecked(ispublic);
		ispublicSwitch.setOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
				ispublic = checkState;
				int res = 0;
				if(checkState){
					res = R.string.video_live_public;
				}else{
					res = R.string.video_live_nopublic;
				}
				video_live_ispublic_txt.setText(res);
			}
		});
		
	}

    
    private String generatorTitle() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getNickName( AccountData.getInstance().getBindphonenumber()));
		//sb.append(DateUtil.getDateString(":"));
		sb.append("的直播");
		return sb.toString();
	}


	protected void checkAndStartLive() {
    	try{
    		String title = videoTitleView.getText().toString();
        	if("".equals(title.trim())){
        		mshowDialog("请输入视频标题");
        		return;
        	}
        	String remark = videoRemarkView.getText().toString();
        	showProgressDialog(R.string.video_saveing_live_, true);
        	shareData.isLive = true;
        	shareData.videoId = UUID.randomUUID().toString();
        	shareData.playUrl = netIF_ZBRJ.address_live+shareData.videoId;
        	shareData.videoTitle = title;
        	VideoStartRecorderData saveData = new VideoStartRecorderData();
        	saveData.videoID = shareData.videoId;
        	//FIXME 服务器没有实现
        	saveData.videoImage= bitmap2String();
        	saveData.playUrl=shareData.playUrl;
        	saveData.account = shareData.account;
        	saveData.label = remark.trim();
        	saveData.videoTitle = title;
        	saveData.isPublic=ispublic?1:0;
        	saveData.videoType= getChannelId();
        	if(compId != null && !"".equals(compId)){
        		saveData.isComp = 1;
        		saveData.compid = compId;
        	}
        	reportStartRecoding(saveData);        	
    	}catch(Exception e){
    		com.lb.common.util.Log.e(LOG_TAG, e);
    	}
	}

   
	private String bitmap2String() {
		if(videoBitmap == null||videoBitmap.isRecycled()){
			setDefaultVideoBitmap();
		}
		String bContent = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		videoBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		bContent = new String(Base64.encode(b));
		return bContent;
	}


	private void setdisplayAction(int strRes) {
		if(displayActionTextView == null)
			displayActionTextView = (TextView) findViewById(R.id.video_control_display_action);
		displayActionTextView.setText(strRes);
	}
	@Override
    protected void onPause() {
        super.onPause();
        
    }

	private void addImListener() {
		try {
			if(imlistener == null){
				imlistener = new VideoRecorderProtocolListener(this ,videoHandler);
				ImCore.getInstance().getConnection().addCustomProtocolListener(imlistener);
			}
		} catch (Exception e) {
			com.lb.common.util.Log.e(e.getMessage(), e);
		}
	}
   private void removeImListener(){
	   if(imlistener != null){
		   try {
			ImCore.getInstance().getConnection().removeCustomProtocolListener(imlistener);
			   imlistener = null;
		} catch (Exception e) {
			com.lb.common.util.Log.e(e.getMessage(), e);
		}
	   }
   }
    @Override
    protected void onDestroy() {
        try {
        	recording = false;
            if (mWakeLock != null) {
                mWakeLock.release();
                mWakeLock = null;
            }
            if(recording){
            	stopRecording();
            }
            
            
            sendIMStatus(SendIMLoopRunnable.donothing);
            stopLivePing();	
            stopDanmuquery(); 
            stopVideoNumberRunnable();
            
        	if(popwinWindow != null && popwinWindow.isShowing()){
        		popwinWindow.dismiss();
        	}
        	removeImListener();
			recording = false;
			if (cameraView != null) {
			    cameraView.stopPreview();
			}
			if (danmuView != null) {
			    danmuView.release();
			}
			
		} catch (Exception e) {
			com.danmu.comm.Log.e(LOG_TAG, e);
		}
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
	private boolean addCameraLayout() {
        /* get size of screen */
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        RelativeLayout.LayoutParams layoutParam = null;  
        
        /* add control button: start and stop */
        layoutParam = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
        
        Log.i(LOG_TAG, "cameara open");
        releaseCamera();
        cameraView = new CameraView(this);
        topLayout.removeAllViews();
        topLayout.addView(cameraView, layoutParam);
        boolean result = cameraView.changeCamera();
        boolean checkaudio = checkAudio();
       
        Log.i(LOG_TAG, "cameara preview start: OK");
        return result && checkaudio;
      
    }
    @SuppressWarnings("deprecation")
	private void initDanmu(){
    	danmuView = (DanmuSurfaceView)findViewById(R.id.video_danmaku);
        DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(DanmakuGlobalConfig.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(true);
    }
    	
    //---------------------------------------
    // initialize ffmpeg_recorder
    private void initRecorder(String ffmpeg_link) {

        Log.i("videoId", shareData.videoId);
        recorder = new FFmpegFrameRecorder(ffmpeg_link,previewWidth ,previewHeight,1);
        yuvIplimage = IplImage.create(previewWidth, previewHeight, opencv_core.IPL_DEPTH_8U, 2);;
        recorder.setFormat("flv");
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setSampleRate(sampleAudioRateInHz);
        recorder.setFrameRate(frameRate);
        Log.i(LOG_TAG, "recorder initialize success");
        audioRecordRunnable = new AudioRecordRunnable();
        audioThread = new Thread(audioRecordRunnable);
        runAudioThread = true;
    }

   
	private String createLocalFile() {
		String filePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + getPackageName();

		File ff = new File(filePath);
		if (!ff.exists()) {
			ff.mkdirs();
		}
		File file = new File(filePath + File.separator + createLocalFileName());
		file.deleteOnExit();
		Log.i(LOG_TAG, "local_fileName: " + file.getAbsolutePath());
		return file.getAbsolutePath();
	}
	private String createLocalFileName(){
		CharSequence timestamp = DateFormat.format("yyyyMMdd_HHmmss", System.currentTimeMillis());
		return "zbrj"+timestamp+".flv";
	}

	private void startRecording() {
		try {
			initRecorder(shareData.playUrl);
			video_control_title.setText(shareData.videoTitle);
			boolean started = false;
			int maxconnected = 20;
			int connected = 0;
			while(!started){
				try {
					recorder.start();
					started = true;
				} catch (Exception e) {
					Log.e(e.getMessage(),e);
					Thread.sleep(100);
					connected++;
					if(connected > maxconnected){
						mshowDialog("直播视频失败");
						return;
					}
				}
			}
			
			startTime = System.currentTimeMillis();
			recording = true;
			audioThread.start();
			hideVideoStartLayout();
			showVideoControlLayout();
			if(shareData.isLive){
				startDanmuQuery();
				danmuView.setStartTime(System.currentTimeMillis());
				danmuView.start(danmuView.getStartTime());
			}
			recording = true;
		} catch (Exception e) {
			com.lb.common.util.Log.e(LOG_TAG, e);
			setdisplayAction(R.string.video_action_live_pre);
			if(shareData.isLive)
				mshowDialog("直播视频失败");
			else {
				mshowDialog("录像失败");
			}
		}finally{
			hideProgressDialog();	
		}
	}


	private void showVideoControlLayout() {
		videoControlLayout.setVisibility(View.VISIBLE);
	}


	private void hideVideoStartLayout() {
    	if(videoStartLayout != null)
    		videoStartLayout.setVisibility(View.GONE);
	}
	private void startVideoNumberRunnable(){
		if(videoNumberQueryRunnable != null)
			videoNumberQueryRunnable.stop();
		videoNumberQueryRunnable = new VideoNumberQueryRunnable(this, false, shareData.videoId, videoHandler, EventVidoNumberChange);
		Thread thread = new Thread(videoNumberQueryRunnable);
		thread.start();
			
	}
	private void stopVideoNumberRunnable(){
		if(videoNumberQueryRunnable != null)
			videoNumberQueryRunnable.stop();
	}
	public void stopRecording() {
		if(recording == false)
			return;
        runAudioThread = false;
        recording = false;
        try {
        	stopLivePing();
			stopDanmuquery();
			audioRecordRunnable = null;
			audioThread = null;
			if (recorder != null ) {
			    Log.v(LOG_TAG,"Finishing recording, calling stop and release on recorder");
			    try {
			    	if(recorder != null){
				        recorder.stop();
				        recorder.release();
			    	}
			    } catch (FFmpegFrameRecorder.Exception e) {
			    }
			    recorder = null;
			}
			if(shareData.isLive){
				setdisplayAction(R.string.video_action_finish);
				stopLivePing();	
			    stopDanmuquery(); 
			    stopVideoNumberRunnable();
			    reportRecordEnd();
			}else{
				setdisplayAction(R.string.video_action_live_pre);
			}
			
		} catch (Exception e) {
			com.lb.common.util.Log.e(LOG_TAG, e);
		}
    }
	private boolean checkAudio(){
		try{
			int bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz, 
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
			new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioRateInHz, 
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
			return true;
		}catch(Exception e){
			com.lb.common.util.Log.e("麦克风没有权限", e.getMessage());
			return false;
		}
	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (recording) {
                popupSavewindow();
            }else{
            	myfinish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    //---------------------------------------------
    // audio thread, gets and encodes audio data
    //---------------------------------------------
    class AudioRecordRunnable implements Runnable {

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            // Audio
            int bufferSize;
            ShortBuffer audioData;
            int bufferReadResult;

            bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz, 
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioData = startAudioRecord(bufferSize);
            /* ffmpeg_audio encoding loop */
            while (runAudioThread ) {
                //Log.v(LOG_TAG,"recording? " + recording);
                bufferReadResult = audioRecord.read(audioData.array(), 0, audioData.capacity());
                if(bufferReadResult<0){
                	continue;
                }
                audioData.limit(bufferReadResult);
                if (bufferReadResult > 0) {
                    if (recording) {
                        if (RECORD_LENGTH <= 0)
                        	try {
                        		recorder.record(audioData);
                        } catch (Exception e) {
                            Log.v(LOG_TAG,e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.v(LOG_TAG,"AudioThread Finished, release audioRecord");
            /* encoding finish, release recorder */
            releaseAudioRecord();
        }

		

		private ShortBuffer startAudioRecord(int bufferSize) {
			ShortBuffer audioData;
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioRateInHz, 
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            audioData = ShortBuffer.allocate(bufferSize);
            Log.d(LOG_TAG, "audioRecord.startRecording()");
            audioRecord.startRecording();
			return audioData;
		}
    }

    private void releaseAudioRecord() {
    	runAudioThread= false;
		if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            Log.v(LOG_TAG,"audioRecord released");
        }
	}
    
  

	//---------------------------------------------
    // camera thread, gets and encodes video data
    //---------------------------------------------
    class CameraView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {

        private SurfaceHolder mHolder;
		
        @SuppressWarnings("deprecation")
		public CameraView(Context context) {
            super(context);
            mHolder = getHolder();
            mHolder.addCallback(CameraView.this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

       

		@Override
        public void surfaceCreated(SurfaceHolder holder) {
           /* try {
            	
            	if (cameraDevice == null)
					return;
            	cameraDevice.setPreviewCallbackWithBuffer(this);
            	stopPreview();
            	cameraDevice.setPreviewDisplay(holder);
            	cameraDevice.setPreviewCallback(this);
                Log.v(LOG_TAG,"surfaceCreated call");
            } catch (IOException exception) {
            }*/
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			try {
				if (cameraDevice == null)
					return;
				Log.v(LOG_TAG, "Setting imageWidth: " + imageWidth
						+ " imageHeight: " + imageHeight + " frameRate: "
						+ frameRate);
				Camera.Parameters camParams = cameraDevice.getParameters();
				camParams.setPreviewSize(previewWidth, previewHeight);
				Log.v(LOG_TAG,
						"Preview Framerate: " + camParams.getPreviewFrameRate());
				camParams.setPreviewFrameRate(frameRate);
				cameraDevice.setParameters(camParams);
				setAutoFocus();
				// FIXME add holder
				cameraDevice.setPreviewDisplay(holder);
				startPreview();
			} catch (Exception e) {
				Log.e(LOG_TAG, "change fail ", e);
			}
        }



		private void setAutoFocus() {
			//cameraDevice.cancelAutoFocus();
		}

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                mHolder.removeCallback(this);
                releaseCamera();
            } catch (RuntimeException e) {
               
            }
        }

        public void startPreview() {
            if (!isPreviewOn && cameraDevice != null) {
                isPreviewOn = true;
                cameraDevice.startPreview();
            }
        }

        public void stopPreview() {
            if (isPreviewOn && cameraDevice != null) {
                isPreviewOn = false;
                cameraDevice.stopPreview();
            }
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        	if(!recording){
        		startTime = System.currentTimeMillis();
        		if(recordingTime >0)
        			recordingTime = 0;
                return;
        	}
        	//transpose
        	
        	
        	
            /* get video data */
            if (yuvIplimage != null && recording) {
                yuvIplimage.getByteBuffer().put(data);
                try {
                	recordingTime = System.currentTimeMillis() - startTime;
                    long t = 1000 * recordingTime;
                    if (t > recorder.getTimestamp()) {
                        recorder.setTimestamp(t);
                    }
                    if(recordingTime -updateStatTime >1000){
                    	updateStatTime = recordingTime;
                    	videoHandler.removeMessages(EventUpdateStatTime);
                    	videoHandler.sendEmptyMessage(EventUpdateStatTime);
                    	shareData.recordingTime = recordingTime;
                    }
                	/*long l = timeStampEstimator.getSequenceTimeStamp();
                	recorder.setTimestamp(l);
                	timeStampEstimator.update();*/
                    recorder.record(yuvIplimage);
                } catch (Exception e) {
                    Log.e(e.getMessage(),e);
//                    toHandlerToast("视频录制错误，停止直播");
                    //stopRecording();
                }
            }
        }
        public boolean changeCamera(){
        	int cameraCount = Camera.getNumberOfCameras();
            if(cameraDevice != null && cameraCount<=0){
            	return false;
            }
        	if(cameraDevice != null){
        		stopPreview();
            	mHolder.removeCallback(this);
            	mHolder = null;
            	releaseCamera();
        	}else{
        		//设置成前端，后面切换成后端
        		cameraPosition = CameraInfo.CAMERA_FACING_FRONT;
        		//设置成后端，后面切换成前端
        		//cameraPosition = CameraInfo.CAMERA_FACING_BACK;
        	}
        	
        	mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            
        	try{
        		//无法判断，直接打开一个即可
        		if(cameraCount <=1){
        			cameraDevice = Camera.open();
        		}else{
		    		
	                if(cameraPosition == CameraInfo.CAMERA_FACING_BACK) {
	                   cameraDevice = Camera.open(CameraInfo.CAMERA_FACING_FRONT);//打开当前选中的摄像头
	                   cameraPosition = CameraInfo.CAMERA_FACING_FRONT;
	                }else if(cameraPosition == CameraInfo.CAMERA_FACING_FRONT){
	                	cameraDevice = Camera.open(CameraInfo.CAMERA_FACING_BACK);//打开当前选中的摄像头
	                    cameraPosition = CameraInfo.CAMERA_FACING_BACK;
	                }

        		}
                 Camera.Parameters camParams = cameraDevice.getParameters();
                 List<Camera.Size> sizes = camParams.getSupportedPreviewSizes();
                 
                 Size size = getOptimalPreviewSize(sizes,imageWidth , imageHeight);
                 previewWidth = size.width;
                 previewHeight = size.height;
                 Log.v(LOG_TAG,"Setting previewWidth: " + previewWidth + " previewHeight: " + previewHeight + " frameRate: " + frameRate);
                
                 cameraDevice.setPreviewCallbackWithBuffer(this);
                 camParams.setPreviewSize(previewWidth,previewHeight);
                 camParams.setPreviewFrameRate(frameRate);
                 //设置自动对焦功能
                 List<String> focusModes = camParams.getSupportedFocusModes();
                 if(focusModes != null && focusModes.size()>0){
	                 if( focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
	                	 camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
	                 }else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
	                	 camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
	                 }else{
	                	 camParams.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
	                 }
	                 
                 }
                 cameraDevice.setParameters(camParams);
                 cameraDevice.setPreviewDisplay(mHolder);
                 cameraDevice.setPreviewCallback(this);    
                 cameraDevice.startPreview();
                 setAutoFocus();
                 return true;
         	}catch(Exception e){
         		canRecord = false;
         		mshowDialog("打开摄像头失败");
         		Log.e(LOG_TAG, "change fail ",e);
         		return false;
         	}
        }
        private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) w / h;
            if (sizes == null) return null;

            Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            // Try to find an size match aspect ratio and size
            for (Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }
    }

 	private void startDanmuQuery() {
		danmuQueryRunnable = new DanmuQueryRunnable(netIF_ZBRJ , danmuView  , shareData.videoId,danmuListAdapter);
		Thread danmuQueryThread = new Thread(danmuQueryRunnable);
		danmuQueryThread.start();
	}

 	private void startLivePing(){
 		stopLivePing();
 		try{
 			videoLivePingRunnable = new VideoLivePingRunnable(this, netIF_ZBRJ, shareData.videoId, videoHandler);
 			(new Thread(videoLivePingRunnable)).start();
 		}catch(Exception e){
 			com.lb.common.util.Log.e(Constants.LOG_TAG, e);
 		}
 	}
 	private void stopLivePing(){
 		if(videoLivePingRunnable != null){
 			videoLivePingRunnable.stop();
 			videoLivePingRunnable = null;
 		}
 	}
	private void stopDanmuquery() {
		try {
			if (danmuQueryRunnable != null){
				danmuQueryRunnable.stop();
				danmuQueryRunnable = null;
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, e);
		}
    	
	}
    private void reportStartRecoding(final VideoStartRecorderData saveData){
    	Runnable runnable = new Runnable() {		
			@Override
			public void run() {
				final BaiduLocation location =  BaiduLocation.getInstance();
				try{
					location.startLocationListener(null, new BaiduLocationLister() {
						@Override
						public void baiduLocFinish(final String latitude, final String longitude,
								String address, String coorTypr) {
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									VideoStartRecorderData data = saveData;
									try{
							    	data.locationX = Double.parseDouble(latitude);
							    	data.locationY = Double.parseDouble(longitude);
									}catch(Exception e){}
							    	data.id = netIF_ZBRJ.getRandomNumber()+"";
							    	shareData.videodata = data;
							    	CommResData result = netIF_ZBRJ.m1_upload_viewInfo_live(data); 
							    	com.lb.common.util.Log.i("startrecoding", result.status+"----"+result.desc);	
							    	if(result != null && Constants.RES_SUCCESS.equals(result.status)){
							    		videoHandler.sendEmptyMessage(EventSaveLiveOk);
							    	}else{
							    		videoHandler.sendEmptyMessage(EventSaveLiveFail);
							    	}
								}
							}).start();
						}
					});
				}catch(Exception e){
					com.lb.common.util.Log.e(LOG_TAG, "获取位置失败",e);
					videoHandler.sendEmptyMessage(EventSaveLiveFail);
				}
			}
		};
    	Thread thread = new Thread(runnable);
    	thread.start();
    }
    
    private Builder mydialog;
    private void mshowDialog(int resId){
    	if(mydialog == null){
    		mydialog = new AlertDialog.Builder(this);
    		mydialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
    	}
    	mydialog.setMessage(resId);
    	mydialog.show();
    }
    private void mshowDialog(String s ){
    	if(mydialog == null){
    		mydialog = new AlertDialog.Builder(this);
    		mydialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
    	}
    	mydialog.setMessage(s);
    	mydialog.show();
    }
    
    OnClickListener finishClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			popupSavewindow();
		}
	};

	private void setVideoStatTime(long time){
			time = time/1000;
			long min = time/60;
			long sec = time%60;
			video_stat_time.setText(min+":"+(sec>9?sec:"0"+sec));
	}
	private void setVideoStatNum(String watchersNum , String bullettNum ,String upNum){
		video_stat_watchersNum.setText(watchersNum);
		video_stat_bulletsNum.setText(bullettNum);
		video_stat_upNum.setText(upNum);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (data != null) {
				switch (requestCode) {
				case Constants.CAMERA_RESULT_CODE:
					if (tempb != null && !tempb.isRecycled())
						tempb.recycle();
					String filePath = CameraGalleryWithClearChoiceDialog.getFilePath();
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
					break;
				case Constants.REQ_AREA:
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
			cameraView.changeCamera();
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
			if (videoBitmap != null && !videoBitmap.isRecycled()) {
				videoBitmap.recycle();
			}
			videoBitmap = compressImage(tempb);
			videoImageView.setImageBitmap(videoBitmap);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			toastermsg(R.string.read_photo_fail);
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

    class VideoHandler extends WeakHandler<RecordActivity>{
    	RecordActivity recordActivity;
		public VideoHandler(RecordActivity owner) {
			super(owner);
			this.recordActivity = owner;
		}
		@Override
		public void handleMessage(Message msg) {
			try {
				Bundle data = msg.getData();
				switch (msg.what) {
				case EventNoPremiss:
					recordActivity.showDialog(R.string.video_no_permission);
					sendEmptyMessageDelayed(EventFinish, 2000);
					break;
				case EventFinish:
					if(popwinWindow != null && popwinWindow.isShowing()){
						popwinWindow.dismiss();
					}
					recordActivity.myfinish();
					break;
				case EventSaveLiveOk:
					reloadVideoDataImageUrl();
					shareData.startTime = System.currentTimeMillis();
					shareData.recordingTime=0;
					recordActivity.setdisplayAction(R.string.video_action_living);
					recordActivity.startRecording();
					popupUpFlag = true;
					startLivePing();
					startVideoNumberRunnable();
					sendIMStatus(SendIMLoopRunnable.start_recorder);
					break;
				case EventSaveLiveFail:
					hideProgressDialog();
					recordActivity.setdisplayAction(R.string.video_action_live_pre);
					mshowDialog(R.string.video_start_file_fail);
					break;
				case EventRequestJoinLive:
					String videoID = data.getString("videoID");
					String nick = data.getString("nick");
					String account = data.getString("account");
					if(!shareData.videoId.equals(data.getString("videoID")))
						return;
					AdapterData adData =new AdapterData();
					adData.account = account;
					adData.nick = nick;
					adData.videoID = videoID;
					adData.videoTitle = shareData.videoTitle;
					adData.msg = nick +"请求加入 "+adData.videoTitle+"，是否同意";
					// 此功能暂时屏蔽掉，没用了
					break;
				case EventVidoNumberChange:
					recordActivity.setVideoStatNum(data.getString("watchersNum")+"("+data.getString("friendNum")+")", data.getString("bulletsNum"), data.getString("upNum"));
					break;
				case EventLiveNetError:
					break;
				case EventLivePingError:
					toastermsg("网络中断，直播结束");
					break;
				case EventToastMsg:
					String s = data.getString("msg");
					if(s != null)
						toastermsg(s);
					break;
				case EventUpdateStatTime:
					video_stat_time.setText(Strings.millisToString(updateStatTime));
					break;
				case EventReloadVideoOk:
					shareMenuUtil = ShareMenuUtil.getInstance(RecordActivity.this);
					String url = netIF_ZBRJ.address_share+"?name="+URLEncoder.encode(shareData.nick)+"&videoid="+shareData.videoId;
					shareMenuUtil.initBottomPopupWindow(videoControlLayout, getResources().getString(R.string.share),shareData.videoTitle, shareData.nick, "briefStr", shareData.nick, url,shareData.videoImage, true, ContactMsgCenterActivity.LAUNCH_MODE_LINKMSG);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				com.lb.common.util.Log.e(e.getMessage(), e);
			}
		}
    	
    }
    private OnClickListener controlClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			try{
				switch (view.getId()) {
				case R.id.video_btn_control_misc:
					micOn = !micOn;
					StringBuilder sb = new StringBuilder();
					if(micOn){
						sb.append(getResources().getString(R.string.turnon));
						video_btn_control_misc.setSelected(false);
					}else{
						video_btn_control_misc.setSelected(true);
						sb.append(getResources().getString(R.string.turnoff));
					}
					AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
					audioManager.setMicrophoneMute(!micOn);
					audioManager.setStreamMute(MediaRecorder.AudioSource.MIC, !micOn);
					//recorder.recorderMIC = miscOn;
					sb.append(getResources().getString(R.string.video_control_micro));
					sb.append(getResources().getString(R.string.success));
					toastermsg(sb.toString());
					break;
				case R.id.video_btn_list_danmu:
					video_danmu_list_layout.setVisibility(View.VISIBLE);
					displayOptionLayout(-1);
					
					break;
				case R.id.video_btn_close_danmu:
					int resid = 0;
					//FIXME 暂时屏蔽
					/*if(recording == false)
						return;*/
					if(danmuQueryRunnable != null){
						stopDanmuquery();
						danmuView.hide();
				        
						resid = R.string.turnon;
						
					}else{
						startDanmuQuery();
						danmuView.show();
						resid = R.string.turnoff;
					}
					toastermsg(video_btn_close_danmu.getText()+"成功");
					video_btn_close_danmu.setText(getResources().getString(resid)+"弹幕");
					
					break;
				case R.id.video_btn_option_danmu:
					video_danmu_option_layout.setVisibility(View.GONE);
					danmuoptionLayout.setVisibility(View.VISIBLE);
					break;
				case R.id.video_btn_forbidden_danmu:
					{
						String forbidden = getResources().getString(R.string.video_forbidden_danmu);
						String allow = getResources().getString(R.string.video_allow_danmu);
						String type =null;
						if(btn_forbidden_danmu.getText().equals(forbidden)){
							type="0";
						}else{
							type = "1";
						}
						NetAsyncTask task = new NetAsyncTask(RecordActivity.this, R.id.video_btn_forbidden_danmu,type,btn_forbidden_danmu.getText().toString());
						AppUtil.execAsyncTask(task);
					}
					break;
					
				case R.id.video_btn_invite_random:
					 invite_video();
					 break;
				case R.id.video_btn_invite_other:
					//委托观众
					entrust_invite_video();
					break;
				case R.id.video_btn_test:
					String a="13681387004";
					//FIXME 测试类方法,按钮已经屏蔽
					CustomProtocolDealerManager cdm = ImCore.getInstance().getCustomProtocolDealerManager();
					/*cdm.createDealer("18610676953").kick_off_video(shareData.videoId, "测试提出");*/
					//CustomProtocolDealerManager cdm = ImCore.getInstance().getCustomProtocolDealerManager();
					//请求加入
//					cdm.createDealer(a).request_join_live(shareData.videoId);
					/*私聊*/
					cdm.createDealer(a).private_bullet("第二次发送", shareData.videoId);
					/*踢出*/
					//cdm.createDealer(a).kick_off_video(shareData.videoId, shareData.videoTitle);
					/*禁言*/
					//cdm.createDealer(a).mute_video(shareData.videoId, shareData.videoTitle);
					/*禁止弹幕*/
					//cdm.createDealer(a).forbid_bullet(shareData.videoId);
					//sendIMStatus(SendIMLoopRunnable.start_watch_live);
					popupSavewindow();
					break;
				case R.id.video_live_channel_triangle:
				case R.id.video_live_channel:
					openChannelDialog();
					break;
				case R.id.video_live_image:
					openImageSelecter();
					break;
				case R.id.video_control_share:
					if(shareMenuUtil == null){
						toastermsg("未开始直播，无法分享");
						return;
					}
					shareMenuUtil.showShareMenu();
					break;
				}
//				displayOptionLayout(-1);
			}catch(Exception e){
				Log.e(e.getMessage(), e);
			}
		}
	};
	private RelativeLayout topLayout;
	private void invite_video(){
		if(invite_video_done){
			toHandlerToast("不能多次邀请") ;
			return;
		}
		invite_video_done = true;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					sendIMStatus(SendIMLoopRunnable.invite_video);
					toHandlerToast("邀请成功") ;
				}catch(Exception e){
					com.lb.common.util.Log.e(e.getMessage(), e);
					invite_video_done = false;
					toHandlerToast("邀请失败") ;
				}
			}
		});
		thread.start();
	}
	private void toHandlerToast(String s){
		Message msg = new Message();
		msg.what = EventToastMsg;
		Bundle data = new Bundle();
		data.putString("msg", s);
		msg.setData(data);
		videoHandler.sendMessage(msg);
	}
	protected void openImageSelecter() {
		CameraGalleryWithClearChoiceDialog dialog = new CameraGalleryWithClearChoiceDialog(RecordActivity.this);
		releaseCamera();
		dialog.addOnChoiceClickListener(new OnChoiceClickListener() {

			@Override
			public void onClicked(int which) {
				if (2 == which) {
					if (videoBitmap != null && !videoBitmap.isRecycled())
						videoBitmap.recycle();
					setDefaultVideoBitmap();
					videoImageView.setImageResource(R.drawable.video_image);
					cameraView.changeCamera();
				}
			}

		});
		dialog.addOnCancelClick(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				cameraView.changeCamera();
			}
		});
		dialog.showAgain();
		
	}


	private void releaseCamera() {
		try {
			if(cameraDevice != null) {
				cameraView.stopPreview();
				cameraDevice.setPreviewCallback(null);
			   cameraDevice.release();
			   cameraDevice = null;
			}
		} catch (Exception e) {
			toastToMessage("调用摄像头失败");
		}
	}


	protected void openChannelDialog() {
		final List<ChannelData> channelList = new ArrayList<ChannelData>();
		try {
			ChannelHelper channelHelper = new ChannelHelper(AccountData.getInstance().getUsername());
			channelList.addAll(channelHelper.findAll());
		} catch (Exception e) {
			com.lb.common.util.Log.e(e.getMessage(), e);
		}
		if(channelList.size()==0){
			toastermsg("没有频道可以选择");
			return;
		}
		String[] nameStrings = new String[channelList.size()];
		for(int i=0; i<channelList.size() ;i++){
			nameStrings[i] = channelList.get(i).name;
		}
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 builder.setTitle("请选择频道");
		 builder.setItems(nameStrings,
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(final DialogInterface dialog, final int item) {
	                      ChannelData channel =  channelList.get(item);
	                      setChannel(channel);
	                    }
	                });
	        builder.create().show();
	}
	private void setChannel(ChannelData channel){
		videoChannelName.setText(channel.name);
		videoChannelId.setText(channel.id+"");
	}
	private int getChannelId(){
		int result = 0;
		try{
			result = Integer.parseInt(videoChannelId.getText().toString());
		}catch(Exception e){
			com.lb.common.util.Log.e("get channelId", e);
		}
		return result;
	}
	private void entrust_invite_video(){
		if(entrust_invite_video){
			toHandlerToast("不能多次邀请") ;
			return;
		}
		entrust_invite_video = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					NetInterfaceStatusDataStruct data = netIF_ZBRJ.m1_get_fans(AccountData.getInstance().getBindphonenumber(), 0, 10);
					ArrayList<FansData> list = null;
					if(!Constants.RES_SUCCESS.equals(data.getStatus())){
						toHandlerToast("获取账户失败") ;
						return;
					}
					list = (ArrayList<FansData>) data.getObj();
					if(list.size() == 0){
						toHandlerToast("获取账户失败") ;
						return ;
					}
					
					List<String> personsList = new ArrayList<String>();
					for(FansData fan:list){
						personsList.add(fan.account);
					}
					if(personsList.size() == 0){
						toastermsg("没有找到观看人，无法操作");
						return;
					}
					SIXmppMessage msg = null;
					CustomProtocolDealerManager cdm = ImCore.getInstance().getCustomProtocolDealerManager();
					for(String account :personsList){
						msg = cdm.createDealer(account).entrust_invite_video(shareData.videoId);
					}
					toHandlerToast( "邀请成功");
				}catch(Exception e){
					com.lb.common.util.Log.e(e.getMessage(), e);
					entrust_invite_video = false;
					toHandlerToast( "请求失败");
				}
				
			}
		}).start();
	}
	public void toastermsg(String msg){
		Util.toaster(RecordActivity.this , msg);
	}
	public void toastermsg(int resid){
		Util.toaster(RecordActivity.this , resid);
	}
	
	public void sendIMStatus(final int type){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					NetInterfaceStatusDataStruct data = netIF_ZBRJ.m1_get_fans(AccountData.getInstance().getBindphonenumber(), -1, 100);
					ArrayList<FansData> list = null;
					if(!Constants.RES_SUCCESS.equals(data.getStatus())){
						//toHandlerToast("获取粉丝失败") ;
						return;
					}
					list = (ArrayList<FansData>) data.getObj();
					if(list.size() == 0){
						//toHandlerToast("没有粉丝，无法邀请") ;
						return ;
					}
					List<String> listaccount = new java.util.ArrayList<String>();
					for(FansData fan : list){
						listaccount.add(fan.account);
					}
					list = null;
					VideoData videoData = convert2VideoData();
					MyApplication.getInstance().imTaskPool.submit(new SendIMLoopRunnable(type, videoData, listaccount));
					
				}catch(Exception e){
					com.danmu.comm.Log.e(e.getMessage(), e);
				}
			}
		}).start();
	}

	
	
	private VideoData convert2VideoData(){
		VideoData result = new VideoData();
		
		result.videoID = shareData.videoId;
		result.title = shareData.videoTitle;
		result.playUrl = shareData.playUrl;
		result.nick = shareData.nick;
		if(shareData.videodata != null){
			result.locationX = shareData.videodata.locationX;
			result.locationY = shareData.videodata.locationY;
		}
		result.dateTime = DateUtil.getFullDateTimeString(":", "-");
		return result;
	}
	
	private void popupSavewindow(){
		if(popupUpFlag == false ){
			myfinish();
		}
		/*stopRecording();*/
		View popupView = LayoutInflater.from(this).inflate(R.layout.pop_nologo_confirm_window, null);
		popwinWindow = new PopupWindow(popupView,  LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		View btn_yes = popupView.findViewById(R.id.btn_yes);
		View btn_no	= popupView.findViewById(R.id.btn_no);
		View btn_cancel = popupView.findViewById(R.id.btn_cancel);
		btn_no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				stopRecording();
				NetAsyncTask task = new NetAsyncTask(RecordActivity.this, taskAction_cancel);
				AppUtil.execAsyncTask(task);
			}
		});
		btn_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				stopRecording();
				popwinWindow.dismiss();
				myfinish();
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popwinWindow.dismiss();
			}
		});
		popwinWindow.showAtLocation(videoControlLayout, Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL,0,0);
		popwinWindow.update();
	}
	class NetAsyncTask extends BaseNetAsyncTask{
		private Context context;
		private int action;
		private String ovalue,actionName;
		public NetAsyncTask(Context context ,int action) {
			super(context);
			this.context = context;
			this.action = action;
		}
		public NetAsyncTask(Context context ,int action,String ovalue,String actionName) {
			super(context);
			this.context = context;
			this.action = action;
			this.ovalue = ovalue;
			this.actionName = actionName;
		}
		@Override
		public NetInterfaceStatusDataStruct doNet() {
			
			try {
				switch (action) {
				case R.id.video_btn_forbidden_danmu:
					return ni.m1_forbid_bullet(shareData.videoId ,ovalue);
				case taskAction_cancel:
					return ni.m1_cancel_save(shareData.videoId);
				}
				
			} catch (Exception e) {
				com.lb.common.util.Log.e(e.getMessage(), e);
			}
			return null;
		}

		@Override
		public void afterNet(NetInterfaceStatusDataStruct result) {
			if(action == R.id.video_btn_forbidden_danmu){
				if(actionName == null){
					actionName = "禁止弹幕";
				}
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					toastermsg( actionName+"成功");
					if("0".equals(ovalue)){
						btn_forbidden_danmu.setText(R.string.video_allow_danmu);
					}else{
						btn_forbidden_danmu.setText(R.string.video_forbidden_danmu);
					}
				}else{
					toastermsg( actionName+"失败");
				}
			}else if(action == taskAction_cancel){
				String s = getResources().getString(R.string.video_cancel_save);
				if(Constants.RES_SUCCESS.equals(result.getStatus())){
					toastermsg(s+getResources().getString(R.string.success)+" 稍候退出");
					videoHandler.sendEmptyMessageDelayed(EventFinish, 2000);
				}else{
					toastermsg(s+getResources().getString(R.string.fail));
				}
			}
			
		}
		
	}
	
	public void reportRecordEnd(){
    	try{
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					VideoData videoData = new VideoData();
					videoData.videoID = shareData.videoId;
					netIF_ZBRJ.m1_upload_viewInfo("0", videoData);
				}
			});
			thread.start();
		}catch(Exception e){
			Log.e("上报直播记录失败" ,e);
		}
    }


	private void setDefaultVideoBitmap() {
		videoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.video_image);
	}
	
	private final TouchEvent touchEvent = new TouchEvent(R.id.video_control_layout,
			new TouchEvent.OnEventListener() {
				//点击
				@Override
				public void onEvent() {
					try{
						if(hideSubMenu()){
							return;
						}
						if(videoBtnsLayout.getVisibility() == View.VISIBLE){
							videoTitleLayout.setVisibility(View.INVISIBLE);
							videoBtnsLayout.setVisibility(View.INVISIBLE);
							//videoStatLayout.setVisibility(View.INVISIBLE);
							danmuoptionLayout.setVisibility(View.GONE);
							video_danmu_option_layout.setVisibility(View.GONE);
							displayOptionLayout(0);
						}else{
							//videoStatLayout.setVisibility(View.VISIBLE);
							videoTitleLayout.setVisibility(View.VISIBLE);
							videoBtnsLayout.setVisibility(View.VISIBLE);
						}
					}catch(Exception e){
						com.danmu.comm.Log.e(LOG_TAG, e);
					}
				}
			},
			new TouchEvent.OnEventListener() {
				//放大
				@Override
				public void onEvent() {
					changeCameraFocus(true);
				}
			},
			new TouchEvent.OnEventListener() {
				//缩小
				@Override
				public void onEvent() {
					changeCameraFocus(false);
					
				}
			}
			);
	
	
	private boolean hideSubMenu(){
		if(danmuoptionLayout.getVisibility() == View.VISIBLE
				||video_danmu_option_layout.getVisibility() == View.VISIBLE){
			danmuoptionLayout.setVisibility(View.GONE);
			video_danmu_option_layout.setVisibility(View.GONE);
			return true;
		}
		return false;
	}
	private void changeCameraFocus(boolean isBigger){
		int scale = 20;
		if(cameraDevice == null)
			return;
		Camera.Parameters camParams =cameraDevice.getParameters();
		camParams.getMaxZoom();
		if(camParams.isZoomSupported() == false){
			toastermsg(R.string.video_zoomSupported);
			touchEvent.canZoom = false;
			return;
		}
		int zoom = camParams.getMaxZoom();
		int nowzoom = camParams.getZoom();
		Log.d("cameraZoom", "当前zoom"+nowzoom);
		if(isBigger){
			if(zoom == nowzoom)
				return;
			nowzoom = nowzoom +(int)(zoom/scale);
			if(nowzoom>zoom){
				nowzoom = zoom;
			}
		}else{
			if(nowzoom == 0)
				return;
			nowzoom = nowzoom -(int)(zoom/scale);
			if(nowzoom<0)
				nowzoom = 0;
		}
		Log.d("cameraZoom", "设置的zoom"+nowzoom);
		camParams.setZoom(nowzoom);
		cameraDevice.setParameters(camParams);
	}
	private void myfinish(){
		releaseAudioRecord();
		releaseCamera();
		if(popwinWindow != null && popwinWindow.isShowing()){
    		popwinWindow.dismiss();
    	}
    	removeImListener();
		recording = false;
		if (cameraView != null) {
		    cameraView.stopPreview();
		}
		if (danmuView != null) {
		    danmuView.release();
		}
		oldfinish();
//		overridePendingTransition(android.R.anim.fade_in , android.R.anim.fade_out);
	}
}
