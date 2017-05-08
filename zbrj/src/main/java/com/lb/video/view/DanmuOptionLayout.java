package com.lb.video.view;

import master.flame.danmaku.danmaku.model.AlphaValue;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;

import com.danmu.widget.DanmuSurfaceView;
import com.lb.video.activity.VideoPlayerActivity;
import com.xuanbo.xuan.R;
import com.lz.oncon.adapter.ViewPageAdapter;
import com.umeng.socialize.net.r;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class DanmuOptionLayout extends LinearLayout {
	private Context context;
	private DanmuSurfaceView danmuSurfaceView;
	private SeekBar numBar,transBar;
	private TextView numText,transText;
	private View btnLook,btnSend;
	private View[] fontViews = new View[3];
	private View[] speedViews = new View[3];
	private View[] locationViews = new View[3];
	public boolean isBozhu = false;
	private int[] lookIds = new int[]{R.id.danmakuNum_layout,R.id.video_num_bar,
									  R.id.tranport_layout ,R.id.video_tranport_bar};
	private int[] sendIds = new int[]{R.id.font_title,R.id.font_layout,
									  R.id.location_title,R.id.location_layout};
	private View parentView;
	//标记选择了哪个按钮，发送或接收
	private boolean selectLookFlag = true;
	public DanmuOptionLayout(Context context) {
		super(context);
		init(context);
	}

	public DanmuOptionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	@SuppressLint("NewApi")
	public DanmuOptionLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context){
		this.context = context;
		 parentView = LayoutInflater.from(context).inflate(R.layout.video_danmu_option1, this);
		 btnLook = parentView.findViewById(R.id.btn_look);
		 btnSend = parentView.findViewById(R.id.btn_send);
		 btnLook.setOnClickListener(switchOnClickListener);
		 btnSend.setOnClickListener(switchOnClickListener);
		 fontViews[0] = parentView.findViewById(R.id.btn_font_lit);
		 fontViews[1] = parentView.findViewById(R.id.btn_font_normal);
		 fontViews[2] = parentView.findViewById(R.id.btn_font_big);
		 for(View v :fontViews){
			v.setOnClickListener(fontClickListener);
		 }
		 fontViews[1].setSelected(true);
		 
		 speedViews[0] = parentView.findViewById(R.id.btn_speed_fast);
		 speedViews[1] = parentView.findViewById(R.id.btn_speed_normal);
		 speedViews[2] = parentView.findViewById(R.id.btn_speed_slow);
		 for(View v :speedViews){
				v.setOnClickListener(speedClickListener);
		 }
		 speedViews[1].setSelected(true);
		 
		 locationViews[0] = parentView.findViewById(R.id.btn_location_up);
		 locationViews[1] = parentView.findViewById(R.id.btn_location_mid);
		 locationViews[2] = parentView.findViewById(R.id.btn_location_down);
		 for(View v :locationViews){
				v.setOnClickListener(locationClickListener);
		 }
		 locationViews[1].setSelected(true);
		 
		 numBar = (SeekBar) parentView.findViewById(R.id.video_num_bar);
		 transBar = (SeekBar) parentView.findViewById(R.id.video_tranport_bar);
		 numText = (TextView) parentView.findViewById(R.id.video_num);
		 transText = (TextView) parentView.findViewById(R.id.video_tranport);
		 numBar.setOnSeekBarChangeListener(mSeekListener);
		 transBar.setOnSeekBarChangeListener(mSeekListener);
		 
		 findViewById(R.id.btn_close).setOnClickListener(closeonClickListener);
	}
	private OnClickListener closeonClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			parentView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
			parentView.setVisibility(View.INVISIBLE);
		}
	};
	private OnClickListener switchOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			View selectView = null;
			View hideView = null;
			int[] displayId = null;
			int[] hideId = null;
			if (view.getId() == R.id.btn_look) {
				selectView = btnLook;
				hideView = btnSend;
				displayId = lookIds;
				hideId = sendIds;
				selectLookFlag = true;
			} else {
				hideView = btnLook;
				selectView = btnSend;
				hideId = lookIds;
				displayId = sendIds;
				selectLookFlag = false;
			}
			selectView.setBackgroundResource(R.color.white);
			hideView.setBackgroundResource(R.color.gray2);
			for(int id:hideId){
				findViewById(id).setVisibility(View.GONE);
			}
			for(int id:displayId){
				findViewById(id).setVisibility(View.VISIBLE);
			}
			
		}
	};
	OnClickListener fontClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int length = fontViews.length;
			 for(int i= 0 ; i < length;i++){
				if(view.getId() == fontViews[i].getId()){
					view.setSelected(true);
					danmuSurfaceView.fontSizeLevel = i;
				}else{
					fontViews[i].setSelected(false);
				}
			}
		}
	};
	OnClickListener speedClickListener= new OnClickListener() {
		@Override
		public void onClick(View view) {
			int length = speedViews.length;
			for (int i = 0; i < length; i++) {
				if (view.getId() == speedViews[i].getId()) {
					view.setSelected(true);
					if(selectLookFlag){
						danmuSurfaceView.lookSpeedLevel = i;
					}else{
						danmuSurfaceView.speedLevel = i;
					}
					
				} else {
					speedViews[i].setSelected(false);
				}
			}
		}
	};
	OnClickListener locationClickListener= new OnClickListener() {
		@Override
		public void onClick(View view) {
			int length = locationViews.length;
			 for(int i= 0 ; i < length;i++){
				if(view.getId() == locationViews[i].getId()){
					view.setSelected(true);
					danmuSurfaceView.locationLevel = i;
				}else{
					locationViews[i].setSelected(false);
				}
			}
		}
	};
	private  OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar bar,int progress, boolean fromUser) {
			if(fromUser == false)
				return;
			int id = bar.getId();
			switch (id) {
			case R.id.video_num_bar:
				changeDanmuNum(progress);	
				break;
			case R.id.video_tranport_bar:
				changeDanmuTranport(progress);
				break;
			default:
				break;
			}
		}
		@Override
		public void onStartTrackingTouch(SeekBar bar) {
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar bar) {
		}
		
	};
	
	
	public void setDanmuSurfaceView(DanmuSurfaceView danmuSurfaceView) {
		this.danmuSurfaceView = danmuSurfaceView;
	}

	private void changeDanmuTranport(int progress) {
		if(danmuSurfaceView == null)
			return;
		if(progress == 0)
			progress = 1;
		//FIXME 暂时不控制
		//transText.setText(getResources().getString(R.string.video_danmu_tranport_value , progress));
		danmuSurfaceView.changeAlpha(progress/ 100f);
	}


	private void changeDanmuNum(int progress) {
		if(progress == 0){
			numText.setText("自动");
		}else{
			numText.setText(getResources().getString(R.string.video_danmu_unit_value, progress));
		}
		
		if(danmuSurfaceView == null){
			return;
		}
		danmuSurfaceView.changeInScreenSize(progress);
	}
	
	public void setSendEnable(boolean flag){
		if(flag)
			btnSend.setOnClickListener(switchOnClickListener);
		else {
			btnSend.setOnClickListener(null);
		}
	}
}
