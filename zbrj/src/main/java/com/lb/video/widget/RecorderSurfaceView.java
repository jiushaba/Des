package com.lb.video.widget;

import java.io.IOException;
import java.nio.ShortBuffer;

import org.bytedeco.javacpp.avcodec.AVCodec.Init_AVCodecContext;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.lb.common.util.Log;
import com.lb.video.comm.ConstantVideo;
import com.lb.video.data.ShareData;

public class RecorderSurfaceView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback{
	private final String LOG_TAG="RecorderSurfaceView";
	/*公用共享数据*/
	public ShareData shareData;
	public String ffmpeg_link = "rtmp://123.57.253.199:1935/live/";
	/*录像时间毫秒*/
	public int RECORD_TIME = 3600*1000;
	int imageWidth = 320;
	public int imageHeight = 240;
	public int frameRate = 30;
	public int sampleAudioRateInHz = 16000;
	public volatile boolean runAudioThread = true;
	private long startTime = 0;
	private boolean recording = false;
	private volatile FFmpegFrameRecorder recorder;
	private boolean isPreviewOn = true;
	public boolean init_success = false;
	
	private AudioRecord audioRecord;
	private AudioRecordRunnable audioRecordRunnable;
	private IplImage yuvIplimage = null;
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private int cameraPosition;
	private int cameraCount = -1;
	public RecorderSurfaceView(Context context) {
		super(context);
		init();
	}
	public RecorderSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RecorderSurfaceView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	private void init() {
		 yuvIplimage = IplImage.create(imageWidth, imageHeight, opencv_core.IPL_DEPTH_8U, 2);
		 try{
			 cameraCount = Camera.getNumberOfCameras();
			 this.changeCamera();
		 }catch(Exception e){
			 Log.e(LOG_TAG,e);
			 alert("获取摄像头失败", ConstantVideo.INFO);
		 }
	}

	
	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera.setPreviewCallbackWithBuffer(this);
        changeCamera();
       
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			stopPreview();
            mHolder.addCallback(null);
            this.releaseCamera();
            realeaseRecorder();
        } catch (RuntimeException e) {
            
        }
		
	}
	
	 public void startPreview() {
         if (!isPreviewOn && mCamera != null) {
             isPreviewOn = true;
             mCamera.startPreview();
         }
     }

     public void stopPreview() {
         if (isPreviewOn && mCamera != null) {
             isPreviewOn = false;
             mCamera.stopPreview();
         }
    }
    /** 
     * 释放摄像头
     */
    private void releaseCamera(){
    	if(mCamera != null){
        	try{
        		mCamera.setPreviewCallback(null);
        		mCamera.release();
        	}catch(Exception e){
        		com.lb.common.util.Log.e(LOG_TAG, e);
        	}
        }
    }
    private void realeaseRecorder(){
    	 if(recorder != null){
			try {
				recorder.release();
				recorder = null;
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				Log.e(LOG_TAG, e);
			}
    	 }
    	 recording = false;
    	 audioRecordRunnable = null;
    	 audioRecord = null;
    }
    public void changeCamera(){
    	//cameraDevice = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
        if(mCamera != null && cameraCount<=0){
        	alert("无法读取摄像机资源，无法切换",ConstantVideo.INFO);
        	return;
        }
    	if(mCamera != null){
    		stopPreview();
        	mHolder.addCallback(null);
        	mHolder = null;
        	mCamera.setPreviewCallback(null);
        	mCamera.release();//释放资源
    	}else{
    		//设置成前端，后面切换成后端
    		cameraPosition = CameraInfo.CAMERA_FACING_FRONT;
    	}
    	
    	mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
    	try{
    		//无法判断，直接打开一个即可
    		if(cameraCount <1){
    			mCamera = Camera.open();
    		}else{
    			for(int i = 0; i < cameraCount; i++ ) {
    			}
    		}
    		for(int i=0 ; i< cameraCount ; i++){
                if(cameraPosition == CameraInfo.CAMERA_FACING_BACK) {
                	mCamera = Camera.open(i);//打开当前选中的摄像头
                   cameraPosition = CameraInfo.CAMERA_FACING_FRONT;
                    break;
                }else if(cameraPosition == CameraInfo.CAMERA_FACING_FRONT){
                	mCamera = Camera.open(i);//打开当前选中的摄像头
                    cameraPosition = CameraInfo.CAMERA_FACING_BACK;
                     break;
                }
    		}
    		 Log.v(LOG_TAG,"Setting imageWidth: " + imageWidth + " imageHeight: " + imageHeight + " frameRate: " + frameRate);
             Camera.Parameters camParams = mCamera.getParameters();
             camParams.setPreviewSize(imageWidth, imageHeight);
             camParams.setPreviewFrameRate(frameRate);
             mCamera.setParameters(camParams);
             mCamera.setPreviewDisplay(mHolder);
             mCamera.setPreviewCallback(this);    	
     	}catch(Exception e){
     		init_success = false;
     		alert("打开摄像头失败", ConstantVideo.ERROR);
     		Log.e(LOG_TAG, "change fail ",e);
     	}
    }

    class AudioRecordRunnable implements Runnable {
        @Override
        public void run() {
        	/*暂时不设定，感觉对界面影响还是很大的*/
           // android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            int bufferSize;
            ShortBuffer audioData;
            int bufferReadResult;

            bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz, 
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioRateInHz, 
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            audioData = ShortBuffer.allocate(bufferSize);
            Log.d(LOG_TAG, "audioRecord.startRecording()");
            audioRecord.startRecording();

            /* ffmpeg_audio encoding loop */
			while (runAudioThread) {
				bufferReadResult = audioRecord.read(audioData.array(), 0,
						audioData.capacity());
				audioData.limit(bufferReadResult);
				if (bufferReadResult > 0) {
					if (recording) {
						try {
							recorder.record(audioData);
							Log.v(LOG_TAG, "recording audiu data");
						} catch (FFmpegFrameRecorder.Exception e) {
							Log.v(LOG_TAG, e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
            Log.v(LOG_TAG,"AudioThread Finished, release audioRecord");
            /* encoding finish, release recorder */
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                Log.v(LOG_TAG,"audioRecord released");
            }
        }
    }
    private void alert(String msg,int type){
    	
    }
    
}
