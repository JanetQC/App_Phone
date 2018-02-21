package com.example.janetdo.toomapp.Helper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;

public class Type implements Serializable, Comparable<Type>{
    public String _id;
    public String _rev;
    public Timestamp timestamp;
    public State state;
    public String comment;
    public int aisle;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public State getState() {
        return state;
    }

    public String getComment() {
        return comment;
    }

    public int getAisle() {
        return aisle;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public int compareTo(Type o) {
        if (this.state.equals(State.NEW)) {
            return -1;
        }
        else if(this.state.equals(State.IN_PROGRESS) && o.getState().equals(State.DONE)){
            return -1;
        }
        return 1;
    }

    public static Comparator<Type> timestampComparator() {
        return new Comparator<Type>() {
            @Override
            public int compare(Type first, Type other) {
                return other.getTimestamp().compareTo(first.getTimestamp());
            }
        };
    }
}
