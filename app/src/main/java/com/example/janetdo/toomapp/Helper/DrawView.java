package com.example.janetdo.toomapp.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.janetdo.toomapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by janetdo on 04.02.18.
 */

public class DrawView extends View {
    Paint paint = new Paint();
    private float zeroX = 745;
    private float zeroY = 590;
    List<String> leftUp;
    List<String> rightUp;
    List<String> leftDown;
    List<String> rightDown;
    Map<String, Coordinate> wholeMap;
    Coordinate coordinate;
    List<ImageView> allPins;
    String category;

    private void init() {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(6f);

        allPins = new ArrayList<>();
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
        wholeMap.put("lacke_1", coord12);
    }

    public DrawView(Context context,  List<ImageView> allPins, String category) {
        super(context);
        this.allPins = allPins;
        this.category = category;
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setCoordinate(Coordinate coord){
        this.coordinate = coord;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0x00AAAAAA);
        //first up
        float d1 = 285;
        // for left + partly right smaller distance
        float leftDistance = 180;
        //first big right
        float rightDistance = 220;
        Coordinate firstUp = new Coordinate(zeroX, zeroY - d1);
        List<Coordinate> allCoordinates = new ArrayList<>();

        if(category.isEmpty()){
            return;
        }
        System.out.println("Chosen category is "+ category);
        Coordinate defaultCoordinate = getPath(category);
       if(coordinate == null){
           coordinate = defaultCoordinate;
       }
        float additionalLength;
       //left half of the map
        if (coordinate.getX() < 0) {
            Coordinate left = new Coordinate(firstUp.getX() - leftDistance * Math.abs(coordinate.getY()), firstUp.getY());
                additionalLength = -4;
            allCoordinates.add(left);
            if (coordinate.getY() > 0) {
                Coordinate downHalf = new Coordinate(left.getX(), left.getY() + (d1 / 2));
                allCoordinates.add(downHalf);
            } else {
                Coordinate upHalf = new Coordinate(left.getX(), left.getY() - (d1 / 2));
                allCoordinates.add(upHalf);
            }
        }
        // right half of the map
        else {
            Coordinate right = new Coordinate(firstUp.getX() + rightDistance * Math.abs(coordinate.getY()), firstUp.getY());
            additionalLength = 4;
            allCoordinates.add(right);
            if (coordinate.getY() > 0) {
                Coordinate downHalf = new Coordinate(right.getX(), right.getY() - (d1 / 2));
                allCoordinates.add(downHalf);
            } else {
                Coordinate upHalf = new Coordinate(right.getX(), right.getY() + (d1 / 2));
                allCoordinates.add(upHalf);
            }
        }

        canvas.drawLine(zeroX, zeroY, firstUp.getX(), firstUp.getY()-4, paint);
        canvas.drawLine(firstUp.getX(), firstUp.getY(),allCoordinates.get(0).getX()+additionalLength, allCoordinates.get(0).getY(), paint);
        canvas.drawLine(allCoordinates.get(0).getX(), allCoordinates.get(0).getY(), allCoordinates.get(1).getX(), allCoordinates.get(1).getY(), paint);
        Drawable icon = getResources().getDrawable(R.drawable.event);
        icon.setBounds(200, 200, 200, 200);
        icon.draw(canvas);
    }

    private Coordinate getPath(String category) {
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
