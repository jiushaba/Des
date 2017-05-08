package com.lz.oncon.data;

public class LocInfoData {
	public static final String TYPE_PROVINCE="province";
	public static final String TYPE_DISTRICT="district";
	public static final String TYPE_CITY="city";
	
	private String locName;
	private String ID;
	private String type;

	public LocInfoData(String locName, String ID,String type) {
		this.locName = locName;
		this.type=type;
		if (TYPE_CITY.equals(type)) {
			this.ID=ID.substring(0,4);
		}else if (TYPE_PROVINCE.equals(type)) {
			this.ID=ID.substring(0,2);
		}
	}

	public String getLocName() {
		return locName;
	}

	public void setLocName(String locName) {
		this.locName = locName;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
