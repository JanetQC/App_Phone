package com.example.janetdo.toomapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janetdo.toomapp.Helper.Catalog;
import com.example.janetdo.toomapp.Helper.CloudantService;
import com.example.janetdo.toomapp.Helper.FirebaseInstanceIDService;
import com.example.janetdo.toomapp.Helper.MyAdapter;
import com.example.janetdo.toomapp.Helper.ListHolder;
import com.example.janetdo.toomapp.Helper.Problem;
import com.example.janetdo.toomapp.Helper.State;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by janetdo on 05.12.17.
 */

public class WorkerActivity extends AppCompatActivity {
    private Problem problem;
    private static String TABLE_PROBLEM = "problems";
    private List<Problem> allProblems;
    private CloudantService service;
    private RelativeLayout relativeLayout;
    private GridLayout gridLayout;
    private int selection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle parameters = getIntent().getExtras();
        service = new CloudantService();
        if (parameters != null && parameters.containsKey("layout")) {

            Intent intent = getIntent();
            // setContentView(parameters.getInt("layout"));
            setContentView(R.layout.test);
            Bundle extras = getIntent().getExtras();
            ListHolder holder = (ListHolder) extras.get("problemsList");
           // allProblems = holder.getProblemList();
            allProblems = holder.getProblemList();
            initView(allProblems);
        } else {
            setContentView(R.layout.activity_worker);
            initListener();
        }


    }

    public void initListener() {
        EditText comment = findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.setText("");
            }
        });

    }

    public void initView(List<Problem> allProblems) {
        int[] states = {R.drawable.new_problem, R.drawable.problem_in_progress, R.drawable.done_problem};
        String[] stuff = {"Hi", "Jup", "Hi", "Jup", "Hi", "Jup"};


        ListView lView;

        MyAdapter customizedAdapter;
        lView = (ListView) findViewById(R.id.list);

        customizedAdapter = new MyAdapter(WorkerActivity.this, states, allProblems, stuff);
        lView.setAdapter(customizedAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopupWindow(allProblems.get(i), i);
            }
        });
    }


    private void showPopupWindow(Problem problem, int position) {

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.information_window, null);


        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, 900, 400, focusable);
        relativeLayout = findViewById(R.id.relativeLayout);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);

        ImageView itemPic;
        TextView type = popupView.findViewById(R.id.type);
        type.setText(problem.getProblemType());
        TextView aisle = popupView.findViewById(R.id.aisle);
        aisle.setText("Gang " + Integer.toString(problem.getAisle()));
        TextView desc = popupView.findViewById(R.id.description);
        desc.setText("Beschreibung des Problems");

        type.setTextSize(35);
        desc.setTextSize(25);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        Button acceptBtn = popupView.findViewById(R.id.accept);
        Button declineBtn = popupView.findViewById(R.id.decline);
        if (allProblems.get(position).getState().equals(State.DONE)) {
            acceptBtn.setVisibility(View.INVISIBLE);
            declineBtn.setVisibility(View.INVISIBLE);
        }
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTask(position);
                popupWindow.dismiss();

            }
        });
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineTask(position);
                popupWindow.dismiss();
            }
        });
    }

    private void acceptTask(int position) {
        Problem problem = allProblems.get(position);
        State state = problem.getState();
        State resultState = State.IN_PROGRESS;
        if (state.equals(State.IN_PROGRESS)) {
            resultState = State.DONE;
        } else if (state.equals(State.DONE)) {
            resultState = State.DONE;
        }
        problem.setState(resultState);
        //TODO: sort list!
       // List<Problem> sortedList = Collections.sort(allProblems);
        initView(allProblems);

    }

    private void declineTask(int position) {

    }

    private List<TextView> createRowContent(Problem problem) {
        List<String> elements = new ArrayList<>();
        List<TextView> textElements = new ArrayList<>();
        elements.add(Integer.toString(problem.getAisle()));
        elements.add(problem.getProblemType());
        elements.add(problem.getTimestamp().toString());
        for (int i = 0; i < 3; i++) {
            TextView view = new TextView(this);
            view.setText(elements.get(i));
            view.setTextSize(20);
            view.setPadding(40, 0, 0, 0);
            view.setWidth(i * 150);
            textElements.add(view);
        }
        return textElements;
    }

    public void searchForHelp(Problem prob) {
       /*
        if (problem == null) {
            problem = new Problem(Problem.problemType.GENERAL);
        }
*/
        service.writeToTable(TABLE_PROBLEM, prob);
        // service.writeToTable(TABLE_PROBLEM, problem);


    }

    public void onRadioButtonClicked(View view) {
        EditText text = findViewById(R.id.comment);
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.generalHelp:
                if (checked)
                    //      problem = new Problem(Problem.problemType.GENERAL);
                    selection = 0;
                text.setVisibility(View.GONE);
                break;
            case R.id.specificHelp:
                if (checked)
                    //     problem = new Problem(Problem.problemType.SPECIFIC);
                    selection = 1;
                text.setVisibility(View.GONE);
                break;
            case R.id.other:
                selection = 2;
                if (checked)
                    //    problem = new Problem(Problem.problemType.OTHER);

                    text.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void sendProblem(View view) {
        EditText comment = findViewById(R.id.comment);
        Problem.problemType type = Problem.problemType.GENERAL;
        switch (selection) {
            case 1:
                type = Problem.problemType.SPECIFIC;
                break;
            case 2:
                type = Problem.problemType.OTHER;
                break;
        }
        Problem newProblem = new Problem(type, comment.getText().toString());
        Toast.makeText(getApplicationContext(), "Ihre Nachricht wurde versandt.",
                Toast.LENGTH_LONG).show();
        searchForHelp(newProblem);
        try {
            wait(1000);
        } catch (Exception e) {

        }
        FirebaseInstanceIDService service = new FirebaseInstanceIDService();
        //  service.onTokenRefresh();
        FirebaseMessaging.getInstance().subscribeToTopic("worker");
        try {
            service.sendPushNotification();
        } catch (Exception e) {
            System.out.println("Firebase Message Error");
        }
        finish();
    }

    public void clickBtn(View view) {
        System.out.println("Clicked!");
        System.out.println(view.getId());
        View p = (View) view.getRootView().getRootView();
        if (p != null) {
            System.out.println(p.findViewById(R.id.accept).getId());
        }
    }

}
