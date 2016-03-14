package com.example.kyle.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Kyle on 3/11/2016.
 */
public class RepAdapter extends RecyclerView.Adapter<RepAdapter.RepViewHolder> {

    private List<RepsActivity.CongressPerson> persons;
    private Context context;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "	WHSFmbzqE3QCgB4uwR0oVxsFs";
    private static final String TWITTER_SECRET = "jcVYHRarn9a3KLvz1kxPi8iYpPfOuFQmGzYVsrSuf4PKj4OfSz ";
    public Tweet repTweetMsg;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class RepViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cv;
        public TextView repName, repSite, repEmail, repTweet, repParty;
        public Button infoButton;
        public ImageView repPic;



        public RepViewHolder(View v) {
            super(v);
            context = v.getContext();
            cv = (CardView) itemView.findViewById(R.id.cv);
            repName = (TextView) v.findViewById(R.id.rep_name);
            repSite = (TextView) v.findViewById(R.id.rep_website);
            repEmail = (TextView) v.findViewById(R.id.rep_email);
            repParty = (TextView) v.findViewById(R.id.rep_party);
            infoButton = (Button) v.findViewById(R.id.infoButton);
            repTweet = (TextView) v.findViewById(R.id.rep_tweet);
            repPic = (ImageView) v.findViewById((R.id.rep_pic));

        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public RepAdapter(List<RepsActivity.CongressPerson> myDataset, Context context) {
        persons = myDataset;
        this.context = context;
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RepViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.repcard, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RepViewHolder vh = new RepViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RepViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String name = persons.get(position).name;
        final String twitterHandle = persons.get(position).twitter;
        final TextView tweet = holder.repTweet;
        String party = persons.get(position).party;

        holder.repName.setText(persons.get(position).name);
        holder.repSite.setText(persons.get(position).website);
        holder.repEmail.setText(persons.get(position).email);
        holder.repParty.setText(persons.get(position).party);

        if(party.equals("Democrat")){
            holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.demoBlue));
            //non-deprecated method is API 23

        }
        else if(party.equals("Republican")){
            holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.repubRed));
        }
        else{
            holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.indep));
        }
        Picasso.with(context).load("http://theunitedstates.io/images/congress/225x275/"
                + persons.get(position).id + ".jpg").resize(100,125).into(holder.repPic);
        Log.d("T", "twitter" + twitterHandle);


//        Bitmap bitmap = ((BitmapDrawable)holder.repPic.getDrawable()).getBitmap();
//        Asset asset = createAssetFromBitmap(bitmap);
//        PutDataMapRequest dataMap = PutDataMapRequest.create("/image");
//        dataMap.getDataMap().putAsset("profileImage", asset);
//        PutDataRequest request = dataMap.asPutDataRequest();
//        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
//                .putDataItem(mGoogleApiClient, request);


        RepsActivity.twitterApiClient.getStatusesService().userTimeline(null, twitterHandle, null, 1l, null, null, null, null, null, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> listResult) {
                repTweetMsg = listResult.data.get(0);
                tweet.setText("@" + twitterHandle + ": " + repTweetMsg.text);

            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });


        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("T", "clicked button");
                Intent sendIntent = new Intent(v.getContext(), DetailedActivity.class);
                sendIntent.putExtra("NAME", name);
                v.getContext().startActivity(sendIntent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return persons.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
