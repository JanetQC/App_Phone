package com.example.janetdo.toomapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janetdo.toomapp.Helper.CloudantService;
import com.example.janetdo.toomapp.Helper.Incident;
import com.example.janetdo.toomapp.Helper.ListHolder;
import com.example.janetdo.toomapp.Helper.Problem;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by janetdo on 05.12.17.
 */

public class WorkerActivity extends AppCompatActivity {
    private Problem problem;
    private static String TABLE_PROBLEM = "problems";
    private List<Problem> problemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        problemList = new ArrayList<>();
        Bundle parameters = getIntent().getExtras();
        System.out.println("still outside");
        if (parameters != null && parameters.containsKey("layout")) {
            System.out.println("here!");

            Intent intent = getIntent();
            setContentView(parameters.getInt("layout"));
            Bundle extras = getIntent().getExtras();
            ListHolder holder = (ListHolder) extras.get("problemsList");
            System.out.println("!!" + holder.toString());
            initView(holder.getProblemList());
        } else {
            setContentView(R.layout.activity_worker);
            Button workerButton = findViewById(R.id.searchWorker);
            workerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Ihre Nachricht wurde versandt.",
                            Toast.LENGTH_LONG).show();
                    searchForHelp(v);
                    try {
                        wait(1000);
                    } catch (Exception e) {

                    }
                    finish();
                }
            });
        }

    }

    public void initView(List<Problem> allProblems) {
        List<Problem> problemList = allProblems;
        TableLayout tableLayout = findViewById(R.id.problemLayout);

        List<TableRow> rows = new ArrayList<>();

        for (int i = 0; i < problemList.size(); i++) {
            TableRow tableRow = new TableRow(this);

            List<TextView> rowView = createRowContent(problemList.get(i));
           /*
            TextView view1 = new TextView(this);
            view1.setText(problemList.get(i).getProblemType().toString());
            TextView view2 = new TextView(this);
            view2.setText(Integer.toString(problemList.get(i).getAisle()));
            TextView view3 = new TextView(this);
            view3.setText(problemList.get(i).getTimestamp().toString());

            view1.setTextSize(20);
            view2.setTextSize(20);
            view3.setTextSize(20);
            view1.setPadding(40, 0, 0, 0);
            view2.setPadding(40, 0, 0, 0);
            view3.setPadding(40, 0, 0, 0);
            view1.setWidth(300);
            view2.setWidth(100);
            view3.setWidth(500);
            tableRow.addView(view1);
            tableRow.addView(view2);
            tableRow.addView(view3);
            */
           for(int j =0; j < rowView.size(); j++){
            tableRow.addView(rowView.get(j));
           }
            rows.add(tableRow);
            tableLayout.addView(tableRow);

        }
    }

    private List<TextView> createRowContent(Problem problem) {
        List<String> elements = new ArrayList<>();
        List<TextView> textElements = new ArrayList<>();
        elements.add(Integer.toString(problem.getAisle()));
        elements.add( problem.getProblemType().toString());
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

    public void searchForHelp(View view) {
        if (problem == null) {
            problem = new Problem(Problem.problemType.GENERAL);
        }
        CloudantService service = new CloudantService();
        service.writeToTable(TABLE_PROBLEM, problem);


    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.generalHelp:
                if (checked)
                    problem = new Problem(Problem.problemType.GENERAL);
                break;
            case R.id.specificHelp:
                if (checked)
                    problem = new Problem(Problem.problemType.SPECIFIC);
                break;
            case R.id.other:
                if (checked)
                    problem = new Problem(Problem.problemType.OTHER);
                break;
        }
    }

    public void deleteProblem(View view) {

    }

}
