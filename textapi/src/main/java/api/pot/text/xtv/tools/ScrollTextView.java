package api.pot.text.xtv.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.Toast;

import api.pot.text.R;

@SuppressLint("AppCompatCustomView")
public class ScrollTextView extends MagicTextView {
    private boolean scroolOnLongSingle = true;

    public boolean isScroolOnLongSingle() {
        return scroolOnLongSingle;
    }

    public void setScroolOnLongSingle(boolean scroolOnLongSingle) {
        this.scroolOnLongSingle = scroolOnLongSingle;
        invalidate();
    }

    public void tongleScroolOnLongSingle() {
        setScroolOnLongSingle(!isScroolOnLongSingle());
    }

    // scrolling feature
    private Scroller mSlr;

    //pix/millisecond
    private float speed = 0.3f;

    // milliseconds for a round of scrolling
    private int mRndDuration = 5000;//250;

    // the X offset when paused
    private int mXPaused = 0;

    // whether it's being paused
    private boolean mPaused = true;

    /*
     * constructor
     */
    public ScrollTextView(Context context) {
        this(context, null);
        // customize the TextView
        //setSingleLine();
        setEllipsize(null);
        //setVisibility(INVISIBLE);
    }

    /*
     * constructor
     */
    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
        // customize the TextView
        //setSingleLine();
        setEllipsize(null);
        //setVisibility(INVISIBLE);
        init(attrs);
    }

    /*
     * constructor
     */
    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setEllipsize(null);
    }

    @Override
    public void init(AttributeSet attrs) {
        super.init(attrs);
        if(attrs != null){
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MagicTextView);
            scroolOnLongSingle = a.getBoolean( R.styleable.MagicTextView_xtv_scrool_on_long_single, true);
        }
        /**setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN && isSinglLine() && (isCutOnLongSingle() || isScroolOnLongSingle())){
                    tongleCutOnLongSingle();
                    invalidate();
                }
                return false;
            }
        });*/
    }

    public void tongle(){
        if(mPaused) startScroll();
        else pauseScroll();
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * begin to scroll the text from the original position
     */
    public void startScroll() {
        //
        setSingleLine();
        // begin from the very right side
        mXPaused = -1 * getWidth();
        // assume it's paused
        mPaused = true;
        resumeScroll();
    }

    /**
     * resume the scroll from the pausing point
     */
    public void resumeScroll() {

        if (!mPaused)
            return;

        // Do not know why it would not scroll sometimes
        // if setHorizontallyScrolling is called in constructor.
        setHorizontallyScrolling(true);

        // use LinearInterpolator for steady scrolling
        mSlr = new Scroller(this.getContext(), new LinearInterpolator());
        setScroller(mSlr);

        int scrollingLen = calculateScrollingLen();
        int distance = scrollingLen - (getWidth() + mXPaused);
        int duration = (new Double(/*mRndDuration*/(scrollingLen/speed) * distance * 1.00000
                / scrollingLen)).intValue();

        setVisibility(VISIBLE);
        mSlr.startScroll(mXPaused, 0, distance, 0, duration);
        mPaused = false;
    }

    private boolean scroolChecked = false;
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scroolControl();
    }

    private void scroolControl() {
        if(!isSinglLine()) return;
        if(scroolOnLongSingle && !cutOnLongSingle){
            if(calculateTextLen()>getWidth()){
                if(!scroolChecked) {
                    startScroll();
                    scroolChecked = true;
                }
            }else{
                scroolChecked = false;
                stopScroll();
            }
        }else if(!mPaused){
            scroolChecked = false;
            stopScroll();
        }
    }

    /**
     * calculate the scrolling length of the text in pixel
     *
     * @return the scrolling length in pixels
     */
    private int calculateScrollingLen() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        int scrollingLen = rect.width() + getWidth();
        rect = null;
        return scrollingLen;
    }

    @Override
    public CharSequence getText() {
        return super.getText();
    }

    /**
     * pause scrolling the text
     */
    public void pauseScroll() {
        if (null == mSlr)
            return;

        if (mPaused)
            return;

        mPaused = true;

        // abortAnimation sets the current X to be the final X,
        // and sets isFinished to be true
        // so current position shall be saved
        mXPaused = mSlr.getCurrX();

        mSlr.abortAnimation();/*

        mSlr.startScroll(0,0,0,0);

        mSlr.abortAnimation();*/
    }

    public void stopScroll() {
        if (null == mSlr)
            return;

        if (mPaused)
            return;

        mPaused = true;
    }

    @Override
    /*
     * override the computeScroll to restart scrolling when finished so as that
     * the text is scrolled forever
     */
    public void computeScroll() {
        super.computeScroll();

        if (null == mSlr)
            return;

        if (mSlr.isFinished() && (!mPaused)) {
            setAlpha(0);
            this.startScroll();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAlpha(1);
                }
            }, 500);
        }
    }

    public int getRndDuration() {
        return mRndDuration;
    }

    public void setRndDuration(int duration) {
        this.mRndDuration = duration;
    }

    public boolean isPaused() {
        return mPaused;
    }
}