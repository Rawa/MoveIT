package gbgsh.moveit.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import gbgsh.moveit.datalayer.Database;

public class MainService extends IntentService implements  Runnable{

    public static final String UPDATE = "update";

    private static final String LOG_TAG = "MainService";
    private static final long TIMER_INTERVAL = 1000;
    private Integer mStep = 0;
    private Integer oldStep;
    private final Handler mHandler = new Handler();
    private StepCounterService mStepCounterService;
    private Database mDb;

    public MainService (){
        super("MainService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "MAINSERVICE started");

        mDb = new Database(getApplicationContext());

        mStepCounterService = new StepCounterService(this);
        mStepCounterService.setStepListener(new StepCounterService.StepListener() {
            @Override
            public void onRecieveStep(int step) {
                mStep = step;
                if(oldStep == null) {
                    oldStep = mStep;
                }
            }
        });

        startTimer();
    }

    protected void startTimer(){
        mHandler.postDelayed(this, 0);
    }

    public void update() {
        Intent intent = new Intent();
        intent.setAction(MainService.UPDATE);
        sendBroadcast(intent);
    }

    @Override
    public void run() {
        if(oldStep != null) {
            int numbSteps = mStep - oldStep;
            oldStep = mStep;

            if(numbSteps > 0) {
                int total = mDb.getTotalSteps();
                Log.d(LOG_TAG, "Steps taken since last time:" + numbSteps + "/" + total);
                mDb.insert(numbSteps);

                update();
            }

        }

        mHandler.postDelayed(this, TIMER_INTERVAL);
    }

}
