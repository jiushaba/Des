package com.lz.oncon.activity.connections.widget;

import com.xuanbo.xuan.R;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuBarViewHolder {
	public View leftBtnLayout;
	public Button leftIconBtn;
	public Button leftTextBtn;
	public View rightBtnLayout;
	public TextView rightBtnTextView;
	public Button rightIconBtn;
	public Button rightTextBtn;
	public TextView titleTextView;
	public View wholeLayout;

	public static MenuBarViewHolder create(Activity activity) {
		MenuBarViewHolder menuBarViewHolder = new MenuBarViewHolder();
		menuBarViewHolder.wholeLayout = activity
				.findViewById(R.id.menu_bar_whole_layout);
		menuBarViewHolder.leftBtnLayout = activity
				.findViewById(R.id.menu_bar_left_btn_layout);
		menuBarViewHolder.leftIconBtn = ((Button) activity
				.findViewById(R.id.menu_bar_back_btn));
		menuBarViewHolder.leftTextBtn = ((Button) activity
				.findViewById(R.id.menu_bar_left_btn));
		menuBarViewHolder.rightBtnLayout = activity
				.findViewById(R.id.menu_bar_right_btn_layout);
		menuBarViewHolder.rightBtnTextView = ((TextView) activity
				.findViewById(R.id.right_textview));
		menuBarViewHolder.rightIconBtn = ((Button) activity
				.findViewById(R.id.menu_bar_more_btn));
		menuBarViewHolder.rightTextBtn = ((Button) activity
				.findViewById(R.id.menu_bar_right_btn));
		menuBarViewHolder.titleTextView = ((TextView) activity
				.findViewById(R.id.menu_bar_title));
		return menuBarViewHolder;
	}

	public static MenuBarViewHolder create(View view) {
		MenuBarViewHolder menuBarViewHolder = new MenuBarViewHolder();
		menuBarViewHolder.wholeLayout = view
				.findViewById(R.id.menu_bar_whole_layout);
		menuBarViewHolder.leftBtnLayout = view
				.findViewById(R.id.menu_bar_left_btn_layout);
		menuBarViewHolder.leftIconBtn = ((Button) view
				.findViewById(R.id.menu_bar_back_btn));
		menuBarViewHolder.leftTextBtn = ((Button) view
				.findViewById(R.id.menu_bar_left_btn));
		menuBarViewHolder.rightBtnLayout = view
				.findViewById(R.id.menu_bar_right_btn_layout);
		menuBarViewHolder.rightBtnTextView = ((TextView) view
				.findViewById(R.id.right_textview));
		menuBarViewHolder.rightIconBtn = ((Button) view
				.findViewById(R.id.menu_bar_more_btn));
		menuBarViewHolder.rightTextBtn = ((Button) view
				.findViewById(R.id.menu_bar_right_btn));
		menuBarViewHolder.titleTextView = ((TextView) view
				.findViewById(R.id.menu_bar_title));
		return menuBarViewHolder;
	}

	public void fillLeft(String leftStr, int leftResId,
			View.OnClickListener leftBtnOnClickListener) {
		fillViews(leftStr, leftResId, leftBtnOnClickListener, null, null, 0,
				null);
	}

	public void fillRight(String rightStr, int rightResId,
			View.OnClickListener rightBtnOnClickListener) {
		fillViews(null, 0, null, null, rightStr, rightResId,
				rightBtnOnClickListener);
	}

	public void fillRightTextView() {
	}

	public void fillTitle(CharSequence title) {
		fillViews(null, 0, null, title, null, 0, null);
	}

	public void fillViews(String leftStr, int leftResId,
			View.OnClickListener leftBtnOnClickListener, CharSequence title,
			String rightStr, int rightResId,
			View.OnClickListener rightBtnOnClickListener) {

		if (((!TextUtils.isEmpty(leftStr)) || (leftResId != 0))
				&& (leftBtnOnClickListener != null)) {
			if (leftBtnLayout != null) {
				leftBtnLayout.setOnClickListener(leftBtnOnClickListener);
				if (TextUtils.isEmpty(leftStr)) {
					leftIconBtn.setBackgroundResource(leftResId);
					leftIconBtn.setVisibility(View.VISIBLE);
					leftIconBtn.setOnClickListener(leftBtnOnClickListener);
					leftTextBtn.setVisibility(View.GONE);
				} else {
					leftTextBtn.setText(leftStr);
					leftTextBtn.setVisibility(View.VISIBLE);
					leftTextBtn.setOnClickListener(leftBtnOnClickListener);
					leftIconBtn.setVisibility(View.GONE);
				}
			}
		}

		if (((TextUtils.isEmpty(rightStr)) && (rightResId == 0))
				|| (rightBtnOnClickListener == null)) {

		} else {
			if (rightBtnLayout != null) {
				rightBtnLayout.setOnClickListener(rightBtnOnClickListener);
				if (TextUtils.isEmpty(rightStr)) {
					if (rightResId != 0) {
						rightIconBtn.setBackgroundResource(rightResId);
						rightIconBtn.setVisibility(View.VISIBLE);
						rightIconBtn
								.setOnClickListener(rightBtnOnClickListener);
						rightTextBtn.setVisibility(View.GONE);
					}
				} else {
					rightTextBtn.setText(rightStr);
					rightTextBtn.setVisibility(View.VISIBLE);
					rightTextBtn
							.setOnClickListener(rightBtnOnClickListener);
					rightIconBtn.setVisibility(View.GONE);
				}
			}
		}

		if ((titleTextView != null) && (!TextUtils.isEmpty(title))) {
			titleTextView.setText(title);
			titleTextView.setVisibility(View.VISIBLE);
		}
	}

	public void hide() {
		if ((wholeLayout != null)
				&& (wholeLayout.getVisibility() == View.VISIBLE)) {
			wholeLayout.setVisibility(View.GONE);
		}
	}

	public void show() {
		if ((wholeLayout != null) && (wholeLayout.getVisibility() == View.GONE)) {
			wholeLayout.setVisibility(View.VISIBLE);
		}
	}
}