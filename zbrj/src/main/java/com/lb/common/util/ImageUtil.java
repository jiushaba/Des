package com.lb.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import com.lb.common.util.Log;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.app.im.morepic.ThumbnailUtils;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.application.MyApplication;

public class ImageUtil {
	
	public static final String FAVORITE_SAVE_DIC = Environment.getExternalStorageDirectory()  + "/" + Constants.PACKAGENAME + "/OnconImages/";
	
	public static int[] getScreenWH(Activity a){
		DisplayMetrics dm = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return new int[]{dm.widthPixels, dm.heightPixels};
	}
	
	public static boolean scaleImage(String dstFile, String srcFile,
			int maxWidth, int maxLength) {
		try {
			if (dstFile == null || dstFile.equals("") || srcFile == null
					|| srcFile.equals("")) {
				return false;
			}

			BitmapFactory.Options opts = new BitmapFactory.Options();

			BitmapFactory.decodeFile(srcFile, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int destWidth = 0;
			int destHeight = 0;

			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth > srcHeight) {
				ratio = srcWidth / maxLength;
				destWidth = maxLength;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = srcHeight / maxLength;
				destHeight = maxLength;
				destWidth = (int) (srcWidth / ratio);
			}

			// 对图片进行压缩，是在读取的过程中进行压缩，而不是把图片读进了内存再进行压缩
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			Bitmap destBm = BitmapFactory.decodeFile(srcFile, newOpts);

			File file = new File(dstFile);
			if (!file.canWrite()) {
				return false;
			}
			FileOutputStream fos = new FileOutputStream(file);
			destBm.compress(CompressFormat.JPEG, 100, fos);
			fos.close();

			return true;
		} catch (Exception e) {
			return false;
		}

	}
	// 转换dip为px
	public static int convertDipToPx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	// 转换px为dip
	public static int convertPxToDip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static int computeSampleSize(BitmapFactory.Options options, 
	        int minSideLength, int maxNumOfPixels) { 
	    int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels); 

	    int roundedSize; 
	    if (initialSize <= 8 ) { 
	        roundedSize = 1; 
	        while (roundedSize < initialSize) { 
	            roundedSize <<= 1; 
	        } 
	    } else { 
	        roundedSize = (initialSize + 7) / 8 * 8; 
	    } 

	    return roundedSize; 
	} 
	
	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) { 
	    double w = options.outWidth; 
	    double h = options.outHeight; 

	    int lowerBound = (maxNumOfPixels == -1) ? 1 : 
	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels)); 
	    int upperBound = (minSideLength == -1) ? 128 : 
	            (int) Math.min(Math.floor(w / minSideLength), 
	            Math.floor(h / minSideLength)); 

	    if (upperBound < lowerBound) { 
	        // return the larger one when there is no overlapping zone. 
	        return lowerBound; 
	    } 

	    if ((maxNumOfPixels == -1) && 
	            (minSideLength == -1)) { 
	        return 1; 
	    } else if (minSideLength == -1) { 
	        return lowerBound; 
	    } else { 
	        return upperBound; 
	    } 
	}
	
	//获得圆角图片的方法 
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){ 

	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap 
	.getHeight(), Config.ARGB_8888); 
	Canvas canvas = new Canvas(output); 

	final int color = 0xff424242; 
	final Paint paint = new Paint(); 
	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
	final RectF rectF = new RectF(rect); 

	paint.setAntiAlias(true); 
	canvas.drawARGB(0, 0, 0, 0); 
	paint.setColor(color); 
	canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 

	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	canvas.drawBitmap(bitmap, rect, rect, paint); 

	return output; 
	} 


	public static void savePicToLocal(String path, Drawable draw) {
		if (DeviceUtils.isExternalStorageWriteable()) {
			if (ReadStorageUtil.readSDcard() < 10 * 1024 * 1024L) {
				return;
			}
		} else {
			if (ReadStorageUtil.readFileSystem() < 10 * 1024 * 1024L) {
				return;
			}
		}
		// // 判断存储路径是否存在，不存在则创建
		File f = new File(IMConstants.PATH_NEWS_PICTURE);
		if (!f.exists()) {
			f.mkdirs();
		}
		File dirPath = new File(path);
		if (!dirPath.exists()) {
			try {
				ImageThumbUtil.getInstance().writeImageToLocal(path, ImageThumbUtil.getInstance().drawableToBitmap(draw));
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
	}
	public static  String getFilePathFromResourceUri(Context c ,Uri uri) {
		if (uri == null) {
			return null;
		}
		ContentResolver resolver = c.getContentResolver();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			return cursor.getString(1);
		}
		return null;
	}
	
    public static Drawable createDrawable(Drawable d, Paint p) {
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap b = bd.getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(bd.getIntrinsicWidth(), bd.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(b, 0, 0, p); // 关键代码，使用新的Paint画原图，
        return new BitmapDrawable(MyApplication.getInstance().getResources(), bitmap);
    }
	
    public static void saveImage2Local(final BaseActivity activity, String srcPath, String toDirPath){
    	FileInputStream fis = null;
		FileOutputStream fos = null;
    	try {
			File inFile = new File(srcPath);
			fis = new FileInputStream(inFile);
			File outFile = new File(toDirPath + inFile.getName());
			File outDir = outFile.getParentFile();
			if(!outDir.exists()){
				outDir.mkdirs();
			}
			fos = new FileOutputStream(outFile);
			int length = -1;
            byte[] buf = new byte[1024];
            while ((length = fis.read(buf)) != -1){
            	fos.write(buf, 0, length);
            	fos.flush();
            }
            activity.toastToMessage(activity.getString(R.string.im_images_saveto) + outDir.getAbsolutePath());
            scanPhotos(outFile.getAbsolutePath(), activity); // 通知刷新图库
		} catch (FileNotFoundException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			activity.toastToMessage(R.string.im_imageshow_save_failed);
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} finally {
			try {
				if(fis!=null){
					fis.close();
				}
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e2) {
			}
		}
    }
    
    public static void saveImage2Local2(final BaseActivity activity, String srcPath, String toDirPath){
    	FileInputStream fis = null;
		FileOutputStream fos = null;
    	try {
			File inFile = new File(srcPath);
			fis = new FileInputStream(inFile);
			File outFile = new File(toDirPath);
			File outDir = outFile.getParentFile();
			if(!outDir.exists()){
				outDir.mkdirs();
			}
			fos = new FileOutputStream(outFile);
			int length = -1;
            byte[] buf = new byte[1024];
            while ((length = fis.read(buf)) != -1){
            	fos.write(buf, 0, length);
            	fos.flush();
            }
            scanPhotos(outFile.getAbsolutePath(), activity); // 通知刷新图库
		} catch (FileNotFoundException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
			activity.toastToMessage(R.string.im_imageshow_save_failed);
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} finally {
			try {
				if(fis!=null){
					fis.close();
				}
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e2) {
			}
		}
    }
    
    public static void scanPhotos(String filePath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
    
    /** 从给定路径加载图片*/  
    public static Bitmap loadBitmap(String imgpath) {  
        return BitmapFactory.decodeFile(imgpath);  
    }  
    
    /** 从给定的路径加载缩略图和获取图片的方向，并指定是否自动旋转方向*/  
    public static Bitmap loadBitmap(String imgpath, boolean adjustOritation) {  
        if (!adjustOritation) {  
        	return ThumbnailUtils.createImageThumbnail(imgpath, ThumbnailUtils.TARGET_SIZE_MINI_THUMBNAIL, false); 
        } else {  
            Bitmap bm = ThumbnailUtils.createImageThumbnail(imgpath, ThumbnailUtils.TARGET_SIZE_MINI_THUMBNAIL, false);
            int digree = 0;  
            ExifInterface exif = null;  
            try {  
                exif = new ExifInterface(imgpath);  
            } catch (IOException e) {  
                e.printStackTrace();  
                exif = null;  
            }  
            if (exif != null) {  
                // 读取图片中相机方向信息  
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
                        ExifInterface.ORIENTATION_UNDEFINED);  
                System.out.println("ori========="+ ori);
                // 计算旋转角度  
                switch (ori) {  
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_180:  
                    digree = 180;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_270:  
                    digree = 270;  
                    break;  
                default:  
                    digree = 0;  
                    break;  
                }  
            }  
            if (digree != 0) {  
                // 旋转图片  
                Matrix m = new Matrix();  
                m.postRotate(digree);  
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),  
                        bm.getHeight(), m, true);  
            }  
            return bm;  
        }  
    }
    
	/**
	 * Android 5.0 获取图片路径
	 * 
	 * @param uri
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 *            [url=home.php?mod=space&uid=7300]@return[/url] The value of
	 *            the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
    
}
