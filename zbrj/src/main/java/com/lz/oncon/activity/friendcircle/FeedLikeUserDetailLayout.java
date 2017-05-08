package com.lz.oncon.activity.friendcircle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.LikeData;

public class FeedLikeUserDetailLayout extends LinearLayout {

	private GridView fc_feedlike_item_gridview;
	private Context context;

	public FeedLikeUserDetailLayout(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public FeedLikeUserDetailLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	@SuppressLint("NewApi")
	public FeedLikeUserDetailLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.fc_feedlike_detailitem, this);
		fc_feedlike_item_gridview = (GridView) findViewById(R.id.fc_feedlike_item_gridview);
	}

	@SuppressWarnings("unchecked")
	public void setValue(ArrayList<LikeData> up) {
		if (up != null){
			LayoutParams params;
			int height = 0;
			if (up.size() <= 6) {
				height = LayoutParams.WRAP_CONTENT;
			} else if (up.size() <=12 ) {
				height = context.getResources().getDimensionPixelSize(R.dimen.fc_like_height_2); 
			} else if (up.size() <= 18) {
				height = context.getResources().getDimensionPixelSize(R.dimen.fc_like_height_3);
			} else if (up.size() <= 24) {
				height = context.getResources().getDimensionPixelSize(R.dimen.fc_like_height_4);
			} else if (up.size() <= 30) {
				height = context.getResources().getDimensionPixelSize(R.dimen.fc_like_height_5);
			} 
			Collections.sort(up, new Source_UpByDate());
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, height);
			fc_feedlike_item_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
			fc_feedlike_item_gridview.setLayoutParams(params);
			fc_feedlike_item_gridview.setAdapter(new FcFeedLikeItemGridViewAdapter(context, up));
			fc_feedlike_item_gridview.setOnItemClickListener(new feedLikeGridViewListener(context, up));
		}
	}

	private class feedLikeGridViewListener implements OnItemClickListener {
		private Context mc;
		private ArrayList<LikeData> up;

		public feedLikeGridViewListener(Context c, ArrayList<LikeData> up) {
			this.mc = c;
			this.up = up;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (up != null) {
				LikeData up_temp = (LikeData) up.get(position);
				if (up_temp != null) {
					PersonController.go2Detail(mc, up_temp.likeAccount);
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	class Source_UpByDate implements Comparator {
		public int compare(Object o1, Object o2) {
			Source_Up s1 = (Source_Up) o1;
			Source_Up s2 = (Source_Up) o2;
			return s1.getCreateTime().compareTo(s2.getCreateTime());
		}
	}

}
