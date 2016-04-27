package com.pockettimer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.pockettimer.classes.SetClass;
import com.pockettimer.classes.WorkoutClass;
import com.google.gson.Gson;
import java.util.ArrayList;
import com.pockettimer.classes.CountDownTimer;
import android.os.Vibrator;

public class WorkoutActivity extends AppCompatActivity {

    // current time information
    public long timeUntilTotalFinished,timeUntilSetFinished,timeUntilRoundFinished;
    public float mCurrAngleBig,mCurrAngleSmall;
    public int mCurrRound,mCurrSet,mOldRound,mOldSet;
    int tickrate;
    long timeInCurrSet,timeInCurrRound,roundNo;
    // other stuff+
    // Get instance of Vibrator from current Context
    Vibrator v;
    public TotalCountDownTimer totalCounter;
    public PauseCountDownTimer pauseCounter;
    public long timeUntilPauseFinished;
    public int mSetPauseLength,mRoundPauseLength,mTotalLength;
    public ArrayList<SetClass> mSets;
    public boolean onPause,onStop,onFinished;
    public boolean mScreenOn;
    public boolean mUseNotification;
    public Button stopBtn,pauseBtn,nextBtn,restartBtn;
    public TextView tvCurrSetName,tvCurrRound,tvRemainingRoundTime,tvRemainingSetTime,tvSetNo;
    public TextView tvNotification1;
    MediaPlayer mpRound, mpSet,mpFinish;

    boolean TotalCounterCancelled;
    boolean totalTimerIsRunning;
    boolean pauseTimerIsRunning;
    boolean mVibrate;
    boolean forcedNextSet;
    boolean pauseChanged;

    // for notification
    Notification notification;
    NotificationManager nm;
    RemoteViews expandedView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workout);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // get Views
        tvCurrSetName=(TextView)findViewById(R.id.tv_currSetName);
        tvCurrRound=(TextView)findViewById(R.id.tv_currRound);
        tvRemainingRoundTime=(TextView)findViewById(R.id.tv_remainingRoundTime);
        tvRemainingSetTime=(TextView)findViewById(R.id.tv_remainingSetTime);
        tvSetNo=(TextView)findViewById(R.id.tv_setNo);
        tvNotification1=(TextView)findViewById(R.id.notificationText1);

        // get buttons
        stopBtn = (Button)findViewById(R.id.btn_stop);
        pauseBtn = (Button)findViewById(R.id.btn_pause);
        pauseBtn.setText(R.string.pause);

        nextBtn = (Button)findViewById(R.id.btn_next);
        restartBtn = (Button)findViewById(R.id.btn_restart);

        // if settings say so, keep screen on for workout
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mScreenOn = SP.getBoolean("keepSecreenOn", false);
        if (mScreenOn) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // if settings say so, use notification icon and layout
        mUseNotification = SP.getBoolean("notificationIcon", false);
        if (mUseNotification) createNotification(this);

        mVibrate = SP.getBoolean("vibrate", false);

        // get length between rounds/sets
        mSetPauseLength = SP.getInt("setPauseLength2", 0);
        mRoundPauseLength = SP.getInt("roundPauseLength2", 0);

        // get data from the Intent
        String jsonMyObject = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("workoutObject");
        }
        WorkoutClass workoutItem = new Gson().fromJson(jsonMyObject, WorkoutClass.class);

        // total workout length
        mTotalLength = 1000*Integer.parseInt(workoutItem.getTotalLength());
        mSets = workoutItem.getSets();

        mCurrRound = 0;
        mCurrSet = 0;
        tickrate = 50;

        totalCounter = new TotalCountDownTimer(mTotalLength, tickrate);
        totalCounter.start();
        tvCurrSetName.setText(mSets.get(mCurrSet).getName());
        tvCurrRound.setText(R.string.round + (mCurrRound + 1) + "/" + mSets.get(mCurrSet).getRounds());

        // set actionbar title to current workout name
        getSupportActionBar().setTitle(workoutItem.getName()); //only works when workout name not empty

        // initialize media player
        mpRound = MediaPlayer.create(getApplicationContext(), R.raw.set_sound);
        mpSet = MediaPlayer.create(getApplicationContext(), R.raw.round_sound);
        mpFinish = MediaPlayer.create(getApplicationContext(), R.raw.finish_sound);

        //activity state variables
        pauseTimerIsRunning = false;
        TotalCounterCancelled = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }

    @Override
    public void onDestroy() { // release media players when Activity stops(performance)
        super.onDestroy();
        if(mUseNotification) nm.cancelAll();
        mpRound.release();
        mpSet.release();
        mpFinish.release();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
                openAbout();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    String long2time(long currTime){
        int currTimeInt = (int)currTime/1000;
        int secs = currTimeInt % 60;
        int mins = (currTimeInt-secs)/60;
        return String.valueOf(mins).format("%02d", mins) +":"+ String.valueOf(secs).format("%02d", secs);
    }

    public boolean getFinished(){return onFinished;}

    public void setPause(View view){
        if(!pauseTimerIsRunning){ // don't do anything if pause screen is shown TODO: why not working
            if (onPause){
                onPause = false;
                pauseBtn.setText(R.string.pause);
                totalCounter = new TotalCountDownTimer(timeUntilTotalFinished,tickrate);
                totalCounter.start();
            }
            else {
                onPause = true;
                pauseBtn.setText(R.string.cont);
                TotalCounterCancelled = true;
                totalCounter.cancel();

            }
        }

    }


    public void setStop(View view){
        final View view2 = view;
        pauseChanged = false;
        if (!onPause){
            setPause(view2);
            pauseChanged = true;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm)
                .setMessage(R.string.really)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onWorkoutFinished();
                        TotalCounterCancelled = true;
                    }})
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (pauseChanged) setPause(view2);
                    }
                }).show();

    }

    public void onWorkoutFinished(){
        // deactivatate buttons
        stopBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        restartBtn.setEnabled(false);

        if (mVibrate) v.vibrate(400);
        totalTimerIsRunning = false;
        totalCounter.cancel();
        tvRemainingSetTime.setText("");
        tvCurrSetName.setText("");
        tvRemainingRoundTime.setText(R.string.done);
        tvCurrRound.setText("");
        tvSetNo.setText("");

        mCurrAngleBig = 90;
        mCurrAngleSmall = 90;

        onStop = true;
        if(mUseNotification && !TotalCounterCancelled) nm.cancelAll();
        mpFinish.start();
        onFinished = true;
    }

    public void  updateCurrTime(){
        onFinished = false;

        mOldRound = mCurrRound;
        mOldSet = mCurrSet;

        int currSetNo = 0;
        long accSetTime = 0;
        int i = 0;
        long elapsedTotalTime = mTotalLength - timeUntilTotalFinished;
        while(accSetTime < elapsedTotalTime){
            accSetTime += 1000*mSets.get(i).getLength() * mSets.get(i).getRounds();
            i += 1;
            currSetNo += 1;
        }
        currSetNo -= 1;
        mCurrSet = currSetNo;
        int timeInCompletedSets = 0;
        for (int j = 0; j < mCurrSet; j++) {
            timeInCompletedSets += 1000*mSets.get(j).getLength() * mSets.get(j).getRounds();
        }
        int roundLength = 1000*mSets.get(mCurrSet).getLength();
        timeInCurrSet = elapsedTotalTime-(long)timeInCompletedSets;
        timeInCurrRound = timeInCurrSet % roundLength;
        timeUntilSetFinished = 1000*mSets.get(mCurrSet).getLength() * mSets.get(mCurrSet).getRounds() - timeInCurrSet;
        timeUntilRoundFinished = 1000*mSets.get(mCurrSet).getLength() - timeInCurrRound;
        roundNo = (timeInCurrSet - timeInCurrRound)/roundLength ;
        mCurrRound = (int)roundNo;

        // update circle angles
        mCurrAngleSmall = (float)360*timeInCurrRound/mSets.get(mCurrSet).getLength()/1000 + 90;
        mCurrAngleBig = (float)360*timeInCurrSet/(mSets.get(mCurrSet).getLength()*mSets.get(mCurrSet).getRounds())/1000  + 90;

        if((mOldRound != mCurrRound) && (mOldSet == mCurrSet)) { // between rounds
            onPauseBetweenRounds();
            //Log.d("test", "between rounds");
        }
        if((mOldSet != mCurrSet)) { // between sets
            onPauseBetweenSets();
            //Log.d("test", "between sets");
        }
    }

    public void openAbout(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void restartSet(View view){

        totalCounter.cancel();
        if(onPause) onPause = false;
        TotalCounterCancelled = true;
        timeUntilTotalFinished += timeInCurrSet -1 ;
        if(pauseTimerIsRunning) pauseCounter.cancel(); // new
        totalCounter = new TotalCountDownTimer(timeUntilTotalFinished,tickrate);
        totalCounter.start();
    }

    public void nextSet(View view){
        forcedNextSet = true;
        if(pauseTimerIsRunning) pauseCounter.cancel(); // new
        totalCounter.cancel();
        if(onPause) onPause = false;
        TotalCounterCancelled = true;
        timeUntilTotalFinished -= timeUntilSetFinished;
        totalCounter = new TotalCountDownTimer(timeUntilTotalFinished,tickrate);
        totalCounter.start();
    }

    public float getAngleBig(){
        return mCurrAngleBig;
    }
    public float getAngleSmall(){
        return mCurrAngleSmall;
    }
    private void createNotification(Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //Create Intent to launch this Activity again if the notification is clicked.

        final Intent notificationIntent = new Intent(context, WorkoutActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent intent2 = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(intent2);
        builder.setTicker("Pocket Timer Workout");
        builder.setSmallIcon(R.drawable.ic_not);
        builder.setAutoCancel(false);
        notification = builder.build();

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        final String text = "Pocket Timer Workout";
        contentView.setTextViewText(R.id.textView, text);
        notification.contentView = contentView;
        if (Build.VERSION.SDK_INT >= 16) {
            // Inflate and set the layout for the expanded notification view
            expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);
            notification.bigContentView = expandedView;
        }
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, notification);

    }

    public class TotalCountDownTimer extends CountDownTimer
    {
        public TotalCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override     public void onFinish() // !!Count Down Timer does not call onFinish when cancel() member is called!!
        {
            onWorkoutFinished();
        }
        @Override     public void onTick(long millisUntilFinished) {
            totalTimerIsRunning = true;
            TotalCounterCancelled = false;
            onFinished = false;
            timeUntilTotalFinished = millisUntilFinished; // update current time
            updateCurrTime(); // update all other time values
            tvCurrSetName.setText(mSets.get(mCurrSet).getName());
            tvRemainingSetTime.setText(long2time(timeUntilSetFinished));
            tvRemainingRoundTime.setText(long2time(timeUntilRoundFinished));
            tvCurrRound.setText(getApplicationContext().getResources().getString(R.string.round_) + " " + (mCurrRound+1) + "/" + mSets.get(mCurrSet).getRounds());
            tvSetNo.setText(getApplicationContext().getResources().getString(R.string.set_) + " " + (mCurrSet+1) +  "/" + mSets.size());
        }
    }

    public class PauseCountDownTimer extends CountDownTimer
    {
        public PauseCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override     public void onFinish() // !!Count Down Timer does not call onFinish when cancel() member is called!!
        {
            pauseTimerIsRunning = false;
            totalCounter = new TotalCountDownTimer(timeUntilTotalFinished,tickrate);
            totalCounter.start();
            pauseBtn.setEnabled(true);
        }
        @Override     public void onTick(long millisUntilFinished) {
            timeUntilPauseFinished = millisUntilFinished; // update current time
            tvCurrSetName.setText("-Pause-");
            tvRemainingSetTime.setText("");
            tvRemainingRoundTime.setText(long2time(timeUntilPauseFinished));
            tvCurrRound.setText("");
            tvSetNo.setText("");
        }
    }

    void onPauseBetweenSets(){
        pauseBtn.setEnabled(false);
        if (mVibrate) v.vibrate(300);
        mpRound.start();
        long setPauseLength = (long)mSetPauseLength*1000;
        TotalCounterCancelled = true;
        totalCounter.cancel();
        onFinished = true;
        mCurrAngleBig = 90;
        mCurrAngleSmall = 90;
        pauseTimerIsRunning = true;
        pauseCounter = new PauseCountDownTimer(setPauseLength,tickrate);
        pauseCounter.start();
    }
    void onPauseBetweenRounds(){
        if (!forcedNextSet){
            pauseBtn.setEnabled(false);
            if (mVibrate) v.vibrate(100);
            mpSet.start();
            long roundPauseLength =(long)mRoundPauseLength*1000;
            totalCounter.cancel();
            onFinished = true;
            mCurrAngleBig = 90;
            mCurrAngleSmall = 90;
            pauseTimerIsRunning = true;
            pauseCounter = new PauseCountDownTimer(roundPauseLength,tickrate);
            pauseCounter.start();
            forcedNextSet = false;
        }
    }
}