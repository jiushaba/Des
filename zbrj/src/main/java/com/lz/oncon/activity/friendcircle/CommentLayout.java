package com.lz.oncon.activity.friendcircle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
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

import com.lb.common.util.Clipboard;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lz.oncon.activity.friendcircle.FriendCircleActivity.ShowCommentLayoutInterface;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.widget.HeadImageView;

/**
 * 评论的布局
 * 
 * @author Administrator
 * 
 */
public class CommentLayout extends LinearLayout {

	private TextView com_TV;
	private String mobile;
	private String name;
	private String replay_name;
	private String content;
	String realContent;
	private Context context;
	private ShowCommentLayoutInterface mSci;
	private int position;
	private boolean isName;
	private HeadImageView headV;

	public CommentLayout(Context context, ShowCommentLayoutInterface mSci, int position) {
		super(context);
		this.context = context;
		this.mSci = mSci;
		this.position = position;
		init();
	}

	public CommentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	@SuppressLint("NewApi")
	public CommentLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.fc_comment_item, this);
		com_TV = (TextView) findViewById(R.id.com_TV);
		headV = (HeadImageView) findViewById(R.id.avatar);
	}

	public void setValue(String name, String content, String mobile, String pid, int type, String replay_name, String tomobile) {
		this.mobile = mobile;
		headV.setMobile(mobile);
		this.name = name;
		this.content = content;
		this.replay_name = replay_name;
		if (TextUtils.isEmpty(name)) {
			this.name = "";
		}
		if (TextUtils.isEmpty(replay_name)) {
			this.replay_name = "";
		}
		if (TextUtils.isEmpty(content)) {
			this.content = "";
		}
		if (1 == type) {// 回复
			com_TV.setVisibility(View.VISIBLE);
			SpannableString spStr = new SpannableString(this.name);
			URLSpan span = new NoLineURLSpan(mobile,this.name); //设置超链接
			spStr.setSpan(span, 0, this.name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			com_TV.append(spStr);
			com_TV.append(getResources().getString(R.string.fc_del_dynamic_reply));
			SpannableString spStr2 = new SpannableString(this.replay_name);
			URLSpan span2 = new NoLineURLSpan(tomobile,this.replay_name); //设置超链接
			spStr2.setSpan(span2, 0, this.replay_name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			com_TV.append(spStr2);
			com_TV.append(": "+this.content);
			com_TV.setMovementMethod(LinkMovementMethod.getInstance());
			com_TV.setOnClickListener(new FriendCircle_comment_Listener(position, pid));
			com_TV.setOnLongClickListener(new FriendCircle_comment_LongListener(mobile,position, pid));
			
		} else {
			com_TV.setVisibility(View.VISIBLE);
			SpannableString spStr = new SpannableString(this.name);
			URLSpan span = new NoLineURLSpan(mobile,this.name); //设置超链接
			spStr.setSpan(span, 0, this.name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			com_TV.append(spStr);
			com_TV.append(": "+this.content);
			com_TV.setMovementMethod(LinkMovementMethod.getInstance());
			com_TV.setOnLongClickListener(new FriendCircle_comment_LongListener(mobile,position, pid));
			com_TV.setOnClickListener(new FriendCircle_comment_Listener(position, pid));
		}
	}

	private void choiceLongPressType(String mobile,final int position, final String pid) {

		if(mobile.equals(AccountData.getInstance().getBindphonenumber())){
			String[] groupTypes2 = {context.getResources().getString(R.string.fc_del_dynamic_copy),context.getResources().getString(R.string.fc_message_detele)};
			new AlertDialog.Builder(context).setItems(groupTypes2, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						Clipboard.setText(context, content);
					}else if(which == 1){//删除自己的评论
						mSci.deteltComment(position, pid);
					}
					Clipboard.setText(context, content);
				}
			}).show();
		}else{
			String[] groupTypes1 = {context.getResources().getString(R.string.fc_del_dynamic_copy)};
			new AlertDialog.Builder(context).setItems(groupTypes1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Clipboard.setText(context, content);
				}
			}).show();
		}

	}

	private void choicePressType(final int position, final String pid) {
		if (!mobile.equals(AccountData.getInstance().getBindphonenumber())) {
			String[] groupTypes1 = { context.getResources().getString(R.string.fc_del_dynamic_reply) };
			new AlertDialog.Builder(context).setItems(groupTypes1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSci.showCommentLayout();
						mSci.sendComment(position, pid);
				}
			}).show();
		}else{
			String[] groupTypes1 = {context.getResources().getString(R.string.fc_del_dynamic_copy)};
			new AlertDialog.Builder(context).setItems(groupTypes1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Clipboard.setText(context, content);
				}
			}).show();
		}
	}

	public class FriendCircle_comment_Listener implements OnClickListener {
		int position;
		String pid;

		public FriendCircle_comment_Listener(int position, String pid) {
			this.position = position;
			this.pid = pid;
		}

		@Override
		public void onClick(View v) {
			if (!isName) {
				choicePressType(position, pid);
			}
		}
	}

	public class FriendCircle_comment_LongListener implements OnLongClickListener {
		String mobile;
		int position2;
		String pid2;
		public FriendCircle_comment_LongListener(String mobile,int position2, String pid2) {
			this.mobile = mobile;
			this.position2 = position2;
			this.pid2 = pid2;
		}
		@Override
		public boolean onLongClick(View v) {
			choiceLongPressType(mobile,position2,pid2);
			return false;
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
	    	isName = true;
	    	if (!TextUtils.isEmpty(mobile)) {
	    		PersonController.go2Detail(context, mobile);
				new Thread(new Runnable() {
					@Override
					public void run() {
						SystemClock.sleep(1000);
						isName = false;
					}
				}).start();
			}
	    }	    
	}

}
