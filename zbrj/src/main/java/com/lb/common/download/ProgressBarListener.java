package com.lb.common.download;

public interface ProgressBarListener {

	// 得到文件的长度
	public void getMax(int size);

	// 每次下载的数据量
	public void getDownload(int size, int flag);// flag 如果是0 就表示是线程的下载更新 ，
												// 如果是1就表示 之前已经下载的更新

}
