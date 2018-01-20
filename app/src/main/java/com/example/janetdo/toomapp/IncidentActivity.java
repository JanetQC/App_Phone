package com.example.janetdo.toomapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janetdo.toomapp.Helper.CloudantService;
import com.example.janetdo.toomapp.Helper.Incident;
import com.example.janetdo.toomapp.Helper.ListHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janetdo on 04.01.18.
 */

public class IncidentActivity extends AppCompatActivity {
    private EditText reportedFlaw;
    private List<Incident> incidentList;

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incidentList = new ArrayList<>();
        Bundle parameters = getIntent().getExtras();
        if (parameters != null && parameters.containsKey("layout")) {
            Intent intent = getIntent();
            setContentView(parameters.getInt("layout"));
            Bundle extras = getIntent().getExtras();
            ListHolder holder = (ListHolder) extras.get("incidentsList");
            System.out.println("!!" + holder.toString());
            initView(holder.getIncidentList());
        } else {
            setContentView(R.layout.activity_incident);
            Button sendFlawButton = findViewById(R.id.sendIncident);
            sendFlawButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getApplicationContext(), "Ihre Nachricht wurde versandt.",
                            Toast.LENGTH_LONG).show();
                    try {
                        wait(1000);
                    } catch (Exception e) {

                    }
                    reportedFlaw = findViewById(R.id.incidentTextField);
                    saveFlaw(reportedFlaw.getText().toString());

                    finish();
                }
            });
        }

    }

    public void initView(List<Incident> incidents) {
        incidentList = incidents;
        System.out.println("Incidents shown:" + incidents.size());
        tableLayout = findViewById(R.id.tableLayout);
        int tableLayoutSize = tableLayout.getChildCount();
        if (tableLayoutSize > 2) {
            View view = tableLayout.getChildAt(0);
            tableLayout.removeAllViews();
            tableLayout.addView(view);

        }

        List<TableRow> rows = new ArrayList<>();

        for (int i = 0; i < incidents.size(); i++) {
            TableRow tableRow = new TableRow(this);

            TextView view1 = new TextView(this);
            view1.setText(Integer.toString(incidents.get(i).getAisle()));
            TextView view2 = new TextView(this);
            view2.setText(incidents.get(i).getDescriptionText());
            CheckBox box = new CheckBox(this);

            view1.setTextSize(30);
            view2.setTextSize(30);
            view1.setPadding(40, 0, 0, 0);
            view2.setPadding(40, 0, 0, 0);
            view1.setWidth(300);
            view2.setWidth(300);

            tableRow.addView(view1);
            tableRow.addView(view2);
            tableRow.addView(box);
            rows.add(tableRow);
            tableLayout.addView(tableRow);

        }
    }

    public void getCheckedCheckboxes(View v) {
        List<Integer> deletingIndexes = new ArrayList<>();
        List<Incident> toBeRemoved = new ArrayList<>();
        for (int i = 0, j = tableLayout.getChildCount(); i < j; i++) {
            View view = tableLayout.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow tr = (TableRow) view;
                View actionElement = tr.getChildAt(2);
                if (actionElement instanceof CheckBox) {
                    CheckBox box = (CheckBox) actionElement;
                    if (box.isChecked()) {
                        toBeRemoved.add(incidentList.get(i - 1));
                        deletingIndexes.add(i - 1);

                    }
                }
            }
        }

        deleteIncidents(toBeRemoved);

    }

    private void deleteIncidents(List<Incident> indexes) {
        List<Incident> tempList = incidentList;
        for (int i = 0; i < indexes.size(); i++) {
            tempList.removeAll(indexes);
        }
        initView(tempList);
    }

    public void saveFlaw(String description) {
        Incident incident = new Incident(description);
        CloudantService service = new CloudantService();
        service.writeToTable("flaws", incident);
    }
}
