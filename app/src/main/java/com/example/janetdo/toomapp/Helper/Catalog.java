package com.example.janetdo.toomapp.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by janetdo on 26.12.17.
 */

public class Catalog {
    private InputStream stream;
    private List<Item> catalog = new ArrayList<>();
    private List<Problem> problems = new ArrayList<>();
    private List<Incident> incidents = new ArrayList<>();

    public Catalog(InputStream stream) throws Exception {
        this.stream = stream;
    }

    public void initSortiment() throws Exception {
        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();

        String json;
        json = new String(buffer, "UTF-8");
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            String id = "";
            String name = "";
            String description = "";
            String quantity = "";
            String category = "";
            String scanCode = "";
            double salesPrice = 0;
            double price = 0;
            int aisle = 0;

            id = obj.getString("id");
            name = obj.getString("name");
            description = obj.getString("description");
            category = obj.getString("category");
            price = obj.getDouble("price");
            aisle = obj.getInt("aisle");
            salesPrice = 0;
            if (obj.has("salesPrice")) {
                salesPrice = obj.getDouble("salesPrice");
            }

            if (obj.has("scanCode")) {
                scanCode = obj.getString("scanCode");
            }
            if (obj.has("quantity")) {
                quantity = obj.getString("quantity");
            }


            Item item = new Item(id, name, description, quantity, category, price, salesPrice, aisle, scanCode);
            catalog.add(item);

        }

    }

    public List<Item> searchItemString(final String searchValue) {
        List<Item> selectedResults = new ArrayList<>();
        if (searchValue.isEmpty() || searchValue.equals("")) {
            return catalog;
        }

        for (int i = 0; i < catalog.size(); i++) {
            Item item = catalog.get(i);
            if (item.getDescription().toLowerCase().contains(searchValue) || item.getName().toLowerCase().contains(searchValue)) {
                selectedResults.add(item);
            }
        }

        return selectedResults;
    }

    public void readProblems() throws Exception {
        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();

        String json;
        json = new String(buffer, "UTF-8");
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            String comment = "";
            String problemType = "";
            String state = "";
           String time="";

            comment = obj.getString("comment");
            problemType = obj.getString("problemType");
            state = obj.getString("state");
            time = (obj.getString("timestamp"));
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
                Date parsedDate = dateFormat.parse(time);
                 timestamp = new java.sql.Timestamp(parsedDate.getTime());
            } catch(Exception e) {
            System.out.println(e);
            }


            ProblemType type = getProblemType(problemType);
            State resState = getState(state);
            Problem problem = new Problem(type, comment, resState, timestamp);
            problems.add(problem);

        }

    }

    public Item getScannedItem(String scanCode) {
         for(int i =0; i < catalog.size(); i++){
             Item item = catalog.get(i);
            if(item.getScanCode().equals(scanCode)){
                return item;
            }
        }
        return null;
    }

    private State getState(String state) {
        State resultState = State.NEW;
        switch (state) {
            case "DONE":
                resultState = State.DONE;
                break;
            case "IN_PROGRESS":
                resultState = State.IN_PROGRESS;
                break;
        }
        return resultState;
    }

    private ProblemType getProblemType(String type) {
        ProblemType resultType = ProblemType.GENERAL;
        switch (type) {
            case "SPECIFIC":
                resultType = ProblemType.SPECIFIC;
                break;
            case "OTHER":
                resultType = ProblemType.OTHER;
                break;
        }
        return resultType;
    }

    public void readFlaws() throws Exception {
        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();

        String json;
        json = new String(buffer, "UTF-8");
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            String state = "";
            String descriptionText = "";
            int aisle = 1;
            String time="";

            descriptionText = obj.getString("descriptionText");
            aisle = obj.getInt("aisle");
            state = obj.getString("state");
            time = obj.getString("timestamp");
            State resState = getState(state);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
                Date parsedDate = dateFormat.parse(time);
                timestamp = new java.sql.Timestamp(parsedDate.getTime());
            } catch(Exception e) {
                System.out.println(e);
            }
            Incident incident = new Incident(descriptionText, aisle, resState, timestamp);
            incidents.add(incident);

        }

    }

    public List<Item> searchCategory(final String category) {
        List<Item> selectedResults = new ArrayList<>();
        //   selectedResults = catalog.stream().filter(ele -> ele.getCategory().contains(category)).collect(Collectors.<Item>toList());

        return selectedResults;
    }

    public List<Item> getCatalog() {
        return catalog;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public List<Incident> getIncidents() {
        return incidents;
    }
}
