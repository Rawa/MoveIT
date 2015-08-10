package gbgsh.moveit.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import 	android.preference.PreferenceManager;
import 	android.content.SharedPreferences;


import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import gbgsh.moveit.MainActivity;
import gbgsh.moveit.R;
import gbgsh.moveit.TimePreference;
import gbgsh.moveit.datalayer.Database;

public class MainService extends IntentService implements  Runnable{

    public static final String UPDATE = "update";

    private static final String LOG_TAG = "MainService";
    private static final long TIMER_INTERVAL = 1500;
    private Integer mStep = 0;
    private Integer oldStep;
    private float globalLevel=0.3f;//0..1
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

       //  mDb = new Database(getApplicationContext());

        mStepCounterService = new StepCounterService(this);
        mStepCounterService.setStepListener(new StepCounterService.StepListener() {
            @Override
            public void onRecieveStep(int step) {
                mStep = step;
                if(oldStep == null) {
                    oldStep = mStep;
                }
                float stepUp=0.1f;//0.01f;
                if(globalLevel+stepUp<1) {
                    globalLevel+=stepUp;

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
        intent.putExtra("level", globalLevel);
        sendBroadcast(intent);
      //  mDb.setCurrentLevel(globalLevel);

     //   int latest = mDb.getLatestSteps(MainActivity.THRESHOLD_TIME_MINUTES);
       // Log.d(LOG_TAG, "Latest steps: " + latest);

       // float level = Math.min((float)latest / (float) MainActivity.THRESHOLD_MAX_STEPS, 1.0f);
  //      Log.d(LOG_TAG, "Level set to: " + level);
        sendNotification(globalLevel);
    }

    @Override
    public void run() {
        /*if(oldStep != null) {
            int numbSteps = mStep - oldStep;
            oldStep = mStep;

            if(numbSteps > 0) {
                int total = mDb.getTotalSteps();
                Log.d(LOG_TAG, "Steps taken since last time:" + numbSteps + "/" + total);

              //  mDb.insert(numbSteps);
            }
        }*/
        float stepUp=0.05f;//0.001f;
        if(globalLevel-stepUp>0) {
            globalLevel -= stepUp;

        }
        update();
        mHandler.postDelayed(this, TIMER_INTERVAL);
    }

    private boolean isItRigthTime(){
       final String TO_KEY = "timePrefB_Key";
       final String FROM_KEY = "timePrefA_Key";

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Get widgets :

        String toKey=prefs.getString(TO_KEY, "17:00");
        String FromKey=prefs.getString(FROM_KEY, "08:00");
        boolean test2=prefs.getBoolean("notifications_new_message_vibrate", false);
        Calendar c = Calendar.getInstance();

        int Curreminute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int currentTimeInMinutes=hour*60+Curreminute;
        int maxValue=Integer.parseInt(toKey.split(":")[0]) *60+Integer.parseInt(toKey.split(":")[1]);
        int minValue=Integer.parseInt(FromKey.split(":")[0])*60+Integer.parseInt(FromKey.split(":")[1]);
        Log.d(LOG_TAG,"min:"+minValue+" max:"+maxValue+" current:"+currentTimeInMinutes);
        return currentTimeInMinutes>minValue&&currentTimeInMinutes<maxValue;

    }





    private boolean isItCorrectDay(){
        Calendar c = Calendar.getInstance();

        int DayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        return true;
    }



    public void sendNotification(float level) {
        if(isItRigthTime()&&isItCorrectDay()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean vibrate=prefs.getBoolean("notifications_new_message_vibrate", false);
            boolean flash=prefs.getBoolean("notifications_new_message_flash", false);
            boolean notice=prefs.getBoolean("notifications_new_message_notice", false);
            boolean ringtone=prefs.getBoolean("notifications_new_message", false);
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                if (level <= MainActivity.NOTIFICATION_THRESHOLD) {
                    if (!notificationSent) {
                        notificationSent = true;
                        if(notice) {
                            Log.d(LOG_TAG, "Sending notification");


                            Notification notification = new Notification.Builder(this)
                                    .setContentTitle("You have to move it!")
                                    .setContentText("MoveIT")
                                    .setSmallIcon(R.drawable.ic_stat_movieit_icon)
                                            // .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                    .setAutoCancel(true)
                                    .setContentIntent(contentIntent).build();


                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(0, notification);
                        }

                        if(vibrate){
                            if (level <= MainActivity.NOTIFICATION_THRESHOLD) {
                                // Get instance of Vibrator from current Context
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 400 milliseconds
                                v.vibrate(400);
                            }
                        }
                        if(ringtone){
                            try {
                                String ringtoneSong=prefs.getString("notifications_new_message_ringtone", "Cant find");
                                Uri myUri = Uri.parse(ringtoneSong);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), myUri);
                                r.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (flash) {
                            try {
                                Camera camera = Camera.open();
                                Camera.Parameters p = camera.getParameters();
                                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                camera.setParameters(p);
                                camera.startPreview();

                                Thread.sleep(500);


                                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                camera.setParameters(p);
                                camera.stopPreview();
                                } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                } else {
                    notificationSent = false;
                }

        }
    }
}
