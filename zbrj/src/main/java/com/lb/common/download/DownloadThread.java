package com.lb.common.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

import com.lz.oncon.data.DownloadInfo;

public class DownloadThread extends Thread {

	// 下载线程的id
	private int threadid;
	// 每条线程的下载量
	private int block;
	// 文件下载的路径
	private String path;
	// 保存的文件
	private File file;
	// 下载的监听器
	private ProgressBarListener listener;

	// 下载的范围
	private int startposition;

	private int endposition;

	private DownloadService service;
	private DownloadManager manager;

	public DownloadThread(int threadid, int block, String path, File file, ProgressBarListener listener, Context context, DownloadManager manager) {
		super();
		this.threadid = threadid;
		this.block = block;
		this.path = path;
		this.file = file;
		this.listener = listener;
		this.manager = manager;

		startposition = threadid * block;
		endposition = (threadid + 1) * block;

		service = new DownloadService(context);
	}

	@Override
	public void run() {
		super.run();
		InputStream is = null;
		RandomAccessFile acf = null;
		try {

			// 查询线程已经下载的数据量
			int size = service.getDownloadSize(new DownloadInfo(threadid, path, 0));
			startposition = startposition + size;

			acf = new RandomAccessFile(file, "rwd");
			acf.seek(startposition);

			// 执行下载操作 http
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			// 给定下载范围
			conn.setRequestProperty("Range", "bytes=" + startposition + "-" + endposition);

			// 不需要对请求码进行再次判断
			is = conn.getInputStream();
			byte[] buffer = new byte[8192];
			int len = 0;
			int count = size;
			DownloadInfo info = new DownloadInfo();
			info.setPath(path);
			info.setThreadid(threadid);
			while ((len = is.read(buffer)) != -1) {
				if (!manager.isDownload()) {
					return;
				}
				listener.getDownload(len, 0);
				// 写入数据
				acf.write(buffer, 0, len);
				count = count + len;
				info.setDownloadlength(count);
				service.update(info);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}

			if (acf != null) {
				try {
					acf.close();
				} catch (IOException e) {
				}
			}
		}

	}

}
