package com.lz.oncon.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.app.im.util.SystemCamera;

public class CameraGalleryWithClearChoiceDialog {
	private BaseActivity mContext;
	private String fromViewName;
	private List<OnChoiceClickListener> mOnChoiceClickListeners;
	
	public static int CAMERA = 0;
	public static int IMAGE_LIB = 1;
	public static int CLEAR_PIC = 2;
	private OnCancelListener onCancelListener ;
	public CameraGalleryWithClearChoiceDialog(BaseActivity mContext) {
		super();
		this.mContext = mContext;
	}
	
	public void show(){
		 Context dialogContext = new ContextThemeWrapper(mContext,  
                 android.R.style.Theme_Light); 
		String [] array = {mContext.getString(R.string.camera)
				, mContext.getString(R.string.gallery)
				, mContext.getString(R.string.clear_pic)};
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,android.R.layout.simple_list_item_1,array);
		AlertDialog.Builder mDialog = new AlertDialog.Builder(dialogContext);  
		mDialog.setTitle(R.string.camera_gallery_dialog_title);
		mDialog.setSingleChoiceItems(adapter,-1,new DialogInterface.OnClickListener() {

			@TargetApi(Build.VERSION_CODES.KITKAT)
			public void onClick(DialogInterface dialog, int which) {
				 dialog.dismiss();
				 if(!TextUtils.isEmpty(fromViewName)){
					 mContext.getIntent().putExtra("fromViewName", fromViewName);
				 }
	    		 switch (which){ 
	    		 	case 1:
	    		 		try{
	    		 			Intent intentFromGallery = new Intent();
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                    intentFromGallery.setAction(Intent.ACTION_OPEN_DOCUMENT);
                            } else {
                                    intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                            }
                            intentFromGallery.setType("image/*");
                            mContext.startActivityForResult(intentFromGallery, Constants.GALLERY_RESULT_CODE);
	    		 		}catch(Exception e){
	    		 			mContext.toastToMessage(R.string.read_photo_fail);
	    		 		}
		 				break;
	    		 	case 0:
	    		 		try{
	    		 			Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
			    			mContext.startActivityForResult(intent1, Constants.CAMERA_RESULT_CODE);
	    		 		}catch(Exception e){
	    		 			mContext.toastToMessage(mContext.getString(R.string.camera) + mContext.getString(R.string.fail));
	    		 		}
		                break;
	    		 	case 2:
	    		 		break;
	    		 }
	    		 if(mOnChoiceClickListeners != null){
	    			 for(OnChoiceClickListener mOnChoiceClickListener:mOnChoiceClickListeners){
	    				 mOnChoiceClickListener.onClicked(which);
	    			 }
	    		 }
			}
			
		  }).show();
	}
	
	public static String captureFilePath;
	public static Uri uri;
	public static String getFilePath(){
		return captureFilePath;
	}
	
	public void showAgain(){
		 Context dialogContext = new ContextThemeWrapper(mContext,  
                android.R.style.Theme_Light); 
		String [] array = {mContext.getString(R.string.camera)
				, mContext.getString(R.string.gallery)
				, mContext.getString(R.string.clear_pic)};
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,android.R.layout.simple_list_item_1,array);
		AlertDialog.Builder mDialog = new AlertDialog.Builder(dialogContext);  
		mDialog.setTitle(R.string.camera_gallery_dialog_title);
		mDialog.setSingleChoiceItems(adapter,-1,new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				 dialog.dismiss();
				 if(!TextUtils.isEmpty(fromViewName)){
					 mContext.getIntent().putExtra("fromViewName", fromViewName);
				 }
	    		 switch (which){ 
	    		 	case 1:
	    		 		try{
	    		 			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			 				intent.setType("image/*");
			 				mContext.startActivityForResult(intent, Constants.GALLERY_RESULT_CODE);
	    		 		}catch(Exception e){
	    		 			mContext.toastToMessage(mContext.getString(R.string.camera) + mContext.getString(R.string.fail));
	    		 		}
		 				break;
	    		 	case 0:
	    		 		try{
	    		 			Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    				captureFilePath = Environment.getExternalStorageDirectory() + "/" + Constants.LOG_TAG + "/oncon/photos/"+"self_"+System.currentTimeMillis()+".jpg";
		    				File out = new File(captureFilePath);
		    				File outParent = out.getParentFile();
		    				if(!outParent.exists()){
		    					outParent.mkdirs();
		    				}
		    				uri = Uri.fromFile(out);
		    				intent2.putExtra(MediaStore.EXTRA_OUTPUT,uri);
		    				mContext.startActivityForResult(intent2, Constants.CAMERA_RESULT_CODE);
	    		 		}catch(Exception e){
	    		 			mContext.toastToMessage(mContext.getString(R.string.camera) + mContext.getString(R.string.fail));
	    		 		}
		                break;
	    		 	case 2:
	    		 		break;
	    		 }
	    		 if(mOnChoiceClickListeners != null){
	    			 for(OnChoiceClickListener mOnChoiceClickListener:mOnChoiceClickListeners){
	    				 mOnChoiceClickListener.onClicked(which);
	    			 }
	    		 }
			}
			
		  });
		if(onCancelListener != null){
			mDialog.setOnCancelListener(onCancelListener);
		}
		mDialog.show();
	}
	
	public void showNoCameraAgain(){
		 Context dialogContext = new ContextThemeWrapper(mContext,  
               android.R.style.Theme_Light); 
		String [] array = { mContext.getString(R.string.gallery)
				, mContext.getString(R.string.clear_pic)};
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,android.R.layout.simple_list_item_1,array);
		AlertDialog.Builder mDialog = new AlertDialog.Builder(dialogContext);  
		mDialog.setTitle(R.string.camera_gallery_dialog_title);
		mDialog.setSingleChoiceItems(adapter,-1,new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				 dialog.dismiss();
				 if(!TextUtils.isEmpty(fromViewName)){
					 mContext.getIntent().putExtra("fromViewName", fromViewName);
				 }
	    		 switch (which){ 
	    		 	case 0:
	    		 		try{
	    		 			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			 				intent.setType("image/*");
			 				mContext.startActivityForResult(intent, Constants.GALLERY_RESULT_CODE);
	    		 		}catch(Exception e){
	    		 			mContext.toastToMessage(mContext.getString(R.string.camera) + mContext.getString(R.string.fail));
	    		 		}
		 				break;
	    		 	/*case 0:
	    		 		try{
	    		 			Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    				captureFilePath = Environment.getExternalStorageDirectory() + "/" + Constants.LOG_TAG + "/oncon/photos/"+"self_"+System.currentTimeMillis()+".jpg";
		    				File out = new File(captureFilePath);
		    				File outParent = out.getParentFile();
		    				if(!outParent.exists()){
		    					outParent.mkdirs();
		    				}
		    				uri = Uri.fromFile(out);
		    				intent2.putExtra(MediaStore.EXTRA_OUTPUT,uri);
		    				mContext.startActivityForResult(intent2, Constants.CAMERA_RESULT_CODE);
	    		 		}catch(Exception e){
	    		 			mContext.toastToMessage(mContext.getString(R.string.camera) + mContext.getString(R.string.fail));
	    		 		}
		                break;*/
	    		 	case 1:
	    		 		break;
	    		 }
	    		 if(mOnChoiceClickListeners != null){
	    			 for(OnChoiceClickListener mOnChoiceClickListener:mOnChoiceClickListeners){
	    				 mOnChoiceClickListener.onClicked(which);
	    			 }
	    		 }
			}
			
		  }).show();
	}
	public void speakShow(){
		 Context dialogContext = new ContextThemeWrapper(mContext,  
                android.R.style.Theme_Light); 
		String [] array = {mContext.getString(R.string.camera)
				, mContext.getString(R.string.gallery)
				};
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,android.R.layout.simple_list_item_1,array);
		AlertDialog.Builder mDialog = new AlertDialog.Builder(dialogContext);  
		mDialog.setTitle(R.string.camera_gallery_dialog_title);
		mDialog.setSingleChoiceItems(adapter,-1,new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				 dialog.dismiss();
				 if(!TextUtils.isEmpty(fromViewName)){
					 mContext.getIntent().putExtra("fromViewName", fromViewName);
				 }
	    		 switch (which){ 
	    		 	case 1://图册
	    		 		SystemCamera.showPictures(mContext,Constants.GALLERY_RESULT_CODE);
		 				break;
	    		 	case 0://拍照
		    			SystemCamera.takePicture(mContext,Constants.CAMERA_RESULT_CODE);
		                break;
	    		 	case 2:
	    		 		break;
	    		 }
	    		 if(mOnChoiceClickListeners != null){
	    			 for(OnChoiceClickListener mOnChoiceClickListener:mOnChoiceClickListeners){
	    				 mOnChoiceClickListener.onClicked(which);
	    			 }
	    		 }
			}
			
		  }).show();
	}
	
	public void updateBgForFriendcircle() {
		Context dialogContext = new ContextThemeWrapper(mContext, android.R.style.Theme_Light);
		String[] array = { mContext.getString(R.string.fc_img_camera), mContext.getString(R.string.fc_img_album)};
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1, array);
		AlertDialog.Builder mDialog = new AlertDialog.Builder(dialogContext);
		mDialog.setTitle(mContext.getString(R.string.fc_change_cover));
		mDialog.setSingleChoiceItems(adapter,-1,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				 dialog.dismiss();
				 if(!TextUtils.isEmpty(fromViewName)){
					 mContext.getIntent().putExtra("fromViewName", fromViewName);
				 }
	    		 switch (which){ 
	    		 	case 0:
	    		 		try{
	    		 			Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    				captureFilePath = Environment.getExternalStorageDirectory() + "/" + Constants.LOG_TAG + "/oncon/photos/"+"self_"+System.currentTimeMillis()+".jpg";
		    				File out = new File(captureFilePath);
		    				File outParent = out.getParentFile();
		    				if(!outParent.exists()){
		    					outParent.mkdirs();
		    				}
		    				uri = Uri.fromFile(out);
		    				intent2.putExtra(MediaStore.EXTRA_OUTPUT,uri);
		    				mContext.startActivityForResult(intent2, Constants.CAMERA_RESULT_CODE);
	    		 		}catch(Exception e){
	    		 			mContext.toastToMessage(mContext.getString(R.string.camera) + mContext.getString(R.string.fail));
	    		 		}
		                break;
	    		 	case 1:
	    		 		try{
	    		 			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
	    		 			intent.setType("image/*");
	    		 			mContext.startActivityForResult(intent, Constants.GALLERY_RESULT_CODE);
	    		 		}catch(Exception e){
	    		 			mContext.toastToMessage(mContext.getString(R.string.camera) + mContext.getString(R.string.fail));
	    		 		}
	    		 		break;
	    		 	case 2:
	    		 		break;
	    		 }
	    		 if(mOnChoiceClickListeners != null){
	    			 for(OnChoiceClickListener mOnChoiceClickListener:mOnChoiceClickListeners){
	    				 mOnChoiceClickListener.onClicked(which);
	    			 }
	    		 }
			}
			
		  }).show();
	}
	
	public String getFromViewName() {
		return fromViewName;
	}

	public void setFromViewName(String fromViewName) {
		this.fromViewName = fromViewName;
	}
	
	public void addOnChoiceClickListener(OnChoiceClickListener onChoiceClickListener){
		if(mOnChoiceClickListeners == null){
			mOnChoiceClickListeners = new ArrayList<OnChoiceClickListener>();
		}
		mOnChoiceClickListeners.add(onChoiceClickListener);
	}
	public void addOnCancelClick(OnCancelListener onCancelListener){
		this.onCancelListener = onCancelListener;
	}
	public interface OnChoiceClickListener{
		public void onClicked(int which);
	}
	
}
