package com.example.janetdo.toomapp.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
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
    private float zeroX = 730;
    private float zeroY = 590;
    List<String> leftUp;
    List<String> rightUp;
    List<String> leftDown;
    List<String> rightDown;
    Map<String, Coordinate> wholeMap;
    Coordinate coordinate;
    List<Item> allSalesItems;
    String category;
    float additionalLength;
    float distanceFirstUp = 325;
    boolean isLeft = true;
    private ImageView avatar;

    private void init() {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(6f);

        /// allSalesItems = new ArrayList<>();
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

        wholeMap.put("leuchten", coord4);
        wholeMap.put("leuchten_2", coord5);
        wholeMap.put("lacke", coord6);

        wholeMap.put("tapeten", coord7);
        wholeMap.put("daemmungen", coord8);
        wholeMap.put("garten", coord9);

        wholeMap.put("haushalt", coord10);
        wholeMap.put("bauzubehoer", coord11);
        wholeMap.put("baustoffe", coord12);

    }

    public DrawView(Context context, List<Item> allSalesItems, String category) {
        super(context);
        this.allSalesItems = allSalesItems;
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

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0x00AAAAAA);
        List<Coordinate> allCoordinates = new ArrayList<>();

        if (category.isEmpty()) {
            return;
        }
        Coordinate firstUp = new Coordinate(zeroX, zeroY - distanceFirstUp);
        System.out.println("Chosen category is " + category);
        allCoordinates = getDrawCoordinates(category);


        canvas.drawLine(zeroX, zeroY, firstUp.getX(), firstUp.getY() - 3, paint);
        canvas.drawLine(firstUp.getX(), firstUp.getY(), allCoordinates.get(0).getX() + additionalLength, allCoordinates.get(0).getY(), paint);
        canvas.drawLine(allCoordinates.get(0).getX(), allCoordinates.get(0).getY(), allCoordinates.get(1).getX(), allCoordinates.get(1).getY(), paint);
        Coordinate lastCoordinate = allCoordinates.get(allCoordinates.size() - 1);
        Drawable icon = getResources().getDrawable(R.drawable.event);
        Bitmap bitmapIcon = ((BitmapDrawable) icon).getBitmap();
        int xIconDistance = 23;
        int yIconDistance = 40;
        float x = lastCoordinate.getX() - xIconDistance;
        float y = lastCoordinate.getY() - yIconDistance;
        if (isLeft) {
            x = lastCoordinate.getX() - xIconDistance;
        }
        isLeft = true;
        canvas.drawBitmap(bitmapIcon, x, y, null);

    }

    public List<Coordinate> getDrawCoordinates(String category) {
        coordinate = new Coordinate(0, 0);
        System.out.println("calculating cat" + category);
        // for left + partly right smaller distance
        float leftDistance = 165;
        //first big right
        float rightDistance = 175;
        Coordinate firstUp = new Coordinate(zeroX, zeroY - distanceFirstUp);
        List<Coordinate> allCoordinates = new ArrayList<>();
        Coordinate defaultCoordinate = getPath(category);
        coordinate = defaultCoordinate;
        //left half of the map
        if (coordinate.getX() < 0) {
            isLeft = true;
            int plus = 0;
            if(Math.abs(coordinate.getY()) > 1){
                plus =12;
            }
            Coordinate left = new Coordinate(firstUp.getX() - leftDistance * Math.abs(coordinate.getY()) - plus, firstUp.getY());
            additionalLength = -3;
            allCoordinates.add(left);
            if (coordinate.getY() > 0) {
                Coordinate downHalf = new Coordinate(left.getX(), left.getY() + (distanceFirstUp / 2));
                allCoordinates.add(downHalf);
            } else {
                Coordinate upHalf = new Coordinate(left.getX(), left.getY() - (distanceFirstUp / 2));
                allCoordinates.add(upHalf);
            }
        }
        // right half of the map
        else {
            isLeft = false;
            Coordinate right = new Coordinate(firstUp.getX() + rightDistance * Math.abs(coordinate.getY()), firstUp.getY());
            additionalLength = 3;
            allCoordinates.add(right);
            if (coordinate.getY() > 0) {
                Coordinate downHalf = new Coordinate(right.getX(), right.getY() - (distanceFirstUp / 2));
                allCoordinates.add(downHalf);
            } else {
                Coordinate upHalf = new Coordinate(right.getX(), right.getY() + (distanceFirstUp / 2));
                allCoordinates.add(upHalf);
            }
        }
        return allCoordinates;
    }

    private Coordinate getPath(String category) {
        Coordinate result = null;
        for (Map.Entry<String, Coordinate> entry : wholeMap.entrySet()) {
            String listCategory = entry.getKey();
            if (listCategory.equals(category)) {
                result = entry.getValue();
                System.out.println("vlaue is" + result);
            }
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //ImageView avatar = findViewById(R.id.avatar);
        float coordX = avatar.getX();
        float coordY = avatar.getY();
        float rotate = avatar.getRotation();
        float rotateY = avatar.getRotationY();
        switch (keyCode) {

            case KeyEvent.KEYCODE_A:
                avatar.setX(coordX - 5f);
                return true;
            case KeyEvent.KEYCODE_D:
                avatar.setX(coordX + 5f);
                return true;
            case KeyEvent.KEYCODE_W:
                avatar.setY(coordY - 5f);
                return true;
            case KeyEvent.KEYCODE_S:
                avatar.setY(coordY + 5f);
                return true;
            case KeyEvent.KEYCODE_K:
                avatar.setRotation(rotate - 50f);
                return true;
            case KeyEvent.KEYCODE_L:
                avatar.setRotation(rotate + 50f);
                return true;
            case KeyEvent.KEYCODE_O:
                avatar.setRotation(0f);
                return true;
            case KeyEvent.KEYCODE_I:
                avatar.setRotation(rotate + 90f);
                return true;
            case KeyEvent.KEYCODE_X:
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}
