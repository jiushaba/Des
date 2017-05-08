package com.lb.video.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lb.common.util.Log;
import com.lb.video.activity.RecordActivity;
import com.lb.video.adapter.data.AdapterData;
import com.xuanbo.xuan.R;

public class RequestListAdapter extends BaseAdapter implements Runnable{
	//显示时间，4秒后删除
	private final int StayTime = 4000;
	MtouchEvent mtouchEvent ;
	List<AdapterData> datas = new ArrayList<AdapterData>();
	//List<AdapterData> checkDatas = new ArrayList<AdapterData>();
	private Thread checkThread;
	private Context context;
	private LayoutInflater mInflater;
	private Handler handler;
	private boolean threadRunFlag = false;
	public RequestListAdapter(Context context , Handler handler , MtouchEvent touchEvent) {
		this.mtouchEvent = touchEvent;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.handler = handler;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		if(datas.size()>position)
			return datas.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		AdapterData data = (AdapterData) getItem(position);
		if(data == null)
			return -1;
		return data.id;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		final AdapterData data = (AdapterData) getItem(position);
		if(data == null)
			return null;
		data.startTime = Calendar.getInstance().getTimeInMillis();
		
		if(view == null){
			view = mInflater.inflate(R.layout.video_request_item, null);
		}
		final TextView txt = (TextView) view.findViewById(R.id.request_msg_txt);
		txt.setText(data.msg);
		txt.setBackgroundColor(Color.TRANSPARENT);
		view.setOnTouchListener(new OnTouchListener() {
			private float x; 
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					x = event.getX();
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					float nx = event.getX();
					//向右滑动
					if(nx <x){
						mtouchEvent.Accept(data);
					}else{//向左滑动
						mtouchEvent.reject(data);
					}
				}
				if(event.getAction() == MotionEvent.ACTION_MOVE){
					float nx = event.getX();
					if(nx<x){
						txt.setBackgroundColor(Color.GREEN);
						txt.setText(data.msg+" 同意");
					}else{//向右滑动
						txt.setBackgroundColor(Color.RED);
						txt.setText(data.msg+" 拒绝");
					}
				}
				return true;
			}
		});
		return view;
	}
	public void addData(AdapterData data){
		synchronized (this) {
			this.datas.add(data);
			data.startTime = Calendar.getInstance().getTimeInMillis();
			startThread();
		}
	}
	public void removeData(AdapterData data){
		synchronized (this) {
			datas.remove(data);
			if(datas.isEmpty()){
				
				handler.sendEmptyMessage(RecordActivity.EventRequestJoinLiveEmpty);
			}
		}
	}
	
	public interface MtouchEvent{
		public void Accept(AdapterData data);
		public void reject(AdapterData data);
	}
	private void startThread(){
		try {
			if(checkThread == null){
				checkThread = new Thread(this);
				checkThread.start();
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
		TimerTask task = null;
	}
	@Override
	public void run() {
		try{	
		while(datas.size()>0){
			long time = Calendar.getInstance().getTimeInMillis();
			for(int i=0; i<datas.size();i++){
				AdapterData data = datas.get(i);
				if(time - data.startTime >StayTime){
					removeData(data);
					notifyDataSetChanged();
				}
			}
			Thread.sleep(1000);
		}
		}catch(Exception e){
			Log.e(e.getMessage(), e);
		}
		checkThread= null;
	}
}

