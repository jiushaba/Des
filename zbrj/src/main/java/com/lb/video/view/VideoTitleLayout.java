package com.lb.video.view;

import com.lb.common.util.Constants;
import com.lb.common.util.ImageLoader;
import com.lb.common.util.Log;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.PersonData;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.HeadImageView;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoTitleLayout extends RelativeLayout {
	private View  view;
	private ImageView personHead;
	private TextView personNick;
	private PersonController mPersonController;
	private PersonData personData;
	public VideoTitleLayout(Context context) {
		super(context);
		init(context);
	}

	public VideoTitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VideoTitleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.video_title_bar,this);
		personHead = (ImageView) view.findViewById(R.id.video_person_head);
		personNick = (TextView)view.findViewById(R.id.video_person_nick);
		mPersonController = new PersonController();
		initPersonData();
	}

	public void initPersonData() {
		personData = mPersonController.findPerson(AccountData.getInstance().getBindphonenumber());
		personNick.setText(personData.nickname);
		String img = personData.image;
		if (StringUtils.isNull(img)) {
			personHead.setVisibility(View.INVISIBLE);
		} else {
			try {
				ImageLoader.displayHeadImage(img, personHead);
				personHead.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, e);
				personHead.setVisibility(View.INVISIBLE);
			}
		}
	}
	public PersonData getPersonData(){
		return personData;
	}
}
