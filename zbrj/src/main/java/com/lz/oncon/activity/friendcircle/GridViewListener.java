package com.lz.oncon.activity.friendcircle;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.lb.zbrj.data.VideoData;

public class GridViewListener implements OnItemClickListener {
	private VideoData dynamic;
	@SuppressWarnings("unused")
	private Context mc;

	public GridViewListener(Context c, VideoData dynamic) {
		this.mc = c;
		this.dynamic = dynamic;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!TextUtils.isEmpty(dynamic.videoImage)){
			//FIXME 播放视频
//			Intent intent = new Intent(mc, Fc_ImageBatchShowActivity.class);
//			Bundle b = new Bundle();
//			b.putInt("position", position);
//			b.putSerializable("photo_list", dynamic.getList_photo());
//			intent.putExtras(b);
//			((Activity)mc).startActivityForResult(intent, FriendCircleActivity.REFRESH);
		}

	}

}
