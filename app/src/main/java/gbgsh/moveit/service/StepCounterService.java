package gbgsh.moveit.service;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepCounterService implements SensorEventListener {
    private static final String LOG_TAG = "StepCounterService";

    private SensorManager mSensorManager;
    private StepListener mListener;

    public interface StepListener {
        public void onRecieveStep(int step);
    }

    public StepCounterService(Service service) {
        Log.d(LOG_TAG, "Starting");

        mSensorManager = (SensorManager) service.getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            mSensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.d(LOG_TAG, "Count sensor not available!");
        }
    }

    public void setStepListener(StepListener listener) {
        mListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(LOG_TAG, String.valueOf(event.values[0]));

        int steps = Math.round(event.values[0]);
        if(mListener != null) {
            mListener.onRecieveStep(steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
