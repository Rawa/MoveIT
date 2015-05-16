package gbgsh.moveit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import gbgsh.moveit.datalayer.Database;
import gbgsh.moveit.service.MainService;


public class MainActivity extends Activity implements Runnable {

    public static final int THRESHOLD_TIME_MINUTES = 1;
    public static final int THRESHOLD_MAX_STEPS = 100;
    public static final float NOTIFICATION_THRESHOLD = 0.1f;

    private Database mDb;
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

        mNow = (TextView) findViewById(R.id.step_now);
        mToday = (TextView) findViewById(R.id.step_today);
        mMonth = (TextView) findViewById(R.id.step_month);

        Intent mainServiceIntent = new Intent(this, MainService.class);
        this.startService(mainServiceIntent);

        IntentFilter intentFilter = new IntentFilter(MainService.UPDATE);
        this.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateBar();
            }
        }, intentFilter);

        mDb = new Database(getApplicationContext());

     //   Button high = (Button) findViewById(R.id.high);
     //   Button low = (Button) findViewById(R.id.low);

        bar = (StepBar) findViewById(R.id.stepbar);

/*        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barLevel += 0.1f;
                bar.setBarLevel(barLevel, true);
                Log.d(LOG_TAG, "high");
                bar.restartPulse();

            }
        });
        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barLevel -= 0.1f;
                bar.setBarLevel(barLevel, true);
                Log.d(LOG_TAG, "low");
                bar.restartPulse();
            }

        });
        */
        Paint paint = new Paint();
        Bitmap bg = Bitmap.createBitmap(240, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        createSmily(canvas,paint,0,20,5,0);
        createSmily(canvas,paint,0,65,5,50);
        createSmily(canvas,paint,0,110,5,100);

        /*createSmily(canvas,paint,100,50,5,10);
        createSmily(canvas,paint,100,100,5,20);
        createSmily(canvas, paint, 0, 250, 2, 50);
        createSmily(canvas, paint, 0, 300, 2, 60);
        createSmily(canvas, paint, 0, 350, 2, 70);
        createSmily(canvas, paint, 0, 400, 2, 80);
        createSmily(canvas, paint, 0, 450, 2, 90);
        createSmily(canvas, paint, 0, 500, 2, 100);*/

        LinearLayout ll = (LinearLayout) findViewById(R.id.smily);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));



//        handler.postDelayed(this, 0);
        updateBar();
    }

    @Override
    public void run() {
        updateBar();
        handler.postDelayed(this, 1000);
    }

    public void updateBar() {
        int latest = mDb.getLatestSteps(THRESHOLD_TIME_MINUTES);
        mNow.setText(latest + " steps");

        int day = mDb.getLatestSteps(24 * 60);
        mToday.setText(day + " steps");

        int month = mDb.getLatestSteps(30 * 24 * 60);
        mMonth.setText(month + " steps");

        if(latest != oldLatestStep) {
            Log.d(LOG_TAG, "Latest steps: " + latest);

            float level = Math.min((float)latest / (float) THRESHOLD_MAX_STEPS, 1.0f);
            Log.d(LOG_TAG, "Level set to: " + level);

            bar.setBarLevel(level, true);
        }

        oldLatestStep = latest;
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
        //   canvas.drawCircle(x, y, 100, paint);
        //   paint.setColor(Color.parseColor("#FFFFFF"));
        //   canvas.drawCircle(x - 20, y, 10, paint);
        //   paint.setColor(Color.parseColor("#111111"));
        //   canvas.drawArc(new  RectF(x-30,y-50, x+30,x+50),0,180,true, paint);
        //   paint.setColor(Color.parseColor("#ff0000"));

        // canvas.drawArc(new  RectF(x-30,y-20, x+20,x+50),0,180,true, paint);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getColorByIntesity(happyness));
        canvas.drawCircle((23+x)*scale, (23+y)*scale, 20*scale, paint);

        paint.setColor(Color.BLACK);
        canvas.drawCircle((22+x)*scale, (15+y)*scale, 3*scale, paint); //Left eye
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
        //if(happyness < 5){
        //   oval.set(11, 22, 35, 45); //sad
        if(happyness < 50) {//happy

            oval.set((11+x)*scale, (14+(happyness/5)+y)*scale, (35+x)*scale,( 37-(happyness/5)+y)*scale); //happy


        }else{//happy
            oval.set((11+x)*scale, (22+((100-happyness)/5)+y)*scale, (35+x)*scale, (45-((100-happyness)/5)+y)*scale); //happy

            //  oval.set(11+x, 22+((50-happyness)/5)+y, 35+x, 45-((50-happyness)/5)+y); //min sad happy=50
        }
        //}

        return oval;
    }



}
