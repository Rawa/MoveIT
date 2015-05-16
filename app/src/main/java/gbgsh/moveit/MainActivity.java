package gbgsh.moveit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import gbgsh.moveit.datalayer.Database;
import gbgsh.moveit.service.MainService;


public class MainActivity extends Activity {

    private Database mDb;

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent mainServiceIntent = new Intent(this, MainService.class);
        this.startService(mainServiceIntent);

        mDb = new Database(getApplicationContext());

        Button high = (Button) findViewById(R.id.high);
        Button low = (Button) findViewById(R.id.low);

        final StepBar bar = (StepBar) findViewById(R.id.stepbar);

        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setBarLevel(0.8f, true);
                Log.d(LOG_TAG, "high");
                bar.restartPulse();

            }
        });
        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setBarLevel(0.4f, true);
                Log.d(LOG_TAG, "low");
                bar.restartPulse();
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
