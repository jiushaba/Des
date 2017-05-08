package com.lz.oncon.app.im.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import com.lb.common.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lb.common.util.Constants;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.api.SIXmppMessage.SourceType;
import com.lz.oncon.app.im.ui.common.VoicePlayClickListener;

public class MiniIlbcPlayerView extends LinearLayout{

	private Context mContext;
	SIXmppMessage data;

	public MiniIlbcPlayerView(Context context) {
		super(context);
		this.mContext = context;
		initView();
	}

	public MiniIlbcPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	private ImageView mPlayButton;
	private AnimationDrawable playAnim;
	private TextView mTimeTextView;
	
	View view;
	LayoutInflater in;
	ProgressBar progressBar;

	private void initView() {
		in = LayoutInflater.from(mContext);
		view = in.inflate(R.layout.message_audio, null);
		mPlayButton = (ImageView) view.findViewById(R.id.message_audio_image);
		progressBar = (ProgressBar) view.findViewById(R.id.message_audio_progressbar);
		mTimeTextView = (TextView) view.findViewById(R.id.message_audio_time);
		mTimeTextView.setText("0\"");

		playAnim = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.audio_play);
		playAnim.setVisible(false, false);

		addView(view);
	}

	public SIXmppMessage getMessage() {
		return this.data;
	}

	public void setMessage(SIXmppMessage data) {
		this.data = data;
		try {
			// set view
			if (mTimeTextView != null) {
				mTimeTextView.setText(StringUtils.sTOm(data.getAudioTimeLength()));
			}
			if(data.getSourceType() == SourceType.RECEIVE_MESSAGE){
				mPlayButton.setImageResource(R.drawable.chatfrom_voice_playing);
			}else{
				mPlayButton.setImageResource(R.drawable.chatto_voice_playing);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, e.getMessage(), e);
		}
		setOnClickListener(new VoicePlayClickListener(data, mPlayButton, mContext));
	}
}