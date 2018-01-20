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

                    System.out.println("New element was added: " + object.toString());
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

    public void checkUnreadMessages() {
        newProblems = new ArrayList<>();
        String selector = "\"selector\": {\"unread\":true}";
        newProblems = (List<Problem>) getSelectedProblems(selector);
        System.out.println("Unread messages:" + newProblems.size());

    }

    public void setMessagesToRead() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Problem pro = new Problem(Problem.problemType.GENERAL);

                    System.out.println("About to update:" + newProblems.size() + " elements.");
                    for (int i = 0; i < newProblems.size(); i++) {
                        newProblems.get(i).setUnread(false);
                        System.out.println("Updating element " + newProblems.get(i));
                        problems.update(newProblems.get(i));
                    }

                    newProblems = new ArrayList<>();
                    System.out.println("Finished updating problems.");
                } catch (Exception e) {
                    System.out.println("Db could not update." + e);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {

        }

    }

    public List<Problem> getSelectedProblems(String selector) {
        String tableName = "problems";
        allProblems = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    allProblems = problems.findByIndex(selector, Problem.class);

                } catch (Exception e) {
                    System.out.println("Db could not fetch all elelments of " + tableName + e);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {

        }
        return allProblems;
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
        return allProblems;
    }

    public List<Incident> getAllIncidents() {
        allIncidents = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    allIncidents = incident.findByIndex(selectorAll, Incident.class);
                    System.out.println("Fetching all problems.." + allIncidents);
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
        return allIncidents;
    }

    public List<Problem> getNewProblems() {
        return newProblems;
    }


}
