package com.lb.video.job;

import com.danmu.data.DanmuContentData;
import com.danmu.widget.DanmuSurfaceView;
import com.lb.common.util.Constants;
import com.lb.common.util.DateUtil;
import com.lb.common.util.Log;
import com.lb.video.adapter.DanmuListAdapter;
import com.lb.video.data.DanmuQueryData;
import com.lb.video.data.DanmuQueryResData;
import com.lb.zbrj.net.NetIF_ZBRJ;

public class DanmuQueryRunnable implements IRunnable {
	private NetIF_ZBRJ netIF_ZBRJ;
	private DanmuSurfaceView danmuView;
	private boolean starting = true;
	private String videoId;
	private DanmuListAdapter danmuListAdapter;
	public DanmuQueryRunnable(NetIF_ZBRJ net , DanmuSurfaceView danmuView  , String videoId ){
		this.netIF_ZBRJ = net;
		this.danmuView = danmuView;
		this.videoId = videoId;
	}
	public DanmuQueryRunnable(NetIF_ZBRJ net , DanmuSurfaceView danmuView  , String videoId,DanmuListAdapter danmuListAdapter ){
		this.netIF_ZBRJ = net;
		this.danmuView = danmuView;
		this.videoId = videoId;
		this.danmuListAdapter = danmuListAdapter;
	}
	@Override
	public void run() {
		DanmuQueryData data = new DanmuQueryData();
		data.videoID = this.videoId;
		data.datetime = DateUtil.getFullDateTimeString("-", ":");
		starting = true;
		while(starting){
			try{
				//FIXME 测试数据加入
				query(data);
				//insertTestData();
			}catch(Exception e){
				Log.e(e.getMessage(), e);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void query(DanmuQueryData data) {
		data.id = netIF_ZBRJ.getRandomNumber()+"";
		DanmuQueryResData result = netIF_ZBRJ.m1_get_bullet(data);
		result.status = Constants.RES_SUCCESS;
		
		if(result == null || Constants.RES_FAIL.equals( result.status) ){
			Log.e("DanmuRunnable.run",Constants.RES_FAIL+(result==null||result.desc == null?"":result.desc));
		}else{
			if(result.dateTime != null){
				data.datetime = result.dateTime;
			}
			if(result.bulletlist != null && result.bulletlist.size()>0){
				int length = result.bulletlist.size();
				for(int i=0 ; i < length ; i++){
					DanmuContentData content = result.bulletlist.get(i);						
					danmuView.addDanmakuDisplayByNow(content);
					if(danmuListAdapter != null){
						danmuListAdapter.addData(content);
					}
				}
				if(danmuListAdapter != null)
					danmuListAdapter.notifyDataSetChanged();
			}
		}
	}
	private void insertTestData(){
		DanmuContentData content = new DanmuContentData();
				content.msg="测试弹幕"+System.currentTimeMillis();
				content.nick = "测试";
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(danmuListAdapter != null){
					danmuListAdapter.addData(content);
					danmuListAdapter.notifyDataSetChanged();
				}
				danmuView.addDanmakuDisplayByNow(content);
	}
	public void stop(){
		starting = false;
	}
}
