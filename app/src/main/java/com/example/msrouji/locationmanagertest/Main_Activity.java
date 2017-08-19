package com.example.msrouji.locationmanagertest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;


public class Main_Activity extends AppCompatActivity implements LocationListener, ReceiveInfo{
    private TextView latituteField;
    private TextView longitudeField;
    private TextView vehicleField;
    private LocationManager locationManager;
    private String provider;
    private final static int LOCATION_KEY = 100;
    private String token;
    private String ipAdress;
    private String session_id;
    private String csrf_token;
    private LocationSender sender;
    private Vehicle vehicle;

    public void ask_permission(String permission, int key_asked) {
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                key_asked);
        onResume();
    }

    public void check_permission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissions");
                builder.setMessage("Please grant location permissions.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ask_permission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_KEY);
                            }
                        });

                builder.show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_KEY);
            }
        }

    }

    public void create_location_manager() {

        try {
            // Get the location manager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);

            Location location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                latituteField.setText("Location not available");
                longitudeField.setText("Location not available");
            }
            onLocationChanged(location);


            //locationManager.requestLocationUpdates(provider, 400, 1, this);

        } catch (SecurityException s) {
            s.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        check_permission();

        Bundle extras = getIntent().getExtras();
        token = extras.getString(getString(R.string.key_token));
        ipAdress = extras.getString(getString(R.string.key_ip));
        session_id = extras.getString("sessionid");
        csrf_token = extras.getString("csrftoken");

        sender = new LocationSender(token, ipAdress);


        latituteField = ((TextView) findViewById(R.id.Latitude_Field));
        longitudeField = ((TextView) findViewById(R.id.Longitude_Field));
        vehicleField = ((TextView) findViewById(R.id.vehicle_field));

        create_location_manager();

        vehicle = new Vehicle(ipAdress,token,session_id, csrf_token, this);

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 400, 1, this);
            }

        } catch (SecurityException s) {
            s.printStackTrace();
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));

        if(vehicle != null && vehicle.getId() != 0)
            sender.send_location(vehicle.getId(),lat,lng);

        //if (isOnline())
            //ssender.send_location(lat, lng);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_KEY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    create_location_manager();
                } else {
                    finish();
                }
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void afterReceive(String info) {
        vehicleField.setText(info);
    }
}
