package api.pot.text.xtv;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

import api.pot.text.xtv.tools.FormattedText;
import api.pot.text.xtv.tools.SmartTextView;

public class XTextView extends SmartTextView {
    public XTextView(Context context) {
        super(context);
        mainText = getText().toString();
    }

    public XTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mainText = getText().toString();
    }

    @Override
    public void setText(FormattedText formattedText) {
        super.setText(formattedText);
        mainText = getText().toString();
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        mainText = getText().toString();
    }

    private String mainText;

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            int start = getLayout().getLineStart(getFirstLineIndex(this));
            int end = getLayout().getLineEnd(getLastLineIndex(this));
            //Toast.makeText(getContext(), getNumberOfWordsDisplayed(this)+" \n:::\n "+getText().toString().substring(start, end), Toast.LENGTH_SHORT).show();
            if(end!=toIndex && (end-start)<mainText.length()){
                fromIndex = start;
                toIndex = end;
                if(toIndex-fromIndex>=1)
                    setText(mainText.substring(fromIndex, toIndex-1)+"…");
                else
                    setText(mainText.substring(fromIndex, toIndex));
            }
        }catch (Exception e){}
    }



    private int getNumberOfWordsDisplayed(TextView textView) {
        int start = textView.getLayout().getLineStart(getFirstLineIndex(textView));
        int end = textView.getLayout().getLineEnd(getLastLineIndex(textView));
        return textView.getText().toString().substring(start, end).split(" ").length;
    }

    /**
     * Gets the first line that is visible on the screen.
     *
     * @return
     */
    public int getFirstLineIndex(TextView textView) {
        int scrollY = textView.getScrollY();
        Layout layout = textView.getLayout();
        if (layout != null) {
            return layout.getLineForVertical(scrollY);
        }
        return -1;
    }

    /**
     * Gets the last visible line number on the screen.
     * @return last line that is visible on the screen.
     */
    public int getLastLineIndex(TextView textView) {
        int height = textView.getHeight();
        int scrollY = textView.getScrollY();
        Layout layout = textView.getLayout();
        if (layout != null) {
            return layout.getLineForVertical(scrollY + height);
        }
        return -1;
    }


}
