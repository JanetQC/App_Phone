package com.example.janetdo.toomapp.Helper;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.example.janetdo.toomapp.Credentials.Credentials;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janetdo on 02.01.18.
 */

public class CloudantService {
    private CloudantClient client;
    private List<Problem> newProblems;
    private List<Problem> allProblems;
    private List<Incident> allIncidents;
    private String selectorAll = "{\"selector\":{}}";

    private Database problems;
    private Database incident;

    public CloudantService() {
        client = ClientBuilder.account(Credentials.CLOUDANT_NAME)
                .username(Credentials.CLOUDANT_NAME)
                .password(Credentials.CLOUDANT_PW)
                .build();
        newProblems = new ArrayList<>();
        allProblems = new ArrayList<>();
        problems = client.database("problems", false);
        incident = client.database("flaws", false);
    }

    public void writeToTable(String tableName, Object object) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (tableName.equals("problems")) {
                        problems.save(object);
                    } else {
                        incident.save(object);
                    }

                } catch (Exception e) {
                    System.out.println("This didn't work so well, huh? Db could not save element:" + object + "in table" + tableName + e);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {

        }
    }

    public List<Problem> getAllProblems() {
        allProblems = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    allProblems = problems.findByIndex(selectorAll, Problem.class);

                } catch (Exception e) {
                    System.out.println("Db could not fetch all elelments of problem" + e);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {

        }

        System.out.println("HERE ARE ALL PROBLEMS");
        System.out.println(allProblems.get(0).toString());
        System.out.println(allProblems.get(1).toString());
        System.out.println(allProblems.get(2).toString());
        System.out.println(allProblems.size());
        return allProblems;
    }

    public List<Incident> getAllIncidents() {
        allIncidents = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    allIncidents = incident.findByIndex(selectorAll, Incident.class);
                } catch (Exception e) {
                    System.out.println("Db could not fetch all elelments of flaws" + e);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {

        }
        System.out.println("HERE ARE ALL INCIDENTS");
        System.out.println(allIncidents.get(0).toString());
        System.out.println(allIncidents.get(1).toString());
        System.out.println(allIncidents.get(2).toString());
        System.out.println(allIncidents.size());
        return allIncidents;
    }

    public List<Problem> getNewProblems() {
        return newProblems;
    }


}
