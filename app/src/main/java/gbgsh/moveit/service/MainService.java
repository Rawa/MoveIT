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
import android.preference.PreferenceManager;
import android.content.SharedPreferences;


import java.util.Calendar;

import java.util.HashSet;

import java.util.Timer;
import java.util.TimerTask;

import gbgsh.moveit.MainActivity;
import gbgsh.moveit.R;
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
    Camera camera = Camera.open();
    Camera.Parameters p = camera.getParameters();
    private String[] daysArr = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private SharedPreferences prefs;


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
                String thresholdString=prefs.getString("threshold", "0");




                Float threshold=Float.parseFloat(thresholdString);



               // Log.d(LOG_TAG,"Rising whit speed: "+thresholdString);


                if(globalLevel+threshold<1) {
                    globalLevel+=threshold;

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
        checkForNotification(globalLevel);
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

        prefs = PreferenceManager.getDefaultSharedPreferences(MainService.this);
        String sendentaryLimitString=prefs.getString("sendentary_limit", "0");

        Float sendentaryLimit=Float.parseFloat(sendentaryLimitString);


      //  Log.d(LOG_TAG, "Lowring whit speed: "+sendentaryLimitString);



        if(globalLevel-sendentaryLimit>0) {
            globalLevel -= sendentaryLimit;

        }
        update();
        mHandler.postDelayed(this, TIMER_INTERVAL);
    }

    private boolean isItRigthTime(){
       final String TO_KEY = "timePrefB_Key";
       final String FROM_KEY = "timePrefA_Key";



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
        return currentTimeInMinutes>minValue&&currentTimeInMinutes<maxValue;

    }

    private boolean isItCorrectDay(){
        Calendar c = Calendar.getInstance();

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);



        HashSet<String> days1 = new HashSet<String>();
        days1.add("Monday");
        days1.add("Tuesday");
        days1.add("Wednesday");
        days1.add("Thursday");
        days1.add("Friday");
        HashSet<String> days = (HashSet<String>) prefs.getStringSet("multi_weekdays_key", days1);

        String today = daysArr[dayOfWeek-1];

        if(days.contains(today)){
            return true;
        } else {
            return false;
        }
    }



    public void checkForNotification(float level) {
        Log.d("test", "test");
        if(isItRigthTime()&&isItCorrectDay()) {

            int alarmRepeat=Integer.parseInt(prefs.getString("repeat_alarm", "0"));
            Timer timer = new Timer();
            if (level <= MainActivity.NOTIFICATION_THRESHOLD) {


                if (!notificationSent) {
                    notificationSent = true;


                    if(alarmRepeat!=0) {
                        Log.d(LOG_TAG, "Start Timmer");
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                Log.d(LOG_TAG, "timmer timed out");
                                sendNotification();

                            }

                        }, 0, alarmRepeat * 1000);
                    }
                    //sendNotification();
                }
            } else {
                Log.d(LOG_TAG, "turn off timmer");
              //  timer.cancel();
                notificationSent = false;
            }

        }

    }
    public void sendNotification(){

        boolean vibrate=prefs.getBoolean("notifications_new_message_vibrate", false);
        boolean flash=prefs.getBoolean("notifications_new_message_flash", false);
        boolean notice=prefs.getBoolean("notifications_new_message_notice", false);
        boolean ringtone=prefs.getBoolean("notifications_new_message", false);

        if(notice) {
            Log.d(LOG_TAG, "Sending notification");


            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);




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
            // Get instance of Vibrator from current Context
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);

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
}
