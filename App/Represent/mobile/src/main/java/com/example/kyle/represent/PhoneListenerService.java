package com.example.kyle.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by Kyle on 3/3/2016.
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String NAME = "/send_name";
    private static final String LOC = "/send_loc";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(NAME) ) {

            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, DetailedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("NAME", value);
            intent.putExtra("PARTY", "Democratic Party");
            if (value.equals("Rep. Barbara Lee")){
                intent.putExtra("RECPIC", R.drawable.barbaralee);
            }
            else if(value.equals("Senator Barbara Boxer")){
                intent.putExtra("RECPIC", R.drawable.boxerbarbara);
            }
            else{
                intent.putExtra("RECPIC",R.drawable.dfeinstein);
            }
            Log.d("T", "about to start watch DetailedActivity with NAME");
            startActivity(intent);

        }
        else if(messageEvent.getPath().equalsIgnoreCase(LOC)){
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, RepsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra(MainActivity.zipkey, value);
            Log.d("T", "about to start watch DetailedActivity with NAME");
            startActivity(intent);

        }
        else {
            super.onMessageReceived( messageEvent );
        }

    }
}

