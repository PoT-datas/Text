package api.pot.text.tools;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

public class TextPainter {
    private static TextDatas textDatas = null;

    public static void drawTextCentered(Canvas canvas, TextDatas in_textDatas) {
        textDatas = in_textDatas;
        //textDatas = new TextDatas(in_textDatas);
        drawTextCentered(textDatas.text, textDatas.paint, canvas, textDatas.bounds, textDatas.isMonoLine);
    }

    public static void drawTextCentered(String text, Paint paint, Canvas canvas, RectF bounds, Boolean isMonoLine) {
        if(text==null || text.length()==0)return;

        if(textDatas == null) textDatas = new TextDatas();

        if(textDatas.size<=0 || paint==null || paint.getTextSize()<=0){
            if(paint==null){
                paint = new Paint();
            }
            textDatas.size = getTextSizeFromContainer(text, paint, bounds.width(), bounds.height());
            paint.setTextSize(textDatas.size);
        }

        if(textDatas.typeface!=null) paint.setTypeface(textDatas.typeface);
        paint.setAlpha(textDatas.alpha);
        paint.setColorFilter(textDatas.colorFilter);
        paint.setColor(textDatas.defaultColor);
        if(textDatas.softEdge>0) {
            paint.setMaskFilter(new BlurMaskFilter(textDatas.softEdge, BlurMaskFilter.Blur.NORMAL));
        }else {
            paint.setMaskFilter(null);
        }
        paint.setShader(textDatas.shader);

        String word = "";
        List<String> allwords = new ArrayList<>();
        String previous = null;
        for(Character c : text.toCharArray()){
            if(c.toString().equals(" ")){
                if(previous!=null && previous.equals(" "))
                    word+=c;
                else {
                    if(previous!=null)
                        allwords.add(word);
                    word=c+"";
                }
            }else {
                if(previous!=null && !previous.equals(" "))
                    word+=c;
                else {
                    if(previous!=null)
                        allwords.add(word);
                    word=c+"";
                }
            }
            previous = c.toString();
        }
        if(word!=null && word.length()!=0)
            allwords.add(word);

        String nextLine = "", next = "";
        List<String> allLines = new ArrayList<>();

        if(!isMonoLine) {
            for (String nextWord : allwords) {
                next = nextLine + nextWord;
                if (paint.measureText(next) <= bounds.width())
                    nextLine += nextWord;
                else {
                    if (nextLine.length() != 0)
                        allLines.add(nextLine);
                    nextLine = nextWord;
                }
            }
            if (nextLine.length() != 0)
                allLines.add(nextLine);
        }else {
            allLines.add(text);
        }

        //
        Rect textBounds = new Rect();
        paint.getTextBounds(allLines.get(0), 0, allLines.get(0).length(), textBounds);
        textDatas.setShadowRadius(textBounds);

        //for border
        Paint borderPaint = null;
        if(textDatas.borderWidth>0){
            borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(textDatas.borderColor);
            borderPaint.setShader(textDatas.borderShader);
            borderPaint.setStrokeWidth(textDatas.borderWidth);
            borderPaint.setAlpha(textDatas.alpha*textDatas.borderAlpha/255);
            borderPaint.setTypeface(paint.getTypeface());
            borderPaint.setTextSize(paint.getTextSize());
            borderPaint.setColorFilter(paint.getColorFilter());
            borderPaint.setMaskFilter(paint.getMaskFilter());
        }

        int i=0;
        for(String line : allLines){
            int xPos = (int) (bounds.left + (int) (bounds.width()/2 - (int)(paint.measureText(line)/2)));
            int yPos = (int) (bounds.top + ((int) bounds.height()/2) + ((int) (Math.abs(paint.descent() + paint.ascent()))) -
                                ((int) (Math.abs(paint.descent() + paint.ascent()))*allLines.size()/2) +
                                ((int) (Math.abs(paint.descent() + paint.ascent()))*i) -
                                (textDatas.lineSpacing*(allLines.size()-1)/2) +
                                (textDatas.lineSpacing*i));
            //shadow
            if(textDatas.shadowRadius>0) {
                Paint shadowPaint = new Paint();
                shadowPaint.setColor(textDatas.shadowColor);
                shadowPaint.setAlpha(textDatas.alpha*textDatas.borderAlpha/255);
                shadowPaint.setTypeface(paint.getTypeface());
                shadowPaint.setTextSize(paint.getTextSize());
                shadowPaint.setColorFilter(paint.getColorFilter());
                shadowPaint.setMaskFilter(paint.getMaskFilter());
                if (!textDatas.isShadowTypeGlow) {
                    shadowPaint.setShadowLayer(textDatas.shadowRadius + (textDatas.borderWidth > 0 ? textDatas.borderWidth : 0),
                            textDatas.shadowDx, textDatas.shadowDy, textDatas.shadowColor);
                    canvas.drawText(line, xPos, yPos, shadowPaint);
                } else {
                    float radius = textDatas.shadowRadius + textDatas.borderWidth > 0 ? textDatas.borderWidth : 0;
                    //
                    shadowPaint.setShadowLayer(radius, textDatas.shadowDx, 0, textDatas.shadowColor);
                    canvas.drawText(line, xPos, yPos, shadowPaint);
                    //
                    shadowPaint.setShadowLayer(radius, -textDatas.shadowDx, 0, textDatas.shadowColor);
                    canvas.drawText(line, xPos, yPos, shadowPaint);
                    //
                    shadowPaint.setShadowLayer(radius, 0, textDatas.shadowDy, textDatas.shadowColor);
                    canvas.drawText(line, xPos, yPos, shadowPaint);
                    //
                    shadowPaint.setShadowLayer(radius, 0, -textDatas.shadowDy, textDatas.shadowColor);
                    canvas.drawText(line, xPos, yPos, shadowPaint);
                }
            }
            //border
            if(textDatas.borderWidth>0){
                canvas.drawText(line, xPos, yPos, borderPaint);
            }
            //main
            canvas.drawText(line, xPos, yPos, paint);
            //
            i++;
        }
        //
        if(textDatas != null) textDatas = null;
    }

    public static float getTextSizeFromContainer(String text, Paint paint, float desiredWidth, float desiredHeight){
        float desiredTextSize = -1;
        if(!(text==null || text.length()==0 || text.indexOf("\n")!=-1)) {
            final float testTextSize = 48f;
            paint.setTextSize(testTextSize);
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            desiredTextSize = testTextSize * desiredWidth / bounds.width();
            int h_memorie = bounds.height();

            paint.setTextSize(desiredTextSize);
            bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            if (bounds.height() > desiredHeight) {
                desiredTextSize = testTextSize * desiredHeight / h_memorie;
            }
        }
        return desiredTextSize;
    }

    public static class TextDatas{

        public TextDatas() {
        }

        public TextDatas(TextDatas textDatas) {
            if(textDatas==null) return;

            this.size = textDatas.size;
            this.isMonoLine = textDatas.isMonoLine;
            if(textDatas.bounds!=null) this.bounds = new RectF(textDatas.bounds);
            this.text = textDatas.text;
            if(textDatas.paint!=null) this.paint = new Paint(textDatas.paint);


            this.borderShader = textDatas.borderShader;
            this.borderAlpha = textDatas.borderAlpha;
            this.lineSpacing = textDatas.lineSpacing;
            this.borderWidth = textDatas.borderWidth;
            this.shadowColor = textDatas.shadowColor;
            this.isShadowTypeGlow = textDatas.isShadowTypeGlow;
            //
            this.shadowRadius = textDatas.shadowRadius;
            this.ratioSR = textDatas.ratioSR;
            this.shadowDx = textDatas.shadowDx;
            this.shadowDy = textDatas.shadowDy;
            //
            this.defaultColor = textDatas.defaultColor;
            //
            this.typeface = textDatas.typeface;
            //
            this.alpha = textDatas.alpha;
            this.colorFilter = textDatas.colorFilter;
            this.softEdge = textDatas.softEdge;
            this.borderColor = textDatas.borderColor;
            //
            this.shader = textDatas.shader;
        }

        public float size = -1;
        public boolean isMonoLine = false;
        public RectF bounds;
        public String text;
        public  Paint paint;

        public int alpha = 255;
        public ColorFilter colorFilter;
        public float softEdge = 0;
        public int borderColor = Color.BLACK;

        public Shader shader;

        public Shader borderShader = null;
        public int borderAlpha = 255;
        public int lineSpacing = 0;
        public float borderWidth = 0;
        public int shadowColor = Color.BLACK;
        public boolean isShadowTypeGlow = false;
        //
        public int shadowRadius = 1;
        public int ratioSR = 5;
        public int shadowDx = 1;
        public int shadowDy = 1;
        //
        public int defaultColor = Color.WHITE;
        //
        public Typeface typeface = Global.ges_main_type_face;

        public void setShadowRadius(Rect textBounds) {
            this.shadowRadius = (int) Math.sqrt(textBounds.height()/ratioSR);
            if(this.shadowRadius==0) this.shadowRadius = 1;
            shadowDx = this.shadowRadius;
            shadowDy = (int) (this.shadowRadius*1.5);
        }

        public void setBounds(RectF bounds){
            this.bounds = bounds;
            //if(paint!=null) paint.setStrokeWidth(-1);
            paint = null;
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.colorFilter = colorFilter;
        }

        public void setTextSize(int size) {
            this.size = size;
        }
    }
}
