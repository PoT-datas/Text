package com.pot.text.test;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pot.text.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import api.pot.text.xtv.tools.ScrollTextView;

public class ScrollActivity extends AppCompatActivity {

    ScrollTextView scrolltext;
    RelativeLayout bg;

    List<Integer> res = new ArrayList<>();

    Random rnd;

    private boolean shortT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        res.add(R.drawable.bg1);
        res.add(R.drawable.bg2);
        res.add(R.drawable.bg3);
        res.add(R.drawable.bg4);

        rnd = new Random();

        scrolltext = (ScrollTextView) findViewById(R.id.scrolltext);
        bg = (RelativeLayout) findViewById(R.id.bg);

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bg.setBackgroundColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                scrolltext.invalidate();
            }
        });
        scrolltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrolltext.tongleCutOnLongSingle();
            }
        });
        findViewById(R.id.tong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrolltext.setText(shortT?"je suis jesus. L'unique fils de Dieu.":"je suis jesus.");
                shortT = !shortT;
            }
        });

        scrolltext.useAutoColor(bg, Color.RED, Color.GREEN, Color.BLUE);
    }

}
