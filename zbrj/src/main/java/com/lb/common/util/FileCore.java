package com.lb.common.util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Vector;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import com.lb.common.util.Log;

import com.lb.common.network.HttpCoreApache;
import com.lb.common.util.Constants;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.application.MyApplication;

public class FileCore {
	
	/**
	 * 在data下创建zip文件或者其他格式的文件
	 * @param fileName 需要创建的文件名
	 * @return 返回已创建的文件
	 * @throws IOException 
	 */
	public static  File createFile(String fileName){
		String file = fileName;
		File getFile = null;
		if (file != null) {
			getFile = new File(file);
			File parentFile = getFile.getParentFile();
			if(parentFile==null){
				return null;
			}else if(!parentFile.exists()){
				parentFile.mkdirs();
			}
			if (!getFile.exists()) {
				try {
					getFile.createNewFile();
				} catch (IOException e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
			}
		}
		return getFile;
		//return null;
	}

	/**
	 * 创建文件目录
	 * @param dirPath  目录路径
	 * @return
	 */
	public static boolean createDir(String dirPath) {
		File dirFile = null;
		String dir = dirPath;
		if (dirPath != null) {
			dirFile = new File(dir);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * 将流写入文件
	 * @param inputData 服务器传输的所有数据
	 * @param fileName  写入数据的文件
	 * @param range   上次已经接收到的数据大小
	 */
	public static boolean writeBytes (byte[] inputData, String fileName, boolean append) {
		/*
		 * 1.首先把String转换成流
		 * 2.打开文件
		 * 3.向文件中写入流
		 */
		FileOutputStream fileOutput = null;
		ByteArrayInputStream in = null;
		if (null != fileName) {
			try {
				//打开文件
				//创建文件输出流
				File file = FileCore.createFile(fileName);
				fileOutput = new FileOutputStream(file,append);
				//创建byteArray输入流
				in = new ByteArrayInputStream(inputData);
				//读流并将流写入文件
				byte[] temp = new byte[1024];
				int size = 0;
				while ((size = in.read(temp)) != -1) {
					fileOutput.write(temp, 0, size);
					fileOutput.flush();
				}
				//关闭输入流和输出流
				fileOutput.close();
				in.close();
				return true;
			} catch (FileNotFoundException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
			finally{
				try {
					if (null != fileOutput) 
						fileOutput.close();
					if (null != in) 
						in.close();
				} catch (IOException e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
			}
		}
		return false;
	}
	
	/**
	 * 向文件中写入流
	 * @param inputData
	 * @param fileName
	 * @return  是否下载完成
	 */
	public static boolean writeInputStream(InputStream inputData, String fileName){
		/*
		 * 1.首先把String转换成流
		 * 2.打开文件
		 * 3.向文件中写入流
		 */
		int a = 0;
		FileOutputStream fileOutput = null;
		if (null != fileName && null != inputData) {
			try {
				//打开文件
				//创建文件输出流
				File file = FileCore.createFile(fileName);
				long fileLength = file.length();
				long contentLength = HttpCoreApache.getContentLength();
				if (contentLength<1) {
					return true;
				}
//				Log.i(Constants.LOG_TAG,String.valueOf(fileLength)+" "+String.valueOf(contentLength)+" "+String.valueOf(fileLength != contentLength));
				if (fileLength >= 0 && contentLength >= 0 && fileLength != contentLength) {
					fileOutput = new FileOutputStream(file, true);
					byte[] temp = new byte[1024];
					int size = 0;
					while ((size = inputData.read(temp)) != -1) {
						fileOutput.write(temp, 0, size);
						fileOutput.flush();
						a+=size;
					}
				}
				//关闭输入流和输出流
				fileOutput.close();
				inputData.close();
//				Log.e(Constants.LOG_TAG, "" + a);
				return true;
			} catch (FileNotFoundException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
			finally{
				try {
					if (null != fileOutput) 
						fileOutput.close();
					if (null != inputData) 
						inputData.close();
				} catch (IOException e) {
					Log.e(Constants.LOG_TAG, e.getMessage(), e);
				}
			}
		} 
		return false;
	}
	
	/**
	 * 获取文件的大小
	 * @param fileName  文件名称
	 * @return
	 */
	public static long getFileSize(String fileName) {
		long fileLength = 0l;
		if (null != fileName) {
			//根据传入的文件名打开文件
			File file = FileCore.createFile(fileName);
			fileLength = file.length();
		}
		return fileLength;
	}
	
	private static File PRO_DIR = new File(Environment.getExternalStorageDirectory().toString() + "/oncon");
	/**
	 * 获取assset 里面的apk 文件
	 * @param mContext
	 * @return
	 */
	public static File getAssetFile(Context mContext) {
		AssetManager asset = mContext.getAssets();
		try {
			String[] array = asset.list("app");
			for (int i = 0; i < array.length; i++) {
				if (array[i].endsWith(".apk")) {
					File f = null;
					PRO_DIR.mkdirs();
					InputStream is = asset.open("app/"+array[i]);
					f = new File(PRO_DIR, array[i]);
					f.createNewFile();
					FileOutputStream fOut = new FileOutputStream(f);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fOut.write(buffer, 0, len);
					}
					fOut.flush();
					is.close();
					fOut.close();
					return f;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取assset 里面的FACE目录下的 文件
	 * @param mcon
	 * @param facePath
	 * @param faceName
	 * @return
	 */
	public static File getAssetFile(Context mcon,String facePath,String faceName) {
		AssetManager asset = mcon.getAssets();
		try {
			String[] array = asset.list(facePath);
			for (int i = 0; i < array.length; i++) {
				if (array[i].endsWith(faceName)) {
					File f = null;
					PRO_DIR.mkdirs();
					InputStream is = asset.open(facePath+"/"+array[i]);
					f = new File(PRO_DIR, array[i]);
					f.createNewFile();
					FileOutputStream fOut = new FileOutputStream(f);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fOut.write(buffer, 0, len);
					}
					fOut.flush();
					is.close();
					fOut.close();
					return f;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 写流到指定文件
	 * @param fileName
	 * @param input
	 * @return
	 */
	public static boolean write2SDFromInput(String faceName, InputStream input) {
		boolean result = false;
		File file = null;
		OutputStream output = null;
		try {
			if (DeviceUtils.isExternalStorageWriteable()) {
				if (ReadStorageUtil.readSDcard() < 10 * 1024 * 1024L) {
					return result;
				}
			} else {
				if (ReadStorageUtil.readFileSystem() < 10 * 1024 * 1024L) {
					return result;
				}
			}
			// // 判断存储路径是否存在，不存在则创建
			File f = new File(IMConstants.PATH_FACE_PICTURE);
			if (!f.exists()) {
				f.mkdirs();
			}
			String path = IMConstants.PATH_FACE_PICTURE + faceName;
			File dirPath = new File(path);
			if (!dirPath.exists()) {
				output = new FileOutputStream(file);
				byte buffer[] = new byte[4 * 1024];
				while ((input.read(buffer)) != -1) {
					output.write(buffer);
				}
				result = true;
				output.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 把表情写入存储卡
	 * @param faceName
	 * @param draw
	 */
	public static boolean writeFaceToLocal(String faceName, Drawable draw) {
		boolean isSuc=false;
		if (DeviceUtils.isExternalStorageWriteable()) {
			if (ReadStorageUtil.readSDcard() < 10 * 1024 * 1024L) {
			}
		} else {
			if (ReadStorageUtil.readFileSystem() < 10 * 1024 * 1024L) {
			}
		}
		// // 判断存储路径是否存在，不存在则创建
		File f = new File(IMConstants.PATH_FACE_PICTURE);
		if (!f.exists()) {
			f.mkdirs();
		}
		String path = IMConstants.PATH_FACE_PICTURE+faceName;
		File dirPath = new File(path);
		if (!dirPath.exists()) {
			try {
				ImageThumbUtil.getInstance().writeImageToLocal(path,
						ImageThumbUtil.getInstance().drawableToBitmap(draw));
				isSuc =true;
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, e.getMessage(), e);
			}
		}
		return isSuc;
	}
	
	/**
	 * 把表情写入存储卡
	 * @param faceName
	 * @param draw
	 */
	public static boolean writeSocialToLocal(String faceName, Drawable draw, int platform) {
		boolean isSuc=false;
		if (DeviceUtils.isExternalStorageWriteable()) {
			if (ReadStorageUtil.readSDcard() < 10 * 1024 * 1024L) {
			}
		} else {
			if (ReadStorageUtil.readFileSystem() < 10 * 1024 * 1024L) {
			}
		}
		// // 判断存储路径是否存在，不存在则创建
		File f = new File(SocialPicPath.getSaveFilePath(platform));
		if (!f.exists()) {
			f.mkdirs();
		}
		String path = SocialPicPath.getSaveFilePath(platform)+faceName;
		File dirPath = new File(path);
		if (!dirPath.exists()) {
			try {
				ImageThumbUtil.getInstance().writeImageToLocal(path,
						ImageThumbUtil.getInstance().drawableToBitmap(draw));
				isSuc =true;
			} catch (IOException e) {
			}
		}
		return isSuc;
	}
	
	/**
	 * 把assest face 文件夹下的文件拷贝到存储卡
	 * @param mcon
	 * @param facePath
	 */
	public static void writeAssetToFile(Context mcon,String facePath) {
		AssetManager asset = mcon.getAssets();
		try {
			String[] array = asset.list(facePath);
			// // 判断存储路径是否存在，不存在则创建
			File f = new File(IMConstants.PATH_FACE_PICTURE);
			if (!f.exists()) {
				f.mkdirs();
			}
			InputStream is = null;
			FileOutputStream fOut = null;
			for (int i = 0; i < array.length; i++) {
				final String path = IMConstants.PATH_FACE_PICTURE + array[i];
				File dirPath = new File(path);
				if (!dirPath.exists()) {
					try {
						is = asset.open(facePath+"/"+array[i]);
						File file = FileCore.createFile(path);
						fOut = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = is.read(buffer)) != -1) {
							fOut.write(buffer, 0, len);
						}
					} catch (IOException e) {
						Log.e(Constants.LOG_TAG, e.getMessage(), e);
					}
				}
				if(fOut!=null){
					fOut.flush();
					fOut.close();
				}
				if(is!=null){
					is.close();
				}
					
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 判断是否存在存储空间
	 * @param zipsize  本地文件大小
	 * @param storageSize  存储空间大小
	 * @return  是否存在存储空间
	 * @throws IOException
	 */
	public static boolean isHaveStorage(long zipsize, long storageSize) throws IOException {
		if (zipsize >= 0 && storageSize >= 0) {
			if (zipsize >= storageSize) {
				throw new IOException("no space exists on sdcard");
			}
		}
		return true;
	}
	
	public static String getResult(InputStream inputStream) {
		byte[] temp_small=new byte[1024*1024];
		StringBuffer temp_string=new StringBuffer();
		int c=0;
		int i=0;
		try {
			while ((c=inputStream.read())!=-1) {
				if (i<1024*1024) {
					temp_small[i]=(byte)c;
					i++;
				}else {
					temp_string.append(new String(temp_small));
//					Log.i(Constants.LOG_TAG, "str are "+temp_string.toString());
					i=0;
				}
			}
			if (i<1024*1024) {
				temp_string.append(new String(temp_small));
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		return temp_string.toString();
	}
	
	public static Vector<String> getFilesName(File zipfile,String type) {
		File file=new File(zipfile.getAbsolutePath());
//		Log.i(Constants.LOG_TAG, zipfile.getAbsolutePath());
		String[] files=file.list();
		Vector<String> names=new Vector<String>();
		if(files!=null){
			for (String name : files) {
//				Log.e(Constants.LOG_TAG, "filename:"+name);
				if (name.endsWith(type)&&!names.contains(name)) {
//					Log.e(Constants.LOG_TAG, "add in names");
					names.add(name);
				}
			}
		}	
		return names;
	}	
	
	public static void deleteFile(File file,String fileType){
		if(file!=null&&file.exists()){
			if(file.isFile()&&file.getName().endsWith(fileType)){
				file.delete();
			}else if(file.isDirectory()){
				File files[] = file.listFiles();
				for(int i=0;i<files.length;i++){
					deleteFile(files[i],fileType);
				}
			}
		}else{
		}
	}
	
	public static InputStream readFile(String filePath) throws FileNotFoundException {
		File file=new File(filePath);
		if(file.exists()){
		InputStream inputStream=new FileInputStream(file);
			return inputStream;
		}else{
			return null;
		}
	}
	
	/**
	 * 从RAW 中读取文件
	 * @param mContext
	 * @param rawString
	 * @return
	 */
	public String getFromRaw(Context mContext,String rawString) {
//		try {
//
//			InputStreamReader inputReader = new InputStreamReader(mContext.getResources().openRawResource(R.raw.rawString));
//
//			BufferedReader bufReader = new BufferedReader(inputReader);
//
//			String line = "";
			String Result = "";
//
//			while ((line = bufReader.readLine()) != null)
//				Result += line;
			return Result;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 从Assets 中读取文件
	 * @param mContext
	 * @param fileName exprfile.txt
	 * @return
	 */
	public static String getFromAssets(String fileName) {
		StringBuffer Result = new StringBuffer();
		try {
			InputStreamReader inputReader = new InputStreamReader(MyApplication.getInstance().getResources().getAssets().open(fileName));

			BufferedReader bufReader = new BufferedReader(inputReader);

			String line = "";

			while ((line = bufReader.readLine()) != null)
				Result.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.toString();
	}
	
	/**
	 * 删除文件
	 * @param filepath
	 * @return
	 */
	public static boolean deleteFile(String filepath){
		boolean result = false;
		if(filepath!=null){
			File file =  new File(filepath);
			if(file.exists()){
				file.delete();
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * 判断文件是否存在
	 * @param filepath
	 * @return
	 */
	public static boolean isExists(String filepath){
		boolean result = false;
		if(filepath!=null){
			File file =  new File(filepath);
			if(file.exists()){
				result = true;
			}
		}
		return result;
	}
	
	public static File[] listSortedFiles(File dirFile) {
    	assert dirFile.isDirectory();

        File[] files = dirFile.listFiles(new HideFileFilter());
        
        FileWrapper [] fileWrappers = new FileWrapper[files.length];
        for (int i=0; i<files.length; i++) {
            fileWrappers[i] = new FileWrapper(files[i]);
        }
        
        Arrays.sort(fileWrappers);
        
        File []sortedFiles = new File[files.length];
        for (int i=0; i<files.length; i++) {
            sortedFiles[i] = fileWrappers[i].getFile();
        }
        
        return sortedFiles;
    }
	
	public static String getSuffix(String filename){
		int dix = filename.lastIndexOf('.');
		if(dix<0){
			return "";
		} else{
			return filename.substring(dix+1);
		}
	}
	
	/**
     * 递归删除文件和文件夹
     * @param file    要删除的根目录
     */
    public static void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
    
    /**
     * 递归删除文件和文件夹
     * @param file    要删除的根目录
     */
    public static void RecursionDeleteFileByTime(File file,long time){
    	long lastModifiedTime = file.lastModified();
    	
    	if(file.isFile() && lastModifiedTime < time){
    		file.delete();
    		return;
    	}
    	if(file.isDirectory()){
    		File[] childFile = file.listFiles();
    		if(childFile == null || childFile.length == 0){
    			file.delete();
    			return;
    		}
    		for(File f : childFile){
    			RecursionDeleteFileByTime(f,time);
    		}
    		if (lastModifiedTime < time) {
    			file.delete();
			}
    	}
    }
}

@SuppressWarnings("rawtypes")
class FileWrapper implements Comparable {
    /** File */
    private File file;
    
    public FileWrapper(File file) {
        this.file = file;
    }
     
    public int compareTo(Object obj) {
        assert obj instanceof FileWrapper;
        
        FileWrapper castObj = (FileWrapper)obj;
                
        if (this.file.getName().compareTo(castObj.getFile().getName()) > 0) {
            return 1;
        } else if (this.file.getName().compareTo(castObj.getFile().getName()) < 0) {
            return -1;
        } else {
            return 0;
        }
    }
    
    public File getFile() {
        return this.file;
    }
}

class HideFileFilter implements FileFilter{

	@Override
	public boolean accept(File pathname) {
		if(pathname.getName().startsWith(".")){
			return false;
		}
		return true;
	}
	
}