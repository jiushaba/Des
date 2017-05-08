package com.lz.oncon.activity.friendcircle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.common.util.Clipboard;
import com.xuanbo.xuan.R;
/**
 * 帖子布局
 * @author Administrator
 *
 */
public class FC_DetalLayout extends LinearLayout{
	
	private LinearLayout friendcircle_comment_root;
	private TextView friendcircle_content;
	private Context context;

	public FC_DetalLayout(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public FC_DetalLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	
	@SuppressLint("NewApi")
	public FC_DetalLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	
	public void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.fc_detailtext_item, this);
		friendcircle_comment_root = (LinearLayout) findViewById(R.id.friendcircle_comment_root);
		friendcircle_content = (TextView)findViewById(R.id.friendcircle_content);
	}
	
	public void setValue(String content){
		friendcircle_content.setText(content);
		friendcircle_comment_root.setOnLongClickListener(new FriendCircle_comment_Listener());
	}

	
	private void choiceGroupType() {
			String[] groupTypes1 = {context.getResources().getString(R.string.fc_del_dynamic_copy)};
			new AlertDialog.Builder(context).setItems(groupTypes1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Clipboard.setText(context, friendcircle_content.getText().toString());
				}
			}).show();
		
	}
	
	public class FriendCircle_comment_Listener implements OnLongClickListener{
		@Override
		public boolean onLongClick(View v) {
			choiceGroupType();
			return false;
		}
		
	}

}
