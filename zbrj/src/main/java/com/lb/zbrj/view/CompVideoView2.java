package com.lb.zbrj.view;

import com.lb.common.util.ImageLoader;
import com.xuanbo.xuan.R;
import com.lb.zbrj.data.VideoData;
import com.lz.oncon.widget.HeadImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CompVideoView2 extends LinearLayout {
	
	ImageView videoImageV;
	HeadImageView avatarV;
	TextView watchersNumV, upNumV, titleV, nickV;

	public CompVideoView2(Context context) {
		super(context);
		init();
	}
	
	public CompVideoView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public CompVideoView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.activity_watch_list_comp_video_item2, this);
		avatarV = (HeadImageView) findViewById(R.id.avatar);
		videoImageV = (ImageView) findViewById(R.id.videoImage);
		watchersNumV = (TextView) findViewById(R.id.watchersNum);
		upNumV = (TextView) findViewById(R.id.upNum);
		nickV = (TextView) findViewById(R.id.nick);
		titleV = (TextView) findViewById(R.id.title);
	}

	public void setData(VideoData data){
		ImageLoader.displayPicImage(data.videoImage, videoImageV);
		avatarV.setPerson(data.account, data.imageUrl);
		watchersNumV.setText(data.watchersNum + "");
		upNumV.setText(data.upNum  + "");
		titleV.setText(data.title);
		nickV.setText(data.nick);
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
