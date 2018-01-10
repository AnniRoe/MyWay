package com.example.madlo.mywaytest1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

//Dorthin wird man geleitet wenn man in der Liste der MainActivity auf einen Namen drueckt
public class DetailedView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);
        final int position = getIntent().getIntExtra("POSITION", 0);



        //TODO: KARTE IM DETAILEDVIEW ANZEIGEN

        //Holt sich die UserList bzw erstellt eine Instanz dieser
        List<Ways> waysList4 = WayData.getInstance().getWaysList();

        //Holt sich Position des users in der liste

        Ways ways = waysList4.get(position);

        //Holt sich den Namen zuerst und traegt ihn dann ins entsprechende Feld ein
        //Je fuer die einzelnen Daten
        TextView textViewTransport = (TextView) findViewById(R.id.textView_TransportField);
        textViewTransport.setText(ways.getTransport());

        TextView textViewDuration = (TextView) findViewById(R.id.textView_DurationField);
        textViewDuration.setText(ways.getDuration());


        TextView textViewDistance = (TextView) findViewById(R.id.textView_DistanceField);
        textViewDistance.setText(ways.getDistance());

        TextView textViewDate = (TextView) findViewById(R.id.textView_DateField);
        textViewDate.setText(ways.getDate());

        TextView textViewTrackNumber = (TextView) findViewById(R.id.textView_TrackNumberField);
        textViewTrackNumber.setText(ways.getTrackingNumber());

    }
}
