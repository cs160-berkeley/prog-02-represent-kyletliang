package com.example.kyle.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RepActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private String zip;
    private String info;
    private String[] infoArray, repArray, voteArray;
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
        info = intent.getStringExtra("INFO_WATCH");
        infoArray = info.split("//");
        repArray = infoArray[0].split("!");
        voteArray = infoArray[1].split("!");


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
        data[0] = new Page[repArray.length/2+1];
        data[1] = new Page[1];
        String county;
        if (voteArray[0] == null)
            county = "Unknown County (Try shaking again)";
        else
            county = voteArray[0];
        int bg;
        if(Double.parseDouble(voteArray[1])>= Double.parseDouble(voteArray[2]))
            bg = R.drawable.bluebg;
        else
            bg = R.drawable.redbg;


        for(int i = 0; i < repArray.length/2; i+=1){
            if (repArray[2*i+1].equals("D")) {
                data[0][i+1] = new Page(repArray[2 * i], "Democrat", county, R.drawable.bluebg, true);
            }
            else if(repArray[2*i+1].equals("R")){
                data[0][i+1] = new Page(repArray[2 * i],"Republican", county, R.drawable.redbg, true);
            }
            else{
                data[0][i+1] = new Page(repArray[2 * i],"Independent", county, R.drawable.indep, true);
            }
        }
        data[0][0] = new Page(voteArray[1], voteArray[2], county, bg, false);
        data[1][0] = new Page(voteArray[1], voteArray[2], county, bg, false);
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
                int range = (43583 - 2) + 1;
                int rand = (int)(Math.random() * range) + 1; //randomly generated number to be zip

                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                sendIntent.putExtra("RAND", Integer.toString(rand));
                startService(sendIntent);
            }
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public static class Page {
        String rep;
        String county;
        String party;
        String obamaVote;
        String romneyVote;
        int mImageResource;

        public Page(String s1, String s2, String c, int im, boolean isRep){
            if(isRep) {
                rep = s1;
                party = s2;
            }
            else{
                obamaVote = s1;
                romneyVote = s2;
            }
            mImageResource = im;
            county = c;
        }


    }


}
