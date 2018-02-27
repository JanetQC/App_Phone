package com.example.janetdo.toomapp.Helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.example.janetdo.toomapp.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by janetdo on 26.12.17.
 */

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {

            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                public void run() {
                    System.out.println("Message!");
                    System.out.println(remoteMessage.getFrom());
                    System.out.println("this is user admin "+ Properties.getInstance().isAdmin());
                    if(!Properties.getInstance().isAdmin() && remoteMessage.getFrom().contains("client")) {
                        Toast toast = Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    else if(Properties.getInstance().isAdmin() && remoteMessage.getFrom().contains("worker")){
                        Toast toast = Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }

    }

}
