package com.example.kyle.represent;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RepsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_reps);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String zip = intent.getStringExtra(MainActivity.zipkey);
        TextView loc = (TextView) findViewById(R.id.loc);
        loc.setText("Zip Code: " + zip);

    }
    public void goToDetailed1(View view) {
        Intent intent = new Intent(this, DetailedActivity.class);
        ImageView repPic = (ImageView) findViewById(R.id.rep1pic);
        intent.putExtra("RECPIC", R.drawable.barbaralee);
        intent.putExtra("NAME", "Rep. Barbara Lee");
        intent.putExtra("PARTY", "Democratic Party");
        startActivity(intent);
    }
    public void goToDetailed2(View view) {
        Intent intent = new Intent(this, DetailedActivity.class);
        ImageView repPic = (ImageView) findViewById(R.id.rep2pic);
        intent.putExtra("RECPIC", R.drawable.boxerbarbara);
        intent.putExtra("NAME", "Senator Barbara Boxer");
        intent.putExtra("PARTY", "Democratic Party");
        startActivity(intent);
    }
    public void goToDetailed3(View view) {
        Intent intent = new Intent(this, DetailedActivity.class);
        ImageView repPic = (ImageView) findViewById(R.id.rep3pic);
        intent.putExtra("RECPIC", R.drawable.dfeinstein);
        intent.putExtra("NAME", "Senator Diane Feinstein");
        intent.putExtra("PARTY", "Democratic Party");
        startActivity(intent);
    }

}