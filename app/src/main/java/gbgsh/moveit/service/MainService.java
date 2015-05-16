package gbgsh.moveit.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import gbgsh.moveit.MainActivity;
import gbgsh.moveit.R;
import gbgsh.moveit.datalayer.Database;

public class MainService extends IntentService implements  Runnable{

    public static final String UPDATE = "update";

    private static final String LOG_TAG = "MainService";
    private static final long TIMER_INTERVAL = 1500;
    private Integer mStep = 0;
    private Integer oldStep;
    private final Handler mHandler = new Handler();
    private StepCounterService mStepCounterService;
    private Database mDb;
    private boolean notificationSent = true;

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

        int latest = mDb.getLatestSteps(MainActivity.THRESHOLD_TIME_MINUTES);
        Log.d(LOG_TAG, "Latest steps: " + latest);

        float level = Math.min((float)latest / (float) MainActivity.THRESHOLD_MAX_STEPS, 1.0f);
        Log.d(LOG_TAG, "Level set to: " + level);
        sendNotification(level, latest);
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
            }
        }

        update();
        mHandler.postDelayed(this, TIMER_INTERVAL);
    }

    public void sendNotification(float level, int latest) {
        if(level <= MainActivity.NOTIFICATION_THRESHOLD) {
            if(!notificationSent) {
                notificationSent = true;
                Log.d(LOG_TAG, "Sending notification");

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new Notification.Builder(this)
                        .setContentTitle("You have to move it!")
                        .setContentText("MoveIT")
                        .setSmallIcon(R.drawable.ic_stat_movieit_icon)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent).build();


                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, notification);

            }
        } else {
            notificationSent = false;
        }
    }
}
