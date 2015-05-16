package gbgsh.moveit;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class StepCounterService extends IntentService implements SensorEventListener {
    private SensorManager mSensorManager;

    public StepCounterService() {
        super("StepCounter");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            mSensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("step", String.valueOf(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
