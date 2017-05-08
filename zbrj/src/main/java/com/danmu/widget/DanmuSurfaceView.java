
package com.danmu.widget;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import master.flame.danmaku.controller.DanmakuFilters.IDanmakuFilter;
import master.flame.danmaku.controller.DrawHandler.Callback;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.AlphaValue;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.R2LDanmaku;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.ui.widget.DanmakuSurfaceView;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

import com.danmu.data.DanmuContentData;
import com.lb.zbrj.controller.PersonController;

public class DanmuSurfaceView extends DanmakuSurfaceView {
//	private  long initialDuration = 5000;
	private BaseDanmakuParser parser;
	private long damustartTime=0;
	private String partten="yyyy-MM-dd HH:mm:ss";
	private IDanmakuFilter<Integer> quantityFilter;
	public static int screenHeight = 0;
	/*字体，速度，位置设置
	 * 字体：0 大 ,1 中 , 2 小
		位置: 0  上  1 中    2 下
		速度： 0  1  2 最慢 
	 * */
	public int fontSizeLevel = 1;
	public int speedLevel = 1;
	public int lookSpeedLevel = -1;
	public int locationLevel = 1;
	private float[] fontSizes = new float[]{18f,22f,26f};
	private int[] speedes = new int[]{4000,7000,10000};
	private float[] locationes = new float[]{0.25f,0.5f,0.75f};
	DanmakuTimer mTimer;
	PersonController personController = new PersonController();
	public DanmuSurfaceView(Context context) {
		super(context);
		init();
	}

	public DanmuSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DanmuSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	private void init(){
		parser = createParser(null);
        setCallback(new Callback() {
            @Override
            public void updateTimer(DanmakuTimer timer) {
            	mTimer = timer;
            }

            @Override
            public void prepared() {
            	start();
            }
        });
        prepare(parser);
        
        showFPS(false);
        enableDanmakuDrawingCache(true);
        DisplayMetrics dm = new DisplayMetrics();
		((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels;
	}
	
  @Override
	public void start() {
		start(damustartTime);
	}

@Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        super.surfaceDestroyed(surfaceHolder);
    }
    public void addDanmakuDisplayByNow(DanmuContentData content){
    	BaseDanmaku danmaku = createDanmaku(content);
    	if(danmaku == null)
    		return;
    	long time = getCurrentTime()+2500;
        danmaku.time = time;
        addDanmaku(danmaku);
    }
    public void addDanmakuDisplayByTime(DanmuContentData content){
    	BaseDanmaku danmaku = createDanmaku(content);
    	if(danmaku == null)
    		return;
   	 	long timemilli = content.getTimemillisecond();
   	 	long time =damustartTime+timemilli;
	   	danmaku.time = time;
	    addDanmaku(danmaku);
    }
	private BaseDanmaku createDanmaku(DanmuContentData content){
		R2LDanmaku danmaku = null;
		float fontSize =1;
        try{
        	fontSize = Float.parseFloat(content.fontsize);
        }catch(Exception e){
        	Log.i("DanmuSurfaceView", "fontsize is not number,use default");
        }
		danmaku = (R2LDanmaku)DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
	    danmaku.text = content.msg;
		danmaku.textSize = generateFontsize(fontSize);
	    danmaku.initY = screenHeight *generateLocation(content.location);
	    //com.lb.common.util.Log.e("location"+screenHeight, danmaku.initY+"");
	    //Log.i("danmaku",danmaku.initY+"");
		danmaku.priority = 1;
        danmaku.isLive = true;
        danmaku.textColor = getDanmuColor(content.account);
        if(lookSpeedLevel >=0){
        	danmaku.duration.setValue(generateSpeed(lookSpeedLevel+""));
        }else{
        	danmaku.duration.setValue(generateSpeed(content.speed));
        }
	    return danmaku;
	}
	
	

	private float generateFontsize(float level) {
		float result = 0f;
		if(level<=0){
			result = fontSizes[0];
		}else if(level >=2){
			result = fontSizes[2];
		}else{
			result = fontSizes[1];
		}
		result = result*(parser.getDisplayer().getDensity());
		return result;
	}
	private int generateSpeed(String speed){
		int i = string2int(speed);
		if(i<=-1)
			i = speedLevel;
		if(i<=0){
			return speedes[0];
		}else if(i>=2){
			return speedes[2];
		}else{
			return speedes[1];
		}
	}
	private float generateLocation(String location){
		int i = string2int(location);
		if(i<=-1)
			i = locationLevel;
		if(i<=0){
			return locationes[0];
		}else if(i>=2){
			return locationes[2];
		}else{
			return locationes[1];
		}
	}
	private BaseDanmakuParser createParser(InputStream stream) {
        
        if(stream==null){
            return new BaseDanmakuParser() {
                
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }
	public void setStartTime(String s){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(partten);
			Date date = sdf.parse(s);
			damustartTime = date.getTime();
		}catch(Exception e){
			Log.e("danmusurfaceView", e.getMessage());
		}
	}
	public void setStartTime(long time){
		try{
			damustartTime = time;
		}catch(Exception e){
			Log.e("danmusurfaceView", e.getMessage());
		}
	}
	public long getStartTime(){
		return damustartTime;
	}
	public void changeFactor(float f){
		DanmakuFactory.MAX_Duration_Scroll_Danmaku.setFactor(f);
		Log.e("danmusurface_factor", f+"");
	}
	public void changeAlpha(float f){
		if(f >= 1.0F){
			AlphaValue.NowAlpha = AlphaValue.OLDMAX;
		}else{
			AlphaValue.NowAlpha = (int)(AlphaValue.OLDMAX * f);
		}
		AlphaValue.MAX =  AlphaValue.NowAlpha;
		Log.e("danmusurface__changeAlpha", AlphaValue.NowAlpha+"");
	}
	@SuppressWarnings("unchecked")
	public void changeInScreenSize(int size){
		DanmakuGlobalConfig.DEFAULT.maxDanmuInScreen = size;	
	}
	private int string2int(String s){
		try{
			if(s != null){
				return Integer.parseInt(s);
			}
			
		}catch(Exception e){
		}
		return -1;
	}
	private int getDanmuColor(String account){
		try{
			if(personController.isFriend(account)){
				return Color.RED;
			}
		}catch(Exception e){
			com.lb.common.util.Log.e(e.getMessage(), e);
		}
		return Color.WHITE;
	}
}
