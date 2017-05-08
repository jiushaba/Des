package com.lz.oncon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class AutoWrapViewGroup extends ViewGroup {
    
    private final static int VIEW_MARGIN = 0;

    private int horizontalSpacing = VIEW_MARGIN;
    private int verticalSpacing = VIEW_MARGIN;

    public AutoWrapViewGroup(Context context) {
        super(context);
    }
    
    public AutoWrapViewGroup(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    
    public AutoWrapViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        for (int index = 0; index < getChildCount(); index++) {
//            final View child = getChildAt(index);
//            // measure
//            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//        }
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    	assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
            	child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            	
                final int childw = child.getMeasuredWidth();
                
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                    line_height = 0;
                }
                
                line_height = Math.max(line_height, child.getMeasuredHeight() + verticalSpacing);
                xpos += childw + horizontalSpacing;
            }
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
//    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
//        final int count = getChildCount();
//        int row=0;// which row lay you view relative to parent
//        int lengthX=arg1;    // right position of child relative to parent
//        int lengthY=arg2;    // bottom position of child relative to parent
//        for(int i=0;i<count;i++){
//            
//            final View child = this.getChildAt(i);
//            int width = child.getMeasuredWidth();
//            int height = child.getMeasuredHeight();
//            lengthX+=width+VIEW_MARGIN;
//            lengthY=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height+arg2;
//            //if it can't drawing on a same line , skip to next line
//            if(lengthX>arg3){
//                lengthX=width+VIEW_MARGIN+arg1;
//                row++;
//                lengthY=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height+arg2;
//                
//            }
//            
//            child.layout(lengthX-width, lengthY-height, lengthX, lengthY);
//        }
//
//    }
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int line_height = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                    line_height = 0;
                }
                if(line_height < childh){
                	line_height = childh;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + horizontalSpacing;
            }
        }
    }
}
