package com.lz.oncon.app.im.util;

import java.util.Comparator;

import android.text.TextUtils;

import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImData;

public class CompareIMThread implements Comparator<String> {

	@Override
	public int compare(String id0, String id1) {
		try{
			if(TextUtils.isEmpty(id0))return 1;
			if(TextUtils.isEmpty(id1))return -1;
			IMThreadData thread0 = ImData.getInstance().getDatas().get(id0);
			if(thread0 == null)return 1;
			IMThreadData thread1 = ImData.getInstance().getDatas().get(id1);
			if(thread1 == null)return -1; 
			long time0 = thread0.getLastMessageTime();
			long time1 = thread1.getLastMessageTime();
			if(ImData.getInstance().hasSetTopChat(id0)){
				if(ImData.getInstance().hasSetTopChat(id1)){//线程1线程2均置顶
					if(time0 > time1){
						return -1;
					}else if(time0 == time1){
						return 0;
					}else{
						return 1;
					}
				}else{//线程1置顶,线程2未置顶
					return -1;
				}
			}else if(ImData.getInstance().hasSetTopChat(id1)){//线程1未置顶,线程2置顶
				return 1;
			}else{//线程1线程2均未置顶
				if(time0 > time1){
					return -1;
				}else if(time0 == time1){
					return 0;
				}else{
					return 1;
				}
			}
		}catch(Exception e){
			return 1;
		}
	}

}
