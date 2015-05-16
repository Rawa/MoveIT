package gbgsh.moveit;

import android.content.Context;
import android.util.AttributeSet;
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
        base = (LinearLayout) inflate(context, R.layout.stepbar, this);
        bar = (LinearLayout) base.findViewById(R.id.bar);
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
}
