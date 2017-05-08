package com.lb.video.job;

import android.content.Context;
import android.widget.Toast;

import com.lb.common.util.Constants;
import com.lb.video.activity.VideoPlayerActivity;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.BaseNetAsyncTask;
import com.lb.zbrj.data.VideoData;
import com.lb.zbrj.net.NetIF_ZBRJ;
import com.lb.zbrj.net.NetInterfaceStatusDataStruct;
import com.lz.oncon.activity.BaseActivity;

public class VideoStartAsyncTask extends BaseNetAsyncTask {
	private String videoId;
	private NetIF_ZBRJ netIF;
	Context context;
	public VideoStartAsyncTask(Context context, String videoId) {
		super(context);
		this.context = context;
		this.videoId = videoId;
		netIF = new NetIF_ZBRJ(context);
	}

	@Override
	public NetInterfaceStatusDataStruct doNet() {
		return netIF.m1_get_videoInfo(videoId);
	}

	@Override
	public void afterNet(NetInterfaceStatusDataStruct result) {
		if(Constants.RES_SUCCESS.equals(result.getStatus())){
			VideoData videoData = (VideoData) result.getObj();
			VideoPlayerActivity.start(context, videoData);
		}else{
			if(context instanceof BaseActivity){
				((BaseActivity)context).toastToMessage(R.string.video_getinfo_wrong);
			}else{
				Toast.makeText(context,R.string.video_getinfo_wrong, 3000);
			}
		}
	}

}
