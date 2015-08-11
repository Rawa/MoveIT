package gbgsh.moveit;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by rawa on 2015-05-16.
 */
public class StepBar extends LinearLayout {
    private RelativeLayout base;
    private LinearLayout bar;

    private int previousColor;
    private int nextColor;
    private static final int FREQUENCY = 1500;
    private Random randomGenerator = new Random();

    private Object waitersGonna = new Object();

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
        base = (RelativeLayout) this.findViewById(R.id.base);
        bar = (LinearLayout) this.findViewById(R.id.bar);
        previousColor = getBarRandColor();
        startPulse();
    }

    public float getBarLevel(){
        int baseHeight = base.getHeight();
        int barHeight = bar.getHeight();
        return (float) barHeight/baseHeight;
    }

    private int getRandDiff(int diff){
        int min = -diff;
        int max = diff;
        return diff = randomGenerator.nextInt(max - min + 1) - min;
    }

    private int applyRandDiff(int color, int diff){
        color = Math.min(255, color);
        color = Math.max(0, color);
        color += getRandDiff(diff);
        color = Math.min(255, color);
        color = Math.max(0, color);
        return color;
    }

    private int getBarRandColor(){
        float level = getBarLevel();

        int red = (int)(51+(level*204));
        int green = (int)(204-(127*level));
        int blue = (int)(255-(221*level));

        red = applyRandDiff(red, 10);
        green = applyRandDiff(green, 5);
        blue = applyRandDiff(blue, 15);

        return Color.rgb(red,green,blue);

        // return Color.rgb((int)(51+(level*204)),(int)(204-(127*level)),(int)(255-(221*level)));

        /*
        if(level > .95f){
            return Color.rgb(255,87,34);
        } else if(level > .5f){
            green = applyRandDiff(255, 20);
            red = 255 - applyRandDiff((int) (2*(getBarLevel()-0.5f) * 255), 20);
        } else {
            green = applyRandDiff((int) (2*getBarLevel()*255), 20);
            red = applyRandDiff(255, 20);
        }
        int blue = applyRandDiff(20, 10);
        Log.d("TAG", "FLOAT= " + getBarLevel());
        Log.d("TAG", "Red: " + red);
        Log.d("TAG", "Green: " + green);
        Log.d("TAG", "Blue: " + blue);
        return Color.rgb(red, green, blue);
        */


    }

    /**
     *
     * @param level Float between 0-1f
    */
    public void setBarLevel(float level, boolean animate){
        if(animate) {
            animateBarHeight(level);
            return;
        }

        int baseHeight = base.getHeight();
        ViewGroup.LayoutParams params = bar.getLayoutParams();
        params.height = (int) (baseHeight*level);
        bar.setLayoutParams(params);
    }

    /**
     * Animate the height of the StepBar from the current value to the supplied.
     * @param to Move the bar to this level
     */
    private void animateBarHeight(float to) {
        final int goalHeight = (int)(base.getHeight() * to);
        final int currentHeight = bar.getLayoutParams().height;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ValueAnimator anim = ValueAnimator.ofInt(currentHeight, goalHeight);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = bar.getLayoutParams();
                        layoutParams.height = val;
                        bar.setLayoutParams(layoutParams);
                        //restartPulse(); // Causes flickering on frequent updates...
                    }
                });
                anim.setDuration(500);
                anim.start();
            }
        };
       // Log.d("Derpy", "Starting animation");
        post(runnable);
    }

    private void startPulse() {

        final Runnable runnable = new Runnable() {
            int i = 0;
            public void run() {
                nextColor = getBarRandColor();
                //start animation
                ObjectAnimator anim = ObjectAnimator.ofInt(bar, "backgroundColor", previousColor, nextColor);
                anim.setEvaluator(new ArgbEvaluator());
                anim.setDuration(FREQUENCY);
                anim.start();
                previousColor = nextColor;
            }
        };

        start(runnable);
    }

    /**
     * Notify and restart the thread that runs the color pulse
     */
    public void restartPulse() {
        synchronized (waitersGonna) {
            waitersGonna.notify();
        }
    }

    private void start(final Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (waitersGonna) {
                    while (true) {
                        try {
                            post(runnable);
                            waitersGonna.wait(FREQUENCY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}
