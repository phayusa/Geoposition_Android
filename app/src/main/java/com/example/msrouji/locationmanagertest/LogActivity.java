package com.example.msrouji.locationmanagertest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogActivity extends Activity {

    public static final String prefName = "LogPreference";
    private final static int INTERNET_KEY = 200;


    private class LogRequest extends AsyncTask<String, Void, String> {

        private String username;
        private String password;


        public LogRequest(String user, String pwd) {
            username = user;
            password = pwd;
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            /*
            if (!isOnline()){
                return "You do not have internet access";
            }
            */

            String responseString;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");


                JSONObject toSendData = new JSONObject();
                try {
                    toSendData.accumulate("username", username);
                    toSendData.accumulate("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Problem with the send of Json";
                }

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(toSendData.toString());
                writer.flush();

                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                BufferedReader buff = new BufferedReader(reader);

                responseString = conn.getResponseMessage();

                if (conn.getResponseMessage().equals("Created")) {
                    String token;
                    try {
                        JSONObject receiveJson = new JSONObject(buff.readLine());
                        token = receiveJson.getString("token");
                        Intent i = new Intent(getApplicationContext(), save_bis.class);
                        i.putExtra(getString(R.string.key_token), token);
                        i.putExtra(getString(R.string.key_ip), params[1]);
                        startActivity(i);
                        responseString = " Welcome " + username;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Problem with the receive json";
                    }


                }

            } catch (IOException e) {
                responseString = "Problem with internet connection";
                //responseString = e.getMessage();
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("result " + result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);

        check_permission();

        SharedPreferences sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.key_username), "");
        String password = sharedPref.getString(getString(R.string.key_passwd), "");
        String ip = sharedPref.getString(getString(R.string.key_ip), "");

        ((EditText) findViewById(R.id.username_field)).setText(username);
        ((EditText) findViewById(R.id.password_field)).setText(password);
        ((EditText) findViewById(R.id.ip_field)).setText(ip);
    }

    public void connectionClient(View v) {
        String ip = ((EditText) findViewById(R.id.ip_field)).getText().toString();
        //String url = "http://" + ip + "/user/login/";
        String url = "http://" +ip ;
        String username = ((TextView) findViewById(R.id.username_field)).getText().toString();
        String password = ((TextView) findViewById(R.id.password_field)).getText().toString();

        new LogRequest(username, password).execute(url, ip);


    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();

        String username = ((TextView) findViewById(R.id.username_field)).getText().toString();
        CheckBox psw = ((CheckBox) findViewById(R.id.remember_user));
        editor.putString(getString(R.string.key_username), username);
        // Only Main_Activity password if it is wanted
        if (psw.isChecked()) {
            String password = ((TextView) findViewById(R.id.password_field)).getText().toString();
            editor.putString(getString(R.string.key_passwd), password);
        }

        String ip = ((EditText) findViewById(R.id.ip_field)).getText().toString();
        editor.putString(getString(R.string.key_ip), ip);
        editor.apply();
    }

    public void check_permission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.INTERNET)) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissions");
                builder.setMessage("Please grant location permissions.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ask_permission(Manifest.permission.INTERNET, INTERNET_KEY);
                            }
                        });

                builder.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        INTERNET_KEY);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INTERNET_KEY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    finish();
                }
            }
        }
    }

    public void ask_permission(String permission, int key_asked) {
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                key_asked);
        onResume();
    }
}
