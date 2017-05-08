package com.lz.oncon.activity.friendcircle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.lb.zbrj.data.VideoData;
import com.lb.common.util.StringUtils;
import com.lz.oncon.application.MyApplication;
import com.lz.oncon.data.AccountData;
import com.lz.oncon.data.db.FCHelper;

@SuppressWarnings("rawtypes")
public class SortDynamicByDate implements Comparator {
	public int compare(Object o1, Object o2) {
		if(o1==null||o2==null){
			return 0;
		}
		VideoData s1 = (VideoData) o1;
		VideoData s2 = (VideoData) o2;
		if(s1==null||s2==null){
			return 0;
		}
		return StringUtils.repNull(s2.dateTime).compareTo(StringUtils.repNull(s1.dateTime));
	}
	
	/**
	 * 获取未读消息数据
	 * @return
	 */
	public static Fc_NoReadMessageBean getNoReadMessage(){
		Fc_NoReadMessageBean fnM = new Fc_NoReadMessageBean();
		String noti_num = "";
		String mobile = "";
		final ArrayList<VideoData> list = new ArrayList<VideoData>();
		HashMap<String, VideoData> sdMap = new FCHelper(AccountData.getInstance().getUsername()).getAll_NoReadFcNoti();
		if (sdMap != null && sdMap.size() > 0) {
			noti_num = String.valueOf(sdMap.size());
				Iterator<String> iterator = sdMap.keySet().iterator();
				boolean isFirst = true;
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					VideoData sd_temp = (VideoData)sdMap.get(key);
					if(sd_temp!=null){
						VideoData source_dynamic = (VideoData) FriendCircleCacheUtil.readObject(key, MyApplication.getInstance());
						if (source_dynamic != null) {
							list.add(source_dynamic);
						}
					}
				}
				Collections.sort(list, new SortDynamicByDate());
				if(list!=null&&list.size()>0){
					fnM.list = list;
					fnM.mobile = list.get(0).operator;
					fnM.num = noti_num;
				}
		}
		return fnM;
	}

}
