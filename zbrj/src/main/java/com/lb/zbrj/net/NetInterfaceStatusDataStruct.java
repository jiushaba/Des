/**
 * 网络返回状态的数据结构
 */
package com.lb.zbrj.net;

public class NetInterfaceStatusDataStruct {

	private String status;
	private String message;
	private Object obj;
	private Object objArr;
	private Object extension;

	public Object getExtension() {
		return extension;
	}

	public void setExtension(Object extension) {
		this.extension = extension;
	}

	public NetInterfaceStatusDataStruct() {
	}

	public NetInterfaceStatusDataStruct(String status, String message) {
		setStatus(status);
		setMessage(message);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObj() {
		if (obj != null) {
			return obj;
		} else if (objArr != null) {
			return objArr;
		} else {
			return null;
		}
	}

	/**
	 * 方便json串里多个list 处理
	 * @param obj
	 */
	public void setObj(Object... obj) {
		if (obj != null) {
			if (obj.length == 1) {
				this.obj = obj[0];
			} else {
				this.objArr = obj;
			}
		}
	}

}