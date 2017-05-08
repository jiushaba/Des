package com.lz.oncon.app.im.ui.common;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class IMMessageListViewOnTouchListener implements OnTouchListener {

	IMMessageInputBar mInputBar;

	public IMMessageListViewOnTouchListener(IMMessageInputBar inputBar) {
		mInputBar = inputBar;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mInputBar.hideKeyboard();
		mInputBar.faceBar.setVisibility(View.GONE);
		mInputBar.moreBtnBar.setVisibility(View.GONE);
		mInputBar.iv_emoticons_normal.setVisibility(View.VISIBLE);
		mInputBar.iv_emoticons_checked.setVisibility(View.GONE);
		mInputBar.emojiIconContainer.setVisibility(View.GONE);
		return false;
	}
}