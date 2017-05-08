package com.lz.oncon.app.im.ui.view;

import java.io.File;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.common.util.ImageLoader;
import com.lb.common.util.ImageUtil;
import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.application.MyApplication;

public class MsgImageView extends LinearLayout {

	public ImageView message_image_image;
	public TextView message_image_text;
	private Resources res;
	private float desity;
	public MsgImageView(Context context) {
		super(context);
		LayoutInflater.from(getContext()).inflate(R.layout.message_image, this);
		message_image_image = (ImageView)findViewById(R.id.message_image_image);
		message_image_text = (TextView)findViewById(R.id.message_image_text);
		res = MyApplication.getInstance().getResources();
		desity = res.getDisplayMetrics().density;
	}

	public void setMessage(SIXmppMessage msg){
		try{
			if(msg.getSourceType() == SIXmppMessage.SourceType.SEND_MESSAGE){//发送类消息,优先获取本地图片
				if(!TextUtils.isEmpty(msg.getThumbnailPath())){
					File f = new File(msg.getThumbnailPath());
					if(f.exists()){//本地已生成缩略图且缩略图存在
						setWH(f.getPath());
						String path_temp = "";
						if(!TextUtils.isEmpty(msg.getThumbnailPath())){
							if(msg.getThumbnailPath().indexOf("file:///")<0){
								path_temp = "file:///".concat(msg.getThumbnailPath());
							}else{
								path_temp = msg.getThumbnailPath();
							}
						}
						ImageLoader.displayPicImage(path_temp, message_image_image);
						return;
					}
				}
				if(!TextUtils.isEmpty(msg.getImagePath())){
					File f = new File(msg.getImagePath());
					if(f.exists()){//本地未生成缩略图但本地大图存在
						setWH(f.getPath());
						String path_temp = "";
						if(!TextUtils.isEmpty(msg.getImagePath())){
							if(msg.getImagePath().indexOf("file:///")<0){
								path_temp = "file:///".concat(msg.getImagePath());
							}else{
								path_temp = msg.getImagePath();
							}
						}
						ImageLoader.displayPicImage(path_temp, message_image_image);
						return;
					}
				}
			}
			//发送类消息，本地图片被删除时  或  接收类消息  判断本地或网络获取
			String imageUrl = msg.getImageURL();
			String imagePath = msg.getImagePath();
			File f = new File(imagePath);
			if(f.exists()){
				setWH(f.getPath());
			}
			if(!TextUtils.isEmpty(imagePath)){
				File f1 = new File(imagePath);
				if(f1.exists()){
					String path_temp = "";
					if(!TextUtils.isEmpty(imagePath)){
						if(imagePath.indexOf("file:///")<0){
							path_temp = "file:///".concat(imagePath);
						}else{
							path_temp = imagePath;
						}
					}
					ImageLoader.displayPicImage(path_temp, message_image_image);
				}else if(!TextUtils.isEmpty(imageUrl)){
					ImageLoader.displayPicImage(imageUrl, message_image_image);
				}
			}else if(!TextUtils.isEmpty(imageUrl)){
				ImageLoader.displayPicImage(imageUrl, message_image_image);
			}
		}catch(Exception e){}
		try{
//			message_image_text.setText(" " + msg.getImageFileSize() / 1024 + "KB");
		}catch(Exception e){}
	}
	
	private void setWH(String pathName){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize=3;
		BitmapFactory.decodeFile(pathName, options);
		int bmHeight = (int)(desity*options.outHeight);
		int bmWidth = (int)(desity*options.outWidth);
		int min = ImageUtil.convertDipToPx(MyApplication.getInstance(), 80);
		int max = ImageUtil.convertDipToPx(MyApplication.getInstance(), 220);
		int h = bmHeight;
		int w = bmWidth;
		if(h > w){
			if(bmHeight > max){
				h = max;
			}else if(bmHeight < min){
				h = min;
			}
			w = (int)((double)h / (double)bmHeight * (double)bmWidth);
		}else{
			if(bmWidth > max){
				w = max;
			}else if(bmWidth < min){
				w = min;
			}
			h = (int)((double)w / (double)bmWidth * (double)bmHeight);
		}
		message_image_image.setLayoutParams(new LinearLayout.LayoutParams(w, h));
	}
}