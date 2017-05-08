package com.lz.oncon.app.im.contact;

import android.content.Context;
import android.util.AttributeSet;
import com.lb.common.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.lb.common.util.Constants;
import com.lb.common.util.ImageUtil;
import com.xuanbo.xuan.R;
import com.lb.zbrj.data.FansData;
import com.lz.oncon.app.im.data.IMContactChooserData;
import com.lz.oncon.widget.HeadImageView;

public class ChooserSelectedListView extends LinearLayout implements View.OnClickListener {
	
	private Context mContext;
	private Button mOKButton;
	private LinearLayout mLayout;
	HorizontalScrollView scrollLayout;
	static int initId = 3008;
	ImageView globalHead ;
	int iconCount = 1;
	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String onconid = (String)v.getTag(R.id.tag_onconid);
			BaseAdapter a = (BaseAdapter)v.getTag(R.id.tag_adapter);
			if(v.getId() != initId){
				removeMember(onconid,a);
				invalidate();
			}
		}
	};
	
	public Button getmOKButton() {
		return mOKButton;
	}

	public void setmOKButton(Button mOKButton) {
		this.mOKButton = mOKButton;
	}

	public ChooserSelectedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	public ChooserSelectedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	public ChooserSelectedListView(Context context) {
		super(context);
		this.mContext = context;
		initView();
	}

	private void initView() {
		LayoutInflater.from(mContext).inflate(R.layout.app_im_contactchooser_selectedlist, this);
		mOKButton = (Button) findViewById(R.id.selectedlist_Button_ok);
		mOKButton.setBackgroundResource(R.drawable.ic_but_send_bg);
		mOKButton.setOnClickListener(this);
		mLayout = (LinearLayout) findViewById(R.id.selectedlist_Layout_list);
		scrollLayout = (HorizontalScrollView) findViewById(R.id.scrollLayout);
		
		int imageLength = ImageUtil.convertDipToPx(mContext, 40);
		LayoutParams params = new LayoutParams(imageLength, imageLength);
		params.rightMargin = 2;
		addInitIcon();
		mOKButton.setText(String.format(mContext.getString(R.string.contact_select_confirm), IMContactChooserData.getInstance().getMemberCount()));
	}
	
	public void addInitIcon(){
		globalHead = new ImageView(mContext);
		globalHead.setId(initId);
		globalHead.setImageResource(R.drawable.addgroupinit);
		int imageLength = ImageUtil.convertDipToPx(mContext, 35);
		LayoutParams params = new LayoutParams(imageLength, imageLength);
		params.rightMargin = 2;
		mLayout.addView(globalHead, params);
	}

	public void addMember(final String onconid,final Object object,final BaseAdapter a) {
		if (onconid != null && !onconid.equals("") && !IMContactChooserData.getInstance().isSelected(onconid)) {
			final HeadImageView head = new HeadImageView(mContext);
			head.setScaleType(ScaleType.FIT_XY);
			head.setAdjustViewBounds(true);
			head.setTag(R.id.tag_onconid, onconid);
			head.setTag(R.id.tag_adapter, a);
			head.setOnClickListener(mOnClickListener);
			if(object instanceof FansData){
				head.setPerson(((FansData)object).account, ((FansData)object).imageurl);
			}
			int imageLength = ImageUtil.convertDipToPx(mContext, 35);
			LayoutParams params = new LayoutParams(imageLength, imageLength);
			params.rightMargin = 2;
			if(mLayout.findViewById(initId) != null){
				mLayout.removeView(globalHead);
			}
			mLayout.addView(head, params);
			addInitIcon();
			iconCount++;
			scrollLayout.post(new Runnable() {
				@Override
				public void run() { 
					scrollLayout.smoothScrollTo(mLayout.getMeasuredWidth()-scrollLayout.getWidth(), ContactMsgCenterActivity.screenHeight - mLayout.getHeight()); 
				}
			});
			IMContactChooserData.getInstance().addMember(onconid,object);
			mOKButton.setText(String.format(mContext.getString(R.string.contact_select_confirm), IMContactChooserData.getInstance().getMemberCount()));
		}
	}

	public void removeMember(String onconid,BaseAdapter a) {
		try{
			if (onconid != null && IMContactChooserData.getInstance().isSelected(onconid)) {
				int index = IMContactChooserData.getInstance().removeMember(onconid);
				if (index >= 0) {
					mLayout.removeViewAt(index);
					mOKButton.setText(String.format(mContext.getString(R.string.contact_select_confirm), IMContactChooserData.getInstance().getMemberCount()));
				}
				a.notifyDataSetChanged();
				iconCount--;
			}
		}catch(Exception e){
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}

	public void removeAllMembers() {
		IMContactChooserData.getInstance().removeAllMembers();
		mLayout.removeAllViews();
		addInitIcon();
		mOKButton.setText(String.format(mContext.getString(R.string.contact_select_confirm), IMContactChooserData.getInstance().getMemberCount()));
		iconCount = 1;
	}

	public void update() {
		invalidate();
	}

	@Override
	public void onClick(View v) {
		if (mOkButtonClickListener != null) {
			mOkButtonClickListener.onOKButtonClickListener();
		}
	}

	public interface OnOKButtonClickListener {
		public void onOKButtonClickListener();
	}

	private OnOKButtonClickListener mOkButtonClickListener;

	public void setOnOKButtonClickListener(OnOKButtonClickListener okClickListener) {
		this.mOkButtonClickListener = okClickListener;
	}
}
