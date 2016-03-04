package com.example.kyle.represent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        mTextView = (TextView) findViewById(R.id.test);
        if (extras != null) {
            String zip = extras.getString("ZIP_CODE");
            mTextView.setText("Zip Code: " + zip);
        }

    }
}
