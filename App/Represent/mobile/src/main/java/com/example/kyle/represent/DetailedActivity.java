package com.example.kyle.represent;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailedActivity extends AppCompatActivity {
    private final String sunlight = "https://congress.api.sunlightfoundation.com";
    private final String sunlightKey = "&apikey=a4ff07863f84459680855ca8c839ef7c";
    private String[] bills = new String[4];
    private String[] comms = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detailed);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        RepsActivity.CongressPerson person = RepsActivity.personsMap.get(name);

        String bioguideID = person.id;
        String billsURL = sunlight + "/bills?sponsor_id=" + bioguideID + sunlightKey;
        String commURL = sunlight + "/committees?member_ids=" + bioguideID + sunlightKey;

        ImageView pic = (ImageView) findViewById(R.id.repPic);
        TextView nameView = (TextView) findViewById(R.id.name);
        TextView party = (TextView) findViewById(R.id.party);
        TextView endTerm = (TextView) findViewById(R.id.end_term);
        Picasso.with(this).load("http://theunitedstates.io/images/congress/225x275/"
                + bioguideID + ".jpg").into(pic);
        nameView.setText(person.name);
        party.setText(person.party);
        endTerm.setText("End of Term: " + person.endTerm);


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            ComTask cTask = new ComTask();
            BillTask bTask = new BillTask();
            try {
                cTask.execute(commURL);
                bTask.execute(billsURL);
                //stalling until data is available
            }
            catch (Exception e){
                Log.e("ERROR", e.getMessage(), e);
                return;
            }
//            new DownloadWebpageTask().execute(url);
        } else {
        }
    }

    private class ComTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject derp = new JSONObject(result);
                JSONArray jsonArray = derp.optJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    if(i == 4)
                        break;
                    JSONObject merp = jsonArray.getJSONObject(i);
                    comms[i] = merp.getString("name");
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return;
            }
            TextView com = (TextView) findViewById(R.id.com1);
            com.setText(comms[0]);
            com = (TextView) findViewById(R.id.com2);
            com.setText(comms[1]);
            com = (TextView) findViewById(R.id.com3);
            com.setText(comms[2]);
            com = (TextView) findViewById(R.id.com4);
            com.setText(comms[3]);
        }

        private String downloadUrl(String myurl) throws IOException {
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    conn.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }
    }
    private class BillTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject derp = new JSONObject(result);
                JSONArray jsonArray = derp.optJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    if(i == 4)
                        break;
                    JSONObject merp = jsonArray.getJSONObject(i);
                    if(merp.getString("short_title")!=null && !merp.getString("short_title").equals("null"))
                        bills[i] = merp.getString("short_title") + " (Introduced on " + merp.getString("introduced_on") + ")";
                    else
                        bills[i] = merp.getString("official_title") + " (Introduced on " + merp.getString("introduced_on") + ")";
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return;
            }
            TextView bill = (TextView) findViewById(R.id.bill1);
            bill.setText(bills[0]);
            bill = (TextView) findViewById(R.id.bill2);
            bill.setText(bills[1]);
            bill = (TextView) findViewById(R.id.bill3);
            bill.setText(bills[2]);
            bill = (TextView) findViewById(R.id.bill4);
            bill.setText(bills[3]);
        }

        private String downloadUrl(String myurl) throws IOException {
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    conn.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }
    }

}
