package gbgsh.moveit;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by rawa on 2015-05-16.
 */
public class StepBar extends LinearLayout {
    private LinearLayout base;
    private LinearLayout bar;

    private int previousColor;
    private int nextColor;
    private int from;
    private static final int FREQUENCY = 1500;

    public StepBar(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.stepbar, null);
        init(context);

    }
    public StepBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StepBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflate(context, R.layout.stepbar, this);
        base = (LinearLayout) this.findViewById(R.id.base);
        bar = (LinearLayout) this.findViewById(R.id.bar);
        from = getBarLevelColor();
        startPulse();
    }

    public float getBarLevel(){
        Log.d("Derpy", "Base = " + base);
        int baseHeight = base.getHeight();
        int barHeight = bar.getHeight();
        return (float) barHeight/baseHeight;
    }

    private int getBarLevelColor(){
        int green = (int) (getBarLevel()*255);
        int red = (int) (255 - (getBarLevel()*255));
        return Color.rgb(red, green, 0);
    }

    /**
     *
     * @param level Float between 0-1f
    */
    public void setBarLevel(float level){
        int baseHeight = base.getHeight();
        ViewGroup.LayoutParams params = bar.getLayoutParams();
        params.height = (int) (baseHeight*level);
        bar.setLayoutParams(params);
    }
    private void startPulse() {

        final Runnable runnable = new Runnable() {
            int i = 0;
            public void run() {

                previousColor = from;
                nextColor = getBarLevelColor();

                //start animation
                ObjectAnimator anim = ObjectAnimator.ofInt(bar, "backgroundColor", previousColor, nextColor);
                anim.setEvaluator(new ArgbEvaluator());
                anim.setDuration(FREQUENCY);
                anim.start();
                from = nextColor;
            }
        };

        start(runnable);
    }

    private void start(final Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        post(runnable);
                        Thread.sleep(FREQUENCY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
