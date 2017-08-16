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
    private Context context;
    private String ip;

    public LocationSender(Context _context, String _token, String _ip) {
        token = _token;
        context = _context;
        ip = _ip;
    }

    public void send_location(double latitude, double longitude) {
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
                URL url = new URL("http://" + ip);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "JWT " + token);
                System.out.println(token);


                JSONObject toSendData = new JSONObject();
                try {
                    toSendData.accumulate("latitude", params[0]);
                    toSendData.accumulate("longitude", params[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Problem with the send of Json";
                }
                System.out.println("Data " + toSendData.toString());

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(toSendData.toString());
                writer.flush();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    responseString = toSendData.toString();
                } else {
                    responseString = "Incorrect login";
                }
            } catch (IOException e) {
                responseString = "Problem with internet connection";
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("result " + result);
            if (!result.equals(""))
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }
}
