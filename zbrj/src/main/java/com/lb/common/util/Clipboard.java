package com.lb.common.util;

import android.content.Context;
import android.text.ClipboardManager;

@SuppressWarnings("deprecation")
public class Clipboard {
	
	public static String getText(Context context){
		ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if(clip.getText()!=null){
			return clip.getText().toString();
		}
		return null;
	}
	
	public static void setText(Context context,String text){
		ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(text);
	}
	
	public static boolean canPaste(Context context){
		ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if(clip.getText()!=null){
			return true;
		} else {
			return false;
		}
	}

	 

	/**
	* 实现粘贴功能
	* 
	* @param context
	* @return
	*/
	public static String paste(Context context) {
	   android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
	            return clipboard.getText().toString().trim();

	}

	 


	
}
