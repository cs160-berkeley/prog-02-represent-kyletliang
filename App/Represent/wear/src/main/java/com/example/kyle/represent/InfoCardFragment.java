package com.example.kyle.represent;

/**
 * Created by Kyle on 3/13/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by Kyle on 3/2/2016.
 */
public class InfoCardFragment extends CardFragment {

    TextView nameView, partyView, countyView;
    Button infoButton;

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.cardfrag_info, container, false);
        Bundle b = getArguments();

        countyView = (TextView) root.findViewById(R.id.county);
        countyView.setText(b.getString("COUNTY"));
        return root;
    }

}
