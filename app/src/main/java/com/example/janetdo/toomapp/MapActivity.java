package com.example.janetdo.toomapp;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.janetdo.toomapp.Helper.Coordinate;
import com.example.janetdo.toomapp.Helper.DrawView;
import com.example.janetdo.toomapp.Helper.Item;
import com.example.janetdo.toomapp.Helper.ListHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janetdo on 05.12.17.
 */

public class MapActivity extends AppCompatActivity {
    private ImageView avatar;
    private RelativeLayout layout;
    private float zeroX = 705;
    private float zeroY = 570;
    List<ImageView> allPins;
    List<Item> allSalesItems;
    DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        layout = findViewById(R.id.map_layout);
        allPins = new ArrayList<>();

        putAvatarDefaultPosition();
        Bundle extras = getIntent().getExtras();
        ListHolder holder = (ListHolder) extras.get("salesPrice");
        String destiny ="";
        if(extras.containsKey("category")){
            destiny = (String) extras.get("category");
        }
        System.out.println("destined category is: "+ destiny);
        allSalesItems = holder.getItemList();
        positionSaleItem();
        addPinsClickHandler();
        drawView = new DrawView(getApplicationContext(), allPins, destiny);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(2000,2000);
        addContentView(drawView, params);
    }

    private void putAvatarDefaultPosition() {
        avatar = new ImageView(this);
        avatar.setImageDrawable(getDrawable(R.drawable.avatar));
        avatar.setX(zeroX);
        avatar.setY(zeroY);
        avatar.setScaleX(1f);
        avatar.setScaleY(1f);
        layout.addView(avatar);
    }

    private Coordinate transformPosition(float x, float y) {
        float newX = zeroX + x;
        float newY = zeroY - y;
        Coordinate coord = new Coordinate(newX, newY);
        return coord;
    }

    private void positionSaleItem() {
        for (int i = 0; i < allSalesItems.size(); i++) {
            ImageView salesPin = new ImageView(this);
            salesPin.setImageDrawable(getDrawable(R.drawable.event));
            Coordinate coord = transformPosition(-100, 100 * i);
            salesPin.setX(coord.getX());
            salesPin.setY(coord.getY());
            salesPin.setId(i);
            allPins.add(salesPin);
            layout.addView(salesPin);
        }
    }

    private void addPinsClickHandler() {
        for (int i = 0; i < allPins.size(); i++) {
            ImageView pin = allPins.get(i);
            pin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    showPopupWindow(id);
                }
            });
        }
    }

    private void showPopupWindow(int id) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        Item item = allSalesItems.get(id);

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 450, focusable);

        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        ImageView itemPic;
        TextView price = popupView.findViewById(R.id.price);
        TextView name = popupView.findViewById(R.id.textName);
        TextView desc = popupView.findViewById(R.id.textDesc);
        TextView salesPrice = popupView.findViewById(R.id.salesPrice);
        salesPrice.setVisibility(View.VISIBLE);

        price.setTextColor(getResources().getColor(R.color.black));
        salesPrice.setTextColor(getResources().getColor(R.color.darkRed));
        price.setText(Double.toString(item.getPrice()) + " €");
        salesPrice.setText(Double.toString(item.getSalesPrice()) + " €");
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        itemPic = popupView.findViewById(R.id.itemPic);
        itemPic.setBackground(getDrawable(R.drawable.sonstiges));

        desc.setText(item.getDescription());
        name.setText(item.getName());
        price.setTextSize(40);
        desc.setTextSize(25);
        salesPrice.setTextSize(40);
        name.setTextSize(35);
        name.setTextSize(25);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
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
