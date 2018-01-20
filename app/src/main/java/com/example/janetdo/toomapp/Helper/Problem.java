package com.example.janetdo.toomapp.Helper;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by janetdo on 28.12.17.
 */

public class Problem implements Serializable {
    private problemType problemType;
    private int aisle;
    private Timestamp timestamp;
    private boolean unread;
    private String _id;
    private String _rev;

    public Problem(problemType type) {
        this.problemType = type;
        this.aisle = 1;
        unread = true;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public enum problemType {
        GENERAL,
        SPECIFIC,
        OTHER
    }

    @Override
    public String toString() {
        return "{" +
                "\"_id: " + _id +
                ",\n_rev=" + _rev +
                ",\nproblemType: " + problemType +
                ",\naisle: " + aisle +
                ",\ntimestamp: " + timestamp +
                ",\nunread: " + unread +
                '}';
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public Problem.problemType getProblemType() {
        return problemType;
    }

    public int getAisle() {
        return aisle;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
