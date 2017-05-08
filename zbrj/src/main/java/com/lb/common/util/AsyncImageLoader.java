
package com.lb.common.util;


import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

public class AsyncImageLoader {

    private HashMap<String, SoftReference<Drawable>> imageCache;
    private Object lock=new Object();
    private boolean isLock;
    private  Handler handler;

    public AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Drawable>>();
        handler = new Handler();
    }

    public void lock() {
        isLock=true;
    }
    
    public void unlock(){
        isLock=false;
        synchronized(lock){
            lock.notifyAll();
        }
    }
    
    public boolean resize(){
        try {
            imageCache.clear();
            System.gc();
            return true;
        } catch (Exception e) {
        	Log.e(Constants.LOG_TAG, e.getMessage(), e);
            return false;
        }
       
    }
    
    public void loadDrawable(final String imageUrl, final int id, final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            final Drawable drawable = softReference.get();
            if (drawable != null) {
            	handler.post(new Runnable() {
					public void run() {
						imageCallback.imageLoaded(drawable, id, imageUrl);
					}
				});
                return;
            }
        }

        new Thread() {
            @Override
            public void run() {
            	Looper.prepare();
                if (isLock) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                        	Log.e(Constants.LOG_TAG, e.getMessage(), e);
                        }
                    }
                }
                try {
                	final Drawable drawable = loadImageFromUrl(imageUrl); 
                	imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                    handler.post(new Runnable() {
    					public void run() {
    						imageCallback.imageLoaded(drawable, id, imageUrl);
    					}
    				});
                } catch (Exception e) {
                	Log.e(Constants.LOG_TAG, e.getMessage(), e);
                }
                Looper.loop();
            }
        }.start();
        return;
    }

    public static Drawable loadImageFromUrl(String url) {
        URL m;
        InputStream i = null;
        try {
        	System.out.println(url);
            m = new URL(url);
            i = (InputStream) m.getContent();
        } catch (MalformedURLException e) {
        	Log.e(Constants.LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
        	Log.e(Constants.LOG_TAG, e.getMessage(), e);
        }
        Drawable d = Drawable.createFromStream(i, "src");
        return d;
    }

    public static Drawable loadImageFromFile(String url) {
        Drawable d=null;
        try {
            d = Drawable.createFromPath(url);
        } catch (Exception e) {
        	Log.e(Constants.LOG_TAG, e.getMessage(), e);
        }
        return d;
    }

    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, int id, String imageUrl);
    }
}
