package com.example.janetdo.toomapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janetdo.toomapp.Helper.CloudantService;
import com.example.janetdo.toomapp.Helper.FirebaseInstanceIDService;
import com.example.janetdo.toomapp.Helper.Incident;
import com.example.janetdo.toomapp.Helper.Properties;
import com.example.janetdo.toomapp.Helper.WorkerAdapter;
import com.example.janetdo.toomapp.Helper.ListHolder;
import com.example.janetdo.toomapp.Helper.Problem;
import com.example.janetdo.toomapp.Helper.ProblemType;
import com.example.janetdo.toomapp.Helper.State;
import com.example.janetdo.toomapp.Helper.Type;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by janetdo on 05.12.17.
 */

public class WorkerActivity extends AppCompatActivity {
    private static String TABLE_PROBLEM = "problems";
    private CloudantService cloudantService;
    private ProblemType problemType = ProblemType.GENERAL;
    private RelativeLayout relativeLayout;
    private FirebaseInstanceIDService notificationService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Type> allTypes;
    private List<Problem> problems;
    private List<Incident> incidents;
    private NetworkInfo activeNetwork;

    private CheckBox all;
    private CheckBox incident;
    private CheckBox problem;
    private CheckBox state;
    private CheckBox timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle parameters = getIntent().getExtras();
        cloudantService = new CloudantService();
        notificationService = new FirebaseInstanceIDService();
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        problems = new ArrayList<>();
        incidents = new ArrayList<>();

        if (parameters != null && parameters.containsKey("layout")) {
            setContentView(parameters.getInt("layout"));
            ListHolder holder = (ListHolder) getIntent().getExtras().get("problemsList");
            problems = holder.getProblemList();
            incidents = holder.getIncidentList();
            allTypes = unifyLists(problems, incidents);
            all = findViewById(R.id.checkAll);
            incident = findViewById(R.id.checkIncidents);
            problem = findViewById(R.id.checkProblems);
            state = findViewById(R.id.checkState);
            timestamp = findViewById(R.id.checkTimestamp);
            all.setChecked(true);
            timestamp.setChecked(true);
            Collections.sort(allTypes, Type.timestampComparator());
            initView();
            initSwipeRefresh();

        } else {
            setContentView(R.layout.activity_worker);
            initListener();
        }

    }

    private void initSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateLists();
                    }
                }
        );
    }

    private List<Type> unifyLists(List<Problem> allProblems, List<Incident> incidents) {
        List<Type> allTypes = new ArrayList<>();
        for (int i = 0; i < allProblems.size(); i++) {
            allTypes.add(allProblems.get(i));
        }

        for (int i = 0; i < incidents.size(); i++) {
            allTypes.add(incidents.get(i));
        }

        return allTypes;
    }

    @Override
    public void onBackPressed() {
        FirebaseMessaging.getInstance().subscribeToTopic("client");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("worker");
        Properties.getInstance().setAdmin(false);
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                FirebaseMessaging.getInstance().subscribeToTopic("client");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("worker");
                Properties.getInstance().setAdmin(false);
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initListener() {
        EditText comment = findViewById(R.id.comment);
        comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    comment.setText("");
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

    }

    private void initView() {
        int[] states = {R.drawable.new_problem, R.drawable.problem_in_progress, R.drawable.double_check};
        String[] stuff = new String[allTypes.size()];
        ListView lView = (ListView) findViewById(R.id.list);
        WorkerAdapter customizedAdapter;

        customizedAdapter = new WorkerAdapter(WorkerActivity.this, states, allTypes, stuff);
        lView.setAdapter(customizedAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopupWindow(allTypes.get(i), i);

            }
        });
    }



    private void showPopupWindow(Type type, int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.information_window, null);
        relativeLayout = findViewById(R.id.relativeLayout);
        Button acceptBtn = popupView.findViewById(R.id.accept);
        Button deleteBtn = popupView.findViewById(R.id.delete);
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 400, true);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);

        TextView problemType = popupView.findViewById(R.id.type);
        TextView aisle = popupView.findViewById(R.id.aisle);
        TextView desc = popupView.findViewById(R.id.description);

        if (type instanceof Problem) {
            Problem problem = (Problem) type;
            problemType.setText(problem.getProblemTypeString());
        } else {
            problemType.setText("Mängel");

        }
        aisle.setText("Gang " + Integer.toString(type.getAisle()));
        desc.setText(type.getComment());
        problemType.setTextSize(35);
        desc.setTextSize(25);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        if (type.getState().equals(State.DONE)) {
            acceptBtn.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.INVISIBLE);
        }
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTask(position);
                popupWindow.dismiss();

            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allTypes.remove(position);
                initView();
                popupWindow.dismiss();
            }
        });
    }

    private void acceptTask(int position) {
        Type type = allTypes.get(position);
        State state = allTypes.get(position).getState();

        if (state.equals(State.NEW)) {
            try {
                notificationService.sendPushNotification("Ein Mitarbeiter ist unterwegs, bitte warten Sie.", "client");
            } catch (Exception e) {
                System.out.println("Firebase Message Error: Could not send push notification!");
            }
        }
        State resultState = State.IN_PROGRESS;
        if (state.equals(State.IN_PROGRESS) || state.equals(State.DONE)) {
            resultState = State.DONE;
        }
        type.setState(resultState);


        initView();

    }

    private void updateLists() {
        boolean isWiFi = false;
        if (activeNetwork != null) {
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        if (isWiFi) {
            List<Problem> allProblems = cloudantService.getAllProblems();
            List<Incident> incidents = cloudantService.getAllIncidents();
            unifyLists(allProblems, incidents);
            initView();
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    public void onRadioButtonClicked(View view) {
        EditText text = findViewById(R.id.comment);
        boolean checked = ((RadioButton) view).isChecked();
        text.setText("Bitte beschreiben Sie Ihren Wunsch.");
        switch (view.getId()) {
            case R.id.generalHelp:
                if (checked)
                    problemType = ProblemType.GENERAL;
                text.setVisibility(View.GONE);
                break;
            case R.id.specificHelp:
                if (checked)
                    problemType = ProblemType.SPECIFIC;
                text.setVisibility(View.GONE);
                break;
            case R.id.other:
                if (checked)
                    problemType = ProblemType.OTHER;
                text.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void sendProblem(View view) {
        EditText comment = findViewById(R.id.comment);
        String content = "";
        if (!comment.getText().toString().equals("Bitte beschreiben Sie Ihren Wunsch.")) {
            content = comment.getText().toString().trim();
        }
        Problem newProblem = new Problem(problemType, content);
        Toast.makeText(getApplicationContext(), "Ihre Nachricht wurde versandt.",
                Toast.LENGTH_LONG).show();
        cloudantService.writeToTable(TABLE_PROBLEM, newProblem);
        try {
            wait(1000);
        } catch (Exception e) {
        }
        try {
            notificationService.sendPushNotification("Ein Kunde benötigt Ihre Hilfe!", "worker");
        } catch (Exception e) {
            System.out.println("Firebase Message Error: Could not send push notification!");
        }
        finish();
        FirebaseMessaging.getInstance().subscribeToTopic("client");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("worker");
    }

    public void sortState(View view) {
        timestamp.setChecked(false);
        Collections.sort(allTypes);
        initView();
    }

    public void sortTimestamp(View view) {
        state.setChecked(false);
        Collections.sort(allTypes, Type.timestampComparator());
        initView();

    }

    public void sortProblems(View view) {
        all.setChecked(false);
        incident.setChecked(false);

        allTypes = unifyLists(problems, new ArrayList<>());
        if (timestamp.isChecked()) {
            Collections.sort(allTypes, Type.timestampComparator());
        } else Collections.sort(allTypes);
        initView();
    }

    public void sortIncidents(View view) {
        all.setChecked(false);
        problem.setChecked(false);
        allTypes = unifyLists(new ArrayList<>(), incidents);
        if (timestamp.isChecked()) {
            Collections.sort(allTypes, Type.timestampComparator());
        } else Collections.sort(allTypes);
        initView();
    }

    public void showAll(View view) {
        incident.setChecked(false);
        problem.setChecked(false);
        allTypes = unifyLists(problems, incidents);
        if (timestamp.isChecked()) {
            Collections.sort(allTypes, Type.timestampComparator());
        } else Collections.sort(allTypes);
        initView();
    }


}
