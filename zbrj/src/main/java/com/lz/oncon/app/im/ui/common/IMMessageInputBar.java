package com.lz.oncon.app.im.ui.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.adapter.GridViewFaceAdapter.FaceGroupLister;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppThreadInfo;
import com.lz.oncon.app.im.data.IMThreadData;
import com.lz.oncon.app.im.data.ImCore;
import com.lz.oncon.app.im.ui.IMMessageListActivity;
import com.lz.oncon.app.im.ui.IMMessageListMenuAdapter;
import com.lz.oncon.app.im.util.SmileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class IMMessageInputBar extends LinearLayout {
	
	enum BUTTON_MODE {// 按钮状态
		TEXT_MODE, RECORD_MODE//文本模式,录音模式
	}
	
	private BUTTON_MODE mButtonMode = BUTTON_MODE.TEXT_MODE;//当前按钮状态
	private IMThreadData.Type mType;//类型:圈子/点对点/群发
	private String mOnconId;//ID,名称
//	private ArrayList<SIXmppMessage> mMsgs;
	
	private EditText mEditText;// 发送编辑框
	public Button mSendButton;// 发送按钮, 录音按钮
	public Button mRecordButton;
	public ImageView mChangeButton, mSwitch2MenuButton, mSwitch2MsgButton, mMoreButton, mMenuDivider;// 切换文本和录音模式, 菜单, 菜单, 更多
	public ImageView iv_emoticons_normal, iv_emoticons_checked;
	public IMMessageFaceBar faceBar;//表情bar
	public IMMessageMoreBtnBar moreBtnBar;//更多按钮bar
	private GridView menuGV;//菜单
	public LinearLayout inputRL1, recordLL;
	public LinearLayout emojiIconContainer;
	private ViewPager expressionViewpager;
	public RelativeLayout inputRL2, editRL;//输入布局,菜单布局;
	private Animation showAnim, hideAnim;//输入布局和菜单布局切换动画
	private List<String> reslist;
	private IMMessageListMenuAdapter menuAdapter;//菜单适配

	public IMMessageInputBar(Context context) {
		super(context);
		init();
	}
	
	public IMMessageInputBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@SuppressLint("NewApi")
	public IMMessageInputBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.app_im_message_input, this);
		mEditText = (EditText) findViewById(R.id.im_message__edit);
		mSendButton = (Button) findViewById(R.id.im_message__send);
		mChangeButton = (ImageView) findViewById(R.id.im_message__change);
		mChangeButton.setOnClickListener(modeOnClickListener);
		mSwitch2MenuButton = (ImageView) findViewById(R.id.im_message_switch_to_menu);
		mSwitch2MenuButton.setOnClickListener(switch2MenuOnClickListener);
		mSwitch2MsgButton = (ImageView) findViewById(R.id.im_message_switch_to_msg);
		mSwitch2MsgButton.setOnClickListener(switch2MsgOnClickListener);
		mMoreButton = (ImageView) findViewById(R.id.im_message__more);
		mMoreButton.setOnClickListener(moreOnClickListener);
		menuGV = (GridView) findViewById(R.id.im_message_accmenu);
		mMenuDivider = (ImageView) findViewById(R.id.im_menu_divider);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		expressionViewpager = (ViewPager) findViewById(R.id.emface_vPager);
		emojiIconContainer = (LinearLayout) findViewById(R.id.em_face_container);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.GONE);
		iv_emoticons_normal.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				iv_emoticons_normal.setVisibility(View.GONE);
				iv_emoticons_checked.setVisibility(View.VISIBLE);
				emojiIconContainer.setVisibility(View.VISIBLE);
				moreBtnBar.setVisibility(View.GONE);
				hideKeyboard();
			}
		});
		iv_emoticons_checked.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.GONE);
				emojiIconContainer.setVisibility(View.GONE);
				moreBtnBar.setVisibility(View.GONE);
			}
		});
		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		
		moreBtnBar = (IMMessageMoreBtnBar) findViewById(R.id.im_message__more_layout);
		moreBtnBar.setInputView(mEditText);
		moreBtnBar.setInputBar(this);
		faceBar = (IMMessageFaceBar) findViewById(R.id.gridviewface);
		
		mRecordButton = (Button) findViewById(R.id.im_message__button_record);
		mRecordButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});
		recordLL = (LinearLayout) findViewById(R.id.im_message__button_recordLL);
		editRL = (RelativeLayout) findViewById(R.id.im_message__editRL);
		
		mMoreButton.setVisibility(View.VISIBLE);
		mSendButton.setVisibility(View.GONE);
		mEditText.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.GONE);
				emojiIconContainer.setVisibility(View.GONE);
			}
		});
		mEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
//				if(mType != null && mType.ordinal() == IMThreadData.Type.BATCH.ordinal()){
//					if(TextUtils.isEmpty(s.toString())){
//						mSendButton.setVisibility(View.GONE);
//					}else{
//						mSendButton.setVisibility(View.VISIBLE);
//					}
//				}else{
					if(TextUtils.isEmpty(s.toString())){
						mMoreButton.setVisibility(View.VISIBLE);
						mSendButton.setVisibility(View.GONE);
					}else{
						mMoreButton.setVisibility(View.GONE);
						mSendButton.setVisibility(View.VISIBLE);
					}
//				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			
		});
		
		inputRL1 = (LinearLayout) findViewById(R.id.im_message__input_layout1);
		inputRL2 = (RelativeLayout) findViewById(R.id.im_message__input_layout2);
		showAnim = (Animation) AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_in);
		hideAnim = (Animation) AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_out);
	}
	
	public void setTextWhenUp(){
		mRecordButton.setText(R.string.im_press2record);
	}
	
	public void setTextWhenEnd(){
		mRecordButton.setText(R.string.im_press2record_end);
	}
	
	public void setTextWhenCancel(){
		mRecordButton.setText(R.string.im_press2record_cancel);
	}
	
	public void setInfo(IMThreadData.Type type, String onconId, String name, ArrayList<SIXmppMessage> msgs,boolean isSpeP2P){
		mType = type;
		mOnconId = onconId;
//		mMsgs = msgs;
		moreBtnBar.setThread(type, mOnconId, name,isSpeP2P);
		faceBar.setThread(type, mOnconId, msgs);
		if(SIXmppThreadInfo.Type.GROUP.ordinal() == mType.ordinal()){
			mSendButton.setOnClickListener(sendOnClickListener);
		}else if(SIXmppThreadInfo.Type.P2P.ordinal() == mType.ordinal()){
			mSendButton.setOnClickListener(sendOnClickListener);
		}
	}
	
	public void setmOnconId(String mOnconId) {
		this.mOnconId = mOnconId;
	}
	
	public void setName(String name){
		moreBtnBar.setName(name);
	}
	
	public void setText(String text){
		mEditText.setText(text);
	}
	
	public String getText(){
		return mEditText.getText().toString();
	}
	
	public void setFaceGroupLister(FaceGroupLister faceGroupLister){
		faceBar.setFaceGroupLister(faceGroupLister);
	}
	
	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive() && mEditText != null) {
			imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
		}
	}
	
	public boolean isMoreShow(){
		if (faceBar.getVisibility() == View.VISIBLE) {
			faceBar.setVisibility(View.GONE);
			return true;
		} else if (moreBtnBar.getVisibility() == View.VISIBLE) {
			moreBtnBar.setVisibility(View.GONE);
			return true;
		}
		return false;
	}
	
	private OnClickListener modeOnClickListener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			if (mButtonMode == BUTTON_MODE.TEXT_MODE) {
				recordLL.setVisibility(View.VISIBLE);
				editRL.setVisibility(View.GONE);
//				mEditText.setVisibility(View.GONE);
				mSendButton.setVisibility(View.GONE);
				mButtonMode = BUTTON_MODE.RECORD_MODE;
				mChangeButton.setImageResource(R.drawable.btn_im_text);
				moreBtnBar.setVisibility(View.GONE);
				faceBar.setVisibility(View.GONE);
				
				mMoreButton.setVisibility(View.GONE);
				mSendButton.setVisibility(View.GONE);
			} else {
				recordLL.setVisibility(View.GONE);
				editRL.setVisibility(View.VISIBLE);
//				mEditText.setVisibility(View.VISIBLE);
				mSendButton.setVisibility(View.VISIBLE);
				mButtonMode = BUTTON_MODE.TEXT_MODE;
				mChangeButton.setImageResource(R.drawable.btn_im_speaker);
				
				if(TextUtils.isEmpty(mEditText.getText().toString())){
					mMoreButton.setVisibility(View.VISIBLE);
					mSendButton.setVisibility(View.GONE);
				}else{
					mMoreButton.setVisibility(View.GONE);
					mSendButton.setVisibility(View.VISIBLE);
				}
			}
			hideKeyboard();
		}
	};
	
	private OnClickListener moreOnClickListener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			if (faceBar.getVisibility() == View.VISIBLE) {
				faceBar.setVisibility(View.GONE);
			}
			if(emojiIconContainer.getVisibility() == View.VISIBLE){
				emojiIconContainer.setVisibility(View.GONE);
			}
			if (View.VISIBLE == moreBtnBar.getVisibility()) {
				moreBtnBar.setVisibility(View.GONE);
			} else {
				moreBtnBar.setVisibility(View.VISIBLE);
				hideKeyboard();
			}
		}
	};
	
	private OnClickListener sendOnClickListener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			String message = mEditText.getText().toString();
			if (message == null || message.equals("")) {
				((BaseActivity)getContext()).toastToMessage("消息不能为空");
			} else {
				if (mOnconId != null && !mOnconId.equals("")) {
					SIXmppMessage xmppMessage = ImCore.getInstance().getChatManager().createChat(mOnconId).sendTextMessage(message,SIXmppThreadInfo.Type.P2P);
					((IMMessageListActivity)getContext()).sendMsg(xmppMessage);
					mEditText.setText("");
				}
			}
		}
	};
	
	private OnClickListener switch2MenuOnClickListener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			hideAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					inputRL1.setVisibility(View.GONE);
					inputRL2.startAnimation(showAnim);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			showAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					inputRL2.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			inputRL1.startAnimation(hideAnim);

			if (faceBar.getVisibility() == View.VISIBLE) {
				faceBar.setVisibility(View.GONE);
			}
			if (View.VISIBLE == moreBtnBar.getVisibility()) {
				moreBtnBar.setVisibility(View.GONE);
			}
			hideKeyboard();
		}
	};
	
	private OnClickListener switch2MsgOnClickListener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			hideAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					inputRL2.setVisibility(View.GONE);
					inputRL1.startAnimation(showAnim);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			showAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					inputRL1.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			inputRL2.startAnimation(hideAnim);
			if (faceBar.getVisibility() == View.VISIBLE) {
				faceBar.setVisibility(View.GONE);
			}
			if (View.VISIBLE == moreBtnBar.getVisibility()) {
				moreBtnBar.setVisibility(View.GONE);
			}
			hideKeyboard();
		}
	};
	
	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(getContext(), R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(getContext(), 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (recordLL.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("com.lz.oncon.app.im.util.SmileUtils");
							Field field = clz.getField(filename);
							mEditText.append(SmileUtils.getSmiledText(getContext(), (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditText.getText())) {

								int selectionStart = mEditText.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditText.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											mEditText.getEditableText().delete(i, selectionStart);
										else
											mEditText.getEditableText().delete(selectionStart - 1, selectionStart);
									} else {
										mEditText.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}
	
	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;
	}
}