package com.lz.oncon.activity.friendcircle.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lb.common.util.Constants;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.fc.selectimage.Fc_PicConstants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;


public class Utils {

	public static final String THUMB_BIG = Environment
			.getExternalStorageDirectory()
			+ "/"
			+ Constants.PACKAGENAME
			+ "/pic/thumb0/";
	public static final String THUMB_SMALL = Environment
			.getExternalStorageDirectory()
			+ "/"
			+ Constants.PACKAGENAME
			+ "/pic/thumb1/";

	public static void showDialog(final Activity c) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle(c.getString(R.string.more_image_exit))
				.setPositiveButton(c.getString(R.string.confirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Fc_PicConstants.fc_selected_Pic_List.clear();
								c.setResult(-1004);
								c.finish();
							}
						})
				.setNegativeButton(c.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
	}

	@SuppressLint("DefaultLocale")
	public static boolean isImage(File fs, String name) {
		String nametemp = name.toLowerCase();
		if (!fs.isDirectory()) {
			if (nametemp.endsWith(".png")
					|| nametemp.endsWith(".jpg")
					|| nametemp.endsWith(".jpeg")
					|| nametemp.endsWith(".gif")) {
				return true;
			}
		}
		return false;
	}

	public static String getThumbnails(Context context, String imgPath) {
		String imgThumb = "";
		if (imgPath != null && !"".equals(imgPath)) {
			String[] arg1 = new String[] { "_id" };
			Cursor c1 = context.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arg1,
					"_data=?", new String[] { imgPath }, null);
			if (c1.moveToNext()) {
				String id = c1.getString(c1.getColumnIndex("_id"));

				String[] arg2 = new String[] { Thumbnails.DATA };
				Cursor c2 = context.getContentResolver().query(
						MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
						arg2, "image_id=?", new String[] { id }, null);
				if (c2.moveToNext()) {
					imgThumb = c2.getString(c2.getColumnIndex(Thumbnails.DATA));
				}
				
				if (c2 != null) {
					c2.close();
				}
				
			}
			if (c1 != null) {
				c1.close();
			}
		}
		return imgThumb;
	}

	public static void writeImageToSDCard(String path, Bitmap bitmap) {
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream fos;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			fos = new FileOutputStream(f);
			if (bitmap != null) {
				bitmap.compress(CompressFormat.JPEG, 100, baos);
				byte[] b = baos.toByteArray();
				fos.write(b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
