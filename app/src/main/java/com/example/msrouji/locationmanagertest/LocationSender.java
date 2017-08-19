package com.example.msrouji.locationmanagertest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by msrouji on 16/08/2017.
 */

class LocationSender {
    private String token;
    private String ip;
    private int id_to_send;

    public LocationSender(String _token, String _ip) {
        token = _token;
        ip = _ip;
    }

    public void send_location(int id_vehicle, double latitude, double longitude) {
        id_to_send = id_vehicle;
        new RequestTask().execute(latitude,longitude);
    }

    private class RequestTask extends AsyncTask<Double, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Double... params) {

            /*if (!isOnline()){
                return "You do not have internet access";
            }*/

            String responseString;
            try {
                URL url = new URL("http://" + ip+"/db/vehicle/"+id_to_send+"/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PATCH");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "JWT " + token);
                System.out.println(token);


                JSONObject toSendData = new JSONObject();
                try {
                    toSendData.accumulate("pos", params[0]+","+params[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Problem with the send of Json";
                }

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(toSendData.toString());
                writer.flush();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    responseString = toSendData.toString();
                } else {
                    responseString = "Problem with sended data";
                }
            } catch (IOException e) {
                responseString = "Problem with internet connection";
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
