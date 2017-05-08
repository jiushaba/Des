package com.lb.video.widget;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {

    private boolean mIsMovingThumb = false;
    static private float THUMB_SLOP = 25;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    private boolean isWithinThumb(MotionEvent event) {
        final float progress = getProgress();
        final float density = this.getResources().getDisplayMetrics().density;
        final float height = getHeight();
        final float y = event.getY();
        final float max = getMax();
        if (progress >= max - (int)(max * (y + THUMB_SLOP * density) / height)
            && progress <= max - (int)(max * (y - THUMB_SLOP * density) / height))
            return true;
        else
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        boolean handled = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isWithinThumb(event)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    mIsMovingThumb = true;
                    handled = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsMovingThumb) {
                    final int max = getMax();
                    setProgress(max - (int) (max* event.getY() / getHeight()));
                    handled = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                mIsMovingThumb = false;
                handled = true;
                break;
        }
        return handled;
    }
}