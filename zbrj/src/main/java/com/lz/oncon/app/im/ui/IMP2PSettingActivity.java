package com.lz.oncon.app.im.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.SIXmppP2PInfo;
import com.lz.oncon.app.im.contact.ContactMsgCenterActivity;
import com.lz.oncon.app.im.data.ImData;
import com.lz.oncon.app.im.ui.view.MsgRoundAngleImageView;

public class IMP2PSettingActivity extends BaseActivity{

	private String mOnconId;
	private SIXmppP2PInfo mP2PInfo;
	private MsgRoundAngleImageView headIV;
	private ImageView newMsgNotiIV, msgNotiSoundIV, setTopChatIV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mOnconId = bundle.getString("onconid");
		} else {
			mOnconId = null;
		}
		
		initView();
		setValues();
		setListeners();
	}

	private void initView() {
		setContentView(R.layout.app_im_p2p_setting);

		newMsgNotiIV = (ImageView) findViewById(R.id.im_p2p_setting_Image_newMsgNoti);
		msgNotiSoundIV = (ImageView) findViewById(R.id.im_p2p_setting_Image_msgNotiSound);
		setTopChatIV = (ImageView) findViewById(R.id.im_p2p_setting_Image_setTopChat);
		headIV = (MsgRoundAngleImageView) findViewById(R.id.im_p2p_setting_head);
	}

	private void setListeners() {
	}

	private void setValues() {
		mP2PInfo = ImData.getInstance().p2p_query(mOnconId);
		if(mP2PInfo == null){
			mP2PInfo = new SIXmppP2PInfo();
		}
		newMsgNotiIV.setImageResource("1".equals(mP2PInfo.getPush()) ? R.drawable.btn_check_on_normal: R.drawable.btn_check_off_normal);
		if("1".equals(mP2PInfo.getPush())){
			msgNotiSoundIV.setEnabled(true);
		}else{
			msgNotiSoundIV.setEnabled(false);
		}
		msgNotiSoundIV.setImageResource("1".equals(mP2PInfo.getPush()) && "1".equals(mP2PInfo.getTone()) ? R.drawable.btn_check_on_normal: R.drawable.btn_check_off_normal);
		setTopChatIV.setImageResource("1".equals(mP2PInfo.getTop()) ? R.drawable.btn_check_on_normal: R.drawable.btn_check_off_normal);
		
		headIV.setMobile(mOnconId);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.common_title_TV_left:
			finish();
			break;
		case R.id.im_p2p_setting_Image_setTopChat:
			if("1".equals(mP2PInfo.getTop())){
				boolean flag = ImData.getInstance().setP2PAttributes(mOnconId, "top", "0");
				if(flag){
					setTopChatIV.setImageResource(R.drawable.btn_check_off_normal);
					ImData.getInstance().setTopChat(mOnconId, "0", false);
				}else{
					
				}
			}else{
				boolean flag = ImData.getInstance().setP2PAttributes(mOnconId, "top", "1");
				if(flag){
					setTopChatIV.setImageResource(R.drawable.btn_check_on_normal);
					ImData.getInstance().setTopChat(mOnconId, "1", false);
				}else{
					
				}
			}
			break;
		case R.id.im_p2p_setting_Image_newMsgNoti:
			if("1".equals(mP2PInfo.getPush())){
				boolean flag = ImData.getInstance().setP2PAttributes(mOnconId, "push", "0");
				if(flag){
					newMsgNotiIV.setImageResource(R.drawable.btn_check_off_normal);
					mP2PInfo.setPush("0");
					msgNotiSoundIV.setEnabled(false);
					msgNotiSoundIV.setImageResource(R.drawable.btn_check_off_normal);
				}else{
					
				}
				
			}else{
				boolean flag = ImData.getInstance().setP2PAttributes(mOnconId, "push", "1");
				if(flag){
					newMsgNotiIV.setImageResource(R.drawable.btn_check_on_normal);
					mP2PInfo.setPush("1");
					msgNotiSoundIV.setEnabled(true);
					if("1".equals(mP2PInfo.getTone())){
						msgNotiSoundIV.setImageResource(R.drawable.btn_check_on_normal);
					}else{
						msgNotiSoundIV.setImageResource(R.drawable.btn_check_off_normal);
					}
				}else{
					
				}
			}
			break;
		case R.id.im_p2p_setting_Image_msgNotiSound:
			if("1".equals(mP2PInfo.getTone())){
				boolean flag = ImData.getInstance().setP2PAttributes(mOnconId, "tone", "0");
				if(flag){
					msgNotiSoundIV.setImageResource(R.drawable.btn_check_off_normal);
					mP2PInfo.setTone("0");
				}else{
					
				}
				
			}else{
				boolean flag = ImData.getInstance().setP2PAttributes(mOnconId, "tone", "1");
				if(flag){
					msgNotiSoundIV.setImageResource(R.drawable.btn_check_on_normal);
					mP2PInfo.setTone("1");
				}else{
					
				}
			}
			break;
		case R.id.im_p2p_setting_add:
//			Intent intent = new Intent(IMP2PSettingActivity.this, CreateGroupActivity.class);
//			startActivity(intent);
			Intent intent1 = new Intent(IMP2PSettingActivity.this, ContactMsgCenterActivity.class);
			intent1.putExtra(ContactMsgCenterActivity.LAUNCH_MODE, ContactMsgCenterActivity.LAUNCH_MODE_P2PTOGROUP);
			intent1.putExtra("onconid", mOnconId);
			startActivity(intent1);
			finish();
			break;
		case R.id.im_p2p_setting_RL_clearAllMsgs:
			ImData.getInstance().deleteMessageData(mOnconId);
			super.toastToMessage(R.string.clear_end);
			break;
		default:
			break;
		}
		super.onClick(view);
	}
}