package com.example.kyle.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public final static String zipkey = "ZIP_CODE";
    private Button zipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

    }

    public void goToReps(View view){
        Intent intent = new Intent(this, RepsActivity.class);
        EditText editText = (EditText) findViewById(R.id.zip_code);
        String zip = editText.getText().toString();
        intent.putExtra(zipkey, zip);
        startActivity(intent);

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra(zipkey, zip);
        startService(sendIntent);

    }

    public void goToRepsFromCurrent(View view){
        Intent intent = new Intent(this, RepsActivity.class);
        intent.putExtra(zipkey, "94704");
        startActivity(intent);

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra(zipkey, "94704");
        startService(sendIntent);

    }

}
