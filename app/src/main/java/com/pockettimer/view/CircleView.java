package com.pockettimer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import com.pockettimer.R;

public class CircleView extends View {
    private int circleCol;
    private Paint circlePaint;
    private float radius = 0;
    private boolean firstDraw = true;
    int viewWidthHalf;
    int viewHeightHalf;
    float circleShadow;
    Context mContext;

    public CircleView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;

        //paint object for drawing in onDraw
        circlePaint = new Paint();

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.LovelyView, 0, 0);

        try {
            circleCol = a.getInteger(R.styleable.LovelyView_circleColor, 0);//0 is default
        } finally {
            a.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if(firstDraw){
            //get half of the width and height as we are working with a circle
            viewWidthHalf = this.getMeasuredWidth()/2;
            viewHeightHalf = this.getMeasuredHeight()/2;
            if(viewWidthHalf>viewHeightHalf)
                radius=viewHeightHalf;
            else
                radius=viewWidthHalf;
            firstDraw = false;
        }
        //set drawing properties
        circlePaint.setStyle(Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(circleCol);
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, circlePaint);
        circlePaint.setShadowLayer(circleShadow, circleShadow, 2.0f, 0xFF000000);
    }
}
