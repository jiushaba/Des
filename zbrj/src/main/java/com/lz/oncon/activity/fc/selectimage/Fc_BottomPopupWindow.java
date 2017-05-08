package com.lz.oncon.activity.fc.selectimage;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xuanbo.xuan.R;

public class Fc_BottomPopupWindow extends PopupWindow {

	private View mMenuView;
	private ListView pop_listview_layout;
	private Context mContext;
	private TextView titleView;

	public Fc_BottomPopupWindow(Activity context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.fc_bottom_popwindow, null);
		pop_listview_layout = (ListView) mMenuView.findViewById(R.id.pop_listview_layout);
		titleView = (TextView) mMenuView.findViewById(R.id.pop_title);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		mMenuView.setFocusable(true);
		mMenuView.setFocusableInTouchMode(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0x00000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(null);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

		mMenuView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// 手机键盘上的返回键
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					dismiss();
					break;
				}
				return false;
			}
		});
	}

	public void setTitle(int textResId) {
		titleView.setText(textResId);
		titleView.setVisibility(View.VISIBLE);
	}

	private Fc_ButtonPopupImageListAdapter fc_imagelist_adapter;

	public void addList(Context act, List<ImageBucket> dataList, OnItemClickListener onItemClickListener) {
		pop_listview_layout.setOnItemClickListener(onItemClickListener);
		if (fc_imagelist_adapter == null) {
			fc_imagelist_adapter = new Fc_ButtonPopupImageListAdapter(act, dataList);
			pop_listview_layout.setAdapter(fc_imagelist_adapter);
		} else {
			if (dataList != null && dataList.size() > 0)
				fc_imagelist_adapter.setDataList(dataList);
			fc_imagelist_adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 在指定控件上方显示，默认x座标与指定控件的中点x座标相同
	 * 
	 * @param anchor
	 * @param xoff
	 * @param yoff
	 */
	public void showAsPullUp(View anchor, int xoff, int yoff) {
		// 保存anchor在屏幕中的位置
		int[] location = new int[2];
		// 保存anchor上部中点
		int[] anchorCenter = new int[2];
		// 读取位置anchor座标
		anchor.getLocationOnScreen(location);
		// 计算anchor中点
		anchorCenter[0] = location[0] - anchor.getWidth() / 2;
		anchorCenter[1] = location[1];
		super.showAtLocation(anchor, Gravity.TOP | Gravity.CENTER, anchorCenter[0] + xoff, anchorCenter[1] - anchor.getContext().getResources().getDimensionPixelSize(R.dimen.personal_menu_itemwidth) + yoff);
	}
}