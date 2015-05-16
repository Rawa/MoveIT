package gbgsh.moveit;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import gbgsh.moveit.datalayer.Database;
import gbgsh.moveit.stepcounter.StepCounterManager;


public class MainActivity extends Activity implements StepCounterManager.StepListener {

    private StepCounterManager mStepCounter;
    private Database mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mStepCounter = new StepCounterManager(this);
        mStepCounter.setStepListener(this);

        mDb = new Database(getApplicationContext());
        mDb.insert(123);

        Button high = (Button) findViewById(R.id.high);
        Button low = (Button) findViewById(R.id.low);

        final StepBar bar = (StepBar) findViewById(R.id.stepbar);

        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setBarLevel(0.8f);
                Log.d("derp", "high");
            }
        });
        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setBarLevel(0.4f);
                Log.d("derp", "low");
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

    @Override
    public void onRecieveStep(int step) {
        TextView stepLabel = (TextView) findViewById(R.id.step);
        stepLabel.setText("Steps: " + step);
    }
}
