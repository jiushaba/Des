package com.lb.zbrj.net;

import com.lb.common.util.Constants;
import com.lb.common.util.DeviceUtils;
import com.lb.zbrj.data.CommentData;
import com.lb.zbrj.data.PersonData;

import android.content.Context;

public class NetIFUI_ZBRJ extends NetIFUI {

	private NetIF_ZBRJ ni ;
	public NetIFUI_ZBRJ(Context context, NetInterfaceListener listener) {
		super(context, listener);
		ni = new NetIF_ZBRJ(context);
	}

	//登录
	public void m1_login(final String username, final String password, final int timeout) {
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				NetInterfaceStatusDataStruct nsds = new NetInterfaceStatusDataStruct();
				if(timeout <= 5 * 1000){
					nsds.setStatus(Constants.RES_NET_ERROR);
					return nsds;
				}
				nsds = ni.m1_login(username, password, DeviceUtils.getUUID());
				return nsds;
			}
		});
	}
	
	//下发短信
	public void m1_get_verify(final String mobile) {
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				return ni.m1_get_verify(mobile);
			}
		});
	}
	
	//注册
	public void m1_reg(final String account, final String password, final String verifyCode) {
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				return ni.m1_reg(account, password, verifyCode);
			}
		});
	}
	
	public void m1_modify_pwd(final String account, final String password, final String verifyCode) {
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				return ni.m1_modify_pwd(account, password, verifyCode);
			}
		});
	}
	
	public void m1_get_personalInfo(final String mobile){
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				NetInterfaceStatusDataStruct nsds = ni.m1_get_personalInfo(mobile);
				return nsds;
			}
		});
	}
	
	public void m1_update_personalInfo(final PersonData person){
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				NetInterfaceStatusDataStruct nsds = ni.m1_update_personalInfo(person);
				return nsds;
			}
		});
	}
	
	public void m1_comment(final CommentData comment){
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				NetInterfaceStatusDataStruct nsds = ni.m1_comment(comment);
				return nsds;
			}
		});
	}
	
	public void m1_del_comment(final String comentVideoID, final String comentid){
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				NetInterfaceStatusDataStruct nsds = ni.m1_del_comment(comentVideoID, comentid);
				return nsds;
			}
		});
	}
	
	public void m1_cancel_blacklist(final String blackAccount){
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				NetInterfaceStatusDataStruct nsds = ni.m1_cancel_blacklist(blackAccount);
				return nsds;
			}
		});
	}
	
	public void m1_upload_image(final String image){
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				NetInterfaceStatusDataStruct nsds = ni.m1_upload_image(image);
				return nsds;
			}
		});
	}
	
	public void m1_cancel_save(final String videoID){
		showProgressDialog(new HandleInterface() {
			public NetInterfaceStatusDataStruct run() {
				NetInterfaceStatusDataStruct nsds = ni.m1_cancel_save(videoID);
				return nsds;
			}
		});
	}
	
}
