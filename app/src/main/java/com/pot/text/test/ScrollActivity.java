package com.pot.text.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pot.text.R;

import api.pot.text.xtv.tools.ScrollTextView;

public class ScrollActivity extends AppCompatActivity {

    ScrollTextView scrolltext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);


        scrolltext = (ScrollTextView) findViewById(R.id.scrolltext);

        findViewById(R.id.tong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrolltext.tongle();
                if(scrolltext.isPaused()) scrolltext.setSingleLine(false);
            }
        });

    }

}
