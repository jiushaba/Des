package com.lb.zbrj.view;

import com.lb.common.util.ImageLoader;
import com.xuanbo.xuan.R;
import com.lb.zbrj.data.VideoData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CompVideoView extends LinearLayout {
	
	ImageView videoImageV;
	TextView watchersNumV, upNumV, titleV;

	public CompVideoView(Context context) {
		super(context);
		init();
	}
	
	public CompVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public CompVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.activity_watch_list_comp_video_item, this);
		videoImageV = (ImageView) findViewById(R.id.videoImage);
		watchersNumV = (TextView) findViewById(R.id.watchersNum);
		upNumV = (TextView) findViewById(R.id.upNum);
		titleV = (TextView) findViewById(R.id.title);
	}

	public void setData(VideoData data){
		ImageLoader.displayPicImage(data.videoImage, videoImageV);
		watchersNumV.setText(data.watchersNum + "");
		upNumV.setText(data.upNum  + "");
		titleV.setText(data.title);
		this.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//FIXME 点击显示视频播放
//				Intent intent = new Intent(CommonRefreshListActivity.this,ConnectionsDetailActivity.class);
////				intent.putExtra("rmid", mConnectionsLists.get(position - 1).rmid);
//				intent.putExtra("level", mLevel);
////				intent.putExtra("relationship", value);
//				intent.putExtra("connection_info", mConnectionsLists.get(position));
//				startActivity(intent);
			}
		});
	}
}
