package com.example.kyle.represent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.internal.TwitterApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class RepsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private TextView loc;
    private List<CongressPerson> persons;
    private final String sunlight = "https://congress.api.sunlightfoundation.com";
    private final String sunlightKey = "&apikey=a4ff07863f84459680855ca8c839ef7c";
    private final String geocode = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public String geoURL, sunURL;
    public Bundle bundleForWatch;
    public String lat;
    public String longi;
    public static TwitterApiClient twitterApiClient;

    public static HashMap<String, CongressPerson> personsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        persons = new ArrayList<>();
        bundleForWatch = new Bundle();
//        setContentView(R.layout.content_reps);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        twitterApiClient = TwitterCore.getInstance().getApiClient();

        Intent intent = getIntent();
        String zip = intent.getStringExtra(MainActivity.zipkey);
        longi = intent.getStringExtra("LONGI");
        lat = intent.getStringExtra("LAT");
        String rand = intent.getStringExtra("RAND");
        if (rand != null){
            try {
                InputStream stream = getAssets().open("us_postal_codes.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                int i = 0;
                String line = br.readLine();
                while (i < Integer.parseInt(rand) && line != null){
                    i++;
                    line = br.readLine();
                }
                String[] randLine = line.split(",");
                lat = randLine[5];
                longi = randLine[6];
            }
            catch(Exception e){
                Log.e("ERROR", e.getMessage(), e);
                return;
            }

        }
        if (zip == null){
            sunURL = sunlight + "/legislators/locate?latitude=" + lat + "&longitude="
                    + longi + sunlightKey;
            geoURL = geocode + lat + "," + longi + "&key=" + "AIzaSyBGWwWhYfxv4WdH5ZQPIckJDCya2RZDoU4"; //insert key manually
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                GeoTask gTask = new GeoTask(getBaseContext());
                try {
                    String x2 = gTask.execute(geoURL).get();
                    //stalling until data is available
                }
                catch (Exception e){
                    Log.e("ERROR", e.getMessage(), e);
                    return;
                }
            } else {
                    loc.setText("No network connection available.");
            }
        }
        else{
            sunURL = sunlight + "/legislators/locate?zip=" + zip + sunlightKey;
            String zipToLatURL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zip + "&region=us";
            Log.d("T", "URL=" + zipToLatURL);
            // TODO: broken concurrency issues
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    String blah = new ZipToCordTask().execute(zipToLatURL).get();
                    //stalling until data is available
                }
                catch (Exception e){
                    Log.e("ERROR", e.getMessage(), e);
                    return;
                }
            } else {
                loc.setText("No network connection available.");
            }
        }

        loc = (TextView) findViewById(R.id.loc);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            RepTask rTask = new RepTask(getBaseContext());
            try {
                String result = rTask.execute(sunURL).get();
                //stalling until data is available
            }
            catch (Exception e){
                Log.e("ERROR", e.getMessage(), e);
                return;
            }
//            new DownloadWebpageTask().execute(url);
        } else {
            loc.setText("No network connection available.");
        }

        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RepAdapter adapter = new RepAdapter(persons, getBaseContext());
        rv.setAdapter(adapter);

    }

    public class CongressPerson {
        String name, id, party, email, website, twitter, endTerm, title;
        int photoId;

        CongressPerson(String name, String email, String website, String party,
                       String twitter, String id, String endTerm, String title) {
//            this.name = name;
            this.email = email;
            this.website = website;
            if (party.equals("D")){
                this.party = "Democrat";
            }
            else if(party.equals("I")){
                this.party = "Independent";
            }
            else{
                this.party = "Republican";
            }
            this.twitter = twitter;
            this.id = id;
            this.endTerm = endTerm;
            if(title.equals("Sen")){
                this.title = "Senator";
            }
            else{
                this.title = "Rep.";
            }
            this.name = this.title + " " + name;
        }

    }

    private class RepTask extends AsyncTask<String, Void, String> {
        private Context context;

        private RepTask(Context context) {
            this.context = context;
        }
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
            String reps = "";
            try {
                JSONObject derp = new JSONObject(result);
                JSONArray jsonArray = derp.optJSONArray("results");
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String name = obj.getString("first_name") + " " + obj.getString("last_name");
                    String email = obj.getString("oc_email");
                    String website = obj.getString("website");
                    String party = obj.getString("party");
                    String twitter = obj.getString("twitter_id");
                    String id = obj.getString("bioguide_id");
                    String endTerm = obj.getString("term_end");
                    String title = obj.getString("title");
                    CongressPerson p = new CongressPerson(name,email,website, party,
                            twitter, id, endTerm, title);
                    persons.add(p);
                    personsMap.put(p.name, p);
                    reps += (p.name + "!");
                    reps += (party + "!");
                }
                bundleForWatch.putString("reps", reps.substring(0,reps.length()-1));
                if (bundleForWatch.getString("countyInfo")!=null) {
                    String infoForWatch = "";
                    infoForWatch += bundleForWatch.getString("reps");
                    infoForWatch += "//" + bundleForWatch.getString("countyInfo");
                    Intent sendIntent = new Intent(context, PhoneToWatchService.class);
                    sendIntent.putExtra("INFO_WATCH", infoForWatch);
                    Log.d("T", "info is " + infoForWatch);
                    startService(sendIntent);
                }

            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return;
            }
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
                }
                finally{
                    conn.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }
    }

    private class GeoTask extends AsyncTask<String, Void, String> {
        private Context context;

        private GeoTask(Context context) {
            this.context = context;
        }
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
                String county = "";
                String state  = "";
                JSONObject derp = new JSONObject(result);
                JSONArray jsonArray = derp.optJSONArray("results");
                JSONObject obj = jsonArray.getJSONObject(0);
                JSONArray jsonArray2 = obj.optJSONArray("address_components");
                for(int i=0; i < jsonArray2.length(); i++) {
                    if (!county.equals("") && !state.equals(""))
                        break;
                    JSONObject x = jsonArray2.getJSONObject(i);
                    JSONArray types = x.optJSONArray("types");
                    for(int j=0; j < types.length(); j++){
                        String type = types.optString(j);
                        if(type.equals("administrative_area_level_2")){
                            county = x.getString("short_name");
                            if(!state.equals(""))
                                break;
                        }
                        if(type.equals("administrative_area_level_1")){
                            state = x.getString("short_name");
                            if(!county.equals(""))
                                break;
                        }
                    }
                }
                String key = county + ", " + state;
                InputStream stream = getAssets().open("newelectioncounty2012.json");
                int size = stream.available();
                byte[] buffer = new byte[size];
                stream.read(buffer);
                stream.close();
                String jsonString = new String(buffer, "UTF-8");
                Log.d("T", key);
                JSONObject countyVote = new JSONObject(jsonString);
                JSONObject votes = countyVote.optJSONObject(key);
                String obama = votes.getString("obama");
                String romney = votes.getString("romney");
                bundleForWatch.putString("countyInfo", key + "!" + obama + "!" + romney);
                if (key == null){
                    loc.setText("Unknown County: Try Again");
                }
                else {
                    loc.setText(key);
                }
                if (bundleForWatch.getString("reps")!=null) {
                    String infoForWatch = "";
                    infoForWatch += bundleForWatch.getString("reps");
                    infoForWatch += "//" + bundleForWatch.getString("countyInfo");
                    Intent sendIntent = new Intent(context, PhoneToWatchService.class);
                    sendIntent.putExtra("INFO_WATCH", infoForWatch);
                    Log.d("T", "info is " + infoForWatch);
                    startService(sendIntent);
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return;
            }
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
                }
                finally{
                    conn.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }
    }

    private class ZipToCordTask extends AsyncTask<String, Void, String> {
        private Context context;

        private ZipToCordTask() {
        }
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
                JSONObject obj = jsonArray.getJSONObject(0);
                JSONObject geo = obj.getJSONObject("geometry");
                JSONObject loc = geo.getJSONObject("location");
                lat = "" + loc.getDouble("lat");
                longi = "" + loc.getDouble("lng");
                geoURL = geocode + lat + "," + longi + "&key=" + "AIzaSyBGWwWhYfxv4WdH5ZQPIckJDCya2RZDoU4";
                Log.d("T", "Long: " + longi + ",Lat: " + lat);
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    GeoTask gTask = new GeoTask(getBaseContext());
                    try {
                        String x2 = gTask.execute(geoURL).get();
                        //stalling until data is available
                    }
                    catch (Exception e){
                        Log.e("ERROR", e.getMessage(), e);
                        return;
                    }
                } else {
//                    loc.setText("No network connection available.");
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return;
            }
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
                }
                finally{
                    conn.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {}

    public void onConnected(Bundle bundle) {}



}