package com.lz.oncon.activity.friendcircle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.common.util.StringUtils;

public class FeedLikeUserLayout extends LinearLayout {
	
	private LinearLayout friendcircle_feedlike_root;
	private TextView friendcircle_name;
	private View fc_drivier_view;
	private String phone_name;
	private Context context;

	public FeedLikeUserLayout(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public FeedLikeUserLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	
	@SuppressLint("NewApi")
	public FeedLikeUserLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	
	public void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.fc_feedlike_item, this);
		friendcircle_feedlike_root = (LinearLayout) findViewById(R.id.friendcircle_feedlike_root);
		friendcircle_name = (TextView) findViewById(R.id.friendcircle_name);
		fc_drivier_view = (View)findViewById(R.id.fc_drivier_view);
	}
	
	public void setValue(String name,String userID,String mobile,boolean isDrivier){
		this.phone_name = StringUtils.ToDBC(name);
		SpannableString spStr = new SpannableString(phone_name);
		URLSpan span = new NoLineURLSpan(mobile, phone_name); // 设置超链接
		spStr.setSpan(span, 0, phone_name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		
		if(isDrivier){
			//有逗号
			friendcircle_name.append(spStr);
			friendcircle_name.append(StringUtils.ToDBC(", "));  //逗号必须分开加,否则无法正确识别出name
		}else{
			//没逗号
			friendcircle_name.append(spStr);
		}
		friendcircle_name.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public class FriendCircle_comment_Listener implements OnClickListener{

		@Override
		public void onClick(View v) {
		}
		
	}
	
	public class FriendCircle_Name_Listener implements OnClickListener{

		@Override
		public void onClick(View v) {
//			Intent intent = new Intent(context, FriendDetailActivity.class);
//			Bundle b = new Bundle();
//			b.putString("mobile", mobile);
//			b.putString("name", phone_name);
//			intent.putExtras(b);
//			context.startActivity(intent);
		}
		
	}
	
	private class NoLineURLSpan extends URLSpan {

		String mobile;
		String name;

		public NoLineURLSpan(String mobile, String name) {
			super(name);
			this.mobile = mobile;
			this.name = name;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(getResources().getColor(R.color.friendc_textlink_color));
			ds.setUnderlineText(false);
		}

		@Override
		public void onClick(View widget) {
			if (!TextUtils.isEmpty(mobile)) {
				PersonController.go2Detail(context, mobile);
			}
		}
	}
	

}
