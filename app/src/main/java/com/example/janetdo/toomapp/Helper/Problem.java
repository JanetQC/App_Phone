
package com.example.janetdo.toomapp.Helper;

import com.example.janetdo.toomapp.Helper.ProblemType;
import com.example.janetdo.toomapp.Helper.State;
import com.example.janetdo.toomapp.Helper.Type;

import java.io.Serializable;
import java.sql.Timestamp;

public class Problem extends Type {
    private ProblemType problemType = ProblemType.GENERAL;


    public Problem(ProblemType type, String comment, State state, Timestamp timestamp) {
        this.problemType = type;
        this.aisle = 1;
        //  this.timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("bla"+timestamp);
        this.timestamp = timestamp;
        this.state = state;
        this.comment = comment;
    }

    public Problem(ProblemType type, String comment) {
        this.problemType = type;
        this.aisle = 1;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        state = State.NEW;
        this.comment = comment;
    }

    public Problem(ProblemType type) {
        this.problemType = type;
        this.aisle = 1;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        state = State.NEW;
        this.comment = "";
    }


    @Override
    public String toString() {
        return "Problem{" +
                "problemType=" + problemType +
                ", aisle=" + aisle +
                ", timestamp=" + timestamp +
                ", _id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", state=" + state +
                ", comment='" + comment + '\'' +
                '}';
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public String getProblemTypeString() {
        String type = "Allgemeine Beratung";
        switch (this.getProblemType()) {
            case SPECIFIC:
                type = "Spezifische Beratung";
                break;
            case OTHER:
                type = "Sonstiges";
                break;
        }
        return type;
    }


}
