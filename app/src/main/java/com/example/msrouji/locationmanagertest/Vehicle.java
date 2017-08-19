package com.example.msrouji.locationmanagertest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by msrouji on 17/08/2017.
 */

public class Vehicle {
    private int id;
    private String model;
    private String registration;
    private int year;
    private String type;
    private float latitude;
    private float longitude;
    private ReceiveInfo after_action;

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getRegistration() {
        return registration;
    }

    public int getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public Vehicle(String ip, String token, String id, String token_csrf, ReceiveInfo after) {
        //System.err.println("Je passe");
        after_action = after;
        new RequestTask().execute(ip, token, id, token_csrf);
    }

    private class RequestTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            /*if (!isOnline()){
                return "You do not have internet access";
            }*/

            String responseString;
            try {
                URL url = new URL("http://" + params[0] + "/db/vehicles/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                //conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "JWT " + params[1]);
                conn.setRequestProperty("Cookie", String.format("sessionsid=%s;csrftoken=%s", params[2], params[3]));

                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                BufferedReader buff = new BufferedReader(reader);

                responseString = conn.getResponseMessage();

                if (conn.getResponseMessage().equals("OK")) {
                    try {
                        JSONObject receiveJson = new JSONArray(buff.readLine()).getJSONObject(0);
                        model = receiveJson.getString("brand") + " " + receiveJson.getString("model");
                        registration = receiveJson.getString("registration");
                        year = receiveJson.getInt("year");
                        type = receiveJson.getString("category");
                        id = receiveJson.getInt("id");
                        String position = receiveJson.getString("pos");

                        latitude = Float.parseFloat(position.split(",")[0]);
                        longitude = Float.parseFloat(position.split(",")[1]);

                        System.err.println(model);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            } catch (IOException e) {
                responseString = "Problem with internet connection";
                e.printStackTrace();
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            after_action.afterReceive(model);
        }
    }
}
