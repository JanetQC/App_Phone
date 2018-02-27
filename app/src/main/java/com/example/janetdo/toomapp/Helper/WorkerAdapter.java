package com.example.janetdo.toomapp.Helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.janetdo.toomapp.R;
import com.example.janetdo.toomapp.WorkerActivity;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by janetdo on 30.01.18.
 */

public class WorkerAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final int[] state;
    private final List<Type> allTypes;
    private static String INCIDENT_STRING = "Mängel";

    public WorkerAdapter(Activity context, int[] state, List<Type> allTypes, String[] itemnames) {
        super(context, R.layout.problem_item_view, itemnames);
        this.context = context;
        this.allTypes = allTypes;
        this.state = state;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.problem_item_view, null, true);
        //TODO:this is really ugly, fix it!
        View emptyView = inflater.inflate(R.layout.empty, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        TextView description = (TextView) rowView.findViewById(R.id.desc);
        TextView timestamp = (TextView) rowView.findViewById(R.id.timestamp);

        Button acceptBtn = rowView.findViewById(R.id.acceptTask);
        Button deleteBtn = rowView.findViewById(R.id.deleteTask);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTask(position, imageView, acceptBtn, deleteBtn);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allTypes.remove(position);

                notifyDataSetChanged();

            }
        });

        SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm");
        if(position >= allTypes.size() ){
            return emptyView;
        }
        Type type = allTypes.get(position);
        System.out.println("position: "+position);
        System.out.println("allTypes size : "+allTypes.size());
        if (type.getState().equals(State.DONE)) {
            acceptBtn.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.INVISIBLE);
        }
        if (type instanceof Problem) {
            Problem prob = (Problem) type;
            txtTitle.setText(prob.getProblemTypeString());
        } else {
            txtTitle.setText(INCIDENT_STRING);
        }
        description.setText("Gang " + Integer.toString(type.getAisle()) + "  " + type.getComment());
        imageView.setImageResource(state[getImage(type.getState().toString())]);

        Timestamp today = new Timestamp(System.currentTimeMillis());
        long diff = today.getTime() - type.getTimestamp().getTime();
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        long ago = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        String timeString = printFormat.format(type.getTimestamp());
        String dayRange;
        if (ago == 0) {
            dayRange = "Heute";
        } else if (ago == 1) {
            dayRange = "Gestern";
        } else if (ago == 2) {
            dayRange = "Vorgestern";
        } else {
            dayRange = "Vor " + ago + " Tagen";
        }
        timestamp.setText(dayRange + " um " + timeString + " Uhr");
        return rowView;

    }

    private void acceptTask(int position, ImageView image, Button acceptBtn, Button deleteBtn) {
        Type type = allTypes.get(position);
        State tempState = type.getState();
        State resultState = State.IN_PROGRESS;

        if (tempState.equals(State.NEW) && type instanceof Problem) {
            FirebaseInstanceIDService notificationService = new FirebaseInstanceIDService();
            try {
                    notificationService.sendPushNotification("Ein Mitarbeiter ist unterwegs, bitte warten Sie.", "client");
            } catch (Exception e) {
                System.out.println("Firebase Message Error: Could not send push notification!");
            }
        } else if (tempState.equals(State.IN_PROGRESS)) {
            resultState = State.DONE;
            acceptBtn.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.INVISIBLE);
        } else if (tempState.equals(State.DONE)) {
            resultState = State.DONE;

        }
        type.setState(resultState);
        image.setImageResource(state[getImage(resultState.toString())]);
    }

    private int getImage(String state) {
        int result = 0;
        switch (state) {
            case "IN_PROGRESS":
                result = 1;
                break;
            case "DONE":
                result = 2;
                break;
        }
        return result;
    }
}