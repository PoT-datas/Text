package api.pot.text.xtv.tools;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import java.util.ArrayList;
import java.util.List;

public class FormattedText {
    private String text = null;
    private int startIndex=-1, endIndex=-1;

    private List<TextFormat> textFormats = new ArrayList<TextFormat>();
    private int i=-1, j=-1;

    private TextFormat textFormat;

    public FormattedText() {
    }

    public FormattedText(String text) {
        if(text!=null)
        setText(text, 0, text.length());
    }

    public FormattedText(String text, int startIndex, int endIndex) {
        if(text!=null)
        setText(text, startIndex, endIndex);
    }

    public void setText(String text, int startIndex, int endIndex) {
        if(text!=null) {
            this.text = text;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }

    public void setText(String text) {
        if(text!=null)
        setText(text, 0, text.length());
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public String toString() {
        return text;
    }

    public void setup(SpannableString ss) {
        if(ss==null || text==null || startIndex<0 || endIndex<=0 || text.length()<endIndex || textFormats==null || textFormats.size()==0) return;
        for(TextFormat textFormat : textFormats){
            i = (0<=textFormat.startIndex && textFormat.startIndex<=text.length())?textFormat.startIndex:startIndex;
            j = (0<=textFormat.endIndex && textFormat.endIndex<=text.length())?textFormat.endIndex:endIndex;
            switch (textFormat.style){
                case BOLD:
                    ss.setSpan(new StyleSpan(Typeface.BOLD), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case ITALIC:
                    ss.setSpan(new StyleSpan(Typeface.ITALIC), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case BOLD_ITALIC:
                    ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case UNDERLINE:
                    ss.setSpan(new UnderlineSpan(), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case STRIKE_THROUGH:
                    ss.setSpan(new StrikethroughSpan(), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case CLICK_LISTENER:
                    ss.setSpan(textFormat.clickableSpan, i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case COLOR:
                    ss.setSpan(new ForegroundColorSpan(textFormat.color), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case TYPE_FACE:
                    ss.setSpan(new CustomTypefaceSpan(textFormat.typefaceFamily, textFormat.typeface), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case EVENT_LISTENER:
                    ss.setSpan(textFormat.spanEventListener, i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                case VISIBILITY:
                    ss.setSpan(new VisibilitySpan(textFormat.visibility), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case BACKGROUND:
                    ss.setSpan(textFormat.backgroundRadius<0?(new BackgroundColorSpan(textFormat.backgroundColor)):(new RoundedBackgroundColorSpan(
                            textFormat.backgroundColor,
                            textFormat.backgroundPadding,
                            textFormat.backgroundRadius
                    )), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }
        }
    }

    public void setBold() {
        setBold(-1,-1);
    }
    public void setBold(int startIndex, int endIndex) {
        textFormats.add(new TextFormat(FormatStyle.BOLD, startIndex, endIndex));
    }

    public void setItalic() {
        setItalic(-1,-1);
    }
    public void setItalic(int startIndex, int endIndex) {
        textFormats.add(new TextFormat(FormatStyle.ITALIC, startIndex, endIndex));
    }

    public void setBoldItalic() {
        setBoldItalic(-1,-1);
    }
    public void setBoldItalic(int startIndex, int endIndex) {
        textFormats.add(new TextFormat(FormatStyle.BOLD_ITALIC, startIndex, endIndex));
    }

    public void setUnderline() {
        setUnderline(-1,-1);
    }
    public void setUnderline(int startIndex, int endIndex) {
        textFormats.add(new TextFormat(FormatStyle.UNDERLINE, startIndex, endIndex));
    }

    public void setStrikeThrough() {
        setStrikeThrough(-1,-1);
    }
    public void setStrikeThrough(int startIndex, int endIndex) {
        textFormats.add(new TextFormat(FormatStyle.STRIKE_THROUGH, startIndex, endIndex));
    }

    public void setClickListener(ClickableSpan clickableSpan) {
        setClickListener(-1,-1, clickableSpan);
    }
    public void setClickListener(int startIndex, int endIndex, ClickableSpan clickableSpan) {
        TextFormat textFormat = new TextFormat(FormatStyle.CLICK_LISTENER, startIndex, endIndex);
        textFormat.clickableSpan = clickableSpan;
        textFormats.add(textFormat);
    }

    public void setColor(int color) {
        setColor(-1,-1, color);
    }
    public void setColor(int startIndex, int endIndex, int color) {
        TextFormat textFormat = new TextFormat(FormatStyle.COLOR, startIndex, endIndex);
        textFormat.color = color;
        textFormats.add(textFormat);
    }

    public void setTypeFace(Typeface typeface) {
        setTypeFace(-1,-1, "", typeface);
    }
    public void setTypeFace(int startIndex, int endIndex, Typeface typeface) {
        setTypeFace(startIndex, endIndex, "", typeface);
    }

    public void setTypeFace(String family, Typeface typeface) {
        setTypeFace(-1,-1, family, typeface);
    }
    public void setTypeFace(int startIndex, int endIndex, String family, Typeface typeface) {
        TextFormat textFormat = new TextFormat(FormatStyle.TYPE_FACE, startIndex, endIndex);
        textFormat.typeface = typeface;
        textFormat.typefaceFamily = family;
        textFormats.add(textFormat);
    }

    public void setEventListener(SpanEventListener spanEventListener) {
        setEventListener(-1,-1, spanEventListener);
    }
    public void setEventListener(int startIndex, int endIndex, SpanEventListener spanEventListener) {
        TextFormat textFormat = new TextFormat(FormatStyle.EVENT_LISTENER, startIndex, endIndex);
        textFormat.spanEventListener = spanEventListener;
        textFormats.add(textFormat);
    }

    public void setVisibility(int visibility) {
        setVisibility(-1,-1, visibility);
    }
    public void setVisibility(int startIndex, int endIndex, int visibility) {
        TextFormat textFormat = new TextFormat(FormatStyle.VISIBILITY, startIndex, endIndex);
        textFormat.visibility = visibility;
        textFormats.add(textFormat);
    }

    public void setBackground(int color, float padding, float radius) {
        setBackground(-1, -1, color, padding, radius);
    }
    public void setBackground(int startIndex, int endIndex, int color, float padding, float radius) {
        TextFormat textFormat = new TextFormat(FormatStyle.BACKGROUND, startIndex, endIndex);
        textFormat.backgroundColor = color;
        textFormat.backgroundPadding = padding;
        textFormat.backgroundRadius = radius;
        textFormats.add(textFormat);
    }
    public void setBackground(int color) {
        setBackground(-1, -1, color);
    }
    public void setBackground(int startIndex, int endIndex, int color) {
        TextFormat textFormat = new TextFormat(FormatStyle.BACKGROUND, startIndex, endIndex);
        textFormat.backgroundColor = color;
        textFormats.add(textFormat);
    }

    /* this class must content all params as possible for any formatting*/
    private class TextFormat{
        public FormatStyle style;
        public int startIndex=-1, endIndex=-1;//apply over the text when value=-1
        public int color;
        public Typeface typeface;
        public String typefaceFamily = "";
        public ClickableSpan clickableSpan;
        public SpanEventListener spanEventListener;
        public int visibility = VisibilitySpan.VISIBLE;
        public int backgroundColor;
        public float backgroundPadding = -1;
        public float backgroundRadius = -1;

        public TextFormat(FormatStyle style, int startIndex, int endIndex) {
            this.style = style;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }

    public enum FormatStyle {
        NONE, BOLD, ITALIC, BOLD_ITALIC, UNDERLINE, STRIKE_THROUGH, CLICK_LISTENER, COLOR, TYPE_FACE, EVENT_LISTENER, VISIBILITY, BACKGROUND;
    }

}
