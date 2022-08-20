package com.pot.text.test;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.pot.text.R;
import api.pot.text.tools.Global;
import api.pot.text.xtv.XTextView;
import api.pot.text.xtv.tools.FormattedText;
import api.pot.text.xtv.tools.SmartTextCallback;
import api.pot.text.xtv.tools.SpanEventListener;

public class XActivity extends AppCompatActivity {

    XTextView ctv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x);


        final String sampleText = getString(R.string.sample_text);

        XTextView simple = (XTextView) findViewById(R.id.simple);
        String text = "so for that\nI do\nlove you";
        /*SpannableString ss = new SpannableString(text);
        //
        //
        simple.setText(ss);
        simple.setMovementMethod(FullLinkMovementMethod.getInstance());*/



        FormattedText f = new FormattedText(text);
        f.setBackground(Color.parseColor("#ffdddd"), 20f, 20f);
        //
        simple.setText(f);
        //
        simple.setShadowLayer(20, 0f, 0f, 0);
        simple.setPadding(20, 20, 20, 20);




        /*EditText editText = (EditText) findViewById(R.id.editText);
        int padding = dp(this, 8);
        int radius = dp(this, 5);
        //
        final Object span = new RoundedBackgroundColorSpan(
                Color.parseColor("#ffaaaa"),
                (float)padding,
                (float) radius
        );
        //
        editText.setShadowLayer(padding, 0f, 0f, 0);
        editText.setPadding(padding, padding, padding, padding);
        //
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                s.setSpan(span, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });*/




        ctv = (XTextView) findViewById(R.id.ctv);
        ////////////
        FormattedText formattedText = new FormattedText("BOLD and ITALIC and BOLD-ITALIC and UNDERLINE and STRIKE-THROUGH and click and RED and AlluraRegular.ttf and longClick\n"
                + sampleText
                + "Some GeoPos : 3°50'50.076\"N 11°28'45.222\"E and \\3 50.8346, 11 28.7537 and 3.847244, 11.479229 ");
        formattedText.setBold(0, 4);
        formattedText.setItalic(9, 15);
        formattedText.setBoldItalic(20, 31);
        formattedText.setUnderline(36, 45);
        formattedText.setStrikeThrough(50, 64);
        formattedText.setClickListener(69, 74,
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(XActivity.this, "click", Toast.LENGTH_SHORT).show();
                    }
                });
        formattedText.setColor(79, 82, Color.RED);
        formattedText.setTypeFace(87, 104, Global.ges_main_type_face);
        formattedText.setEventListener(109, 118,
                new SpanEventListener() {
                    @Override
                    public void onLongClick(View view) {
                        Toast.makeText(XActivity.this, "longclick", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(XActivity.this, "click2", Toast.LENGTH_SHORT).show();
                    }
                });
        formattedText.setBackground(5, 8, Color.parseColor("#ffdddd"));
        ///////
        FormattedText format = new FormattedText();
        format.setBold();
        format.setTypeFace(Global.ges_large_type_face);
        ctv.setFormats(format, format, format, format, format, format, format);
        //
        ctv.setEmailColorCode("#3cb371");
        ctv.setPhoneNumberColorCode("#ff33aa");
        ctv.setHashTagColorCode("#f37735");
        ctv.setUrlColorCode("#ffc425");
        ctv.setMentionColorCode("#57b8dd");
        ctv.setmGeoPosColorCode("#ffaadd");
        ctv.setmRegexColorCode("#000000");
        //
        ctv.setRegex("(is a)|(and)");
        ctv.setDetection(true, true, true, true, true, true, true);
        ctv.setAutoRegexClick(false);
        //
        //ctv.setSmartTextCallback(this);
        ////////
        ctv.setText(formattedText);

    }

}
