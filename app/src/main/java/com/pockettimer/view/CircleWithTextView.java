package com.pockettimer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import com.pockettimer.R;

public class CircleWithTextView extends View {
    //circle and text colors
    private int circleCol, labelCol;
    //label text
    private String circleText;
    //paint for drawing custom view
    private Paint circlePaint;
    private float radius = 0;
    private boolean firstDraw = true;
    int viewWidthHalf;
    int viewHeightHalf;
    float circleShadow;
    Context mContext;

    public CircleWithTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;

        //paint object for drawing in onDraw
        circlePaint = new Paint();

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.LovelyView, 0, 0);

        try {
            //get the text and colors specified using the names in attrs.xml
            circleText = a.getString(R.styleable.LovelyView_circleLabel);
            circleCol = a.getInteger(R.styleable.LovelyView_circleColor, 0);//0 is default
            labelCol = a.getInteger(R.styleable.LovelyView_labelColor, 0);
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
        //set the paint color using the circle color specified
        circlePaint.setColor(circleCol);
        //draw the circle using the properties defined
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, circlePaint);
        //set the text color using the color specified
        //circlePaint.setColor(labelCol);

        int myColor = this.getResources().getColor(R.color.white_dark);
        circlePaint.setColor(myColor);


        circlePaint.setShadowLayer(circleShadow, circleShadow, 2.0f, 0xFF000000);
        //set text properties
        circlePaint.setTextAlign(Paint.Align.CENTER);
        circlePaint.setTextSize(55);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "RobotoSlab-Regular.ttf");
        circlePaint.setTypeface(font);
        //draw the text using the string attribute and chosen properties
        canvas.drawText(circleText, viewWidthHalf, viewHeightHalf + 20, circlePaint);

    }
}
