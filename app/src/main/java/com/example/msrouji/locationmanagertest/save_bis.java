package com.example.msrouji.locationmanagertest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class save_bis extends AppCompatActivity  {
    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;
    private final static int LOCATION_KEY = 100;
    private String token;
    private String ipAdress;
    private GPSTracker gps;


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
        }else {
            //Intent intentService = new Intent(this,LocationService.class);
            //intentService.putExtra(getString(R.string.key_ip),"192.169.0.100:9999");
            //intentService.putExtra(getString(R.string.key_token),"blablba");

            //startService(intentService);
            longitudeField.setText("fini");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latituteField = ((TextView) findViewById(R.id.Latitude_Field));
        longitudeField = ((TextView) findViewById(R.id.Longitude_Field));

        check_permission();

        /*Bundle extras = getIntent().getExtras();
        token = extras.getString(getString(R.string.key_token));
        ipAdress = extras.getString(getString(R.string.key_ip));*/



        latituteField.setText("Ta mere");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_KEY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Intent intentService = new Intent(this,LocationService.class);
                    //intentService.putExtra(getString(R.string.key_ip),"192.169.0.100:9999");
                    //intentService.putExtra(getString(R.string.key_token),"blablba");

                    //startService(intentService);

                } else {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(getApplicationContext(), LocationService.class));

    }
}
