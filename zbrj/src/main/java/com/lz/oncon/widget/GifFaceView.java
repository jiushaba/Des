package com.lz.oncon.widget;

import java.io.InputStream;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xuanbo.xuan.R;
import com.terry.gif.TypegifView;

public class GifFaceView extends LinearLayout {

	private TypegifView gifView ;
	private InputStream imageInputStream;
	
	public InputStream getImageInputStream() {
		return imageInputStream;
	}

	public void setImageInputStream(InputStream imageInputStream) {
		this.imageInputStream = imageInputStream;
	}

	public TypegifView getGifView() {
		return gifView;
	}

	public void setGifView(TypegifView gifView) {
		this.gifView = gifView;
	}

	public GifFaceView(Context context) {
		super(context);
		View convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_face, this);
		gifView = (TypegifView)convertView.findViewById(R.id.gifView1);
//		if(imageInputStream!=null)
//		gifView.setGifImage(imageInputStream);
//		gifView.setBackgroundResource(R.attr.src);
	}

}
