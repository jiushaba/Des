package com.lz.oncon.activity.friendcircle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.common.util.Clipboard;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.common.util.StringUtils;
import com.lz.oncon.activity.friendcircle.FriendCircleActivity.ShowCommentLayoutInterface;
import com.lz.oncon.data.AccountData;

/**
 * 评论的布局
 * 
 * @author Administrator
 * 
 */
public class CommentDeailLayout extends LinearLayout {

	private LinearLayout friendcircle_comment_root;
	private LinearLayout comment_type;
	private com.lz.oncon.widget.HeadImageView fc_item_message_head;
	private TextView fc_create_time;
	private TextView friendcircle_name;
	private TextView fc_del_dynamic_reply;
	private TextView friendcircle_reply_name;
	private TextView friendcircle_content;
	private String mobile;
	private Context context;
	private ShowCommentLayoutInterface mSci;

	public CommentDeailLayout(Context context, ShowCommentLayoutInterface mSci) {
		super(context);
		this.context = context;
		this.mSci = mSci;
		init();
	}

	public CommentDeailLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	@SuppressLint("NewApi")
	public CommentDeailLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.fc_comment_detalitem, this);
		friendcircle_comment_root = (LinearLayout) findViewById(R.id.friendcircle_comment_root);
		comment_type = (LinearLayout)findViewById(R.id.comment_type);
		fc_item_message_head = (com.lz.oncon.widget.HeadImageView)findViewById(R.id.fc_item_message_head);
		fc_create_time = (TextView)findViewById(R.id.fc_create_time);
		friendcircle_name = (TextView) findViewById(R.id.friendcircle_name);
		fc_del_dynamic_reply = (TextView) findViewById(R.id.fc_del_dynamic_reply);
		friendcircle_reply_name = (TextView) findViewById(R.id.friendcircle_reply_name);
		friendcircle_content = (TextView) findViewById(R.id.friendcircle_content);
	}

	public void setValue(String name, String content, String mobile, String pid, String type, String replay_name, String tomobile,String createTime,boolean isShowType,int position) {
		this.mobile = mobile;
		if(isShowType){
			comment_type.setVisibility(View.VISIBLE);
		}else{
			comment_type.setVisibility(View.INVISIBLE);
		}
		if ("1".equals(type)) {// 回复
			fc_del_dynamic_reply.setVisibility(View.VISIBLE);
			friendcircle_reply_name.setVisibility(View.VISIBLE);
			fc_item_message_head.setMobile(mobile);
			friendcircle_name.setText(StringUtils.repNull(name));
			friendcircle_reply_name.setText(StringUtils.repNull(replay_name));
			friendcircle_reply_name.setOnClickListener(new FriendCircle_Name_Listener(tomobile,replay_name));
			if(TextUtils.isEmpty(createTime)){
				fc_create_time.setText(StringUtils.repNull(FC_TimeUtils.getMessagDetailDate(context, System.currentTimeMillis())));
			}else{
				fc_create_time.setText(StringUtils.repNull(FC_TimeUtils.getMessagDetailDate(context, Long.parseLong(createTime))));
			}
			friendcircle_content.setText(StringUtils.repNull(content));
			friendcircle_comment_root.setOnLongClickListener(new FriendCircle_comment_LongListener(mobile,position, pid));
			friendcircle_comment_root.setOnClickListener(new FriendCircle_comment_Listener(position, pid));
			friendcircle_name.setOnClickListener(new FriendCircle_Name_Listener(mobile,name));

		} else {
			fc_del_dynamic_reply.setVisibility(View.GONE);
			friendcircle_reply_name.setVisibility(View.GONE);
			fc_item_message_head.setMobile(mobile);
			friendcircle_name.setText(StringUtils.repNull(name));
			if(TextUtils.isEmpty(createTime)){
				fc_create_time.setText(StringUtils.repNull(FC_TimeUtils.getMessagDetailDate(context, System.currentTimeMillis())));
			}else{
				fc_create_time.setText(StringUtils.repNull(FC_TimeUtils.getMessagDetailDate(context, Long.parseLong(createTime))));
			}
			friendcircle_content.setText(StringUtils.repNull(content));
			friendcircle_comment_root.setOnLongClickListener(new FriendCircle_comment_LongListener(mobile,position, pid));
			friendcircle_comment_root.setOnClickListener(new FriendCircle_comment_Listener(position, pid));
			friendcircle_name.setOnClickListener(new FriendCircle_Name_Listener(mobile,name));
		}
	}

	private void choiceLongPressType(String mobile,final int position, final String pid) {
		if(mobile.equals(AccountData.getInstance().getBindphonenumber())){
			String[] groupTypes2 = {context.getResources().getString(R.string.fc_del_dynamic_copy),context.getResources().getString(R.string.fc_message_detele)};
			new AlertDialog.Builder(context).setItems(groupTypes2, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						Clipboard.setText(context, friendcircle_content.getText().toString());
					}else if(which == 1){//删除自己的评论
						mSci.deteltComment(position, pid);
					}
					Clipboard.setText(context, friendcircle_content.getText().toString());
				}
			}).show();
		}else{
			String[] groupTypes1 = {context.getResources().getString(R.string.fc_del_dynamic_copy)};
			new AlertDialog.Builder(context).setItems(groupTypes1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Clipboard.setText(context, friendcircle_content.getText().toString());
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
					if (position == -1) {// 详
					} else {
						mSci.sendComment(position, pid);
					}
				}
			}).show();
		}else{
			String[] groupTypes1 = {context.getResources().getString(R.string.fc_del_dynamic_copy)};
			new AlertDialog.Builder(context).setItems(groupTypes1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Clipboard.setText(context, friendcircle_content.getText().toString());
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
			choicePressType(position, pid);
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

	public class FriendCircle_Name_Listener implements OnClickListener {
		String mobile = "";
		String name = "";

		public FriendCircle_Name_Listener(String mobile ,String name) {
			this.mobile = mobile;
			this.name = name;
		}

		@Override
		public void onClick(View v) {
			if (!TextUtils.isEmpty(mobile)) {
				PersonController.go2Detail(context, mobile);
			}
		}

	}

}
