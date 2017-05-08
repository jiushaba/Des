package com.lz.oncon.widget;

import com.lb.common.util.ImageUtil;
import com.xuanbo.xuan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CancleableTextView extends RelativeLayout {
	
	private TextView cancleable_textview_TV;
	private ImageView cancleable_textview_IV;
	private OnDispearListener onDispearListener;
	private AfterTVClickListener afterTVClickListener;
	private Context mContext;
	private boolean isFillIn;
	
	public boolean isFillIn() {
		return isFillIn;
	}

	public void setFillIn(boolean isFillIn) {
		this.isFillIn = isFillIn;
	}
	
	public CancleableTextView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public CancleableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public CancleableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.cancleable_textview, this);
		cancleable_textview_TV =  (TextView)findViewById(R.id.cancleable_textview_TV);
		cancleable_textview_IV =  (ImageView)findViewById(R.id.cancleable_textview_IV);
		initStyles();
		initListeners();
	}
	
	private void initStyles(){
		cancleable_textview_IV.setVisibility(View.INVISIBLE);
		int maxpixels = getResources().getDisplayMetrics().widthPixels - ImageUtil.convertDipToPx(this.mContext, 13*2 + 10);
		cancleable_textview_TV.setMaxWidth(maxpixels);
	}

	private void initListeners(){
		cancleable_textview_TV.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(View.INVISIBLE == cancleable_textview_IV.getVisibility()){
					cancleable_textview_IV.setVisibility(View.VISIBLE);
				}else{
					cancleable_textview_IV.setVisibility(View.INVISIBLE);
				}
				if(afterTVClickListener != null){
					afterTVClickListener.afterTVClick();
				}
			}
			
		});
		
		cancleable_textview_IV.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				CancleableTextView.this.setVisibility(View.INVISIBLE);
				if(onDispearListener != null){
					onDispearListener.onDispear();
				}
			}
			
		});
	}
	
	public void setValue(String value){
		cancleable_textview_TV.setText(value);
	}
	
	public OnDispearListener getOnDispearListener() {
		return onDispearListener;
	}

	public void setOnDispearListener(OnDispearListener onDispearListener) {
		this.onDispearListener = onDispearListener;
	}

	public TextView getCancleable_textview_TV() {
		return cancleable_textview_TV;
	}

	public void setCancleable_textview_TV(TextView cancleable_textview_TV) {
		this.cancleable_textview_TV = cancleable_textview_TV;
	}
	
	public ImageView getCancleable_textview_IV() {
		return cancleable_textview_IV;
	}

	public void setCancleable_textview_IV(ImageView cancleable_textview_IV) {
		this.cancleable_textview_IV = cancleable_textview_IV;
	}

	public AfterTVClickListener getAfterTVClickListener() {
		return afterTVClickListener;
	}

	public void setAfterTVClickListener(AfterTVClickListener afterTVClickListener) {
		this.afterTVClickListener = afterTVClickListener;
	}
	
	public interface OnDispearListener{
		public void onDispear();
	}
	
	public interface AfterTVClickListener{
		public void afterTVClick();
	}
}
