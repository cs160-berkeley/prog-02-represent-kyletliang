package com.example.kyle.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Kyle on 3/2/2016.
 */
public class RepCardFragment extends CardFragment{

    TextView nameView, partyView, countyView;
    Button infoButton;

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.cardfrag_rep, container, false);
        Bundle b = getArguments();

        nameView = (TextView) root.findViewById(R.id.name);
        partyView = (TextView) root.findViewById(R.id.party);
//        countyView = (TextView) root.findViewById(R.id.county);
        infoButton = (Button) root.findViewById(R.id.infoButton);


        nameView.setText(b.getString("NAME"));
        partyView.setText(b.getString("PARTY"));
//        countyView.setText(b.getString("COUNTY"));

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getActivity(), WatchToPhoneService.class);
                sendIntent.putExtra("NAME", nameView.getText());
                getActivity().startService(sendIntent);
            }
        });

        return root;
    }

}
