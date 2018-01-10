package com.example.madlo.mywaytest1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 222;
    public static final float DEFAULT_ZOOM_LEVEL = 17.0f;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG2 = NavigationDrawer.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Timer timer;
    private TimerTask timerTask;
    final Handler polylineHandler = new Handler();

    private TextView resultTextView;
    private View startTrackingButton;
    private View stopTrackingButton;
    private View saveTrackingButton;
    private View showTrackingListButton;


    private List<LatLng> LatLngPosition = new ArrayList<>();
    private boolean trackingActive = false;
    private Polyline currentPolyLine;
    private long trackingStartTime;
    private long trackingEndTime;
    private String transportChoice = "";
    private String convertedTime;
    private double distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Toast der auf Standortfreigabe hinweist
        final Context context = getApplicationContext();
        CharSequence text = "Bitte stellen Sie sicher, dass Sie die Standortfreigabe für diese App unter 'Einstellungen' > 'Apps' aktiviert haben";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        resultTextView = (TextView) findViewById(R.id.text_view_tracking_result);


        startTrackingButton = findViewById(R.id.button_start_tracking);
        stopTrackingButton = findViewById(R.id.button_stop_tracking);
        saveTrackingButton = findViewById(R.id.button_Save);
        showTrackingListButton = findViewById(R.id.button_showList);


        //Start Tracking Button ist nur sichtbar wenn nicht gerade getrackt wird
        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, TransportSelectActivity.class);
                startActivity(intent);
            }
        });


        //Stop Tracking Button ist nur sichtbar wenn gerade getrackt wird
        stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopTracking();
                startTrackingButton.setVisibility(View.VISIBLE);
                stopTrackingButton.setVisibility(View.GONE);
                resultTextView.setVisibility(View.VISIBLE);
                saveTrackingButton.setVisibility(View.VISIBLE);

            }
        });
//TODO:SPEICHERN MIT DIALOG
        //SaveTrackingButton speichert das letzte Tracking nur nach beenden eines Trackings sichtbar
        saveTrackingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TrackingEnded();

            }
        });

        showTrackingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigationDrawer.this, ListActivity.class);
                startActivity(intent);

            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Wird benutzt um regelmäßig die letzte bekannte Location abzufragen (siehe:
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }


    private void startTracking() {
        trackingActive = true;

        startTrackingButton.setVisibility(View.GONE);
        stopTrackingButton.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.GONE);
        saveTrackingButton.setVisibility(View.GONE);

        //Toast für Hinweis dass Tracking aktiv ist
        Context context = getApplicationContext();
        CharSequence text = "Tracking ist aktiv";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


        trackingStartTime = System.currentTimeMillis();

        // Altes Tracking entfernen
        if (currentPolyLine != null) {
            currentPolyLine.remove();
        }
        LatLngPosition.clear();


        PolylineOptions options = new PolylineOptions()
                .width(5)
                .color(Color.RED);

        currentPolyLine = mMap.addPolyline(options);
        // neuer timer (ist für die zeitsteuerung zuständig
        timer = new Timer();

        //timer task starten --- timertask sagt WAS es alle 5 Sekunden tun soll
        timerTask = new TimerTask() {
            public void run() {
                updatePolyLine();
            }
        };

        //nach 0,5s starten und alle 5 sekunden abfragen
        timer.schedule(timerTask, 500, 5000); //
    }


    private void updatePolyLine() {
        polylineHandler.post(polylineRunnable);
    }

    @SuppressLint("MissingPermissions")
    // internet: http://www.trivisonno.com/programming/update-android-gui-timer
    final Runnable polylineRunnable = new Runnable() {
        @Override
        public void run() {
            // aktuelle location holen
            // Rot-Unterstrichenes geht trotzdem
            //TODO: VOR ABGABE ENTFERNEN
            @SuppressLint("MissingPermission")
            Task task = mFusedLocationClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Neue location einfügen
                        handleNewLocation(location);
                    }
                }
            });
            // Polyline neu zeichnen
            currentPolyLine.setPoints(LatLngPosition);
        }
    };

    private void stopTracking() {
        // Deaktiviert weiteres Vermerken von Daten in Liste
        trackingActive = false;
        //Toast zeigt Nachricht wenn Tracking deaktiviert wurde
        Context context = getApplicationContext();
        CharSequence text = "Tracking deaktiviert";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


        trackingEndTime = System.currentTimeMillis();
        // Stop the timer
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask.cancel();
        }
        polylineHandler.removeCallbacks(polylineRunnable);

        long trackingDuration = trackingEndTime - trackingStartTime;
        // MS in xx min und xx sec umwandeln
        //String oben initialisiert und hier vor converted String entfernt
        convertedTime = String.format(Locale.GERMAN, "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(trackingDuration),
                TimeUnit.MILLISECONDS.toSeconds(trackingDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackingDuration))
        );
        String durationText = "Dauer: " + convertedTime;

        // Streckenberechnung
        distance = calculateTrackedDistance();

        String distanceString = "Distanz (Meter): " + String.format(Locale.GERMAN, "%.2f", distance);

        String resultViewText = this.transportChoice + "\n" + durationText + "\n" + distanceString;

        this.transportChoice = ""; // Alte transportChoice wieder entfernen

        //Zeigt eine Auswertung der getrackten Strecke
        resultTextView.setText(resultViewText);

        //Marker an Anfang und Ende setzen
        LatLng startPosition = LatLngPosition.get(0);
        LatLng endPosition = LatLngPosition.get(LatLngPosition.size() - 1);


        mMap.addMarker(new MarkerOptions()
                .position(startPosition)
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        );
        mMap.addMarker(new MarkerOptions()
                .position(endPosition)
                .title("Ende")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        );
    }

    private double calculateTrackedDistance() {
        double distance = 0.0;
        for (int i = 0; i < LatLngPosition.size() - 1; i++) {
            LatLng current = LatLngPosition.get(i);
            LatLng nextPosition = LatLngPosition.get(i + 1);
            distance = distance + SphericalUtil.computeDistanceBetween(current, nextPosition);
        }

        return distance;

    }


    //Funktioniert nicht, muss umgangen werden und manuell unter Einstellungen
    //Apps erlaubt werden für die App
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "This app requires permission to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }

            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }


    // Müsste für den "settings"-Button sein, oben rechts
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //TODO: Toast mit Hinweis dass Navigation-Drawer-Symbol nicht geht erscheint nicht
        // Wenn der Drawer wieder funktioniert, die folgenden Zeilen wieder aufnehmen

//        if (id == R.id.nav_position) {
//
//        } else if (id == R.id.nav_means_of_transport) {
//
//        } else if (id == R.id.nav_tracks) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

        // Übergebene Daten, die vom Intent kommen
        // Hier werden nur Daten von "TransportSelectActivity" angenommen wegen Schlagwort transport
        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            String transportChoice = parameters.getString("transport");
            if (transportChoice != null && !transportChoice.isEmpty()) {
                this.transportChoice = transportChoice;
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        //Team Treehouse
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    //Team Treehouse
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    //Team Treehouse
    private void handleNewLocation(Location location) {
        Log.d(TAG2, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //Wenn Tracking aktiv ist oder die Länge der Positionsliste 0 ist fuege die neue Location hinzu
        if (trackingActive || LatLngPosition.size() == 0) {
            addLatLngtoList(latLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL));
    }


    public void addLatLngtoList(LatLng latLng) {
        LatLngPosition.add(latLng);
    }


    //Team Treehouse
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG2, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Team Treehouse
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG2, "Location services suspended. Please reconnect. ");
    }

    //Team Treehouse
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG2, "Location services connection failed with code " + connectionResult.getErrorMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Erst hier wird das Tracking gestartet (wir müssen auf die Google-Map warten, sonst bekommen wir einen Fehler)
        if (!transportChoice.isEmpty()) {
            startTracking();
        }
    }

    public void TrackingEnded() {

        //Auslesen der Werte aus den Widgets
        //AUSSER TRANSPORTMITTEL DAS STEHT IN transportChoice


        String distanceCast = String.format(Locale.GERMAN, "%.2f", distance);


        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN).format(new Date());

        // Tracknummer basteln
        String trackNumber = date + System.currentTimeMillis();

        //WERTE EINTRAGEN
        //String tracknumber = DATE+UHRZEIT;

        //Neues User-Objekt erzeugen
        Ways ways = new Ways();

        ways.setTransport(transportChoice);
        ways.setDuration(convertedTime);
        ways.setDistance(distanceCast);
        ways.setDate(date);
        ways.setTrackingNumber(trackNumber);

        //TODO: Rausfinden warum doppeltgespeichert wird
        //Erzeugtes User-Objekt der User Liste in der User Data Klasse hinzufuegen mittels eigenerstellter Methode
        WayData.getInstance().addWayToList(ways);

        DbHelper.getInstance(NavigationDrawer.this).saveWay(ways);

        //Naechste Zeilen benoetigt man für den Toast
        Log.i("TAG", "Nutzer wurde hinzugefügt");
        Toast.makeText(NavigationDrawer.this, "Weg wurde hinzugefügt", Toast.LENGTH_SHORT).show();

        //Braucht man folgendes?
        //finish();
    }

}
