package gbgsh.moveit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;

import android.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import gbgsh.moveit.service.MainService;


public class MainActivity extends Activity implements Runnable {

    public static final int THRESHOLD_TIME_MINUTES = 1;
    public static final int THRESHOLD_MAX_STEPS = 100;
    public static final float NOTIFICATION_THRESHOLD = 0.1f;

    Intent mainServiceIntent;
    private MainService MainService;
    private StepBar bar;

    public Handler handler = new Handler();
    private int oldLatestStep = 0;

    private static final String LOG_TAG = "MainActivity";
    float barLevel = 0f;
    private boolean notificationSent = false;
    private TextView mNow;
    private TextView mToday;
    private TextView mMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mainServiceIntent = new Intent(this, MainService.class);
        this.startService(mainServiceIntent);

       IntentFilter intentFilter = new IntentFilter(MainService.UPDATE);
        this.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                float level = intent.getExtras().getFloat("level");
               // Log.d(LOG_TAG, "state: " + state);
                updateBar(level);
            }
        }, intentFilter);

       




        bar = (StepBar) findViewById(R.id.stepbar);



        boolean alarmOnOff= PreferenceManager.getDefaultSharedPreferences(this).getBoolean("alarmOnOff", true);
        Switch alarm= (Switch) findViewById(R.id.alarm);
        alarm.setChecked(alarmOnOff);

        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("alarmOnOff",isChecked); // value to store
                editor.commit();
            }
        });


    }

    @Override
    public void run() {
     //   updateBar();
        handler.postDelayed(this, 1000);

    }

    public void updateBar(float level) {


        bar.setBarLevel(level, true);


        Paint paint = new Paint();
        Bitmap bg = Bitmap.createBitmap(240, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        createSmily(canvas, paint, 10, 0, 4, Math.round(level * 100.0f));

        LinearLayout ll = (LinearLayout) findViewById(R.id.smily);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //happyness 0-100
    //width nummber of times bigger standard 1
    private void createSmily(Canvas canvas,Paint paint,int x,int y,int scale,int happyness){

        happyness = 100 - happyness;

        paint.setAntiAlias(true);
        paint.setStrokeWidth(4.0f);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getColorByIntesity(happyness));
        canvas.drawCircle((23+x)*scale, (23+y)*scale, 20*scale, paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle((20+x+10)*scale, (15+y)*scale, 4*scale, paint); //Left eye
        canvas.drawCircle((20+x-5)*scale, (15+y)*scale, 4*scale, paint); //Right eye
        //canvas.drawCircle(31, 15, 3, paint); //Right eye

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((23+x)*scale, (23+y)*scale, 20*scale, paint);

        if(happyness >= 55) {
            canvas.drawArc(getMouthDrawingByIntesity(happyness, x, y,scale), 180, 180, false, paint); //Mouth
            canvas.drawArc(getMouthDrawingByIntesity(happyness+10, x, y,scale), 180, 180, false, paint);
        }
        else if(happyness < 55) {
            canvas.drawArc(getMouthDrawingByIntesity(happyness, x, y,scale), 0, 180, false, paint);
            if(happyness+10<50) {
                canvas.drawArc(getMouthDrawingByIntesity(happyness + 10, x, y,scale), 0, 180, false, paint);
            }
        }


    }
    public int getColorByIntesity(int happyness){
        return 200;
    }
    public RectF getMouthDrawingByIntesity(int happyness,int x, int y,int scale){
        final RectF oval = new RectF();
        if(happyness < 50) {//happy

            oval.set((11+x)*scale, (14+(happyness/5)+y)*scale, (35+x)*scale,( 37-(happyness/5)+y)*scale); //happy


        }else{//happy
            oval.set((11+x)*scale, (22+((100-happyness)/5)+y)*scale, (35+x)*scale, (45-((100-happyness)/5)+y)*scale); //happy
        }

        return oval;
    }



}
