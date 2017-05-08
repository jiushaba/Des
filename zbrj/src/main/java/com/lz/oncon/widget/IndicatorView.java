package com.lz.oncon.widget;

import java.util.ArrayList;

import com.xuanbo.xuan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class IndicatorView extends ScrollView{
	
	public OnIndicatorChangeListener mOnIndicatorChangeListener;
	LinearLayout ll;
	ArrayList<View> vs = new ArrayList<View>();
	private int mSelectedColor = getResources().getColor(R.color.publicacc_sel_text), mUnSelectedColor = getResources().getColor(R.color.publicacc_nor_text);

	public IndicatorView(Context context) {
		super(context);
		init();
	}
	
	public IndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public IndicatorView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		setVerticalScrollBarEnabled(false);
		ll = new LinearLayout(getContext());
		ll.setOrientation(LinearLayout.VERTICAL);
		addView(ll, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
	}
	
	public void setColor(int selectedColor, int unSelectedColor){
		mSelectedColor = selectedColor;
		mUnSelectedColor = unSelectedColor;
	}
	
	public void setDatas(ArrayList<String> datas){
		vs.clear();
		ll.removeAllViews();
		if(datas != null && datas.size() > 0){
			final int size = datas.size();
			for(int i=0;i<size;i++){
				final int idx = i;
				View v = genView(datas.get(i), i);
				v.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						for(int i=0;i<size;i++){
							TextView tv = (TextView) vs.get(i).findViewById(R.id.tab_tv);
							if(i == idx){
								tv.setTextColor(mSelectedColor);
								tv.setBackgroundColor(getResources().getColor(R.color.publicacc_sel_bg));
								tv.getPaint().setFakeBoldText(true);
							}else{
								tv.setTextColor(mUnSelectedColor);
								tv.setBackgroundColor(getResources().getColor(R.color.gray_publicaccount));
								tv.getPaint().setFakeBoldText(false);
							}
						}
						if(mOnIndicatorChangeListener != null){
							mOnIndicatorChangeListener.onChange(idx);
						}
					}
				});
				vs.add(v);
				ll.addView(v, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			}
		}
	}
	
	private View genView(String str, int idx){
		View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_indicator, null);
		TextView tv = (TextView) view.findViewById(R.id.tab_tv);
		tv.setText(str);
		if(idx == 0){
			tv.setTextColor(mSelectedColor);
			tv.getPaint().setFakeBoldText(true);
			tv.setBackgroundColor(getResources().getColor(R.color.publicacc_sel_bg));
		}else{
			tv.setTextColor(mUnSelectedColor);
			tv.getPaint().setFakeBoldText(false);
			tv.setBackgroundColor(getResources().getColor(R.color.gray_publicaccount));
			
		}
		return view;
	}
	
	public interface OnIndicatorChangeListener{
		public void onChange(int idx);
	}
}