package com.lb.common.download;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

import com.lz.oncon.data.DownloadInfo;

/**
 * 多线程下载器
 * 
 */
public class DownloadManager {

	// 下载的线程数
	private final static int threadsize = 3;
	private DownloadService serivce;
	private Context context;

	private boolean isDownload = true;

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public DownloadManager(Context context) {
		this.context = context;
		serivce = new DownloadService(context);
	}

	public boolean download(String path, File dir, ProgressBarListener listener) throws Exception {
		// 使用http协议
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			int filesize = conn.getContentLength();

			// 设置进度条的最大值
			listener.getMax(filesize);

			if (!dir.exists()) {
				dir.mkdir();
			}

			// 生成一个和服务器大小一样的文件
			File file = new File(dir, getFileName(path));

			// 判断是否存在下载记录
			boolean isExist = serivce.isExist(path);
			if (isExist) {
				// 查询下载的总数据量
				int size = serivce.getDownloadSize(path);
				listener.getDownload(size, 1);
			} else {
				// 插入下载记录
				for (int i = 0; i < threadsize; i++) {
					serivce.save(new DownloadInfo(i, path, 0));
				}
			}

			// 每条线程的下载量
			int block = filesize % threadsize == 0 ? filesize / threadsize : filesize / threadsize + 1;

			// 开启线程进行下载操作
			for (int i = 0; i < threadsize; i++) {
				new DownloadThread(i, block, path, file, listener, context, this).start();
			}

			return true;
		} else {
			return false;
		}
	}

	// 得到文件的名称
	private String getFileName(String path) {
		String filename = path.substring(path.lastIndexOf("/") + 1);
		return filename;
	}

}
