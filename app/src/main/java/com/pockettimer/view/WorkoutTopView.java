package com.pockettimer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import com.pockettimer.R;
import com.pockettimer.WorkoutActivity;
import java.util.Timer;
import java.util.TimerTask;

public class WorkoutTopView extends View {

    final Handler myHandler = new Handler();

    Paint paint1 = new Paint();
    Paint paint11 = new Paint();
    Paint paint2 = new Paint();
    Paint paint22 = new Paint();
    Paint paint3 = new Paint();

    float mCurrAngleBig;
    float mCurrAngleSmall;

    boolean stop;

    //int mRoundLength;
    //ArrayList<SetClass> mSets;
    Timer myTimer;

    RectF circle_big = new RectF();
    RectF circle_big_light = new RectF();
    RectF circle_smaller = new RectF();
    RectF circle_smaller_light = new RectF();
    RectF circleCover1 = new RectF();
    RectF circleCover2 = new RectF();
    RectF endCircle11 = new RectF();
    RectF endCircle12 = new RectF();
    RectF endCircle21 = new RectF();
    RectF endCircle22 = new RectF();

    @Override // determine sizes of View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int viewSize = parentWidth > parentHeight ? parentHeight:parentWidth;
        // TODO: get position of circle center here? for later placement of textviews...!!

        this.setMeasuredDimension(viewSize, viewSize);
    }

    public WorkoutTopView(Context context) {
        super(context);
    }
    public WorkoutTopView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // define timer that updates the canvas
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                myHandler.post(myRunnable);
            }
        }, 0, 50); // update every 50ms


        paint1.setStrokeWidth(0);
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setColor(getResources().getColor(R.color.colorPrimaryDark));

        paint11.setStrokeWidth(0);
        paint11.setAntiAlias(true);
        paint11.setStyle(Paint.Style.FILL_AND_STROKE);
        paint11.setColor(getResources().getColor(R.color.grey_light));

        paint2.setStrokeWidth(0);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setColor(getResources().getColor(R.color.colorPrimary));

        paint22.setStrokeWidth(0);
        paint22.setAntiAlias(true);
        paint22.setStyle(Paint.Style.FILL_AND_STROKE);
        paint22.setColor(getResources().getColor(R.color.grey_light));

        paint3.setStrokeWidth(0);
        paint3.setAntiAlias(true);
        paint3.setStyle(Paint.Style.FILL_AND_STROKE);
        paint3.setColor(getResources().getColor(R.color.white_dark));

    }
    public WorkoutTopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //init(getContext());
        super.onDraw(canvas);

        int height = this.getMeasuredHeight();
        int width = this.getMeasuredWidth();

        int diff = 30; //diameter difference between concentric circles

        int diamCircleTotal = height*9/10; // can change circle diameter relative to screen
        int diamCircleLocal = diamCircleTotal - diff;
        int diamCircleCoverBig = diamCircleTotal - diff;
        int diamCircleCoverSmall = diamCircleLocal-diff;

        int distX_big = (width - diamCircleTotal)/2;
        int distY_big = (height - diamCircleTotal)/2;

        int distX_smaller = (width - diamCircleLocal)/2;
        int distY_smaller = (height - diamCircleLocal)/2;

        int distX_cover1 = (width - diamCircleCoverBig)/2;
        int distY_cover1 = (height - diamCircleCoverBig)/2;

        int distX_cover2 = (width - diamCircleCoverSmall)/2;
        int distY_cover2 = (height - diamCircleCoverSmall)/2;

        circle_big_light.set(distX_big, distY_big, distX_big + diamCircleTotal, distY_big + diamCircleTotal);
        canvas.drawArc(circle_big_light, -90, 360 , true, paint11);

        circle_big.set(distX_big, distY_big, distX_big + diamCircleTotal, distY_big + diamCircleTotal);
        canvas.drawArc(circle_big, -90, mCurrAngleBig - 90, true, paint1);

        circleCover1.set(distX_cover1, distY_cover1, distX_cover1 + diamCircleCoverBig, distY_cover1 + diamCircleCoverBig);
        canvas.drawArc(circleCover1, -90, 360, true, paint3);

        circle_smaller_light.set(distX_smaller, distY_smaller, distX_smaller + diamCircleLocal, distY_smaller + diamCircleLocal);
        canvas.drawArc(circle_smaller_light, -90, 360 , true, paint22);

        circle_smaller.set(distX_smaller, distY_smaller, distX_smaller + diamCircleLocal, distY_smaller + diamCircleLocal);
        canvas.drawArc(circle_smaller, -90, mCurrAngleSmall - 90, true, paint2);

        circleCover2.set(distX_cover2, distY_cover2, distX_cover2 + diamCircleCoverSmall, distY_cover2 + diamCircleCoverSmall);
        canvas.drawArc(circleCover2, -90, 360, true, paint3);

        double theta1 = -2*Math.PI*(mCurrAngleBig+90)/360;
        double theta2 = -2*Math.PI*(mCurrAngleSmall+90)/360;

        double endCircle11posX = distX_big + diamCircleTotal/2 + (diamCircleTotal/2-diff/4)*Math.sin(theta1)- diff/4;
        double endCircle11posY = distY_big + diamCircleTotal/2 + (diamCircleTotal/2-diff/4)*Math.cos(theta1)- diff/4;

        double endCircle21posX = distX_smaller + diamCircleLocal/2 + (diamCircleLocal/2-diff/4)*Math.sin(theta2)- diff/4;
        double endCircle21posY = distY_smaller + diamCircleLocal/2 + (diamCircleLocal/2-diff/4)*Math.cos(theta2)- diff/4;

        float endCircle11posX_f = (float)endCircle11posX;
        float endCircle11posY_f = (float)endCircle11posY;
        float endCircle21posX_f = (float)endCircle21posX;
        float endCircle21posY_f = (float)endCircle21posY;
        float endCircle12posX_f =  distX_big + diamCircleTotal/2- diff/4;
        float endCircle12posY_f =  distY_big;
        float endCircle22posX_f = distX_smaller + diamCircleLocal/2- diff/4;
        float endCircle22posY_f = distY_smaller;

        if(!stop) {
            endCircle11.set(endCircle11posX_f, endCircle11posY_f, endCircle11posX_f + diff / 2, endCircle11posY_f + diff / 2);
            canvas.drawArc(endCircle11, 0, 360, true, paint1);
            endCircle21.set(endCircle21posX_f, endCircle21posY_f, endCircle21posX_f + diff / 2, endCircle21posY_f + diff / 2);
            canvas.drawArc(endCircle21, 0, 360, true, paint2);
            endCircle12.set(endCircle12posX_f, endCircle12posY_f, endCircle12posX_f + diff / 2, endCircle12posY_f + diff / 2);
            canvas.drawArc(endCircle12, 0, 360, true, paint1);
            endCircle22.set(endCircle22posX_f, endCircle22posY_f, endCircle22posX_f + diff / 2, endCircle22posY_f + diff / 2);
            canvas.drawArc(endCircle22, 0, 360, true, paint2);
        }
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
                update(); // update values
                invalidate(); //redraw of View: method onDraw is being called again!
        }
    };

   // void init(Context context){
        //if (stop){
        //    myTimer.cancel();
        //}
    //}

    void update(){
        mCurrAngleBig = ((WorkoutActivity)getContext()).getAngleBig();
        mCurrAngleSmall = ((WorkoutActivity)getContext()).getAngleSmall();
        stop = ((WorkoutActivity)getContext()).getFinished();
    }
}


