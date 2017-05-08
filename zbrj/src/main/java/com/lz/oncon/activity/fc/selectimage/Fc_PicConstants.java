package com.lz.oncon.activity.fc.selectimage;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;

import com.lz.oncon.activity.friendcircle.Source_Dynamic;

public class Fc_PicConstants {
	//朋友圈选择集合,进来时候需要清空
	public static LinkedHashMap<String, ImageItem> fc_selected_Pic_List = new LinkedHashMap<String, ImageItem>();
	public static ArrayList<ImageItem> selectlist = new ArrayList<ImageItem>();//准备发送的图片列表
	public static Source_Dynamic source_Dynamic = null;//发图回传对象
	public static String et_content="";//输入文字暂时保存

	
	/**
	 * 准备发送的图片列表
	 * @return
	 */
	public static List<ImageItem> getImagesToSend(ArrayList<ImageItem> selectlist) {
		if(selectlist == null){
			selectlist =new ArrayList<ImageItem>();
		}
		if (fc_selected_Pic_List != null) {
			Iterator<Entry<String, ImageItem>> itr = fc_selected_Pic_List.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, ImageItem> entry = (Map.Entry<String, ImageItem>) itr.next();
				ImageItem iItem = entry.getValue();
				if(!TextUtils.isEmpty(iItem.imagePath)){
					File f = new File(iItem.imagePath);
					if(f.exists()){
						selectlist.add(iItem);
					}
				}
			}
		}
		return selectlist;
	}
	
	/**
	 * 获取选中的图片对象集合
	 * @return
	 */
	public static List<ImageItem> getSelectImageItemList() {
		List<ImageItem> tmpList = null;
		if (fc_selected_Pic_List != null&&fc_selected_Pic_List.size()>0) {
			tmpList = new ArrayList<ImageItem>();
			Iterator<Entry<String, ImageItem>> itr = fc_selected_Pic_List.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, ImageItem> entry = (Map.Entry<String, ImageItem>) itr.next();
				ImageItem iItem = entry.getValue();
				if(iItem!=null){
					tmpList.add(iItem);
				}
			}
		}
		return tmpList;
	}
}
