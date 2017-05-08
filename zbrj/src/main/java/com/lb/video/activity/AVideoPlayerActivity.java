/*****************************************************************************
 * VideoPlayerActivity.java
 *****************************************************************************
 * Copyright © 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.lb.video.activity;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.libvlc.Media;
import org.videolan.vlc.MediaWrapper;
import org.videolan.vlc.MediaWrapperListPlayer;
import org.videolan.vlc.VLCApplication;
import org.videolan.vlc.audio.AudioServiceController;
import org.videolan.vlc.util.AndroidDevices;
import org.videolan.vlc.util.Strings;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.util.VLCInstance;
import org.videolan.vlc.util.WeakHandler;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lb.common.util.Log;
import com.lb.video.view.VideoplaySurfaceView;
import com.lb.zbrj.data.VideoData;
import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;

@SuppressLint("NewApi")
public abstract class AVideoPlayerActivity extends BaseActivity implements IVideoPlayer, GestureDetector.OnDoubleTapListener {
	 public final static String TAG = "VLC/VideoPlayerActivity";

    // Internal intent identifier to distinguish between internal launch and
    // external intent.
   // public final static String PLAY_FROM_VIDEOGRID = "org.videolan.vlc.gui.video.PLAY_FROM_VIDEOGRID";

    public final static String PLAY_EXTRA_ITEM_LOCATION = "item_location";
    public final static String PLAY_EXTRA_SUBTITLES_LOCATION = "subtitles_location";
    public final static String PLAY_EXTRA_ITEM_TITLE = "item_title";
    public final static String PLAY_EXTRA_FROM_START = "from_start";
	    
	protected VideoplaySurfaceView mSurfaceView;
    protected SurfaceView mSubtitlesSurfaceView;
    protected SurfaceHolder mSurfaceHolder;
    protected SurfaceHolder mSubtitlesSurfaceHolder;
    protected Surface mSurface = null;
    protected Surface mSubtitleSurface = null;
    protected FrameLayout mSurfaceFrame;
    protected MediaRouter mMediaRouter;
    protected MediaRouter.SimpleCallback mMediaRouterCallback;
    /*protected SecondaryDisplay mPresentation;*/
    protected int mPresentationDisplayId = -1;
    protected LibVLC mLibVLC;
    protected MediaWrapperListPlayer mMediaListPlayer;
    protected String mLocation;
//    protected GestureDetectorCompat mDetector;

    protected static final int SURFACE_BEST_FIT = 0;
    protected static final int SURFACE_FIT_HORIZONTAL = 1;
    protected static final int SURFACE_FIT_VERTICAL = 2;
    protected static final int SURFACE_FILL = 3;
    protected static final int SURFACE_16_9 = 4;
    protected static final int SURFACE_4_3 = 5;
    protected static final int SURFACE_ORIGINAL = 6;
    protected int mCurrentSize = SURFACE_FIT_HORIZONTAL;
    boolean wasPaused=false;
    protected SharedPreferences mSettings;

    /** Overlay */
   /* protected ActionBar mActionBar;*/
    protected View mOverlayProgress;
//    protected View mOverlayBackground;
    protected static final int OVERLAY_TIMEOUT = 4000;
    protected static final int OVERLAY_INFINITE = -1;
    protected static final int FADE_OUT = 1;
    protected static final int SHOW_PROGRESS = 2;
    protected static final int SURFACE_LAYOUT = 3;
    protected static final int FADE_OUT_INFO = 4;
    protected static final int AUDIO_SERVICE_CONNECTION_SUCCESS = 5;
    protected static final int AUDIO_SERVICE_CONNECTION_FAILED = 6;
    /*protected static final int END_DELAY_STATE = 7;*/
    protected static final int RESET_BACK_LOCK = 8;
    protected boolean mDragging;
    protected boolean mShowing;
    protected int mUiVisibility = -1;
    protected SeekBar mSeekbar;
  /*  protected TextView mSysTime;*/
//    protected TextView mBattery;
    protected TextView mTime;
    //protected TextView mLength;
    protected TextView mInfo;
    protected View mVerticalBar;
    protected View mVerticalBarProgress;
    //protected ImageView mLoading;
   // protected TextView mLoadingText;
    protected ImageView mTipsBackground;
    protected ImageView mPlayPause;
    //protected ImageView mTracks;
//    protected ImageView mAdvOptions;
//    protected ImageView mDelayPlus;
//    protected ImageView mDelayMinus;
    protected boolean mEnableBrightnessGesture;
    protected boolean mEnableCloneMode;
    protected boolean mDisplayRemainingTime = false;
    protected int mScreenOrientation;
    protected int mScreenOrientationLock;
   // protected ImageView mLock;
//    protected ImageView mSize;
    protected boolean mIsLocked = false;
    protected int mLastAudioTrack = -1;
    protected int mLastSpuTrack = -2;
    protected int mOverlayTimeout = 0;
    protected boolean mLockBackButton = false;

    /**
     * For uninterrupted switching between audio and video mode
     */
    protected boolean mSwitchingView;
    protected boolean mHardwareAccelerationError;
    protected boolean mEndReached;
    protected boolean mCanSeek;

    // Playlist
    protected int savedIndexPosition = -1;

    // size of the video
    protected int mVideoHeight;
    protected int mVideoWidth;
    protected int mVideoVisibleHeight;
    protected int mVideoVisibleWidth;
    protected int mSarNum;
    protected int mSarDen;

    //Volume
    protected AudioManager mAudioManager;
    protected int mAudioMax;
    protected boolean mMute = false;
    protected int mVolSave;
    protected float mVol;

    //Touch Events
    protected static final int TOUCH_NONE = 0;
    protected static final int TOUCH_VOLUME = 1;
    protected static final int TOUCH_BRIGHTNESS = 2;
    protected static final int TOUCH_SEEK = 3;
    protected int mTouchAction;
    protected int mSurfaceYDisplayRange;
    protected float mInitTouchY, mTouchY, mTouchX;

    //stick event
    protected static final int JOYSTICK_INPUT_DELAY = 300;
    protected long mLastMove;

    // Brightness
    protected boolean mIsFirstBrightnessGesture = true;
    protected float mRestoreAutoBrightness = -1f;

    // Tracks & Subtitles
    protected Map<Integer,String> mAudioTracksList;
    protected Map<Integer,String> mSubtitleTracksList;
    /**
     * Used to store a selected subtitle; see onActivityResult.
     * It is possible to have multiple custom subs in one session
     * (just like desktop VLC allows you as well.)
     */
    protected final ArrayList<String> mSubtitleSelectedFiles = new ArrayList<String>();

    // Whether fallback from HW acceleration to SW decoding was done.
    protected boolean mDisabledHardwareAcceleration = false;
    protected int mPreviousHardwareAccelerationMode;

    /**
     * Flag to indicate whether the media should be paused once loaded
     * (e.g. lock screen, or to restore the pause state)
     */
    protected boolean mPlaybackStarted = false;

    /**
     * Flag used by changeAudioFocus and mAudioFocusListener
     */
    protected boolean mLostFocus = false;
    protected boolean mHasAudioFocus = false;

    /* Flag to indicate if AudioService is bound or binding */
    protected boolean mBound = false;

    // Tips
    protected View mOverlayTips;
    protected static final String PREF_TIPS_SHOWN = "video_player_tips_shown";

    // Navigation handling (DVD, Blu-Ray...)
    protected boolean mHasMenu = false;
    protected boolean mIsNavMenu = true;

    /* for getTime and seek */
    protected long mForcedTime = -1;
    protected long mLastTime = -1;

    protected OnLayoutChangeListener mOnLayoutChangeListener;
    protected AlertDialog mAlertDialog;

    protected boolean mHasHdmiAudio = false;
    
	private long playtime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	initView();
    	init();
    }
    protected void init(){
    	initVlc();
    	initViewAndEvents();
    	 
    }
    private void initView(){
    	initContentView(R.layout.activity_player);
    }
    @SuppressLint("NewApi")
	protected void initVlc(){
        if (!VLCInstance.testCompatibleCPU(this)) {
            myfinish();
            return;
        }
        mLibVLC = VLCInstance.get();
        mMediaListPlayer = MediaWrapperListPlayer.getInstance(mLibVLC);
        if (LibVlcUtil.isJellyBeanMR1OrLater()) {
            // Get the media router service (Miracast)
            mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
            mMediaRouterCallback = new MediaRouter.SimpleCallback() {
                @Override
                public void onRoutePresentationDisplayChanged(
                        MediaRouter router, MediaRouter.RouteInfo info) {
                    Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);
                    final Display presentationDisplay = info.getPresentationDisplay();
                    final int newDisplayId = presentationDisplay != null ? presentationDisplay.getDisplayId() : -1;
                    if (newDisplayId != mPresentationDisplayId)
                        removePresentation();
                }
            };
            Log.d(TAG, "MediaRouter information : " + mMediaRouter  .toString());
        }

        /* Services and miscellaneous */
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        createPresentation();
        if (LibVlcUtil.isICSOrLater())
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if (visibility == mUiVisibility)
                                return;
                            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !mShowing && !isFinishing()) {
                                showOverlay();
                            }
                            mUiVisibility = visibility;
                        }
                    }
            );
    }
    /** initialize Views an their Events */
    protected void initViewAndEvents(){
    	
    	mOverlayProgress = findViewById(R.id.progress_overlay);
        mTime = (TextView) findViewById(R.id.video_stat_time);
        //mTime.setOnClickListener(mRemainingTimeListener);
        //mLength = (TextView) findViewById(R.id.player_overlay_length);
        //mLength.setOnClickListener(mRemainingTimeListener);
        mInfo = (TextView) findViewById(R.id.player_overlay_info);
      
        mPlayPause = (ImageView) findViewById(R.id.player_overlay_play);
        mPlayPause.setOnClickListener(mPlayPauseListener);
        /*mLock = (ImageButton) findViewById(R.id.lock_overlay_button);
        mLock.setOnClickListener(mLockListener);*/
       /* mSize = (ImageView) findViewById(R.id.player_overlay_size);
        mSize.setOnClickListener(mSizeListener);*/
        
       
        
        /*mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mSubtitlesSurfaceHolder = mSubtitlesSurfaceView.getHolder();
        mSubtitlesSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSubtitlesSurfaceView.setZOrderMediaOverlay(true);
        mSubtitlesSurfaceHolder.addCallback(mSubtitlesSurfaceCallback);*/
        
        mSeekbar = (SeekBar) findViewById(R.id.player_overlay_seekbar);
        mSeekbar.setOnSeekBarChangeListener(mSeekListener);

        /* Loading view */
       /* mLoading = (ImageView) findViewById(R.id.player_overlay_loading);
        mLoadingText = (TextView) findViewById(R.id.player_overlay_loading_text);*/
        //startLoadingAnimation();
        mSwitchingView = false;
        mEndReached = false;
        mSurfaceView = (VideoplaySurfaceView) findViewById(R.id.player_surface);
        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        mSubtitlesSurfaceView = (SurfaceView) findViewById(R.id.subtitles_surface);
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(VLCApplication.SLEEP_INTENT);

        Log.d(TAG,
                "Hardware acceleration mode: "
                        + Integer.toString(mLibVLC.getHardwareAcceleration()));

        /* Only show the subtitles surface when using "Full Acceleration" mode */
        if (mLibVLC.getHardwareAcceleration() == LibVLC.HW_ACCELERATION_FULL)
            mSubtitlesSurfaceView.setVisibility(View.VISIBLE);
        

        final EventHandler em = EventHandler.getInstance();
        em.addHandler(mEventHandler);
        
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        updateNavStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSeekbar.setOnSeekBarChangeListener(null);
        /* Stop the earliest possible to avoid vout error */
        if (isFinishing())
            stopPlayback();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	try {
			if(mLibVLC != null){
				 if (!LibVlcUtil.isHoneycombOrLater())
			            setSurfaceLayout(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
			        
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//        initView();
//        init();
        
        
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onStop() {
        super.onStop();

        if (mAlertDialog != null && mAlertDialog.isShowing())
            mAlertDialog.dismiss();
        stopPlayback();

        // Dismiss the presentation when the activity is not visible.
        /*if (mPresentation != null) {
            Log.i(TAG, "Dismissing presentation because the activity is no longer visible.");
            mPresentation.dismiss();
            mPresentation = null;
        }*/
        restoreBrightness();
    }

    @TargetApi(android.os.Build.VERSION_CODES.FROYO)
    protected void restoreBrightness() {
        if (mRestoreAutoBrightness != -1f) {
            int brightness = (int) (mRestoreAutoBrightness*255f);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightness);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioManager = null;
    }

    protected void bindAudioService() {
        if (mBound)
            return;
        mBound = true;
        AudioServiceController.getInstance().bindAudioService(this,
                new AudioServiceController.AudioServiceConnectionListener() {
                    @Override
                    public void onConnectionSuccess() {
                        mHandler.sendEmptyMessage(AUDIO_SERVICE_CONNECTION_SUCCESS);
                    }

                    @Override
                    public void onConnectionFailed() {
                        mBound = false;
                        mHandler.sendEmptyMessage(AUDIO_SERVICE_CONNECTION_FAILED);
                    }
                });
    }
    protected void unbindAudioService() {
        AudioServiceController.getInstance().unbindAudioService(this);
        mBound = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindSurefaceView();
        mSwitchingView = false;
        mSeekbar.setOnSeekBarChangeListener(mSeekListener);
        bindAudioService();
      
//        if (mIsLocked && mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR)
//            setRequestedOrientation(mScreenOrientationLock);
//       
       // mSurfaceView.setOrientation(90);
       /* mSurfaceFrame.setRotation(90);
        mSurfaceView.setRotation(90);*/
       
        if (!LibVlcUtil.isHoneycombOrLater())
            setSurfaceLayout(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
    }
    private void bindSurefaceView(){
    	mSurfaceHolder = mSurfaceView.getHolder();
    	mSurfaceHolder.removeCallback(mSurfaceCallback);
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mSubtitlesSurfaceHolder = mSubtitlesSurfaceView.getHolder();
        mSubtitlesSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSubtitlesSurfaceView.setZOrderMediaOverlay(true);
        mSubtitlesSurfaceHolder.removeCallback(mSubtitlesSurfaceCallback);
        mSubtitlesSurfaceHolder.addCallback(mSubtitlesSurfaceCallback);
    }
    /**
     * Add or remove MediaRouter callbacks. This is provided for version targeting.
     *
     * @param add true to add, false to remove
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void mediaRouterAddCallback(boolean add) {
        if(!LibVlcUtil.isJellyBeanMR1OrLater() || mMediaRouter == null) return;

        if(add)
            mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
        else
            mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    @SuppressLint("NewApi")
	protected void startPlayback() {
        if (mPlaybackStarted)
            return;
        mPlaybackStarted = true;
        if (LibVlcUtil.isHoneycombOrLater()) {
            if (mOnLayoutChangeListener == null) {
                mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right,
                                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom)
                            setSurfaceLayout(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
                    }
                };
            }
            mSurfaceFrame.addOnLayoutChangeListener(mOnLayoutChangeListener);
        }
        setSurfaceLayout(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);

        if (mMediaRouter != null) {
            // Listen for changes to media routes.
            mediaRouterAddCallback(true);
        }

        mSurfaceView.setKeepScreenOn(true);

       

        // Signal to LibVLC that the videoPlayerActivity was created, thus the
        // SurfaceView is now available for MediaCodec direct rendering.
       // mLibVLC.eventVideoPlayerActivityCreated(true);
        loadMedia();
        // Add any selected subtitle file from the file picker
        /*if(mSubtitleSelectedFiles.size() > 0) {
            for(String file : mSubtitleSelectedFiles) {
                Log.i(TAG, "Adding user-selected subtitle " + file);
                mLibVLC.addSubtitleTrack(file);
            }
        }*/
    }

    @SuppressLint("NewApi")
	protected void stopPlayback() {
        if (!mPlaybackStarted)
            return;

        mPlaybackStarted = false;

        if(mSwitchingView) {
            Log.d(TAG, "mLocation = \"" + mLocation + "\"");
            AudioServiceController.getInstance().showWithoutParse(savedIndexPosition);
            unbindAudioService();
            return;
        }

        final EventHandler em = EventHandler.getInstance();
        em.removeHandler(mEventHandler);
        mEventHandler.removeCallbacksAndMessages(null);

        mHandler.removeCallbacksAndMessages(null);

        mSurfaceView.setKeepScreenOn(false);

        if (mMediaRouter != null) {
            // Stop listening for changes to media routes.
            mediaRouterAddCallback(false);
        }

        changeAudioFocus(false);

        //final boolean isPaused = !mLibVLC.isPlaying();
        mLibVLC.stop();

        // MediaCodec opaque direct rendering should not be used anymore since there is no surface to attach.
        //mLibVLC.eventVideoPlayerActivityCreated(false);
        // HW acceleration was temporarily disabled because of an error, restore the previous value.
        if (mDisabledHardwareAcceleration)
            mLibVLC.setHardwareAcceleration(mPreviousHardwareAccelerationMode);

        if (LibVlcUtil.isHoneycombOrLater() && mOnLayoutChangeListener != null)
            mSurfaceFrame.removeOnLayoutChangeListener(mOnLayoutChangeListener);
        unbindAudioService();
    }





    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        showOverlay();
        return true;
    }

    @TargetApi(12) //only active for Android 3.1+
    public boolean dispatchGenericMotionEvent(MotionEvent event){
        //Check for a joystick event
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) !=
                InputDevice.SOURCE_JOYSTICK ||
                event.getAction() != MotionEvent.ACTION_MOVE)
            return false;

        InputDevice mInputDevice = event.getDevice();

        float dpadx = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float dpady = event.getAxisValue(MotionEvent.AXIS_HAT_Y);
        if (mInputDevice == null || Math.abs(dpadx) == 1.0f || Math.abs(dpady) == 1.0f)
            return false;

        float x = AndroidDevices.getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_X);
        float y = AndroidDevices.getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_Y);
        float rz = AndroidDevices.getCenteredAxis(event, mInputDevice,
                MotionEvent.AXIS_RZ);

        if (System.currentTimeMillis() - mLastMove > JOYSTICK_INPUT_DELAY){
            if (Math.abs(x) > 0.3){
                if (AndroidDevices.hasTsp()) {
                    seekDelta(x > 0.0f ? 10000 : -10000);
                } else
                    navigateDvdMenu(x > 0.0f ? KeyEvent.KEYCODE_DPAD_RIGHT : KeyEvent.KEYCODE_DPAD_LEFT);
            } else if (Math.abs(y) > 0.3){
                if (AndroidDevices.hasTsp()) {
                    if (mIsFirstBrightnessGesture)
                        initBrightnessTouch();
                    changeBrightness(-y / 10f);
                } else
                    navigateDvdMenu(x > 0.0f ? KeyEvent.KEYCODE_DPAD_UP : KeyEvent.KEYCODE_DPAD_DOWN);
            } else if (Math.abs(rz) > 0.3){
                mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int delta = -(int) ((rz / 7) * mAudioMax);
                int vol = (int) Math.min(Math.max(mVol + delta, 0), mAudioMax);
                setAudioVolume(vol,true);
            }
            mLastMove = System.currentTimeMillis();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mLockBackButton) {
            mLockBackButton = false;
            mHandler.sendEmptyMessageDelayed(RESET_BACK_LOCK, 2000);
            //Toast.makeText(this, "Press back again to quit video", Toast.LENGTH_SHORT).show();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        showOverlayTimeout(OVERLAY_TIMEOUT);
        switch (keyCode) {
        case KeyEvent.KEYCODE_F:
        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            seekDelta(10000);
            return true;
        case KeyEvent.KEYCODE_R:
        case KeyEvent.KEYCODE_MEDIA_REWIND:
            seekDelta(-10000);
            return true;
        case KeyEvent.KEYCODE_BUTTON_R1:
            seekDelta(60000);
            return true;
        case KeyEvent.KEYCODE_BUTTON_L1:
            seekDelta(-60000);
            return true;
        case KeyEvent.KEYCODE_BUTTON_A:
            if (mOverlayProgress.getVisibility() == View.VISIBLE)
                return false;
        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
        case KeyEvent.KEYCODE_MEDIA_PLAY:
        case KeyEvent.KEYCODE_MEDIA_PAUSE:
        case KeyEvent.KEYCODE_SPACE:
            if (mIsNavMenu)
                return navigateDvdMenu(keyCode);
            else
                doPlayPause();
            return true;
        case KeyEvent.KEYCODE_O:
        case KeyEvent.KEYCODE_BUTTON_Y:
        case KeyEvent.KEYCODE_MENU:
//            showAdvancedOptions(mAdvOptions);
            return true;
        case KeyEvent.KEYCODE_V:
        case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK:
        case KeyEvent.KEYCODE_BUTTON_X:
           Log.e(TAG, "not support KEYCODE_BUTTON_X");
            return true;
        case KeyEvent.KEYCODE_N:
            showNavMenu();
            return true;
        case KeyEvent.KEYCODE_A:
            resizeVideo();
            return true;
        case KeyEvent.KEYCODE_M:
        case KeyEvent.KEYCODE_VOLUME_MUTE:
            updateMute();
            return true;
        case KeyEvent.KEYCODE_S:
        case KeyEvent.KEYCODE_MEDIA_STOP:
            myfinish();
            return true;
        case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_DPAD_LEFT:
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            if (mIsNavMenu)
                return navigateDvdMenu(keyCode);
            else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    protected boolean navigateDvdMenu(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mLibVLC.playerNavigate(LibVLC.INPUT_NAV_UP);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mLibVLC.playerNavigate(LibVLC.INPUT_NAV_DOWN);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mLibVLC.playerNavigate(LibVLC.INPUT_NAV_LEFT);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mLibVLC.playerNavigate(LibVLC.INPUT_NAV_RIGHT);
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_X:
            case KeyEvent.KEYCODE_BUTTON_A:
                mLibVLC.playerNavigate(LibVLC.INPUT_NAV_ACTIVATE);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setSurfaceLayout(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        if (width * height == 0)
            return;
        // store video size
    	mVideoHeight = height;
        mVideoWidth = width;
    	mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth  = visible_width;
        
        mSarNum = sar_num;
        mSarDen = sar_den;
        Message msg = mHandler.obtainMessage(SURFACE_LAYOUT);
        mHandler.sendMessage(msg);
    }

    protected static class ConfigureSurfaceHolder {
        protected final Surface surface;
        protected boolean configured;

        protected ConfigureSurfaceHolder(Surface surface) {
            this.surface = surface;
        }
    }

    @Override
    public int configureSurface(Surface surface, final int width, final int height, final int hal) {
        if (LibVlcUtil.isICSOrLater() || surface == null)
            return -1;
        if (width * height == 0)
            return 0;
        Log.d(TAG, "configureSurface: " + width +"x"+height);

        final ConfigureSurfaceHolder holder = new ConfigureSurfaceHolder(surface);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mSurface == holder.surface && mSurfaceHolder != null) {
                    if (hal != 0)
                        mSurfaceHolder.setFormat(hal);
                    mSurfaceHolder.setFixedSize(width, height);
                } else if (mSubtitleSurface == holder.surface && mSubtitlesSurfaceHolder != null) {
                    if (hal != 0)
                        mSubtitlesSurfaceHolder.setFormat(hal);
                    mSubtitlesSurfaceHolder.setFixedSize(width, height);
                }

                synchronized (holder) {
                    holder.configured = true;
                    holder.notifyAll();
                }
            }
        });

        try {
            synchronized (holder) {
                while (!holder.configured)
                    holder.wait();
            }
        } catch (InterruptedException e) {
            return 0;
        }
        return 1;
    }


    /**
     * Show text in the info view and vertical progress bar for "duration" milliseconds
     * @param text
     * @param duration
     * @param barNewValue new volume/brightness value (range: 0 - 15)
     */
    /* protected void showInfoWithVerticalBar(String text, int duration, int barNewValue) {
        showInfo(text, duration);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVerticalBarProgress.getLayoutParams();
        layoutParams.weight = barNewValue;
        mVerticalBarProgress.setLayoutParams(layoutParams);
        mVerticalBar.setVisibility(View.VISIBLE);
    }*/

    /**
     * Show text in the info view for "duration" milliseconds
     * @param text
     * @param duration
     */
    protected void showInfo(String text, int duration) {
        /*if (mPresentation == null)
            mVerticalBar.setVisibility(View.INVISIBLE);
*/        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    protected void showInfo(int textid, int duration) {
        /*if (mPresentation == null)
            mVerticalBar.setVisibility(View.INVISIBLE);*/
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(textid);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    /**
     * Show text in the info view
     * @param text
     */
    protected void showInfo(String text) {
       /* if (mPresentation == null)
            mVerticalBar.setVisibility(View.INVISIBLE);*/
        mHandler.removeMessages(FADE_OUT_INFO);
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        hideInfo(1000);
    }

    protected void showInfo(int res) {
		showInfo(getResources().getString(res));
	}
    /**
     * hide the info view with "delay" milliseconds delay
     * @param delay
     */
    protected void hideInfo(int delay) {
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, delay);
    }

    /**
     * hide the info view
     */
    protected void hideInfo() {
        hideInfo(0);
    }

    protected void fadeOutInfo() {
        if (mInfo.getVisibility() == View.VISIBLE)
            mInfo.startAnimation(AnimationUtils.loadAnimation(
            		getActitity(), android.R.anim.fade_out));
        mInfo.setVisibility(View.INVISIBLE);

      /*  if (mPresentation == null) {
            if (mVerticalBar.getVisibility() == View.VISIBLE)
                mVerticalBar.startAnimation(AnimationUtils.loadAnimation(
                		getActitity(), android.R.anim.fade_out));
            mVerticalBar.setVisibility(View.INVISIBLE);
        }*/
    }

    protected OnAudioFocusChangeListener mAudioFocusListener = !LibVlcUtil.isFroyoOrLater() ? null :
            new OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            /*
             * Pause playback during alerts and notifications
             */
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    changeAudioFocus(false);
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mLibVLC.isPlaying()) {
                        mLostFocus = true;
                        mLibVLC.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    if (!mLibVLC.isPlaying() && mLostFocus) {
                        mLibVLC.play();
                        mLostFocus = false;
                    }
                    break;
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.FROYO)
    protected int changeAudioFocus(boolean acquire) {
        if(!LibVlcUtil.isFroyoOrLater()) // NOP if not supported
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

        if (mAudioManager == null)
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;

        int result = AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        if (acquire) {
            if (!mHasAudioFocus) {
                result = mAudioManager.requestAudioFocus(mAudioFocusListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                mAudioManager.setParameters("bgm_state=true");
                mHasAudioFocus = true;
            }
        }
        else {
            if (mHasAudioFocus) {
                result = mAudioManager.abandonAudioFocus(mAudioFocusListener);
                mAudioManager.setParameters("bgm_state=false");
                mHasAudioFocus = true;
            }
        }

        return result;
    }

    /**
     *  Handle libvlc asynchronous events
     */
    protected final Handler mEventHandler = new VideoPlayerEventHandler((AVideoPlayerActivity)getActitity());

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (!mIsLocked) {
            doPlayPause();
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    protected static class VideoPlayerEventHandler extends WeakHandler<AVideoPlayerActivity> {
        public VideoPlayerEventHandler(AVideoPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            AVideoPlayerActivity activity = getOwner();
            if(activity == null) return;
            // Do not handle events if we are leaving the VideoPlayerActivity
            if (activity.mSwitchingView) return;
            
            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaParsedChanged:
                    activity.updateNavStatus();
                    break;
                case EventHandler.MediaPlayerPlaying:
                    Log.i(TAG, "MediaPlayerPlaying");
                   // activity.stopLoadingAnimation();
                    activity.showOverlay();
                    activity.setESTracks();
                    activity.changeAudioFocus(true);
                    activity.updateNavStatus();
                    break;
                case EventHandler.MediaPlayerPaused:
                    Log.i(TAG, "MediaPlayerPaused");
                    break;
                case EventHandler.MediaPlayerStopped:
                    Log.i(TAG, "MediaPlayerStopped");
                    activity.changeAudioFocus(false);
                    break;
                case EventHandler.MediaPlayerEndReached:
                    Log.i(TAG, "MediaPlayerEndReached");
                    activity.changeAudioFocus(false);
                    activity.endReached();
                    break;
                case EventHandler.MediaPlayerVout:
                    activity.updateNavStatus();
                    if (!activity.mHasMenu)
                        activity.handleVout(msg);
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    if (!activity.mCanSeek)
                        activity.mCanSeek = true;
                    //don't spam the logs
                    break;
                case EventHandler.MediaPlayerEncounteredError:
                    Log.i(TAG, "MediaPlayerEncounteredError");
                    activity.encounteredError();
                    break;
                case EventHandler.HardwareAccelerationError:
                    Log.i(TAG, "HardwareAccelerationError");
                    activity.handleHardwareAccelerationError();
                    break;
                case EventHandler.MediaPlayerTimeChanged:
                    // avoid useless error logs
                    break;
                case EventHandler.MediaPlayerESAdded:
                   /* if (!activity.mHasMenu && activity.mLibVLC.getVideoTracksCount() < 1) {
                        Log.i(TAG, "No video track, open in audio mode");
                        activity.switchToAudioMode();
                    }*/
                	Log.e("MediaPlayerESAdded", "no support MediaPlayerESAdded");
                    // no break here, we want to invalidate tracks
                case EventHandler.MediaPlayerESDeleted:
                    activity.invalidateESTracks(msg.getData().getInt("data"));
                    break;
                default:
                    break;
            }
            activity.childVLCHandler(msg);
            activity.updateOverlayPausePlay();
        }
    };

    /**
     * Handle resize of the surface and the overlay
     */
    protected final Handler mHandler = new VideoPlayerHandler((AVideoPlayerActivity)getActitity());

    private static class VideoPlayerHandler extends WeakHandler<AVideoPlayerActivity> {
        public VideoPlayerHandler(AVideoPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            AVideoPlayerActivity activity = getOwner();
            if(activity == null) // WeakReference could be GC'ed early
                return;

            switch (msg.what) {
                case FADE_OUT:
                    activity.hideOverlay(false);
                    break;
                case SHOW_PROGRESS:
                    int pos = activity.setOverlayProgress();
                    if (activity.canShowProgress()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
                case SURFACE_LAYOUT:
                    activity.changeSurfaceLayout();
                    break;
                case FADE_OUT_INFO:
                    activity.fadeOutInfo();
                    break;
                case AUDIO_SERVICE_CONNECTION_SUCCESS:
                    activity.startPlayback();
                    break;
                case AUDIO_SERVICE_CONNECTION_FAILED:
                    activity.myfinish();
                    break;
               /* case END_DELAY_STATE:
                    activity.endDelaySetting();*/
                case RESET_BACK_LOCK:
                    activity.mLockBackButton = true;
            }
            activity.childPlayHandler(msg);
        }
    };

    protected boolean canShowProgress() {
        //return !mDragging && mShowing && mLibVLC.isPlaying();
    	return mLibVLC.isPlaying();
    }

    protected void endReached() {
        if(mMediaListPlayer.expand(savedIndexPosition) == 0) {
            Log.d(TAG, "Found a video playlist, expanding it");
            mEventHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMedia();
                }
            }, 1000);
        } else {
            /* Exit player when reaching the end */
            mEndReached = true;
            //finish();
        }
    }

    protected void encounteredError() {
        if (isFinishing())
            return;
        /* Encountered Error, exit player with a message */
        mAlertDialog = new AlertDialog.Builder(getActitity())
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                myfinish();
            }
        })
        .setTitle(R.string.encountered_error_title)
        .setMessage(getURLErrorText())
        .create();
        mAlertDialog.show();
    }

    public void eventHardwareAccelerationError() {
        EventHandler em = EventHandler.getInstance();
        em.callback(EventHandler.HardwareAccelerationError, new Bundle());
    }

    protected void handleHardwareAccelerationError() {
        mHardwareAccelerationError = true;
        if (mSwitchingView)
            return;
        mLibVLC.stop();
        /* 硬件编码错误直接使用软件解码*/
        mDisabledHardwareAcceleration = true;
        mPreviousHardwareAccelerationMode = mLibVLC.getHardwareAcceleration();
        mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
        loadMedia();
      /*  mAlertDialog = new AlertDialog.Builder(getActitity())
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mDisabledHardwareAcceleration = true;
                mPreviousHardwareAccelerationMode = mLibVLC.getHardwareAcceleration();
                mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
                loadMedia();
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        })
        .setTitle(R.string.hardware_acceleration_error_title)
        .setMessage(R.string.hardware_acceleration_error_message)
        .create();
        if(!isFinishing())
            mAlertDialog.show();*/
    }
   
    protected void handleVout(Message msg) {
        if (msg.getData().getInt("data") == 0 && !mEndReached) {
            /* Video track lost, open in audio mode */
            Log.i(TAG, "Video track lost, switching to audio");
            mSwitchingView = true;
            myfinish();
        }
    }

    /*public void switchToAudioMode() {
        if (mHardwareAccelerationError)
            return;
        mSwitchingView = true;
        // Show the MainActivity if it is not in background.
        if (getIntent().getAction() != null
            && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Intent i = new Intent(this, MainActivity.class);
            if (!Util.isCallable(i)){
                try {
                    i = new Intent(this, Class.forName("org.videolan.vlc.gui.tv.audioplayer.AudioPlayerActivity"));
                } catch (ClassNotFoundException e) {
                    return;
                }
            }
            startActivity(i);
        }
        finish();
    }
*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void changeSurfaceLayout() {
        int sw;
        int sh;

        // get screen size
//        if (mPresentation == null) {
            sw = getWindow().getDecorView().getWidth();
            sh = getWindow().getDecorView().getHeight();
      /*  } else {
            sw = mPresentation.getWindow().getDecorView().getWidth();
            sh = mPresentation.getWindow().getDecorView().getHeight();
        }*/
        if (mLibVLC != null && !mLibVLC.useCompatSurface())
            mLibVLC.setWindowSize(sw, sh);

        double dw = sw, dh = sh;
        boolean isPortrait;

       /* if (mPresentation == null) {
            // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
            
        } else {
            isPortrait = false;
//        }*/
         isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }

        // compute the aspect ratio
        double ar, vw;
        if (mSarDen == mSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double)mVideoVisibleWidth / (double)mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double)mSarNum / mSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = dw / ar;
                break;
            case SURFACE_FIT_VERTICAL:
                dw = dh * ar;
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = vw;
                break;
        }

        SurfaceView surface;
        SurfaceView subtitlesSurface;
        FrameLayout surfaceFrame;

//        if (mPresentation == null) {
            surface = mSurfaceView;
            subtitlesSurface = mSubtitlesSurfaceView;
            surfaceFrame = mSurfaceFrame;
        /*} else {
            surface = mPresentation.mSurfaceView;
            subtitlesSurface = mPresentation.mSubtitlesSurfaceView;
            surfaceFrame = mPresentation.mSurfaceFrame;
        }*/

        // set display size
        LayoutParams lp = surface.getLayoutParams();
        lp.width  = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        surface.setLayoutParams(lp);
       
        subtitlesSurface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = surfaceFrame.getLayoutParams();
        lp.width = (int) Math.floor(dw);
        lp.height = (int) Math.floor(dh);
        surfaceFrame.setLayoutParams(lp);

        surface.invalidate();
        subtitlesSurface.invalidate();
    }

    /**
     * show/hide the overlay
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (mDetector.onTouchEvent(event))
            return true;*/
        if (mIsLocked) {
            // locked, only handle show/hide & ignore all actions
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!mShowing) {
                    showOverlay();
                } else {
                    hideOverlay(true);
                }
            }
            return false;
        }

        DisplayMetrics screen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screen);

        if (mSurfaceYDisplayRange == 0)
            mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);

        float x_changed, y_changed;
        if (mTouchX != -1 && mTouchY != -1) {
            y_changed = event.getRawY() - mTouchY;
            x_changed = event.getRawX() - mTouchX;
        } else {
            x_changed = 0f;
            y_changed = 0f;
        }


        // coef is the gradient's move to determine a neutral zone
        float coef = Math.abs (y_changed / x_changed);
        float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);
        float delta_y = Math.max(1f,((mInitTouchY - event.getRawY()) / screen.xdpi + 0.5f)*2f);

        /* Offset for Mouse Events */
        int[] offset = new int[2];
        mSurfaceView.getLocationOnScreen(offset);
        int xTouch = Math.round((event.getRawX() - offset[0]) * mVideoWidth / mSurfaceView.getWidth());
        int yTouch = Math.round((event.getRawY() - offset[1]) * mVideoHeight / mSurfaceView.getHeight());

        switch (event.getAction()) {

        /*case MotionEvent.ACTION_DOWN:
            // Audio
            mTouchY = mInitTouchY = event.getRawY();
            mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mTouchAction = TOUCH_NONE;
            // Seek
            mTouchX = event.getRawX();
            // Mouse events for the core
            LibVLC.sendMouseEvent(MotionEvent.ACTION_DOWN, 0, xTouch, yTouch);
            break;*/

        /*case MotionEvent.ACTION_MOVE:
            // Mouse events for the core
            LibVLC.sendMouseEvent(MotionEvent.ACTION_MOVE, 0, xTouch, yTouch);

            // No volume/brightness action if coef < 2 or a secondary display is connected
            //TODO : Volume action when a secondary display is connected
            if (mTouchAction != TOUCH_SEEK && coef > 2) {// && mPresentation == null
                if (Math.abs(y_changed/mSurfaceYDisplayRange) < 0.05)
                    return false;
                mTouchY = event.getRawY();
                mTouchX = event.getRawX();
                // Volume (Up or Down - Right side)
                if (!mEnableBrightnessGesture || (int)mTouchX > (3 * screen.widthPixels / 5)){
                    doVolumeTouch(y_changed);
                    hideOverlay(true);
                }
                // Brightness (Up or Down - Left side)
                if (mEnableBrightnessGesture && (int)mTouchX < (2 * screen.widthPixels / 5)){
                    doBrightnessTouch(y_changed);
                    hideOverlay(true);
                }
            } else {
                // Seek (Right or Left move)
                doSeekTouch(Math.round(delta_y), xgesturesize, false);
            }
            break;*/

        case MotionEvent.ACTION_UP:
            // Mouse events for the core
            //LibVLC.sendMouseEvent(MotionEvent.ACTION_UP, 0, xTouch, yTouch);

            if (mTouchAction == TOUCH_NONE) {
                if (!mShowing) {
                    showOverlay();
                } else {
                	if(hidenSubMenu() == true)
                		return true;
                    hideOverlay(true);
                }
            }
            /*// Seek
            if (mTouchAction == TOUCH_SEEK)
                doSeekTouch(Math.round(delta_y), xgesturesize, true);*/
            mTouchX = -1f;
            mTouchY = -1f;
            break;
        }
        return mTouchAction != TOUCH_NONE;
    }

    protected void setAudioVolume(int vol,boolean showInfo) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);

        /* Since android 4.3, the safe volume warning dialog is displayed only with the FLAG_SHOW_UI flag.
         * We don't want to always show the default UI volume, so show it only when volume is not set. */
        int newVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (vol != newVol)
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_SHOW_UI);
        vol = vol*100 / mAudioMax;
        if(showInfo){
        	showInfo(getString(R.string.volume) + '\u00A0' + Integer.toString(vol) + '%');
        }
    }

    protected void mute(boolean mute) {
        mMute = mute;
        if (mMute)
            mVolSave = mLibVLC.getVolume();
        mLibVLC.setVolume(mMute ? 0 : mVolSave);
    }

    protected void updateMute () {
        mute(!mMute);
        showInfo(mMute ? R.string.sound_off : R.string.sound_on,1000);
    }

    @TargetApi(android.os.Build.VERSION_CODES.FROYO)
    protected void initBrightnessTouch() {
        float brightnesstemp = 0.6f;
        // Initialize the layoutParams screen brightness
        try {
            if (LibVlcUtil.isFroyoOrLater() &&
                    Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                mRestoreAutoBrightness = android.provider.Settings.System.getInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
            } else {
                brightnesstemp = android.provider.Settings.System.getInt(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightnesstemp;
        getWindow().setAttributes(lp);
        mIsFirstBrightnessGesture = false;
    }

    protected void doBrightnessTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
            return;
        if (mIsFirstBrightnessGesture) initBrightnessTouch();
            mTouchAction = TOUCH_BRIGHTNESS;

        // Set delta : 2f is arbitrary for now, it possibly will change in the future
        float delta = - y_changed / mSurfaceYDisplayRange;

        changeBrightness(delta);
    }

    protected void changeBrightness(float delta) {
        // Estimate and adjust Brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =  Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1);
        // Set Brightness
        getWindow().setAttributes(lp);
        int brightness = Math.round(lp.screenBrightness * 100);
       // showInfoWithVerticalBar(getString(R.string.brightness) + '\u00A0' + brightness + '%', 1000, brightness);
    }

    /**
     * handle changes of the seekbar (slicer)
     */
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            showOverlayTimeout(OVERLAY_INFINITE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            showOverlay(true);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && mCanSeek) {
                seek(progress);
                setOverlayProgress();
                mTime.setText(Strings.millisToString(progress));
                showInfo(Strings.millisToString(progress));
            }

        }
    };

   

    protected interface TrackSelectedListener {
        public boolean onTrackSelected(int trackID);
    }
   
    protected void showNavMenu() {
        /* Try to return to the menu. */
        /* FIXME: not working correctly in all cases */
        mLibVLC.setTitle(0);
    }

    /**
    *
    */
    protected final OnClickListener mPlayPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doPlayPause();
        }
    };



    protected  void doPlayPause() {
        if (mLibVLC.isPlaying()) {
            pause();
            showOverlayTimeout(OVERLAY_INFINITE);
        } else {
            play();
            showOverlayTimeout(OVERLAY_TIMEOUT);
        }
        updateOverlayPausePlay();
    }

    protected long getTime() {
        long time = mLibVLC.getTime();
        playtime = time;
        if (mForcedTime != -1 && mLastTime != -1) {
            /* XXX: After a seek, mLibVLC.getTime can return the position before or after
             * the seek position. Therefore we return mForcedTime in order to avoid the seekBar
             * to move between seek position and the actual position.
             * We have to wait for a valid position (that is after the seek position).
             * to re-init mLastTime and mForcedTime to -1 and return the actual position.
             */
            if (mLastTime > mForcedTime) {
                if (time <= mLastTime && time > mForcedTime)
                    mLastTime = mForcedTime = -1;
            } else {
                if (time > mForcedTime)
                    mLastTime = mForcedTime = -1;
            }
        }
        return mForcedTime == -1 ? time : mForcedTime;
    }

    protected void seek(long position) {
        seek(position, mLibVLC.getLength());
    }

    protected void seek(long position, float length) {
        mForcedTime = position;
        mLastTime = mLibVLC.getTime();
        if (length == 0f)
            mLibVLC.setTime(position);
        else
            mLibVLC.setPosition(position / length);
    }

    protected void seekDelta(int delta) {
        // unseekable stream
        if(mLibVLC.getLength() <= 0 || !mCanSeek) return;

        long position = getTime() + delta;
        if (position < 0) position = 0;
        seek(position);
        showOverlay();
    }


    /**
     *
     */
    protected final OnClickListener mSizeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            resizeVideo();
        }
    };

    protected void resizeVideo() {
        if (mCurrentSize < SURFACE_ORIGINAL) {
            mCurrentSize++;
        } else {
            mCurrentSize = 0;
        }
        changeSurfaceLayout();
        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                showInfo(R.string.surface_best_fit, 1000);
                break;
            case SURFACE_FIT_HORIZONTAL:
                showInfo(R.string.surface_fit_horizontal, 1000);
                break;
            case SURFACE_FIT_VERTICAL:
                showInfo(R.string.surface_fit_vertical, 1000);
                break;
            case SURFACE_FILL:
                showInfo(R.string.surface_fill, 1000);
                break;
            case SURFACE_16_9:
                showInfo("16:9", 1000);
                break;
            case SURFACE_4_3:
                showInfo("4:3", 1000);
                break;
            case SURFACE_ORIGINAL:
                showInfo(R.string.surface_original, 1000);
                break;
        }
        showOverlay();
    }


    /**
     * attach and disattach surface to the lib
     */
    protected final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(mLibVLC != null) {
                final Surface newSurface = holder.getSurface();
                if (mSurface != newSurface) {
                    mSurface = newSurface;
                    Log.d(TAG, "surfaceChanged: " + mSurface);
                    mLibVLC.attachSurface(mSurface, (AVideoPlayerActivity)getActitity());
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        	 Log.d(TAG, "surfaceCreated");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");
            if(mLibVLC != null) {
                mSurface = null;
                //FIXME 修改87
                if(mLibVLC.isPlaying())
                	mLibVLC.pause();
                mLibVLC.detachSurface();
                if(!isFinishing() && needResume()){
                	playtime = mLibVLC.getTime();
                }
            }
        }
    };

    protected final SurfaceHolder.Callback mSubtitlesSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(mLibVLC != null) {
                final Surface newSurface = holder.getSurface();
                if (mSubtitleSurface != newSurface) {
                    mSubtitleSurface = newSurface;
                    mLibVLC.attachSubtitlesSurface(mSubtitleSurface);
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(mLibVLC != null) {
                mSubtitleSurface = null;
                mLibVLC.detachSubtitlesSurface();
            }
        }
    };

    /**
     * show overlay
     * @param forceCheck: adjust the timeout in function of playing state
     */
    protected void showOverlay(boolean forceCheck) {
        if (forceCheck)
            mOverlayTimeout = 0;
        showOverlayTimeout(0);
    }

    /**
     * show overlay with the previous timeout value
     */
    protected void showOverlay() {
        showOverlay(false);
    }


    /**
     * show overlay
     */
    protected void showOverlayTimeout(int timeout) {
    	/*if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE){
    		 mHandler.removeMessages(FADE_OUT);
    		 mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), OVERLAY_TIMEOUT);
    		 return;
    	}*/
        if (timeout != 0)
            mOverlayTimeout = timeout;
        if (mOverlayTimeout == 0)
            mOverlayTimeout = mLibVLC.isPlaying() ? OVERLAY_TIMEOUT : OVERLAY_INFINITE;
        if (mIsNavMenu){
            mShowing = true;
            return;
        }
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (!mShowing) {
            mShowing = true;
            if (!mIsLocked) {
                /*if (mTracks != null)
                    mTracks.setVisibility(View.VISIBLE);*/
//                if (mAdvOptions !=null)
//                    mAdvOptions.setVisibility(View.VISIBLE);
               // mSize.setVisibility(View.VISIBLE);
                dimStatusBar(false);
            	//dimStatusBar(true);
            }
            mOverlayProgress.setVisibility(View.VISIBLE);
        }
        mHandler.removeMessages(FADE_OUT);
        //不在自动隐藏
        /*if (mOverlayTimeout != OVERLAY_INFINITE)
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), mOverlayTimeout);*/
        updateOverlayPausePlay();
    }


    /**
     * hider overlay
     */
    protected void hideOverlay(boolean fromUser) {
        if (mShowing) {
            mHandler.removeMessages(FADE_OUT);
            //mHandler.removeMessages(SHOW_PROGRESS);
            Log.i(TAG, "remove View!");
            if (mOverlayTips != null) mOverlayTips.setVisibility(View.INVISIBLE);
            if (!fromUser && !mIsLocked) {
                mOverlayProgress.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                //mPlayPause.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                /*if (mTracks != null)
                    mTracks.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));*/
               /* if (mAdvOptions !=null)
                    mAdvOptions.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));*/
            } 
            /*else
                mSize.setVisibility(View.INVISIBLE);*/
            mOverlayProgress.setVisibility(View.INVISIBLE);
            //mPlayPause.setVisibility(View.INVISIBLE);
           /* if (mTracks != null)
                mTracks.setVisibility(View.INVISIBLE);*/
           /* if (mAdvOptions !=null)
                mAdvOptions.setVisibility(View.INVISIBLE);*/
            mShowing = false;
            dimStatusBar(true);
        } else if (!fromUser) {
            /*
             * Try to hide the Nav Bar again.
             * It seems that you can't hide the Nav Bar if you previously
             * showed it in the last 1-2 seconds.
             */
            dimStatusBar(true);
        }
    }

    /**
     * Dim the status bar and/or navigation icons when needed on Android 3.x.
     * Hide it on Android 4.0 and later
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void dimStatusBar(boolean dim) {
        if (!LibVlcUtil.isHoneycombOrLater() || mIsNavMenu)
            return;
        int visibility = 0;
        int navbar = 0;
        
        if (!AndroidDevices.hasCombBar() && LibVlcUtil.isJellyBeanOrLater()) {
            visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            navbar = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (dim) {
            navbar |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            if (!AndroidDevices.hasCombBar()) {
                navbar |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                if (LibVlcUtil.isKitKatOrLater())
                    visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
                visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            visibility |= View.SYSTEM_UI_FLAG_VISIBLE;
        }

        if (AndroidDevices.hasNavBar())
            visibility |= navbar;
        getWindow().getDecorView().setSystemUiVisibility(visibility);
    }

    protected void updateOverlayPausePlay() {
        if (mLibVLC == null)
            return;
        mPlayPause.setImageResource(mLibVLC.isPlaying() ? R.drawable.ic_pause
                : R.drawable.ic_play);
    }

    /**
     * update the overlay
     */
    protected int setOverlayProgress() {
        if (mLibVLC == null) {
            return 0;
        }
        int length = (int) mLibVLC.getLength();
        int time = (int) getTime();
        mSeekbar.setMax(length);
        mSeekbar.setProgress(time);
        // Update all view elements
        if (time >= 0) mTime.setText(Strings.millisToString(time));

        return time;
    }

    protected void invalidateESTracks(int type) {
        switch (type) {
            case Media.Track.Type.Audio:
                mAudioTracksList = null;
                break;
            case Media.Track.Type.Text:
                mSubtitleTracksList = null;
                break;
        }
    }

    protected void setESTracks() {
        if (mLastAudioTrack >= 0) {
            mLibVLC.setAudioTrack(mLastAudioTrack);
            mLastAudioTrack = -1;
        }
        if (mLastSpuTrack >= -1) {
            mLibVLC.setSpuTrack(mLastSpuTrack);
            mLastSpuTrack = -2;
        }
    }

    protected void setESTrackLists() {
        if (mAudioTracksList == null && mLibVLC.getAudioTracksCount() > 1)
            mAudioTracksList = mLibVLC.getAudioTrackDescription();
        if (mSubtitleTracksList == null && mLibVLC.getSpuTracksCount() > 0)
            mSubtitleTracksList = mLibVLC.getSpuTrackDescription();
    }


    /**
     *
     */
    protected void play()
    {
    	wasPaused = false;
        mLibVLC.play();
        mSurfaceView.setKeepScreenOn(true);
    }

    /**
     *
     */
    protected void pause() {
    	wasPaused = true;
        mLibVLC.pause();
        mSurfaceView.setKeepScreenOn(false);
    }

    /*
     * Additionnal method to prevent alert dialog to pop up
     */
    protected void loadMedia(boolean fromStart) {
        getIntent().putExtra(PLAY_EXTRA_FROM_START, fromStart);
        loadMedia();
    }

    /**
     * External extras:
     * - position (long) - position of the video to start with (in ms)
     */
    protected void loadMedia() {
        mLocation = null;
        Uri data;
        long intentPosition = -1; // position passed in by intent (ms)
        long mediaLength = 0l;

       
        /*
         * If the activity has been paused by pressing the power button, then
         * pressing it again will show the lock screen.
         * But onResume will also be called, even if vlc-android is still in
         * the background.
         * To workaround this, pause playback if the lockscreen is displayed.
         */
        final KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);

        if (getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            /* Started from external application 'content' */
            data = getIntent().getData();
            if (data != null
                    && data.getScheme() != null
                    && data.getScheme().equals("content")) {


                // Mail-based apps - download the stream to a temporary file and play it
                if(data.getHost().equals("com.fsck.k9.attachmentprovider")
                       || data.getHost().equals("gmail-ls")) {
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        Cursor cursor = getContentResolver().query(data,
                                new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            String filename = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                            cursor.close();
                            Log.i(TAG, "Getting file " + filename + " from content:// URI");

                            is = getContentResolver().openInputStream(data);
                            os = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Download/" + filename);
                            byte[] buffer = new byte[1024];
                            int bytesRead = 0;
                            while((bytesRead = is.read(buffer)) >= 0) {
                                os.write(buffer, 0, bytesRead);
                            }
                            mLocation = LibVLC.PathToURI(Environment.getExternalStorageDirectory().getPath() + "/Download/" + filename);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't download file from mail URI");
                        encounteredError();
                        return;
                    } finally {
                        Util.close(is);
                        Util.close(os);
                    }
                }
                // Media or MMS URI
                else {
                    try {
                        Cursor cursor = getContentResolver().query(data,
                                new String[]{ MediaStore.Video.Media.DATA }, null, null, null);
                        if (cursor != null) {
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                            if (cursor.moveToFirst())
                                mLocation = LibVLC.PathToURI(cursor.getString(column_index));
                            cursor.close();
                        }
                        // other content-based URI (probably file pickers)
                        else {
                            mLocation = data.getPath();
                        }
                    } catch (Exception e) {
                        mLocation = data.getPath();
                        if (!mLocation.startsWith("file://"))
                            mLocation = "file://"+mLocation;
                        Log.e(TAG, "Couldn't read the file from media or MMS");
                    }
                }
            } /* External application */
            else if (getIntent().getDataString() != null) {
                // Plain URI
                mLocation = getIntent().getDataString();
                // Remove VLC prefix if needed
                if (mLocation.startsWith("vlc://")) {
                    mLocation = mLocation.substring(6);
                }
                // Decode URI
                if (!mLocation.contains("/")){
                    try {
                        mLocation = URLDecoder.decode(mLocation,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.w(TAG, "UnsupportedEncodingException while decoding MRL " + mLocation);
                    }
                }
            } else {
                Log.e(TAG, "Couldn't understand the intent");
                encounteredError();
                return;
            }

            // Try to get the position
            if(getIntent().getExtras() != null)
                intentPosition = getIntent().getExtras().getLong("position", -1);
        } /* ACTION_VIEW */
        /* Started from VideoListActivity */
        else  {
            mLocation = getVideoLocation();
            if (getIntent().hasExtra(PLAY_EXTRA_SUBTITLES_LOCATION))
                mSubtitleSelectedFiles.add(getIntent().getExtras().getString(PLAY_EXTRA_SUBTITLES_LOCATION));
        }

        /* WARNING: hack to avoid a crash in mediacodec on KitKat.
         * Disable hardware acceleration if the media has a ts extension. */
        if (mLocation != null && LibVlcUtil.isKitKatOrLater()) {
            String locationLC = mLocation.toLowerCase(Locale.ENGLISH);
            if (locationLC.endsWith(".ts")
                || locationLC.endsWith(".tts")
                || locationLC.endsWith(".m2t")
                || locationLC.endsWith(".mts")
                || locationLC.endsWith(".m2ts")) {
                mDisabledHardwareAcceleration = true;
                mPreviousHardwareAccelerationMode = mLibVLC.getHardwareAcceleration();
                mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
            }
        }

        /* prepare playback */
        AudioServiceController.getInstance().stop(); // Stop the previous playback.
        if ( mLocation != null && mLocation.length() > 0) {
            mMediaListPlayer.getMediaList().clear();
            final Media media = new Media(mLibVLC, mLocation);
            media.parse(); // FIXME: parse should'nt be done asynchronously
            media.release();
            mMediaListPlayer.getMediaList().add(new MediaWrapper(media));
            savedIndexPosition = mMediaListPlayer.getMediaList().size() - 1;
        }
        mCanSeek = true;

        // Start playback & seek
        VLCInstance.setAudioHdmiEnabled(this, mHasHdmiAudio);
        mMediaListPlayer.playIndex(savedIndexPosition, wasPaused);
        /*if(mCanSeek && playtime >0)
        	seek(playtime, mediaLength);*/
        if(playtime>0 && needResume()){
        	seek(playtime);
        }
        // Get possible subtitles
        ArrayList<String> prefsList = new ArrayList<String>();
        for(String x : prefsList){
            if(!mSubtitleSelectedFiles.contains(x))
                mSubtitleSelectedFiles.add(x);
         }

        
       
    }

    @SuppressWarnings("deprecation")
    protected int getScreenRotation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /* Android 2.2 has getRotation */) {
            try {
                Method m = display.getClass().getDeclaredMethod("getRotation");
                return (Integer) m.invoke(display);
            } catch (Exception e) {
                return Surface.ROTATION_0;
            }
        } else {
            return display.getOrientation();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    protected int getScreenOrientation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rot = getScreenRotation();
        /*
         * Since getRotation() returns the screen's "natural" orientation,
         * which is not guaranteed to be SCREEN_ORIENTATION_PORTRAIT,
         * we have to invert the SCREEN_ORIENTATION value if it is "naturally"
         * landscape.
         */
        @SuppressWarnings("deprecation")
        boolean defaultWide = display.getWidth() > display.getHeight();
        if(rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
            defaultWide = !defaultWide;
        if(defaultWide) {
            switch (rot) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_180:
                // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            case Surface.ROTATION_270:
                // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            default:
                return 0;
            }
        } else {
            switch (rot) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_180:
                // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_270:
                // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            default:
                return 0;
            }
        }
    }


    public void showAdvancedOptions(View v) {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void createPresentation() {
        if (mMediaRouter == null || mEnableCloneMode)
            return;

        // Get the current route and its presentation display.
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
            MediaRouter.ROUTE_TYPE_LIVE_VIDEO);

       /* Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;

        if (presentationDisplay != null) {
            // Show a new presentation if possible.
            Log.i(TAG, "Showing presentation on display: " + presentationDisplay);
            mPresentation = new SecondaryDisplay(this, mLibVLC, presentationDisplay);
            mPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPresentation.show();
                mPresentationDisplayId = presentationDisplay.getDisplayId();
            } catch (WindowManager.InvalidDisplayException ex) {
                Log.w(TAG, "Couldn't show presentation!  Display was removed in "
                        + "the meantime.");
                mPresentation = null;
            }
        } else
            Log.i(TAG, "No secondary display detected");*/
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void removePresentation() {
        if (mMediaRouter == null)
            return;

        // Dismiss the current presentation if the display has changed.
        Log.i(TAG, "Dismissing presentation because the current route no longer "
                + "has a presentation display.");
       /* if (mPresentation != null) mPresentation.dismiss();
        mPresentation = null;*/
        mPresentationDisplayId = -1;
        stopPlayback();

        recreate();
    }

    /**
     * Listens for when presentations are dismissed.
     */
   /* protected final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (dialog == mPresentation) {
                Log.i(TAG, "Presentation was dismissed.");
                mPresentation = null;
            }
        }
    };*/


    

   
    public void onClickOverlayTips(View v) {
        mOverlayTips.setVisibility(View.GONE);
    }


    protected void updateNavStatus() {
        mHasMenu = mLibVLC.getChapterCountForTitle(0) > 1 && mLibVLC.getTitleCount() > 1 && !mLocation.endsWith(".mkv");
        mIsNavMenu = mHasMenu && mLibVLC.getTitle() == 0;
        /***
         * HACK ALERT: assume that any media with >1 titles = DVD with menus
         * Should be replaced with a more robust title/chapter selection popup
         */

        Log.d(TAG,
                "updateNavStatus: getChapterCountForTitle(0) = "
                        + mLibVLC.getChapterCountForTitle(0)
                        + ", getTitleCount() = " + mLibVLC.getTitleCount());
        if (mIsNavMenu) {
            /*
             * Keep the overlay hidden in order to have touch events directly
             * transmitted to navigation handling.
             */
            hideOverlay(false);
        }
        else if (mHasMenu)
            setESTracks();
    }

    protected GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {}

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {}

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };
    
    protected abstract Context getActitity(); 
    protected abstract String getVideoTitle();
    protected abstract String getVideoLocation();
    protected abstract void  childVLCHandler(Message msg);
    protected abstract void  childPlayHandler(Message msg);
    protected abstract boolean hidenSubMenu();
    protected abstract int getURLErrorText();
    protected abstract void myfinish();
    protected abstract boolean needResume();
}
