package com.example.janetdo.toomapp.Helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by janetdo on 26.12.17.
 */

public class MessagingService extends FirebaseMessagingService {
    private Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {

            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                public void run() {
                    System.out.println("Message!");
                    System.out.println(remoteMessage.getTo());
                    System.out.println(remoteMessage.getFrom());
                    System.out.println(remoteMessage.getData());
                    //TODO: check user or client...
                    if(User.isUser()) {
                        Toast toast = Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }

    }

}
