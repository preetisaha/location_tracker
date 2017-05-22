package com.uwrf.locationapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class DisplayActivity extends AppCompatActivity {
    private TextView emailView;
    private TextView latitudeView;
    private TextView longitudeView;
    private TextView timestampView;
    private Button doneButton;

    private static final String emailText = "Email: ";
    private static final String latitudeText = "Latitude: ";
    private static final String longitudeText = "Longitude: ";
    private static final String timestampText = "Timestamp: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        emailView = (TextView) findViewById(R.id.email_field);
        latitudeView = (TextView) findViewById(R.id.latitude_field);
        longitudeView = (TextView) findViewById(R.id.longitude_field);
        timestampView = (TextView) findViewById(R.id.timestamp_field);

        doneButton = (Button) findViewById(R.id.done_button);

        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        emailView.setText(emailText + prefs.getString("email", ""));
        latitudeView.setText(latitudeText + prefs.getString("latitude", ""));
        longitudeView.setText(longitudeText + prefs.getString("longitude", ""));
        timestampView.setText(timestampText + (new Date( Long.parseLong(prefs.getString("timestamp", "0")) )).toString() );

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

    }

    private void done() {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        prefs.edit().remove("email");
        prefs.edit().remove("latitude");
        prefs.edit().remove("longitude");
        prefs.edit().remove("timestamp");
        emailView.setText("");
        latitudeView.setText("");
        longitudeView.setText("");
        timestampView.setText("");
        this.finish();
    }
}
