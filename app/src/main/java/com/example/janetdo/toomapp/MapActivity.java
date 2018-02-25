package com.example.janetdo.toomapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
import java.util.Map;

/**
 * Created by janetdo on 05.12.17.
 */

public class MapActivity extends AppCompatActivity {
    private ImageView avatar;
    private RelativeLayout layout;
    private float zeroX = 730;
    private float zeroY = 570;
    List<ImageView> allPins;
    List<Item> allSalesItems;
    DrawView drawView;
    public static Context context;
    private static List<Item> salesCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        layout = findViewById(R.id.map_layout);
        allPins = new ArrayList<>();
        context = getBaseContext();

        putAvatarDefaultPosition();
        Bundle extras = getIntent().getExtras();
        ListHolder holder = (ListHolder) extras.get("salesPrice");
        String destiny = "";
        if (extras.containsKey("category")) {
            destiny = (String) extras.get("category");
        }
        System.out.println("destined category is: " + destiny);
        allSalesItems = holder.getItemList();
        salesCatalog = allSalesItems;
        positionSaleItem();
        addPinsClickHandler();
        drawView = new DrawView(getApplicationContext(), allSalesItems, destiny);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(2000, 2000);
        addContentView(drawView, params);
    }

    private void putAvatarDefaultPosition() {
        avatar = new ImageView(this);
        avatar.setImageDrawable(getDrawable(R.drawable.avatar));
        avatar.setX(zeroX - 18);
        avatar.setY(zeroY + 12);
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
        DrawView view = new DrawView(getApplicationContext(), null);
        for (int i = 0; i < allSalesItems.size(); i++) {
            String category = allSalesItems.get(i).getCategory();
            List<Coordinate> coordinates = view.getDrawCoordinates(category);
            System.out.println("cat" + allSalesItems.get(i).getCategory());
            ImageView salesPin = new ImageView(this);
            salesPin.setImageDrawable(getDrawable(R.drawable.sale_info));

            float x = coordinates.get(coordinates.size() - 1).getX();
            float y = coordinates.get(coordinates.size() - 1).getY();
            System.out.println("coords" + x + ", " + y);

            if (category.contains("daemmungen")) {
                x += 15;
                y -= 60;
            } else {
                x -= 22;
                y += 10;
            }
            salesPin.setX(x);
            salesPin.setY(y);
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
        price.setText(String.format("%.2f", item.getPrice()) + " €");
        salesPrice.setText(String.format("%.2f", item.getSalesPrice()) + " €");
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        itemPic = popupView.findViewById(R.id.itemPic);
        itemPic.setBackground(setItemPic(item));

        ImageView navi = popupView.findViewById(R.id.navi);
     //   setNavigationOnClickListener(navi, id);
        navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Position: ");
                Intent intent = new Intent(getContext(), MapActivity.class);
                ListHolder holder = new ListHolder();
                holder.initItemList(allSalesItems);
                intent.putExtra("salesPrice", holder);
                Item item = allSalesItems.get(id);
                intent.putExtra("category", item.getCategory());
                popupWindow.dismiss();
                finish();
                startActivity(intent);

            }
        });

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
    private Drawable setItemPic(Item item) {
        Drawable drawable = null;
        switch (item.getCategory().toLowerCase()) {
            case "bodenbelag":
                drawable = getDrawable(R.drawable.bodenbelag);
                break;
            case "pflanzen":
                drawable = getDrawable(R.drawable.pflanzen);
                break;
            case "lacke":
                drawable = getDrawable(R.drawable.category_paint);
                break;
            case "garten":
                drawable = getDrawable(R.drawable.garten);
                break;
            case "zement":
                drawable = getDrawable(R.drawable.zement);
                break;
            case "bauzubehoer":
                drawable = getDrawable(R.drawable.bauzubehoer);
                break;
            case "styroporleisten":
                drawable = getDrawable(R.drawable.styroporleisten);
                break;
            case "baustoffe":
                drawable = getDrawable(R.drawable.baustoffe);
                break;
            case "daemmungen":
                drawable = getDrawable(R.drawable.daemmstoffe);
                break;
            case "leuchten":
                drawable = getDrawable(R.drawable.lampen);
                break;
            default:
                drawable = getDrawable(R.drawable.sonstiges);


        }
        return drawable;

    }

    public void setNavigationOnClickListener(ImageView view, int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Position: ");
                Intent intent = new Intent(getContext(), MapActivity.class);
                ListHolder holder = new ListHolder();
                holder.initItemList(allSalesItems);
                intent.putExtra("salesPrice", holder);
                Item item = allSalesItems.get(position);
                intent.putExtra("category", item.getCategory());
               // MapActivity.getContext().startActivity(intent);
                finish();
                startActivity(intent);

            }
        });

    }

    public static Context getContext() {
        return context;
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
