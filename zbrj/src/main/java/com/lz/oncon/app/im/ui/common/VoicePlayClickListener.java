/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lz.oncon.app.im.ui.common;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;


import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.xuanbo.xuan.R;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.ui.IMMessageListActivity;

public class VoicePlayClickListener implements View.OnClickListener {

	SIXmppMessage message;
	EMMessage emmsg;
	ImageView voiceIconView;

	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	Context context;

	public static boolean isPlaying = false;
	public static VoicePlayClickListener currentPlayListener = null;

	/**
	 * 
	 * @param message
	 * @param v
	 * @param iv_read_status
	 * @param context
	 * @param activity
	 * @param user
	 * @param chatType
	 */
	public VoicePlayClickListener(SIXmppMessage message, ImageView v, Context context) {
		this.message = message;
		emmsg = EMChatManager.getInstance().getMessage(message.getId());
		voiceIconView = v;
		this.context = context;
	}

	public void stopPlayVoice() {
		voiceAnimation.stop();
		if (message.getSourceType() == SIXmppMessage.SourceType.RECEIVE_MESSAGE) {
			voiceIconView.setImageResource(R.drawable.chatfrom_voice_playing);
		} else {
			voiceIconView.setImageResource(R.drawable.chatto_voice_playing);
		}
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		((IMMessageListActivity)context).playMsgId = null;
		((IMMessageListActivity)context).mAdapter.notifyDataSetChanged();
	}

	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
			return;
		}
		((IMMessageListActivity)context).playMsgId = message.getId();
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

		mediaPlayer = new MediaPlayer();
		if (EMChatManager.getInstance().getChatOptions().getUseSpeaker()) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);// 关闭扬声器
			// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mediaPlayer.release();
					mediaPlayer = null;
					stopPlayVoice(); // stop animation
				}

			});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();
		} catch (Exception e) {
		}
	}

	// show the voice playing animation
	private void showAnimation() {
		// play voice, and start animation
		if (message.getSourceType() == SIXmppMessage.SourceType.RECEIVE_MESSAGE) {
			voiceIconView.setImageResource(R.anim.voice_from_icon);
		} else {
			voiceIconView.setImageResource(R.anim.voice_to_icon);
		}
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		if (isPlaying) {
			if (((IMMessageListActivity)context).playMsgId != null && ((IMMessageListActivity)context).playMsgId.equals(message.getId())) {
				currentPlayListener.stopPlayVoice();
				return;
			}
			currentPlayListener.stopPlayVoice();
		}

		if (message.getSourceType() == SIXmppMessage.SourceType.SEND_MESSAGE) {
			// for sent msg, we will try to play the voice file directly
			playVoice(message.getAudioPath());
		} else {
			if (emmsg.status == EMMessage.Status.SUCCESS) {
				File file = new File(message.getAudioPath());
				if (file.exists() && file.isFile())
					playVoice(message.getAudioPath());
				else
					System.err.println("file not exist");
			} else if (emmsg.status == EMMessage.Status.INPROGRESS) {
				((IMMessageListActivity)context).toastToMessage(R.string.Is_download_voice_click_later);
			} else if (emmsg.status == EMMessage.Status.FAIL) {
				((IMMessageListActivity)context).toastToMessage(R.string.Is_download_voice_click_later);
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						EMChatManager.getInstance().asyncFetchMessage(emmsg);
						return null;
					}
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						((IMMessageListActivity)context).mAdapter.notifyDataSetChanged();
					}
				}.execute();
			}
		}
	}
}