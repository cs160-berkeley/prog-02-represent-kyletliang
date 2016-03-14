package com.example.kyle.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kyle on 3/2/2016.
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    private static final String INFO_WATCH = "/INFO_WATCH";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases

        if (messageEvent.getPath().equalsIgnoreCase(INFO_WATCH)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = getApplicationContext().getPackageManager()
                    .getLaunchIntentForPackage(getApplicationContext().getPackageName() );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //you need to add this flag since you're starting a new activity from a service
            intent.putExtra("INFO_WATCH", value);
            Log.d("T", "about to start watch RepActivity");
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }

    }

//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        for (DataEvent event : dataEvents) {
//            if (event.getType() == DataEvent.TYPE_CHANGED &&
//                    event.getDataItem().getUri().getPath().equals("/image")) {
//                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
//                Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
//                Bitmap bitmap = loadBitmapFromAsset(profileAsset);
//                // Do something with the bitmap
//            }
//        }
//    }
//    public Bitmap loadBitmapFromAsset(Asset asset) {
//        if (asset == null) {
//            throw new IllegalArgumentException("Asset must be non-null");
//        }
//        ConnectionResult result =
//                mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
//        if (!result.isSuccess()) {
//            return null;
//        }
//        // convert asset into a file descriptor and block until it's ready
//        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
//                mGoogleApiClient, asset).await().getInputStream();
//        mGoogleApiClient.disconnect();
//
//        if (assetInputStream == null) {
//            Log.w(TAG, "Requested an unknown Asset.");
//            return null;
//        }
//        // decode the stream into a bitmap
//        return BitmapFactory.decodeStream(assetInputStream);
//    }
}

