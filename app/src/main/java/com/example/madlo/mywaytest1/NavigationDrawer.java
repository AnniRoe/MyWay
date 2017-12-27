package com.example.madlo.mywaytest1;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 222;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "111";
    public static final float DEFAULT_ZOOM_LEVEL = 17.0f;
    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    private Boolean mRequestingLocationUpdates;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private String TAG = "Tag";
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG2 = NavigationDrawer.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Timer timer;
    private TimerTask timerTask;
    final Handler polylineHandler = new Handler();

    private TextView resultTextView;

    private List<LatLng> LatLngPosition = new ArrayList<>();
    private boolean trackingActive = false;
    private Polyline currentPolyLine;
    private long trackingStartTime;
    private long trackingEndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Annika und Vicky

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Annika und Vicky

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        resultTextView = (TextView) findViewById(R.id.text_view_tracking_result);
        final View startTrackingButton = findViewById(R.id.button_start_tracking);
        final View stopTrackingButton = findViewById(R.id.button_stop_tracking);

        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startTracking();
                startTrackingButton.setVisibility(View.GONE);
                stopTrackingButton.setVisibility(View.VISIBLE);
                resultTextView.setVisibility(View.GONE);
            }
        });

        stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopTracking();
                startTrackingButton.setVisibility(View.VISIBLE);
                stopTrackingButton.setVisibility(View.GONE);
                resultTextView.setVisibility(View.VISIBLE);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }


    private void startTracking() {
        trackingActive = true;
        trackingStartTime = System.currentTimeMillis();
        // Remove old tracking

        if (currentPolyLine != null) {
            currentPolyLine.remove();
        }
        LatLngPosition.clear();

        PolylineOptions options = new PolylineOptions()
                .width(5)
                .color(Color.RED);

        currentPolyLine = mMap.addPolyline(options);
        // neuer timer
        timer = new Timer();

        //timer task starten
        timerTask = new TimerTask() {
            public void run() {
                updatePolyLine();
            }
        };

        //nach 0,5s starten und jede sekunde abfragen
        timer.schedule(timerTask, 500, 5000); //
    }


    // internet: http://www.trivisonno.com/programming/update-android-gui-timer
    private void updatePolyLine() {
        polylineHandler.post(polylineRunnable);
    }

    @SuppressLint("MissingPermission")
    final Runnable polylineRunnable = new Runnable() {
        @Override
        public void run() {
            // aktuelle location holen
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
        // Deactivate data-collection in list
        trackingActive = false;
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
        String convertedTime = String.format(Locale.GERMAN, "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(trackingDuration),
                TimeUnit.MILLISECONDS.toSeconds(trackingDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackingDuration))
        );
        String durationText = "Dauer: " + convertedTime;

        // Streckenberechnung
        double distance = calculateTrackedDistance();

        String distanceString = "Distanz (Meter): " + String.format(Locale.GERMAN, "%.2f", distance);
        String resultViewText = durationText + "\n" + distanceString;

        resultTextView.setText(resultViewText);
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

            // other 'case' lines to check for other
            // permissions this app might request
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
    //Annika und Vicky

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }
    //Annika und Vicky

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //Annika und Vicky

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        // Wenn der Drawer wieder funktioniert, diesen Toast entfernen und unten einkommentieren
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "Diese Funktion ist noch nicht verfügbar", duration);
        toast.show();


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

    //Annika und Vicky
    @Override
    protected void onResume() {
        super.onResume();
        /**if (mRequestingLocationUpdates) {
         startLocationUpdates();
         }*/
        //Team Treehouse
        //setUpMapIfNeeded();
        mGoogleApiClient.connect();
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }


}
