package com.lb.common.util.corpimage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lb.common.util.Constants;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import com.lb.common.util.Log;

public class CropUtil {
	/**
	 * 压缩图片并返回图片字节数据
	 * @param b		：被压缩的图片
	 * @param len	：指定被压缩后的最大宽度或高度
	 * @param size	:指定被压缩后的最大容量
	 * @return
	 */
	public static byte[] compressPhotoByte(Bitmap b, int len, int maxSize){
		int w = b.getWidth();
        int h = b.getHeight();
        float s;
        if(w<len && h<len){
        	s = 1;
        }
        if(w>h){
        	s = (float)len/w; 
        }else{
        	s = (float)len/h;
        }
    	Matrix matrix = new Matrix();  
        matrix.postScale(s, s);  
        //压缩图片
        Bitmap newB = Bitmap.createBitmap(b , 0, 0, w, h, matrix, false); 
        //将压缩后的图片转换为字节数组，如果字节数组大小超过200K，继续压缩
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int qt = 70;
        newB.compress(CompressFormat.JPEG, qt, bos);
        int size = bos.size();
        while(qt!=0 && size>maxSize){
        	if(qt<0)
        		qt = 0;
        	bos.reset();
        	newB.compress(CompressFormat.JPEG, qt, bos);
        	size = bos.size();
    		qt -= 10;
        }
        return bos.toByteArray();
	}
	
	/**
	 * 关闭IO流
	 * @param in
	 * @param out
	 */
	public static void closeIO(InputStream in, OutputStream out){
		try {
			if(in!=null)
				in.close();
			if(out!=null)
				out.close();
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * 缓存图片到本地存储卡
	 * @param in		：输入流
	 * @param nameKey	：文件名称
	 */
	public static File makeTempFile(Bitmap photo, String parentPath, String nameKey){
		//等比例压缩图片，将较长的一边压缩到600px一下，最大容量不超过200K
		byte[] tempData = CropUtil.compressPhotoByte(photo, 600, 200*1024);
		//将压缩后的图片缓存到存储卡根目录下（权限）
		File bFile = new File(parentPath, nameKey);
		FileOutputStream fos = null;
		try {
			if(bFile.exists()){
				bFile.delete();
			}
			fos = new FileOutputStream(bFile);
			fos.write(tempData);
			fos.flush();
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		} finally{
			CropUtil.closeIO(null, fos);
		}
		return bFile;
	}
}
