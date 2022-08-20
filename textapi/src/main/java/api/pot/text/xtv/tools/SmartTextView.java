package api.pot.text.xtv.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import api.pot.text.tools.Global;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("AppCompatCustomView")
public class SmartTextView extends ScrollTextView {

    private Context mContext;

    ////////////
    private String mEmailColorCode;
    private String mUrlColorCode;
    private String mPhoneNumberColorCode;
    private String mMentionColorCode;
    private String mHashTagColorCode;
    private String mGeoPosColorCode;
    private String mRegexColorCode;

    private FormattedText emailFormat;
    private FormattedText urlFormat;
    private FormattedText phoneNumberFormat;
    private FormattedText mentionFormat;
    private FormattedText hashTagFormat;
    private FormattedText geoPosFormat;
    private FormattedText regexFormat;
    //
    private FormattedText specialCharFormat;
    ///////////

    private SmartTextCallback mSmartTextCallback;

    private boolean detectEmails = false;
    private boolean detectUrls = false;
    private boolean detectPhoneNumbers = false;
    private boolean detectMentions = false;
    private boolean detectHashTags = false;
    private boolean detectGeoPos = false;
    private boolean detectRegex = false;

    private boolean autoEmailsClick = true;
    private boolean autoUrlsClick = true;
    private boolean autoPhoneNumbersClick = true;
    private boolean autoMentionsClick = true;
    private boolean autoHashTagsClick = true;
    private boolean autoGeoPosClick = true;
    private boolean autoRegexClick = true;

    private boolean regexCaseSensitive = false;
    private String regex = null;

    private FormattedText formattedText;

    private boolean must_update = false;
    private Handler handler_for_invalidate = null;
    private Runnable runnable_for_invalidate = null;

    public void invalidater(){
        setup();
        /*if(must_update) return;
        must_update = true;*/
    }

    public void initInvalidater(){
        if(runnable_for_invalidate!=null && handler_for_invalidate!=null) return;
        //
        handler_for_invalidate = new Handler();
        runnable_for_invalidate = new Runnable() {
            @Override
            public void run() {
                View parent = (View)getParent();
                RectF parentBound = null;
                if(parent!=null) {
                    int[] location = new int[2];
                    parent.getLocationOnScreen(location);
                    parentBound = new RectF(location[0], location[1], location[0] + parent.getWidth(), location[1] + parent.getHeight());
                }
                //
                if(must_update && Global.isViewIntoContainer(SmartTextView.this, parentBound)) {
                    setup();
                    must_update = false;
                }
                //
                handler_for_invalidate.postDelayed(runnable_for_invalidate, Global.ctv_invalidate_time);
            }
        };
        handler_for_invalidate.postDelayed(runnable_for_invalidate, Global.ctv_invalidate_time);
    }

    public SmartTextView(Context context){
        super(context);
        this.mContext = context;
        //
        initInvalidater();
    }

    public SmartTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mEmailColorCode = "#3344ff";
        this.mUrlColorCode = "#3344ff";
        this.mPhoneNumberColorCode = "#3344ff";
        this.mMentionColorCode = "#3344ff";
        this.mHashTagColorCode = "#3344ff";
        this.mGeoPosColorCode = "#33ddff";
        this.mRegexColorCode = "#000000";
        //
        specialCharFormat = new FormattedText();
        specialCharFormat.setColor(Color.parseColor("#dddddd"));//wont matched with visibility set
        //specialCharFormat.setVisibility(VisibilitySpan.INVISIBLE);
        //
        initInvalidater();
    }

    public void setText(FormattedText formattedText){
        this.formattedText = formattedText;
        if(formattedText!=null)
            super.setText(formattedText.toString());
        invalidater();
    }

    public void setText(String text){
        super.setText(text);
        if(formattedText!=null)
            formattedText.setText(getText().toString());
        invalidater();
    }

    private void setup(){
        String text = getText().toString();
        ///
        SpannableString ss = new SpannableString(text);
        ///
        if(formattedText!=null) formattedText.setup(ss);
        ///
        //
        int fromIndex = 0;
        // Splitting the words by spaces
        String[] words = text.split(" ");
        for (String word : words){
            Log.d("test", word);
            word = word.replaceAll("\n", "");

            if (detectEmails && word.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")){
                // the word is email. Set the email span
                int startIndex = text.indexOf(word, fromIndex);
                int endIndex = fromIndex = startIndex + word.length();
                final String finalWord = word;
                if(autoEmailsClick) {
                    ClickableSpan emailClickSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mSmartTextCallback == null) {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", finalWord, null));
                                mContext.startActivity(Intent.createChooser(emailIntent, "Send Email"));
                            } else {
                                mSmartTextCallback.emailClick(finalWord);
                            }
                        }
                    };
                    ss.setSpan(emailClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if(mEmailColorCode!=null) {
                    ForegroundColorSpan emailColorSpan = new ForegroundColorSpan(Color.parseColor(mEmailColorCode));
                    ss.setSpan(emailColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Log.d("test", "Email matched" + startIndex + " : " + endIndex);
                ///
                if(emailFormat!=null){
                    emailFormat.setText(text);
                    emailFormat.setStartIndex(startIndex);
                    emailFormat.setEndIndex(endIndex);
                    emailFormat.setup(ss);
                }
            }
            else if (detectUrls && word.matches("(http:\\/\\/|https:\\/\\/|www.).{3,}")){
                // word is a URL
                int startIndex = text.indexOf(word, fromIndex);
                int endIndex = fromIndex = startIndex + word.length();
                final String finalWord1 = word.startsWith("http://") || word.startsWith("https://") ? word : "http://" + word;
                if(autoUrlsClick) {
                    ClickableSpan urlClickSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mSmartTextCallback == null) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(finalWord1));
                                mContext.startActivity(i);
                            } else {
                                mSmartTextCallback.webUrlClick(finalWord1);
                            }
                        }
                    };
                    ss.setSpan(urlClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if(mUrlColorCode!=null) {
                    ForegroundColorSpan urlColorSpan = new ForegroundColorSpan(Color.parseColor(mUrlColorCode));
                    ss.setSpan(urlColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Log.d("test", "Url matched" + startIndex + " : " + endIndex);
                ///
                if(urlFormat!=null){
                    urlFormat.setText(text);
                    urlFormat.setStartIndex(startIndex);
                    urlFormat.setEndIndex(endIndex);
                    urlFormat.setup(ss);
                }
            }
            /*else if (detectPhoneNumbers && word.matches("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b")){
                // word is a phone number
                int startIndex = text.indexOf(word, fromIndex);
                int endIndex = fromIndex = startIndex + word.length();
                final String finalWord2 = word;
                if(autoPhoneNumbersClick) {
                    ClickableSpan numberClickSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mSmartTextCallback == null) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + finalWord2));
                                mContext.startActivity(intent);
                            } else {
                                mSmartTextCallback.phoneNumberClick(finalWord2);
                            }
                        }
                    };
                    ss.setSpan(numberClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if(mPhoneNumberColorCode!=null) {
                    ForegroundColorSpan numberColorSpan = new ForegroundColorSpan(Color.parseColor(mPhoneNumberColorCode));
                    ss.setSpan(numberColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Log.d("test", "Phone Number matched" + startIndex + " : " + endIndex);
                ///
                if(phoneNumberFormat!=null){
                    phoneNumberFormat.setText(text);
                    phoneNumberFormat.setStartIndex(startIndex);
                    phoneNumberFormat.setEndIndex(endIndex);
                    phoneNumberFormat.setup(ss);
                }
            }*/
            else if (detectMentions && word.startsWith("@") && word.length() >= 2){
                // word is a mention
                int startIndex = text.indexOf(word, fromIndex);
                int endIndex = fromIndex = startIndex + word.length();
                final String finalWord3 = word;
                if(autoMentionsClick) {
                    ClickableSpan mentionClickSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mSmartTextCallback == null) {
                                Log.e("test", "Implement SmartTextCallback and set setSmartTextCallback(this) in your activity/fragment");
                            } else {
                                mSmartTextCallback.mentionClick(finalWord3);
                            }
                        }
                    };
                    ss.setSpan(mentionClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if(mMentionColorCode!=null) {
                    ForegroundColorSpan mentionColorSpan = new ForegroundColorSpan(Color.parseColor(mMentionColorCode));
                    ss.setSpan(mentionColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Log.d("test", "Mention matched" + startIndex + " : " + endIndex);
                ///
                if(mentionFormat!=null){
                    mentionFormat.setText(text);
                    mentionFormat.setStartIndex(startIndex);
                    mentionFormat.setEndIndex(endIndex);
                    mentionFormat.setup(ss);
                }
            }
            else if (detectHashTags && word.startsWith("#") && word.length() >= 2){
                // word is a hash tag
                int startIndex = text.indexOf(word, fromIndex);
                int endIndex = fromIndex = startIndex + word.length();
                final String finalWord4 = word;
                if(autoHashTagsClick) {
                    ClickableSpan hashTagClickSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mSmartTextCallback == null) {
                                Log.e("test", "Implement SmartTextCallback and set setSmartTextCallback(this) in your activity/fragment");
                            } else {
                                mSmartTextCallback.hashTagClick(finalWord4);
                            }
                        }
                    };
                    ss.setSpan(hashTagClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if(mHashTagColorCode!=null) {
                    ForegroundColorSpan hashTagColorSpan = new ForegroundColorSpan(Color.parseColor(mHashTagColorCode));
                    ss.setSpan(hashTagColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Log.d("test", "Hash tag matched" + startIndex + " : " + endIndex);
                ///
                if(hashTagFormat!=null){
                    hashTagFormat.setText(text);
                    hashTagFormat.setStartIndex(startIndex);
                    hashTagFormat.setEndIndex(endIndex);
                    hashTagFormat.setup(ss);
                }
            }
            /*else if (detectGeoPos && word.matches("is")){
                // word is a particular regex
                int startIndex = text.indexOf(word, fromIndex);
                int endIndex = fromIndex = startIndex + word.length();
                final String finalWord5 = word;
                if(autoGeoPosClick) {
                    ClickableSpan geoPosClickSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mSmartTextCallback == null) {
                                //
                            } else {
                                mSmartTextCallback.geoPosClick(0, 0);
                            }
                        }
                    };
                    ss.setSpan(geoPosClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if(mGeoPosColorCode!=null) {
                    ForegroundColorSpan geoPosColorSpan = new ForegroundColorSpan(Color.parseColor(mGeoPosColorCode));
                    ss.setSpan(geoPosColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Log.d("test", "georaphic position matched" + startIndex + " : " + endIndex);
                ///
                if(geoPosFormat!=null){
                    geoPosFormat.setText(text);
                    geoPosFormat.setStartIndex(startIndex);
                    geoPosFormat.setEndIndex(endIndex);
                    geoPosFormat.setup(ss);
                }
            }*/
            /*else if (detectRegex && regex!=null && regex.length()>0 && (regexCaseSensitive?word.matches(regex):word.toLowerCase().matches(regex.toLowerCase()))){
                // word is a particular regex
                int startIndex = text.indexOf(word, fromIndex);
                int endIndex = fromIndex = startIndex + word.length();
                final String finalWord6 = word;
                if(autoRegexClick) {
                    ClickableSpan regexClickSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mSmartTextCallback == null) {
                                Toast.makeText(getContext(), finalWord6, Toast.LENGTH_SHORT).show();
                            } else {
                                mSmartTextCallback.regexClick(finalWord6);
                            }
                        }
                    };
                    ss.setSpan(regexClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if(mRegexColorCode!=null) {
                    ForegroundColorSpan regexColorSpan = new ForegroundColorSpan(Color.parseColor(mRegexColorCode));
                    ss.setSpan(regexColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Log.d("test", "particular regex matched" + startIndex + " : " + endIndex);
                ///
                if(regexFormat!=null){
                    regexFormat.setText(text);
                    regexFormat.setStartIndex(startIndex);
                    regexFormat.setEndIndex(endIndex);
                    regexFormat.setup(ss);
                }
            }*/
        }
        //
        String dd = "[+-]?\\d{1,2}([.]\\d+)?(,)( )?[+-]?\\d{1,3}([.]\\d+)?",
                ddm = "[+-]?\\d{1,2}( )[+-]?\\d{1,2}([.]\\d+)?(,)( )[+-]?\\d{1,3}( )[+-]?\\d{1,2}([.]\\d+)?",
                dms = "\\d{1,2}(°)\\d{1,2}(')\\d{1,4}([.]\\d+)?(\")( )?[N,S]( )?\\d{1,3}(°)\\d{1,2}(')\\d{1,4}([.]\\d+)?(\")( )?[W,E]";
        String regex_phoneNumber = "(?!(0-9))(?!(a-z))(?!(A-Z))(\\+[0-9]{1,3}( )?)?((([0-9])(( )?[0-9]){7,11})|(\\([0-9]{1,4}\\)( )[0-9]{1,4}-[0-9]{1,4}))(?!(0-9))(?!(a-z))(?!(A-Z))";
        //
        String copyText = regexCaseSensitive?text:text.toLowerCase();
        //
        Pattern pattern_dd = Pattern.compile(dd), pattern_ddm = Pattern.compile(ddm), pattern_dms = Pattern.compile(dms),
                pattern_regex = regex!=null?Pattern.compile(regexCaseSensitive?regex:regex.toLowerCase()):null, pattern_phoneNumber = Pattern.compile(regex_phoneNumber);
        Matcher matcher_dd = pattern_dd.matcher(text), matcher_ddm = pattern_ddm.matcher(text), matcher_dms = pattern_dms.matcher(text),
                matcher_regex = regex!=null?pattern_regex.matcher(copyText):null, matcher_phoneNumber = pattern_phoneNumber.matcher(text);
        //
        fromIndex = 0;
        int last_fromIndex = fromIndex;
        //
        while (detectPhoneNumbers){
            if (detectPhoneNumbers && matcher_phoneNumber.find(fromIndex)){
                // word is a phone number
                int startIndex = matcher_phoneNumber.start();
                int endIndex = fromIndex = matcher_phoneNumber.end();
                final String finalWord2 = matcher_phoneNumber.group();
                if(Patterns.PHONE.matcher(finalWord2).matches()) {
                    if (autoPhoneNumbersClick) {
                        ClickableSpan numberClickSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                if (mSmartTextCallback == null) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + finalWord2));
                                    mContext.startActivity(intent);
                                } else {
                                    mSmartTextCallback.phoneNumberClick(finalWord2);
                                }
                            }
                        };
                        ss.setSpan(numberClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (mPhoneNumberColorCode != null) {
                        ForegroundColorSpan numberColorSpan = new ForegroundColorSpan(Color.parseColor(mPhoneNumberColorCode));
                        ss.setSpan(numberColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    Log.d("test", "Phone Number matched" + startIndex + " : " + endIndex);
                    ///
                    if (phoneNumberFormat != null) {
                        phoneNumberFormat.setText(text);
                        phoneNumberFormat.setStartIndex(startIndex);
                        phoneNumberFormat.setEndIndex(endIndex);
                        phoneNumberFormat.setup(ss);
                    }
                }
            }
            //
            if(last_fromIndex==fromIndex) break;
            last_fromIndex = fromIndex;
        }
        //
        fromIndex = 0;
        last_fromIndex = fromIndex;
        //
        while (detectGeoPos){
            if(detectGeoPos){
                if (matcher_dms.find(fromIndex)){
                    int startIndex = matcher_dms.start();
                    int endIndex = fromIndex = matcher_dms.end();
                    final String value = matcher_dms.group();
                    double[] geopos = GeoPos.getPosFromDMS(value);
                    setGeoPos(startIndex, endIndex, geopos[0], geopos[1], ss, text);
                }else if (matcher_ddm.find(fromIndex)){
                    int startIndex = matcher_ddm.start();
                    int endIndex = fromIndex = matcher_ddm.end();
                    final String value = matcher_ddm.group();
                    double[] geopos = GeoPos.getPosFromDDM(value);
                    setGeoPos(startIndex, endIndex, geopos[0], geopos[1], ss, text);
                }else if (matcher_dd.find(fromIndex)){
                    int startIndex = matcher_dd.start();
                    int endIndex = fromIndex = matcher_dd.end();
                    final String value = matcher_dd.group();
                    double[] geopos = GeoPos.getPosFromDD(value);
                    setGeoPos(startIndex, endIndex, geopos[0], geopos[1], ss, text);
                }
            }
            //
            if(last_fromIndex==fromIndex) break;
            last_fromIndex = fromIndex;
        }
        //
        fromIndex = 0;
        last_fromIndex = fromIndex;
        //
        while (detectRegex){
            if (detectRegex && regex!=null && regex.length()>0 && matcher_regex.find(fromIndex)){
                // a particular regex
                int startIndex = matcher_regex.start();
                int endIndex = fromIndex = matcher_regex.end();
                final String finalWord6 = matcher_regex.group();
                if(autoRegexClick) {
                    ClickableSpan regexClickSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mSmartTextCallback == null) {
                                Toast.makeText(getContext(), finalWord6, Toast.LENGTH_SHORT).show();
                            } else {
                                mSmartTextCallback.regexClick(finalWord6);
                            }
                        }
                    };
                    ss.setSpan(regexClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if(mRegexColorCode!=null) {
                    ForegroundColorSpan regexColorSpan = new ForegroundColorSpan(Color.parseColor(mRegexColorCode));
                    ss.setSpan(regexColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                Log.d("test", "particular regex matched" + startIndex + " : " + endIndex);
                ///
                if(regexFormat!=null){
                    regexFormat.setText(text);
                    regexFormat.setStartIndex(startIndex);
                    regexFormat.setEndIndex(endIndex);
                    regexFormat.setup(ss);
                }
            }
            //
            if(last_fromIndex==fromIndex) break;
            last_fromIndex = fromIndex;
        }

        super.setText(ss);
        super.setMovementMethod(FullLinkMovementMethod.getInstance());
    }

    private void setGeoPos(int startIndex, int endIndex, final double lat, final double lng, SpannableString ss, String text) {
        boolean special = false;
        try {
            special=(startIndex-1>=0) && text.substring(startIndex-1, startIndex).equals("\\") && ((startIndex-2<0) || !text.substring(startIndex-2, startIndex-1).equals("\\"));
        }catch (Exception e){}
        if(Math.abs(lat)>90 || Math.abs(lng)>180 || special) {
            if(special) {
                if(specialCharFormat!=null){
                    specialCharFormat.setText(text);
                    specialCharFormat.setStartIndex(startIndex-1);
                    specialCharFormat.setEndIndex(startIndex);
                    specialCharFormat.setup(ss);
                }
            }
            return;
        }
        //
        if(autoGeoPosClick) {
            ClickableSpan geoPosClickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (mSmartTextCallback == null) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://maps.google.com/maps?q="+lat+","+lng+"&z=18"));
                        mContext.startActivity(i);
                    } else {
                        mSmartTextCallback.geoPosClick(lat, lng);
                    }
                }
            };
            ss.setSpan(geoPosClickSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if(mGeoPosColorCode!=null) {
            ForegroundColorSpan geoPosColorSpan = new ForegroundColorSpan(Color.parseColor(mGeoPosColorCode));
            ss.setSpan(geoPosColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        Log.d("test", "georaphic position matched" + startIndex + " : " + endIndex);
        ///
        if(geoPosFormat!=null){
            geoPosFormat.setText(text);
            geoPosFormat.setStartIndex(startIndex);
            geoPosFormat.setEndIndex(endIndex);
            geoPosFormat.setup(ss);
        }
    }

    ///////////////////
    public void setEmailColorCode(String emailColorCode) {
        this.mEmailColorCode = emailColorCode;
        invalidater();
    }
    public void setUrlColorCode(String urlColorCode){
        this.mUrlColorCode = urlColorCode;
        invalidater();
    }
    public void setPhoneNumberColorCode(String phoneNumberColorCode){
        this.mPhoneNumberColorCode = phoneNumberColorCode;
        invalidater();
    }
    public void setHashTagColorCode(String hashTagColorCode){
        this.mHashTagColorCode = hashTagColorCode;
        invalidater();
    }
    public void setMentionColorCode(String mentionColorCode){
        this.mMentionColorCode = mentionColorCode;
        invalidater();
    }
    public void setmGeoPosColorCode(String mGeoPosColorCode) {
        this.mGeoPosColorCode = mGeoPosColorCode;
        invalidater();
    }
    public void setmRegexColorCode(String mRegexColorCode) {
        this.mRegexColorCode = mRegexColorCode;
        invalidater();
    }

    ///

    public void setEmailColorCode(int emailColorCode) {
        this.mEmailColorCode = "#" + Integer.toHexString(emailColorCode).substring(2);
        invalidater();
    }
    public void setUrlColorCode(int urlColorCode){
        this.mUrlColorCode = "#" + Integer.toHexString(urlColorCode).substring(2);
        invalidater();
    }
    public void setPhoneNumberColorCode(int phoneNumberColorCode){
        this.mPhoneNumberColorCode = "#" + Integer.toHexString(phoneNumberColorCode).substring(2);
        invalidater();
    }
    public void setHashTagColorCode(int hashTagColorCode){
        this.mHashTagColorCode = "#" + Integer.toHexString(hashTagColorCode).substring(2);
        invalidater();
    }
    public void setMentionColorCode(int mentionColorCode){
        this.mMentionColorCode = "#" + Integer.toHexString(mentionColorCode).substring(2);
        invalidater();
    }
    public void setmGeoPosColorCode(int mGeoPosColorCode) {
        this.mGeoPosColorCode = "#" + Integer.toHexString(mGeoPosColorCode).substring(2);
        invalidater();
    }
    public void setmRegexColorCode(int mRegexColorCode) {
        this.mRegexColorCode = "#" + Integer.toHexString(mRegexColorCode).substring(2);
        invalidater();
    }

    ///

    public void setEmailFormat(FormattedText emailFormat) {
        this.emailFormat = emailFormat;
        invalidater();
    }

    public void setUrlFormat(FormattedText urlFormat) {
        this.urlFormat = urlFormat;
        invalidater();
    }

    public void setPhoneNumberFormat(FormattedText phoneNumberFormat) {
        this.phoneNumberFormat = phoneNumberFormat;
        invalidater();
    }

    public void setMentionFormat(FormattedText mentionFormat) {
        this.mentionFormat = mentionFormat;
        invalidater();
    }

    public void setHashTagFormat(FormattedText hashTagFormat) {
        this.hashTagFormat = hashTagFormat;
        invalidater();
    }

    public void setGeoPosFormat(FormattedText geoPosFormat) {
        this.geoPosFormat = geoPosFormat;
        invalidater();
    }

    public void setRegexFormat(FormattedText regexFormat) {
        this.regexFormat = regexFormat;
        invalidater();
    }

    public void setSpecialCharFormat(FormattedText specialCharFormat) {
        this.specialCharFormat = specialCharFormat;
        invalidater();
    }

    public void setFormats(FormattedText emailFormat, FormattedText urlFormat, FormattedText phoneNumberFormat, FormattedText mentionFormat, FormattedText hashTagFormat, FormattedText geoPosFormat, FormattedText regexFormat) {
        this.emailFormat = emailFormat;
        this.urlFormat = urlFormat;
        this.phoneNumberFormat = phoneNumberFormat;
        this.mentionFormat = mentionFormat;
        this.hashTagFormat = hashTagFormat;
        this.geoPosFormat = geoPosFormat;
        this.regexFormat = regexFormat;
        invalidater();
    }
    /////////////////////

    public void setSmartTextCallback(SmartTextCallback mSmartTextCallback) {
        this.mSmartTextCallback = mSmartTextCallback;
        invalidater();
    }

    public void setDetectMentions(boolean detectMentions){
        this.detectMentions = detectMentions;
        invalidater();
    }

    public void setDetectHashTags(boolean detectHashTags){
        this.detectHashTags = detectHashTags;
        invalidater();
    }

    public void setDetectEmails(boolean detectEmails) {
        this.detectEmails = detectEmails;
        invalidater();
    }

    public void setDetectUrls(boolean detectUrls) {
        this.detectUrls = detectUrls;
        invalidater();
    }

    public void setDetectPhoneNumbers(boolean detectPhoneNumbers) {
        this.detectPhoneNumbers = detectPhoneNumbers;
        invalidater();
    }

    public void setDetectGeoPos(boolean detectGeoPos) {
        this.detectGeoPos = detectGeoPos;
        invalidater();
    }

    public void setDetectRegex(boolean detectRegex) {
        this.detectRegex = detectRegex;
        invalidater();
    }

    public void setDetection(boolean detectMentions, boolean detectHashTags, boolean detectEmails, boolean detectUrls, boolean detectPhoneNumbers, boolean detectGeoPos, boolean detectRegex) {
        this.detectMentions = detectMentions;
        this.detectHashTags = detectHashTags;
        this.detectEmails = detectEmails;
        this.detectUrls = detectUrls;
        this.detectPhoneNumbers = detectPhoneNumbers;
        this.detectGeoPos = detectGeoPos;
        this.detectRegex = detectRegex;
        invalidater();
    }

    public void setAutoEmailsClick(boolean autoEmailsClick) {
        this.autoEmailsClick = autoEmailsClick;
        invalidater();
    }

    public void setAutoUrlsClick(boolean autoUrlsClick) {
        this.autoUrlsClick = autoUrlsClick;
        invalidater();
    }

    public void setAutoPhoneNumbersClick(boolean autoPhoneNumbersClick) {
        this.autoPhoneNumbersClick = autoPhoneNumbersClick;
        invalidater();
    }

    public void setAutoMentionsClick(boolean autoMentionsClick) {
        this.autoMentionsClick = autoMentionsClick;
        invalidater();
    }

    public void setAutoHashTagsClick(boolean autoHashTagsClick) {
        this.autoHashTagsClick = autoHashTagsClick;
        invalidater();
    }

    public void setAutoGeoPosClick(boolean autoGeoPosClick) {
        this.autoGeoPosClick = autoGeoPosClick;
        invalidater();
    }

    public void setAutoRegexClick(boolean autoRegexClick) {
        this.autoRegexClick = autoRegexClick;
        invalidater();
    }

    public void setAutoClick(boolean autoEmailsClick, boolean autoUrlsClick, boolean autoPhoneNumbersClick, boolean autoMentionsClick, boolean autoHashTagsClick, boolean autoGeoPosClick, boolean autoRegexClick) {
        this.autoEmailsClick = autoEmailsClick;
        this.autoUrlsClick = autoUrlsClick;
        this.autoPhoneNumbersClick = autoPhoneNumbersClick;
        this.autoMentionsClick = autoMentionsClick;
        this.autoHashTagsClick = autoHashTagsClick;
        this.autoGeoPosClick = autoGeoPosClick;
        this.autoRegexClick = autoRegexClick;
        invalidater();
    }

    public FormattedText getFormattedText() {
        return formattedText;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        invalidater();
    }

    public void setRegexCaseSensitive(boolean regexCaseSensitive) {
        this.regexCaseSensitive = regexCaseSensitive;
        invalidater();
    }
}
