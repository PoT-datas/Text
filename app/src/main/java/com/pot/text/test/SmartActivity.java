package com.pot.text.test;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.pot.text.R;

import api.pot.text.xtv.tools.SmartTextCallback;
import api.pot.text.xtv.tools.SmartTextView;

public class SmartActivity extends AppCompatActivity {

    SmartTextView mSmartTextView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart);

        context = this;

        findViewById(R.id.tong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSmartTextView.tongle();
                if(mSmartTextView.isPaused()) mSmartTextView.setSingleLine(false);
            }
        });

        final String sampleText = getString(R.string.sample_text);
        mSmartTextView = (SmartTextView) findViewById(R.id.smarTextView);
        mSmartTextView.setEmailColorCode("#3cb371");
        mSmartTextView.setPhoneNumberColorCode("#ff33aa");
        mSmartTextView.setHashTagColorCode("#f37735");
        mSmartTextView.setUrlColorCode("#ffc425");
        mSmartTextView.setMentionColorCode("#57b884");
        mSmartTextView.setDetection(true, true, true, true, true, true, true);
        mSmartTextView.setText(sampleText);
        mSmartTextView.setSmartTextCallback(new SmartTextCallback(){

            @Override
            public void emailClick(String email) {
                // Define your own email intent. If you don't use the SmartTextCallback, library will use the
                // inbuilt ones.
                Toast.makeText(context, "email: "+email, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "653379379 "+ Patterns.PHONE.matcher("653379379").matches(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void hashTagClick(String hashTag) {
                // Do something with the hash tags
                Toast.makeText(context, "hashTag: "+hashTag, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void webUrlClick(String webUrl) {
                // Define what happens on url click. Library has it own default if you don't implement SmartTextCallback.
                Toast.makeText(context, "webUrl: "+webUrl, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void phoneNumberClick(String phoneNumber) {
                // Do something with the phone number. Library has it own default if you don't implement SmartTextCallback.
                Toast.makeText(context, "phoneNumber: "+phoneNumber, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void mentionClick(String mention) {
                // Do something with the mention clicks
                Toast.makeText(context, "mention: "+mention, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void geoPosClick(double lat, double lng) {
                // Do something with the geographic position clicks
                Toast.makeText(context, "lat: "+lat+"/lng: "+lng, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void regexClick(String value) {
                // Do something with the value of a regex
                Toast.makeText(context, "regex: "+value, Toast.LENGTH_SHORT).show();
            }
        });
        mSmartTextView.startScroll();

    }

}
