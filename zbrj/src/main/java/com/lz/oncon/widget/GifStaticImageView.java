package com.lz.oncon.widget;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class GifStaticImageView extends ImageView{
	public GifStaticImageView(Context context) {
		super(context);
		int dimens = 150;
		float density = context.getResources().getDisplayMetrics().density;
		int finalDimens = (int)(dimens * density);

		LayoutParams lp = new LayoutParams(finalDimens, finalDimens);
		this.setLayoutParams(lp);
	}
}