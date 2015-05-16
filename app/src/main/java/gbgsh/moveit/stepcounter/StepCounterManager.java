package gbgsh.moveit.stepcounter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class StepCounterManager extends BroadcastReceiver {

    private StepListener mListener;

    public interface StepListener {
        public void onRecieveStep(int step);
    }

    public StepCounterManager(Activity activity) {
        Intent intent = new Intent(activity, StepCounterService.class);
        activity.startService(intent);

        IntentFilter filter = new IntentFilter(StepCounterService.STEP_COUNT);
        activity.registerReceiver(this, filter);
    }

    public void setStepListener(StepListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mListener != null) {
            int step = intent.getIntExtra(StepCounterService.STEP, 0);
            mListener.onRecieveStep(step);
        }
    }
}
