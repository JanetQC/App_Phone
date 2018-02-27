package com.example.janetdo.toomapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.janetdo.toomapp.Helper.CloudantService;
import com.example.janetdo.toomapp.Helper.FirebaseInstanceIDService;
import com.example.janetdo.toomapp.Helper.Incident;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janetdo on 04.01.18.
 */

public class IncidentActivity extends AppCompatActivity {
    private CloudantService cloudantService;
    private FirebaseInstanceIDService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cloudantService = new CloudantService();
        notificationService = new FirebaseInstanceIDService();
        setContentView(R.layout.activity_incident);
        initListener();

    }

    private void initListener() {
        EditText comment = findViewById(R.id.incidentTextField);
        comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void sendIncident(View view) {
        EditText incidentTextField = findViewById(R.id.incidentTextField);
        Toast.makeText(getApplicationContext(), "Der Mangel wurde gemeldet. Vielen Dank.",
                Toast.LENGTH_LONG).show();
        Incident incident = new Incident(incidentTextField.getText().toString().trim());
        cloudantService.writeToTable("flaws", incident);
        try {
            wait(1000);
        } catch (Exception e) {
        }

        try {
            notificationService.sendPushNotification("Ein Mangel wurde gemeldet.", "worker");
        } catch (Exception e) {
            System.out.println("Firebase Message Error: Could not send incident push notification!");
        }
        finish();
    }

}
