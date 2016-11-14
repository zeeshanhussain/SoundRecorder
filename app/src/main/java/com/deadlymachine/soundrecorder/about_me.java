package com.deadlymachine.soundrecorder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

public class about_me extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me);
        Button a = (Button) findViewById(R.id.facebook);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.facebook.com/sanyam.53jain"));
                startActivity(m);
            }
        });
        Button b = (Button) findViewById(R.id.google);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent z = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://plus.google.com/+SanyamJain007"));
                startActivity(z);
            }

        });
        Button c = (Button) findViewById(R.id.twitter);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent y = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://twitter.com/jain007sanyam"));
                startActivity(y);
            }
        });
        CardView d = (CardView) findViewById(R.id.xda);
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent g = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://forum.xda-developers.com/member.php?u=5226068"));
                startActivity(g);
            }
        });
    }
}
