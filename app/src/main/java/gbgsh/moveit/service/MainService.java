package gbgsh.moveit.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import gbgsh.moveit.stepcounter.StepCounterService;

/**
 * Created by e on 2015-05-16.
 */
public class MainService extends IntentService implements  Runnable{

    private static final String LOG_TAG = "MAINSERVICE";
    private static final long TIMER_INTERVAL = 5000;
    private StepCounterService stepCounterService;
    private int mStep = 0;
    private int oldStep = 0;
    private final Handler mHandler = new Handler();


    public MainService (){
        super("MainService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "MAINSERVICE started");
        Intent stepIntent = new Intent(this, StepCounterService.class);
        this.startService(stepIntent);

        IntentFilter filter = new IntentFilter(StepCounterService.STEP_COUNT);
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mStep =  intent.getIntExtra(StepCounterService.STEP, 0);
                Log.d(LOG_TAG, "mStep: " + mStep);
            }
        }, filter);
        startTimer();
    }

    protected void startTimer(){
        mHandler.postDelayed(this, 0);
    }

    @Override
    public void run() {
        mHandler.postDelayed(this, TIMER_INTERVAL);
        int numbSteps = mStep - oldStep;
        Log.d(LOG_TAG, "Steps taken since last time:" + numbSteps);
        oldStep = mStep;
        Log.d(LOG_TAG, "TIMER TRIGGERED");
    }
    
}
