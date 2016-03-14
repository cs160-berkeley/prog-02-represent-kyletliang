package com.example.kyle.represent;

/**
 * Created by Kyle on 3/3/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Kyle on 3/2/2016.
 */
public class VoteCardFragment extends CardFragment {

    TextView romneyView, obamaView;

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.cardfrag_vote, container, false);
        Bundle b = getArguments();
        TextView countyView = (TextView) root.findViewById(R.id.county);
        TextView obamaView = (TextView) root.findViewById(R.id.obamaVote);
        TextView romneyView = (TextView) root.findViewById(R.id.romneyVote);
        countyView.setText(b.getString("COUNTY"));
        obamaView.setText("Obama: " + b.getString("OBAMA") + "%");
        romneyView.setText("Romney: " + b.getString("ROMNEY")+ "%");

        return root;
    }

}

