package com.example.kyle.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RepActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private String zip;
    private Page[][] data = new Page[2][];
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private final float NOISE = (float) 2.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep);

        Intent intent = getIntent();
        zip = intent.getStringExtra("ZIP_CODE");

        setupPageInfo();

        final DotsPageIndicator mPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setAdapter(new SampleGridPagerAdapter(this, data, getFragmentManager()));
        mPageIndicator.setPager(pager);


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }



    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setupPageInfo(){
        data[0] = new Page[3];
        data[1] = new Page[1];

        if (zip == null){
            zip = "0";
        }

        data[0][0] = new Page("Rep. Barbara Lee", zip, R.drawable.bluebg);
        data[0][1] = new Page("Senator Barbara Boxer", zip, R.drawable.bluebg);
        data[0][2] = new Page("Senator Diana Steinfeld", zip, R.drawable.bluebg);
        data[1][0] = new Page("Barbara Boxer", zip, R.drawable.bluebg);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE) deltaX = (float) 0.0;
            if (deltaY < NOISE) deltaY = (float) 0.0;
            if (deltaZ < NOISE) deltaZ = (float) 0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            if ((deltaX + deltaY + deltaZ) > (float) 50){
                int range = (99999 - 10000) + 1;
                int zip = (int)(Math.random() * range) + 10000; //randomly generated number to be zip

                Intent intent = new Intent(this, RepActivity.class);
                intent.putExtra("ZIP_CODE", Integer.toString(zip));
                startActivity(intent);

                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("LOC", Integer.toString(zip));
                startService(sendIntent);


                //Log.d("T", "ay lmao");
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public static class Page {
        private String rep;
        private String zipCode;
        private String party;
        private int mImageResource;

        public Page(String r, String z, int im){
            rep = r;
            zipCode = z;
            mImageResource = im;
            party = "Democrat";
        }

        public String getRep(){
            return rep;
        }
        public String getZipCode(){
            return zipCode;
        }
        public String getParty(){
            return party;
        }
        public int getmImageResource(){
            return mImageResource;
        }
    }

}
