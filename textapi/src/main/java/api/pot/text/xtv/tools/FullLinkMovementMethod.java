package api.pot.text.xtv.tools;

import android.os.Handler;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;


public class FullLinkMovementMethod extends LinkMovementMethod {

    private Handler mLongClickHandler;
    private static int LONG_CLICK_TIME = 1000;
    private boolean mIsLongPressed = false;

    @Override
    public boolean onTouchEvent(final TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if(action == MotionEvent.ACTION_CANCEL){
            if(mLongClickHandler!=null){
                mLongClickHandler.removeCallbacksAndMessages(null);
            }
        }

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            final SpanEventListener[] link = buffer.getSpans(off, off, SpanEventListener.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    if(mLongClickHandler!=null){
                        mLongClickHandler.removeCallbacksAndMessages(null);
                    }
                    if(!mIsLongPressed) {
                        link[0].onClick(widget);
                    }
                    mIsLongPressed = false;
                } else {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                    mLongClickHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            link[0].onLongClick(widget);
                            mIsLongPressed = true;
                        }
                    },LONG_CLICK_TIME);
                }
                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }


    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new FullLinkMovementMethod();
            sInstance.mLongClickHandler = new Handler();
        }

        return sInstance;
    }
    private static FullLinkMovementMethod sInstance;
}