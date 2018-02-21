package com.example.janetdo.toomapp.Helper;
import java.io.Serializable;
import java.sql.Timestamp;

public class Incident extends Type {
    public Incident(String descriptionText, int aisle, State state, Timestamp timestamp) {
        this.comment = descriptionText;
        this.aisle = aisle;
        this.timestamp = timestamp;
        this.state = state;
    }

    public Incident(String descriptionText) {
        this.comment = descriptionText;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.aisle = 2;
        this.state = State.NEW;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "descriptionText='" + comment + '\'' +
                ", _id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", aisle=" + aisle +
                ", timestamp=" + timestamp +
                ", state=" + state +
                '}';
    }




}
