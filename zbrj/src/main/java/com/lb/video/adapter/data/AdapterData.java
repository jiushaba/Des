package com.lb.video.adapter.data;

public class AdapterData{ 
	public long id;
	public String account;
	public String nick;
	public String videoID;
	public String videoTitle;
	public String msg;
	public long startTime = 0;
	@Override
	public boolean equals(Object o) {
		if(o instanceof AdapterData){
			if(((AdapterData)o).id == id)
				return true;
		}
		return false;
	}
	@Override
	public int hashCode() {
		Long l = new Long(id);
		return l.hashCode();
	}
	
}
