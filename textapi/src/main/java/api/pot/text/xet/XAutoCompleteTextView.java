package api.pot.text.xet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class XAutoCompleteTextView extends AutoCompleteTextView {
    public XAutoCompleteTextView(Context context) {
        super(context);
    }

    public XAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
            performFiltering(getText(), 0);
        }
    }

    /**/
    /*@Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            performFiltering(getText(), 0);
            showDropDown();
        }
    }*/

    //set threshold 0.
    /*public void setThreshold(int threshold) {
        if (threshold < 0) {
            threshold = 0;
        }
        myThreshold = threshold;
    }
    //if threshold   is 0 than return true
    public boolean enoughToFilter() {
        return true;
    }
    //invoke on focus
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        //skip space and backspace
        super.performFiltering("", 67);
        // TODO Auto-generated method stub
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

    }

    protected void performFiltering(CharSequence text, int keyCode) {
        // TODO Auto-generated method stub
        super.performFiltering(text, keyCode);
    }

    public int getThreshold() {
        return myThreshold;
    }*/

}
