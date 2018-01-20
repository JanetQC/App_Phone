package com.example.janetdo.toomapp.Helper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by janetdo on 28.12.17.
 */

public class Incident implements Serializable {
    private String descriptionText;
    private String _id;
    private String _rev;
    private boolean unread;
    private int aisle;
    private Timestamp timestamp;

    public Incident(String descriptionText) {
        this.descriptionText = descriptionText;
        this.unread = true;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.aisle = 2;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    @Override
    public String toString() {
        return "{\"_id: " + _id +
                ",\n_rev=" + _rev +
                ",\ndescriptionText: " + descriptionText +
                ",\naisle: " + aisle +
                ",\ntimestamp: " + timestamp +
                ",\nunread: " + unread +
                '}';
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public int getAisle() {
        return aisle;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
