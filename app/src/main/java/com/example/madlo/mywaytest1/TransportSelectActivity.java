package com.example.madlo.mywaytest1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class TransportSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_select);

        final Context context = getApplicationContext();

        Button carButton = (Button) findViewById(R.id.button_car);
        Button bikeButton = (Button) findViewById(R.id.button_bike);
        Button walkButton = (Button) findViewById(R.id.button_walk);
        Button publicTransportButton = (Button) findViewById(R.id.button_public_transport);

        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NavigationDrawer.class);
                intent.putExtra("transport", "Mit dem Auto");
                startActivity(intent);
            }
        });

        bikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NavigationDrawer.class);
                intent.putExtra("transport", "Mit dem Fahrrad");
                startActivity(intent);
            }
        });

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NavigationDrawer.class);
                intent.putExtra("transport", "Zu Fuß");
                startActivity(intent);
            }
        });

        publicTransportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NavigationDrawer.class);
                intent.putExtra("transport", "Mit öffentlichen Verkehrsmitteln");
                startActivity(intent);
            }
        });
    }
}
