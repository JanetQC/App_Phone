package com.example.janetdo.toomapp.Helper;

import android.util.Log;

import com.example.janetdo.toomapp.Credentials.Credentials;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;
import static org.apache.http.impl.client.HttpClientBuilder.*;

/**
 * Created by janetdo on 26.12.17.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private String key = "";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String key = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Refreshed token: " + refreshedToken);
        try {
          //  sendPushNotification(refreshedToken);
         //   System.out.println("Sending notification....");
        } catch (Exception e) {
            System.out.println("NOT GONNA WORK " + e);
        }

    }



    public void sendPushNotification(String body, String topic)
            throws IOException, JSONException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url = new URL(Credentials.API_URL_FCM);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "key=" + Credentials.AUTH_KEY_FCM);
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject json = new JSONObject();
                    json.put("to", "/topics/"+topic);
                    JSONObject data = new JSONObject();
                    data.put("title", "Meldung von Toom Smart Shopping");
                    data.put("body", body);
                    json.put("notification", data);

                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(json.toString());
                    wr.flush();
                    conn.getInputStream();

                    System.out.println("GCM Notification is sent successfully");
                } catch (Exception e) {
                    System.out.println("Connection to database failed.");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}
