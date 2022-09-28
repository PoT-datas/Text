package api.pot.text.xet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import api.pot.text.R;
import api.pot.text.tools.TextPainter;

import static api.pot.text.tools.BitmapMng.decodeSampledBitmapFromResource;

@SuppressLint("AppCompatCustomView")
public class XEditText extends EditText {
    public XEditText(Context context) {
        super(context);
        init();
        setWillNotDraw(false);
    }

    public XEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setWillNotDraw(false);
    }

    public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setWillNotDraw(false);
    }

    private void init() {
    }

    /*@Override
    protected void onDraw(Canvas canvas) {

        ///int save = canvas.save();

        ///canvas.drawColor(Color.argb(20, 0,0,0));

        ///TextPainter.drawTextCentered("Description", null, canvas,
           ///     new RectF(0,0,getWidth(),getHeight()), true);

        ///super.onDraw(canvas);

        ///canvas.restoreToCount(save);

        Rect clipRect = new Rect();
        RectF clipRectF = new RectF();
        Path clipPath = new Path();

        int save = canvas.save();

        canvas.getClipBounds(clipRect);
        clipRectF.set(clipRect.left, clipRect.top, clipRect.right,
                clipRect.bottom);
        clipPath.addRoundRect(clipRectF, 0, 0, Path.Direction.CW);

        canvas.clipPath(clipPath);

        super.onDraw(canvas);
        TextPainter.drawTextCentered("Description", null, canvas,
                new RectF(0,0,getWidth(),getHeight()), true);

        canvas.restoreToCount(save);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }*/
}
