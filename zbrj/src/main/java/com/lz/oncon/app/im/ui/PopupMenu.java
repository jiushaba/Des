package com.lz.oncon.app.im.ui;

import java.util.ArrayList;

import com.lb.common.util.ImageUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.data.MenuData;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class PopupMenu extends PopupWindow{

	private Context mContext;
	private LinearLayout ll, subll;
	private ArrayList<MenuData> list;
	private String mOnconId;
	private LinearLayout.LayoutParams itemLP;

	public PopupMenu(Context context, ArrayList<MenuData> datas, String onconId) {
		super(context);
		mContext = context;
		mOnconId = onconId;
		ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setGravity(Gravity.CENTER);
		//设置SelectPicPopupWindow的View
		this.setContentView(ll);
		list = datas;
		initMenu();
		ll.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		this.setWidth(ll.getMeasuredWidth());
		this.setHeight(ll.getMeasuredHeight());
		//设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		//设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottomSlow);
		//实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x00000000);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		//mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//		mMenuView.setOnTouchListener(new OnTouchListener() {
//			
//			public boolean onTouch(View v, MotionEvent event) {
//				
//				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
//				int y=(int) event.getY();
//				if(event.getAction()==MotionEvent.ACTION_UP){
//					if(y<height){
//						dismiss();
//					}
//				}				
//				return true;
//			}
//		});
	}
	
	private void initMenu(){
		subll = new LinearLayout(mContext);
		subll.setOrientation(LinearLayout.VERTICAL);
		subll.setBackgroundResource(R.drawable.bg_im_popmenu);
		ll.addView(subll);
		itemLP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ImageUtil.convertDipToPx(mContext, 40));
		for(int i=0;i<list.size();i++){
			if(i != 0){
				subll.addView(initMenuItemDivider());
			}
			subll.addView(initMenuItem(list.get(i)));
		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ImageUtil.convertDipToPx(mContext, 10));
		lp.topMargin = 0-ImageUtil.convertDipToPx(mContext, 8);
		ImageView iv = new ImageView(mContext);
		iv.setImageResource(R.drawable.bg_im_popmenu_arrow);
		iv.setLayoutParams(lp);
		ll.addView(iv);
	}
	
	private View initMenuItem(final MenuData menu){
		TextView tv = new TextView(mContext);
		tv.setLayoutParams(itemLP);
		tv.setText(menu.name);
		tv.setPadding(10, 10, 10, 10);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(R.drawable.bg_im_menu_sub);
		tv.setTextSize(16);
		tv.setTextColor(mContext.getResources().getColorStateList(R.drawable.text_color));
		return tv;
	}
	
	private View initMenuItemDivider(){
		ImageView iv = new ImageView(mContext);
		iv.setImageResource(R.drawable.bg_im_popmenu_divider);
		return iv;
	}
}