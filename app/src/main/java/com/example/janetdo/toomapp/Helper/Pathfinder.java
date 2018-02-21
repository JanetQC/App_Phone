package com.example.janetdo.toomapp.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by janetdo on 06.02.18.
 */

public class Pathfinder {
    List<String> leftUp;
    List<String> rightUp;
    List<String> leftDown;
    List<String> rightDown;
    Map<String, Coordinate> wholeMap;

    private void initCategories() {
        leftUp = new ArrayList<>();
        rightUp = new ArrayList<>();
        leftDown = new ArrayList<>();
        rightDown = new ArrayList<>();
        wholeMap = new HashMap<>();
        //bad&dusche
        Coordinate coord1 = new Coordinate(-1, 1);
        Coordinate coord2 = new Coordinate(-1, 2);
        Coordinate coord3 = new Coordinate(-1, 3);
        //leuchten,elektro
        Coordinate coord4 = new Coordinate(1, -1);
        Coordinate coord5 = new Coordinate(1, -2);
        Coordinate coord6 = new Coordinate(1, -3);

        //tapeten, dämmungen
        Coordinate coord7 = new Coordinate(-1, -1);
        Coordinate coord8 = new Coordinate(-1, -2);
        Coordinate coord9 = new Coordinate(-1, -3);

        //kamin, lacke
        Coordinate coord10 = new Coordinate(1, 1);
        Coordinate coord11 = new Coordinate(1, 2);
        Coordinate coord12 = new Coordinate(1, 3);

        wholeMap.put("bodenbelag", coord1);
        wholeMap.put("bad_dusche_1", coord2);
        wholeMap.put("bad_dusche_2", coord3);

        wholeMap.put("leuchten_1", coord4);
        wholeMap.put("leuchten_2", coord5);
        wholeMap.put("lacke_2", coord6);

        wholeMap.put("tapeten", coord7);
        wholeMap.put("daemmungen", coord8);
        wholeMap.put("garten", coord9);

        wholeMap.put("haushalt", coord10);
        wholeMap.put("kamin", coord11);
        wholeMap.put("lacke_1", coord10);

    }

    public Coordinate getPath(String category) {
        Coordinate result = null;
        for (Map.Entry<String, Coordinate> entry : wholeMap.entrySet()) {
            String listCategory = entry.getKey();
            if (listCategory.equals(category)) {
                result = entry.getValue();
            }
        }
        return result;
    }

}
