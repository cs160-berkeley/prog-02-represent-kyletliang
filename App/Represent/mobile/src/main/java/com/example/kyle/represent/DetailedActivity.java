package com.example.kyle.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detailed);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int repPicId= intent.getIntExtra("RECPIC", 0);
        String name= intent.getStringExtra("NAME");
        String party = intent.getStringExtra("PARTY");

        ImageView pic = (ImageView) findViewById(R.id.repPic);
        TextView n = (TextView) findViewById(R.id.name);
        TextView p = (TextView) findViewById(R.id.party);
        pic.setImageResource(repPicId);
        n.setText(name);
        p.setText(party);


    }

}
