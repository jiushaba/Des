package com.lb.video.job;

import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.danmu.data.DanmuContentData;
import com.danmu.widget.DanmuSurfaceView;
import com.lb.common.util.Log;
import com.lb.video.activity.VideoPlayerActivity;
import com.lb.video.adapter.DanmuListAdapter;
import com.lb.zbrj.net.NetIF_ZBRJ;

public class DanmuFileLoadRunnable implements Runnable {
	private Context context;
	private Handler handler;
	private DanmuSurfaceView danmuview;
	private String bulletFile;
	private DanmuListAdapter danmuListAdapter;
	public DanmuFileLoadRunnable(Context context, Handler handler,
			DanmuSurfaceView danmuview ,String videoId,DanmuListAdapter danmuListAdapter) {
		super();
		this.context = context;
		this.handler = handler;
		this.danmuview = danmuview;
		this.bulletFile = videoId;
		this.danmuListAdapter = danmuListAdapter;
	}

	@Override
	public void run() {
		int what;
		try {
			NetIF_ZBRJ videoNet = new NetIF_ZBRJ(context);
			List<DanmuContentData> result = videoNet.downloadDanmuFile(bulletFile);
			Log.d(bulletFile, bulletFile);
			what = VideoPlayerActivity.MYEVENT_DANMULOAD_SUCCESS;
			if(result == null){
				what = VideoPlayerActivity.MYEVENT_DANMULOAD_FAIL;
			}else{
				if(result.size()>0){
					for(DanmuContentData content:result){
						danmuview.addDanmakuDisplayByTime(content);
						danmuListAdapter.addData(content);
					}
					danmuListAdapter.notifyDataSetChanged();
				}
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			what = VideoPlayerActivity.MYEVENT_DANMULOAD_FAIL;
		}
		
		handler.sendEmptyMessage(what);
	}

	private void insertTestData(){
		DanmuContentData content = new DanmuContentData();
		for(int i=0; i<5; i++){
				content.msg="快接啊两地分居adfadf"+i+System.currentTimeMillis();
				content.nick = "测试";
				danmuview.addDanmakuDisplayByNow(content);
		}
	}
}
