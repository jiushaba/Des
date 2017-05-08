package com.lz.oncon.app.im.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.lz.oncon.data.FriendData;

public class IMContactChooserData {
	
	private LinkedHashMap<String, Object> mSelectedMembers;

	private static IMContactChooserData instance = null;
	
	public static IMContactChooserData getInstance(){
		if(instance==null){
			instance = new IMContactChooserData();
		}
		return instance;
	}
	
	private IMContactChooserData() {
		mSelectedMembers = new LinkedHashMap<String, Object>();
		mListeners = new ArrayList<IMContactChooserData.OnIMContactChooserDataChangedListener>();
	}
	
	public void addMember(String onconid,Object object){
		if(onconid!=null && !mSelectedMembers.containsKey(onconid)){
			mSelectedMembers.put(onconid,object);
			for(OnIMContactChooserDataChangedListener listener:mListeners){
				listener.onDataChanged();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public int removeMember(String onconid){
		if(onconid!=null && mSelectedMembers.containsKey(onconid)){
			int count = 0;
			 for (Iterator it =  mSelectedMembers.keySet().iterator();it.hasNext();) 
			   { 
				  Object key = it.next(); 
				  if(key.equals(onconid)){
					  mSelectedMembers.remove(onconid);
					  break;
				  }
				  count++;
			   } 
			for(OnIMContactChooserDataChangedListener listener:mListeners){
				listener.onDataChanged();
			}
			return count;
		}
		return -1;
	}
	
	public void removeAllMembers(){
		mSelectedMembers.clear();
		for(OnIMContactChooserDataChangedListener listener:mListeners){
			listener.onDataChanged();
		}
	}
	
	public int getMemberCount(){
		return mSelectedMembers.size();
	}

	public HashMap<String,Object> getMembers(){
		return mSelectedMembers;		
	}
	
	public HashMap<String, String> getMemberNumberAndNames(){
		HashMap<String, String> members = new HashMap<String, String>();
		Iterator<String> numbers = mSelectedMembers.keySet().iterator();
        while(numbers.hasNext()){
        	String number = numbers.next();
        	Object object = mSelectedMembers.get(number);
        	if(object instanceof FriendData){
        		members.put(number, ((FriendData)object).getContactName());
			}
        }
		return members;		
	}
	
	/**
	 * 获取选中的手机号码集合
	 * @return
	 */
	public ArrayList<String> getMemberNumber(){
		ArrayList<String> members = new ArrayList<String>();
		Iterator<String> numbers = mSelectedMembers.keySet().iterator();
        while(numbers.hasNext()){
        	String number = numbers.next();
        	members.add(number);
        }
		return members;		
	}
	
	public boolean isSelected(String onconid){
		if(onconid!=null && mSelectedMembers.containsKey(onconid)){
			return true;
		}
		return false;
	}

	
	public interface OnIMContactChooserDataChangedListener{
		public void onDataChanged();
	}
	private ArrayList<OnIMContactChooserDataChangedListener> mListeners;
	public void addOnIMContactChooserDataChangedListener(OnIMContactChooserDataChangedListener listener){
		mListeners.add(listener);
	}
	public void removeOnIMContactChooserDataChangedListener(OnIMContactChooserDataChangedListener listener){
		mListeners.remove(listener);
	}
	public void clearOnIMContactChooserDataChangedListener(){
		mListeners.clear();
	}
	
	/**
	 * 清理所有数据
	 */
	public void clear(){
		removeAllMembers();
		clearOnIMContactChooserDataChangedListener();
	}
	
	/**
	 * 返回给MAS的对象
	 * @return
	 */
	public SelectMembers getMasInfo(){
		SelectMembers sm = new SelectMembers();
		String mobile = "";
		String editText = "";
		HashMap<String, Object> members= IMContactChooserData.getInstance()
				.getMembers();
		HashMap<String, String> namesAndMobiles = new HashMap<String, String>();
		
		if (members != null) {
			Iterator<Entry<String, Object>> it = members.entrySet().iterator();
	        while(it.hasNext()){
	        	Entry<String, Object> d = it.next();
	        	String member = d.getKey();
	        	if(d.getValue() instanceof FriendData){
	        		member=((FriendData)d.getValue()).getContactName();
	        	}
	        	editText = editText.concat(member).concat(",");
	        	mobile = mobile.concat(d.getKey()).concat(",");
	        	namesAndMobiles.put(d.getKey(), member);
	        }
		}
		sm.setMobile(mobile);
		sm.setName(editText);
		sm.setNamesAndMobiles(namesAndMobiles);
		return sm;
	}

}


