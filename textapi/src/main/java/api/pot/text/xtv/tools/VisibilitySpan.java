package api.pot.text.xtv.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

public class VisibilitySpan extends ReplacementSpan {
    public static int VISIBLE = 0;
    public static int INVISIBLE = 1;
    private int visibility = VISIBLE;

    int size = 0;

    public VisibilitySpan(int visibility) {
        this.visibility = visibility;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        if(size>0)canvas.drawText(text, start, end, x, y, paint);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        return (size = visibility==INVISIBLE?0:(int) paint.measureText(text, start, end));
    }
}