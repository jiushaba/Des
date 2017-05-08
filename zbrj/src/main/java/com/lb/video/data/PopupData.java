package com.lb.video.data;

import android.view.View.OnClickListener;

public class PopupData {
	public String title;
	public String msg;
	public Callback callback;
	public Callback cancelCallback;
	public interface Callback{
		public void callback();
	}
}
