package com.lz.oncon.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
public class RoundImageView extends ImageView { 
	private Context mContext;
    private Paint paint;
    private Paint paint2;
 
    public RoundImageView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
        mContext = context;
        init(context, attrs); 
    } 
 
    public RoundImageView(Context context, AttributeSet attrs) { 
        super(context, attrs);
        mContext = context;
        init(context, attrs); 
    } 
 
    public RoundImageView(Context context) { 
        super(context);
        mContext = context;
        init(context, null); 
    } 
     
    private void init(Context context, AttributeSet attrs) { 
        paint = new Paint(); 
        paint.setColor(Color.WHITE); 
        paint.setAntiAlias(true); 
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT)); 
         
        paint2 = new Paint(); 
        paint2.setXfermode(null); 
    } 
     
    @Override 
    public void draw(Canvas canvas) { 
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888); 
        Canvas canvas2 = new Canvas(bitmap); 
        super.draw(canvas2); 
        drawLiftUp(canvas2); 
        drawRightUp(canvas2); 
        drawLiftDown(canvas2); 
        drawRightDown(canvas2); 
        canvas.drawBitmap(bitmap, 0, 0, paint2); 
        bitmap.recycle(); 
    } 
     
    private void drawLiftUp(Canvas canvas) { 
        Path path = new Path(); 
        path.moveTo(0, getHeight()/2); 
        path.lineTo(0, 0); 
        path.lineTo(getWidth()/2, 0); 
        path.arcTo(new RectF(0, 0, getWidth(), getHeight()), -90, -90); 
        path.close(); 
        canvas.drawPath(path, paint); 
    } 
     
    private void drawLiftDown(Canvas canvas) { 
        Path path = new Path(); 
        path.moveTo(0, getHeight()/2); 
        path.lineTo(0, getHeight()); 
        path.lineTo(getWidth()/2, getHeight()); 
        path.arcTo(new RectF(0, 0, getWidth(), getWidth()), 90, 90); 
        path.close(); 
        canvas.drawPath(path, paint); 
    } 
     
    private void drawRightDown(Canvas canvas) { 
        Path path = new Path(); 
        path.moveTo(getWidth()/2, getHeight()); 
        path.lineTo(getWidth(), getHeight()); 
        path.lineTo(getWidth(), getHeight()/2); 
        path.arcTo(new RectF(0, 0, getWidth(), getHeight()), 0, 90); 
        path.close(); 
        canvas.drawPath(path, paint); 
    } 
     
    private void drawRightUp(Canvas canvas) { 
        Path path = new Path(); 
        path.moveTo(getWidth(), getHeight()/2); 
        path.lineTo(getWidth(), 0); 
        path.lineTo(getWidth()/2, 0); 
        path.arcTo(new RectF(0, 0, getWidth(), getHeight()), -90, 90); 
        path.close(); 
        canvas.drawPath(path, paint); 
    }
}